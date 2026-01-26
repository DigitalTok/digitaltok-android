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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.yourcompany.digitaltok.R
import java.io.File

class DecorateFragment : Fragment() {

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

        // ----- bind -----
        toggleTabs = view.findViewById(R.id.toggleTabs)
        tvCount = view.findViewById(R.id.tvCount)

        rvGrid = view.findViewById(R.id.rvGrid)
        rvTemplateList = view.findViewById(R.id.rvTemplateList)

        tilSearch = view.findViewById(R.id.tilSearch)
        etSearch = view.findViewById(R.id.etSearch)
        tvStationHint = view.findViewById(R.id.tvStationHint)
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

        gridAdapter = DecorateAdapter(recentItems) { updateSendButtonUI() }
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
            } else {
                Toast.makeText(requireContext(), "선택한 이미지 전송: ${selected.id}", Toast.LENGTH_SHORT).show()
                // TODO: NFC 전송 로직 연결
            }
        }

        updateCountUI()
        updateSendButtonUI()
    }

    // -------------------- TAB CONTROL --------------------

    private fun setTab(tab: Tab) {
        currentTab = tab
        val isRecent = tab == Tab.RECENT

        rvGrid.visibility = if (isRecent) View.VISIBLE else View.GONE
        sendContainer.visibility = if (isRecent) View.VISIBLE else View.GONE

        if (isRecent) {
            // 최근사진 화면
            tvCount.visibility = View.VISIBLE

            // 템플릿 관련 다 숨김
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
            // 템플릿 탭: 메뉴부터
            tvCount.visibility = View.GONE
            showTemplateMenu()
        }

        updateSendButtonUI()
    }

    private fun showTemplateMenu() {
        currentScreen = TemplateScreen.MENU

        rvTemplateList.visibility = View.VISIBLE

        rvTransportSeats.visibility = View.GONE
        tilSearch.visibility = View.GONE
        tvStationHint.visibility = View.GONE
        rvStations.visibility = View.GONE

        etSearch.setText("")
    }

    // -------------------- TEMPLATE FLOW --------------------

    /** 템플릿 메뉴 → 교통약자 → 좌석 리스트 */
    private fun showSeatList() {
        currentScreen = TemplateScreen.SEAT_LIST

        rvTemplateList.visibility = View.GONE
        rvTransportSeats.visibility = View.VISIBLE

        tilSearch.visibility = View.GONE
        tvStationHint.visibility = View.GONE
        rvStations.visibility = View.GONE

        // placeholder 변경
        etSearch.hint = "예) 임산부석"
        etSearch.setText("")

        // (나중에 API) fetch seats
        shownTransport.clear()
        shownTransport.addAll(allTransportSeats)
        transportAdapter.notifyDataSetChanged()
    }

    /** 템플릿 메뉴 → 지하철역 → 역 리스트(A) */
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
    }

    /** 교통약자 → 좌석 클릭(임산부석) → 역 리스트(B) */
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

    // -------------------- IMAGE PICKER --------------------

    private fun showAddImageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.bottom_sheet_image_picker, null, false)

        val dialog = MaterialAlertDialogBuilder(requireContext())
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

        dialogView.findViewById<MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        dialog.window?.let { window ->
            window.setGravity(Gravity.BOTTOM)

            val navBarHeightPx = run {
                val resId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
                if (resId > 0) resources.getDimensionPixelSize(resId) else 0
            }

            val marginPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                32f,
                resources.displayMetrics
            ).toInt()

            val params = window.attributes
            params.y = navBarHeightPx + marginPx
            window.attributes = params

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
