package com.example.util

/**
 * High-precision Tamil Transliteration & Cleansing Helper for Aadhaar Cards.
 * Converts names, initials, addresses, relations, and districts into natural, clear, accurate Tamil.
 */
object TamilAadhaarTransliterationHelper {

    // 38 Tamil Nadu Districts mapping
    private val districtMap = mapOf(
        "madurai" to "மதுரை",
        "chennai" to "சென்னை",
        "coimbatore" to "கோயம்புத்தூர்",
        "kovai" to "கோவை",
        "tiruchirappalli" to "திருச்சிராப்பள்ளி",
        "tiruchirapalli" to "திருச்சிராப்பள்ளி",
        "trichy" to "திருச்சி",
        "salem" to "சேலம்",
        "tirunelveli" to "திருநெல்வேலி",
        "nellai" to "நெல்லை",
        "thoothukudi" to "தூத்துக்குடி",
        "tuticorin" to "தூத்துக்குடி",
        "erode" to "ஈரோடு",
        "vellore" to "வேலூர்",
        "dindigul" to "திண்டுக்கல்",
        "thanjavur" to "தஞ்சாவூர்",
        "tanjore" to "தஞ்சாவூர்",
        "ranipet" to "இராணிப்பேட்டை",
        "virudhunagar" to "விருதுநகர்",
        "kanyakumari" to "கன்னியாகுமரி",
        "karur" to "கரூர்",
        "namakkal" to "நாமக்கல்",
        "theni" to "தேனி",
        "sivaganga" to "சிவகங்கை",
        "sivagangai" to "சிவகங்கை",
        "ramanathapuram" to "இராமநாதபுரம்",
        "ramnad" to "இராமநாதபுரம்",
        "pudukkottai" to "புதுக்கோட்டை",
        "pudukottai" to "புதுக்கோட்டை",
        "nagapattinam" to "நாகப்பட்டினம்",
        "tiruvarur" to "திருவாரூர்",
        "thiruvarur" to "திருவாரூர்",
        "mayiladuthurai" to "மயிலாடுதுறை",
        "cuddalore" to "கடலூர்",
        "villupuram" to "விழுப்புரம்",
        "viluppuram" to "விழுப்புரம்",
        "kallakurichi" to "கள்ளக்குறிச்சி",
        "tiruvannamalai" to "திருவண்ணாமலை",
        "thiruvannamalai" to "திருவண்ணாமலை",
        "tirupathur" to "திருப்பத்தூர்",
        "thirupathur" to "திருப்பத்தூர்",
        "dharmapuri" to "தர்மபுரி",
        "krishnagiri" to "கிருஷ்ணகிரி",
        "kanchipuram" to "காஞ்சிபுரம்",
        "kancheepuram" to "காஞ்சிபுரம்",
        "chengalpattu" to "செங்கல்பட்டு",
        "chengalpet" to "செங்கல்பட்டு",
        "tiruvallur" to "திருவள்ளூர்",
        "thiruvallur" to "திருவள்ளூர்",
        "tenkasi" to "தென்காசி",
        "tirupur" to "திருப்பூர்",
        "tiruppur" to "திருப்பூர்",
        "nilgiris" to "நீலகிரி",
        "the nilgiris" to "நீலகிரி",
        "ooty" to "ஊட்டி",
        "ariyalur" to "அரியலூர்",
        "perambalur" to "பெரம்பலூர்"
    )

