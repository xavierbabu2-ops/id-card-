package com.example.util

import com.example.data.MemberCardEntity
import java.util.Locale

object DistrictCodeHelper {

    private val districtCodeMap = mapOf(
        "மதுரை" to "MDU",
        "சென்னை" to "CHN",
        "கோயம்புத்தூர்" to "CBE",
        "திருச்சி" to "TRY",
        "திருச்சிராப்பள்ளி" to "TRY",
        "சேலம்" to "SLM",
        "திண்டுக்கல்" to "DGL",
        "திருநெல்வேலி" to "TNV",
        "தஞ்சாவூர்" to "TNJ",
        "ஈரோடு" to "ERD",
        "திருப்பூர்" to "TPR",
        "வேலூர்" to "VLR",
        "விருதுநகர்" to "VDR",
        "சிவகங்கை" to "SVG",
        "தேனி" to "TNI",
        "ராமநாதபுரம்" to "RMD",
        "புதுக்கோட்டை" to "PDK",
        "கரூர்" to "KRR",
        "நாமக்கல்" to "NMK",
        "கன்னியாகுமரி" to "KKI",
        "தூத்துக்குடி" to "TUT",
        "கடலூர்" to "CDL",
        "காஞ்சிபுரம்" to "KCH",
        "செங்கல்பட்டு" to "CGL",
        "கள்ளக்குறிச்சி" to "KLK",
        "விழுப்புரம்" to "VPM",
        "தர்மபுரி" to "DPI",
        "கிருஷ்ணகிரி" to "KGI",
        "திருவண்ணாமலை" to "TVM",
        "திருவள்ளூர்" to "TLR",
        "திருவாரூர்" to "TVR",
        "நாகப்பட்டினம்" to "NGP",
        "மயிலாடுதுறை" to "MYD",
        "அரியலூர்" to "ALR",
        "பெரம்பலூர்" to "PBL",
        "நீலகிரி" to "NIL",
        "ராணிப்பேட்டை" to "RNP",
        "திருப்பத்தூர்" to "TPT",
        "தென்காசி" to "TKS"
    )

    fun getDistrictCode(districtName: String): String {
        val cleanName = districtName.substringBefore(" (").trim()
        return districtCodeMap[cleanName] ?: districtCodeMap.entries.firstOrNull {
            cleanName.contains(it.key, ignoreCase = true) || it.key.contains(cleanName, ignoreCase = true)
        }?.value ?: "TN"
    }

    /**
     * Generates a 4-digit district-coded member ID starting from 0001:
     * e.g., Madurai -> TN-MDU-0001
     * Chennai -> TN-CHN-0001
     * Executive -> TN-EXEC-MDU-0001
     * Contractor -> TN-CON-CHN-0001
     */
    fun generateDistrictMemberId(
        district: String,
        cardType: String = "MEMBER",
        sequenceNumber: Int = 1
    ): String {
        val distCode = getDistrictCode(district)
        val fourDigitNum = String.format(Locale.US, "%04d", sequenceNumber.coerceAtLeast(1))

        return when (cardType) {
            "EXECUTIVE" -> "TN-EXEC-$distCode-$fourDigitNum"
            "CONTRACTOR" -> "TN-CON-$distCode-$fourDigitNum"
            else -> "TN-$distCode-$fourDigitNum"
        }
    }

    /**
     * Computes the next sequential member ID for a specific district starting from 0001.
     */
    fun getNextMemberIdForDistrict(
        district: String,
        cardType: String = "MEMBER",
        existingCards: List<MemberCardEntity>
    ): String {
        val distCode = getDistrictCode(district)
        val prefix = when (cardType) {
            "EXECUTIVE" -> "TN-EXEC-$distCode-"
            "CONTRACTOR" -> "TN-CON-$distCode-"
            else -> "TN-$distCode-"
        }

        val maxSeq = existingCards
            .filter { it.memberId.startsWith(prefix) }
            .mapNotNull { card ->
                val numPart = card.memberId.removePrefix(prefix).take(4)
                numPart.toIntOrNull()
            }
            .maxOrNull() ?: 0

        return generateDistrictMemberId(district, cardType, maxSeq + 1)
    }

    /**
     * Detects Tamil Nadu district from raw Aadhaar address string or OCR text
     */
    fun detectDistrictFromText(text: String): String {
        for ((district, _) in districtCodeMap) {
            if (text.contains(district, ignoreCase = true)) {
                return district
            }
        }

        // Check common English district names
        val englishMap = mapOf(
            "madurai" to "மதுரை",
            "chennai" to "சென்னை",
            "coimbatore" to "கோயம்புத்தூர்",
            "trichy" to "திருச்சி",
            "tiruchirappalli" to "திருச்சி",
            "salem" to "சேலம்",
            "dindigul" to "திண்டுக்கல்",
            "tirunelveli" to "திருநெல்வேலி",
            "thanjavur" to "தஞ்சாவூர்",
            "erode" to "ஈரோடு",
            "tiruppur" to "திருப்பூர்",
            "vellore" to "வேலூர்",
            "virudhunagar" to "விருதுநகர்",
            "sivagangai" to "சிவகங்கை",
            "theni" to "தேனி",
            "ramanathapuram" to "ராமநாதபுரம்",
            "pudukkottai" to "புதுக்கோட்டை",
            "karur" to "கரூர்",
            "namakkal" to "நாமக்கல்",
            "kanyakumari" to "கன்னியாகுமரி",
            "tuticorin" to "தூத்துக்குடி",
            "thoothukudi" to "தூத்துக்குடி",
            "cuddalore" to "கடலூர்",
            "kanchipuram" to "காஞ்சிபுரம்",
            "chengalpattu" to "செங்கல்பட்டு",
            "kallakurichi" to "கள்ளக்குறிச்சி",
            "villupuram" to "விழுப்புரம்",
            "dharmapuri" to "தர்மபுரி",
            "krishnagiri" to "கிருஷ்ணகிரி",
            "tiruvannamalai" to "திருவண்ணாமலை",
            "tiruvallur" to "திருவள்ளூர்",
            "tiruvarur" to "திருவாரூர்",
            "nagapattinam" to "நாகப்பட்டினம்",
            "mayiladuthurai" to "மயிலாடுதுறை",
            "ariyalur" to "அரியலூர்",
            "perambalur" to "பெரம்பலூர்",
            "nilgiris" to "நீலகிரி",
            "ranipet" to "ராணிப்பேட்டை",
            "tirupathur" to "திருப்பத்தூர்",
            "tenkasi" to "தென்காசி"
        )

        val lower = text.lowercase()
        for ((eng, tam) in englishMap) {
            if (lower.contains(eng)) {
                return tam
            }
        }

        return "மதுரை"
    }
}
