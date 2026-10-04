package com.example.util

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object AadhaarOcrEngine {

    private val recognizer by lazy {
        TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    }

    suspend fun recognizeTextFromBitmap(bitmap: Bitmap): String = suspendCancellableCoroutine { cont ->
        try {
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (cont.isActive) cont.resume(visionText.text)
                }
                .addOnFailureListener { ex ->
                    ex.printStackTrace()
                    if (cont.isActive) cont.resume("")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            if (cont.isActive) cont.resume("")
        }
    }

    suspend fun recognizeTextFromUri(context: Context, uri: Uri): String = suspendCancellableCoroutine { cont ->
        try {
            val image = InputImage.fromFilePath(context, uri)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (cont.isActive) cont.resume(visionText.text)
                }
                .addOnFailureListener { ex ->
                    ex.printStackTrace()
                    if (cont.isActive) cont.resume("")
                }
        } catch (e: Exception) {
            e.printStackTrace()
            if (cont.isActive) cont.resume("")
        }
    }
}