    // Common address terms mapping
    private val addressTerms = listOf(
        Pair(Regex("\\bTamil\\s*Nadu\\b|\\bTamilnadu\\b|\\bTN\\b", RegexOption.IGNORE_CASE), "தமிழ்நாடு"),
        Pair(Regex("\\bIndia\\b", RegexOption.IGNORE_CASE), "இந்தியா"),
        Pair(Regex("\\bStreet\\b|\\bSt\\b|\\bStr\\b", RegexOption.IGNORE_CASE), "தெரு"),
        Pair(Regex("\\bCross\\s*Street\\b|\\bCross\\b", RegexOption.IGNORE_CASE), "குறுக்குத் தெரு"),
        Pair(Regex("\\bMain\\s*Road\\b|\\bMain\\s*Rd\\b", RegexOption.IGNORE_CASE), "மெயின் ரோடு"),
        Pair(Regex("\\bRoad\\b|\\bRd\\b", RegexOption.IGNORE_CASE), "சாலை"),
        Pair(Regex("\\bLane\\b|\\bLn\\b", RegexOption.IGNORE_CASE), "சந்து"),
        Pair(Regex("\\bNagar\\b|\\bNgr\\b", RegexOption.IGNORE_CASE), "நகர்"),
        Pair(Regex("\\bColony\\b|\\bClny\\b", RegexOption.IGNORE_CASE), "காலனி"),
        Pair(Regex("\\bPost\\b|\\bPO\\b|\\bP\\.O\\.\\b", RegexOption.IGNORE_CASE), "அஞ்சல்"),
        Pair(Regex("\\bTaluk\\b|\\bTk\\b|\\bTlk\\b", RegexOption.IGNORE_CASE), "வட்டம்"),
        Pair(Regex("\\bDistrict\\b|\\bDt\\b|\\bDist\\b", RegexOption.IGNORE_CASE), "மாவட்டம்"),
        Pair(Regex("\\bVillage\\b|\\bVill\\b", RegexOption.IGNORE_CASE), "கிராமம்"),
        Pair(Regex("\\bTown\\b", RegexOption.IGNORE_CASE), "நகரம்"),
        Pair(Regex("\\bNear\\b|\\bNr\\b", RegexOption.IGNORE_CASE), "அருகில்"),
        Pair(Regex("\\bOpposite\\b|\\bOpp\\b", RegexOption.IGNORE_CASE), "எதிரில்"),
        Pair(Regex("\\bBehind\\b", RegexOption.IGNORE_CASE), "பின்புறம்"),
        Pair(Regex("\\bDoor\\s*No\\b|\\bD\\.No\\b|\\bDNo\\b|\\bNo\\.\\b|\\bNo\\b|\\bH\\.No\\b", RegexOption.IGNORE_CASE), "எண்"),
        Pair(Regex("\\bPlot\\s*No\\b", RegexOption.IGNORE_CASE), "மனை எண்"),
        Pair(Regex("\\bFlat\\s*No\\b", RegexOption.IGNORE_CASE), "பிளாட் எண்"),
        Pair(Regex("\\bFloor\\b|\\bFlr\\b", RegexOption.IGNORE_CASE), "தளம்"),
        Pair(Regex("\\bApartment\\b|\\bApt\\b|\\bAppartment\\b", RegexOption.IGNORE_CASE), "குடியிருப்பு"),
        Pair(Regex("\\bNorth\\b", RegexOption.IGNORE_CASE), "வடக்கு"),
        Pair(Regex("\\bSouth\\b", RegexOption.IGNORE_CASE), "தெற்கு"),
        Pair(Regex("\\bEast\\b", RegexOption.IGNORE_CASE), "கிழக்கு"),
        Pair(Regex("\\bWest\\b", RegexOption.IGNORE_CASE), "மேற்கு"),
        Pair(Regex("\\bS/O\\b|\\bSon\\s*of\\b|\\bS/o\\b", RegexOption.IGNORE_CASE), "த/பெ"),
        Pair(Regex("\\bD/O\\b|\\bDaughter\\s*of\\b|\\bD/o\\b", RegexOption.IGNORE_CASE), "ம/பெ"),
        Pair(Regex("\\bW/O\\b|\\bWife\\s*of\\b|\\bW/o\\b", RegexOption.IGNORE_CASE), "க/பெ"),
        Pair(Regex("\\bC/O\\b|\\bCare\\s*of\\b|\\bC/o\\b", RegexOption.IGNORE_CASE), "பராமரிப்பில்"),
        Pair(Regex("\\bBlock\\b", RegexOption.IGNORE_CASE), "பிளாக்"),
        Pair(Regex("\\bSector\\b", RegexOption.IGNORE_CASE), "செக்டார்"),
        Pair(Regex("\\bBus\\s*Stand\\b", RegexOption.IGNORE_CASE), "பேருந்து நிலையம்"),
        Pair(Regex("\\bTemple\\b|\\bKoil\\b|\\bKovil\\b", RegexOption.IGNORE_CASE), "கோவில்"),
        Pair(Regex("\\bHospital\\b|\\bGH\\b", RegexOption.IGNORE_CASE), "மருத்துவமனை"),
        Pair(Regex("\\bSchool\\b", RegexOption.IGNORE_CASE), "பள்ளி")
    )

