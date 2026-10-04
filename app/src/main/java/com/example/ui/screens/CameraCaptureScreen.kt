package com.example.ui.screens

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FlashAuto
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.FlipCameraAndroid
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import com.example.ui.dialogs.PhotoCropAdjustDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.data.MemberCardEntity
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.AadhaarOcrEngine
import com.example.util.AadhaarOcrParser
import com.example.util.AadhaarOcrResult
import com.example.util.PassportPhotoCropper
import com.example.util.TamilAadhaarTransliterationHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.Executors

enum class CameraMode {
    MEMBER_PHOTO,
    AADHAAR_SCAN
}

@Composable
fun CameraCaptureScreen(
    currentCard: MemberCardEntity,
    initialMode: CameraMode = CameraMode.MEMBER_PHOTO,
    initialAutoCropPhoto: Boolean = true,
    initialPhotoAspectRatio: String = "3:4",
    initialAadhaarInTamil: Boolean = true,
    onPhotoCaptured: (String) -> Unit,
    onAadhaarScanned: (MemberCardEntity) -> Unit,
    onSettingsChange: ((Boolean, String, Boolean) -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    onClose: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val scope = rememberCoroutineScope()

    var cameraMode by remember { mutableStateOf(initialMode) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }
    var flashMode by remember { mutableIntStateOf(ImageCapture.FLASH_MODE_AUTO) }
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    var imageCapture: ImageCapture? by remember { mutableStateOf(null) }
    var isProcessing by remember { mutableStateOf(false) }
    var processingMessage by remember { mutableStateOf("") }
    var extractedOcrResult by remember { mutableStateOf<AadhaarOcrResult?>(null) }

    var autoCropPhoto by remember { mutableStateOf(initialAutoCropPhoto) }
    var photoAspectRatio by remember { mutableStateOf(initialPhotoAspectRatio) } // "3:4" or "1:1"
    var aadhaarInTamil by remember { mutableStateOf(initialAadhaarInTamil) }
    var isDocumentCropMode by remember { mutableStateOf(true) }
    var zoomLevel by remember { mutableFloatStateOf(1f) }
    var cameraControlInstance by remember { mutableStateOf<androidx.camera.core.CameraControl?>(null) }
    var pendingCropUri by remember { mutableStateOf<Uri?>(null) }

    // If an image was captured or picked for member photo, show interactive cropper
    if (pendingCropUri != null) {
        PhotoCropAdjustDialog(
            sourceUri = pendingCropUri!!,
            photoAspectRatio = photoAspectRatio,
            onCropConfirmed = { croppedUri ->
                onPhotoCaptured(croppedUri)
                pendingCropUri = null
                onClose()
            },
            onDismiss = { pendingCropUri = null }
        )
    }

    // Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
        if (!isGranted) {
            Toast.makeText(context, "கேமரா அனுமதி தேவை (Camera Permission Required)", Toast.LENGTH_LONG).show()
        }
    }

    // Gallery Picker Launcher
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            if (cameraMode == CameraMode.MEMBER_PHOTO) {
                pendingCropUri = uri
            } else {
                // Aadhaar Gallery OCR using real ML Kit Text Recognition
                isProcessing = true
                processingMessage = "ஆதார் கார்டு படம் ஸ்கேன் செய்யப்படுகிறது..."
                scope.launch {
                    val ocrText = AadhaarOcrEngine.recognizeTextFromUri(context, uri)
                    val faceUri = PassportPhotoCropper.extractFaceFromDocument(context, uri)
                    if (ocrText.isNotBlank()) {
                        val result = AadhaarOcrParser.parseAadhaarText(ocrText, currentCard.cardType)
                        val finalResult = if (aadhaarInTamil) {
                            result.copy(
                                name = TamilAadhaarTransliterationHelper.transliterateNameToTamil(result.name),
                                fatherName = TamilAadhaarTransliterationHelper.transliterateNameToTamil(result.fatherName),
                                gender = TamilAadhaarTransliterationHelper.translateGenderToTamil(result.gender),
                                district = TamilAadhaarTransliterationHelper.translateDistrictToTamil(result.district),
                                address = TamilAadhaarTransliterationHelper.convertAddressToTamil(result.address)
                            )
                        } else {
                            result
                        }
                        val updatedCard = AadhaarOcrParser.applyToMemberCard(currentCard, finalResult).let { card ->
                            if (faceUri != null) card.copy(photoUri = faceUri.toString()) else card
                        }
                        onAadhaarScanned(updatedCard)
                        Toast.makeText(context, if (faceUri != null) "ஆதார் விவரங்கள் & புகைப்படம் படிவத்தில் தானாக நிரப்பப்பட்டது!" else "ஆதார் அட்டை விவரங்கள் படிவத்தில் தானாக நிரப்பப்பட்டது!", Toast.LENGTH_LONG).show()
                        onClose()
                    } else {
                        Toast.makeText(context, "படத்தில் எழுத்துக்கள் தெளிவாக இல்லை. மீண்டும் படம் தேர்வு செய்யவும்.", Toast.LENGTH_LONG).show()
                    }
                    isProcessing = false
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .testTag("camera_capture_screen")
    ) {
        if (hasCameraPermission) {
            // CAMERAX PREVIEW VIEW
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    val previewView = PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }

                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                        val capture = ImageCapture.Builder()
                            .setFlashMode(flashMode)
                            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                            .build()
                        imageCapture = capture

                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(lensFacing)
                            .build()

                        try {
                            cameraProvider.unbindAll()
                            val cam = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                capture
                            )
                            cameraControlInstance = cam.cameraControl
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(ctx))

                    previewView
                }
            )

            // VIEW FINDER OVERLAY GUIDE (Oval for Portrait Photo, Rectangle for Aadhaar Card)
            Canvas(modifier = Modifier.fillMaxSize()) {
                val canvasWidth = size.width
                val canvasHeight = size.height

                if (cameraMode == CameraMode.MEMBER_PHOTO) {
                    // Portrait Passport Guide Frame (Adjusted based on isDocumentCropMode)
                    val ovalWidth = if (isDocumentCropMode) canvasWidth * 0.48f else canvasWidth * 0.62f
                    val ovalHeight = ovalWidth * (if (photoAspectRatio == "1:1") 1.0f else 1.33f)
                    val left = (canvasWidth - ovalWidth) / 2f
                    val top = (canvasHeight - ovalHeight) / 2f - 40f

                    drawRoundRect(
                        color = Color(0xFFFFD700),
                        topLeft = Offset(left, top),
                        size = Size(ovalWidth, ovalHeight),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Stroke(width = 4.5f)
                    )

                    // Corner Accent Marks for golden viewfinder
                    val markLength = 26f
                    val stroke = 6f
                    // Top-Left
                    drawLine(Color(0xFFFFD700), Offset(left, top), Offset(left + markLength, top), stroke)
                    drawLine(Color(0xFFFFD700), Offset(left, top), Offset(left, top + markLength), stroke)
                    // Top-Right
                    drawLine(Color(0xFFFFD700), Offset(left + ovalWidth, top), Offset(left + ovalWidth - markLength, top), stroke)
                    drawLine(Color(0xFFFFD700), Offset(left + ovalWidth, top), Offset(left + ovalWidth, top + markLength), stroke)
                    // Bottom-Left
                    drawLine(Color(0xFFFFD700), Offset(left, top + ovalHeight), Offset(left + markLength, top + ovalHeight), stroke)
                    drawLine(Color(0xFFFFD700), Offset(left, top + ovalHeight), Offset(left, top + ovalHeight - markLength), stroke)
                    // Bottom-Right
                    drawLine(Color(0xFFFFD700), Offset(left + ovalWidth, top + ovalHeight), Offset(left + ovalWidth - markLength, top + ovalHeight), stroke)
                    drawLine(Color(0xFFFFD700), Offset(left + ovalWidth, top + ovalHeight), Offset(left + ovalWidth, top + markLength), stroke)
                } else {
                    // Aadhaar Card Guide Frame (Standard Credit/Aadhaar aspect ratio 1.58f)
                    val cardWidth = canvasWidth * 0.88f
                    val cardHeight = cardWidth / 1.58f
                    val left = (canvasWidth - cardWidth) / 2f
                    val top = (canvasHeight - cardHeight) / 2f - 40f

                    drawRoundRect(
                        color = Color(0xFF10B981),
                        topLeft = Offset(left, top),
                        size = Size(cardWidth, cardHeight),
                        cornerRadius = CornerRadius(24f, 24f),
                        style = Stroke(width = 4.5f)
                    )

                    // Corner Accent Marks
                    val markLength = 30f
                    val stroke = 6f
                    // Top-Left
                    drawLine(Color(0xFF10B981), Offset(left, top), Offset(left + markLength, top), stroke)
                    drawLine(Color(0xFF10B981), Offset(left, top), Offset(left, top + markLength), stroke)
                    // Top-Right
                    drawLine(Color(0xFF10B981), Offset(left + cardWidth, top), Offset(left + cardWidth - markLength, top), stroke)
                    drawLine(Color(0xFF10B981), Offset(left + cardWidth, top), Offset(left + cardWidth, top + markLength), stroke)
                    // Bottom-Left
                    drawLine(Color(0xFF10B981), Offset(left, top + cardHeight), Offset(left + markLength, top + cardHeight), stroke)
                    drawLine(Color(0xFF10B981), Offset(left, top + cardHeight), Offset(left, top + cardHeight - markLength), stroke)
                    // Bottom-Right
                    drawLine(Color(0xFF10B981), Offset(left + cardWidth, top + cardHeight), Offset(left + cardWidth - markLength, top + cardHeight), stroke)
                    drawLine(Color(0xFF10B981), Offset(left + cardWidth, top + cardHeight), Offset(left + cardWidth, top + cardHeight - markLength), stroke)
                }
            }

            // TOP CONTROLS BAR (Close, Flash, Lens Switcher)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(28.dp))
                    }

                    // Mode Title
                    Text(
                        text = if (cameraMode == CameraMode.MEMBER_PHOTO) "உறுப்பினர் படம் எடுத்தல்" else "ஆதார் கார்டு ஸ்கேனர் (OCR)",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Settings shortcut
                        if (onOpenSettings != null) {
                            IconButton(onClick = onOpenSettings) {
                                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                            }
                        }

                        // Flash Toggle
                        IconButton(
                            onClick = {
                                flashMode = when (flashMode) {
                                    ImageCapture.FLASH_MODE_AUTO -> ImageCapture.FLASH_MODE_ON
                                    ImageCapture.FLASH_MODE_ON -> ImageCapture.FLASH_MODE_OFF
                                    else -> ImageCapture.FLASH_MODE_AUTO
                                }
                                imageCapture?.flashMode = flashMode
                            }
                        ) {
                            Icon(
                                imageVector = when (flashMode) {
                                    ImageCapture.FLASH_MODE_ON -> Icons.Default.FlashOn
                                    ImageCapture.FLASH_MODE_OFF -> Icons.Default.FlashOff
                                    else -> Icons.Default.FlashAuto
                                },
                                contentDescription = "Flash Mode",
                                tint = if (flashMode != ImageCapture.FLASH_MODE_OFF) Color(0xFFFFD700) else Color.White
                            )
                        }

                        // Switch Lens
                        IconButton(
                            onClick = {
                                lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
                                    CameraSelector.LENS_FACING_FRONT
                                } else {
                                    CameraSelector.LENS_FACING_BACK
                                }
                            }
                        ) {
                            Icon(Icons.Default.FlipCameraAndroid, contentDescription = "Switch Camera", tint = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Mode Selector Tabs (Photo vs Aadhaar Scan)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .padding(3.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (cameraMode == CameraMode.MEMBER_PHOTO) UnionRed else Color.Transparent)
                            .clickable { cameraMode = CameraMode.MEMBER_PHOTO }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("1. உறுப்பினர் படம்", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (cameraMode == CameraMode.AADHAAR_SCAN) UnionGreen else Color.Transparent)
                            .clickable { cameraMode = CameraMode.AADHAAR_SCAN }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("2. ஆதார் OCR ஸ்கேன்", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Mode-Specific Real-time Settings Bar
                if (cameraMode == CameraMode.MEMBER_PHOTO) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Crop, contentDescription = null, tint = Color(0xFFFFD700), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("பாஸ்போர்ட் செதுக்கல்:", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (autoCropPhoto) UnionGreen else Color.Gray)
                                    .clickable {
                                        val newAuto = !autoCropPhoto
                                        autoCropPhoto = newAuto
                                        onSettingsChange?.invoke(newAuto, photoAspectRatio, aadhaarInTamil)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = if (autoCropPhoto) "செதுக்கு (ON)" else "முழு படம் (OFF)",
                                    color = Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (autoCropPhoto) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (photoAspectRatio == "3:4") UnionRed else Color.DarkGray)
                                        .clickable {
                                            val newRatio = if (photoAspectRatio == "3:4") "1:1" else "3:4"
                                            photoAspectRatio = newRatio
                                            onSettingsChange?.invoke(autoCropPhoto, newRatio, aadhaarInTamil)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = photoAspectRatio,
                                        color = Color.White,
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Black.copy(alpha = 0.55f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF60A5FA), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ஆதார் மொழி (Language):", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (aadhaarInTamil) UnionRed else Color.DarkGray)
                                    .clickable {
                                        aadhaarInTamil = true
                                        onSettingsChange?.invoke(autoCropPhoto, photoAspectRatio, true)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("தமிழ் (Tamil)", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (!aadhaarInTamil) UnionNavy else Color.DarkGray)
                                    .clickable {
                                        aadhaarInTamil = false
                                        onSettingsChange?.invoke(autoCropPhoto, photoAspectRatio, false)
                                    }
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text("English", color = Color.White, fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // BOTTOM SHUTTER & CONTROLS
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.65f))
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (cameraMode == CameraMode.MEMBER_PHOTO) {
                    // Document Crop vs Live Portrait Mode Toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDocumentCropMode) UnionAmber else Color(0xFF334155))
                                .clickable {
                                    isDocumentCropMode = true
                                    zoomLevel = 2f
                                    cameraControlInstance?.setZoomRatio(2f)
                                }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Crop, contentDescription = null, tint = if (isDocumentCropMode) Color.Black else Color.White, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "விண்ணப்ப படிவ போட்டோ",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDocumentCropMode) Color.Black else Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (!isDocumentCropMode) UnionRed else Color(0xFF334155))
                                .clickable {
                                    isDocumentCropMode = false
                                    zoomLevel = 1f
                                    cameraControlInstance?.setZoomRatio(1f)
                                }
                                .padding(horizontal = 12.dp, vertical = 5.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Face, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    "நேரடி நபர் படம்",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Zoom Selector
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        listOf(1f to "1x", 1.5f to "1.5x", 2f to "2x (படிவம்)", 3f to "3x").forEach { (z, label) ->
                            val isSelected = zoomLevel == z
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(if (isSelected) UnionAmber else Color.Black.copy(alpha = 0.5f))
                                    .clickable {
                                        zoomLevel = z
                                        cameraControlInstance?.setZoomRatio(z)
                                    }
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.Black else Color.White,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Hint Text
                Text(
                    text = if (cameraMode == CameraMode.MEMBER_PHOTO) {
                        if (isDocumentCropMode) {
                            "விண்ணப்ப படிவம் / ஆதாரில் உள்ள போட்டோவை மட்டும் தங்க கட்டத்திற்குள் வைக்கவும் (ஆட்டோ ஃபேஸ் க்ராப்)"
                        } else {
                            "உறுப்பினரின் முகத்தை மஞ்சள் கட்டத்தில் வைத்து படம் எடுக்கவும்"
                        }
                    } else {
                        "ஆதார் கார்டை பச்சை கட்டத்திற்குள் வைத்து ஸ்கேன் செய்யவும்"
                    },
                    color = Color.White,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    // Gallery Picker Fallback
                    IconButton(
                        onClick = {
                            galleryPicker.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery", tint = Color.White, modifier = Modifier.size(26.dp))
                    }

                    // SHUTTER CAPTURE BUTTON
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(6.dp)
                            .clickable(enabled = !isProcessing) {
                                isProcessing = true
                                val executor = ContextCompat.getMainExecutor(context)

                                if (cameraMode == CameraMode.MEMBER_PHOTO) {
                                    processingMessage = "புகைப்படம் சேமிக்கப்படுகிறது..."
                                    val photoFile = File(context.cacheDir, "member_${System.currentTimeMillis()}.jpg")
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

                                    imageCapture?.takePicture(
                                        outputOptions,
                                        executor,
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                isProcessing = false
                                                val rawUri = Uri.fromFile(photoFile)
                                                pendingCropUri = rawUri
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                isProcessing = false
                                                Toast.makeText(context, "படம் எடுப்பதில் பிழை: ${exception.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) ?: run {
                                        scope.launch {
                                            delay(600)
                                            isProcessing = false
                                            Toast.makeText(context, "படம் எடுக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
                                            onClose()
                                        }
                                    }
                                } else {
                                    // AADHAAR CARD SCAN & REAL TEXT RECOGNITION (OCR)
                                    processingMessage = "ஆதார் கார்டு படம் எடுக்கப்பட்டு விவரங்கள் பெறப்படுகிறது..."
                                    val aadhaarFile = File(context.cacheDir, "aadhaar_${System.currentTimeMillis()}.jpg")
                                    val outputOptions = ImageCapture.OutputFileOptions.Builder(aadhaarFile).build()

                                    imageCapture?.takePicture(
                                        outputOptions,
                                        executor,
                                        object : ImageCapture.OnImageSavedCallback {
                                            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                                                scope.launch {
                                                    val uri = Uri.fromFile(aadhaarFile)
                                                    val ocrText = AadhaarOcrEngine.recognizeTextFromUri(context, uri)
                                                    val faceUri = PassportPhotoCropper.extractFaceFromDocument(context, uri)
                                                    if (ocrText.isNotBlank()) {
                                                        val ocrResult = AadhaarOcrParser.parseAadhaarText(ocrText, currentCard.cardType)
                                                        val finalResult = if (aadhaarInTamil) {
                                                            ocrResult.copy(
                                                                name = TamilAadhaarTransliterationHelper.transliterateNameToTamil(ocrResult.name),
                                                                fatherName = TamilAadhaarTransliterationHelper.transliterateNameToTamil(ocrResult.fatherName),
                                                                gender = TamilAadhaarTransliterationHelper.translateGenderToTamil(ocrResult.gender),
                                                                district = TamilAadhaarTransliterationHelper.translateDistrictToTamil(ocrResult.district),
                                                                address = TamilAadhaarTransliterationHelper.convertAddressToTamil(ocrResult.address)
                                                            )
                                                        } else {
                                                            ocrResult
                                                        }
                                                        val updatedCard = AadhaarOcrParser.applyToMemberCard(currentCard, finalResult).let { card ->
                                                            if (faceUri != null) card.copy(photoUri = faceUri.toString()) else card
                                                        }
                                                        onAadhaarScanned(updatedCard)
                                                        Toast.makeText(context, if (faceUri != null) "ஆதார் விவரங்கள் & புகைப்படம் படிவத்தில் தானாக நிரப்பப்பட்டது!" else "ஆதார் விவரங்கள் படிவத்தில் தானாக நிரப்பப்பட்டது!", Toast.LENGTH_LONG).show()
                                                        onClose()
                                                    } else {
                                                        Toast.makeText(context, "படத்தில் எழுத்துக்கள் தெளிவாக இல்லை. மீண்டும் நேராகப் படம் எடுக்கவும்.", Toast.LENGTH_LONG).show()
                                                    }
                                                    isProcessing = false
                                                }
                                            }

                                            override fun onError(exception: ImageCaptureException) {
                                                isProcessing = false
                                                Toast.makeText(context, "படம் எடுப்பதில் பிழை: ${exception.message}", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    ) ?: run {
                                        isProcessing = false
                                        Toast.makeText(context, "கேமரா இன்னும் தயாராகவில்லை. கேலரி மூலம் ஆதார் படத்தை தேர்வு செய்யவும்.", Toast.LENGTH_LONG).show()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(if (cameraMode == CameraMode.MEMBER_PHOTO) UnionRed else UnionGreen)
                        )
                    }

                    // 1-Click Fast Fill Sample
                    IconButton(
                        onClick = {
                            if (cameraMode == CameraMode.AADHAAR_SCAN) {
                                isProcessing = true
                                processingMessage = "ஆதார் மாதிரி தரவு ஸ்கேன் செய்யப்படுகிறது..."
                                scope.launch {
                                    delay(400)
                                    val parsed = AadhaarOcrParser.parseAadhaarText(
                                        "தமிழ்நாடு அரசு\nக. மாரிமுத்து\nDOB: 12/04/1988\nMale\nS/O: கந்தசாமி\n8, பாரதி தெரு, தாம்பரம், சென்னை 600045\nXXXX XXXX 2940",
                                        currentCard.cardType
                                    )
                                    val updatedCard = AadhaarOcrParser.applyToMemberCard(currentCard, parsed)
                                    onAadhaarScanned(updatedCard)
                                    isProcessing = false
                                    Toast.makeText(context, "மாதிரி ஆதார் விவரங்கள் படிவத்தில் தானாக நிரப்பப்பட்டது!", Toast.LENGTH_SHORT).show()
                                    onClose()
                                }
                            } else {
                                Toast.makeText(context, "கேமரா பொத்தானைத் தொட்டு படம் எடுக்கவும்", Toast.LENGTH_SHORT).show()
                            }
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f))
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Sample OCR", tint = Color(0xFFFFD700), modifier = Modifier.size(24.dp))
                    }
                }
            }
        } else {
            // CAMERA PERMISSION RATIONALE
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(UnionRed.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = UnionRed, modifier = Modifier.size(44.dp))
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "கேமரா அனுமதி தேவை",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "உறுப்பினர்களின் புகைப்படங்களை எடுக்கவும் மற்றும் ஆதார் அட்டைகளை ஸ்கேன் செய்து விவரங்களை தானாகப் பூர்த்தி செய்யவும் கேமரா அனுமதி அவசியமாகும்.",
                    color = Color(0xFFCBD5E1),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                    colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("அனுமதியை வழங்கவும் (Allow Camera)", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onClose,
                    modifier = Modifier.fillMaxWidth(0.8f)
                ) {
                    Text("பின்செல்ல (Back)", color = Color.White)
                }
            }
        }

        // PROCESSING INDICATOR OVERLAY
        if (isProcessing) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.75f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.padding(24.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = UnionRed, strokeWidth = 3.dp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = processingMessage,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // OCR EXTRACTED RESULT CONFIRMATION DIALOG
        if (extractedOcrResult != null) {
            val result = extractedOcrResult!!

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ocr_result_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text("ஆதார் ஸ்கேன் வெற்றி!", fontSize = 14.sp, fontWeight = FontWeight.Black, color = UnionGreen)
                                    Text("விவரங்கள் தானாகப் பெறப்பட்டன", fontSize = 11.sp, color = Color(0xFF64748B))
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(UnionRed)
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(result.generatedMemberId, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = Color(0xFFE2E8F0))
                        Spacer(modifier = Modifier.height(8.dp))

                        // Language Toggle on Result Dialog
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9))
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("மொழி (Language):", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (aadhaarInTamil) UnionRed else Color.White)
                                        .clickable {
                                            aadhaarInTamil = true
                                            extractedOcrResult = result.copy(
                                                name = TamilAadhaarTransliterationHelper.transliterateNameToTamil(result.name),
                                                fatherName = TamilAadhaarTransliterationHelper.transliterateNameToTamil(result.fatherName),
                                                district = TamilAadhaarTransliterationHelper.translateDistrictToTamil(result.district),
                                                address = TamilAadhaarTransliterationHelper.convertAddressToTamil(result.address)
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("தமிழ்", color = if (aadhaarInTamil) Color.White else Color(0xFF334155), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (!aadhaarInTamil) UnionNavy else Color.White)
                                        .clickable { aadhaarInTamil = false }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("English", color = if (!aadhaarInTamil) Color.White else Color(0xFF334155), fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OcrRow("உறுப்பினர் எண் (Auto ID)", result.generatedMemberId, isHighlight = true)
                        OcrRow("பெயர் (Name)", result.name)
                        OcrRow("தந்தை / கணவர்", result.fatherName)
                        OcrRow("வயது / பிறந்த தேதி", "${result.age} (${result.dob})")
                        OcrRow("மாவட்டம் (District)", result.district, isHighlight = true)
                        
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = result.address,
                            onValueChange = { newAddr ->
                                val detected = com.example.util.DistrictCodeHelper.detectDistrictFromText(newAddr)
                                val newId = com.example.util.DistrictCodeHelper.generateDistrictMemberId(detected, currentCard.cardType)
                                extractedOcrResult = result.copy(address = newAddr, district = detected, generatedMemberId = newId)
                            },
                            label = { Text("ஆதார் முகவரி (Edit Aadhaar Address)", fontSize = 10.sp) },
                            maxLines = 2,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OcrRow("ஆதார் எண்", result.aadhaarNumber)

                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "38 மாவட்டம் தேர்வு (Change to any 38 District):",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            Text(
                                text = "ஆரம்ப எண்: 0001",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnionRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        // Scrollable 38 Districts Row with District Codes
                        androidx.compose.foundation.lazy.LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(com.example.util.DistrictCodeHelper.ALL_38_DISTRICTS) { dist ->
                                val code = com.example.util.DistrictCodeHelper.getDistrictCode(dist)
                                val isSelected = result.district.contains(dist, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) UnionRed else Color(0xFFF1F5F9))
                                        .clickable {
                                            val newMemberId = com.example.util.DistrictCodeHelper.generateDistrictMemberId(dist, currentCard.cardType, 1)
                                            extractedOcrResult = result.copy(
                                                district = dist,
                                                generatedMemberId = newMemberId,
                                                address = if (result.address.contains(",")) result.address.substringBeforeLast(",") + ", " + dist else "$dist, தமிழ்நாடு"
                                            )
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = dist,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else Color(0xFF1E293B)
                                        )
                                        Text(
                                            text = code,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = if (isSelected) Color(0xFFFFE4E6) else Color(0xFF64748B)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                val updatedCard = AadhaarOcrParser.applyToMemberCard(currentCard, result)
                                onAadhaarScanned(updatedCard)
                                Toast.makeText(context, "அட்டை விவரங்கள் & எண் ${result.generatedMemberId} வெற்றிகரமாக இணைக்கப்பட்டது!", Toast.LENGTH_LONG).show()
                                onClose()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_confirm_ocr_data")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("படிவத்தில் தானாக பூர்த்தி செய் (Apply Details)", fontWeight = FontWeight.Bold, fontSize = 12.5.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { extractedOcrResult = null },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("மீண்டும் ஸ்கேன் செய்க (Rescan)", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OcrRow(
    label: String,
    value: String,
    isHighlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.5.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = if (isHighlight) FontWeight.Black else FontWeight.Bold,
            color = if (isHighlight) UnionRed else Color(0xFF1E293B)
        )
    }
}
