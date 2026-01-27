package com.yourcompany.digitaltok.ui.decorate

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.ImageUploadResult
import com.yourcompany.digitaltok.data.repository.ImageRepository
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class DecorateViewModel : ViewModel() {

    private val imageRepository = ImageRepository()

    // UI 상태를 나타내는 sealed class
    sealed class UploadUiState {
        object Idle : UploadUiState()
        object Loading : UploadUiState()
        data class Success(val result: ImageUploadResult) : UploadUiState()
        data class Error(val message: String) : UploadUiState()
    }

    private val _uploadState = MutableLiveData<UploadUiState>(UploadUiState.Idle)
    val uploadState: LiveData<UploadUiState> = _uploadState

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            _uploadState.value = UploadUiState.Loading

            // MultipartBody.Part 생성
            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            // Repository를 통해 API 호출
            val result = imageRepository.uploadImage(imageFile.name, body)

            result.onSuccess {
                _uploadState.value = UploadUiState.Success(it)
            }.onFailure {
                _uploadState.value = UploadUiState.Error(it.message ?: "업로드 중 오류가 발생했습니다.")
            }
        }
    }
}
