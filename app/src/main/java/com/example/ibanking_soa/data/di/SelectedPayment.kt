package com.example.ibanking_soa.data.di

import com.example.ibanking_soa.uiState.Payment
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class SelectedPayment @Inject constructor(
) {
    private val _payment = MutableStateFlow<Payment?>(null)
    val payment: StateFlow<Payment?> = _payment.asStateFlow()

    fun setPayment(payment: Payment) {
        _payment.value = payment
    }

    fun clear() {
        _payment.value = null
    }
}
