package com.example.util

import com.example.data.MemberCardEntity

data class AadhaarOcrResult(
    val name: String,
    val fatherName: String,
    val age: String,
    val dob: String,
    val gender: String,
    val address: String,
    val district: String,
    val aadhaarNumber: String,
    val generatedMemberId: String
)

object AadhaarOcrParser {

    /**
     * Parses raw OCR text extracted from an Aadhaar card image
     * and maps it to structured Tamil & English fields.
     */
    fun parseAadhaarText(rawText: String, cardType: String = "MEMBER"): AadhaarOcrResult {
        val lines = rawText.lines().map { it.trim() }.filter { it.isNotBlank() }

        var name = ""
        var fatherName = ""
        var dob = ""
        var age = ""
        var gender = "ஆண் (Male)"
        var address = ""
        var district = "மதுரை"
        var aadhaarNum = ""

        // 1. Aadhaar 12-digit number detection (e.g. 1234 5678 9012 or XXXX XXXX 1234)
        val aadhaarRegex = Regex("\\b\\d{4}\\s+\\d{4}\\s+\\d{4}\\b|\\b[X\\d]{4}\\s+[X\\d]{4}\\s+\\d{4}\\b")
        val matchAadhaar = aadhaarRegex.find(rawText)
        if (matchAadhaar != null) {
            aadhaarNum = matchAadhaar.value
        }

        // 2. Date of Birth / Year of Birth / Age
        val dobRegex = Regex("(?:DOB|Date of Birth|பிறந்த தேதி|DOB:)\\s*[:\\-]?\\s*(\\d{2}[/\\-]\\d{2}[/\\-]\\d{4})", RegexOption.IGNORE_CASE)
        val matchDob = dobRegex.find(rawText)
        if (matchDob != null) {
            dob = matchDob.groupValues[1]
            val year = dob.takeLast(4).toIntOrNull()
            if (year != null && year in 1940..2020) {
                age = (2026 - year).toString()
            }
        } else {
            val yobRegex = Regex("(?:Year of Birth|பிறந்த ஆண்டு|YOB)\\s*[:\\-]?\\s*(\\d{4})", RegexOption.IGNORE_CASE)
            val matchYob = yobRegex.find(rawText)
            if (matchYob != null) {
                val year = matchYob.groupValues[1].toIntOrNull()
                if (year != null && year in 1940..2020) {
                    age = (2026 - year).toString()
                    dob = "01/01/$year"
                }
            }
        }

        // 3. Gender Detection
        if (rawText.contains("Female", ignoreCase = true) || rawText.contains("பெண்", ignoreCase = true)) {
            gender = "பெண் (Female)"
        } else if (rawText.contains("Male", ignoreCase = true) || rawText.contains("ஆண்", ignoreCase = true)) {
            gender = "ஆண் (Male)"
        }

        // 4. Father's / Husband's Name (S/O, D/O, W/O, C/O, த/பெ, க/பெ)
        val fatherRegex = Regex("(?:S/O|D/O|W/O|C/O|த/பெ|க/பெ|Father's Name|Husband's Name)\\s*[:\\.]?\\s*([A-Za-z\\s\\.\\u0B80-\\u0BFF]+)", RegexOption.IGNORE_CASE)
        val matchFather = fatherRegex.find(rawText)
        if (matchFather != null) {
            val rawFather = matchFather.groupValues[1].substringBefore(",").substringBefore("\n").trim()
            fatherName = TamilAadhaarTransliterationHelper.cleanOcrText(rawFather)
        }

        // 5. District Detection
        district = DistrictCodeHelper.detectDistrictFromText(rawText)

        // 6. Address Detection (Support Multi-line Tamil & English Aadhaar Address)
        val addrKeywordRegex = Regex("(?:Address|முகவரி|C/O|S/O|D/O|W/O|த/பெ|க/பெ|இருப்பிடம்)\\s*[:\\-]?\\s*([\\s\\S]+)", RegexOption.IGNORE_CASE)
        val matchAddr = addrKeywordRegex.find(rawText)
        if (matchAddr != null) {
            val potentialAddr = matchAddr.groupValues[1]
                .replace(aadhaarRegex, "")
                .lines()
                .map { it.trim() }
                .filter { line ->
                    line.isNotBlank() &&
                    !line.contains("Unique", ignoreCase = true) &&
                    !line.contains("Authority", ignoreCase = true) &&
                    !line.contains("Government", ignoreCase = true) &&
                    !line.contains("ஆதார்", ignoreCase = true) &&
                    !line.contains("அடையாளம்", ignoreCase = true) &&
                    !line.contains("Help", ignoreCase = true) &&
                    !line.contains("1947", ignoreCase = true)
                }
                .take(4)
                .joinToString(", ")
                .trimEnd(',', ' ')
            if (potentialAddr.length >= 6) {
                address = TamilAadhaarTransliterationHelper.cleanOcrText(potentialAddr)
            }
        }

        // Alternative Address Extraction from lines containing numbers, street or district keywords
        if (address.isBlank()) {
            val addrLines = mutableListOf<String>()
            for (line in lines) {
                if (line != name && line != fatherName &&
                    !line.contains("Government", ignoreCase = true) &&
                    !line.contains("Unique", ignoreCase = true) &&
                    !line.contains("Authority", ignoreCase = true) &&
                    !line.contains("DOB", ignoreCase = true) &&
                    !line.contains("Date of Birth", ignoreCase = true) &&
                    !line.contains("Male", ignoreCase = true) &&
                    !line.contains("Female", ignoreCase = true) &&
                    !line.contains("ஆண்", ignoreCase = true) &&
                    !line.contains("பெண்", ignoreCase = true) &&
                    !line.contains("ஆதார்", ignoreCase = true) &&
                    !line.contains("XXXX", ignoreCase = true) &&
                    !line.contains("Help", ignoreCase = true)
                ) {
                    if (line.any { it.isDigit() } ||
                        line.contains("தெரு", ignoreCase = true) ||
                        line.contains("நகர்", ignoreCase = true) ||
                        line.contains("கிராமம்", ignoreCase = true) ||
                        line.contains("வட்டம்", ignoreCase = true) ||
                        line.contains("Street", ignoreCase = true) ||
                        line.contains("Road", ignoreCase = true) ||
                        line.contains("Nagar", ignoreCase = true) ||
                        line.contains("Village", ignoreCase = true) ||
                        line.contains("Post", ignoreCase = true) ||
                        line.contains("Taluk", ignoreCase = true) ||
                        line.contains("Dist", ignoreCase = true) ||
                        line.contains("மாவட்டம்", ignoreCase = true)
                    ) {
                        addrLines.add(line)
                    }
                }
            }
            if (addrLines.isNotEmpty()) {
                address = TamilAadhaarTransliterationHelper.cleanOcrText(addrLines.take(3).joinToString(", "))
            }
        }

        // 7. Name Extraction
        // Prioritize native Tamil name line if detected
        val tamilNameLine = lines.firstOrNull { line ->
            line.any { it in '\u0B80'..'\u0BFF' } &&
            !line.contains("ஆதார்", ignoreCase = true) &&
            !line.contains("இந்திய", ignoreCase = true) &&
            !line.contains("அரசு", ignoreCase = true) &&
            !line.contains("தேதி", ignoreCase = true) &&
            !line.contains("முகவரி", ignoreCase = true) &&
            !line.contains("ஆண்", ignoreCase = true) &&
            !line.contains("பெண்", ignoreCase = true) &&
            !line.contains("த/பெ", ignoreCase = true) &&
            line.length in 3..35 &&
            !line.any { it.isDigit() }
        }

        if (tamilNameLine != null) {
            name = TamilAadhaarTransliterationHelper.cleanOcrText(tamilNameLine)
        } else {
            for (line in lines) {
                if (!line.contains("Government", ignoreCase = true) &&
                    !line.contains("Unique", ignoreCase = true) &&
                    !line.contains("Authority", ignoreCase = true) &&
                    !line.contains("DOB", ignoreCase = true) &&
                    !line.contains("Date of Birth", ignoreCase = true) &&
                    !line.contains("India", ignoreCase = true) &&
                    !line.contains("Male", ignoreCase = true) &&
                    !line.contains("Female", ignoreCase = true) &&
                    !line.contains("Help", ignoreCase = true) &&
                    !line.contains("Aadhaar", ignoreCase = true) &&
                    line.length in 4..30 &&
                    !line.any { it.isDigit() }
                ) {
                    name = TamilAadhaarTransliterationHelper.cleanOcrText(line)
                    break
                }
            }
        }

        // Generate member ID if district detected
        val generatedId = DistrictCodeHelper.generateDistrictMemberId(district, cardType)

        return AadhaarOcrResult(
            name = name,
            fatherName = fatherName,
            age = age,
            dob = dob,
            gender = gender,
            address = address,
            district = district,
            aadhaarNumber = aadhaarNum,
            generatedMemberId = generatedId
        )
    }

    /**
     * Converts OCR Result into an updated MemberCardEntity without overwriting non-empty fields with blank values
     */
    fun applyToMemberCard(currentCard: MemberCardEntity, result: AadhaarOcrResult): MemberCardEntity {
        return currentCard.copy(
            memberId = if (result.generatedMemberId.isNotBlank()) result.generatedMemberId else currentCard.memberId,
            name = if (result.name.isNotBlank()) result.name else currentCard.name,
            fatherName = if (result.fatherName.isNotBlank()) result.fatherName else currentCard.fatherName,
            age = if (result.age.isNotBlank()) result.age else currentCard.age,
            district = if (result.district.isNotBlank()) result.district else currentCard.district,
            address = if (result.address.isNotBlank()) result.address else currentCard.address,
            aadhaarNumber = if (result.aadhaarNumber.isNotBlank()) result.aadhaarNumber else currentCard.aadhaarNumber
        )
    }
}
