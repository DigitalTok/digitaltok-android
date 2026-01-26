package com.yourcompany.digitaltok.ui.decorate

import android.net.Uri
import androidx.annotation.DrawableRes

data class DecorateItem(
    val id: String,
    val imageUri: Uri? = null,
    @DrawableRes val imageRes: Int? = null,
    val isPinned: Boolean = false,
    val isSelected: Boolean = false
)
