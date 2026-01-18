package com.example.ibanking_soa.viewModel

import android.R.attr.password
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.ibanking_soa.Screens
import com.example.ibanking_soa.data.di.PaymentTransaction
import com.example.ibanking_soa.data.di.UserSession
import com.example.ibanking_soa.data.dto.ConfirmPaymentRequest
import com.example.ibanking_soa.data.dto.LoginRequest
import com.example.ibanking_soa.data.repository.OtpRepository
import com.example.ibanking_soa.data.repository.PaymentRepository
import com.example.ibanking_soa.data.repository.TuitionRepository
import com.example.ibanking_soa.data.repository.UserRepository
import com.example.ibanking_soa.data.utils.ApiResult
import com.example.ibanking_soa.event.LoginEffect
import com.example.ibanking_soa.event.LoginEvent
import com.example.ibanking_soa.event.PaymentDetailEffect
import com.example.ibanking_soa.event.PaymentDetailEvent
import com.example.ibanking_soa.uiState.AppUiState
import com.example.ibanking_soa.uiState.LoginUS
import com.example.ibanking_soa.uiState.Payment
import com.example.ibanking_soa.uiState.PaymentDetailUS
import com.example.ibanking_soa.uiState.TuitionFee
import com.example.ibanking_soa.utils.SnackBarType
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.NumberFormat


@HiltViewModel
class PaymentDetailVM @Inject constructor(
    private val otpRepository: OtpRepository,
    private val userRepository: UserRepository,
    private val paymentTransaction: PaymentTransaction,
    private val userSession: UserSession
) : ViewModel() {
    private val _uiState = MutableStateFlow(PaymentDetailUS())
    val uiState: StateFlow<PaymentDetailUS> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<PaymentDetailEffect>()
    val uiEffect: SharedFlow<PaymentDetailEffect> = _uiEffect.asSharedFlow()

    init {
        viewModelScope.launch {
            userSession.user.collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        user = it
                    )
                }
            }
        }
        viewModelScope.launch {
            paymentTransaction.payment.collect {
                _uiState.update { currentState ->
                    currentState.copy(
                        payment = it
                    )
                }
            }
        }
    }

    fun onEvent(event: PaymentDetailEvent) {
        when (event) {
            is PaymentDetailEvent.ChangeOtp -> changeOtp(event.otp)
            PaymentDetailEvent.DismissOtpBox -> dismissOtpBox()
            PaymentDetailEvent.SendOtp -> sendOtp()
        }
    }

    private fun sendOtp() {
        viewModelScope.launch {


            _uiState.update {
                it.copy(isSendingOtp = true)
            }
            if (uiState.value.payment == null) {
                _uiEffect.emit(
                    PaymentDetailEffect.ShowSnackBar(
                        "Payment data is missing",
                        SnackBarType.ERROR
                    )
                )
                return@launch
            }
            val apiResult = otpRepository.sendOtp(uiState.value.payment!!.id)
            if (apiResult is ApiResult.Error) {
                _uiState.update {
                    it.copy(isSendingOtp = false)
                }
                _uiEffect.emit(
                    PaymentDetailEffect.ShowSnackBar(
                        apiResult.message,
                        SnackBarType.ERROR
                    )
                )
                return@launch
            }
            val isSent = (apiResult as ApiResult.Success).data
            if (isSent) {
                _uiEffect.emit(
                    PaymentDetailEffect.ShowSnackBar(
                        "OTP sent successfully",
                        SnackBarType.SUCCESS
                    )
                )

                _uiState.update {
                    it.copy(
                        isSendingOtp = false,
                        isOtpBoxVisible = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(isSendingOtp = false)
                }
                _uiEffect.emit(
                    PaymentDetailEffect.ShowSnackBar(
                        "Failed to send OTP",
                        SnackBarType.ERROR
                    )
                )
            }

        }
    }

    private fun dismissOtpBox() {
        _uiState.update {
            it.copy(
                isOtpBoxVisible = false
            )
        }
    }

    private fun changeOtp(otp: String) {
        _uiState.update {
            it.copy(
                otpValue = otp
            )
        }
        if (otp.length == 6) {
            _uiState.update {
                it.copy(
                    isLoading = true
                )
            }
            viewModelScope.launch {
                val apiResult = userRepository.confirmPayment(
                    ConfirmPaymentRequest(
                        otp = uiState.value.otpValue,
                        paymentId = _uiState.value.payment!!.id
                    )
                )
                when (apiResult) {
                    is ApiResult.Success -> {
                        reloadUser()

                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                payment = apiResult.data,
                                isOtpBoxVisible = false
                            )
                        }
                        paymentTransaction.setPayment(
                            apiResult.data
                        )
                        _uiEffect.emit(
                            PaymentDetailEffect.PaymentSuccess
                        )
                    }

                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                otpValue = "",
                                isLoading = false
                            )
                        }
                        _uiEffect.emit(
                            PaymentDetailEffect.ShowSnackBar(
                                apiResult.message,
                                SnackBarType.ERROR
                            )
                        )
                        return@launch
                    }
                }

            }
            return
        }

    }

    private fun reloadUser() {
        viewModelScope.launch {
            val apiResult = userRepository.getMyInformation()
            when (apiResult) {
                is ApiResult.Success -> {
                    val userData = apiResult.data
                    userSession.setUser(userData)
                }

                is ApiResult.Error -> {
                    _uiEffect.emit(
                        PaymentDetailEffect.ShowSnackBar(
                            apiResult.message,
                            SnackBarType.ERROR
                        )
                    )
                    return@launch
                }
            }
        }
    }
}