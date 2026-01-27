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

    // 이미지 업로드 UI 상태
    sealed class UploadUiState {
        object Idle : UploadUiState()
        object Loading : UploadUiState()
        data class Success(val result: ImageUploadResult) : UploadUiState()
        data class Error(val message: String) : UploadUiState()
    }

    private val _uploadState = MutableLiveData<UploadUiState>(UploadUiState.Idle)
    val uploadState: LiveData<UploadUiState> = _uploadState

    // 즐겨찾기 UI 상태
    sealed class FavoriteUiState {
        object Idle : FavoriteUiState()
        object Loading : FavoriteUiState()
        data class Success(val imageId: String, val isFavorite: Boolean) : FavoriteUiState()
        data class Error(val message: String) : FavoriteUiState()
    }

    private val _favoriteState = MutableLiveData<FavoriteUiState>(FavoriteUiState.Idle)
    val favoriteState: LiveData<FavoriteUiState> = _favoriteState

    fun uploadImage(imageFile: File) {
        viewModelScope.launch {
            _uploadState.value = UploadUiState.Loading

            val requestFile = imageFile.asRequestBody("image/*".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("file", imageFile.name, requestFile)

            val result = imageRepository.uploadImage(imageFile.name, body)

            result.onSuccess {
                _uploadState.value = UploadUiState.Success(it)
            }.onFailure {
                _uploadState.value = UploadUiState.Error(it.message ?: "업로드 중 오류가 발생했습니다.")
            }
        }
    }

    fun toggleFavoriteStatus(imageId: String, isFavorite: Boolean) {
        viewModelScope.launch {
            _favoriteState.value = FavoriteUiState.Loading
            val result = imageRepository.updateFavoriteStatus(imageId, isFavorite)
            result.onSuccess {
                _favoriteState.value = FavoriteUiState.Success(imageId, isFavorite)
            }.onFailure {
                _favoriteState.value = FavoriteUiState.Error(it.message ?: "즐겨찾기 상태 변경에 실패했습니다.")
            }
        }
    }
}
