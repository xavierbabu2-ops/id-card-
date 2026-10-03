package com.example.data

import kotlinx.coroutines.flow.Flow

class MemberCardRepository(private val dao: MemberCardDao) {

    val allCards: Flow<List<MemberCardEntity>> = dao.getAllCards()

    fun getCardsByType(cardType: String): Flow<List<MemberCardEntity>> = dao.getCardsByType(cardType)

    fun getCardsByDistrict(district: String): Flow<List<MemberCardEntity>> = dao.getCardsByDistrict(district)

    fun getCardsByApprovalStatus(status: String): Flow<List<MemberCardEntity>> = dao.getCardsByApprovalStatus(status)

    fun getCardById(id: Long): Flow<MemberCardEntity?> = dao.getCardById(id)

    fun searchCards(query: String): Flow<List<MemberCardEntity>> = dao.searchCards(query)

    suspend fun saveCard(card: MemberCardEntity): Long {
        return if (card.id == 0L) {
            dao.insertCard(card)
        } else {
            dao.updateCard(card)
            card.id
        }
    }

    suspend fun updateApproval(id: Long, status: String, approvedBy: String, reason: String? = null) {
        dao.updateApprovalStatus(
            id = id,
            status = status,
            approvedBy = approvedBy,
            approvedAt = System.currentTimeMillis(),
            reason = reason
        )
    }

    suspend fun deleteCard(card: MemberCardEntity) {
        dao.deleteCard(card)
    }

    suspend fun deleteCardById(id: Long) {
        dao.deleteCardById(id)
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
                    address = "1/14 அம்பலக்காரன் பட்டி, ஒத்தங்குடி, மதுரை",
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
        }
    }
}
