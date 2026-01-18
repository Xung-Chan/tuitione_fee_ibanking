package com.example.ibanking_soa.event

import com.example.ibanking_soa.uiState.User
import com.example.ibanking_soa.utils.SnackBarType

sealed class LoginEvent {
    data class ChangeEmail(val email: String) : LoginEvent()
    data class ChangePassword(val password: String) : LoginEvent()
    object IsRememberMeChecked : LoginEvent()
    object Login : LoginEvent()
}

sealed class LoginEffect {
    object LoginSuccess : LoginEffect()
    data class ShowSnackBar(val message: String, val type: SnackBarType) : LoginEffect()
}
