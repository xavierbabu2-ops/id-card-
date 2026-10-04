package com.example.ui.dialogs

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed

@Composable
fun PhotoSourceSelectionDialog(
    card: MemberCardEntity,
    autoCropPhoto: Boolean = true,
    photoAspectRatio: String = "3:4",
    onPhotoSelected: (String?) -> Unit,
    onOpenCamera: () -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    // If user picks an image from gallery or wants to adjust current photo, open PhotoCropAdjustDialog
    var pendingCropUri by remember { mutableStateOf<Uri?>(null) }

    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            pendingCropUri = uri
        }
    }

    if (pendingCropUri != null) {
        PhotoCropAdjustDialog(
            sourceUri = pendingCropUri!!,
            photoAspectRatio = photoAspectRatio,
            onCropConfirmed = { croppedUriString ->
                onPhotoSelected(croppedUriString)
                pendingCropUri = null
                onDismiss()
            },
            onDismiss = { pendingCropUri = null }
        )
        return
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp)
                .testTag("photo_source_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
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
                            Icon(Icons.Default.CameraAlt, contentDescription = null, tint = UnionRed, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "உறுப்பினர் புகைப்படம்",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Current Photo Preview
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CardPhotoFrame(
                        photoUri = card.photoUri,
                        avatarPreset = 0,
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        borderColor = UnionRed,
                        borderWidth = 1.5.dp
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = card.name, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                        Text(
                            text = if (card.photoUri != null) "புகைப்படம் இணைக்கப்பட்டுள்ளது" else "புகைப்படம் இன்னும் சேர்க்கப்படவில்லை",
                            fontSize = 10.5.sp,
                            color = Color(0xFF64748B)
                        )
                    }

                    if (card.photoUri != null) {
                        OutlinedButton(
                            onClick = {
                                try {
                                    pendingCropUri = Uri.parse(card.photoUri)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        ) {
                            Icon(Icons.Default.Crop, contentDescription = null, tint = UnionRed, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("செதுக்கு", fontSize = 10.5.sp, color = UnionRed, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Option 1: Live Camera
                PhotoOptionButton(
                    icon = Icons.Default.CameraAlt,
                    iconBg = Color(0xFFFFE4E6),
                    iconColor = UnionRed,
                    title = "நேரடி கேமரா - பாஸ்போர்ட் போட்டோ (Live Camera)",
                    desc = "உறுப்பினரை நேரில் வைத்து பாஸ்போர்ட் புகைப்படம் எடுக்க",
                    onClick = {
                        onDismiss()
                        onOpenCamera()
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 2: Document / Application Form Cropper
                PhotoOptionButton(
                    icon = Icons.Default.Description,
                    iconBg = Color(0xFFDCFCE7),
                    iconColor = UnionGreen,
                    title = "விண்ணப்ப படிவம் / ஆவணத்திலிருந்து போட்டோ (From Form)",
                    desc = "முழு படிவ தாள் வராமல் பாஸ்போர்ட் படத்தை மட்டும் செதுக்க",
                    onClick = {
                        galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Option 3: Gallery Picker
                PhotoOptionButton(
                    icon = Icons.Default.PhotoLibrary,
                    iconBg = Color(0xFFEFF6FF),
                    iconColor = UnionNavy,
                    title = "மொபைல் கேலரியில் இருந்து தேர்வு (Gallery)",
                    desc = "சேமிக்கப்பட்ட புகைப்படத்தை தேர்ந்தெடுக்க",
                    onClick = {
                        galleryPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                    }
                )

                if (onOpenSettings != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    PhotoOptionButton(
                        icon = Icons.Default.Settings,
                        iconBg = Color(0xFFFEF3C7),
                        iconColor = Color(0xFFB45309),
                        title = "செதுக்குதல் அமைப்புகள் (Crop Settings)",
                        desc = if (autoCropPhoto) "பாஸ்போர்ட் செதுக்குதல்: இயக்கத்தில் ($photoAspectRatio)" else "செதுக்குதல் முடக்கத்தில் உள்ளது",
                        onClick = {
                            onDismiss()
                            onOpenSettings()
                        }
                    )
                }

                if (card.photoUri != null) {
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            onPhotoSelected(null)
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("படத்தை நீக்குக (Remove Photo)", color = Color.Red, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotoOptionButton(
    icon: ImageVector,
    iconBg: Color,
    iconColor: Color,
    title: String,
    desc: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B)
                )
                Text(
                    text = desc,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B)
                )
            }
        }
    }
}
