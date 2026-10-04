package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity to persist the complete registration and audit history of members.
 * Automatically saves timeline records when members register, update, scan Aadhaar,
 * get approved, or download ID cards.
 */
@Entity(tableName = "member_registration_history")
data class MemberRegistrationHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardId: Long = 0,
    val memberId: String = "",
    val memberName: String = "",
    val district: String = "மதுரை",
    val jobTitle: String = "வண்ணப் பூச்சாளர்",
    val actionType: String = "NEW_REGISTRATION", // "NEW_REGISTRATION", "AADHAAR_AUTO_FILL", "CARD_UPDATED", "PAYMENT_SUBMITTED", "APPROVED", "REJECTED", "DOWNLOADED", "SHARED"
    val actionTitleTamil: String = "புதிய உறுப்பினர் பதிவு",
    val details: String = "",
    val actor: String = "சுய பதிவு",
    val photoUri: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
