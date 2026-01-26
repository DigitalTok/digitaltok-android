package com.yourcompany.digitaltok.ui.decorate

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.Gravity
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
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import java.io.File
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yourcompany.digitaltok.R
import android.graphics.Color
import android.graphics.drawable.ColorDrawable

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

    private val maxSlots = 15

    // 초기: 회색 무지 15칸(이미지 없음)
    private val recentItems = mutableListOf<DecorateItem>().apply {
        repeat(maxSlots) { idx ->
            add(DecorateItem(id = "slot_$idx")) // imageUri=null, imageRes=null
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

    // 갤러리(시스템 Photo Picker)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) return@registerForActivityResult
            addRecentImage(uri)
        }

    // 카메라(촬영 후 Uri에 저장)
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val uri = pendingCameraUri
            if (!success || uri == null) return@registerForActivityResult
            addRecentImage(uri)
        }

    // 카메라 권한 요청
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
        val spanCount = 3
        rvGrid.layoutManager = GridLayoutManager(requireContext(), spanCount)

        // 간격(좌우 12dp, 상하 13dp) ItemDecoration
        val hSpace = resources.getDimensionPixelSize(R.dimen.grid_spacing_horizontal)
        val vSpace = resources.getDimensionPixelSize(R.dimen.grid_spacing_vertical)
        if (rvGrid.itemDecorationCount == 0) {
            rvGrid.addItemDecoration(GridSpacingItemDecoration(spanCount, hSpace, vSpace))
        }

        gridAdapter = DecorateAdapter(recentItems) {
            // 아이템 선택/해제될 때 버튼 UI 갱신
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

        updateCountUI()
        updateSendButtonUI()
    }

    private fun setTab(tab: Tab) {
        currentTab = tab
        val isRecent = tab == Tab.RECENT

        rvGrid.visibility = if (isRecent) View.VISIBLE else View.GONE
        rvTemplateList.visibility = if (isRecent) View.GONE else View.VISIBLE
        sendContainer.visibility = if (isRecent) View.VISIBLE else View.GONE

        tvCount.text = if (isRecent) {
            val filled = recentItems.count { it.imageUri != null }
            "최근 사용한 사진 ($filled/$maxSlots)"
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

    private fun updateCountUI() {
        if (currentTab != Tab.RECENT) return
        val filled = recentItems.count { it.imageUri != null }
        tvCount.text = "최근 사용한 사진 ($filled/$maxSlots)"
    }



    private fun showAddImageDialog() {
        val dialogView = layoutInflater.inflate(
            R.layout.bottom_sheet_image_picker,
            null,
            false
        )

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()

        // 카메라
        dialogView.findViewById<TextView>(R.id.tvCamera).setOnClickListener {
            dialog.dismiss()
            openCamera()
        }

        // 갤러리
        dialogView.findViewById<TextView>(R.id.tvGallery).setOnClickListener {
            dialog.dismiss()
            openGallery()
        }

        // 돌아가기
        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.apply {
            // 다이얼로그 창 자체 배경을 투명으로
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        // 🔽 여기부터 "위치 제어" 핵심
        dialog.window?.let { window ->
            window.setGravity(Gravity.BOTTOM)

            // 1️⃣ 네비게이션 바 높이(px)
            val navBarHeightPx = run {
                val resId = resources.getIdentifier(
                    "navigation_bar_height",
                    "dimen",
                    "android"
                )
                if (resId > 0) resources.getDimensionPixelSize(resId) else 0
            }

            // 2️⃣ 16dp → px
            val margin16dpPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                32f,
                resources.displayMetrics
            ).toInt()

            // 3️⃣ 최종 y 오프셋 = 네비게이션바 + 16dp
            val params = window.attributes
            params.y = navBarHeightPx + margin16dpPx
            window.attributes = params

            // (선택) 가로 폭 꽉 차게
            window.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
    }




    private fun openGallery() {
        pickImageLauncher.launch(
            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
        )
    }

    private fun openCamera() {
        requestCameraPermission.launch(Manifest.permission.CAMERA)
    }

    private fun openCameraInternal() {
        val uri = createImageUriForCamera()
        pendingCameraUri = uri
        takePictureLauncher.launch(uri)
    }

    private fun addRecentImage(uri: Uri) {
        val newItem = DecorateItem(
            id = "user_${System.currentTimeMillis()}",
            imageUri = uri
        )

        // 맨 앞에 추가하고, 15개 유지
        recentItems.add(0, newItem)
        if (recentItems.size > maxSlots) {
            recentItems.removeAt(recentItems.lastIndex)
        }

        gridAdapter.submitList(recentItems.toList())

        updateCountUI()
        updateSendButtonUI()
    }

    private fun createImageUriForCamera(): Uri {
        val imagesDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val imageFile = File(imagesDir, "camera_${System.currentTimeMillis()}.jpg")

        val authority = "${requireContext().packageName}.fileprovider"
        return FileProvider.getUriForFile(requireContext(), authority, imageFile)
    }
}
