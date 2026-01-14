package com.yourcompany.digitaltok.ui.decorate

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class DecorateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 임시로 텍스트를 표시하는 뷰를 생성합니다.
        return TextView(requireContext()).apply {
            text = "꾸미기 화면"
            gravity = android.view.Gravity.CENTER
            textSize = 24f
        }
    }
}
