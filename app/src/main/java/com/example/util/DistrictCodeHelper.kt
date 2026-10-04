package com.example.util

import com.example.data.MemberCardEntity
import java.util.Locale

data class DistrictDetail(
    val tamilName: String,
    val englishName: String,
    val code: String,
    val startingId: String,
    val sampleAddress: String,
    val samplePincode: String,
    val sampleName: String,
    val sampleFatherName: String = "முத்துசாமி"
)

object DistrictCodeHelper {

    /**
     * Complete list of all 38 official districts in Tamil Nadu
     */
    val ALL_38_DISTRICTS = listOf(
        "மதுரை",
        "சென்னை",
        "கோயம்புத்தூர்",
        "திருச்சிராப்பள்ளி",
        "சேலம்",
        "திண்டுக்கல்",
        "திருநெல்வேலி",
        "தஞ்சாவூர்",
        "ஈரோடு",
        "திருப்பூர்",
        "வேலூர்",
        "விருதுநகர்",
        "சிவகங்கை",
        "தேனி",
        "ராமநாதபுரம்",
        "புதுக்கோட்டை",
        "கரூர்",
        "நாமக்கல்",
        "கன்னியாகுமரி",
        "தூத்துக்குடி",
        "கடலூர்",
        "காஞ்சிபுரம்",
        "செங்கல்பட்டு",
        "கள்ளக்குறிச்சி",
        "விழுப்புரம்",
        "தர்மபுரி",
        "கிருஷ்ணகிரி",
        "திருவண்ணாமலை",
        "திருவள்ளூர்",
        "திருவாரூர்",
        "நாகப்பட்டினம்",
        "மயிலாடுதுறை",
        "அரியலூர்",
        "பெரம்பலூர்",
        "நீலகிரி",
        "ராணிப்பேட்டை",
        "திருப்பத்தூர்",
        "தென்காசி"
    )

