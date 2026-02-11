package com.yourcompany.digitaltok.ui.decorate

import android.net.Uri

/**
 * '꾸미기' 탭의 그리드에 표시될 아이템을 나타내는 데이터 클래스입니다.
 * 이 클래스는 UI 표현에 필요한 모든 정보를 담고 있습니다.
 */
data class DecorateItem(
    val id: String, // 서버 이미지의 경우 imageId, 로컬 슬롯의 경우 고유 ID
    val previewUrl: String? = null, // 서버에서 받은 썸네일 URL
    val isFavorite: Boolean = false, // 즐겨찾기 상태
    val imageUri: Uri? = null, // 갤러리/카메라에서 가져온 로컬 이미지 Uri
    val imageRes: Int? = null, // (필요 시) 드로어블 리소스 ID
    val isSlot: Boolean = true // 최근 사진 목록의 빈 슬롯 여부
)
