package com.yourcompany.digitaltok.ui.device

import android.content.Context
import android.content.Intent
import android.nfc.NfcManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourcompany.digitaltok.databinding.FragmentDeviceConnectBinding
import com.yourcompany.digitaltok.ui.MainUiViewModel

class DeviceConnectFragment : Fragment() {

    private var _binding: FragmentDeviceConnectBinding? = null
    private val binding get() = _binding!!

    // ✅ HomeScreen(Compose)와 상태 공유
    private val mainUiViewModel: MainUiViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeviceConnectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.connectTopAppBar.titleTextView.text = "장치 (Device)"
        binding.connectTopAppBar.backButton.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.nfcConnectButton.setOnClickListener {
            checkNfcAndProceed()
        }
    }

    private fun checkNfcAndProceed() {
        val nfcManager = context?.getSystemService(Context.NFC_SERVICE) as? NfcManager
        val nfcAdapter = nfcManager?.defaultAdapter

        if (nfcAdapter != null) {
            if (!nfcAdapter.isEnabled) {
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("NFC가 켜져 있지 않습니다. 설정에서 NFC 기능을 켜주세요.")
                    .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("확인") { _, _ ->
                        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                    }
                    .show()
            } else {
                // ✅ (요구사항) "연결 시작" 누르면 -> 홈에서 '연결 안됨' 화면 보이게
                mainUiViewModel.updateDeviceConnected(false)

                mainUiViewModel.requestNavigate("home")

                // ✅ 그리고 기존대로 검색 화면도 띄우고 싶으면 아래 유지
                parentFragmentManager.beginTransaction()
                    .replace((requireView().parent as ViewGroup).id, DeviceSearchingFragment())
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
