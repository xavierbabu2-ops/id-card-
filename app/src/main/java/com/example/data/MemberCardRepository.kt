package com.example.data

import kotlinx.coroutines.flow.Flow

class MemberCardRepository(
    private val dao: MemberCardDao,
    private val historyDao: MemberRegistrationHistoryDao
) {

    val allCards: Flow<List<MemberCardEntity>> = dao.getAllCards()
    val allHistory: Flow<List<MemberRegistrationHistoryEntity>> = historyDao.getAllHistory()

    fun getCardsByType(cardType: String): Flow<List<MemberCardEntity>> = dao.getCardsByType(cardType)

    fun getCardsByDistrict(district: String): Flow<List<MemberCardEntity>> = dao.getCardsByDistrict(district)

    fun getCardsByApprovalStatus(status: String): Flow<List<MemberCardEntity>> = dao.getCardsByApprovalStatus(status)

    fun getCardById(id: Long): Flow<MemberCardEntity?> = dao.getCardById(id)

    fun searchCards(query: String): Flow<List<MemberCardEntity>> = dao.searchCards(query)

    fun getHistoryByDistrict(district: String): Flow<List<MemberRegistrationHistoryEntity>> = historyDao.getHistoryByDistrict(district)

    fun getHistoryByMemberId(memberId: String): Flow<List<MemberRegistrationHistoryEntity>> = historyDao.getHistoryByMemberId(memberId)

    fun searchHistory(query: String): Flow<List<MemberRegistrationHistoryEntity>> = historyDao.searchHistory(query)

    suspend fun logHistory(history: MemberRegistrationHistoryEntity): Long {
        return historyDao.insertHistory(history)
    }

    suspend fun saveCard(
        card: MemberCardEntity,
        actionType: String = if (card.id == 0L) "NEW_REGISTRATION" else "CARD_UPDATED",
        actionTitleTamil: String = if (card.id == 0L) "புதிய உறுப்பினர் பதிவு" else "விவரங்கள் திருத்தப்பட்டது",
        details: String = "${card.name} (${card.memberId}) - மாவட்டம்: ${card.district}, தொழில்: ${card.jobTitle}",
        actor: String = "சுய பதிவு"
    ): Long {
        val savedId = if (card.id == 0L) {
            dao.insertCard(card)
        } else {
            dao.updateCard(card)
            card.id
        }

        // Automatically log registration history
        historyDao.insertHistory(
            MemberRegistrationHistoryEntity(
                cardId = savedId,
                memberId = card.memberId,
                memberName = card.name,
                district = card.district,
                jobTitle = card.jobTitle,
                actionType = actionType,
                actionTitleTamil = actionTitleTamil,
                details = details,
                actor = actor,
                photoUri = card.photoUri,
                timestamp = System.currentTimeMillis()
            )
        )

        return savedId
    }

    suspend fun updateApproval(
        id: Long,
        status: String,
        approvedBy: String,
        reason: String? = null,
        memberCard: MemberCardEntity? = null
    ) {
        val now = System.currentTimeMillis()
        dao.updateApprovalStatus(
            id = id,
            status = status,
            approvedBy = approvedBy,
            approvedAt = now,
            reason = reason
        )

        val cardName = memberCard?.name ?: "உறுப்பினர் #$id"
        val cardMemberId = memberCard?.memberId ?: "TN-ID-$id"
        val cardDistrict = memberCard?.district ?: "தமிழ்நாடு"
        val isApproved = status == "APPROVED"

        historyDao.insertHistory(
            MemberRegistrationHistoryEntity(
                cardId = id,
                memberId = cardMemberId,
                memberName = cardName,
                district = cardDistrict,
                actionType = if (isApproved) "APPROVED" else "REJECTED",
                actionTitleTamil = if (isApproved) "அடையாள அட்டை அங்கீகரிக்கப்பட்டது" else "விண்ணப்பம் நிராகரிக்கப்பட்டது",
                details = if (isApproved) "சூப்பர் அட்மின் ஒப்புதல் அளித்தார். உறுப்பினர் எண்: $cardMemberId" else "காரணம்: ${reason ?: "தகவல் போதாது"}",
                actor = approvedBy,
                photoUri = memberCard?.photoUri,
                timestamp = now
            )
        )
    }

    suspend fun deleteCard(card: MemberCardEntity) {
        dao.deleteCard(card)
        historyDao.insertHistory(
            MemberRegistrationHistoryEntity(
                cardId = card.id,
                memberId = card.memberId,
                memberName = card.name,
                district = card.district,
                actionType = "DELETED",
                actionTitleTamil = "உறுப்பினர் அட்டை நீக்கப்பட்டது",
                details = "${card.name} (${card.memberId}) அட்டை தரவுத்தளத்திலிருந்து நீக்கப்பட்டது.",
                actor = "Super Admin",
                timestamp = System.currentTimeMillis()
            )
        )
    }

    suspend fun deleteCardById(id: Long) {
        dao.deleteCardById(id)
    }

    suspend fun deleteHistoryById(id: Long) {
        historyDao.deleteHistoryById(id)
    }

    suspend fun clearAllHistory() {
        historyDao.clearAllHistory()
    }

    suspend fun checkAndSeedInitialData() {
        if (dao.getCount() == 0) {
            val initialCards = listOf(
                MemberCardEntity(
                    cardType = "MEMBER",
                    memberId = "TN-MDU-0001",
                    name = "மு. கார்த்திகேயன்",
                    jobTitle = "வண்ணப் பூச்சாளர் (Wall Painter)",
                    fatherName = "முத்துசாமி",
                    age = "34",
                    bloodGroup = "O +ve",
                    address = "1/14 அம்பலக்காரன்பட்டி, உத்தங்குடி போஸ்ட் அவுட் மதுரை 625107",
                    district = "மதுரை",
                    phone = "9876543210",
                    emergencyPhone = "9443100000",
                    avatarPreset = 1,
                    themeColorHex = "#D3121B",
                    qrVerificationCode = "TNPA2-MEM-MDU-0001-KARTHIKEYAN",
                    approvalStatus = "APPROVED",
                    utrNumber = "428910839211",
                    approvedBy = "Super Admin"
                ),
                MemberCardEntity(
                    cardType = "EXECUTIVE",
                    memberId = "TN-EXEC-MDU-0001",
                    name = "S. மைக்கேல் ஆல்வின்",
                    jobTitle = "தலைமை ஓவியர் & ஆலோசகர்",
                    fatherName = "சவரிமுத்து",
                    age = "46",
                    bloodGroup = "B +ve",
                    address = "மெயின் ரோடு, கே.கே. நகர், மதுரை",
                    district = "மதுரை",
                    phone = "9443312345",
                    emergencyPhone = "9443399999",
                    designation = "மாநிலத் தலைவர் (State President)",
                    avatarPreset = 2,
                    themeColorHex = "#D3121B",
                    qrVerificationCode = "TNPA2-EXEC-MDU-0001-MICHAEL-ALVIN",
                    approvalStatus = "APPROVED",
                    utrNumber = "OFFICIAL-APPOINTMENT",
                    approvedBy = "Super Admin"
                ),
                MemberCardEntity(
                    cardType = "CONTRACTOR",
                    memberId = "TN-CON-DGL-0001",
                    name = "ஆர். சண்முகம்",
                    jobTitle = "பெயிண்டிங் ஒப்பந்ததாரர்",
                    fatherName = "ராமலிங்கம்",
                    age = "42",
                    bloodGroup = "A +ve",
                    address = "45, காமராஜர் சாலை, திண்டுக்கல்",
                    district = "திண்டுக்கல்",
                    phone = "9876543210",
                    emergencyPhone = "9842155555",
                    firmName = "ஸ்ரீ முருகன் பெயிண்டர்ஸ் & டெக்கரேட்டர்ஸ்",
                    authorizedDate = "01-04-2024",
                    validUntil = "31-03-2027",
                    avatarPreset = 3,
                    themeColorHex = "#B45309",
                    qrVerificationCode = "TNPA2-CON-DGL-0001-SHANMUGAM",
                    approvalStatus = "APPROVED",
                    utrNumber = "429012384912",
                    approvedBy = "Super Admin"
                ),
                MemberCardEntity(
                    cardType = "MEMBER",
                    memberId = "TN-CBE-0001",
                    name = "வே. சுப்பிரமணி",
                    jobTitle = "கலை ஓவியர் (Artist & Muralist)",
                    fatherName = "வேலு",
                    age = "29",
                    bloodGroup = "B +ve",
                    address = "12, அண்ணா நகர், கோயம்புத்தூர்",
                    district = "கோயம்புத்தூர்",
                    phone = "9786512345",
                    emergencyPhone = "9786599999",
                    avatarPreset = 4,
                    themeColorHex = "#D3121B",
                    qrVerificationCode = "TNPA2-MEM-CBE-0001-SUBRAMANI",
                    approvalStatus = "APPROVED",
                    utrNumber = "430192837465",
                    approvedBy = "Super Admin"
                ),
                MemberCardEntity(
                    cardType = "MEMBER",
                    memberId = "TN-CHN-0001",
                    name = "க. மாரிமுத்து",
                    jobTitle = "ஸ்ப்ரே பெயிண்டர் (Spray Painter)",
                    fatherName = "கந்தசாமி",
                    age = "38",
                    bloodGroup = "AB +ve",
                    address = "8, பாரதி தெரு, தாம்பரம், சென்னை",
                    district = "சென்னை",
                    phone = "9840198765",
                    emergencyPhone = "9840100000",
                    avatarPreset = 5,
                    themeColorHex = "#D3121B",
                    qrVerificationCode = "TNPA2-MEM-CHN-0001-MARIMUTHU",
                    approvalStatus = "PENDING",
                    utrNumber = "431289012345",
                    paymentAmount = 100.0,
                    approvedBy = null
                )
            )
            dao.insertAll(initialCards)

            // Seed initial member registration history
            val now = System.currentTimeMillis()
            val dayMs = 86400000L
            val initialHistories = listOf(
                MemberRegistrationHistoryEntity(
                    memberId = "TN-MDU-0001",
                    memberName = "மு. கார்த்திகேயன்",
                    district = "மதுரை",
                    jobTitle = "வண்ணப் பூச்சாளர்",
                    actionType = "NEW_REGISTRATION",
                    actionTitleTamil = "புதிய உறுப்பினர் நேரடி பதிவு",
                    details = "மதுரை மாவட்ட முதல் உறுப்பினர் எண் TN-MDU-0001 பதிவு செய்யப்பட்டு ஒப்புதல் அளிக்கப்பட்டது.",
                    actor = "ஆதார் ஸ்கேனர் & சுய பதிவு",
                    timestamp = now - (3 * dayMs)
                ),
                MemberRegistrationHistoryEntity(
                    memberId = "TN-EXEC-MDU-0001",
                    memberName = "S. மைक्केல் ஆல்வின்",
                    district = "மதுரை",
                    jobTitle = "தலைமை ஓவியர்",
                    actionType = "NEW_REGISTRATION",
                    actionTitleTamil = "நிர்வாகி அடையாள அட்டை பதிவு",
                    details = "மாநிலத் தலைவர் பதவிக்கு நிர்வாக அடையாள அட்டை பதிவு செய்யப்பட்டது.",
                    actor = "மாநில நிர்வாகக் குழு",
                    timestamp = now - (2 * dayMs)
                ),
                MemberRegistrationHistoryEntity(
                    memberId = "TN-CON-DGL-0001",
                    memberName = "ஆர். சண்முகம்",
                    district = "திண்டுக்கல்",
                    jobTitle = "பெயிண்டிங் ஒப்பந்ததாரர்",
                    actionType = "NEW_REGISTRATION",
                    actionTitleTamil = "ஒப்பந்ததாரர் அங்கீகார அட்டை பதிவு",
                    details = "திண்டுக்கல் மாவட்ட ஒப்பந்ததாரர் அட்டை TN-CON-DGL-0001 பதிவு செய்யப்பட்டது.",
                    actor = "சுய பதிவு",
                    timestamp = now - (1 * dayMs)
                ),
                MemberRegistrationHistoryEntity(
                    memberId = "TN-CBE-0001",
                    memberName = "வே. சுப்பிரமணி",
                    district = "கோயம்புத்தூர்",
                    jobTitle = "கலை ஓவியர்",
                    actionType = "NEW_REGISTRATION",
                    actionTitleTamil = "புதிய உறுப்பினர் பதிவு",
                    details = "கோயம்புத்தூர் மாவட்ட அட்டை TN-CBE-0001 பதிவு செய்யப்பட்டு கட்டணம் செலுத்தப்பட்டது.",
                    actor = "சுய பதிவு",
                    timestamp = now - (12 * 3600000L)
                ),
                MemberRegistrationHistoryEntity(
                    memberId = "TN-CHN-0001",
                    memberName = "க. மாரிமுத்து",
                    district = "சென்னை",
                    jobTitle = "ஸ்ப்ரே பெயிண்டர்",
                    actionType = "AADHAAR_AUTO_FILL",
                    actionTitleTamil = "ஆதார் நேரடி ஸ்கேன் பதிவு",
                    details = "சென்னை மாவட்ட ஆதார் கார்டு ஸ்கேன் செய்யப்பட்டு TN-CHN-0001 எண் ஒதுக்கப்பட்டது. ஒப்புதலுக்கு காத்திருக்கிறது.",
                    actor = "ஆதார் ஸ்கேனர்",
                    timestamp = now - (2 * 3600000L)
                )
            )
            historyDao.insertAllHistory(initialHistories)
        }
    }
}
