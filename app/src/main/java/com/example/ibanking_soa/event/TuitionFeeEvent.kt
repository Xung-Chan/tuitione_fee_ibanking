package com.example.ibanking_soa.event

import com.example.ibanking_soa.utils.SnackBarType

sealed class TuitionFeeEvent {
    object ViewHistory : TuitionFeeEvent()
    object Logout : TuitionFeeEvent()
    object Search : TuitionFeeEvent()
    object ContinuePayment : TuitionFeeEvent()
    data class ChangeStudentId(val studentId: String) : TuitionFeeEvent()
}

sealed class TuitionFeeEffect {
    data class ShowSnackBar(val message: String, val type: SnackBarType) : TuitionFeeEffect()
    object NavigateHistory : TuitionFeeEffect()
    object NavigateToLogin : TuitionFeeEffect()
    object NavigateToConfirmPayment : TuitionFeeEffect()
}
