package com.yourcompany.digitaltok.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.ImagePreview
import com.yourcompany.digitaltok.data.repository.ImageRepository
import kotlinx.coroutines.launch

class ImagePreviewViewModel : ViewModel() {

    private val imageRepository = ImageRepository()

    // UI 상태를 관리하는 LiveData
    sealed class PreviewUiState {
        object Loading : PreviewUiState()
        data class Success(val preview: ImagePreview) : PreviewUiState()
        data class Error(val message: String) : PreviewUiState()
    }

    private val _previewState = MutableLiveData<PreviewUiState>()
    val previewState: LiveData<PreviewUiState> = _previewState

    /**
     * 서버에 이미지 미리보기를 요청하는 함수
     */
    fun fetchImagePreview(imageId: Int) {
        viewModelScope.launch {
            _previewState.value = PreviewUiState.Loading
            val result = imageRepository.getImagePreview(imageId)
            result.onSuccess {
                _previewState.value = PreviewUiState.Success(it)
            }.onFailure {
                _previewState.value = PreviewUiState.Error(it.message ?: "미리보기를 불러오는데 실패했습니다.")
            }
        }
    }
}
