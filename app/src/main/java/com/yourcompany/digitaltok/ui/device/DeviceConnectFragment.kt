package com.yourcompany.digitaltok.ui.device

import android.content.Context
import android.nfc.NfcManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            // 이 프래그먼트는 백스택의 가장 처음이므로, 네비게이션 컴포넌트가 뒤로가기를 처리하도록 함
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
                // NFC가 꺼져있으면 직접 만든 NfcDisabledFragment를 다이얼로그로 띄움
                NfcDisabledFragment().show(parentFragmentManager, "NfcDisabledDialog")
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
