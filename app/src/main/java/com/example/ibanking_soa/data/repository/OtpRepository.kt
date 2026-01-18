package com.example.ibanking_soa.data.repository

import com.example.ibanking_soa.data.api.OtpApi
import com.example.ibanking_soa.data.dto.SendOtpRequest
import com.example.ibanking_soa.data.utils.ApiResult
import com.example.ibanking_soa.data.utils.safeApiCall
import jakarta.inject.Inject
import kotlinx.coroutines.delay

class OtpRepository @Inject constructor(private val api: OtpApi) {
    suspend fun sendOtp(paymentId: Int): ApiResult<Boolean> {
        return safeApiCall { api.sendOtp(data=SendOtpRequest(paymentId)) }
//        delay(1000L)
//        return ApiResult.Success(true)
    }
}