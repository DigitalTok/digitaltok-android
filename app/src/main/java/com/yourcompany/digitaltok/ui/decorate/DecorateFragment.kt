package com.yourcompany.digitaltok.ui.decorate

import android.Manifest
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yourcompany.digitaltok.R
import com.yourcompany.digitaltok.ui.upload.ImagePreviewFragment
import java.io.File

class DecorateFragment : Fragment() {

    private val viewModel: DecorateViewModel by viewModels()
    private var loadingDialog: AlertDialog? = null

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
    private lateinit var templateAdapter: TemplateAdapter

    // ----- Stations list (reused UI) -----
    private lateinit var tilSearch: TextInputLayout
    private lateinit var etSearch: TextInputEditText
    private lateinit var tvStationHint: TextView
    private lateinit var rvStations: RecyclerView
    private lateinit var stationAdapter: TemplateAdapter
    private val shownStations = mutableListOf<TemplateItem>()

    // ----- Transport seats list -----
    private lateinit var rvTransportSeats: RecyclerView
    private lateinit var transportAdapter: TemplateAdapter
    private lateinit var tvTabHint: TextView

    private val shownTransport = mutableListOf<TemplateItem>()

    private enum class Tab { RECENT, TEMPLATE }
    private var currentTab: Tab = Tab.RECENT
    private enum class TemplateScreen {
        MENU,                  // 템플릿 메뉴(2개)
        SEAT_LIST,             // 교통약자 좌석 리스트
        STATION_LIST_FROM_MENU,// 지하철역 클릭으로 들어온 역 리스트(A)
        STATION_LIST_FROM_SEAT // 임산부석 등 좌석 클릭으로 들어온 역 리스트(B)
    }
    private var currentScreen: TemplateScreen = TemplateScreen.MENU

    // API 분기용 선택값
    private var selectedSeatId: String? = null
    private var selectedStationId: String? = null

    private val maxSlots = 15

    // 최근사진 15칸
    private val recentItems = mutableListOf<DecorateItem>().apply {
        repeat(maxSlots) { idx -> add(DecorateItem(id = "slot_$idx")) }
    }

