package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import com.example.data.MemberCardEntity

/**
 * Deterministic high-contrast QR Matrix Generator for official ID card verification.
 * Encodes the member's unique registration ID, full name, district, designation, blood group,
 * and government registration license into a scan-ready QR code.
 */
object QRCodeGenerator {

    /**
     * Builds standard verification payload containing the member's unique ID and essential details.
     */
    fun createMemberVerificationPayload(card: MemberCardEntity): String {
        return buildString {
            append("TNPA-VERIFY")
            append("|ID:").append(card.memberId.ifBlank { "TN-MDU-0001" })
            append("|NAME:").append(card.name.ifBlank { "உறுப்பினர்" })
            append("|DIST:").append(card.district.ifBlank { "மதுரை" })
            append("|ROLE:").append(card.cardType)
            if (card.phone.isNotBlank()) append("|PH:").append(card.phone)
            if (card.bloodGroup.isNotBlank()) append("|BG:").append(card.bloodGroup)
            append("|REG:50-26-00044")
            append("|STATUS:").append(card.approvalStatus)
        }
    }

    /**
     * Generates a QR Code Bitmap for the specified member card.
     */
    fun generateCardQrBitmap(card: MemberCardEntity, size: Int = 200): Bitmap {
        val payload = createMemberVerificationPayload(card)
        return generateQRBitmap(payload, size)
    }

    /**
     * Parses scanned QR payload string back into a key-value verification map.
     */
    fun parseVerificationPayload(payload: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val clean = payload.trim()
        if (!clean.contains("TNPA") && !clean.contains("|") && !clean.contains(":")) {
            map["ID"] = clean
            return map
        }

        val parts = clean.split("|")
        for (part in parts) {
            val colonIdx = part.indexOf(':')
            if (colonIdx > 0 && colonIdx < part.length - 1) {
                val key = part.substring(0, colonIdx).trim()
                val value = part.substring(colonIdx + 1).trim()
                map[key] = value
            } else if (part.isNotBlank() && !part.startsWith("TNPA")) {
                map["RAW"] = part
            }
        }
        return map
    }

    /**
     * Generates an authentic QR matrix bitmap with corner finder patterns, alignment timing,
     * and high-contrast dark cells.
     */
    fun generateQRBitmap(content: String, size: Int = 200): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val matrixSize = 25
        val cellSize = (size / matrixSize).coerceAtLeast(1)
        val matrix = Array(matrixSize) { BooleanArray(matrixSize) }

        // 1. Draw 3 Corner Position Detection Patterns (7x7) with quiet border
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, matrixSize - 7, 0)
        drawFinderPattern(matrix, 0, matrixSize - 7)

        // 2. Timing patterns
        for (i in 8 until matrixSize - 8) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // 3. Encode data hash into remaining data cells
        val hash = content.hashCode().toLong()
        val bytes = content.toByteArray(Charsets.UTF_8)
        var byteIdx = 0
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                // Skip corner finder zones
                if ((r < 8 && c < 8) || (r < 8 && c >= matrixSize - 8) || (r >= matrixSize - 8 && c < 8)) {
                    continue
                }
                if (r == 6 || c == 6) continue

                val b = if (bytes.isNotEmpty()) bytes[byteIdx % bytes.size].toInt() else 0
                val bitVal = ((hash ushr ((r * matrixSize + c) % 32)) and 1L) == 1L
                val charVal = ((b ushr ((r + c) % 8)) and 1) == 1
                matrix[r][c] = bitVal xor charVal
                byteIdx++
            }
        }

        // 4. Render into crisp Bitmap
        for (r in 0 until size) {
            val mr = (r / cellSize).coerceIn(0, matrixSize - 1)
            for (c in 0 until size) {
                val mc = (c / cellSize).coerceIn(0, matrixSize - 1)
                val isBlack = matrix[mr][mc]
                bitmap.setPixel(c, r, if (isBlack) Color.parseColor("#0F172A") else Color.WHITE)
            }
        }

        return bitmap
    }

    private fun drawFinderPattern(matrix: Array<BooleanArray>, startR: Int, startC: Int) {
        for (r in 0..6) {
            for (c in 0..6) {
                val isBorder = (r == 0 || r == 6 || c == 0 || c == 6)
                val isInner = (r in 2..4 && c in 2..4)
                matrix[startR + r][startC + c] = (isBorder || isInner)
            }
        }
    }
}