    /**
     * Complete details for all 38 official districts in Tamil Nadu
     * Starting Member ID strictly begins from 0001 for each district
     */
    val ALL_38_DISTRICT_DETAILS: List<DistrictDetail> = listOf(
        DistrictDetail("மதுரை", "Madurai", "MDU", "TN-MDU-0001", "1/14, அம்பலக்காரன் பட்டி, ஒத்தங்குடி, மதுரை", "625001", "மு. கார்த்திகேயன்", "முத்துசாமி"),
        DistrictDetail("சென்னை", "Chennai", "CHN", "TN-CHN-0001", "8, பாரதி தெரு, அண்ணா நகர், சென்னை", "600001", "க. மாரிமுத்து", "கந்தசாமி"),
        DistrictDetail("கோயம்புத்தூர்", "Coimbatore", "CBE", "TN-CBE-0001", "12, காமராஜர் சாலை, பீளமேடு, கோயம்புத்தூர்", "641001", "வே. சுப்பிரமணி", "வேலுச்சாமி"),
        DistrictDetail("திருச்சிராப்பள்ளி", "Tiruchirappalli", "TRY", "TN-TRY-0001", "24, காவேரி தெரு, ஸ்ரீரங்கம், திருச்சி", "620001", "ஆர். சக்திவேல்", "ராமையா"),
        DistrictDetail("சேலம்", "Salem", "SLM", "TN-SLM-0001", "5, காமராஜர் வீதி, சூரமங்கலம், சேலம்", "636001", "சு. பழனிசாமி", "சுந்தரம்"),
        DistrictDetail("திண்டுக்கல்", "Dindigul", "DGL", "TN-DGL-0001", "45, நேதாஜி சாலை, பழனி ரோடு, திண்டுக்கல்", "624001", "செ. முருகேசன்", "செந்தில்"),
        DistrictDetail("திருநெல்வேலி", "Tirunelveli", "TNV", "TN-TNV-0001", "18, தெற்கு ரத வீதி, பாளையங்கோட்டை, திருநெல்வேலி", "627001", "பா. ஆறுமுகம்", "பால்ராஜ்"),
        DistrictDetail("தஞ்சாவூர்", "Thanjavur", "TNJ", "TN-TNJ-0001", "9, பெரிய கோவில் தெரு, தஞ்சாவூர்", "613001", "கோ. கணேசன்", "கோவிந்தன்"),
        DistrictDetail("ஈரோடு", "Erode", "ERD", "TN-ERD-0001", "22, மேட்டூர் ரோடு, பவானி மெயின், ஈரோடு", "638001", "ச. தங்கவேல்", "சின்னசாமி"),
        DistrictDetail("திருப்பூர்", "Tiruppur", "TPR", "TN-TPR-0001", "15, பல்லடம் ரோடு, குமரன் நகர், திருப்பூர்", "641601", "ம. செல்வராஜ்", "மாணிக்கம்"),
        DistrictDetail("வேலூர்", "Vellore", "VLR", "TN-VLR-0001", "33, காட்பாடி ரோடு, கோட்டை வட்டம், வேலூர்", "632001", "ஜெ. நாராயணன்", "ஜெயராமன்"),
        DistrictDetail("விருதுநகர்", "Virudhunagar", "VDR", "TN-VDR-0001", "7, மதுரை ரோடு, அருப்புக்கோட்டை, விருதுநகர்", "626001", "சி. காசிராஜன்", "சிவசாமி"),
        DistrictDetail("சிவகங்கை", "Sivaganga", "SVG", "TN-SVG-0001", "11, கல்லூரி சாலை, காரைக்குடி, சிவகங்கை", "630001", "அ. சிதம்பரம்", "அண்ணாமலை"),
        DistrictDetail("தேனி", "Theni", "TNI", "TN-TNI-0001", "6, கம்பம் ரோடு, பெரியகுளம், தேனி", "625531", "பொ. பாண்டியன்", "பொன்னுசாமி"),
        DistrictDetail("ராமநாதபுரம்", "Ramanathapuram", "RMD", "TN-RMD-0001", "28, அரண்மனை தெரு, பரமக்குடி, ராமநாதபுரம்", "623501", "மு. முனியசாமி", "முருகன்"),
        DistrictDetail("புதுக்கோட்டை", "Pudukkottai", "PDK", "TN-PDK-0001", "14, கீழ ராஜ வீதி, புதுக்கோட்டை", "622001", "வை. ராஜேந்திரன்", "வடிவேல்"),
        DistrictDetail("கரூர்", "Karur", "KRR", "TN-KRR-0001", "19, ஜவஹர் பஜார், லைட்ஹவுஸ் கார்னர், கரூர்", "639001", "கு. மணிகண்டன்", "குப்புசாமி"),
        DistrictDetail("நாமக்கல்", "Namakkal", "NMK", "TN-NMK-0001", "3, சேலம் மெயின் ரோடு, திருச்செங்கோடு, நாமக்கல்", "637001", "நா. பூபதி", "நடராஜன்"),
        DistrictDetail("கன்னியாகுமரி", "Kanyakumari", "KKI", "TN-KKI-0001", "52, டபிள்யூ.சி.சி ரோடு, நாகர்கோவில், கன்னியாகுமரி", "629001", "டி. ஜோசப் சேவியர்", "தர்மராஜ்"),
        DistrictDetail("தூத்துக்குடி", "Thoothukudi", "TUT", "TN-TUT-0001", "88, பீச் ரோடு, கோவில்பட்டி, தூத்துக்குடி", "628001", "வி. ஆண்டனி ராஜ்", "வின்சென்ட்"),
        DistrictDetail("கடலூர்", "Cuddalore", "CDL", "TN-CDL-0001", "16, கடற்கரை சாலை, சிதம்பரம், கடலூர்", "607001", "ப. சம்பத் குமார்", "பழனிவேல்"),
        DistrictDetail("காஞ்சிபுரம்", "Kanchipuram", "KCH", "TN-KCH-0001", "41, காந்தி ரோடு, சங்கர மடம் அருகில், காஞ்சிபுரம்", "631501", "ஏ. வரதராஜன்", "அரங்கநாதன்"),
        DistrictDetail("செங்கல்பட்டு", "Chengalpattu", "CGL", "TN-CGL-0001", "10, ஜி.எஸ்.டி சாலை, தாம்பரம் கிழக்கு, செங்கல்பட்டு", "603001", "எஸ். தயாளன்", "சுப்பிரமணியன்"),
        DistrictDetail("கள்ளக்குறிச்சி", "Kallakurichi", "KLK", "TN-KLK-0001", "25, சேலம் மெயின் ரோடு, கள்ளக்குறிச்சி", "606202", "க. ஏழுமலை", "காசிநாதன்"),
        DistrictDetail("விழுப்புரம்", "Villupuram", "VPM", "TN-VPM-0001", "37, நேருஜி ரோடு, திண்டிவனம் மெயின், விழுப்புரம்", "605602", "ர. அய்யனார்", "ரங்கநாதன்"),
        DistrictDetail("தர்மபுரி", "Dharmapuri", "DPI", "TN-DPI-0001", "13, நேதாஜி பைபாஸ், பென்னாகரம் ரோடு, தர்மபுரி", "636701", "தி. மாதேஸ்வரன்", "திருநாவுக்கரசு"),
        DistrictDetail("கிருஷ்ணகிரி", "Krishnagiri", "KGI", "TN-KGI-0001", "90, பெங்களூரு மெயின் ரோடு, ஓசூர், கிருஷ்ணகிரி", "635001", "கி. முனிராஜ்", "கிருஷ்ணப்பா"),
        DistrictDetail("திருவண்ணாமலை", "Tiruvannamalai", "TVM", "TN-TVM-0001", "77, தேரடி வீதி, கிரிவலப் பாதை, திருவண்ணாமலை", "606601", "அ. அண்ணாமலை", "அம்பிகாபதி"),
        DistrictDetail("திருவள்ளூர்", "Tiruvallur", "TLR", "TN-TLR-0001", "61, சி.டி.எச் ரோடு, ஆவடி, திருவள்ளூர்", "602001", "ஆ. தணிகாசலம்", "ஆறுமுகம்"),
        DistrictDetail("திருவாரூர்", "Tiruvarur", "TVR", "TN-TVR-0001", "29, தெற்கு வீதி, மன்னார்குடி, திருவாரூர்", "610001", "ம. வைத்தியநாதன்", "மருதமுத்து"),
        DistrictDetail("நாகப்பட்டினம்", "Nagapattinam", "NGP", "TN-NGP-0001", "17, பப்ளிக் ஆபீஸ் ரோடு, வேளாங்கண்ணி, நாகப்பட்டினம்", "611001", "நா. ஜெகநாதன்", "நடேசன்"),
        DistrictDetail("மயிலாடுதுறை", "Mayiladuthurai", "MYD", "TN-MYD-0001", "34, பட்டமங்கல தெரு, சீர்காழி, மயிலாடுதுறை", "609001", "ம. கல்யாணசுந்தரம்", "மாணிக்கம்"),
        DistrictDetail("அரியலூர்", "Ariyalur", "ALR", "TN-ALR-0001", "8, சிமெண்ட் பேக்டரி ரோடு, ஜெயங்கொண்டம், அரியலூர்", "621704", "அ. கருணாநிதி", "அன்பழகன்"),
        DistrictDetail("பெரம்பலூர்", "Perambalur", "PBL", "TN-PBL-0001", "21, வெங்கடேசபுரம், புது பஸ் ஸ்டாண்ட், பெரம்பலூர்", "621212", "பெ. ராஜா", "பெருமாள்"),
        DistrictDetail("நீலகிரி", "Nilgiris", "NIL", "TN-NIL-0001", "44, கமர்சியல் ரோடு, குன்னூர், நீலகிரி", "643001", "ஜே. ரிச்சர்ட் ஜான்", "ஜான்சன்"),
        DistrictDetail("ராணிப்பேட்டை", "Ranipet", "RNP", "TN-RNP-0001", "55, ஆற்காடு ரோடு, வாலாஜாபேட்டை, ராணிப்பேட்டை", "632401", "ரா. மோகன்", "ராஜகோபால்"),
        DistrictDetail("திருப்பத்தூர்", "Tirupathur", "TPT", "TN-TPT-0001", "31, வாணியம்பாடி மெயின் ரோடு, திருப்பத்தூர்", "635601", "தி. சீனிவாசன்", "திருவேங்கடம்"),
        DistrictDetail("தென்காசி", "Tenkasi", "TKS", "TN-TKS-0001", "12, குற்றாலம் மெயின் ரோடு, சங்கரன்கோவில், தென்காசி", "627811", "தெ. முத்தையா", "தெய்வநாயகம்")
    )