    // Extensive Tamil Names Dictionary
    private val popularNames = mapOf(
        "karthik" to "கார்த்திக்",
        "karthikeyan" to "கார்த்திகேயன்",
        "karthigeyan" to "கார்த்திகேயன்",
        "muthu" to "முத்து",
        "muthusamy" to "முத்துசாமி",
        "muthuswami" to "முத்துசாமி",
        "muthuraman" to "முத்துராமன்",
        "muthukumar" to "முத்துக்குமார்",
        "muthukumaran" to "முத்துக்குமரன்",
        "murugan" to "முருகன்",
        "murugesan" to "முருகேசன்",
        "murugadoss" to "முருகதாஸ்",
        "murugadas" to "முருகதாஸ்",
        "senthil" to "செந்தில்",
        "senthilkumar" to "செந்தில்குமார்",
        "senthilnathan" to "செந்தில்நாதன்",
        "kumar" to "குமார்",
        "kumaran" to "குமரன்",
        "saravanan" to "சரவணன்",
        "saravana" to "சரவணா",
        "ramesh" to "ரமேஷ்",
        "suresh" to "சுரேஷ்",
        "mahesh" to "மகேஷ்",
        "rajesh" to "ராஜேஷ்",
        "dinesh" to "தினேஷ்",
        "lokesh" to "லோகேஷ்",
        "vignesh" to "விக்னேஷ்",
        "sathish" to "சதீஷ்",
        "satheesh" to "சதீஷ்",
        "palanisamy" to "பழனிசாமி",
        "palaniswamy" to "பழனிசாமி",
        "palani" to "பழனி",
        "palanivel" to "பழனிவேல்",
        "marimuthu" to "மாரிமுத்து",
        "mariyappan" to "மாரியப்பன்",
        "subramani" to "சுப்பிரமணி",
        "subramanian" to "சுப்பிரமணியன்",
        "subbiah" to "சுப்பையா",
        "sakthivel" to "சக்திவேல்",
        "sakthi" to "சக்தி",
        "ganesan" to "கணேசன்",
        "ganesh" to "கணேஷ்",
        "selvam" to "செல்வம்",
        "selvaraj" to "செல்வராஜ்",
        "selvakumar" to "செல்வக்குமார்",
        "selvamani" to "செல்வமணி",
        "rajan" to "ராஜன்",
        "raja" to "ராஜா",
        "rajendran" to "ராஜேந்திரன்",
        "rajeshkumar" to "ராஜேஷ்குமார்",
        "sundaram" to "சுந்தரம்",
        "sundar" to "சுந்தர்",
        "sundararajan" to "சுந்தரராஜன்",
        "soundar" to "சௌந்தர்",
        "soundararajan" to "சௌந்தரராஜன்",
        "babu" to "பாபு",
        "xavier" to "சேவியர்",
        "robert" to "ராபர்ட்",
        "joseph" to "ஜோசப்",
        "david" to "டேவிட்",
        "paul" to "பால்",
        "peter" to "பீட்டர்",
        "john" to "ஜான்",
        "anthony" to "அந்தோணி",
        "antony" to "அந்தோணி",
        "manikandan" to "மணிகண்டன்",
        "mani" to "மணி",
        "manivannan" to "மணிவண்ணன்",
        "manickam" to "மாணிக்கம்",
        "venkatesh" to "வெங்கடேஷ்",
        "venkatesan" to "வெங்கடேசன்",
        "venkat" to "வெங்கட்",
        "vijay" to "விஜய்",
        "vijayan" to "விஜயன்",
        "vijayakumar" to "விஜயகுமார்",
        "ajith" to "அஜித்",
        "ajithkumar" to "அஜித்குமார்",
        "surya" to "சூர்யா",
        "suriya" to "சூர்யா",
        "mohan" to "மோகன்",
        "mohankumar" to "மோகன்குமார்",
        "praveen" to "பிரவீன்",
        "praveenkumar" to "பிரவீன்குமார்",
        "naveen" to "நவீன்",
        "naveenkumar" to "நவீன்குமார்",
        "anand" to "ஆனந்த்",
        "anandan" to "ஆனந்தன்",
        "anandhakumar" to "ஆனந்தகுமார்",
        "balaji" to "பாலாஜி",
        "balu" to "பாலு",
        "balamurugan" to "பாலமுருகன்",
        "balasubramanian" to "பாலசுப்பிரமணியன்",
        "balasubramani" to "பாலசுப்பிரமணி",
        "chandran" to "சந்திரன்",
        "chandrasekar" to "சந்திரசேகர்",
        "chandrasekaran" to "சந்திரசேகரன்",
        "dharmaraj" to "தர்மராஜ்",
        "dharma" to "தர்மா",
        "dhanapal" to "தனபால்",
        "elango" to "இளங்கோ",
        "elangovan" to "இளங்கோவன்",
        "gopal" to "கோபால்",
        "gopalakrishnan" to "கோபாலகிருஷ்ணன்",
        "hari" to "ஹரி",
        "hariharan" to "ஹரிஹரன்",
        "harikrishnan" to "ஹரிகிருஷ்ணன்",
        "ilango" to "இளங்கோ",
        "jayaram" to "ஜெயராம்",
        "jayaraman" to "ஜெயராமன்",
        "jeyaram" to "ஜெயராம்",
        "jeyaraman" to "ஜெயராமன்",
        "jayakumar" to "ஜெயக்குமார்",
        "jeyakumar" to "ஜெயக்குமார்",
        "kalai" to "கலை",
        "kalaivanan" to "கலைவாணன்",
        "kalaiselvan" to "கலைச்செல்வன்",
        "lakshmanan" to "லட்சுமணன்",
        "lakshman" to "லட்சுமணன்",
        "madhavan" to "மாதவன்",
        "natarajan" to "நடராஜன்",
        "nagarajan" to "நாகராஜன்",
        "nagaraj" to "நாகராஜ்",
        "pandian" to "பாண்டியன்",
        "pandi" to "பாண்டி",
        "pandiyarajan" to "பாண்டியராஜன்",
        "radhakrishnan" to "ராதாகிருஷ்ணன்",
        "ramakrishnan" to "ராமகிருஷ்ணன்",
        "sankar" to "சங்கர்",
        "shankar" to "சங்கர்",
        "sivashankar" to "சிவசங்கர்",
        "thirumurugan" to "திருமுருகன்",
        "thirunavukkarasu" to "திருநாவுக்கரசு",
        "udhayakumar" to "உதயகுமார்",
        "udhayan" to "உதயன்",
        "velu" to "வேலு",
        "velmurugan" to "வேல்முருகன்",
        "vadivel" to "வடிவேல்",
        "vadivelu" to "வடிவேலு",
        "yuvaraj" to "யுவராஜ்",
        "prakash" to "பிரகாஷ்",
        "sivakumar" to "சிவக்குமார்",
        "siva" to "சிவா",
        "kannan" to "கண்ணன்",
        "perumal" to "பெருமாள்",
        "govind" to "கோவிந்த்",
        "govindan" to "கோவிந்தன்",
        "govindaraj" to "கோவிந்தராஜ்",
        "ram" to "ராம்",
        "ramasamy" to "ராமசாமி",
        "ramu" to "ராமு",
        "ramadass" to "ராமதாஸ்",
        "arumugam" to "ஆறுமுகம்",
        "anbazhagan" to "அன்பழகன்",
        "anbarasan" to "அன்பரசன்",
        "baskaran" to "பாஸ்கரன்",
        "chelladurai" to "செல்லதுரை",
        "chinnasamy" to "சின்னசாமி",
        "durai" to "துரை",
        "duraisamy" to "துரைசாமி",
        "gurusamy" to "குருசாமி",
        "iyappan" to "அய்யப்பன்",
        "karuppasamy" to "கருப்பசாமி",
        "karuppan" to "கருப்பன்",
        "kathiravan" to "கதிரவன்",
        "loganathan" to "லோகநாதன்",
        "mayandi" to "மாயாண்டி",
        "muniyandi" to "முனியாண்டி",
        "paramasivam" to "பரமசிவம்",
        "periyakaruppan" to "பெரியகருப்பன்",
        "sasikumar" to "சசிகுமார்",
        "shanmugam" to "சண்முகம்",
        "shanmugasundaram" to "சண்முகசுந்தரம்",
        "sokkalingam" to "சொக்கலிங்கம்",
        "thangaraj" to "தங்கராஜ்",
        "thangavel" to "தங்கவேல்",
        "ulaganathan" to "உலகநாதன்",
        "veeramani" to "வீரமணி",
        "veerappan" to "வீரப்பன்",
        "vasanth" to "வசந்த்",
        "vasanthakumar" to "வசந்தகுமார்",
        "vimal" to "விமல்",
        "yogaraj" to "யோகராஜ்",
        "lakshmi" to "லட்சுமி",
        "priya" to "பிரியா",
        "kavitha" to "கவிதா",
        "deepa" to "தீபா",
        "divya" to "திவ்யா",
        "revathi" to "ரேவதி",
        "shanthi" to "சாந்தி",
        "sangeetha" to "சங்கீதா",
        "anitha" to "அனிதா",
        "geetha" to "கீதா",
        "radha" to "ராதா",
        "devi" to "தேவி",
        "meena" to "மீனா",
        "valli" to "வள்ளி",
        "maragatham" to "மரகதம்"
    )

