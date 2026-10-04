package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.MemberCardEntity
import com.example.ui.components.CardFace
import com.example.ui.components.CardFormEditor
import com.example.ui.components.FlippableCardContainer
import com.example.ui.dialogs.AadhaarScannerDialog
import com.example.ui.dialogs.AppSettingsDialog
import com.example.ui.dialogs.AssociationInfoDialog
import com.example.ui.dialogs.CardDownloadExportDialog
import com.example.ui.dialogs.PaymentUtrDialog
import com.example.ui.dialogs.PhotoSourceSelectionDialog
import com.example.ui.dialogs.VerificationScannerDialog
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionRed
import com.example.ui.viewmodel.AppNavTab
import com.example.ui.viewmodel.MainViewModel
import com.example.util.BitmapRendererHelper
import com.example.util.CardExporter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CardMakerScreen(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentCard by viewModel.currentCard.collectAsStateWithLifecycle()
    val cardFace by viewModel.cardFace.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val allCards by viewModel.allCards.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedFilter by viewModel.selectedFilter.collectAsStateWithLifecycle()
    val showInfoDialog by viewModel.showInfoDialog.collectAsStateWithLifecycle()
    val showVerifyDialog by viewModel.showVerifyDialog.collectAsStateWithLifecycle()
    val showPaymentDialog by viewModel.showPaymentDialog.collectAsStateWithLifecycle()
    val showDownloadDialog by viewModel.showDownloadDialog.collectAsStateWithLifecycle()
    val showPhotoSourceDialog by viewModel.showPhotoSourceDialog.collectAsStateWithLifecycle()
    val showAadhaarScannerDialog by viewModel.showAadhaarScannerDialog.collectAsStateWithLifecycle()
    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val autoCropPhoto by viewModel.autoCropPhoto.collectAsStateWithLifecycle()
    val photoAspectRatio by viewModel.photoAspectRatio.collectAsStateWithLifecycle()
    val aadhaarAutoTamil by viewModel.aadhaarAutoTamil.collectAsStateWithLifecycle()
    val showCameraScreen by viewModel.showCameraScreen.collectAsStateWithLifecycle()
    val cameraScreenMode by viewModel.cameraScreenMode.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val pendingCount = remember(allCards) { allCards.count { it.approvalStatus == "PENDING" } }
    val snackbarHostState = remember { SnackbarHostState() }

    // Logo Gallery Picker
    val logoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateCustomLogo(uri.toString())
        }
    }

    // Flag Gallery Picker
    val flagPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateCustomFlag(uri.toString())
        }
    }

    // Govt Accreditation Seal Gallery Picker
    val sealPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.updateCustomSeal(uri.toString())
        }
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbarMessage()
        }
    }

    BackHandler(enabled = showCameraScreen || currentTab != AppNavTab.EDITOR) {
        if (showCameraScreen) {
            viewModel.closeCamera()
        } else {
            viewModel.setTab(AppNavTab.EDITOR)
        }
    }

    if (showCameraScreen) {
        // FULLSCREEN CAMERAX CAPTURE & AADHAAR OCR SCANNER SCREEN
        CameraCaptureScreen(
            currentCard = currentCard,
            initialMode = cameraScreenMode,
            initialAutoCropPhoto = autoCropPhoto,
            initialPhotoAspectRatio = photoAspectRatio,
            initialAadhaarInTamil = aadhaarAutoTamil,
            onPhotoCaptured = { uri -> viewModel.updateMemberPhoto(uri) },
            onAadhaarScanned = { updated -> viewModel.applyAadhaarExtractedCard(updated) },
            onSettingsChange = { autoCrop, ratio, tamilMode ->
                viewModel.setAutoCropPhoto(autoCrop)
                viewModel.setPhotoAspectRatio(ratio)
                viewModel.setAadhaarAutoTamil(tamilMode)
            },
            onOpenSettings = { viewModel.setShowSettingsDialog(true) },
            onClose = { viewModel.closeCamera() }
        )
        return
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (currentTab != AppNavTab.SUPER_ADMIN) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_association_logo),
                                contentDescription = "TNPA Logo",
                                modifier = Modifier.size(38.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "தமிழ்நாடு பெயிண்டர்கள் & ஓவியர்கள் சங்கம்",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFFFD700))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "அரசு பதிவு எண்: 50-26-00044",
                                        color = Color(0xFFFFECEC),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    },
                    actions = {
                        // Instant Share Card / Export PDF Action
                        IconButton(
                            onClick = {
                                CardExporter.shareMemberCardPdfDirectly(context, currentCard)
                            },
                            modifier = Modifier.testTag("action_quick_share_pdf")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share ID Card",
                                tint = Color.White
                            )
                        }

                        // Live Camera Open Button
                        IconButton(
                            onClick = { viewModel.openCamera(CameraMode.AADHAAR_SCAN) },
                            modifier = Modifier.testTag("action_open_camera_ocr")
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Camera Scanner",
                                tint = Color.White
                            )
                        }

                        // Super Admin Portal Button with pending badge
                        IconButton(
                            onClick = { viewModel.setTab(AppNavTab.SUPER_ADMIN) },
                            modifier = Modifier.testTag("action_super_admin")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (pendingCount > 0) {
                                        Badge(containerColor = Color(0xFFFFD700), contentColor = Color.Black) {
                                            Text(pendingCount.toString(), fontWeight = FontWeight.Black)
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Super Admin Portal",
                                    tint = Color.White
                                )
                            }
                        }

                        // QR Scanner / Verify
                        IconButton(
                            onClick = { viewModel.setShowVerifyDialog(true) },
                            modifier = Modifier.testTag("action_verify_qr")
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Verify QR",
                                tint = Color.White
                            )
                        }

                        // Association Info
                        IconButton(
                            onClick = { viewModel.setShowInfoDialog(true) },
                            modifier = Modifier.testTag("action_info")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = "Association Info",
                                tint = Color.White
                            )
                        }

                        // App Settings (Camera & Aadhaar Options)
                        IconButton(
                            onClick = { viewModel.setShowSettingsDialog(true) },
                            modifier = Modifier.testTag("action_app_settings")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = UnionRed,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            }
        },
        bottomBar = {
            if (currentTab != AppNavTab.SUPER_ADMIN) {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == AppNavTab.EDITOR,
                        onClick = { viewModel.setTab(AppNavTab.EDITOR) },
                        icon = { Icon(Icons.Default.Badge, contentDescription = "Editor") },
                        label = { Text("அட்டை", fontSize = 9.5.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UnionRed,
                            selectedTextColor = UnionRed,
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("tab_editor")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.PREVIEW,
                        onClick = { viewModel.setTab(AppNavTab.PREVIEW) },
                        icon = { Icon(Icons.Default.Visibility, contentDescription = "Preview") },
                        label = { Text("முன்னோட்டம்", fontSize = 9.5.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UnionRed,
                            selectedTextColor = UnionRed,
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("tab_preview")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.DISTRICTS,
                        onClick = { viewModel.setTab(AppNavTab.DISTRICTS) },
                        icon = { Icon(Icons.Default.LocationCity, contentDescription = "Districts") },
                        label = { Text("மாவட்டங்கள்", fontSize = 9.5.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UnionRed,
                            selectedTextColor = UnionRed,
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("tab_districts")
                    )

                    NavigationBarItem(
                        selected = currentTab == AppNavTab.DIRECTORY,
                        onClick = { viewModel.setTab(AppNavTab.DIRECTORY) },
                        icon = { Icon(Icons.Default.People, contentDescription = "Directory") },
                        label = { Text("பதிவேடு (${allCards.size})", fontSize = 9.5.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = UnionRed,
                            selectedTextColor = UnionRed,
                            indicatorColor = Color(0xFFFFE4E6)
                        ),
                        modifier = Modifier.testTag("tab_directory")
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF1F5F9))
        ) {
            when (currentTab) {
                AppNavTab.EDITOR -> {
                    // LIVE EDITOR WITH REAL-TIME MINI PREVIEW ABOVE FORM
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(14.dp),
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Mini Live Preview Header Card
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "நேரடி மாதிரி அட்டை (Live PVC Preview):",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = "💡 லோகோ, படம், பெயரைத் தொட்டு நேரடியாக மாற்றலாம்",
                                            fontSize = 9.5.sp,
                                            color = Color(0xFF64748B)
                                        )
                                    }

                                    Row {
                                        if (currentCard.cardType == "MEMBER") {
                                            IconButton(
                                                onClick = { viewModel.flipCardFace() },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Flip,
                                                    contentDescription = "Flip",
                                                    tint = UnionRed,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        IconButton(
                                            onClick = {
                                                val bmp = BitmapRendererHelper.renderCardToBitmap(
                                                    context,
                                                    currentCard,
                                                    isBack = (cardFace == CardFace.Back && currentCard.cardType == "MEMBER")
                                                )
                                                CardExporter.shareCardBitmap(
                                                    context,
                                                    bmp,
                                                    "${currentCard.name} - ${currentCard.memberId}"
                                                )
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Share,
                                                contentDescription = "Share",
                                                tint = UnionGreen,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = {
                                                viewModel.setShowDownloadDialog(true)
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Download,
                                                contentDescription = "Download",
                                                tint = UnionRed,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Interactive Card Container (Touch to customize Logo, Flag, Photo, Aadhaar Auto-Fill)
                                FlippableCardContainer(
                                    card = currentCard,
                                    cardFace = cardFace,
                                    onFlip = { viewModel.flipCardFace() },
                                    onLogoClick = {
                                        logoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onFlagClick = {
                                        flagPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    onPhotoClick = {
                                        viewModel.setShowPhotoSourceDialog(true)
                                    },
                                    onNameOrAddressClick = {
                                        viewModel.openCamera(CameraMode.AADHAAR_SCAN)
                                    },
                                    onSealClick = {
                                        sealPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Detailed Form Inputs with Payment Integration
                        CardFormEditor(
                            card = currentCard,
                            onCardChange = { viewModel.updateCurrentCard(it) },
                            onSave = { viewModel.saveCurrentCard() },
                            onReset = { viewModel.resetToNewCard(currentCard.cardType) },
                            onOpenPayment = { viewModel.setShowPaymentDialog(true) },
                            onOpenAadhaarScan = { viewModel.openCamera(CameraMode.AADHAAR_SCAN) },
                            onOpenPhotoPicker = { viewModel.setShowPhotoSourceDialog(true) },
                            onOpenSettings = { viewModel.setShowSettingsDialog(true) }
                        )

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }

                AppNavTab.PREVIEW -> {
                    CardPreviewScreen(
                        card = currentCard,
                        cardFace = cardFace,
                        onFlip = { viewModel.flipCardFace() },
                        onEdit = { viewModel.setTab(AppNavTab.EDITOR) },
                        onSave = { viewModel.saveCurrentCard() }
                    )
                }

                AppNavTab.DISTRICTS -> {
                    DistrictDirectoryScreen(
                        cards = allCards,
                        onSelectCardForPreview = { viewModel.selectCardForPreview(it) },
                        onSelectCardForEdit = { viewModel.selectCardForEdit(it) }
                    )
                }

                AppNavTab.DIRECTORY -> {
                    CardDirectoryScreen(
                        cards = allCards,
                        searchQuery = searchQuery,
                        onSearchChange = { viewModel.setSearchQuery(it) },
                        selectedFilter = selectedFilter,
                        onFilterChange = { viewModel.setSelectedFilter(it) },
                        onSelectCardForEdit = { viewModel.selectCardForEdit(it) },
                        onSelectCardForPreview = { viewModel.selectCardForPreview(it) },
                        onDeleteCard = { viewModel.deleteCard(it) },
                        onDuplicateCard = { viewModel.duplicateCard(it) },
                        onCreateNewCard = {
                            viewModel.resetToNewCard("MEMBER")
                            viewModel.setTab(AppNavTab.EDITOR)
                        }
                    )
                }

                AppNavTab.SUPER_ADMIN -> {
                    SuperAdminScreen(
                        cards = allCards,
                        onApproveCard = { viewModel.approveCard(it) },
                        onRejectCard = { card, reason -> viewModel.rejectCard(card, reason) },
                        onBack = { viewModel.setTab(AppNavTab.EDITOR) }
                    )
                }
            }
        }
    }

    // Modal Dialogs
    if (showPaymentDialog) {
        PaymentUtrDialog(
            card = currentCard,
            onSubmitUtr = { utr -> viewModel.submitUtrAndRequestApproval(utr) },
            onDismiss = { viewModel.setShowPaymentDialog(false) }
        )
    }

    if (showInfoDialog) {
        AssociationInfoDialog(onDismiss = { viewModel.setShowInfoDialog(false) })
    }

    if (showVerifyDialog) {
        VerificationScannerDialog(
            allCards = allCards,
            onDismiss = { viewModel.setShowVerifyDialog(false) }
        )
    }

    if (showDownloadDialog) {
        CardDownloadExportDialog(
            card = currentCard,
            onDismiss = { viewModel.setShowDownloadDialog(false) }
        )
    }

    if (showPhotoSourceDialog) {
        PhotoSourceSelectionDialog(
            card = currentCard,
            autoCropPhoto = autoCropPhoto,
            photoAspectRatio = photoAspectRatio,
            onPhotoSelected = { uri -> viewModel.updateMemberPhoto(uri) },
            onOpenCamera = { viewModel.openCamera(CameraMode.MEMBER_PHOTO) },
            onOpenSettings = { viewModel.setShowSettingsDialog(true) },
            onDismiss = { viewModel.setShowPhotoSourceDialog(false) }
        )
    }

    if (showAadhaarScannerDialog) {
        AadhaarScannerDialog(
            currentCard = currentCard,
            initialTamilMode = aadhaarAutoTamil,
            onLanguageModeChange = { viewModel.setAadhaarAutoTamil(it) },
            onAadhaarDataExtracted = { updatedCard -> viewModel.applyAadhaarExtractedCard(updatedCard) },
            onOpenSettings = { viewModel.setShowSettingsDialog(true) },
            onDismiss = { viewModel.setShowAadhaarScannerDialog(false) }
        )
    }

    if (showSettingsDialog) {
        AppSettingsDialog(
            autoCropPhoto = autoCropPhoto,
            photoAspectRatio = photoAspectRatio,
            aadhaarAutoTamil = aadhaarAutoTamil,
            onAutoCropPhotoChange = { viewModel.setAutoCropPhoto(it) },
            onPhotoAspectRatioChange = { viewModel.setPhotoAspectRatio(it) },
            onAadhaarAutoTamilChange = { viewModel.setAadhaarAutoTamil(it) },
            onDismiss = { viewModel.setShowSettingsDialog(false) }
        )
    }
}
