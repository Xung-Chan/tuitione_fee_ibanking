package com.example.ibanking_soa.data.repository

import com.example.ibanking_soa.data.api.UserApi
import com.example.ibanking_soa.data.dto.ConfirmPaymentRequest
import com.example.ibanking_soa.data.dto.LoginRequest
import com.example.ibanking_soa.data.dto.LoginResponse
import com.example.ibanking_soa.data.utils.ApiResult
import com.example.ibanking_soa.data.utils.safeApiCall
import com.example.ibanking_soa.uiState.Payment
import com.example.ibanking_soa.uiState.User
import jakarta.inject.Inject
import jakarta.inject.Named
import kotlinx.coroutines.delay

class UserRepository @Inject constructor(
    @Named("AuthUser") private val authApi: UserApi,
    @Named("NonAuthUser") private val nonAuthApi: UserApi,
) {

    suspend fun login(loginRequest: LoginRequest): ApiResult<LoginResponse> {
        return safeApiCall { nonAuthApi.login(loginRequest) }
//        delay(1000L)
//        return ApiResult.Success(
//            LoginResponse(
//                access = "TODO()",
//                refresh = "TODO()",
//                user = User()
//            )
//        )
    }

    suspend fun confirmPayment(confirmPaymentRequest: ConfirmPaymentRequest): ApiResult<Payment> {
        return safeApiCall { authApi.confirmPayment(confirmPaymentRequest) }
//        delay(1000L)
//        return ApiResult.Success(
//            Payment(
//            )
//        )
    }

    suspend fun getMyInformation(): ApiResult<User> {
        return safeApiCall { authApi.getMe() }
//        delay(1000L)
//        return ApiResult.Success(
//            User(
//            )
//        )
    }
}