    // District Code Mapping for all 38 Districts (both Tamil and English spellings)
    private val districtCodeMap = mapOf(
        // 1. மதுரை (Madurai)
        "மதுரை" to "MDU", "madurai" to "MDU",
        // 2. சென்னை (Chennai)
        "சென்னை" to "CHN", "chennai" to "CHN", "madras" to "CHN",
        // 3. கோயம்புத்தூர் / கோவை (Coimbatore)
        "கோயம்புத்தூர்" to "CBE", "கோவை" to "CBE", "coimbatore" to "CBE", "kovai" to "CBE",
        // 4. திருச்சிராப்பள்ளி / திருச்சி (Tiruchirappalli / Trichy)
        "திருச்சிராப்பள்ளி" to "TRY", "திருச்சி" to "TRY", "tiruchirappalli" to "TRY", "trichy" to "TRY", "tiruchi" to "TRY",
        // 5. சேலம் (Salem)
        "சேலம்" to "SLM", "salem" to "SLM",
        // 6. திண்டுக்கல் (Dindigul)
        "திண்டுக்கல்" to "DGL", "dindigul" to "DGL",
        // 7. திருநெல்வேலி / நெல்லை (Tirunelveli)
        "திருநெல்வேலி" to "TNV", "நெல்லை" to "TNV", "tirunelveli" to "TNV", "nellai" to "TNV",
        // 8. தஞ்சாவூர் (Thanjavur)
        "தஞ்சாவூர்" to "TNJ", "thanjavur" to "TNJ", "tanjore" to "TNJ",
        // 9. ஈரோடு (Erode)
        "ஈரோடு" to "ERD", "erode" to "ERD",
        // 10. திருப்பூர் (Tiruppur)
        "திருப்பூர்" to "TPR", "tiruppur" to "TPR", "tirupur" to "TPR",
        // 11. வேலூர் (Vellore)
        "வேலூர்" to "VLR", "vellore" to "VLR",
        // 12. விருதுநகர் (Virudhunagar)
        "விருதுநகர்" to "VDR", "virudhunagar" to "VDR",
        // 13. சிவகங்கை (Sivaganga)
        "சிவகங்கை" to "SVG", "sivaganga" to "SVG", "sivagangai" to "SVG",
        // 14. தேனி (Theni)
        "தேனி" to "TNI", "theni" to "TNI",
        // 15. ராமநாதபுரம் (Ramanathapuram)
        "ராமநாதபுரம்" to "RMD", "இராமநாதபுரம்" to "RMD", "ramanathapuram" to "RMD", "ramnad" to "RMD",
        // 16. புதுக்கோட்டை (Pudukkottai)
        "புதுக்கோட்டை" to "PDK", "pudukkottai" to "PDK", "pudukottai" to "PDK",
        // 17. கரூர் (Karur)
        "கரூர்" to "KRR", "karur" to "KRR",
        // 18. நாமக்கல் (Namakkal)
        "நாமக்கல்" to "NMK", "namakkal" to "NMK",
        // 19. கன்னியாகுமரி (Kanyakumari)
        "கன்னியாகுமரி" to "KKI", "kanyakumari" to "KKI", "kanyakumari district" to "KKI",
        // 20. தூத்துக்குடி (Thoothukudi / Tuticorin)
        "தூத்துக்குடி" to "TUT", "thoothukudi" to "TUT", "tuticorin" to "TUT",
        // 21. கடலூர் (Cuddalore)
        "கடலூர்" to "CDL", "cuddalore" to "CDL",
        // 22. காஞ்சிபுரம் (Kanchipuram)
        "காஞ்சிபுரம்" to "KCH", "kanchipuram" to "KCH", "kancheepuram" to "KCH",
        // 23. செங்கல்பட்டு (Chengalpattu)
        "செங்கல்பட்டு" to "CGL", "chengalpattu" to "CGL", "chengalpet" to "CGL",
        // 24. கள்ளக்குறிச்சி (Kallakurichi)
        "கள்ளக்குறிச்சி" to "KLK", "kallakurichi" to "KLK",
        // 25. விழுப்புரம் (Villupuram)
        "விழுப்புரம்" to "VPM", "villupuram" to "VPM", "viluppuram" to "VPM",
        // 26. தர்மபுரி (Dharmapuri)
        "தர்மபுரி" to "DPI", "dharmapuri" to "DPI",
        // 27. கிருஷ்ணகிரி (Krishnagiri)
        "கிருஷ்ணகிரி" to "KGI", "krishnagiri" to "KGI",
        // 28. திருவண்ணாமலை (Tiruvannamalai)
        "திருவண்ணாமலை" to "TVM", "tiruvannamalai" to "TVM", "thiruvannamalai" to "TVM",
        // 29. திருவள்ளூர் (Tiruvallur)
        "திருவள்ளூர்" to "TLR", "tiruvallur" to "TLR", "thiruvallur" to "TLR",
        // 30. திருவாரூர் (Tiruvarur)
        "திருவாரூர்" to "TVR", "tiruvarur" to "TVR", "thiruvarur" to "TVR",
        // 31. நாகப்பட்டினம் (Nagapattinam)
        "நாகப்பட்டினம்" to "NGP", "nagapattinam" to "NGP", "nagai" to "NGP",
        // 32. மயிலாடுதுறை (Mayiladuthurai)
        "மயிலாடுதுறை" to "MYD", "mayiladuthurai" to "MYD",
        // 33. அரியலூர் (Ariyalur)
        "அரியலூர்" to "ALR", "ariyalur" to "ALR",
        // 34. பெரம்பலூர் (Perambalur)
        "பெரம்பலூர்" to "PBL", "perambalur" to "PBL",
        // 35. நீலகிரி / ஊட்டி (Nilgiris / Ooty)
        "நீலகிரி" to "NIL", "nilgiris" to "NIL", "nilgiri" to "NIL", "ooty" to "NIL", "ஊட்டி" to "NIL",
        // 36. ராணிப்பேட்டை (Ranipet)
        "ராணிப்பேட்டை" to "RNP", "இராணிப்பேட்டை" to "RNP", "ranipet" to "RNP",
        // 37. திருப்பத்தூர் (Tirupathur)
        "திருப்பத்தூர்" to "TPT", "tirupathur" to "TPT", "thirupathur" to "TPT",
        // 38. தென்காசி (Tenkasi)
        "தென்காசி" to "TKS", "tenkasi" to "TKS"
    )

