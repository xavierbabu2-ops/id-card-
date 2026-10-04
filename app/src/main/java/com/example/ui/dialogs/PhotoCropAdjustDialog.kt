package com.example.ui.dialogs

import android.graphics.Bitmap
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.PassportPhotoCropper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Interactive Dialog to crop and extract passport photos from Application Forms,
 * Aadhaar cards, or camera/gallery portraits.
 * Prevents the entire application form from ever becoming the member photo.
 */
@Composable
fun PhotoCropAdjustDialog(
    sourceUri: Uri,
    photoAspectRatio: String = "3:4",
    onCropConfirmed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var originalBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var previewCroppedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    // Normalized center coordinates (0.0 .. 1.0)
    var centerX by remember { mutableFloatStateOf(0.72f) }
    var centerY by remember { mutableFloatStateOf(0.20f) }
    var zoomScale by remember { mutableFloatStateOf(1.5f) }
    var selectedPreset by remember { mutableStateOf(PassportPhotoCropper.CropPreset.FORM_TOP_RIGHT) }

    // Load original bitmap and apply initial auto-detection
    LaunchedEffect(sourceUri) {
        isLoading = true
        withContext(Dispatchers.IO) {
            val bmp = PassportPhotoCropper.loadOrientedBitmap(context, sourceUri)
            originalBitmap = bmp

            if (bmp != null) {
                // If it looks like an Aadhaar card (landscape ratio ~1.5x)
                val isAadhaar = (bmp.width.toFloat() / bmp.height.toFloat()) in 1.25f..1.8f
                // If it looks like an A4 document (portrait ratio > 1.2x)
                val isDocument = bmp.height > bmp.width * 1.2f

                if (isAadhaar) {
                    selectedPreset = PassportPhotoCropper.CropPreset.AADHAAR_LEFT
                    centerX = 0.22f
                    centerY = 0.45f
                    zoomScale = 1.4f
                } else if (isDocument) {
                    selectedPreset = PassportPhotoCropper.CropPreset.FORM_TOP_RIGHT
                    centerX = 0.74f
                    centerY = 0.18f
                    zoomScale = 1.6f
                } else {
                    // Try auto face
                    val faceBmp = PassportPhotoCropper.detectAndCropFace(bmp, photoAspectRatio)
                    if (faceBmp != null) {
                        selectedPreset = PassportPhotoCropper.CropPreset.AUTO_FACE
                        centerX = 0.50f
                        centerY = 0.45f
                        zoomScale = 1.2f
                    } else {
                        selectedPreset = PassportPhotoCropper.CropPreset.CENTER_VIEWFINDER
                        centerX = 0.50f
                        centerY = 0.50f
                        zoomScale = 1.0f
                    }
                }
            }
        }
        isLoading = false
    }

    // Update real-time cropped preview bitmap whenever position or zoom changes
    LaunchedEffect(centerX, centerY, zoomScale, originalBitmap) {
        val src = originalBitmap ?: return@LaunchedEffect
        withContext(Dispatchers.Default) {
            try {
                val targetRatio = if (photoAspectRatio == "1:1") 1.0f else 0.75f
                val baseWidthRatio = 0.45f / kotlin.math.max(0.4f, zoomScale)
                val cropWidth = (src.width * baseWidthRatio).toInt().coerceIn(60, src.width)
                val cropHeight = (cropWidth / targetRatio).toInt().coerceIn(60, src.height)

                val cX = (src.width * centerX).toInt()
                val cY = (src.height * centerY).toInt()

                val startX = (cX - cropWidth / 2).coerceIn(0, kotlin.math.max(0, src.width - cropWidth))
                val startY = (cY - cropHeight / 2).coerceIn(0, kotlin.math.max(0, src.height - cropHeight))

                val safeW = cropWidth.coerceIn(10, src.width - startX)
                val safeH = cropHeight.coerceIn(10, src.height - startY)

                val sub = Bitmap.createBitmap(src, startX, startY, safeW, safeH)
                val preview = Bitmap.createScaledBitmap(sub, 240, 320, true)
                if (preview != sub) sub.recycle()
                previewCroppedBitmap = preview
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("photo_crop_adjust_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, tint = UnionRed, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "போட்டோவை மட்டும் செதுக்குக",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "படிவத்தின் முழு தாள் வராமல் பாஸ்போர்ட் படத்தை மட்டும் தேர்ந்தெடுக்கவும்",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = UnionRed)
                    }
                } else {
                    // Visual Preview Box (Shows the cropped passport photo as it will look on the ID card)
                    Card(
                        modifier = Modifier
                            .size(120.dp, 160.dp)
                            .border(2.5.dp, UnionRed, RoundedCornerShape(10.dp)),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            val bmp = previewCroppedBitmap
                            if (bmp != null) {
                                Image(
                                    bitmap = bmp.asImageBitmap(),
                                    contentDescription = "Cropped Photo Preview",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(36.dp))
                            }

                            // Watermark / Badge
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth()
                                    .background(Color(0xCC000000))
                                    .padding(vertical = 2.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "3:4 பாஸ்போர்ட் படம்",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "✓ அட்டைக்கு இந்த பாஸ்போர்ட் போட்டோ மட்டும் இணைக்கப்படும்",
                        fontSize = 11.sp,
                        color = UnionGreen,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Preset Buttons (One-tap smart selection)
                    Text(
                        text = "தானியங்கி தேர்வு (Smart Presets):",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Preset 1: Application Form Top Right
                        FilterChip(
                            selected = selectedPreset == PassportPhotoCropper.CropPreset.FORM_TOP_RIGHT,
                            onClick = {
                                selectedPreset = PassportPhotoCropper.CropPreset.FORM_TOP_RIGHT
                                centerX = 0.74f
                                centerY = 0.18f
                                zoomScale = 1.6f
                            },
                            label = { Text("📋 படிவம் (Top-Right)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFE4E6),
                                selectedLabelColor = UnionRed
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Preset 2: Aadhaar Card Photo
                        FilterChip(
                            selected = selectedPreset == PassportPhotoCropper.CropPreset.AADHAAR_LEFT,
                            onClick = {
                                selectedPreset = PassportPhotoCropper.CropPreset.AADHAAR_LEFT
                                centerX = 0.22f
                                centerY = 0.45f
                                zoomScale = 1.4f
                            },
                            label = { Text("🪪 ஆதார் (Left)", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFE4E6),
                                selectedLabelColor = UnionRed
                            ),
                            modifier = Modifier.weight(1f)
                        )

                        // Preset 3: Center
                        FilterChip(
                            selected = selectedPreset == PassportPhotoCropper.CropPreset.CENTER_VIEWFINDER,
                            onClick = {
                                selectedPreset = PassportPhotoCropper.CropPreset.CENTER_VIEWFINDER
                                centerX = 0.50f
                                centerY = 0.48f
                                zoomScale = 1.2f
                            },
                            label = { Text("🎯 மையம்", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFE4E6),
                                selectedLabelColor = UnionRed
                            ),
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Zoom Adjustment Slider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.ZoomOut, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("பெரிதாக்கு (Zoom):", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                                Text("${String.format("%.1f", zoomScale)}x", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = UnionRed)
                            }
                            Slider(
                                value = zoomScale,
                                onValueChange = { zoomScale = it },
                                valueRange = 0.8f..3.2f,
                                colors = SliderDefaults.colors(
                                    thumbColor = UnionRed,
                                    activeTrackColor = UnionRed
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(Icons.Default.ZoomIn, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }

                    // Position Controls (Up / Down / Left / Right)
                    Text(
                        text = "போட்டோ நிலையை நகர்த்தவும் (Fine-tune Position):",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { centerX = (centerX - 0.05f).coerceIn(0.1f, 0.9f) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Move Left", tint = Color(0xFF334155))
                        }
                        Spacer(modifier = Modifier.width(10.dp))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            IconButton(
                                onClick = { centerY = (centerY - 0.05f).coerceIn(0.1f, 0.9f) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape)
                            ) {
                                Icon(Icons.Default.ArrowUpward, contentDescription = "Move Up", tint = Color(0xFF334155))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            IconButton(
                                onClick = { centerY = (centerY + 0.05f).coerceIn(0.1f, 0.9f) },
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFFF1F5F9), CircleShape)
                            ) {
                                Icon(Icons.Default.ArrowDownward, contentDescription = "Move Down", tint = Color(0xFF334155))
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        IconButton(
                            onClick = { centerX = (centerX + 0.05f).coerceIn(0.1f, 0.9f) },
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFF1F5F9), CircleShape)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Move Right", tint = Color(0xFF334155))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Confirm Button
                Button(
                    onClick = {
                        scope.launch {
                            val croppedUri = withContext(Dispatchers.IO) {
                                PassportPhotoCropper.cropCustomNormalizedRect(
                                    context = context,
                                    sourceUri = sourceUri,
                                    centerXNorm = centerX,
                                    centerYNorm = centerY,
                                    zoomScale = zoomScale,
                                    aspectRatio = photoAspectRatio
                                )
                            }
                            Toast.makeText(context, "பாஸ்போர்ட் புகைப்படம் வெற்றிகரமாக செதுக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
                            onCropConfirmed(croppedUri.toString())
                            onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_confirm_crop_photo")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "✓ இந்த போட்டோவை மட்டும் அட்டையில் சேர்",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ரத்து செய் (Cancel)", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
