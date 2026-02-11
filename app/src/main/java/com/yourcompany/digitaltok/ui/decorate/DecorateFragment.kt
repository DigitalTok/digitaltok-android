package com.yourcompany.digitaltok.ui.decorate

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.upload.TemplatePreviewFragment
import com.yourcompany.digitaltok.ui.upload.ImagePreviewFragment
import java.io.File

class DecorateFragment : Fragment() {

    private val viewModel: DecorateViewModel by viewModels()

    // TOP BAR
    private lateinit var connectTopAppBar: View
    private lateinit var topBarTitle: TextView
    private lateinit var topBarBack: View

    // ----- Top / Tabs -----
    private lateinit var toggleTabs: MaterialButtonToggleGroup
    private lateinit var tvCount: TextView

    // ----- Recent -----
    private lateinit var rvGrid: RecyclerView
    private lateinit var sendContainer: View
    private lateinit var btnSend: com.google.android.material.button.MaterialButton
    private lateinit var gridAdapter: DecorateAdapter

    // ----- Template menu (2 items) -----
    private lateinit var rvTemplateList: RecyclerView
    private lateinit var templateAdapter: PriorityAdapter

    // ----- Stations list (reused UI) -----
    private lateinit var tilSearch: TextInputLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var tvStationHint: TextView
    private lateinit var rvStations: RecyclerView
    private lateinit var stationAdapter: PriorityAdapter
    private val shownStations = mutableListOf<TemplateItem>()

    // ----- Transport seats list -----
    private lateinit var rvTransportSeats: RecyclerView
    private lateinit var transportAdapter: PriorityAdapter
    private lateinit var tvTabHint: TextView

    private val shownTransport = mutableListOf<TemplateItem>()

    private enum class Tab { RECENT, TEMPLATE }
    private var currentTab: Tab = Tab.RECENT

    private enum class TemplateScreen {
        MENU,                  // 템플릿 메뉴(2개)
        SEAT_LIST,             // 교통약자 좌석 리스트
        STATION_LIST_FROM_MENU // 지하철역 클릭으로 들어온 역 리스트(A)
    }
    private var currentScreen: TemplateScreen = TemplateScreen.MENU

    // API 분기용 선택값
    private var selectedStationId: String? = null

    private val maxSlots = 15

    // 최근사진 15칸
    private var recentItems = mutableListOf<DecorateItem>()

    // 템플릿 메뉴 2개
    private var templateList = mutableListOf(
        TemplateItem(
            id = "template_transport",
            title = "교통약자 좌석",
            desc = "교통약자 좌석",
            thumbRes = R.drawable.blank_img
        ),
        TemplateItem(
            id = "template_station",
            title = "지하철역",
            desc = "지하철 노선별로 정리된 템플릿",
            thumbRes = R.drawable.blank_img
        )
    )

    // 역 리스트(샘플)
    private val allStations: List<TemplateItem> = listOf(
        TemplateItem("st_gangnam", "강남역", "2호선", R.drawable.blank_img),
        TemplateItem("st_gangbyeon", "강변역", "2호선", R.drawable.blank_img),
        TemplateItem("st_konkuk", "건대입구역", "2호선", R.drawable.blank_img),
        TemplateItem("st_gyodae", "교대역", "2호선", R.drawable.blank_img),
        TemplateItem("st_guui", "구의역", "2호선", R.drawable.blank_img),
    )

    // 카메라 촬영 저장용 Uri
    private var pendingCameraUri: Uri? = null