    // Tamil Initials (e.g. M. -> மு. , K. -> கே. , R. -> ரா. , S. -> செ. / எஸ். )
    private val initialsMap = mapOf(
        "a" to "அ.",
        "b" to "பா.",
        "c" to "செ.",
        "d" to "து.",
        "e" to "இ.",
        "f" to "எப்.",
        "g" to "கோ.",
        "h" to "ஹெச்.",
        "i" to "ஐ.",
        "j" to "ஜெ.",
        "k" to "கே.",
        "l" to "எல்.",
        "m" to "மு.",
        "n" to "நா.",
        "o" to "ஓ.",
        "p" to "பா.",
        "q" to "கியூ.",
        "r" to "ரா.",
        "s" to "செ.",
        "t" to "தி.",
        "u" to "உ.",
        "v" to "வே.",
        "w" to "டபிள்யூ.",
        "x" to "எக்ஸ்.",
        "y" to "வை.",
        "z" to "இசட்."
    )

    /**
     * Cleans OCR artifacts from text (stray symbols, non-printable characters)
     */
    fun cleanOcrText(input: String): String {
        return input
            .replace(Regex("[\\|\\{\\}\\[\\]_~`^@#$%*<>\"\\\\]"), " ")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Translates or transliterates an English person's name into clean, natural Tamil.
     * Preserves existing Tamil characters while cleaning up formatting.
     */
    fun transliterateNameToTamil(name: String): String {
        val cleaned = cleanOcrText(name)
        if (cleaned.isBlank()) return ""

        // If the string already contains Tamil characters, clean and format nicely
        if (cleaned.any { it in '\u0B80'..'\u0BFF' }) {
            return formatTamilName(cleaned)
        }

        val tokens = cleaned.split(Regex("\\s+"))
        val result = mutableListOf<String>()

        for (token in tokens) {
            val cleanToken = token.trimEnd('.', ',').lowercase()
            if (cleanToken.length == 1) {
                // Initial e.g. "M." or "K"
                result.add(initialsMap[cleanToken] ?: "${token.uppercase()}.")
            } else if (popularNames.containsKey(cleanToken)) {
                result.add(popularNames[cleanToken]!!)
            } else {
                // High-precision phonetic transliteration
                result.add(phoneticTransliterate(cleanToken))
            }
        }

        return result.joinToString(" ")
    }

    /**
     * Formats existing Tamil name (normalizes initials and spacing)
     */
    private fun formatTamilName(tamilName: String): String {
        return tamilName
            .replace(Regex("([அ-ஹ])\\s*\\."), "$1.")
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    /**
     * Translates gender to Tamil
     */
    fun translateGenderToTamil(gender: String): String {
        val lower = gender.lowercase()
        return when {
            lower.contains("female") || lower.contains("பெண்") -> "பெண் (Female)"
            lower.contains("male") || lower.contains("ஆண்") -> "ஆண் (Male)"
            lower.contains("trans") -> "மூன்றாம் பாலினம்"
            else -> "ஆண் (Male)"
        }
    }

    /**
     * Translates district name to Tamil
     */
    fun translateDistrictToTamil(district: String): String {
        val clean = district.trim().lowercase()
        for ((key, value) in districtMap) {
            if (clean.contains(key)) return value
        }
        return if (district.any { it in '\u0B80'..'\u0BFF' }) district else "மதுரை"
    }

    /**
     * Converts a full multi-line or single-line address into clean, readable Tamil
     */
    fun convertAddressToTamil(address: String): String {
        var result = cleanOcrText(address)
        if (result.isBlank()) return ""

        // Filter out Aadhaar header noise
        result = result
            .replace(Regex("(?i)Government\\s+of\\s+India", RegexOption.IGNORE_CASE), "")
            .replace(Regex("(?i)Unique\\s+Identification\\s+Authority.*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("(?i)Help\\s*line.*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("(?i)Enrollment\\s*No.*", RegexOption.IGNORE_CASE), "")
            .replace(Regex("(?i)www\\.uidai\\.gov\\.in", RegexOption.IGNORE_CASE), "")
            .replace(Regex("(?i)Signature\\s+valid", RegexOption.IGNORE_CASE), "")
            .trim()

        // Apply known address terms replacements
        for ((regex, replacement) in addressTerms) {
            result = result.replace(regex, replacement)
        }

        // Apply district name replacements
        for ((distEng, distTam) in districtMap) {
            result = result.replace(Regex("\\b$distEng\\b", RegexOption.IGNORE_CASE), distTam)
        }

        // Replace any remaining known English names within the address (villages named after deities/names)
        for ((nameEng, nameTam) in popularNames) {
            result = result.replace(Regex("\\b$nameEng\\b", RegexOption.IGNORE_CASE), nameTam)
        }

        // Fix spacing and commas
        result = result
            .replace(Regex("\\s*,\\s*"), ", ")
            .replace(Regex(",\\s*,"), ",")
            .replace(Regex("\\s+"), " ")
            .trim(',', ' ')

        return result
    }

    /**
     * English to Tamil phonetic rule-based transliteration converter
     */
    private fun phoneticTransliterate(englishWord: String): String {
        val word = englishWord.lowercase()
        val sb = StringBuilder()
        var i = 0

        while (i < word.length) {
            // 4-letter combos
            if (i + 4 <= word.length) {
                val sub4 = word.substring(i, i + 4)
                when (sub4) {
                    "than" -> { sb.append("தன்"); i += 4; continue }
                    "mani" -> { sb.append("மணி"); i += 4; continue }
                    "velu" -> { sb.append("வேலு"); i += 4; continue }
                    "raja" -> { sb.append("ராஜா"); i += 4; continue }
                    "samy" -> { sb.append("சாமி"); i += 4; continue }
                    "devi" -> { sb.append("தேவி"); i += 4; continue }
                    "babu" -> { sb.append("பாபு"); i += 4; continue }
                    "paul" -> { sb.append("பால்"); i += 4; continue }
                }
            }

            // 3-letter combos
            if (i + 3 <= word.length) {
                val sub3 = word.substring(i, i + 3)
                when (sub3) {
                    "tha" -> { sb.append("தா"); i += 3; continue }
                    "thi" -> { sb.append("தி"); i += 3; continue }
                    "thu" -> { sb.append("து"); i += 3; continue }
                    "the" -> { sb.append("தே"); i += 3; continue }
                    "tho" -> { sb.append("தோ"); i += 3; continue }
                    "sha" -> { sb.append("ஷா"); i += 3; continue }
                    "shi" -> { sb.append("ஷி"); i += 3; continue }
                    "shu" -> { sb.append("ஷு"); i += 3; continue }
                    "she" -> { sb.append("ஷே"); i += 3; continue }
                    "sho" -> { sb.append("ஷோ"); i += 3; continue }
                    "cha" -> { sb.append("சா"); i += 3; continue }
                    "chi" -> { sb.append("சி"); i += 3; continue }
                    "chu" -> { sb.append("சு"); i += 3; continue }
                    "che" -> { sb.append("செ"); i += 3; continue }
                    "cho" -> { sb.append("சோ"); i += 3; continue }
                    "dha" -> { sb.append("தா"); i += 3; continue }
                    "dhi" -> { sb.append("தி"); i += 3; continue }
                    "dhu" -> { sb.append("து"); i += 3; continue }
                    "dhe" -> { sb.append("தே"); i += 3; continue }
                    "dho" -> { sb.append("தோ"); i += 3; continue }
                    "pra" -> { sb.append("பிர"); i += 3; continue }
                    "pri" -> { sb.append("பிரி"); i += 3; continue }
                    "nan" -> { sb.append("நன்"); i += 3; continue }
                    "ran" -> { sb.append("ரன்"); i += 3; continue }
                    "van" -> { sb.append("வன்"); i += 3; continue }
                    "yan" -> { sb.append("யன்"); i += 3; continue }
                    "san" -> { sb.append("சன்"); i += 3; continue }
                    "raj" -> { sb.append("ராஜ்"); i += 3; continue }
                    "vel" -> { sb.append("வேல்"); i += 3; continue }
                    "ram" -> { sb.append("ராம்"); i += 3; continue }
                }
            }

            // 2-letter combos
            if (i + 2 <= word.length) {
                val sub2 = word.substring(i, i + 2)
                when (sub2) {
                    "aa" -> { sb.append("ஆ"); i += 2; continue }
                    "ee" -> { sb.append("ஈ"); i += 2; continue }
                    "oo" -> { sb.append("ஊ"); i += 2; continue }
                    "ai" -> { sb.append("ஐ"); i += 2; continue }
                    "au" -> { sb.append("ஔ"); i += 2; continue }
                    "ka" -> { sb.append("க"); i += 2; continue }
                    "ki" -> { sb.append("கி"); i += 2; continue }
                    "ku" -> { sb.append("கு"); i += 2; continue }
                    "ke" -> { sb.append("கே"); i += 2; continue }
                    "ko" -> { sb.append("கோ"); i += 2; continue }
                    "ga" -> { sb.append("க"); i += 2; continue }
                    "gi" -> { sb.append("கி"); i += 2; continue }
                    "gu" -> { sb.append("கு"); i += 2; continue }
                    "ge" -> { sb.append("கே"); i += 2; continue }
                    "go" -> { sb.append("கோ"); i += 2; continue }
                    "sa" -> { sb.append("ச"); i += 2; continue }
                    "si" -> { sb.append("சி"); i += 2; continue }
                    "su" -> { sb.append("சு"); i += 2; continue }
                    "se" -> { sb.append("செ"); i += 2; continue }
                    "so" -> { sb.append("சோ"); i += 2; continue }
                    "ja" -> { sb.append("ஜ"); i += 2; continue }
                    "ji" -> { sb.append("ஜி"); i += 2; continue }
                    "ju" -> { sb.append("ஜு"); i += 2; continue }
                    "je" -> { sb.append("ஜெ"); i += 2; continue }
                    "jo" -> { sb.append("ஜோ"); i += 2; continue }
                    "ta" -> { sb.append("த"); i += 2; continue }
                    "ti" -> { sb.append("தி"); i += 2; continue }
                    "tu" -> { sb.append("து"); i += 2; continue }
                    "te" -> { sb.append("தே"); i += 2; continue }
                    "to" -> { sb.append("தோ"); i += 2; continue }
                    "da" -> { sb.append("ட"); i += 2; continue }
                    "di" -> { sb.append("டி"); i += 2; continue }
                    "du" -> { sb.append("டு"); i += 2; continue }
                    "de" -> { sb.append("டெ"); i += 2; continue }
                    "do" -> { sb.append("டோ"); i += 2; continue }
                    "na" -> { sb.append("ந"); i += 2; continue }
                    "ni" -> { sb.append("னி"); i += 2; continue }
                    "nu" -> { sb.append("னு"); i += 2; continue }
                    "ne" -> { sb.append("நே"); i += 2; continue }
                    "no" -> { sb.append("நோ"); i += 2; continue }
                    "pa" -> { sb.append("ப"); i += 2; continue }
                    "pi" -> { sb.append("பி"); i += 2; continue }
                    "pu" -> { sb.append("பு"); i += 2; continue }
                    "pe" -> { sb.append("பெ"); i += 2; continue }
                    "po" -> { sb.append("போ"); i += 2; continue }
                    "ba" -> { sb.append("ப"); i += 2; continue }
                    "bi" -> { sb.append("பி"); i += 2; continue }
                    "bu" -> { sb.append("பு"); i += 2; continue }
                    "be" -> { sb.append("பெ"); i += 2; continue }
                    "bo" -> { sb.append("போ"); i += 2; continue }
                    "ma" -> { sb.append("ம"); i += 2; continue }
                    "mi" -> { sb.append("மி"); i += 2; continue }
                    "mu" -> { sb.append("மு"); i += 2; continue }
                    "me" -> { sb.append("மே"); i += 2; continue }
                    "mo" -> { sb.append("மோ"); i += 2; continue }
                    "ya" -> { sb.append("ய"); i += 2; continue }
                    "yi" -> { sb.append("யி"); i += 2; continue }
                    "yu" -> { sb.append("யு"); i += 2; continue }
                    "ye" -> { sb.append("யே"); i += 2; continue }
                    "yo" -> { sb.append("யோ"); i += 2; continue }
                    "ra" -> { sb.append("ர"); i += 2; continue }
                    "ri" -> { sb.append("ரி"); i += 2; continue }
                    "ru" -> { sb.append("ரு"); i += 2; continue }
                    "re" -> { sb.append("ரே"); i += 2; continue }
                    "ro" -> { sb.append("ரோ"); i += 2; continue }
                    "la" -> { sb.append("ல"); i += 2; continue }
                    "li" -> { sb.append("லி"); i += 2; continue }
                    "lu" -> { sb.append("லு"); i += 2; continue }
                    "le" -> { sb.append("லே"); i += 2; continue }
                    "lo" -> { sb.append("லோ"); i += 2; continue }
                    "va" -> { sb.append("வ"); i += 2; continue }
                    "vi" -> { sb.append("வி"); i += 2; continue }
                    "vu" -> { sb.append("வு"); i += 2; continue }
                    "ve" -> { sb.append("வே"); i += 2; continue }
                    "vo" -> { sb.append("வோ"); i += 2; continue }
                    "ha" -> { sb.append("ஹ"); i += 2; continue }
                    "hi" -> { sb.append("ஹி"); i += 2; continue }
                    "hu" -> { sb.append("ஹு"); i += 2; continue }
                    "he" -> { sb.append("ஹே"); i += 2; continue }
                    "ho" -> { sb.append("ஹோ"); i += 2; continue }
                    "th" -> { sb.append("த்"); i += 2; continue }
                    "sh" -> { sb.append("ஷ்"); i += 2; continue }
                    "ch" -> { sb.append("ச்"); i += 2; continue }
                    "dh" -> { sb.append("த்"); i += 2; continue }
                    "an" -> { sb.append("அன்"); i += 2; continue }
                    "am" -> { sb.append("அம்"); i += 2; continue }
                    "ar" -> { sb.append("அர்"); i += 2; continue }
                    "al" -> { sb.append("அல்"); i += 2; continue }
                    "in" -> { sb.append("இன்"); i += 2; continue }
                    "il" -> { sb.append("இல்"); i += 2; continue }
                }
            }

            // Single letters
            val c = word[i]
            when (c) {
                'a' -> sb.append(if (i == 0) "அ" else "ா")
                'b' -> sb.append("ப்")
                'c' -> sb.append("க்")
                'd' -> sb.append("ட்")
                'e' -> sb.append(if (i == 0) "எ" else "ெ")
                'f' -> sb.append("ப்")
                'g' -> sb.append("க்")
                'h' -> sb.append("ஹ்")
                'i' -> sb.append(if (i == 0) "இ" else "ி")
                'j' -> sb.append("ஜ்")
                'k' -> sb.append("க்")
                'l' -> sb.append("ல்")
                'm' -> sb.append("ம்")
                'n' -> sb.append("ன்")
                'o' -> sb.append(if (i == 0) "ஒ" else "ொ")
                'p' -> sb.append("ப்")
                'q' -> sb.append("க்")
                'r' -> sb.append("ர்")
                's' -> sb.append("ஸ்")
                't' -> sb.append("த்")
                'u' -> sb.append(if (i == 0) "உ" else "ு")
                'v' -> sb.append("வ்")
                'w' -> sb.append("வ்")
                'x' -> sb.append("க்ஸ்")
                'y' -> sb.append("ய்")
                'z' -> sb.append("ஸ்")
                else -> sb.append(c)
            }
            i++
        }

        return sb.toString()
    }
}