    // 템플릿 메뉴 2개
    private val templateList = listOf(
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

    // 교통약자 좌석 리스트(샘플)
    private val allTransportSeats: List<TemplateItem> = listOf(
        TemplateItem("ts_pregnant", "임산부석", "UX라이팅 필요", R.drawable.blank_img),
        TemplateItem("ts_elder", "노약자석", "UX라이팅 필요", R.drawable.blank_img),
        TemplateItem("ts_disabled", "장애인석", "UX라이팅 필요", R.drawable.blank_img),
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
        templateAdapter = TemplateAdapter(templateList) { item ->
            when (item.id) {
                "template_station" -> {
                    // 템플릿 메뉴 → 지하철역 → 역 리스트(A)
                    selectedSeatId = null
                    showStationListFromMenu()
                }

                "template_transport" -> {
                    // 템플릿 메뉴 → 교통약자 → 좌석 리스트
                    selectedSeatId = null
                    showSeatList()
                }
            }
        }
        rvTemplateList.adapter = templateAdapter

        // ----- transport seats list -----
        rvTransportSeats.layoutManager = LinearLayoutManager(requireContext())
        shownTransport.clear()
        shownTransport.addAll(allTransportSeats)
        transportAdapter = TemplateAdapter(shownTransport) { seat ->
            // 교통약자 → 좌석 클릭(예: 임산부석) → 역 리스트(B)
            selectedSeatId = seat.id
            showStationListFromSeat(seatTitle = seat.title)
        }
        rvTransportSeats.adapter = transportAdapter

        // ----- stations list (UI reused for A/B) -----
        rvStations.layoutManager = LinearLayoutManager(requireContext())
        shownStations.clear()
        shownStations.addAll(allStations)
        stationAdapter = TemplateAdapter(shownStations) { station ->
            selectedStationId = station.id

            when (currentScreen) {
                TemplateScreen.STATION_LIST_FROM_MENU -> {
                    // 나중에 “지하철역 경로” 상세 API
                    Toast.makeText(requireContext(),
                        "역 상세(지하철역 경로): ${station.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: apiStation.getStationDetail(station.id)
                }

                TemplateScreen.STATION_LIST_FROM_SEAT -> {
                    // 나중에 “좌석 경로” 상세 API
                    Toast.makeText(requireContext(),
                        "역 상세(좌석 경로, seat=${selectedSeatId}): ${station.title}",
                        Toast.LENGTH_SHORT
                    ).show()
                    // TODO: apiSeat.getStationDetailForSeat(selectedSeatId!!, station.id)
                }

                else -> Unit
            }
        }
        rvStations.adapter = stationAdapter

        // ----- search: stations list에서만 -----
        etSearch.addTextChangedListener { editable ->
            if (currentScreen != TemplateScreen.STATION_LIST_FROM_MENU &&
                currentScreen != TemplateScreen.STATION_LIST_FROM_SEAT
            ) return@addTextChangedListener

            val q = editable?.toString()?.trim().orEmpty()

            // 지금은 로컬 필터
            // 나중에 source에 따라 API 검색으로도 분리 가능
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
            if (selected == null) {
                showAddImageDialog()
                return@setOnClickListener
            }

            // 1. 서버에 있는 이미지 (ID가 숫자)
            val imageId = selected.id.toIntOrNull()
            if (imageId != null && selected.imageUri != null) {
                goToPreviewScreen(imageId, selected.imageUri.toString())
                return@setOnClickListener
            }

            // 2. 로컬에만 있는 새 이미지 (ID가 "user_"로 시작)
            if (selected.id.startsWith("user_") && selected.imageUri != null) {
                val imageFile = uriToFile(selected.imageUri)
                if (imageFile != null) {
                    viewModel.uploadImage(imageFile) // 업로드 후 observeViewModel에서 처리
                } else {
                    Toast.makeText(requireContext(), "이미지 파일을 준비할 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                return@setOnClickListener
            }

            // 3. 예외 케이스
            Toast.makeText(requireContext(), "이미지를 처리할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }

        updateCountUI()
        updateSendButtonUI()
    }

    private fun updateBackButtonVisibility() {
        if (!::topBarBack.isInitialized) return  // 안전장치

        val show = (currentTab == Tab.TEMPLATE && currentScreen != TemplateScreen.MENU)
        topBarBack.visibility = if (show) View.VISIBLE else View.GONE
    }

    // -------------------- BACK PRESS --------------------
    private fun setupOnBackPressed() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // 템플릿 탭의 하위 메뉴(좌석 리스트, 역 리스트 등)에 있을 경우
                if (currentTab == Tab.TEMPLATE && currentScreen != TemplateScreen.MENU) {
                    // 이전 화면인 템플릿 메뉴로 돌아감
                    showTemplateMenu()
                } else {
                    // 그 외의 경우(최근 사진 탭, 템플릿 첫 화면)에는 기본 뒤로가기 동작 수행
                    // 콜백을 비활성화하고, 액티비티의 기본 뒤로가기 로직을 다시 호출
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
            selectedSeatId = null
            selectedStationId = null

            val filled = recentItems.count { it.imageUri != null }
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
        shownTransport.clear()
        shownTransport.addAll(allTransportSeats)
        transportAdapter.notifyDataSetChanged()

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

    private fun showStationListFromSeat(seatTitle: String) {
        currentScreen = TemplateScreen.STATION_LIST_FROM_SEAT

        rvTemplateList.visibility = View.GONE
        rvTransportSeats.visibility = View.GONE
        tilSearch.visibility = View.VISIBLE
        tvStationHint.visibility = View.VISIBLE
        rvStations.visibility = View.VISIBLE

        tvStationHint.text = "${seatTitle}에 적용할 역을 선택해 주세요"
        etSearch.hint = "더 많은 역을 검색해 보세요"
        etSearch.setText("")

        loadStationsForSeat(selectedSeatId)

        updateBackButtonVisibility()
    }



    // -------------------- DATA LOADING (NOW: SAMPLE / LATER: API) --------------------

    private fun loadStationsForMenu() {
        // TODO: API A (지하철역 경로)로 교체
        shownStations.clear()
        shownStations.addAll(allStations)
        stationAdapter.notifyDataSetChanged()
    }

    private fun loadStationsForSeat(seatId: String?) {
        // TODO: API B (좌석 경로)로 교체 (seatId 사용)
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

    // -------------------- IMAGE PICKER & CROP --------------------

    private fun showAddImageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_image_picker, null, false)

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        // 클릭 이벤트
        dialogView.findViewById<TextView>(R.id.tvCamera).setOnClickListener {
            dialog.dismiss()
            openCamera()
        }

        dialogView.findViewById<TextView>(R.id.tvGallery).setOnClickListener {
            dialog.dismiss()
            openGallery()
        }

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        // 윈도우 설정은 show() 이후에 해야 적용됨
        dialog.window?.let { w ->
            // 1) 배경 투명 (dialogView의 카드/라운드가 보이게)
            w.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            // 2) 딤 강제 ON
            w.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            w.setDimAmount(0.55f)

            // 3) 아래 붙이기
            w.setGravity(Gravity.BOTTOM)

            // 4) 네비게이션 바 + margin 만큼 위로 올리기
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

            // 5) 폭/높이
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
            imageUri = uri
        )

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

    // -------------------- VIEWMODEL & UPLOAD --------------------

    private fun observeViewModel() {
        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.UploadUiState.Loading -> {
                    showLoadingDialog()
                }
                is DecorateViewModel.UploadUiState.Success -> {
                    hideLoadingDialog()
                    // 업로드 성공 시, 미리보기 화면으로 이동
                    val result = state.result
                    goToPreviewScreen(result.image.imageId, result.image.previewUrl)

                }
                is DecorateViewModel.UploadUiState.Error -> {
                    hideLoadingDialog()
                    Toast.makeText(requireContext(), "업로드 실패: ${state.message}", Toast.LENGTH_LONG).show()
                }
                is DecorateViewModel.UploadUiState.Idle -> {
                    hideLoadingDialog()
                }
            }
        }

        viewModel.favoriteState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.FavoriteUiState.Loading -> {
                    // 로딩 중 UI 표시가 필요하다면 여기에 구현 (예: 작은 스피너 표시)
                }
                is DecorateViewModel.FavoriteUiState.Success -> {
                    val message = if (state.isFavorite) "즐겨찾기에 추가했습니다." else "즐겨찾기에서 해제했습니다."
                    Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                }
                is DecorateViewModel.FavoriteUiState.Error -> {
                    Toast.makeText(requireContext(), "오류: ${state.message}", Toast.LENGTH_SHORT).show()
                    // 참고: API 에러 발생 시, 어댑터의 UI 상태를 원래대로 되돌리는 로직을 추가하면
                    // 더 안정적인 사용자 경험을 제공할 수 있습니다.
                }
                is DecorateViewModel.FavoriteUiState.Idle -> {
                    // 아무것도 안 함
                }
            }
        }

        viewModel.recentImagesState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is DecorateViewModel.RecentImagesUiState.Loading -> {
                    // 로딩 UI 표시 없음 (사용자 요청)
                }
                is DecorateViewModel.RecentImagesUiState.Success -> {
                    val recentApiItems = state.response.items

                    // 1. API 응답을 UI 모델 (DecorateItem) 리스트로 변환
                    val newItems = recentApiItems.map {
                        DecorateItem(
                            id = it.imageId.toString(),
                            imageUri = Uri.parse(it.previewUrl) // 서버 URL을 Uri로 변환
                        )
                    }

                    // 2. 즐겨찾기 된 이미지 ID들을 Set으로 준비
                    val pinnedIds = recentApiItems
                        .filter { it.isFavorite }
                        .map { it.imageId.toString() }
                        .toSet()

                    // 3. Fragment의 메인 리스트를 교체
                    recentItems.clear()
                    recentItems.addAll(newItems)

                    // 4. 최대 슬롯(15개)에 맞춰 빈 아이템 추가
                    val emptySlots = maxSlots - newItems.size
                    if (emptySlots > 0) {
                        repeat(emptySlots) { idx -> recentItems.add(DecorateItem(id = "slot_$idx")) }
                    }

                    // 5. 어댑터에 최종 리스트와 즐겨찾기 정보 전달
                    gridAdapter.submitList(recentItems.toList(), pinnedIds)

                    // 6. 상단 카운트 UI 업데이트
                    updateCountUI()
                }
                is DecorateViewModel.RecentImagesUiState.Error -> {
                    Toast.makeText(requireContext(), "최근 이미지 로딩 실패: ${state.message}", Toast.LENGTH_SHORT).show()
                    // TODO: 이미지 로딩 실패 시 UI/UX 개선 (예: 재시도 버튼 표시)
                }
                else -> { /* Idle */ }
            }
        }
    }

    private fun goToPreviewScreen(imageId: Int, previewUrl: String?) {
        if (previewUrl == null) {
            Toast.makeText(requireContext(), "미리보기 정보를 불러올 수 없습니다.", Toast.LENGTH_SHORT).show()
            return
        }
        parentFragmentManager.beginTransaction()
            .add((requireView().parent as ViewGroup).id, ImagePreviewFragment.newInstance(imageId, previewUrl))
            .addToBackStack(null)
            .commit()
    }

    private fun uriToFile(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            // Create a temporary file in the cache directory
            val file = File(requireContext().cacheDir, "upload_${System.currentTimeMillis()}.jpg")
            file.outputStream().use { fileOut ->
                inputStream.copyTo(fileOut)
            }
            inputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun showLoadingDialog() {
        if (loadingDialog == null) {
            val progressBar = ProgressBar(requireContext()).apply {
                val padding = 100
                setPadding(padding, padding, padding, padding)
            }
            loadingDialog = MaterialAlertDialogBuilder(requireContext())
                .setTitle("업로드 중...")
                .setView(progressBar)
                .setCancelable(false)
                .create()
        }
        loadingDialog?.show()
    }

    private fun hideLoadingDialog() {
        loadingDialog?.dismiss()
    }
}
