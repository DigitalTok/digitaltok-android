package com.yourcompany.digitaltok.ui.decorate

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourcompany.digitaltok.R
import java.io.File

class DecorateFragment : Fragment() {

    private lateinit var toggleTabs: MaterialButtonToggleGroup
    private lateinit var tvCount: TextView
    private lateinit var rvGrid: RecyclerView
    private lateinit var rvTemplateList: RecyclerView

    private lateinit var sendContainer: View
    private lateinit var btnSend: com.google.android.material.button.MaterialButton

    private lateinit var gridAdapter: DecorateAdapter
    private lateinit var templateAdapter: StationTemplateAdapter

    private enum class Tab { RECENT, TEMPLATE }
    private var currentTab: Tab = Tab.RECENT

    private val recentItems = mutableListOf<DecorateItem>().apply {
        repeat(15) { idx ->
            add(DecorateItem("recent_$idx", imageRes = R.drawable.splash_logo, isFavorite = true))
        }
    }

    private val stationTemplates = listOf(
        StationTemplateItem("st1", "시청", "1호선", R.drawable.ic_launcher_foreground),
        StationTemplateItem("st2", "용산", "1호선", R.drawable.ic_launcher_foreground),
        StationTemplateItem("st3", "강남", "2호선", R.drawable.ic_launcher_foreground),
        StationTemplateItem("st4", "압구정", "3호선", R.drawable.ic_launcher_foreground),
        StationTemplateItem("st5", "이촌", "4호선", R.drawable.ic_launcher_foreground),
    )

    // 카메라 촬영 저장용 Uri
    private var pendingCameraUri: Uri? = null

    // ✅ 갤러리(시스템 Photo Picker)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) return@registerForActivityResult
            addRecentImage(uri)
        }

    // ✅ 카메라(촬영 후 Uri에 저장)
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val uri = pendingCameraUri
            if (!success || uri == null) return@registerForActivityResult
            addRecentImage(uri)
        }

    // ✅ 카메라 권한 요청
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCameraInternal()
            else Toast.makeText(requireContext(), "카메라 권한이 필요해요", Toast.LENGTH_SHORT).show()
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_decorate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toggleTabs = view.findViewById(R.id.toggleTabs)
        tvCount = view.findViewById(R.id.tvCount)
        rvGrid = view.findViewById(R.id.rvGrid)
        rvTemplateList = view.findViewById(R.id.rvTemplateList)
        sendContainer = view.findViewById(R.id.sendContainer)
        btnSend = view.findViewById(R.id.btnSend)

        // 최근 사진 그리드
        rvGrid.layoutManager = GridLayoutManager(requireContext(), 3)
        gridAdapter = DecorateAdapter(recentItems) {
            updateSendButtonUI()
        }
        rvGrid.adapter = gridAdapter

        // 역명 템플릿 리스트
        rvTemplateList.layoutManager = LinearLayoutManager(requireContext())
        templateAdapter = StationTemplateAdapter(stationTemplates) { item ->
            Toast.makeText(requireContext(), "템플릿 선택: ${item.stationName}", Toast.LENGTH_SHORT).show()
        }
        rvTemplateList.adapter = templateAdapter

        // 초기 탭
        toggleTabs.check(R.id.btnRecent)
        setTab(Tab.RECENT)

        // 탭 변경
        toggleTabs.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            if (checkedId == R.id.btnRecent) setTab(Tab.RECENT) else setTab(Tab.TEMPLATE)
        }

        // 버튼 클릭 동작
        btnSend.setOnClickListener {
            if (currentTab != Tab.RECENT) return@setOnClickListener

            val selected = gridAdapter.getSelectedItem()
            if (selected == null) {
                showAddImageDialog()
            } else {
                Toast.makeText(requireContext(), "선택한 이미지 전송: ${selected.id}", Toast.LENGTH_SHORT).show()
                // TODO: NFC 전송 로직 연결
            }
        }

        updateSendButtonUI()
    }

    private fun setTab(tab: Tab) {
        currentTab = tab
        val isRecent = tab == Tab.RECENT

        rvGrid.visibility = if (isRecent) View.VISIBLE else View.GONE
        rvTemplateList.visibility = if (isRecent) View.GONE else View.VISIBLE
        sendContainer.visibility = if (isRecent) View.VISIBLE else View.GONE

        tvCount.text = if (isRecent) {
            "최근 사용한 사진 (${recentItems.size}/${recentItems.size})"
        } else {
            "템플릿"
        }

        updateSendButtonUI()
    }

    private fun updateSendButtonUI() {
        if (currentTab != Tab.RECENT) return

        val hasSelected = gridAdapter.getSelectedItem() != null
        btnSend.isEnabled = true
        btnSend.alpha = 1f
        btnSend.text = if (hasSelected) "이미지 전송하기" else "+ 내 이미지 추가"
    }

    private fun showAddImageDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("이미지 가져오기")
            .setItems(arrayOf("갤러리에서 가져오기", "카메라에서 가져오기")) { _, which ->
                when (which) {
                    0 -> openGallery()
                    1 -> openCamera()
                }
            }
            .setNegativeButton("취소", null)
            .show()
    }

    // ✅ 갤러리 열기(시스템 Photo Picker)
    private fun openGallery() {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    // ✅ 카메라 열기 (권한 먼저 요청)
    private fun openCamera() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    // ✅ 권한 승인 후 실제 카메라 실행
    private fun openCameraInternal() {
        val uri = createImageUriForCamera()
        pendingCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    private fun addRecentImage(uri: Uri) {
        recentItems.add(
            0,
            DecorateItem(
                id = "user_${System.currentTimeMillis()}",
                imageUri = uri,
                isFavorite = true
            )
        )
        gridAdapter.submitList(recentItems.toList())

        tvCount.text = "최근 사용한 사진 (${recentItems.size}/${recentItems.size})"
        updateSendButtonUI()
    }

    // ✅ 카메라가 저장할 Uri 생성(FileProvider)
    private fun createImageUriForCamera(): Uri {
        val imagesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, "camera_${System.currentTimeMillis()}.jpg")

        // ✅ 가장 안전: manifest의 ${applicationId}.fileprovider 와 항상 일치
        val authority = "${requireContext().packageName}.fileprovider"


        return FileProvider.getUriForFile(requireContext(), authority, imageFile)
    }
}
