package com.yourcompany.digitaltok.data.repository

import android.util.Log
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.model.DeviceRegistrationRequest
import com.yourcompany.digitaltok.data.network.RetrofitClient

/**
 * 장치 관련 데이터 처리를 담당하는 클래스.
 * 가이드에 따라 API 호출 결과를 Result로 래핑하여 반환하고, 상세한 오류 처리를 수행합니다.
 */
class DeviceRepository {

    private val apiService = RetrofitClient.apiService

    suspend fun registerDevice(nfcUid: String, deviceName: String): Result<DeviceData> = try {
        val response = apiService.registerDevice(
            DeviceRegistrationRequest(nfcUid = nfcUid, deviceName = deviceName)
        )

        if (response.isSuccessful) {
            val body = response.body()
            if (body?.data != null) {
                // 성공: Status 2xx 이고, body와 data가 모두 존재
                Log.d("DeviceRepository", "Device registration successful: ${body.data}")
                Result.success(body.data)
            } else {
                // 실패: Status 2xx 이지만, body 또는 data가 null
                val errorMessage = body?.message ?: "Response body or data is null"
                Log.e("DeviceRepository", "Device registration failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            // 실패: Status 4xx, 5xx 등
            val errorBody = response.errorBody()?.string() ?: "Unknown error"
            Log.e("DeviceRepository", "Device registration error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        // 실패: 네트워크 오류 등
        Log.e("DeviceRepository", "Network error: ${e.message}", e)
        Result.failure(e)
    }
}
