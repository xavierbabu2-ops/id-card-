package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MemberCardEntity
import com.example.data.MemberCardRepository
import com.example.ui.components.CardFace
import com.example.util.DistrictCodeHelper
import com.example.util.TamilAadhaarTransliterationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppNavTab {
    EDITOR,
    PREVIEW,
    DIRECTORY,
    DISTRICTS,
    SUPER_ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemberCardRepository
    val allCards: StateFlow<List<MemberCardEntity>>

    private val _currentCard = MutableStateFlow(
        MemberCardEntity(
            cardType = "MEMBER",
            memberId = "TN-MDU-0001",
            name = "மு. கார்த்திகேயன்",
            jobTitle = "வண்ணப் பூச்சாளர் (Wall Painter)",
            fatherName = "முத்துசாமி",
            age = "34",
            bloodGroup = "O +ve",
            address = "1/14 அம்பலக்காரன் பட்டி, ஒத்தங்குடி, மதுரை",
            district = "மதுரை",
            phone = "9876543210",
            emergencyPhone = "9443100000",
            avatarPreset = 1,
            themeColorHex = "#D3121B",
            approvalStatus = "APPROVED",
            utrNumber = "428910839211"
        )
    )
    val currentCard: StateFlow<MemberCardEntity> = _currentCard.asStateFlow()

    private val _cardFace = MutableStateFlow(CardFace.Front)
    val cardFace: StateFlow<CardFace> = _cardFace.asStateFlow()

    private val _currentTab = MutableStateFlow(AppNavTab.PREVIEW)
    val currentTab: StateFlow<AppNavTab> = _currentTab.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow("ALL")
    val selectedFilter: StateFlow<String> = _selectedFilter.asStateFlow()

    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: StateFlow<Boolean> = _showInfoDialog.asStateFlow()

    private val _showVerifyDialog = MutableStateFlow(false)
    val showVerifyDialog: StateFlow<Boolean> = _showVerifyDialog.asStateFlow()

    private val _showPaymentDialog = MutableStateFlow(false)
    val showPaymentDialog: StateFlow<Boolean> = _showPaymentDialog.asStateFlow()

    private val _showDownloadDialog = MutableStateFlow(false)
    val showDownloadDialog: StateFlow<Boolean> = _showDownloadDialog.asStateFlow()

    private val _showPhotoSourceDialog = MutableStateFlow(false)
    val showPhotoSourceDialog: StateFlow<Boolean> = _showPhotoSourceDialog.asStateFlow()

    private val _showAadhaarScannerDialog = MutableStateFlow(false)
    val showAadhaarScannerDialog: StateFlow<Boolean> = _showAadhaarScannerDialog.asStateFlow()

    private val prefs = application.getSharedPreferences("app_settings_tnpa", Context.MODE_PRIVATE)

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _autoCropPhoto = MutableStateFlow(prefs.getBoolean("auto_crop_photo", true))
    val autoCropPhoto: StateFlow<Boolean> = _autoCropPhoto.asStateFlow()

    private val _photoAspectRatio = MutableStateFlow(prefs.getString("photo_aspect_ratio", "3:4") ?: "3:4")
    val photoAspectRatio: StateFlow<String> = _photoAspectRatio.asStateFlow()

    private val _aadhaarAutoTamil = MutableStateFlow(prefs.getBoolean("aadhaar_auto_tamil", true))
    val aadhaarAutoTamil: StateFlow<Boolean> = _aadhaarAutoTamil.asStateFlow()

    private val _showCameraScreen = MutableStateFlow(false)
    val showCameraScreen: StateFlow<Boolean> = _showCameraScreen.asStateFlow()

    private val _cameraScreenMode = MutableStateFlow(com.example.ui.screens.CameraMode.MEMBER_PHOTO)
    val cameraScreenMode: StateFlow<com.example.ui.screens.CameraMode> = _cameraScreenMode.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    init {
        val db = AppDatabase.getDatabase(application)
        repository = MemberCardRepository(db.memberCardDao())

        allCards = repository.allCards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    fun updateCurrentCard(card: MemberCardEntity) {
        _currentCard.value = card
    }

    fun updateCustomLogo(uri: String?) {
        _currentCard.value = _currentCard.value.copy(customLogoUri = uri)
        _snackbarMessage.value = "சங்க லோகோ மாற்றப்பட்டது! (Logo Updated)"
    }

    fun updateCustomFlag(uri: String?) {
        _currentCard.value = _currentCard.value.copy(customFlagUri = uri)
        _snackbarMessage.value = "சங்கக் கொடி படம் மாற்றப்பட்டது! (Flag Updated)"
    }

    fun updateCustomSeal(uri: String?) {
        _currentCard.value = _currentCard.value.copy(customSealUri = uri)
        _snackbarMessage.value = "தமிழ்நாடு அரசு அங்கீகார முத்திரை மாற்றப்பட்டது! (Govt Seal Updated)"
    }

    fun resetCustomSeal() {
        _currentCard.value = _currentCard.value.copy(customSealUri = null)
        _snackbarMessage.value = "அரசு அங்கீகார முத்திரை இயல்புநிலைக்கு மாற்றப்பட்டது!"
    }

    fun updateMemberPhoto(uri: String?) {
        _currentCard.value = _currentCard.value.copy(photoUri = uri)
        _snackbarMessage.value = "உறுப்பினர் புகைப்படம் மாற்றப்பட்டது! (Photo Updated)"
    }

    fun updateAvatarPreset(preset: Int) {
        _currentCard.value = _currentCard.value.copy(avatarPreset = preset, photoUri = null)
        _snackbarMessage.value = "முன்மாதிரி சின்னம் தேர்வு செய்யப்பட்டது!"
    }

    fun applyAadhaarExtractedCard(updatedCard: MemberCardEntity) {
        val finalCard = if (_aadhaarAutoTamil.value) {
            updatedCard.copy(
                name = TamilAadhaarTransliterationHelper.transliterateNameToTamil(updatedCard.name),
                fatherName = TamilAadhaarTransliterationHelper.transliterateNameToTamil(updatedCard.fatherName),
                district = TamilAadhaarTransliterationHelper.translateDistrictToTamil(updatedCard.district),
                address = TamilAadhaarTransliterationHelper.convertAddressToTamil(updatedCard.address)
            )
        } else {
            updatedCard
        }
        _currentCard.value = finalCard
        _snackbarMessage.value = "ஆதார் விபரம் & புதிய அடையாள எண் ${finalCard.memberId} தானாக பூர்த்தி செய்யப்பட்டது!"
    }

    fun setCardFace(face: CardFace) {
        _cardFace.value = face
    }

    fun flipCardFace() {
        _cardFace.value = if (_cardFace.value == CardFace.Front) CardFace.Back else CardFace.Front
    }

    fun setTab(tab: AppNavTab) {
        _currentTab.value = tab
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSelectedFilter(filter: String) {
        _selectedFilter.value = filter
    }

    fun setShowInfoDialog(show: Boolean) {
        _showInfoDialog.value = show
    }

    fun setShowVerifyDialog(show: Boolean) {
        _showVerifyDialog.value = show
    }

    fun setShowPaymentDialog(show: Boolean) {
        _showPaymentDialog.value = show
    }

    fun setShowDownloadDialog(show: Boolean) {
        _showDownloadDialog.value = show
    }

    fun setShowPhotoSourceDialog(show: Boolean) {
        _showPhotoSourceDialog.value = show
    }

    fun setShowAadhaarScannerDialog(show: Boolean) {
        _showAadhaarScannerDialog.value = show
    }

    fun openCamera(mode: com.example.ui.screens.CameraMode) {
        _cameraScreenMode.value = mode
        _showCameraScreen.value = true
    }

    fun closeCamera() {
        _showCameraScreen.value = false
    }

    fun setShowSettingsDialog(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun setAutoCropPhoto(enabled: Boolean) {
        _autoCropPhoto.value = enabled
        prefs.edit().putBoolean("auto_crop_photo", enabled).apply()
        _snackbarMessage.value = if (enabled) "புகைப்படம் மட்டும் தானாக செதுக்குதல் இயக்கப்பட்டது" else "புகைப்பட செதுக்குதல் முடக்கப்பட்டது"
    }

    fun setPhotoAspectRatio(ratio: String) {
        _photoAspectRatio.value = ratio
        prefs.edit().putString("photo_aspect_ratio", ratio).apply()
    }

    fun setAadhaarAutoTamil(enabled: Boolean) {
        _aadhaarAutoTamil.value = enabled
        prefs.edit().putBoolean("aadhaar_auto_tamil", enabled).apply()
        _snackbarMessage.value = if (enabled) "ஆதார் ஸ்கேன் விவரங்கள் தமிழில் நிரப்பப்படும் (Tamil Auto-Fill ON)" else "ஆதார் ஸ்கேன் விவரங்கள் ஆங்கிலத்தில் நிரப்பப்படும் (English Fill)"
    }

    fun clearSnackbarMessage() {
        _snackbarMessage.value = null
    }

    fun saveCurrentCard() {
        viewModelScope.launch {
            val savedId = repository.saveCard(_currentCard.value)
            _currentCard.value = _currentCard.value.copy(id = savedId)
            _snackbarMessage.value = "அடையாள அட்டை வெற்றிகரமாக சேமிக்கப்பட்டது! (Card Saved)"
        }
    }

    fun submitUtrAndRequestApproval(utrNumber: String) {
        viewModelScope.launch {
            val updated = _currentCard.value.copy(
                approvalStatus = "PENDING",
                utrNumber = utrNumber,
                paymentAmount = 100.0,
                paymentDate = System.currentTimeMillis()
            )
            val savedId = repository.saveCard(updated)
            _currentCard.value = updated.copy(id = savedId)
            _showPaymentDialog.value = false
            _snackbarMessage.value = "UTR சமர்ப்பிக்கப்பட்டது! சூப்பர் அட்மின் ஒப்புதலுக்கு அனுப்பப்பட்டுள்ளது."
        }
    }

    fun approveCard(card: MemberCardEntity) {
        viewModelScope.launch {
            repository.updateApproval(
                id = card.id,
                status = "APPROVED",
                approvedBy = "Super Admin"
            )
            if (_currentCard.value.id == card.id) {
                _currentCard.value = _currentCard.value.copy(
                    approvalStatus = "APPROVED",
                    approvedBy = "Super Admin"
                )
            }
            _snackbarMessage.value = "${card.name} அவர்களின் அட்டை வெற்றிகரமாக அப்ரூவல் செய்யப்பட்டது! ✅"
        }
    }

    fun rejectCard(card: MemberCardEntity, reason: String) {
        viewModelScope.launch {
            repository.updateApproval(
                id = card.id,
                status = "REJECTED",
                approvedBy = "Super Admin",
                reason = reason
            )
            if (_currentCard.value.id == card.id) {
                _currentCard.value = _currentCard.value.copy(
                    approvalStatus = "REJECTED",
                    rejectionReason = reason
                )
            }
            _snackbarMessage.value = "${card.name} அவர்களின் அட்டை நிராகரிக்கப்பட்டது."
        }
    }

    fun resetToNewCard(cardType: String = "MEMBER") {
        val newId = DistrictCodeHelper.getNextMemberIdForDistrict("மதுரை", cardType, allCards.value)

        _currentCard.value = MemberCardEntity(
            id = 0,
            cardType = cardType,
            memberId = newId,
            name = "",
            jobTitle = if (cardType == "CONTRACTOR") "பெயிண்டிங் ஒப்பந்ததாரர்" else "வண்ணப் பூச்சாளர்",
            fatherName = "",
            age = "",
            bloodGroup = "O +ve",
            address = "மதுரை",
            district = "மதுரை",
            phone = "",
            emergencyPhone = "",
            firmName = "",
            designation = "மாநிலத் தலைவர்",
            avatarPreset = (1..5).random(),
            themeColorHex = if (cardType == "CONTRACTOR") "#B45309" else "#D3121B",
            approvalStatus = "PENDING",
            utrNumber = ""
        )
        _cardFace.value = CardFace.Front
    }

    fun selectCardForEdit(card: MemberCardEntity) {
        _currentCard.value = card
        _cardFace.value = CardFace.Front
        _currentTab.value = AppNavTab.EDITOR
    }

    fun selectCardForPreview(card: MemberCardEntity) {
        _currentCard.value = card
        _cardFace.value = CardFace.Front
        _currentTab.value = AppNavTab.PREVIEW
    }

    fun deleteCard(card: MemberCardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
            _snackbarMessage.value = "அட்டை நீக்கப்பட்டது (Card Deleted)"
        }
    }

    fun duplicateCard(card: MemberCardEntity) {
        viewModelScope.launch {
            val copy = card.copy(
                id = 0,
                memberId = DistrictCodeHelper.getNextMemberIdForDistrict(card.district, card.cardType, allCards.value),
                name = card.name + " (நகல்)",
                createdAt = System.currentTimeMillis()
            )
            repository.saveCard(copy)
            _snackbarMessage.value = "அட்டை நகலெடுக்கப்பட்டது (Card Duplicated)"
        }
    }
}
