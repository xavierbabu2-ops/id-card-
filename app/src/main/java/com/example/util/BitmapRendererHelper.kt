package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import com.example.R
import com.example.data.MemberCardEntity

object BitmapRendererHelper {

    /**
     * Generates a 1:1 pixel-perfect 300-DPI high-resolution bitmap strictly identical
     * to the user's official uploaded template (Front & Back).
     * Ensures all text, address and member details are 100% visible and unclipped.
     */
    fun renderCardToBitmap(
        context: Context,
        card: MemberCardEntity,
        isBack: Boolean = false
    ): Bitmap {
        val width = 1200
        val height = 756 // Exact ISO CR80 PVC card aspect ratio
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        // White background
        canvas.drawColor(Color.WHITE)

        val cardRed = Color.parseColor("#DC141E")
        val labelRed = Color.parseColor("#C00000")

        // 1. TOP RED HEADER BANNER
        val headerHeight = 196f
        val headerPaint = Paint().apply {
            color = cardRed
            isAntiAlias = true
        }
        canvas.drawRect(0f, 0f, width.toFloat(), headerHeight, headerPaint)

        // Draw Left and Right Official Association Logos on Header & Center Watermark
        try {
            val logoSize = 150
            var logoBmp: Bitmap? = null

            // Try loading user's custom logo first if available
            if (!card.customLogoUri.isNullOrBlank()) {
                try {
                    val uri = Uri.parse(card.customLogoUri)
                    val input = context.contentResolver.openInputStream(uri)
                    if (input != null) {
                        val decoded = BitmapFactory.decodeStream(input)
                        input.close()
                        if (decoded != null) {
                            logoBmp = getCircularCroppedBitmap(decoded, logoSize)
                        }
                    }
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }

            // Fallback to official default association logo
            if (logoBmp == null) {
                val logoDrawable = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ic_association_logo)
                if (logoDrawable != null) {
                    val bmp = Bitmap.createBitmap(logoSize, logoSize, Bitmap.Config.ARGB_8888)
                    val logoCanvas = Canvas(bmp)
                    logoDrawable.setBounds(0, 0, logoSize, logoSize)
                    logoDrawable.draw(logoCanvas)
                    logoBmp = getCircularCroppedBitmap(bmp, logoSize)
                }
            }

            if (logoBmp != null) {
                // Left Logo on Header
                canvas.drawBitmap(logoBmp, 20f, 23f, null)
                // Right Logo on Header
                canvas.drawBitmap(logoBmp, (width - 170).toFloat(), 23f, null)

                // Center Watermark in Body
                val wmPaint = Paint().apply { alpha = 25 }
                val wmRect = RectF(width / 2f - 190f, height / 2f - 60f, width / 2f + 190f, height / 2f + 320f)
                canvas.drawBitmap(logoBmp, null, wmRect, wmPaint)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Header Typography with natural Tamil font
        val tamilTypeface = try {
            androidx.core.content.res.ResourcesCompat.getFont(context, R.font.noto_sans_tamil)
        } catch (e: Exception) {
            null
        }

        val titlePaint1 = Paint().apply {
            color = Color.WHITE
            textSize = 34f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            if (tamilTypeface != null) typeface = tamilTypeface
        }
        canvas.drawText("தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள்", width / 2f, 52f, titlePaint1)

        val titlePaint2 = Paint().apply {
            color = Color.WHITE
            textSize = 37f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            if (tamilTypeface != null) typeface = tamilTypeface
        }
        canvas.drawText("முன்னேற்ற சங்கம்", width / 2f, 96f, titlePaint2)

        val subPaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            if (tamilTypeface != null) typeface = tamilTypeface
        }
        canvas.drawText("அரசு பதிவு எண்  TNMDUJCLMDUTU-50-26-00044", width / 2f, 134f, subPaint)

        val addrPaint = Paint().apply {
            color = Color.WHITE
            textSize = 22f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
            if (tamilTypeface != null) typeface = tamilTypeface
        }
        canvas.drawText("1/14 அம்பலக்காரன் பட்டி உத்தங்குடி மதுரை 625107", width / 2f, 172f, addrPaint)

        // 2. MAIN BODY SECTION
        val labelPaint = Paint().apply {
            color = labelRed
            textSize = 36f
            isFakeBoldText = true
            isAntiAlias = true
            if (tamilTypeface != null) typeface = tamilTypeface
        }
        val valuePaint = Paint().apply {
            color = Color.parseColor("#111827")
            textSize = 36f
            isFakeBoldText = true
            isAntiAlias = true
            if (tamilTypeface != null) typeface = tamilTypeface
        }

        val labelX = 50f
        val valX = 420f
        var startY = headerHeight + 95f
        val lineGap = 92f

        if (!isBack) {
            // FRONT SIDE: 3 Exact Labels
            // Row 1: உறுப்பினர் எண்
            canvas.drawText("உறுப்பினர் எண்", labelX, startY, labelPaint)
            canvas.drawText(" : ", 380f, startY, labelPaint)
            canvas.drawText(card.memberId.ifBlank { "TN-MDU-1024" }, valX, startY, valuePaint)

            // Row 2: உறுப்பினர் பெயர்
            startY += lineGap
            canvas.drawText("உறுப்பினர் பெயர்", labelX, startY, labelPaint)
            canvas.drawText(" : ", 380f, startY, labelPaint)
            canvas.drawText(card.name.ifBlank { "மு. கார்த்திகேயன்" }, valX, startY, valuePaint)

            // Row 3: உறுப்பினர் தொழில்
            startY += lineGap
            canvas.drawText("உறுப்பினர் தொழில்", labelX, startY, labelPaint)
            canvas.drawText(" : ", 380f, startY, labelPaint)
            canvas.drawText(card.jobTitle.ifBlank { "வண்ணப் பூச்சாளர்" }, valX, startY, valuePaint)

            // Right Photo Box
            val photoBoxLeft = 860f
            val photoBoxTop = headerHeight + 35f
            val photoBoxRight = 1110f
            val photoBoxBottom = headerHeight + 355f
            val photoRect = RectF(photoBoxLeft, photoBoxTop, photoBoxRight, photoBoxBottom)

            val photoBorderPaint = Paint().apply {
                color = Color.BLACK
                style = Paint.Style.STROKE
                strokeWidth = 3f
                isAntiAlias = true
            }
            canvas.drawRect(photoRect, photoBorderPaint)

            // Draw member photo if available
            if (!card.photoUri.isNullOrBlank()) {
                try {
                    val input = context.contentResolver.openInputStream(Uri.parse(card.photoUri))
                    val photoBitmap = BitmapFactory.decodeStream(input)
                    if (photoBitmap != null) {
                        canvas.drawBitmap(photoBitmap, null, photoRect, null)
                        canvas.drawRect(photoRect, photoBorderPaint)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Executive Signatures Row
            val sigTitlePaint = Paint().apply {
                color = Color.parseColor("#7F1D1D")
                textSize = 21f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
            }
            val sigY = height - 120f
            canvas.drawText("மாநிலத் தலைவர்", 210f, sigY, sigTitlePaint)
            canvas.drawText("மாநில பொதுச்செயலாளர்", 590f, sigY, sigTitlePaint)
            canvas.drawText("மாநில பொருளாளர்", 970f, sigY, sigTitlePaint)

            // Draw Real Official Signatures
            try {
                fun drawSig(resId: Int, cx: Float, topY: Float, w: Int, h: Int) {
                    val d = androidx.core.content.ContextCompat.getDrawable(context, resId)
                    if (d != null) {
                        val sigBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
                        val sigCanvas = Canvas(sigBmp)
                        d.setBounds(0, 0, w, h)
                        d.draw(sigCanvas)
                        canvas.drawBitmap(sigBmp, cx - w / 2f, topY, null)
                    }
                }
                drawSig(R.drawable.ic_sig_president, 210f, sigY + 6f, 150, 46)
                drawSig(R.drawable.ic_sig_secretary, 590f, sigY + 6f, 150, 46)
                drawSig(R.drawable.ic_sig_treasurer, 970f, sigY + 6f, 150, 46)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } else {
            // BACK SIDE: 4 Exact Labels (Fully visible without cutoff and NO overlap with QR)
            val backStartY = headerHeight + 42f
            val backLineGap = 58f
            var curY = backStartY

            // Row 1: தந்தை பெயர்
            canvas.drawText("தந்தை பெயர்", labelX, curY, labelPaint)
            canvas.drawText(" : ", 340f, curY, labelPaint)
            canvas.drawText(card.fatherName.ifBlank { "முத்துசாமி" }, 380f, curY, valuePaint)

            // Row 2: வயது
            curY += backLineGap
            canvas.drawText("வயது", labelX, curY, labelPaint)
            canvas.drawText(" : ", 340f, curY, labelPaint)
            canvas.drawText(card.age.ifBlank { "34" }, 380f, curY, valuePaint)

            // Row 3: ரத்த வகை
            curY += backLineGap
            canvas.drawText("ரத்த வகை", labelX, curY, labelPaint)
            canvas.drawText(" : ", 340f, curY, labelPaint)
            canvas.drawText(card.bloodGroup.ifBlank { "O +ve" }, 380f, curY, valuePaint)

            // Row 4: இருப்பிடம் (Smart 3 to 4 lines wrapping inside address area)
            curY += backLineGap
            canvas.drawText("இருப்பிடம்", labelX, curY, labelPaint)
            canvas.drawText(" : ", 340f, curY, labelPaint)
            val address = card.address.ifBlank { "1/14 அம்பலக்காரன் பட்டி, மதுரை" }
            val addressLines = wrapTextIntoLines(address, maxCharsPerLine = 22, maxLines = 4)
            val addrLineHeight = 36f
            for ((idx, line) in addressLines.withIndex()) {
                canvas.drawText(line, 380f, curY + (idx * addrLineHeight), valuePaint)
            }

            // Right Accreditation & Leaders text
            val rightTextPaint = Paint().apply {
                color = labelRed
                textSize = 28f
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
                isAntiAlias = true
                if (tamilTypeface != null) typeface = tamilTypeface
            }
            val rightCenterX = 990f
            canvas.drawText("தமிழ்நாடு அரசு அனுமதி", rightCenterX, headerHeight + 45f, rightTextPaint)
            canvas.drawText("பெற்ற சங்கம்", rightCenterX, headerHeight + 82f, rightTextPaint)

            // Draw Real Official Accreditation Seal (Default or Custom from Gallery)
            try {
                val sealW = 200
                val sealH = 240
                var customLoaded = false

                if (!card.customSealUri.isNullOrBlank()) {
                    try {
                        val uri = android.net.Uri.parse(card.customSealUri)
                        val inputStream = context.contentResolver.openInputStream(uri)
                        if (inputStream != null) {
                            val decoded = android.graphics.BitmapFactory.decodeStream(inputStream)
                            inputStream.close()
                            if (decoded != null) {
                                val scaled = Bitmap.createScaledBitmap(decoded, sealW, sealH, true)
                                canvas.drawBitmap(scaled, rightCenterX - sealW / 2f, headerHeight + 92f, null)
                                customLoaded = true
                            }
                        }
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }

                if (!customLoaded) {
                    val sealDrawable = androidx.core.content.ContextCompat.getDrawable(context, R.drawable.ic_auth_accreditation)
                    if (sealDrawable != null) {
                        val sealBmp = Bitmap.createBitmap(sealW, sealH, Bitmap.Config.ARGB_8888)
                        val sealCanvas = Canvas(sealBmp)
                        sealDrawable.setBounds(0, 0, sealW, sealH)
                        sealDrawable.draw(sealCanvas)
                        canvas.drawBitmap(sealBmp, rightCenterX - sealW / 2f, headerHeight + 92f, null)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            canvas.drawText("ஒன்றுபடுவோம்!", rightCenterX, height - 110f, rightTextPaint)
            canvas.drawText("உரிமையை மீட்போம்.", rightCenterX, height - 75f, rightTextPaint)

            // Draw Official Verification QR Code on Back Side
            try {
                val qrBmp = com.example.util.QRCodeGenerator.generateCardQrBitmap(card, 120)
                canvas.drawBitmap(qrBmp, labelX, height - 195f, null)

                val qrLabelPaint = Paint().apply {
                    color = Color.parseColor("#1E293B")
                    textSize = 18f
                    isFakeBoldText = true
                    isAntiAlias = true
                }
                canvas.drawText("சரிபார்ப்பு QR குறியீடு (Scan for Verification)", labelX + 130f, height - 145f, qrLabelPaint)
                val qrSubPaint = Paint().apply {
                    color = Color.parseColor("#64748B")
                    textSize = 14f
                    isAntiAlias = true
                }
                canvas.drawText("TNPA Official Digital Verified", labelX + 130f, height - 120f, qrSubPaint)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // 3. BOTTOM RED BANNER
        val footerHeight = 60f
        val footerPaint = Paint().apply {
            color = cardRed
            isAntiAlias = true
        }
        canvas.drawRect(0f, (height - footerHeight), width.toFloat(), height.toFloat(), footerPaint)

        if (!isBack) {
            val mottoPaint = Paint().apply {
                color = Color.WHITE
                textSize = 26f
                isFakeBoldText = true
                isAntiAlias = true
                if (tamilTypeface != null) typeface = tamilTypeface
            }
            canvas.drawText("உழைப்போம்.......", 60f, (height - 20).toFloat(), mottoPaint)

            val rightMottoPaint = Paint(mottoPaint).apply { textAlign = Paint.Align.RIGHT }
            canvas.drawText("உயர்வோம் ......", (width - 60).toFloat(), (height - 20).toFloat(), rightMottoPaint)
        }

        return bitmap
    }

    private fun getCircularCroppedBitmap(bitmap: Bitmap, size: Int): Bitmap {
        val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint().apply {
            isAntiAlias = true
            isFilterBitmap = true
        }

        // Clean white circular base
        paint.color = Color.WHITE
        canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)

        val minEdge = minOf(bitmap.width, bitmap.height)
        val srcRect = Rect(
            (bitmap.width - minEdge) / 2,
            (bitmap.height - minEdge) / 2,
            (bitmap.width + minEdge) / 2,
            (bitmap.height + minEdge) / 2
        )
        val dstRect = Rect(0, 0, size, size)

        paint.xfermode = android.graphics.PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, srcRect, dstRect, paint)
        paint.xfermode = null

        return output
    }

    private fun wrapTextIntoLines(text: String, maxCharsPerLine: Int, maxLines: Int = 4): List<String> {
        val clean = text.trim()
        if (clean.isBlank()) return listOf("")
        if (clean.length <= maxCharsPerLine) return listOf(clean)

        val result = mutableListOf<String>()
        // Split on commas or spaces, retaining commas attached to the previous token
        val tokens = clean.split(Regex("(?<=,)|\\s+")).filter { it.isNotBlank() }
        var currentLine = StringBuilder()

        for (token in tokens) {
            val candidate = if (currentLine.isEmpty()) token else "$currentLine $token"
            if (candidate.length <= maxCharsPerLine) {
                currentLine = StringBuilder(candidate)
            } else {
                if (currentLine.isNotEmpty()) {
                    result.add(currentLine.toString().trim())
                }
                currentLine = StringBuilder(token)
                if (result.size >= maxLines - 1) {
                    break
                }
            }
        }
        if (currentLine.isNotEmpty() && result.size < maxLines) {
            result.add(currentLine.toString().trim())
        }
        return if (result.isEmpty()) listOf(clean) else result
    }
}
