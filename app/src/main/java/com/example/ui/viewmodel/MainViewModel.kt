package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.MemberCardEntity
import com.example.data.MemberCardRepository
import com.example.data.MemberRegistrationHistoryEntity
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
    HISTORY,
    SUPER_ADMIN
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MemberCardRepository
    val allCards: StateFlow<List<MemberCardEntity>>
    val allHistory: StateFlow<List<MemberRegistrationHistoryEntity>>

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
        repository = MemberCardRepository(db.memberCardDao(), db.memberHistoryDao())

        allCards = repository.allCards.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        allHistory = repository.allHistory.stateIn(
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
        val district = if (_aadhaarAutoTamil.value) {
            TamilAadhaarTransliterationHelper.translateDistrictToTamil(updatedCard.district)
        } else {
            updatedCard.district
        }
        val nextMemberId = DistrictCodeHelper.getNextMemberIdForDistrict(district, updatedCard.cardType, allCards.value)

        val finalCard = if (_aadhaarAutoTamil.value) {
            updatedCard.copy(
                memberId = nextMemberId,
                name = TamilAadhaarTransliterationHelper.transliterateNameToTamil(updatedCard.name),
                fatherName = TamilAadhaarTransliterationHelper.transliterateNameToTamil(updatedCard.fatherName),
                district = district,
                address = TamilAadhaarTransliterationHelper.convertAddressToTamil(updatedCard.address)
            )
        } else {
            updatedCard.copy(
                memberId = nextMemberId,
                district = district
            )
        }
        _currentCard.value = finalCard

        // Automatically save and record history
        viewModelScope.launch {
            val savedId = repository.saveCard(
                card = finalCard,
                actionType = "AADHAAR_AUTO_FILL",
                actionTitleTamil = "ஆதார் நேரடி ஸ்கேன் பதிவு",
                details = "${finalCard.name} (${finalCard.memberId}) ஆதார் மூலம் ஸ்கேன் செய்யப்பட்டு ${finalCard.district} மாவட்டத்தில் தானாக சேமிக்கப்பட்டது.",
                actor = "ஆதார் ஸ்கேனர்"
            )
            _currentCard.value = finalCard.copy(id = savedId)
        }

        _snackbarMessage.value = "ஆதார் விவரங்கள் $district மாவட்டத்தில் தானாக சேமிக்கப்பட்டது! புதிய அடையாள எண்: $nextMemberId"
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
            val cardToSave = _currentCard.value
            val isNew = cardToSave.id == 0L
            val actionType = if (isNew) "NEW_REGISTRATION" else "CARD_UPDATED"
            val actionTitle = if (isNew) "புதிய உறுப்பினர் பதிவு" else "விவரங்கள் திருத்தப்பட்டது"

            val savedId = repository.saveCard(
                card = cardToSave,
                actionType = actionType,
                actionTitleTamil = actionTitle,
                details = "${cardToSave.name} (${cardToSave.memberId}) - ${cardToSave.district} மாவட்டம் வாரியாக சேமிக்கப்பட்டது.",
                actor = "சுய பதிவு"
            )
            _currentCard.value = cardToSave.copy(id = savedId)

            if (cardToSave.approvalStatus != "APPROVED" && cardToSave.utrNumber.isBlank()) {
                _snackbarMessage.value = "அட்டை சேமிக்கப்பட்டது! சூப்பர் அட்மின் ஒப்புதலுக்காக 7010131915 எண்ணிற்கு ₹100 செலுத்தி UTR உள்ளிடவும்."
                _showPaymentDialog.value = true
            } else {
                _snackbarMessage.value = "அடையாள அட்டை ${cardToSave.district} மாவட்டம் வாரியாக சேமிக்கப்பட்டது! வரலாறு பதிவு செய்யப்பட்டது."
            }
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
            val savedId = repository.saveCard(
                card = updated,
                actionType = "PAYMENT_SUBMITTED",
                actionTitleTamil = "கட்டணம் & UTR சமர்ப்பிக்கப்பட்டது",
                details = "UTR எண்: $utrNumber சமர்ப்பிக்கப்பட்டு சூப்பர் அட்மின் ஒப்புதலுக்கு அனுப்பப்பட்டது.",
                actor = "சுய பதிவு"
            )
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
                approvedBy = "Super Admin",
                memberCard = card
            )
            if (_currentCard.value.id == card.id || _currentCard.value.memberId == card.memberId) {
                _currentCard.value = _currentCard.value.copy(
                    approvalStatus = "APPROVED",
                    approvedBy = "Super Admin"
                )
            }
            _snackbarMessage.value = "${card.name} அவர்களின் அட்டை அங்கீகரிக்கப்பட்டது! வரலாறு புதுப்பிக்கப்பட்டது. ✅"
        }
    }

    fun approveAllPendingCards() {
        viewModelScope.launch {
            val pendingList = allCards.value.filter { it.approvalStatus == "PENDING" }
            for (card in pendingList) {
                repository.updateApproval(
                    id = card.id,
                    status = "APPROVED",
                    approvedBy = "Super Admin",
                    memberCard = card
                )
            }
            if (_currentCard.value.approvalStatus == "PENDING") {
                _currentCard.value = _currentCard.value.copy(
                    approvalStatus = "APPROVED",
                    approvedBy = "Super Admin"
                )
            }
            _snackbarMessage.value = "${pendingList.size} நிலுவை அட்டைகளும் சூப்பர் அட்மின் மூலம் அங்கீகரிக்கப்பட்டன! ✅"
        }
    }

    fun rejectCard(card: MemberCardEntity, reason: String) {
        viewModelScope.launch {
            repository.updateApproval(
                id = card.id,
                status = "REJECTED",
                approvedBy = "Super Admin",
                reason = reason,
                memberCard = card
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

    fun recordCardDownloaded(card: MemberCardEntity) {
        viewModelScope.launch {
            repository.logHistory(
                MemberRegistrationHistoryEntity(
                    cardId = card.id,
                    memberId = card.memberId,
                    memberName = card.name,
                    district = card.district,
                    jobTitle = card.jobTitle,
                    actionType = "DOWNLOADED",
                    actionTitleTamil = "அடையாள அட்டை பதிவிறக்கம் செய்யப்பட்டது",
                    details = "${card.name} (${card.memberId}) அட்டை உயர் தெளிவுத்திறனில் பதிவிறக்கம்/பகிரப்பட்டது.",
                    actor = "பயனர்",
                    photoUri = card.photoUri,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearAllHistory()
            _snackbarMessage.value = "பதிவு வரலாறு வெற்றிகரமாக அழிக்கப்பட்டது."
        }
    }

    fun createNewCardForDistrict(districtName: String) {
        val nextId = DistrictCodeHelper.getNextMemberIdForDistrict(districtName, "MEMBER", allCards.value)
        _currentCard.value = MemberCardEntity(
            id = 0,
            cardType = "MEMBER",
            memberId = nextId,
            name = "",
            jobTitle = "வண்ணப் பூச்சாளர்",
            fatherName = "",
            age = "",
            bloodGroup = "O +ve",
            address = "$districtName மாவட்டம்",
            district = districtName,
            phone = "",
            emergencyPhone = "",
            avatarPreset = 1,
            approvalStatus = "PENDING",
            utrNumber = ""
        )
        _currentTab.value = AppNavTab.EDITOR
        _snackbarMessage.value = "$districtName மாவட்டத்திற்கான புதிய அட்டை தயார் செய்யப்படுகிறது! ஒதுக்கப்பட்ட எண்: $nextId"
    }

    fun resetToNewCard(cardType: String = "MEMBER") {
        val district = _currentCard.value.district.ifBlank { "மதுரை" }
        val newId = DistrictCodeHelper.getNextMemberIdForDistrict(district, cardType, allCards.value)

        _currentCard.value = when (cardType) {
            "EXECUTIVE" -> MemberCardEntity(
                cardType = "EXECUTIVE",
                memberId = newId,
                name = "",
                jobTitle = "தலைமை நிர்வாகி",
                fatherName = "",
                age = "",
                bloodGroup = "O +ve",
                address = "",
                district = district,
                phone = "",
                designation = "மாவட்ட செயலாளர்",
                themeColorHex = "#D3121B",
                approvalStatus = "PENDING",
                utrNumber = ""
            )
            "CONTRACTOR" -> MemberCardEntity(
                cardType = "CONTRACTOR",
                memberId = newId,
                name = "",
                jobTitle = "பெயிண்டிங் ஒப்பந்ததாரர்",
                fatherName = "",
                age = "",
                bloodGroup = "O +ve",
                address = "",
                district = district,
                phone = "",
                firmName = "",
                themeColorHex = "#B45309",
                approvalStatus = "PENDING",
                utrNumber = ""
            )
            else -> MemberCardEntity(
                cardType = "MEMBER",
                memberId = newId,
                name = "",
                jobTitle = "வண்ணப் பூச்சாளர்",
                fatherName = "",
                age = "",
                bloodGroup = "O +ve",
                address = "",
                district = district,
                phone = "",
                approvalStatus = "PENDING",
                utrNumber = ""
            )
        }
        _snackbarMessage.value = "புதிய அட்டை படிவம் திறக்கப்பட்டது ($district - $newId)"
    }

    fun selectCardForEdit(card: MemberCardEntity) {
        _currentCard.value = card
        _currentTab.value = AppNavTab.EDITOR
        _snackbarMessage.value = "${card.name} அட்டை திருத்துவதற்கு தேர்ந்தெடுக்கப்பட்டது."
    }

    fun selectCardForPreview(card: MemberCardEntity) {
        _currentCard.value = card
        _currentTab.value = AppNavTab.PREVIEW
    }

    fun duplicateCard(card: MemberCardEntity) {
        viewModelScope.launch {
            val nextId = DistrictCodeHelper.getNextMemberIdForDistrict(card.district, card.cardType, allCards.value)
            val newCard = card.copy(
                id = 0,
                memberId = nextId,
                name = "${card.name} (நகல் / Copy)",
                createdAt = System.currentTimeMillis()
            )
            val savedId = repository.saveCard(
                card = newCard,
                actionType = "NEW_REGISTRATION",
                actionTitleTamil = "நகல் அட்டை உருவாக்கப்பட்டது",
                details = "${card.name} அவர்களின் அட்டை நகலெடுக்கப்பட்டு $nextId எண் உருவாக்கப்பட்டது."
            )
            _currentCard.value = newCard.copy(id = savedId)
            _snackbarMessage.value = "அட்டை வெற்றிகரமாக நகலெடுக்கப்பட்டது! புதிய எண்: $nextId"
        }
    }

    fun deleteCard(card: MemberCardEntity) {
        viewModelScope.launch {
            repository.deleteCard(card)
            _snackbarMessage.value = "${card.name} அவர்களின் அட்டை நீக்கப்பட்டது."
            if (_currentCard.value.id == card.id) {
                resetToNewCard()
            }
        }
    }
}
