package com.yourcompany.digitaltok.ui.upload

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.databinding.FragmentImagePreviewBinding

class ImagePreviewFragment : Fragment() {

    private var _binding: FragmentImagePreviewBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ImagePreviewViewModel by viewModels()
    private var einkDataUrl: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentImagePreviewBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 전달받은 arguments 처리
        val imageId = arguments?.getInt(ARG_IMAGE_ID)
        einkDataUrl = arguments?.getString(ARG_EINK_DATA_URL)

        setupToolbar()
        setupClickListeners()
        observeViewModel()

        if (imageId != null) {
            viewModel.fetchImagePreview(imageId)
        } else {
            Toast.makeText(requireContext(), "이미지 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        binding.appBar.titleTextView.text = "사진 업로드"
        binding.appBar.backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupClickListeners() {
        // 디링에 전송하기 버튼 리스너
        binding.btnSendToDiring.setOnClickListener {
            if (einkDataUrl != null) {
                // TODO: einkDataUrl을 사용해 바이너리 데이터 다운로드 및 NFC 전송 로직 호출
                Toast.makeText(requireContext(), "NFC 전송 시작! (URL: $einkDataUrl)", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(requireContext(), "전송할 이미지 데이터가 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.previewState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is ImagePreviewViewModel.PreviewUiState.Success -> {
                    Glide.with(this)
                        .load(state.preview.previewUrl)
                        .placeholder(R.drawable.ic_launcher_background) // 로딩 중 표시할 이미지
                        .into(binding.ivPreview)
                }
                is ImagePreviewViewModel.PreviewUiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                ImagePreviewViewModel.PreviewUiState.Loading -> {
                    // 로딩 상태 처리 안함
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_IMAGE_ID = "IMAGE_ID"
        private const val ARG_EINK_DATA_URL = "EINK_DATA_URL"

        fun newInstance(imageId: Int, einkDataUrl: String) = ImagePreviewFragment().apply {
            arguments = Bundle().apply {
                putInt(ARG_IMAGE_ID, imageId)
                putString(ARG_EINK_DATA_URL, einkDataUrl)
            }
        }
    }
}