    // 갤러리(시스템 Photo Picker)
    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) return@registerForActivityResult
            startCrop(uri)
        }

    // 카메라(촬영 후 Uri에 저장)
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            val uri = pendingCameraUri
            if (!success || uri == null) return@registerForActivityResult
            startCrop(uri)
        }

    // 카메라 권한 요청
    private val requestCameraPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) openCameraInternal()
            else Toast.makeText(requireContext(), "카메라 권한이 필요해요", Toast.LENGTH_SHORT).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // CropImageFragment로부터 크롭된 이미지 결과를 받기 위한 리스너 설정
        parentFragmentManager.setFragmentResultListener(CropImageFragment.REQUEST_KEY, this) { _, bundle ->
            val croppedUriString = bundle.getString(CropImageFragment.RESULT_CROPPED_URI)
            if (croppedUriString != null) {
                addRecentImage(croppedUriString.toUri())
            }
        }
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

        // 상단바 include
        connectTopAppBar = view.findViewById(R.id.connectTopAppBar)
        topBarTitle = connectTopAppBar.findViewById(R.id.titleTextView)
        topBarBack = connectTopAppBar.findViewById(R.id.backButton)

        topBarTitle.text = "꾸미기"
        topBarBack.visibility = View.VISIBLE
        topBarBack.setOnClickListener { showTemplateMenu() }

        observeViewModel()
        setupOnBackPressed()
        updateBackButtonVisibility()

        // ----- bind -----
        toggleTabs = view.findViewById(R.id.toggleTabs)
        tvCount = view.findViewById(R.id.tvCount)

        rvGrid = view.findViewById(R.id.rvGrid)
        rvTemplateList = view.findViewById(R.id.rvTemplateList)

        tilSearch = view.findViewById(R.id.tilSearch)
        etSearch = view.findViewById(R.id.etSearch)
        tvStationHint = view.findViewById(R.id.tvStationHint)
        tvTabHint = view.findViewById(R.id.tvTabHint)
        rvStations = view.findViewById(R.id.rvStations)

        rvTransportSeats = view.findViewById(R.id.rvTransportSeats)

        sendContainer = view.findViewById(R.id.sendContainer)
        btnSend = view.findViewById(R.id.btnSend)

        // ----- recent grid -----
        val spanCount = 3
        rvGrid.layoutManager = GridLayoutManager(requireContext(), spanCount)
        val hSpace = resources.getDimensionPixelSize(R.dimen.grid_spacing_horizontal)
        val vSpace = resources.getDimensionPixelSize(R.dimen.grid_spacing_vertical)
        if (rvGrid.itemDecorationCount == 0) {
            rvGrid.addItemDecoration(GridSpacingItemDecoration(spanCount, hSpace, vSpace))
        }

        gridAdapter = DecorateAdapter(
            items = recentItems,
            onItemClick = { updateSendButtonUI() },
            onFavoriteClick = { imageId, isFavorite ->
                viewModel.toggleFavoriteStatus(imageId, isFavorite)
            }
        )
        rvGrid.adapter = gridAdapter

        // ----- template menu (2 items) -----
        rvTemplateList.layoutManager = LinearLayoutManager(requireContext())
        templateAdapter = PriorityAdapter(templateList) { item ->
            when (item.id) {
                "template_station" -> showStationListFromMenu()
                "template_transport" -> showSeatList()
            }
        }
        rvTemplateList.adapter = templateAdapter

        // ----- transport seats list -----
        rvTransportSeats.layoutManager = LinearLayoutManager(requireContext())
        transportAdapter = PriorityAdapter(shownTransport) { seat ->
            seat.id.toIntOrNull()?.let {
                viewModel.fetchPriorityTemplateDetail(it)
            }
        }
        rvTransportSeats.adapter = transportAdapter

        // ----- stations list (UI reused for A) -----
        rvStations.layoutManager = LinearLayoutManager(requireContext())
        stationAdapter = PriorityAdapter(shownStations) { station ->
            selectedStationId = station.id

            when (currentScreen) {
                TemplateScreen.STATION_LIST_FROM_MENU -> {
                    Toast.makeText(
                        requireContext(),
                        "역 상세(지하철역 경로): ${station.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: apiStation.getStationDetail(station.id)
                }
                else -> Unit
            }
        }
        rvStations.adapter = stationAdapter

        // ----- search: stations list에서만 -----
        etSearch.addTextChangedListener { editable ->
            if (currentScreen != TemplateScreen.STATION_LIST_FROM_MENU) return@addTextChangedListener
            val q = editable?.toString()?.trim().orEmpty()
            applyStationFilterLocal(q)
        }

        // ----- tabs -----
        toggleTabs.check(R.id.btnRecent)
        setTab(Tab.RECENT)

        toggleTabs.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener
            if (checkedId == R.id.btnRecent) setTab(Tab.RECENT) else setTab(Tab.TEMPLATE)
        }

        // ----- send button -----
        btnSend.setOnClickListener {
            if (currentTab != Tab.RECENT) return@setOnClickListener

            val selected = gridAdapter.getSelectedItem()
            if (selected == null || selected.isSlot) {
                showAddImageDialog()
                return@setOnClickListener
            }

            // 1. 서버에 있는 이미지 (ID가 숫자)
            val imageId = selected.id.toIntOrNull()
            if (imageId != null && !selected.previewUrl.isNullOrEmpty()) {
                goToPreviewScreen(imageId, selected.previewUrl)
                return@setOnClickListener
            }

            // 2. 로컬에만 있는 새 이미지 (ID가 "user_"로 시작)
            if (selected.id.startsWith("user_") && selected.imageUri != null) {
                val imageFile = uriToFile(selected.imageUri)
                if (imageFile != null) {
                    viewModel.uploadImage(imageFile)
                } else {
                    Toast.makeText(requireContext(), "이미지 파일을 준비할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            Toast.makeText(requireContext(), "이미지를 처리할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        updateCountUI()
        updateSendButtonUI()
    }

    private fun updateBackButtonVisibility() {
        if (!::topBarBack.isInitialized) return
        val show = (currentTab == Tab.TEMPLATE && currentScreen != TemplateScreen.MENU)
        topBarBack.visibility = if (show) View.VISIBLE else View.GONE
    }

    // -------------------- BACK PRESS --------------------
    private fun setupOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (currentTab == Tab.TEMPLATE && currentScreen != TemplateScreen.MENU) {
                    showTemplateMenu()
                } else {
                    isEnabled = false
                    requireActivity().onBackPressedDispatcher.onBackPressed()
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }

    // ----- TAB CONTROL -----
    private fun setTab(tab: Tab) {
        currentTab = tab
        val isRecent = tab == Tab.RECENT

        rvGrid.visibility = if (isRecent) View.VISIBLE else View.GONE
        sendContainer.visibility = if (isRecent) View.VISIBLE else View.GONE

        if (isRecent) {
            tvCount.visibility = View.VISIBLE
            tvTabHint.visibility = View.GONE

            rvTemplateList.visibility = View.GONE
            rvTransportSeats.visibility = View.GONE
            tilSearch.visibility = View.GONE
            tvStationHint.visibility = View.GONE
            rvStations.visibility = View.GONE

            currentScreen = TemplateScreen.MENU
            selectedStationId = null

            val filled = recentItems.count { !it.isSlot }
            tvCount.text = "최근 사용한 사진 ($filled/$maxSlots)"
        } else {
            tvCount.visibility = View.GONE
            tvTabHint.visibility = View.GONE
            tvTabHint.text = "역명과 노선 색상이 포함된 템플릿"
            showTemplateMenu()
        }

        updateBackButtonVisibility()
        updateSendButtonUI()
    }

    private fun showTemplateMenu() {
        currentScreen = TemplateScreen.MENU

        tvTabHint.visibility = View.GONE
        rvTemplateList.visibility = View.VISIBLE
        rvTransportSeats.visibility = View.GONE
        tilSearch.visibility = View.GONE
        tvStationHint.visibility = View.GONE
        rvStations.visibility = View.GONE
        etSearch.setText("")

        updateBackButtonVisibility()
    }

    private fun showSeatList() {
        currentScreen = TemplateScreen.SEAT_LIST

        tvTabHint.visibility = View.VISIBLE
        rvTemplateList.visibility = View.GONE
        rvTransportSeats.visibility = View.VISIBLE
        tilSearch.visibility = View.GONE
        tvStationHint.visibility = View.GONE
        rvStations.visibility = View.GONE

        etSearch.setText("")

        updateBackButtonVisibility()
    }

    private fun showStationListFromMenu() {
        currentScreen = TemplateScreen.STATION_LIST_FROM_MENU

        rvTemplateList.visibility = View.GONE
        rvTransportSeats.visibility = View.GONE
        tilSearch.visibility = View.VISIBLE
        tvStationHint.visibility = View.VISIBLE
        rvStations.visibility = View.VISIBLE

        tvStationHint.text = "역명과 노선 색상이 포함된 템플릿"
        etSearch.hint = "더 많은 역을 검색해 보세요"
        etSearch.setText("")

        loadStationsForMenu()

        updateBackButtonVisibility()
    }

    // -------------------- DATA LOADING (NOW: SAMPLE / LATER: API) --------------------
    private fun loadStationsForMenu() {
        shownStations.clear()
        shownStations.addAll(allStations)
        stationAdapter.notifyDataSetChanged()
    }

    private fun applyStationFilterLocal(query: String) {
        val base = allStations
        shownStations.clear()

        if (query.isBlank()) {
            shownStations.addAll(base)
        } else {
            shownStations.addAll(
                base.filter {
                    it.title.contains(query, ignoreCase = true) ||
                            it.desc.contains(query, ignoreCase = true)
                }
            )
        }
        stationAdapter.notifyDataSetChanged()
    }

    // -------------------- RECENT BUTTON UI --------------------
    private fun updateSendButtonUI() {
        if (currentTab != Tab.RECENT) return
        val selected = gridAdapter.getSelectedItem()
        val hasSelected = selected != null && !selected.isSlot
        btnSend.isEnabled = true
        btnSend.alpha = 1f
        btnSend.text = if (hasSelected) "이미지 전송하기" else "+ 내 이미지 추가"
    }

    private fun updateCountUI() {
        if (currentTab != Tab.RECENT) return
        val filled = recentItems.count { !it.isSlot }
        tvCount.text = "최근 사용한 사진 ($filled/$maxSlots)"
    }

    // -------------------- IMAGE PICKER & CROP --------------------
    private fun showAddImageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_image_picker, null, false)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialogView.findViewById<TextView>(R.id.tvCamera).setOnClickListener {
            dialog.dismiss()
            openCamera()
        }

        dialogView.findViewById<TextView>(R.id.tvGallery).setOnClickListener {
            dialog.dismiss()
            openGallery()
        }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel)
            .setOnClickListener { dialog.dismiss() }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        dialog.window?.let { w ->
            w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            w.setDimAmount(0.55f)
            w.setGravity(Gravity.BOTTOM)

            val navBarHeightPx = run {
                val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resId > 0) resources.getDimensionPixelSize(resId) else 0
            }

            val marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                32f,
                resources.displayMetrics
            ).toInt()

            w.attributes = w.attributes.apply {
                y = navBarHeightPx + marginPx
            }

            w.setLayout(
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

    private fun startCrop(uri: Uri) {
        val containerId = (requireView().parent as ViewGroup).id
        parentFragmentManager.beginTransaction()
            .add(containerId, CropImageFragment.newInstance(uri))
            .addToBackStack(null)
            .commit()
    }

    private fun addRecentImage(uri: Uri) {
        val newItem = DecorateItem(
            id = "user_${System.currentTimeMillis()}",
            imageUri = uri,
            isSlot = false
        )

        val firstEmptySlotIndex = recentItems.indexOfFirst { it.isSlot }
        if (firstEmptySlotIndex != -1) {
            recentItems[firstEmptySlotIndex] = newItem
        } else {
            if (recentItems.size >= maxSlots) {
                recentItems.removeAt(recentItems.lastIndex)
            }
            recentItems.add(0, newItem)
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

    // -------------------- VIEWMODEL & UPLOAD --------------------
    private fun observeViewModel() {
        // 교통약자 템플릿 목록
        viewModel.priorityTemplatesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.PriorityTemplatesUiState.Loading -> {
                    // no-op
                }
                is DecorateViewModel.PriorityTemplatesUiState.Success -> {
                    // 상세 목록 (원래 코드)
                    val newItems = state.templates.map {
                        TemplateItem(
                            id = it.templateId.toString(),
                            title = it.priorityType,
                            thumbUrl = it.templateImageUrl
                        )
                    }
                    shownTransport.clear()
                    shownTransport.addAll(newItems)
                    transportAdapter.notifyDataSetChanged()

                    // 메인 메뉴 아이템 썸네일 업데이트
                    state.templates.firstOrNull()?.let { firstTemplate ->
                        val transportMenuIndex = templateList.indexOfFirst { it.id == "template_transport" }
                        if (transportMenuIndex != -1) {
                            val oldItem = templateList[transportMenuIndex]
                            templateList[transportMenuIndex] = oldItem.copy(
                                thumbUrl = firstTemplate.templateImageUrl,
                                thumbRes = 0 // URL 사용할 것이므로 리소스 ID는 0으로
                            )
                            templateAdapter.notifyItemChanged(transportMenuIndex)
                        }
                    }
                }
                is DecorateViewModel.PriorityTemplatesUiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        // 교통약자 템플릿 상세
        viewModel.priorityTemplateDetailState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.PriorityTemplateDetailUiState.Loading -> {
                    // 로딩 표시가 필요하다면 여기에 구현
                }
                is DecorateViewModel.PriorityTemplateDetailUiState.Success -> {
                    val detail = state.templateDetail
                    parentFragmentManager.beginTransaction()
                        .add(
                            (requireView().parent as ViewGroup).id,
                            TemplatePreviewFragment.newInstance(
                                name = detail.priorityType,
                                imageUrl = detail.templateImageUrl,
                                dataUrl = detail.templateDataUrl
                            )
                        )
                        .addToBackStack(null)
                        .commit()
                }
                is DecorateViewModel.PriorityTemplateDetailUiState.Error -> {
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.UploadUiState.Loading -> {
                    // no-op
                }
                is DecorateViewModel.UploadUiState.Success -> {
                    val result = state.result
                    goToPreviewScreen(result.image.imageId, result.image.previewUrl)
                }
                is DecorateViewModel.UploadUiState.Error -> {
                    Toast.makeText(requireContext(), "업로드 실패: ${state.message}", Toast.LENGTH_LONG).show()
                }
                is DecorateViewModel.UploadUiState.Idle -> Unit
            }
        }

        viewModel.favoriteState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.FavoriteUiState.Loading -> {
                    // no-op
                }
                is DecorateViewModel.FavoriteUiState.Success -> {
                    viewModel.fetchRecentImages()
                }
                is DecorateViewModel.FavoriteUiState.Error -> {
                    Toast.makeText(requireContext(), "오류: ${state.message}", Toast.LENGTH_SHORT).show()
                    viewModel.fetchRecentImages()
                }
                is DecorateViewModel.FavoriteUiState.Idle -> Unit
            }
        }

        viewModel.recentImagesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.RecentImagesUiState.Loading -> {
                    // no-op
                }
                is DecorateViewModel.RecentImagesUiState.Success -> {
                    val recentApiItems = state.response.items

                    val newItems = recentApiItems.map {
                        DecorateItem(
                            id = it.imageId.toString(),
                            previewUrl = it.previewUrl,
                            isFavorite = it.isFavorite,
                            isSlot = false
                        )
                    }

                    val sortedItems = newItems.sortedWith(compareByDescending { it.isFavorite })

                    recentItems.clear()
                    recentItems.addAll(sortedItems)

                    val emptySlots = maxSlots - recentItems.size
                    if (emptySlots > 0) {
                        repeat(emptySlots) { idx ->
                            recentItems.add(DecorateItem(id = "slot_$idx", isSlot = true))
                        }
                    }

                    gridAdapter.submitList(recentItems.toList())
                    updateCountUI()
                }
                is DecorateViewModel.RecentImagesUiState.Error -> {
                    Toast.makeText(requireContext(), "최근 이미지 로딩 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                }
                else -> Unit
            }
        }
    }

    private fun goToPreviewScreen(imageId: Int, previewUrl: String?) {
        if (previewUrl == null) {
            Toast.makeText(requireContext(), "미리보기 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        Log.d("PREVIEW_DEBUG", "imageId=$imageId")
        Log.d("PREVIEW_DEBUG", "previewUrl=$previewUrl")
        parentFragmentManager.beginTransaction()
            .add((requireView().parent as ViewGroup).id, ImagePreviewFragment.newInstance(imageId, previewUrl))
            .addToBackStack(null)
            .commit()
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val cr = requireContext().contentResolver
            val mime = cr.getType(uri) ?: "image/png"

            val ext = when (mime.lowercase()) {
                "image/png" -> "png"
                "image/jpeg", "image/jpg" -> "jpg"
                else -> "png"
            }

            cr.openInputStream(uri)?.use { input ->
                val file = File(requireContext().cacheDir, "upload_${System.currentTimeMillis()}.$ext")
                file.outputStream().use { out -> input.copyTo(out) }
                file
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
