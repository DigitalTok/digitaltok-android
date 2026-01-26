package com.yourcompany.digitaltok.ui.upload

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourcompany.digitaltok.data.model.ImageUploadResult
import com.yourcompany.digitaltok.data.repository.ImageRepository
import kotlinx.coroutines.launch
import okhttp3.MultipartBody

class ImageViewModel : ViewModel() {

    private val imageRepository = ImageRepository()
    private val _uploadResult = MutableLiveData<Result<ImageUploadResult>>()
    val uploadResult: LiveData<Result<ImageUploadResult>> = _uploadResult

    fun uploadImage(imageName: String, imageFile: MultipartBody.Part) {
        viewModelScope.launch {
            val result = imageRepository.uploadImage(imageName, imageFile)
            _uploadResult.postValue(result)
        }
    }
}