    fun getDistrictCode(districtName: String): String {
        val cleanName = districtName.substringBefore(" (").trim().lowercase()
        return districtCodeMap[cleanName] ?: districtCodeMap.entries.firstOrNull {
            cleanName.contains(it.key, ignoreCase = true) || it.key.contains(cleanName, ignoreCase = true)
        }?.value ?: "MDU"
    }

    fun getDistrictDetail(districtNameOrCode: String): DistrictDetail {
        val clean = districtNameOrCode.substringBefore(" (").trim().lowercase()
        val found = ALL_38_DISTRICT_DETAILS.firstOrNull {
            it.tamilName.equals(clean, ignoreCase = true) ||
            it.englishName.equals(clean, ignoreCase = true) ||
            it.code.equals(clean, ignoreCase = true) ||
            clean.contains(it.tamilName, ignoreCase = true) ||
            clean.contains(it.englishName, ignoreCase = true)
        }
        return found ?: ALL_38_DISTRICT_DETAILS.first()
    }

    /**
     * Generates a 4-digit district-coded member ID starting strictly from 0001:
     * e.g., Madurai -> TN-MDU-0001
     * Chennai -> TN-CHN-0001
     * Coimbatore -> TN-CBE-0001
     * Trichy -> TN-TRY-0001
     * Salem -> TN-SLM-0001
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
     * If no existing cards in this district, sequence starts at 0001.
     * If cards exist, it increments to 0002, 0003, etc.
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

        // Look for cards matching this district prefix or district code
        val maxSeq = existingCards
            .filter { card ->
                card.memberId.startsWith(prefix) ||
                (card.district.contains(district, ignoreCase = true) && card.memberId.contains(distCode))
            }
            .mapNotNull { card ->
                // Extract last contiguous digits
                val match = Regex("""\d{1,5}""").findAll(card.memberId).lastOrNull()?.value
                match?.toIntOrNull()
            }
            .maxOrNull() ?: 0

        return generateDistrictMemberId(district, cardType, maxSeq + 1)
    }

    /**
     * Major towns & taluks to district mapping for high accuracy Aadhaar detection
     */
    private val townToDistrictMap = mapOf(
        // Chennai / Chengalpattu / Tiruvallur
        "tambaram" to "செங்கல்பட்டு", "தாம்பரம்" to "செங்கல்பட்டு",
        "pallavaram" to "செங்கல்பட்டு", "பல்லாவரம்" to "செங்கல்பட்டு",
        "chromepet" to "செங்கல்பட்டு", "குரோம்பேட்டை" to "செங்கல்பட்டு",
        "avadi" to "திருவள்ளூர்", "ஆவடி" to "திருவள்ளூர்",
        "poonamallee" to "திருவள்ளூர்", "பூந்தமல்லி" to "திருவள்ளூர்",
        "ambattur" to "சென்னை", "அம்பத்தூர்" to "சென்னை",
        "sholinganallur" to "சென்னை", "சோழிங்கநல்லூர்" to "சென்னை",
        "guindy" to "சென்னை", "கிண்டி" to "சென்னை",
        "velachery" to "சென்னை", "வேளச்சேரி" to "சென்னை",
        "t nagar" to "சென்னை", "தியாகராய நகர்" to "சென்னை",
        "mylapore" to "சென்னை", "மயிலாப்பூர்" to "சென்னை",
        // Coimbatore
        "pollachi" to "கோயம்புத்தூர்", "பொள்ளாச்சி" to "கோயம்புத்தூர்",
        "mettupalayam" to "கோயம்புத்தூர்", "மேட்டுப்பாளையம்" to "கோயம்புத்தூர்",
        "sulur" to "கோயம்புத்தூர்", "சூலூர்" to "கோயம்புத்தூர்",
        // Madurai
        "melur" to "மதுரை", "மேலூர்" to "மதுரை",
        "thiruparankundram" to "மதுரை", "திருப்பரங்குன்றம்" to "மதுரை",
        "usilampatti" to "மதுரை", "உசிலம்பட்டி" to "மதுரை",
        // Trichy
        "srirangam" to "திருச்சிராப்பள்ளி", "ஸ்ரீரங்கம்" to "திருச்சிராப்பள்ளி",
        "manapparai" to "திருச்சிராப்பள்ளி", "மணப்பாறை" to "திருச்சிராப்பள்ளி",
        "thuvakudi" to "திருச்சிராப்பள்ளி", "துவாக்குடி" to "திருச்சிராப்பள்ளி",
        // Salem
        "attur" to "சேலம்", "ஆத்தூர்" to "சேலம்",
        "mettur" to "சேலம்", "மேட்டூர்" to "சேலம்",
        "omalur" to "சேலம்", "ஓமலூர்" to "சேலம்",
        // Tirunelveli & Tenkasi
        "palayamkottai" to "திருநெல்வேலி", "பாளையங்கோட்டை" to "திருநெல்வேலி",
        "sankarankovil" to "தென்காசி", "சங்கரன்கோவில்" to "தென்காசி",
        "courtallam" to "தென்காசி", "குற்றாலம்" to "தென்காசி",
        // Virudhunagar
        "sivakasi" to "விருதுநகர்", "சிவகாசி" to "விருதுநகர்",
        "rajapalayam" to "விருதுநகர்", "ராஜபாளையம்" to "விருதுநகர்",
        "srivilliputhur" to "விருதுநகர்", "ஸ்ரீவில்லிபுத்தூர்" to "விருதுநகர்",
        // Thoothukudi
        "kovilpatti" to "தூத்துக்குடி", "கோவில்பட்டி" to "தூத்துக்குடி",
        "tiruchendur" to "தூத்துக்குடி", "திருச்செந்தூர்" to "தூத்துக்குடி",
        // Kanyakumari
        "nagercoil" to "கன்னியாகுமரி", "நாகர்கோவில்" to "கன்னியாகுமரி",
        "padmanabhapuram" to "கன்னியாகுமரி", "பத்மநாபபுரம்" to "கன்னியாகுமரி",
        "marthandam" to "கன்னியாகுமரி", "மார்த்தாண்டம்" to "கன்னியாகுமரி",
        // Nilgiris
        "ooty" to "நீலகிரி", "ஊட்டி" to "நீலகிரி",
        "coonoor" to "நீலகிரி", "குன்னூர்" to "நீலகிரி",
        "kotagiri" to "நீலகிரி", "கோத்தகிரி" to "நீலகிரி",
        // Thanjavur & Mayiladuthurai & Nagapattinam
        "kumbakonam" to "தஞ்சாவூர்", "கும்பகோணம்" to "தஞ்சாவூர்",
        "sirkazhi" to "மயிலாடுதுறை", "சீர்காழி" to "மயிலாடுதுறை",
        "velankanni" to "நாகப்பட்டினம்", "வேளாங்கண்ணி" to "நாகப்பட்டினம்",
        // Krishnagiri & Dharmapuri
        "hosur" to "கிருஷ்ணகிரி", "ஓசூர்" to "கிருஷ்ணகிரி",
        "harur" to "தர்மபுரி", "அரூர்" to "தர்மபுரி",
        // Vellore / Ranipet / Tirupathur
        "katpadi" to "வேலூர்", "காட்பாடி" to "வேலூர்",
        "arakkonam" to "ராணிப்பேட்டை", "அரக்கோணம்" to "ராணிப்பேட்டை",
        "vaniyambadi" to "திருப்பத்தூர்", "வாணியம்பாடி" to "திருப்பத்தூர்",
        "ambur" to "திருப்பத்தூர்", "ஆம்பூர்" to "திருப்பத்தூர்"
    )

