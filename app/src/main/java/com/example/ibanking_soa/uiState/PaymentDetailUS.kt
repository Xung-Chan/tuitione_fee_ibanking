package com.example.ibanking_soa.uiState

data class PaymentDetailUS(
    val isLoading: Boolean = false,
    val user: User? = null,
    val payment: Payment? = null,
    val isOtpBoxVisible: Boolean = false,
    val isSendingOtp: Boolean = false,
    val otpValue: String = "",
)

