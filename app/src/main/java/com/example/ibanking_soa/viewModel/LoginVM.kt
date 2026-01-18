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
import com.example.ibanking_soa.uiState.AppUiState
import com.example.ibanking_soa.uiState.LoginUS
import com.example.ibanking_soa.uiState.Payment
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
import kotlinx.coroutines.selects.select
import java.math.BigDecimal
import java.text.NumberFormat


@HiltViewModel
class LoginVM @Inject constructor(
    private val userRepository: UserRepository,
    private val userSession: UserSession,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUS())
    val uiState: StateFlow<LoginUS> = _uiState.asStateFlow()

    private val _uiEffect = MutableSharedFlow<LoginEffect>()
    val uiEffect: SharedFlow<LoginEffect> = _uiEffect.asSharedFlow()

    init {
        val savedEmail = sharedPreferences.getString("saved_email", null)
        val savedPassword = sharedPreferences.getString("saved_password", null)
        if (savedEmail != null && savedPassword != null) {
            _uiState.update {
                it.copy(
                    email = savedEmail,
                    password = savedPassword,
                    isRememberMe = savedEmail.isNotEmpty() && savedPassword.isNotEmpty()
                )
            }
        }

    }

    fun onEvent(event: LoginEvent) {
        viewModelScope.launch {
            when (event) {
                is LoginEvent.ChangeEmail -> changeEmail(event.email)
                is LoginEvent.ChangePassword -> changePassword(event.password)
                LoginEvent.Login -> login()
                LoginEvent.IsRememberMeChecked -> changeRememberMeChecked()
            }
        }
    }

    private fun changeRememberMeChecked() {
        _uiState.update { currentState ->
            currentState.copy(
                isRememberMe = !currentState.isRememberMe
            )
        }
    }

    private fun login(
    ) {
        _uiState.update {
            it.copy(
                isLogging = true,
                errMessage = ""
            )
        }
        val loginRequest = LoginRequest(uiState.value.email, uiState.value.password)
        viewModelScope.launch {
            try {
                val apiResult = userRepository.login(loginRequest)
                when (apiResult) {
                    is ApiResult.Error -> {
                        _uiState.update {
                            it.copy(
                                isLogging = false,
                                errMessage = apiResult.message
                            )
                        }
                        _uiEffect.emit(
                            LoginEffect.ShowSnackBar(
                                apiResult.message,
                                SnackBarType.ERROR
                            )
                        )
                        Log.e("err", "Login Error: ${apiResult.message}")
                        return@launch

                    }

                    is ApiResult.Success -> {
                        val loginResponse = apiResult.data
                        val userData = loginResponse.user
                        _uiState.update {
                            it.copy(
                                isLogging = false
                            )
                        }
                        sharedPreferences.edit {
                            putString("access", loginResponse.access)
                            putString("refresh", loginResponse.refresh)
                        }
                        if (uiState.value.isRememberMe) {
                            saveCredentials(
                                uiState.value.email,
                                uiState.value.password
                            )

                        } else {
                            saveCredentials(
                                "",
                                ""
                            )
                        }
                        userSession.setUser(userData)
                        _uiEffect.emit(LoginEffect.LoginSuccess)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                val errMessage = "An unexpected error occurred: ${e.localizedMessage}"
                _uiEffect.emit(LoginEffect.ShowSnackBar(errMessage, SnackBarType.ERROR))
                _uiState.update {
                    it.copy(
                        isLogging = false,
                        errMessage = errMessage
                    )
                }
            }
        }

    }

    private fun saveCredentials(email: String, password: String) {
        sharedPreferences.edit {
            putString("saved_email", email)
            putString("saved_password", password)
        }
    }

    private fun changeEmail(email: String) {
        _uiState.update { currentState ->
            currentState.copy(
                email = email
            )
        }
    }

    private fun changePassword(password: String) {
        _uiState.update { currentState ->
            currentState.copy(
                password = password
            )
        }
    }
}