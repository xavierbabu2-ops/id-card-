package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "member_cards")
data class MemberCardEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val cardType: String = "MEMBER", // "MEMBER", "EXECUTIVE", "CONTRACTOR"
    val memberId: String = "TN-MDU-1024",
    val name: String = "மு. கார்த்திகேயன்",
    val jobTitle: String = "வண்ணப் பூச்சாளர்",
    val fatherName: String = "முத்துசாமி",
    val age: String = "34",
    val bloodGroup: String = "O +ve",
    val address: String = "1/14 அம்பலக்காரன் பட்டி, ஒத்தங்குடி, மதுரை",
    val district: String = "மதுரை",
    val phone: String = "9876543210",
    val emergencyPhone: String = "9443100000",
    val firmName: String = "ஸ்ரீ முருகன் பெயிண்டர்ஸ்",
    val designation: String = "மாநிலத் தலைவர்",
    val authorizedDate: String = "01-04-2024",
    val validUntil: String = "31-03-2027",
    val photoUri: String? = null,
    val customLogoUri: String? = null,
    val customFlagUri: String? = null,
    val aadhaarNumber: String? = null,
    val avatarPreset: Int = 1,
    val themeColorHex: String = "#D3121B",
    val qrVerificationCode: String = "TNPA2-502600044-VERIFIED",
    val approvalStatus: String = "APPROVED", // "PENDING", "APPROVED", "REJECTED"
    val utrNumber: String = "",
    val paymentAmount: Double = 100.0,
    val paymentMethod: String = "GPay/PhonePe/Paytm (7010131915)",
    val paymentDate: Long = System.currentTimeMillis(),
    val approvedBy: String? = "Super Admin",
    val approvedAt: Long? = System.currentTimeMillis(),
    val rejectionReason: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)
