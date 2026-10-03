package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.MemberCardEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

object CardExporter {

    /**
     * Saves a bitmap image in PNG format directly into the device Pictures gallery.
     */
    fun saveBitmapToGallery(context: Context, bitmap: Bitmap, fileName: String): Uri? {
        var outputStream: OutputStream? = null
        var imageUri: Uri? = null

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.png")
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TN_Painters_Cards")
                }
                val resolver = context.contentResolver
                imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                if (imageUri != null) {
                    outputStream = resolver.openOutputStream(imageUri)
                }
            } else {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/TN_Painters_Cards"
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdirs()
                }
                val image = File(imagesDir, "$fileName.png")
                outputStream = FileOutputStream(image)
                imageUri = Uri.fromFile(image)
            }

            outputStream?.let {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                it.flush()
                it.close()
                Toast.makeText(context, "அடையாள அட்டை படம் சேமிக்கப்பட்டது! (Saved to Gallery)", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "சேமிப்பதில் பிழை: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
        return imageUri
    }

    /**
     * Generates a combined dual-side (Front + Back) print-ready sheet bitmap.
     */
    fun generateDualSideSheetBitmap(frontBitmap: Bitmap, backBitmap: Bitmap): Bitmap {
        val width = maxOf(frontBitmap.width, backBitmap.width) + 80
        val height = frontBitmap.height + backBitmap.height + 160
        val combined = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(combined)
        canvas.drawColor(Color.WHITE)

        val borderPaint = Paint().apply {
            color = Color.LTGRAY
            style = Paint.Style.STROKE
            strokeWidth = 3f
            pathEffect = DashPathEffect(floatArrayOf(15f, 15f), 0f)
        }

        val textPaint = Paint().apply {
            color = Color.DKGRAY
            textSize = 28f
            isAntiAlias = true
            isFakeBoldText = true
        }

        // Draw Front Label & Card
        val frontX = (width - frontBitmap.width) / 2f
        canvas.drawText("முன்பக்கம் (Front Side)", frontX, 45f, textPaint)
        canvas.drawBitmap(frontBitmap, frontX, 60f, null)
        canvas.drawRect(RectF(frontX - 4, 56f, frontX + frontBitmap.width + 4, 64f + frontBitmap.height), borderPaint)

        // Draw Back Label & Card
        val backY = frontBitmap.height + 120f
        val backX = (width - backBitmap.width) / 2f
        canvas.drawText("பின்பக்கம் (Back Side)", backX, backY - 15f, textPaint)
        canvas.drawBitmap(backBitmap, backX, backY, null)
        canvas.drawRect(RectF(backX - 4, backY - 4, backX + backBitmap.width + 4, backY + backBitmap.height + 4), borderPaint)

        return combined
    }

    /**
     * Generates an official high-resolution PDF document containing the Member ID Card (Front & Back)
     * formatted on an A4 page with cutting guides and union verification details.
     */
    fun generateMemberIdCardPdf(
        context: Context,
        card: MemberCardEntity,
        frontBitmap: Bitmap,
        backBitmap: Bitmap?
    ): File? {
        val pdfDocument = PdfDocument()

        try {
            // Standard A4 Size in points (72 points/inch: 595 x 842 points)
            val pageWidth = 595
            val pageHeight = 842
            val pageInfo = PdfDocument.PageInfo.Builder(pageWidth, pageHeight, 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            // Background
            canvas.drawColor(Color.WHITE)

            val headerPaint = Paint().apply {
                color = Color.parseColor("#D3121B")
                textSize = 15f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val subHeaderPaint = Paint().apply {
                color = Color.parseColor("#1E293B")
                textSize = 10f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val regPaint = Paint().apply {
                color = Color.parseColor("#B45309")
                textSize = 9f
                isAntiAlias = true
                isFakeBoldText = true
                textAlign = Paint.Align.CENTER
            }

            val guidePaint = Paint().apply {
                color = Color.LTGRAY
                style = Paint.Style.STROKE
                strokeWidth = 1f
                pathEffect = DashPathEffect(floatArrayOf(6f, 6f), 0f)
            }

            val labelPaint = Paint().apply {
                color = Color.DKGRAY
                textSize = 9f
                isAntiAlias = true
                isFakeBoldText = true
            }

            val notePaint = Paint().apply {
                color = Color.GRAY
                textSize = 8f
                isAntiAlias = true
                textAlign = Paint.Align.CENTER
            }

            // Top Header
            canvas.drawText("தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள் முன்னேற்ற சங்கம்", pageWidth / 2f, 40f, headerPaint)
            canvas.drawText("Tamil Nadu Painters & Artists Welfare Association (TNPA²)", pageWidth / 2f, 55f, subHeaderPaint)
            canvas.drawText("அரசு பதிவு எண்: TNMDUJCLMDUTU-50-26-00044 | தலைமை: மதுரை - 625107", pageWidth / 2f, 68f, regPaint)

            // Header Separator Line
            val linePaint = Paint().apply {
                color = Color.parseColor("#E2E8F0")
                strokeWidth = 1.5f
            }
            canvas.drawLine(40f, 80f, pageWidth - 40f, 80f, linePaint)

            // Calculate Card Dimensions on PDF (Standard CR80 size: approx 320 x 202 points)
            val cardWidth = 320f
            val cardHeight = 202f
            val cardX = (pageWidth - cardWidth) / 2f

            // Front Card
            val frontY = 110f
            canvas.drawText("1. உறுப்பினர் அட்டை முன்பக்கம் (Front Side):", cardX, frontY - 10f, labelPaint)
            val frontRect = RectF(cardX, frontY, cardX + cardWidth, frontY + cardHeight)
            canvas.drawBitmap(frontBitmap, null, frontRect, null)
            canvas.drawRect(frontRect, guidePaint)

            // Back Card or Executive/Contractor Card
            if (backBitmap != null && card.cardType == "MEMBER") {
                val backY = frontY + cardHeight + 40f
                canvas.drawText("2. உறுப்பினர் அட்டை பின்பக்கம் (Back Side):", cardX, backY - 10f, labelPaint)
                val backRect = RectF(cardX, backY, cardX + cardWidth, backY + cardHeight)
                canvas.drawBitmap(backBitmap, null, backRect, null)
                canvas.drawRect(backRect, guidePaint)
            }

            // Bottom Instructions & Cutting Guide Note
            val bottomY = pageHeight - 90f
            canvas.drawLine(40f, bottomY - 20f, pageWidth - 40f, bottomY - 20f, linePaint)
            canvas.drawText("✂️ புள்ளிக் கோடுகள் வழியே கத்தரித்து லேமினேஷன் (Lamination) அல்லது PVC கார்டு பிரிண்ட் செய்து கொள்ளலாம்.", pageWidth / 2f, bottomY, notePaint)
            canvas.drawText("உறுப்பினர் பெயர்: ${card.name} | எண்: ${card.memberId} | மாவட்டம்: ${card.district} | நிலை: ${card.approvalStatus}", pageWidth / 2f, bottomY + 14f, notePaint)
            canvas.drawText("அதிகாரப்பூர்வ டிஜிட்டல் சான்றிதழ் ஆவணம் | TNPA2 Security Verified", pageWidth / 2f, bottomY + 28f, notePaint)

            pdfDocument.finishPage(page)

            // Save PDF to cache/files dir
            val pdfDir = File(context.cacheDir, "generated_pdfs")
            if (!pdfDir.exists()) pdfDir.mkdirs()

            val fileName = "TNPA_${card.memberId}_${card.name.replace(" ", "_")}.pdf"
            val pdfFile = File(pdfDir, fileName)
            val outputStream = FileOutputStream(pdfFile)
            pdfDocument.writeTo(outputStream)
            outputStream.flush()
            outputStream.close()

            // Also copy to Downloads via MediaStore if on Android 10+
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                try {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                        put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/TN_Painters_Cards")
                    }
                    val resolver = context.contentResolver
                    val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)
                    if (uri != null) {
                        resolver.openOutputStream(uri)?.use { out ->
                            pdfFile.inputStream().copyTo(out)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            try {
                Toast.makeText(context, "PDF ஆவணம் வெற்றிகரமாக பதிவிறக்கப்பட்டது! (PDF Downloaded)", Toast.LENGTH_LONG).show()
            } catch (ignored: Exception) {}
            return pdfFile
        } catch (e: Exception) {
            e.printStackTrace()
            try {
                Toast.makeText(context, "PDF உருவாக்குவதில் பிழை: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            } catch (ignored: Exception) {}
            return null
        } finally {
            try {
                pdfDocument.close()
            } catch (ignored: Exception) {}
        }
    }

    /**
     * Opens or shares the generated PDF document.
     */
    fun openOrSharePdf(context: Context, pdfFile: File, isShare: Boolean = false) {
        try {
            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, pdfFile)

            val action = if (isShare) Intent.ACTION_SEND else Intent.ACTION_VIEW
            val intent = Intent(action).apply {
                setDataAndType(uri, "application/pdf")
                if (isShare) {
                    type = "application/pdf"
                    putExtra(Intent.EXTRA_STREAM, uri)
                    putExtra(Intent.EXTRA_SUBJECT, "TNPA ID Card PDF")
                    putExtra(Intent.EXTRA_TEXT, "தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள் முன்னேற்ற சங்கம் - உறுப்பினர் அடையாள அட்டை (PDF)")
                }
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(Intent.createChooser(intent, if (isShare) "PDF பகிரவும்" else "PDF திறக்க"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF திறப்பதில் பிழை: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Generates the high-resolution PDF for the given member card and immediately opens
     * the system share sheet to send via WhatsApp, Telegram, Gmail, Bluetooth, or other messaging apps.
     */
    fun shareMemberCardPdfDirectly(context: Context, card: MemberCardEntity) {
        try {
            val frontBmp = BitmapRendererHelper.renderCardToBitmap(context, card, isBack = false)
            val backBmp = if (card.cardType == "MEMBER") {
                BitmapRendererHelper.renderCardToBitmap(context, card, isBack = true)
            } else null

            val pdfFile = generateMemberIdCardPdf(context, card, frontBmp, backBmp)
            if (pdfFile != null) {
                openOrSharePdf(context, pdfFile, isShare = true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "PDF பகிர்வதில் பிழை: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares a combined dual-side sheet (Front + Back) directly via WhatsApp or messaging apps.
     */
    fun shareMemberCardDualSheetDirectly(context: Context, card: MemberCardEntity) {
        try {
            val frontBmp = BitmapRendererHelper.renderCardToBitmap(context, card, isBack = false)
            val backBmp = BitmapRendererHelper.renderCardToBitmap(context, card, isBack = true)
            val combined = generateDualSideSheetBitmap(frontBmp, backBmp)
            shareCardBitmap(context, combined, "${card.name} - ${card.memberId} (Front & Back)")
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "பகிர்வதில் பிழை: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Shares a card bitmap image via social media / WhatsApp.
     */
    fun shareCardBitmap(context: Context, bitmap: Bitmap, title: String) {
        try {
            val cachePath = File(context.cacheDir, "shared_cards")
            cachePath.mkdirs()
            val file = File(cachePath, "card_${System.currentTimeMillis()}.png")
            val fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()

            val authority = "${context.packageName}.fileprovider"
            val uri = FileProvider.getUriForFile(context, authority, file)

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, title)
                putExtra(Intent.EXTRA_TEXT, "தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள் முன்னேற்ற சங்கம் - அதிகாரப்பூர்வ அடையாள அட்டை: $title")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(shareIntent, "அடையாள அட்டையை பகிரவும் (Share ID Card)"))
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "பகிர்வதில் பிழை: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
