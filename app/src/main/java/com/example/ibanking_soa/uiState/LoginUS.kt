package com.example.ibanking_soa.uiState

data class LoginUS(
    val isLogging: Boolean = false,
    val isRememberMe: Boolean = false,
    val email: String = "",
    val password: String = "",
    val errMessage: String = "",
)

