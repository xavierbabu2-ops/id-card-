package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

object PassportPhotoCropper {

    /**
     * Crops the captured camera image specifically to standard Passport Photo proportions (3:4 ratio),
     * focusing on the center portrait area and removing background clutter.
     * Also corrects EXIF orientation if needed.
     */
    fun cropToPassportFrame(
        context: Context,
        sourceUri: Uri,
        aspectRatio: String = "3:4" // "3:4" or "1:1"
    ): Uri {
        return try {
            val orientation = getExifOrientation(context, sourceUri)

            val inputStream: InputStream = context.contentResolver.openInputStream(sourceUri) ?: return sourceUri
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) return sourceUri

            // Correct rotation if camera rotated the picture
            val rotatedBitmap = if (orientation != 0) {
                val matrix = Matrix().apply { postRotate(orientation.toFloat()) }
                val rotated = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                if (rotated != originalBitmap) {
                    originalBitmap.recycle()
                }
                rotated
            } else {
                originalBitmap
            }

            val cropped = cropBitmapToRatio(rotatedBitmap, aspectRatio)
            if (cropped != rotatedBitmap) {
                rotatedBitmap.recycle()
            }
            saveCroppedBitmap(context, cropped)
        } catch (e: Exception) {
            e.printStackTrace()
            sourceUri
        }
    }

    /**
     * Directly crops a Bitmap to Passport proportions (3:4) or Square (1:1)
     * Focuses on the center-upper portion where member face/portrait is located.
     */
    fun cropBitmapToRatio(bitmap: Bitmap, aspectRatio: String = "3:4"): Bitmap {
        val targetRatio = if (aspectRatio == "1:1") 1.0f else 0.75f // 3:4 = 0.75
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        val currentRatio = originalWidth.toFloat() / originalHeight.toFloat()

        val cropWidth: Int
        val cropHeight: Int
        val startX: Int
        val startY: Int

        if (currentRatio > targetRatio) {
            // Original is wider than passport frame -> crop sides, keep center
            cropHeight = (originalHeight * 0.88f).toInt()
            cropWidth = (cropHeight * targetRatio).toInt()
            startX = (originalWidth - cropWidth) / 2
            startY = (originalHeight - cropHeight) / 2
        } else {
            // Original is taller than passport frame -> crop top/bottom, keep upper center for portrait face
            cropWidth = (originalWidth * 0.88f).toInt()
            cropHeight = (cropWidth / targetRatio).toInt()
            startX = (originalWidth - cropWidth) / 2
            // Position slightly higher (30% from top) so face is centered
            startY = max(0, ((originalHeight - cropHeight) * 0.30f).toInt())
        }

        val safeX = max(0, min(startX, originalWidth - cropWidth))
        val safeY = max(0, min(startY, originalHeight - cropHeight))
        val safeWidth = min(cropWidth, originalWidth - safeX)
        val safeHeight = min(cropHeight, originalHeight - safeY)

        val cropped = Bitmap.createBitmap(bitmap, safeX, safeY, safeWidth, safeHeight)

        // Standardize output to 600 x 800 (or 600 x 600) for sharp passport quality
        val outputWidth = 600
        val outputHeight = if (aspectRatio == "1:1") 600 else 800
        val scaled = Bitmap.createScaledBitmap(cropped, outputWidth, outputHeight, true)
        if (scaled != cropped) {
            cropped.recycle()
        }
        return scaled
    }

    private fun getExifOrientation(context: Context, uri: Uri): Int {
        return try {
            val input = context.contentResolver.openInputStream(uri) ?: return 0
            val exif = ExifInterface(input)
            val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
            input.close()
            when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            0
        }
    }

    private fun saveCroppedBitmap(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "passport_crop_${System.currentTimeMillis()}.jpg")
        val outStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outStream)
        outStream.flush()
        outStream.close()
        return Uri.fromFile(file)
    }
}
