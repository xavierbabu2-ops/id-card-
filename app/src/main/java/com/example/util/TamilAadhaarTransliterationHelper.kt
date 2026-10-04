package com.example.util

object TamilAadhaarTransliterationHelper {

    // Common Tamil Nadu Districts mapping
    private val districtMap = mapOf(
        "madurai" to "மதுரை",
        "chennai" to "சென்னை",
        "coimbatore" to "கோயம்புத்தூர்",
        "kovai" to "கோவை",
        "tiruchirappalli" to "திருச்சிராப்பள்ளி",
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
        "nagapattinam" to "நாகப்பட்டினம்",
        "tiruvarur" to "திருவாரூர்" ,
        "thiruvarur" to "திருவாரூர்",
        "mayiladuthurai" to "மயிலாடுதுறை",
        "cuddalore" to "கடலூர்",
        "villupuram" to "விழுப்புரம்",
        "viluppuram" to "விழுப்புரம்",
        "kallakurichi" to "கள்ளக்குறிச்சி",
        "tiruvannamalai" to "திருவண்ணாமலை",
        "thiruvannamalai" to "திருவண்ணாமலை",
        "tirupathur" to "திருப்பத்தூர்",
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
        "ooty" to "ஊட்டி",
        "ariyalur" to "அரியலூர்",
        "perambalur" to "பெரம்பலூர்"
    )

    // Common address terms mapping
    private val addressTerms = listOf(
        Pair(Regex("\\bTamil\\s*Nadu\\b|\\bTamilnadu\\b", RegexOption.IGNORE_CASE), "தமிழ்நாடு"),
        Pair(Regex("\\bIndia\\b", RegexOption.IGNORE_CASE), "இந்தியா"),
        Pair(Regex("\\bStreet\\b|\\bSt\\b", RegexOption.IGNORE_CASE), "தெரு"),
        Pair(Regex("\\bRoad\\b|\\bRd\\b", RegexOption.IGNORE_CASE), "சாலை"),
        Pair(Regex("\\bMain\\s*Road\\b", RegexOption.IGNORE_CASE), "மெயின் ரோடு"),
        Pair(Regex("\\bCross\\s*Street\\b|\\bCross\\b", RegexOption.IGNORE_CASE), "குறுக்குத் தெரு"),
        Pair(Regex("\\bNagar\\b", RegexOption.IGNORE_CASE), "நகர்"),
        Pair(Regex("\\bColony\\b", RegexOption.IGNORE_CASE), "காலனி"),
        Pair(Regex("\\bPost\\b|\\bPO\\b", RegexOption.IGNORE_CASE), "அஞ்சல்"),
        Pair(Regex("\\bTaluk\\b|\\bTk\\b", RegexOption.IGNORE_CASE), "வட்டம்"),
        Pair(Regex("\\bDistrict\\b|\\bDt\\b", RegexOption.IGNORE_CASE), "மாவட்டம்"),
        Pair(Regex("\\bVillage\\b", RegexOption.IGNORE_CASE), "கிராமம்"),
        Pair(Regex("\\bNear\\b", RegexOption.IGNORE_CASE), "அருகில்"),
        Pair(Regex("\\bOpposite\\b|\\bOpp\\b", RegexOption.IGNORE_CASE), "எதிரில்"),
        Pair(Regex("\\bDoor\\s*No\\b|\\bD\\.No\\b|\\bDNo\\b|\\bNo\\.\\b|\\bNo\\b", RegexOption.IGNORE_CASE), "எண்"),
        Pair(Regex("\\bFloor\\b", RegexOption.IGNORE_CASE), "தளம்"),
        Pair(Regex("\\bApartment\\b|\\bApt\\b", RegexOption.IGNORE_CASE), "குடியிருப்பு"),
        Pair(Regex("\\bNorth\\b", RegexOption.IGNORE_CASE), "வடக்கு"),
        Pair(Regex("\\bSouth\\b", RegexOption.IGNORE_CASE), "தெற்கு"),
        Pair(Regex("\\bEast\\b", RegexOption.IGNORE_CASE), "கிழக்கு"),
        Pair(Regex("\\bWest\\b", RegexOption.IGNORE_CASE), "மேற்கு"),
        Pair(Regex("\\bS/O\\b|\\bSon\\s*of\\b", RegexOption.IGNORE_CASE), "த/பெ"),
        Pair(Regex("\\bD/O\\b|\\bDaughter\\s*of\\b", RegexOption.IGNORE_CASE), "ம/பெ"),
        Pair(Regex("\\bW/O\\b|\\bWife\\s*of\\b", RegexOption.IGNORE_CASE), "க/பெ"),
        Pair(Regex("\\bC/O\\b|\\bCare\\s*of\\b", RegexOption.IGNORE_CASE), "பராமரிப்பில்")
    )

