package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.graphics.PointF
import android.media.ExifInterface
import android.media.FaceDetector
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.max
import kotlin.math.min

object PassportPhotoCropper {

    enum class CropPreset {
        AUTO_FACE,          // AI / Face detector crop
        FORM_TOP_RIGHT,     // Application form photo box (Standard Tamil Nadu union/govt form top-right)
        AADHAAR_LEFT,       // Aadhaar card left photo box
        CENTER_VIEWFINDER   // Center 50% framing
    }

    /**
     * Loads a bitmap from Uri with automatic EXIF rotation handling.
     */
    fun loadOrientedBitmap(context: Context, sourceUri: Uri): Bitmap? {
        return try {
            val orientation = getExifOrientation(context, sourceUri)
            val inputStream: InputStream = context.contentResolver.openInputStream(sourceUri) ?: return null
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()
            if (originalBitmap == null) return null

            if (orientation != 0) {
                val matrix = Matrix().apply { postRotate(orientation.toFloat()) }
                val rotated = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.width, originalBitmap.height, matrix, true)
                if (rotated != originalBitmap) {
                    originalBitmap.recycle()
                }
                rotated
            } else {
                originalBitmap
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Crops image to standard Passport Photo proportions (3:4 ratio).
     * 1. First tries AI Face Detection to automatically isolate just the member's photo.
     * 2. If face detection fails and image is document-like (portrait A4/letter), checks top-right photo box.
     * 3. Fallback: Viewfinder crop so application paper borders are excluded.
     */
    fun cropToPassportFrame(
        context: Context,
        sourceUri: Uri,
        aspectRatio: String = "3:4",
        forceTightCrop: Boolean = true
    ): Uri {
        return try {
            val rotatedBitmap = loadOrientedBitmap(context, sourceUri) ?: return sourceUri

            // 1. Try multi-resolution face detection
            val faceCropped = detectAndCropFace(rotatedBitmap, aspectRatio)
            val finalCropped = if (faceCropped != null) {
                if (rotatedBitmap != faceCropped) {
                    rotatedBitmap.recycle()
                }
                faceCropped
            } else {
                // 2. Fallback: If height > width * 1.25 (document ratio), crop the top-right photo box where photos are affixed
                if (rotatedBitmap.height > rotatedBitmap.width * 1.25f) {
                    val formPhoto = cropPresetRegion(rotatedBitmap, CropPreset.FORM_TOP_RIGHT, aspectRatio)
                    if (rotatedBitmap != formPhoto) rotatedBitmap.recycle()
                    formPhoto
                } else {
                    val tightCrop = cropBitmapToViewfinder(rotatedBitmap, aspectRatio, if (forceTightCrop) 0.52f else 0.70f)
                    if (rotatedBitmap != tightCrop) rotatedBitmap.recycle()
                    tightCrop
                }
            }

            saveCroppedBitmap(context, finalCropped)
        } catch (e: Exception) {
            e.printStackTrace()
            sourceUri
        }
    }

    /**
     * Extracts member face photo from an entire Aadhaar card or scanned application document.
     */
    fun extractFaceFromDocument(context: Context, sourceUri: Uri): Uri? {
        return try {
            val rotated = loadOrientedBitmap(context, sourceUri) ?: return null

            // 1. Try face detection first
            val faceCropped = detectAndCropFace(rotated, "3:4")
            if (faceCropped != null) {
                if (rotated != faceCropped) rotated.recycle()
                return saveCroppedBitmap(context, faceCropped)
            }

            // 2. If it's a document or Aadhaar card:
            val isAadhaarLike = (rotated.width.toFloat() / rotated.height.toFloat()) in 1.2f..1.8f
            val presetCrop = if (isAadhaarLike) {
                cropPresetRegion(rotated, CropPreset.AADHAAR_LEFT, "3:4")
            } else if (rotated.height > rotated.width * 1.2f) {
                cropPresetRegion(rotated, CropPreset.FORM_TOP_RIGHT, "3:4")
            } else {
                cropBitmapToViewfinder(rotated, "3:4", 0.50f)
            }

            if (rotated != presetCrop) rotated.recycle()
            saveCroppedBitmap(context, presetCrop)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Crops image using specific preset regions (Form Top-Right, Aadhaar Left, Center)
     */
    fun cropPresetRegion(
        bitmap: Bitmap,
        preset: CropPreset,
        aspectRatio: String = "3:4"
    ): Bitmap {
        val targetRatio = if (aspectRatio == "1:1") 1.0f else 0.75f // width / height

        return when (preset) {
            CropPreset.FORM_TOP_RIGHT -> {
                // Application form photo box is typically affixed at top-right
                // X: 58% to 94%, Y: 5% to 32%
                val cropWidth = (bitmap.width * 0.35f).toInt()
                val cropHeight = (cropWidth / targetRatio).toInt()
                val startX = min(bitmap.width - cropWidth, (bitmap.width * 0.60f).toInt())
                val startY = min(bitmap.height - cropHeight, (bitmap.height * 0.06f).toInt())
                cropAndScale(bitmap, startX, startY, cropWidth, cropHeight, targetRatio)
            }
            CropPreset.AADHAAR_LEFT -> {
                // Aadhaar card member photo is on the left side
                // X: 5% to 40%, Y: 20% to 75%
                val cropWidth = (bitmap.width * 0.32f).toInt()
                val cropHeight = (cropWidth / targetRatio).toInt()
                val startX = (bitmap.width * 0.05f).toInt()
                val startY = min(bitmap.height - cropHeight, (bitmap.height * 0.22f).toInt())
                cropAndScale(bitmap, startX, startY, cropWidth, cropHeight, targetRatio)
            }
            CropPreset.AUTO_FACE -> {
                detectAndCropFace(bitmap, aspectRatio) ?: cropBitmapToViewfinder(bitmap, aspectRatio, 0.52f)
            }
            CropPreset.CENTER_VIEWFINDER -> {
                cropBitmapToViewfinder(bitmap, aspectRatio, 0.52f)
            }
        }
    }

    /**
     * Crops a user-specified normalized bounding box from the image.
     * Normalized coordinates (0.0f .. 1.0f) relative to the source image.
     */
    fun cropCustomNormalizedRect(
        context: Context,
        sourceUri: Uri,
        centerXNorm: Float,
        centerYNorm: Float,
        zoomScale: Float = 1.0f,
        aspectRatio: String = "3:4"
    ): Uri {
        return try {
            val bitmap = loadOrientedBitmap(context, sourceUri) ?: return sourceUri
            val targetRatio = if (aspectRatio == "1:1") 1.0f else 0.75f

            // Base crop width: at 1x zoom, takes 45% of width; with zoomScale > 1, takes tighter region
            val baseWidthRatio = 0.45f / max(0.5f, zoomScale)
            val cropWidth = (bitmap.width * baseWidthRatio).toInt().coerceIn(100, bitmap.width)
            val cropHeight = (cropWidth / targetRatio).toInt().coerceIn(100, bitmap.height)

            val centerX = (bitmap.width * centerXNorm).toInt()
            val centerY = (bitmap.height * centerYNorm).toInt()

            val startX = (centerX - cropWidth / 2).coerceIn(0, max(0, bitmap.width - cropWidth))
            val startY = (centerY - cropHeight / 2).coerceIn(0, max(0, bitmap.height - cropHeight))

            val cropped = cropAndScale(bitmap, startX, startY, cropWidth, cropHeight, targetRatio)
            if (bitmap != cropped) {
                bitmap.recycle()
            }
            saveCroppedBitmap(context, cropped)
        } catch (e: Exception) {
            e.printStackTrace()
            sourceUri
        }
    }

    /**
     * Multi-scale Face Detector: detects faces even on printed cards, documents, and paper forms.
     */
    fun detectAndCropFace(bitmap: Bitmap, aspectRatio: String = "3:4"): Bitmap? {
        val targetRatio = if (aspectRatio == "1:1") 1.0f else 0.75f

        // Try detecting at 2 different resolutions for better sensitivity on small photos
        val resolutions = listOf(1000, 1500)
        for (maxDim in resolutions) {
            val result = tryDetectFaceAtScale(bitmap, maxDim, targetRatio)
            if (result != null) return result
        }
        return null
    }

    private fun tryDetectFaceAtScale(bitmap: Bitmap, maxDimension: Int, targetRatio: Float): Bitmap? {
        return try {
            val evenWidth = if (bitmap.width % 2 == 0) bitmap.width else bitmap.width - 1
            val evenHeight = bitmap.height

            val scaleFactor = if (max(evenWidth, evenHeight) > maxDimension) {
                maxDimension.toFloat() / max(evenWidth, evenHeight).toFloat()
            } else {
                1.0f
            }

            val detectionWidth = ((evenWidth * scaleFactor).toInt() / 2) * 2
            val detectionHeight = (evenHeight * scaleFactor).toInt()

            val scaledBitmap = if (scaleFactor < 1.0f) {
                Bitmap.createScaledBitmap(bitmap, detectionWidth, detectionHeight, true)
            } else {
                bitmap
            }

            val rgb565 = scaledBitmap.copy(Bitmap.Config.RGB_565, true)
            if (scaledBitmap != bitmap) {
                scaledBitmap.recycle()
            }

            val detector = FaceDetector(rgb565.width, rgb565.height, 1)
            val faces = arrayOfNulls<FaceDetector.Face>(1)
            val facesFound = detector.findFaces(rgb565, faces)
            rgb565.recycle()

            if (facesFound > 0 && faces[0] != null) {
                val face = faces[0]!!
                val midPointScaled = PointF()
                face.getMidPoint(midPointScaled)

                val midX = midPointScaled.x / scaleFactor
                val midY = midPointScaled.y / scaleFactor
                val eyeDistance = face.eyesDistance() / scaleFactor

                // Passport margins: Width is ~2.8x eye distance, Height is ~3.7x eye distance
                val cropWidth = (eyeDistance * 2.8f).toInt()
                val cropHeight = (cropWidth / targetRatio).toInt()

                val startX = (midX - cropWidth / 2f).toInt()
                val startY = (midY - cropHeight * 0.40f).toInt()

                val safeX = max(0, min(startX, bitmap.width - 50))
                val safeY = max(0, min(startY, bitmap.height - 50))
                val safeWidth = min(cropWidth, bitmap.width - safeX)
                val safeHeight = min(cropHeight, bitmap.height - safeY)

                if (safeWidth > 80 && safeHeight > 80) {
                    return cropAndScale(bitmap, safeX, safeY, safeWidth, safeHeight, targetRatio)
                }
            }
            null
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Crops bitmap to the exact central viewfinder box.
     */
    fun cropBitmapToViewfinder(
        bitmap: Bitmap,
        aspectRatio: String = "3:4",
        viewfinderWidthPercent: Float = 0.52f
    ): Bitmap {
        val targetRatio = if (aspectRatio == "1:1") 1.0f else 0.75f
        val originalWidth = bitmap.width
        val originalHeight = bitmap.height

        val cropWidth = (originalWidth * viewfinderWidthPercent).toInt()
        val cropHeight = (cropWidth / targetRatio).toInt()

        val startX = (originalWidth - cropWidth) / 2
        val startY = max(0, (originalHeight - cropHeight) / 2 - (originalHeight * 0.04f).toInt())

        val safeX = max(0, min(startX, originalWidth - cropWidth))
        val safeY = max(0, min(startY, originalHeight - cropHeight))
        val safeWidth = min(cropWidth, originalWidth - safeX)
        val safeHeight = min(cropHeight, originalHeight - safeY)

        return cropAndScale(bitmap, safeX, safeY, safeWidth, safeHeight, targetRatio)
    }

    private fun cropAndScale(bitmap: Bitmap, x: Int, y: Int, width: Int, height: Int, targetRatio: Float): Bitmap {
        val safeX = x.coerceIn(0, bitmap.width - 1)
        val safeY = y.coerceIn(0, bitmap.height - 1)
        val safeW = width.coerceIn(10, bitmap.width - safeX)
        val safeH = height.coerceIn(10, bitmap.height - safeY)

        val cropped = Bitmap.createBitmap(bitmap, safeX, safeY, safeW, safeH)
        val outWidth = 600
        val outHeight = (outWidth / targetRatio).toInt()
        val scaled = Bitmap.createScaledBitmap(cropped, outWidth, outHeight, true)
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

    fun saveCroppedBitmap(context: Context, bitmap: Bitmap): Uri {
        val file = File(context.cacheDir, "passport_crop_${System.currentTimeMillis()}.jpg")
        val outStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 95, outStream)
        outStream.flush()
        outStream.close()
        return Uri.fromFile(file)
    }
}
