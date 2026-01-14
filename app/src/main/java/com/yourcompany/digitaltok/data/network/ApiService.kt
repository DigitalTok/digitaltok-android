package com.yourcompany.digitaltok.data.network

import com.yourcompany.digitaltok.data.model.ApiResponse
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.model.DeviceRegistrationRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 서버 API와 통신하기 위한 Retrofit 인터페이스
 */
interface ApiService {

    /**
     * NFC 태그 정보를 서버에 보내 장치를 등록합니다.
     * @param request 등록할 장치의 nfcUid와 deviceName이 담긴 요청 객체
     * @return 서버로부터 받은 공통 응답 객체. 실제 데이터는 data 필드 안에 들어있습니다.
     */
    @POST("devices") // TODO: 실제 API 엔드포인트로 수정해야 합니다. 예: "api/v1/devices"
    suspend fun registerDevice(@Body request: DeviceRegistrationRequest): Response<ApiResponse<DeviceData>>
}
