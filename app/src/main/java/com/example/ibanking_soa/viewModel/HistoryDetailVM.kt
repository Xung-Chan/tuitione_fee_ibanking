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
import com.example.ibanking_soa.data.di.SelectedPayment
import com.example.ibanking_soa.data.di.UserSession
import com.example.ibanking_soa.data.dto.ConfirmPaymentRequest
import com.example.ibanking_soa.data.dto.LoginRequest
import com.example.ibanking_soa.data.repository.OtpRepository
import com.example.ibanking_soa.data.repository.PaymentRepository
import com.example.ibanking_soa.data.repository.TuitionRepository
import com.example.ibanking_soa.data.repository.UserRepository
import com.example.ibanking_soa.data.utils.ApiResult
import com.example.ibanking_soa.event.HistoryEffect
import com.example.ibanking_soa.event.HistoryEvent
import com.example.ibanking_soa.event.LoginEffect
import com.example.ibanking_soa.event.LoginEvent
import com.example.ibanking_soa.uiState.AppUiState
import com.example.ibanking_soa.uiState.HistoryUS
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
import java.math.BigDecimal
import java.text.NumberFormat


@HiltViewModel
class HistoryDetailVM @Inject constructor(
    private val selectedPayment: SelectedPayment
) : ViewModel() {
    private val _uiState = MutableStateFlow<Payment?>(null)
    val uiState: StateFlow<Payment?> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            selectedPayment.payment.collect {
                _uiState.value = it
            }

        }

    }

}