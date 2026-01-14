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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourcompany.digitaltok.databinding.FragmentDeviceConnectBinding

class DeviceConnectFragment : Fragment() {

    private var _binding: FragmentDeviceConnectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?): View {
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
                // NFC가 꺼져있으면 확인 다이얼로그 표시
                MaterialAlertDialogBuilder(requireContext())
                    .setMessage("NFC가 켜져 있지 않습니다. 설정에서 NFC 기능을 켜주세요.")
                    .setNegativeButton("취소") { dialog, _ -> dialog.dismiss() }
                    .setPositiveButton("확인") { _, _ ->
                        startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
                    }
                    .show()
            } else {
                // NFC가 켜져있으면 DeviceSearchingFragment로 화면 전환
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
