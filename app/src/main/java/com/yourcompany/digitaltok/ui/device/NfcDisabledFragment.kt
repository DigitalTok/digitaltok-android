package com.yourcompany.digitaltok.ui.device

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.yourcompany.digitaltok.databinding.FragmentDeviceNfcDisabledBinding

class NfcDisabledFragment : DialogFragment() {

    private var _binding: FragmentDeviceNfcDisabledBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceNfcDisabledBinding.inflate(inflater, container, false)
        // 다이얼로그의 배경을 투명하게 설정하여, 커스텀 레이아웃의 CardView가 잘 보이도록 함
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 확인 버튼 클릭 시 NFC 설정 화면으로 이동 후 다이얼로그 닫기
        binding.btnConfirm.setOnClickListener {
            startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            dismiss()
        }

        // 취소 버튼 클릭 시 다이얼로그 닫기
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
