package com.yourcompany.digitaltok.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _isBottomNavVisible = MutableLiveData(true)
    val isBottomNavVisible: LiveData<Boolean> = _isBottomNavVisible

    // 기기 연결 상태
    private val _isDeviceConnected = MutableLiveData(false)
    val isDeviceConnected: LiveData<Boolean> = _isDeviceConnected

    fun setBottomNavVisibility(isVisible: Boolean) {
        _isBottomNavVisible.value = isVisible
    }

    fun setDeviceConnected(isConnected: Boolean) {
        _isDeviceConnected.value = isConnected
    }
}
