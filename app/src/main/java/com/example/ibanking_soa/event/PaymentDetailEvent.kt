package com.example.ibanking_soa.event

import com.example.ibanking_soa.utils.SnackBarType

sealed class PaymentDetailEvent {
    object SendOtp : PaymentDetailEvent()
    data class ChangeOtp(val otp: String) : PaymentDetailEvent()

    object  DismissOtpBox: PaymentDetailEvent()
}

sealed class PaymentDetailEffect {
    data class ShowSnackBar(val message: String, val type: SnackBarType) : PaymentDetailEffect()
    object PaymentSuccess : PaymentDetailEffect()
}
