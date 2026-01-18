package com.example.ibanking_soa.uiState

data class TuitionFeeUS(
    val isSearching: Boolean = false,
    val isCreatingPayment: Boolean = false,
    val payable: Boolean = false,


    val user: User? = null,
    val studentIdValue: String = "",
    val tuitionFee: TuitionFee? = null,
)

