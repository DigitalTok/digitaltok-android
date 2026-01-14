package com.yourcompany.digitaltok.ui.device

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.DeviceData
import com.yourcompany.digitaltok.data.repository.DeviceRepository
import kotlinx.coroutines.launch

/**
 * Device 관련 비즈니스 로직을 처리하고 UI 상태를 관리하는 ViewModel
 */
class DeviceViewModel : ViewModel() {

    private val repository = DeviceRepository()

    // 서버로부터의 장치 등록 결과를 관찰하기 위한 LiveData
    private val _registrationResult = MutableLiveData<Result<DeviceData>>()
    val registrationResult: LiveData<Result<DeviceData>> = _registrationResult

    /**
     * NFC 태그 UID와 장치 이름을 사용하여 서버에 장치 등록을 요청합니다.
     * 이 함수가 호출되면, viewModelScope에 의해 백그라운드에서 서버 통신이 실행되고,
     * 그 결과는 registrationResult LiveData에 저장됩니다.
     */
    fun registerDevice(nfcUid: String, deviceName: String) {
        viewModelScope.launch {
            val result = repository.registerDevice(nfcUid, deviceName)
            _registrationResult.postValue(result)
        }
    }
}