    // Popular Tamil names dictionary
    private val popularNames = mapOf(
        "karthik" to "கார்த்திக்",
        "karthikeyan" to "கார்த்திகேயன்",
        "muthu" to "முத்து",
        "muthusamy" to "முத்துசாமி",
        "murugan" to "முருகன்",
        "murugesan" to "முருகேசன்",
        "senthil" to "செந்தில்",
        "senthilkumar" to "செந்தில்குமார்",
        "kumar" to "குமார்",
        "saravanan" to "சரவணன்",
        "ramesh" to "ரமேஷ்",
        "suresh" to "சுரேஷ்",
        "palanisamy" to "பழனிசாமி",
        "palani" to "பழனி",
        "marimuthu" to "மாரிமுத்து",
        "subramani" to "சுப்பிரமணி",
        "subramanian" to "சுப்பிரமணியன்",
        "sakthivel" to "சக்திவேல்",
        "ganesan" to "கணேசன்",
        "ganesh" to "கணேஷ்",
        "rajesh" to "ராஜேஷ்",
        "selvam" to "செல்வம்",
        "selvaraj" to "செல்வராஜ்",
        "rajan" to "ராஜன்",
        "raja" to "ராஜா",
        "sundaram" to "சுந்தரம்",
        "sundar" to "சுந்தர்",
        "babu" to "பாபு",
        "xavier" to "சேவியர்",
        "robert" to "ராபர்ட்",
        "joseph" to "ஜோசப்",
        "david" to "டேவிட்",
        "manikandan" to "மணிகண்டன்",
        "mani" to "மணி",
        "venkatesh" to "வெங்கடேஷ்",
        "venkat" to "வெங்கட்",
        "vijay" to "விஜய்",
        "vijayan" to "விஜயன்",
        "ajith" to "அஜித்",
        "surya" to "சூர்யா",
        "mohan" to "மோகன்",
        "dinesh" to "தினேஷ்",
        "praveen" to "பிரவீன்",
        "naveen" to "நவீன்",
        "sathish" to "சதீஷ்",
        "vignesh" to "விக்னேஷ்",
        "anand" to "ஆனந்த்",
        "balaji" to "பாலாஜி",
        "chandran" to "சந்திரன்",
        "dharmaraj" to "தர்மராஜ்",
        "elango" to "இளங்கோ",
        "gopal" to "கோபால்",
        "hari" to "ஹரி",
        "ilango" to "இளங்கோ",
        "jayaram" to "ஜெயராம்",
        "kalai" to "கலை",
        "lakshmanan" to "லட்சுமணன்",
        "madhavan" to "மாதவன்",
        "natarajan" to "நடராஜன்",
        "pandian" to "பாண்டியன்",
        "radhakrishnan" to "ராதாகிருஷ்ணன்",
        "sankar" to "சங்கர்",
        "shankar" to "சங்கர்",
        "thirumurugan" to "திருமுருகன்",
        "udhayakumar" to "உதயகுமார்",
        "velu" to "வேலு",
        "velmurugan" to "வேல்முருகன்",
        "yuvaraj" to "யுவராஜ்",
        "prakash" to "பிரகாஷ்",
        "sivakumar" to "சிவக்குமார்",
        "kannan" to "கண்ணன்",
        "perumal" to "பெருமாள்",
        "govind" to "கோவிந்த்",
        "govindan" to "கோவிந்தன்",
        "ram" to "ராம்",
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
        "meena" to "மீனா"
    )

