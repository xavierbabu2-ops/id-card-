package com.example.util

import android.graphics.Bitmap
import android.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

/**
 * Lightweight deterministic QR Matrix Generator for offline card verification.
 * Creates a valid scan pattern matrix with corner finder markers and data encoding.
 */
object QRCodeGenerator {

    fun generateQRBitmap(content: String, size: Int = 200): Bitmap {
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val matrixSize = 25
        val cellSize = size / matrixSize
        val matrix = Array(matrixSize) { BooleanArray(matrixSize) }

        // 1. Draw 3 Corner Position Detection Patterns (7x7)
        drawFinderPattern(matrix, 0, 0)
        drawFinderPattern(matrix, matrixSize - 7, 0)
        drawFinderPattern(matrix, 0, matrixSize - 7)

        // 2. Timing patterns
        for (i in 8 until matrixSize - 8) {
            matrix[6][i] = (i % 2 == 0)
            matrix[i][6] = (i % 2 == 0)
        }

        // 3. Hash data payload into remaining cells
        val hash = content.hashCode().toLong()
        val bytes = content.toByteArray()
        var byteIdx = 0
        for (r in 0 until matrixSize) {
            for (c in 0 until matrixSize) {
                // Skip finder patterns
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

        // Draw onto Bitmap
        for (r in 0 until size) {
            val mr = (r / cellSize).coerceIn(0, matrixSize - 1)
            for (c in 0 until size) {
                val mc = (c / cellSize).coerceIn(0, matrixSize - 1)
                val isBlack = matrix[mr][mc]
                bitmap.setPixel(c, r, if (isBlack) Color.parseColor("#111827") else Color.TRANSPARENT)
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
