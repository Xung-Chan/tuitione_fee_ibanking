package com.example.ibanking_soa.data.repository

import com.example.ibanking_soa.data.api.PaymentApi
import com.example.ibanking_soa.data.dto.PaymentRequest
import com.example.ibanking_soa.data.utils.ApiResult
import com.example.ibanking_soa.data.utils.safeApiCall
import com.example.ibanking_soa.uiState.Payment
import kotlinx.coroutines.delay
import javax.inject.Inject

class PaymentRepository @Inject constructor(private val api: PaymentApi) {
    suspend fun isInTransaction(studentId: String): ApiResult<Payment?> {

        return safeApiCall {
            api.isInTransaction(
                 studentId = studentId,
            )
        }
//        delay(1000L)
//        return ApiResult.Success(
//            null
//        )
    }
    suspend fun createPayment(studentId: String): ApiResult<Payment?> {
        return safeApiCall {
            api.createPayment(
                paymentRequest = PaymentRequest(studentId = studentId)
            )
        }
//        delay(1000L)
//        return ApiResult.Success(
//            Payment(
//            )
//        )

    }
    suspend fun getPaymentHistories(): ApiResult<List<Payment>> {
        return safeApiCall {
            api.getPaymentHistories()
        }
//        delay(1000L)
//        return ApiResult.Success(
//            listOf(
//                Payment(
//                ),
//                Payment(
//                ),
//            )
//        )
    }
}