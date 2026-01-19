package com.yourcompany.digitaltok.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


//앱의 전반적인 상태를 관리하는 ViewModel (예: 기기 연결 상태)
class MainViewModel : ViewModel() {

    // 기기 연결 상태를 저장 (true: 연결됨, false: 연결 안됨)
    private val _isDeviceConnected = MutableLiveData<Boolean>(false)
    val isDeviceConnected: LiveData<Boolean> = _isDeviceConnected


    //기기 연결 상태를 '연결됨'으로 설정
    fun onDeviceConnected() {
        _isDeviceConnected.value = true
    }
}
