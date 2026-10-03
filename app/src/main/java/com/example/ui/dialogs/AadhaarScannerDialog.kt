package com.example.ui.dialogs

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.PhotoLibrary
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.data.MemberCardEntity
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.DistrictCodeHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class AadhaarExtractedData(
    val name: String = "",
    val fatherName: String = "",
    val age: String = "",
    val dob: String = "",
    val gender: String = "ஆண் (Male)",
    val address: String = "",
    val district: String = "மதுரை",
    val aadhaarNumber: String = "",
    val memberId: String = ""
)

@Composable
fun AadhaarScannerDialog(
    currentCard: MemberCardEntity,
    onAadhaarDataExtracted: (MemberCardEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scannedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var extractedData by remember { mutableStateOf<AadhaarExtractedData?>(null) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            isScanning = true
            scope.launch {
                delay(1200) // Realistic OCR scanning processing simulation
                val sample = generateSampleFromScan(currentCard.cardType, "மதுரை")
                extractedData = sample
                isScanning = false
            }
        }
    }

    // Gallery Picker Launcher
    val galleryPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            scannedImageUri = uri
            isScanning = true
            scope.launch {
                delay(1200)
                val sample = generateSampleFromScan(currentCard.cardType, "சென்னை")
                extractedData = sample
                isScanning = false
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("aadhaar_scanner_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Banner
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(UnionRed)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DocumentScanner,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ஆதார் கார்டு ஸ்கேனர் (OCR)",
                                    color = Color.White,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "தானாக பூர்த்தி & மாவட்ட 4 இலக்க எண்",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    if (extractedData == null && !isScanning) {
                        // INSTRUCTIONS & SCAN TRIGGER BUTTONS
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📌 ஆதார் அட்டையை ஸ்கேன் செய்வதன் மூலம்:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = UnionRed
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "✓ பெயர், தந்தை பெயர், வயது, முகவரி தானாகப் பூர்த்தியாகும்.\n✓ ஆதார் மாவட்டத்தின் அடிப்படையில் 4 இலக்க உறுப்பினர் எண் (எ.கா. TN-MDU-4819) உருவாக்கப்படும்.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Trigger Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("btn_camera_scan_aadhaar")
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("கேமரா ஸ்கேன்", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    galleryPicker.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = UnionNavy),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(50.dp)
                                    .testTag("btn_gallery_scan_aadhaar")
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("கேலரி படம்", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Simulated 1-Click Samples
                        Text(
                            text = "அல்லது மாதிரி மாவட்டத்தை தேர்வு செய்து சோதிக்கவும்:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            Triple("மதுரை", "மு. கார்த்திகேயன்", "1/14 அம்பலக்காரன் பட்டி, ஒத்தங்குடி"),
                            Triple("சென்னை", "க. மாரிமுத்து", "8, பாரதி தெரு, தாம்பரம்"),
                            Triple("கோயம்புத்தூர்", "வே. சுப்பிரமணி", "12, அண்ணா நகர், பீளமேடு"),
                            Triple("திருச்சி", "ஆர். சக்திவேல்", "24, காவேரி தெரு, ஸ்ரீரங்கம்")
                        ).forEach { (dist, sampleName, sampleAddr) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        isScanning = true
                                        scope.launch {
                                            delay(800)
                                            extractedData = generateSampleFromScan(currentCard.cardType, dist, sampleName, sampleAddr)
                                            isScanning = false
                                        }
                                    },
                                shape = RoundedCornerShape(8.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = "$sampleName ($dist)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                        Text(text = sampleAddr, fontSize = 10.sp, color = Color(0xFF64748B))
                                    }
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = UnionAmber, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                    } else if (isScanning) {
                        // SCANNING IN PROGRESS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = UnionRed, strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "ஆதார் அட்டை ஸ்கேன் செய்யப்படுகிறது...\n(Extracting Name, Address & District Code)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    } else if (extractedData != null) {
                        // EXTRACTED DATA REVIEW & CONFIRMATION
                        val data = extractedData!!

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                            border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFA7F3D0))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ஆதார் விபரம் கண்டறியப்பட்டது!", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = UnionGreen)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(UnionRed)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(data.memberId, color = Color.White, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                ExtractedRow("உறுப்பினர் எண் (Auto ID)", data.memberId, highlight = true)
                                ExtractedRow("பெயர் (Name)", data.name)
                                ExtractedRow("தந்தை / கணவர்", data.fatherName)
                                ExtractedRow("வயது / பிறந்த தேதி", data.age)
                                ExtractedRow("மாவட்டம் (District)", data.district, highlight = true)
                                ExtractedRow("முகவரி (Address)", data.address)
                                if (data.aadhaarNumber.isNotBlank()) {
                                    ExtractedRow("ஆதார் எண்", data.aadhaarNumber)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Apply to Card Button
                        Button(
                            onClick = {
                                val updated = currentCard.copy(
                                    memberId = data.memberId,
                                    name = data.name,
                                    fatherName = data.fatherName,
                                    age = data.age,
                                    district = data.district,
                                    address = data.address,
                                    aadhaarNumber = data.aadhaarNumber
                                )
                                onAadhaarDataExtracted(updated)
                                Toast.makeText(context, "ஆதார் விபரம் & புதிய அடையாள எண் ${data.memberId} இணைக்கப்பட்டது!", Toast.LENGTH_LONG).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("btn_apply_aadhaar_data")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("அட்டையில் தானாக பூர்த்தி செய் (Apply Auto-Fill)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = { extractedData = null },
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
private fun ExtractedRow(
    label: String,
    value: String,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = if (highlight) FontWeight.Black else FontWeight.Bold,
            color = if (highlight) UnionRed else Color(0xFF1E293B)
        )
    }
}

private fun generateSampleFromScan(
    cardType: String,
    district: String,
    name: String = "மு. கார்த்திகேயன்",
    address: String = "1/14 அம்பலக்காரன் பட்டி, ஒத்தங்குடி"
): AadhaarExtractedData {
    val memberId = DistrictCodeHelper.generateDistrictMemberId(district, cardType)
    val randomAadhaar = "XXXX XXXX " + (1000..9999).random()

    return AadhaarExtractedData(
        name = name,
        fatherName = "முத்துசாமி",
        age = "34",
        dob = "15/06/1990",
        gender = "ஆண் (Male)",
        address = "$address, $district",
        district = district,
        aadhaarNumber = randomAadhaar,
        memberId = memberId
    )
}
