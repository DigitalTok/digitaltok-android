package com.yourcompany.digitaltok.ui.decorate

import android.net.Uri

data class DecorateItem(
    val id: String,
    val imageRes: Int? = null,
    val imageUri: Uri? = null,
    val isFavorite: Boolean = false
)