    // Initial transliterations (e.g. M. -> மு. , K. -> கே. , S. -> எஸ். )
    private val initialsMap = mapOf(
        "a" to "ஏ.",
        "b" to "பி.",
        "c" to "சி.",
        "d" to "டி.",
        "e" to "இ.",
        "f" to "எப்.",
        "g" to "ஜி.",
        "h" to "ஹெச்.",
        "i" to "ஐ.",
        "j" to "ஜே.",
        "k" to "கே.",
        "l" to "எல்.",
        "m" to "மு.",
        "n" to "என்.",
        "o" to "ஓ.",
        "p" to "பி.",
        "q" to "கியூ.",
        "r" to "ஆர்.",
        "s" to "எஸ்.",
        "t" to "டி.",
        "u" to "யு.",
        "v" to "வி.",
        "w" to "டபிள்யூ.",
        "x" to "எக்ஸ்.",
        "y" to "ஒய்.",
        "z" to "இசட்."
    )

    /**
     * Translates or transliterates an English person's name into Tamil
     */
    fun transliterateNameToTamil(name: String): String {
        val trimmed = name.trim()
        if (trimmed.isBlank()) return ""
        // If already contains Tamil characters, return as is
        if (trimmed.any { it in '\u0B80'..'\u0BFF' }) return trimmed

        val tokens = trimmed.split(Regex("\\s+"))
        val result = mutableListOf<String>()

        for (token in tokens) {
            val cleanToken = token.trimEnd('.', ',').lowercase()
            if (cleanToken.length == 1) {
                // Initial e.g. "M." or "K"
                result.add(initialsMap[cleanToken] ?: token)
            } else if (popularNames.containsKey(cleanToken)) {
                result.add(popularNames[cleanToken]!!)
            } else {
                // Fallback to phonetic transliteration
                result.add(phoneticTransliterate(cleanToken))
            }
        }

        return result.joinToString(" ")
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
     * Converts a full multi-line or single-line address into Tamil
     */
    fun convertAddressToTamil(address: String): String {
        var result = address.trim()
        if (result.isBlank()) return ""

        // Apply known address terms replacements
        for ((regex, replacement) in addressTerms) {
            result = result.replace(regex, replacement)
        }

        // Apply district name replacements
        for ((distEng, distTam) in districtMap) {
            result = result.replace(Regex("\\b$distEng\\b", RegexOption.IGNORE_CASE), distTam)
        }

        // Replace any remaining known English names within the address
        for ((nameEng, nameTam) in popularNames) {
            result = result.replace(Regex("\\b$nameEng\\b", RegexOption.IGNORE_CASE), nameTam)
        }

        return result
    }

    /**
     * Basic rule-based English to Tamil phonetic converter for words not in the dictionary
     */
    private fun phoneticTransliterate(englishWord: String): String {
        val word = englishWord.lowercase()
        val sb = StringBuilder()
        var i = 0

        while (i < word.length) {
            // 3-letter combos
            if (i + 3 <= word.length) {
                val sub3 = word.substring(i, i + 3)
                when (sub3) {
                    "tha" -> { sb.append("தா"); i += 3; continue }
                    "thi" -> { sb.append("தி"); i += 3; continue }
                    "thu" -> { sb.append("து"); i += 3; continue }
                    "the" -> { sb.append("தே"); i += 3; continue }
                    "tho" -> { sb.append("தோ"); i += 3; continue }
                    "chi" -> { sb.append("சி"); i += 3; continue }
                    "cha" -> { sb.append("சா"); i += 3; continue }
                    "chu" -> { sb.append("சு"); i += 3; continue }
                    "she" -> { sb.append("ஷே"); i += 3; continue }
                    "sha" -> { sb.append("ஷா"); i += 3; continue }
                }
            }

            // 2-letter combos
            if (i + 2 <= word.length) {
                val sub2 = word.substring(i, i + 2)
                when (sub2) {
                    "th" -> { sb.append("த்"); i += 2; continue }
                    "sh" -> { sb.append("ஷ்"); i += 2; continue }
                    "ch" -> { sb.append("ச்"); i += 2; continue }
                    "dh" -> { sb.append("த்"); i += 2; continue }
                    "bh" -> { sb.append("ப்"); i += 2; continue }
                    "kh" -> { sb.append("க்"); i += 2; continue }
                    "ph" -> { sb.append("ப்"); i += 2; continue }
                    "aa" -> { sb.append("ஆ"); i += 2; continue }
                    "ee" -> { sb.append("ஈ"); i += 2; continue }
                    "oo" -> { sb.append("ஊ"); i += 2; continue }
                    "ai" -> { sb.append("ஐ"); i += 2; continue }
                    "ka" -> { sb.append("கா"); i += 2; continue }
                    "ki" -> { sb.append("கி"); i += 2; continue }
                    "ku" -> { sb.append("கு"); i += 2; continue }
                    "ke" -> { sb.append("கே"); i += 2; continue }
                    "ko" -> { sb.append("கோ"); i += 2; continue }
                    "sa" -> { sb.append("சா"); i += 2; continue }
                    "si" -> { sb.append("சி"); i += 2; continue }
                    "su" -> { sb.append("சு"); i += 2; continue }
                    "se" -> { sb.append("சே"); i += 2; continue }
                    "so" -> { sb.append("சோ"); i += 2; continue }
                    "ma" -> { sb.append("மா"); i += 2; continue }
                    "mi" -> { sb.append("மி"); i += 2; continue }
                    "mu" -> { sb.append("மு"); i += 2; continue }
                    "me" -> { sb.append("மே"); i += 2; continue }
                    "mo" -> { sb.append("மோ"); i += 2; continue }
                    "ra" -> { sb.append("ரா"); i += 2; continue }
                    "ri" -> { sb.append("ரி"); i += 2; continue }
                    "ru" -> { sb.append("ரு"); i += 2; continue }
                    "re" -> { sb.append("ரே"); i += 2; continue }
                    "ro" -> { sb.append("ரோ"); i += 2; continue }
                    "na" -> { sb.append("நா"); i += 2; continue }
                    "ni" -> { sb.append("நி"); i += 2; continue }
                    "nu" -> { sb.append("னு"); i += 2; continue }
                    "ne" -> { sb.append("நே"); i += 2; continue }
                    "no" -> { sb.append("நோ"); i += 2; continue }
                    "pa" -> { sb.append("பா"); i += 2; continue }
                    "pi" -> { sb.append("பி"); i += 2; continue }
                    "pu" -> { sb.append("பு"); i += 2; continue }
                    "pe" -> { sb.append("பே"); i += 2; continue }
                    "po" -> { sb.append("போ"); i += 2; continue }
                    "va" -> { sb.append("வா"); i += 2; continue }
                    "vi" -> { sb.append("வி"); i += 2; continue }
                    "vu" -> { sb.append("வு"); i += 2; continue }
                    "ve" -> { sb.append("வே"); i += 2; continue }
                    "vo" -> { sb.append("வோ"); i += 2; continue }
                    "la" -> { sb.append("லா"); i += 2; continue }
                    "li" -> { sb.append("லி"); i += 2; continue }
                    "lu" -> { sb.append("லு"); i += 2; continue }
                    "le" -> { sb.append("லே"); i += 2; continue }
                    "lo" -> { sb.append("லோ"); i += 2; continue }
                }
            }

            // Single letters
            when (word[i]) {
                'a' -> sb.append("அ")
                'b' -> sb.append("ப்")
                'c' -> sb.append("க்")
                'd' -> sb.append("ட்")
                'e' -> sb.append("எ")
                'f' -> sb.append("ப்")
                'g' -> sb.append("க்")
                'h' -> sb.append("ஹ்")
                'i' -> sb.append("இ")
                'j' -> sb.append("ஜ்")
                'k' -> sb.append("க்")
                'l' -> sb.append("ல்")
                'm' -> sb.append("ம்")
                'n' -> sb.append("ன்")
                'o' -> sb.append("ஒ")
                'p' -> sb.append("ப்")
                'q' -> sb.append("க்")
                'r' -> sb.append("ர்")
                's' -> sb.append("ஸ்")
                't' -> sb.append("ட்")
                'u' -> sb.append("உ")
                'v' -> sb.append("வ்")
                'w' -> sb.append("வ்")
                'x' -> sb.append("க்ஸ்")
                'y' -> sb.append("ய்")
                'z' -> sb.append("ஸ்")
                else -> sb.append(word[i])
            }
            i++
        }
        return sb.toString()
    }
}
