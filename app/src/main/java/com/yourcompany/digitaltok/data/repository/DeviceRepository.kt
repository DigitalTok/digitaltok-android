package com.yourcompany.digitaltok.data.repository

import android.util.Log
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.model.DeviceRegistrationRequest
import com.yourcompany.digitaltok.data.network.RetrofitClient

/**
 * 장치 관련 데이터 처리를 담당하는 클래스.
 */
class DeviceRepository {

    private val apiService = RetrofitClient.apiService

    // nfcUid를 deviceId로 사용하여 서버에 장치 등록을 요청
    suspend fun registerDevice(nfcUid: String): Result<DeviceData> = try {
        // 기존의 nfcUid를 deviceId로 사용합니다.
        val response = apiService.registerDevice(
            DeviceRegistrationRequest(deviceId = nfcUid)
        )

        // isSuccessful은 HTTP Status Code (200-299)를 확인합니다.
        if (response.isSuccessful) {
            val body = response.body()
            // body가 null이 아니고, 서버 응답의 isSuccess가 true이며, 실제 데이터(result)가 null이 아닐 때 성공으로 처리합니다.
            if (body != null && body.isSuccess && body.result != null) {
                Log.d("DeviceRepository", "기기 연결 성공 ${body.result}")
                Result.success(body.result)
            } else {
                // Status 2xx 이지만, 서버가 isSuccess:false를 반환했거나 데이터가 없는 경우
                val errorMessage = body?.message ?: "Response body, isSuccess, or result is problematic"
                Log.e("DeviceRepository", "Device registration failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            // 실패: Status 4xx, 5xx 등 (HTTP 에러)
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Log.e("DeviceRepository", "Device registration HTTP error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        // 실패: 네트워크 연결 오류 등
        Log.e("DeviceRepository", "Network or other exception: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun getDeviceById(deviceId: Int): Result<DeviceData> = try {
        val response = apiService.getDeviceById(deviceId)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Log.d("DeviceRepository", "기기 상태 조회 성공 ${body.result}")
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Response body, isSuccess, or result is problematic"
                Log.e("DeviceRepository", "Get device by ID failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Log.e("DeviceRepository", "Get device by ID HTTP error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Log.e("DeviceRepository", "Network or other exception: ${e.message}", e)
        Result.failure(e)
    }

    suspend fun deleteDevice(deviceId: Int): Result<DeviceData> = try {
        val response = apiService.deleteDevice(deviceId)

        if (response.isSuccessful) {
            val body = response.body()
            if (body != null && body.isSuccess && body.result != null) {
                Log.d("DeviceRepository", "기기 연결 해제 성공 ${body.result}")
                Result.success(body.result)
            } else {
                val errorMessage = body?.message ?: "Response body, isSuccess, or result is problematic"
                Log.e("DeviceRepository", "Device deletion failed: $errorMessage")
                Result.failure(RuntimeException(errorMessage))
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Unknown HTTP error"
            Log.e("DeviceRepository", "Device deletion HTTP error: ${response.code()} - $errorBody")
            Result.failure(RuntimeException("API Error ${response.code()}: $errorBody"))
        }
    } catch (e: Exception) {
        Log.e("DeviceRepository", "Network or other exception: ${e.message}", e)
        Result.failure(e)
    }
}
