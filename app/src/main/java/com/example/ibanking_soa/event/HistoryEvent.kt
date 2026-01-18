package com.example.ibanking_soa.event

import com.example.ibanking_soa.uiState.Payment
import com.example.ibanking_soa.utils.SnackBarType

sealed class HistoryEvent {
    data class SelectPayment(val payment: Payment) : HistoryEvent()
}

sealed class HistoryEffect {
    data class ShowSnackBar(val message: String, val type: SnackBarType) : HistoryEffect()
    object NavigateToPaymentDetail : HistoryEffect()
}
