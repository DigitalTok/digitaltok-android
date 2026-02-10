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

    // 이미지 업로드 결과
    private val _uploadResult = MutableLiveData<Result<ImageUploadResult>>()
    val uploadResult: LiveData<Result<ImageUploadResult>> = _uploadResult

    // 바이너리 데이터 준비 결과 (NFC 전송용)
    private val _binaryData = MutableLiveData<Result<ByteArray>>()
    val binaryData: LiveData<Result<ByteArray>> = _binaryData

    fun uploadImage(imageName: String, imageFile: MultipartBody.Part) {
        viewModelScope.launch {
            val result = imageRepository.uploadImage(imageName, imageFile)
            _uploadResult.postValue(result)
        }
    }

    /**
     * 1. imageId로 바이너리 정보(URL, 사이즈)를 가져온다.
     * 2. 가져온 URL로 실제 바이너리 데이터를 다운로드한다.
     * 3. 사이즈를 검증하고 LiveData에 결과를 담는다.
     */
    fun fetchAndDownloadBinary(imageId: Int) {
        viewModelScope.launch {
            // 1. 바이너리 정보 가져오기
            val binaryInfoResult = imageRepository.getImageBinaryInfo(imageId)

            binaryInfoResult.onSuccess { binaryInfo ->
                // 2. 바이너리 데이터 다운로드
                val downloadResult = imageRepository.downloadImageBinary(binaryInfo.einkDataUrl)

                downloadResult.onSuccess { responseBody ->
                    val bytes = responseBody.bytes()

                    // 3. 사이즈 검증
                    if (bytes.size == binaryInfo.meta.payloadBytes) {
                        _binaryData.postValue(Result.success(bytes))
                    } else {
                        _binaryData.postValue(Result.failure(Exception("Downloaded file size does not match metadata.")))
                    }
                }.onFailure { exception ->
                    _binaryData.postValue(Result.failure(exception))
                }
            }.onFailure { exception ->
                _binaryData.postValue(Result.failure(exception))
            }
        }
    }
}
