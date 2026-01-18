package com.example.ibanking_soa.data.di

import com.example.ibanking_soa.uiState.User
import jakarta.inject.Inject
import jakarta.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@Singleton
class UserSession @Inject constructor(
) {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    fun setUser(user: User) {
        _user.value = user
    }

    fun clear() {
        _user.value = null
    }

}