    /**
     * Detects Tamil Nadu district from raw Aadhaar address string or OCR text
     * Covers:
     * 1. Direct Tamil district names (38 official districts)
     * 2. Direct English district names (38 official districts)
     * 3. Famous Tamil Nadu towns & taluks
     * 4. Tamil Nadu Postal PIN codes (600xxx to 643xxx)
     */
    fun detectDistrictFromText(text: String): String {
        val lower = text.lowercase()

        // 1. Direct match with official Tamil list
        for (dist in ALL_38_DISTRICTS) {
            if (text.contains(dist, ignoreCase = true)) {
                return dist
            }
        }

        // 2. Direct English / Alternate names mapping to Tamil
        for (detail in ALL_38_DISTRICT_DETAILS) {
            if (lower.contains(detail.englishName.lowercase())) {
                return detail.tamilName
            }
        }

        // 3. Towns & Taluks mapping
        for ((town, dist) in townToDistrictMap) {
            if (lower.contains(town)) {
                return dist
            }
        }

        // 4. Pin code detection (Tamil Nadu PINs: 600xxx - 643xxx)
        val pinRegex = Regex("""\b(6\d{2})\s?(\d{3})\b""")
        val pinMatch = pinRegex.find(text)
        if (pinMatch != null) {
            val prefix3 = pinMatch.groupValues[1].toIntOrNull() ?: 0
            val detected = when (prefix3) {
                600 -> "சென்னை"
                601, 602 -> "திருவள்ளூர்"
                603 -> "செங்கல்பட்டு"
                604, 605 -> "விழுப்புரம்"
                606 -> "கள்ளக்குறிச்சி"
                607, 608 -> "கடலூர்"
                609 -> "மயிலாடுதுறை"
                610 -> "திருவாரூர்"
                611 -> "நாகப்பட்டினம்"
                612, 613, 614 -> "தஞ்சாவூர்"
                620 -> "திருச்சிராப்பள்ளி"
                621 -> "அரியலூர்"
                622 -> "புதுக்கோட்டை"
                623 -> "ராமநாதபுரம்"
                624 -> "திண்டுக்கல்"
                625 -> "மதுரை"
                626 -> "விருதுநகர்"
                627 -> "திருநெல்வேலி"
                628 -> "தூத்துக்குடி"
                629 -> "கன்னியாகுமரி"
                630 -> "சிவகங்கை"
                631 -> "காஞ்சிபுரம்"
                632 -> "வேலூர்"
                635 -> "கிருஷ்ணகிரி"
                636 -> "சேலம்"
                637 -> "நாமக்கல்"
                638 -> "ஈரோடு"
                639 -> "கரூர்"
                641 -> "கோயம்புத்தூர்"
                642 -> "திருப்பூர்"
                643 -> "நீலகிரி"
                else -> null
            }
            if (detected != null) {
                return detected
            }
        }

        return "மதுரை"
    }
}
