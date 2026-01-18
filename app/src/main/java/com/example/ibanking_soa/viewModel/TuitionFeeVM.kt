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
import com.example.ibanking_soa.event.TuitionFeeEffect
import com.example.ibanking_soa.event.TuitionFeeEvent
import com.example.ibanking_soa.uiState.AppUiState
import com.example.ibanking_soa.uiState.LoginUS
import com.example.ibanking_soa.uiState.Payment
import com.example.ibanking_soa.uiState.TuitionFee
import com.example.ibanking_soa.uiState.TuitionFeeUS
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
class TuitionFeeVM @Inject constructor(
    private val paymentRepository: PaymentRepository,
    private val tuitionRepository: TuitionRepository,
    private val userSession: UserSession,
    private val paymentTransaction: PaymentTransaction
) : ViewModel() {
    private val _uiState = MutableStateFlow(TuitionFeeUS())
    val uiState: StateFlow<TuitionFeeUS> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<TuitionFeeEffect>()
    val uiEffect: SharedFlow<TuitionFeeEffect> = _uiEffect.asSharedFlow()

    init {
        paymentTransaction.clear()
        viewModelScope.launch {
            userSession.user.collect { user ->
                _uiState.update {
                    it.copy(
                        user = user
                    )
                }
            }
        }
    }

    fun onEvent(event: TuitionFeeEvent) {
        viewModelScope.launch {
            when (event) {

                is TuitionFeeEvent.ViewHistory -> clickViewHistory()


                is TuitionFeeEvent.Logout -> logout()


                is TuitionFeeEvent.Search -> searchTuitionFee()

                is TuitionFeeEvent.ContinuePayment -> continuePayment()

                is TuitionFeeEvent.ChangeStudentId -> changeStudentId(event.studentId)
            }

        }
    }

    private fun clickViewHistory() {
        viewModelScope.launch {
            _uiEffect.emit(
                TuitionFeeEffect.NavigateHistory
            )

        }
    }

    private fun logout() {
        viewModelScope.launch {
            userSession.clear()
            _uiEffect.emit(
                TuitionFeeEffect.NavigateToLogin
            )
        }
    }

    private fun changeStudentId(studentId: String) {
        _uiState.update {
            it.copy(
                studentIdValue = studentId
            )
        }
    }

    private fun continuePayment() {
        _uiState.update {
            it.copy(isCreatingPayment = true)
        }
        viewModelScope.launch {
            val apiResult = paymentRepository.createPayment(uiState.value.studentIdValue)
            when (apiResult) {
                is ApiResult.Success -> {
                    if (apiResult.data != null) {
                        _uiState.update {
                            it.copy(
                                isCreatingPayment = false,
                                tuitionFee = null,
                                payable = false
                            )
                        }
                        paymentTransaction.setPayment(apiResult.data)
                        _uiEffect.emit(
                            TuitionFeeEffect.NavigateToConfirmPayment
                        )
                    } else {
                        _uiState.update {
                            it.copy(
                                isCreatingPayment = false,
                            )
                        }
                        _uiEffect.emit(
                            TuitionFeeEffect.ShowSnackBar(
                                "Tạo yêu cầu thanh toán không thành công",
                                SnackBarType.ERROR
                            )
                        )
                    }
                }

                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            isCreatingPayment = false,
                            payable = false
                        )
                    }
                    _uiEffect.emit(
                        TuitionFeeEffect.ShowSnackBar(
                            "Tạo yêu cầu thanh toán không thành công",
                            SnackBarType.ERROR
                        )
                    )
                }
            }

        }
    }

    private fun searchTuitionFee() {
        _uiState.update {
            it.copy(isSearching = true)
        }
        viewModelScope.launch {
            val apiResultPayment = paymentRepository.isInTransaction(uiState.value.studentIdValue)
            if (apiResultPayment is ApiResult.Error) {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        payable = false,
                        tuitionFee = null
                    )
                }
                return@launch
            }
            val paymentData = (apiResultPayment as ApiResult.Success).data
            if (paymentData == null) {
                val apiResultTuition =
                    tuitionRepository.getTuitionByStudentId(uiState.value.studentIdValue)
                when (apiResultTuition) {
                    is ApiResult.Success -> {
                        if (apiResultTuition.data.totalPending > BigDecimal(0)) {
                            _uiState.update {
                                it.copy(
                                    isSearching = false,
                                    payable = apiResultTuition.data.isPayable,
                                    tuitionFee = TuitionFee(
                                        studentId = apiResultTuition.data.studentId,
                                        studentFullName = apiResultTuition.data.fullName,
                                        amount = apiResultTuition.data.totalPending
                                    ),
                                )
                            }
                        } else {
                            _uiState.update {
                                it.copy(
                                    isSearching = false,
                                    payable = false,

                                    )
                            }
                            _uiEffect.emit(
                                TuitionFeeEffect.ShowSnackBar(
                                    "No tuition fee due for this student ID",
                                    SnackBarType.ERROR
                                )
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isSearching = false,
                                payable = false,
                                tuitionFee = null,
                                studentIdValue = ""
                            )
                        }
                        _uiEffect.emit(
                            TuitionFeeEffect.ShowSnackBar(
                                apiResultTuition.message,
                                SnackBarType.ERROR
                            )
                        )
                    }
                }


            } else {
                _uiState.update {
                    it.copy(
                        isSearching = false,
                        payable = false,
                    )
                }
                paymentTransaction.setPayment(paymentData)
                _uiEffect.emit(
                    TuitionFeeEffect.NavigateToConfirmPayment
                )
            }
        }
    }

}