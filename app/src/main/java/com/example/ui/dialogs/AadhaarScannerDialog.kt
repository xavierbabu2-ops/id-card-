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
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
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
import com.example.data.MemberCardEntity
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.AadhaarOcrEngine
import com.example.util.AadhaarOcrParser
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
    var manualAadhaarText by remember { mutableStateOf("") }
    var extractedData by remember { mutableStateOf<AadhaarExtractedData?>(null) }

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            isScanning = true
            scope.launch {
                val ocrText = AadhaarOcrEngine.recognizeTextFromBitmap(bitmap)
                if (ocrText.isNotBlank()) {
                    manualAadhaarText = ocrText
                    val parsed = AadhaarOcrParser.parseAadhaarText(ocrText, currentCard.cardType)
                    extractedData = AadhaarExtractedData(
                        name = parsed.name.ifBlank { currentCard.name },
                        fatherName = parsed.fatherName.ifBlank { currentCard.fatherName },
                        age = parsed.age.ifBlank { currentCard.age },
                        dob = parsed.dob,
                        gender = parsed.gender,
                        address = parsed.address.ifBlank { currentCard.address },
                        district = parsed.district.ifBlank { currentCard.district },
                        aadhaarNumber = parsed.aadhaarNumber,
                        memberId = parsed.generatedMemberId
                    )
                    Toast.makeText(context, "ஆதார் கார்டு தகவல்கள் வெற்றிகரமாகப் பெறப்பட்டது!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "படத்தில் எழுத்துக்கள் தெளிவாக இல்லை. மீண்டும் படம் எடுக்கவும் அல்லது கீழே தட்டச்சு செய்யவும்.", Toast.LENGTH_LONG).show()
                }
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
                val ocrText = AadhaarOcrEngine.recognizeTextFromUri(context, uri)
                if (ocrText.isNotBlank()) {
                    manualAadhaarText = ocrText
                    val parsed = AadhaarOcrParser.parseAadhaarText(ocrText, currentCard.cardType)
                    extractedData = AadhaarExtractedData(
                        name = parsed.name.ifBlank { currentCard.name },
                        fatherName = parsed.fatherName.ifBlank { currentCard.fatherName },
                        age = parsed.age.ifBlank { currentCard.age },
                        dob = parsed.dob,
                        gender = parsed.gender,
                        address = parsed.address.ifBlank { currentCard.address },
                        district = parsed.district.ifBlank { currentCard.district },
                        aadhaarNumber = parsed.aadhaarNumber,
                        memberId = parsed.generatedMemberId
                    )
                    Toast.makeText(context, "ஆதார் கார்டு தகவல்கள் பெறப்பட்டது!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "படத்தில் உள்ள எழுத்துக்கள் தெளிவாக இல்லை. மீண்டும் படம் தேர்வு செய்யவும்.", Toast.LENGTH_LONG).show()
                }
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
                                    text = "முகவரி & மாவட்ட வாரியான 4 இலக்க எண்",
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
                                    text = "✓ உங்கள் ஆதார் அட்டையில் உள்ள பெயர், தந்தை பெயர், பிறந்த தேதி மற்றும் உண்மை முகவரி தானாகப் பெறப்படும்.\n✓ நீங்கள் தேர்ந்தெடுக்கும் மாவட்டத்திற்கு ஏற்ப தானாக உறுப்பினர் எண் (எ.கா. TN-MDU-4819) உருவாக்கப்படும்.",
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

                        // Manual Text Paste / Type Option
                        Text(
                            text = "✍️ அல்லது ஆதார் உரை / முகவரியை ஒட்டவும் (Paste text):",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = manualAadhaarText,
                            onValueChange = { manualAadhaarText = it },
                            placeholder = { Text("எ.கா: மு. கார்த்திகேயன், S/O முத்துசாமி, 12, காந்தி ரோடு, மதுரை - 625001", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                focusedLabelColor = UnionRed
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (manualAadhaarText.isNotBlank()) {
                                    val parsed = AadhaarOcrParser.parseAadhaarText(manualAadhaarText, currentCard.cardType)
                                    extractedData = AadhaarExtractedData(
                                        name = parsed.name.ifBlank { currentCard.name },
                                        fatherName = parsed.fatherName.ifBlank { currentCard.fatherName },
                                        age = parsed.age.ifBlank { currentCard.age },
                                        dob = parsed.dob,
                                        gender = parsed.gender,
                                        address = parsed.address.ifBlank { manualAadhaarText.trim() },
                                        district = parsed.district.ifBlank { currentCard.district },
                                        aadhaarNumber = parsed.aadhaarNumber,
                                        memberId = parsed.generatedMemberId
                                    )
                                } else {
                                    Toast.makeText(context, "முகவரியை உள்ளிடவும் அல்லது கேமரா மூலம் ஸ்கேன் செய்யவும்", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F766E)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.ContentPaste, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("உரையை ஆராய்ந்து பெறுக (Extract Address)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Quick District Samples
                        Text(
                            text = "அல்லது மாதிரி மாவட்டத்தை தேர்வு செய்து சோதிக்கவும்:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B)
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        listOf(
                            Triple("மதுரை", "மு. கார்த்திகேயன்", "1/14 அம்பலக்காரன் பட்டி, ஒத்தங்குடி, மதுரை"),
                            Triple("சென்னை", "க. மாரிமுத்து", "8, பாரதி தெரு, தாம்பரம், சென்னை"),
                            Triple("கோயம்புத்தூர்", "வே. சுப்பிரமணி", "12, அண்ணா நகர், பீளமேடு, கோயம்புத்தூர்"),
                            Triple("திருச்சி", "ஆர். சக்திவேல்", "24, காவேரி தெரு, ஸ்ரீரங்கம், திருச்சி"),
                            Triple("சேலம்", "சு. பழனிசாமி", "5, காமராஜர் வீதி, சூரமங்கலம், சேலம்")
                        ).forEach { (dist, sampleName, sampleAddr) ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.5.dp)
                                    .clickable {
                                        isScanning = true
                                        scope.launch {
                                            delay(300)
                                            extractedData = AadhaarExtractedData(
                                                name = sampleName,
                                                fatherName = currentCard.fatherName.ifBlank { "முத்துசாமி" },
                                                age = currentCard.age.ifBlank { "34" },
                                                dob = "15/06/1990",
                                                gender = "ஆண் (Male)",
                                                address = sampleAddr,
                                                district = dist,
                                                aadhaarNumber = "XXXX XXXX " + (1000..9999).random(),
                                                memberId = DistrictCodeHelper.generateDistrictMemberId(dist, currentCard.cardType)
                                            )
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
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = "$sampleName ($dist)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                                        Text(text = sampleAddr, fontSize = 10.sp, color = Color(0xFF64748B), maxLines = 1)
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
                                    text = "ஆதார் அட்டை விவரங்கள் படிக்கப்படுகிறது...\n(Reading Name, Address & Generating ID)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color(0xFF1E293B)
                                )
                            }
                        }
                    } else if (extractedData != null) {
                        // EXTRACTED DATA REVIEW & FULLY EDITABLE FORM
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
                                        Text("ஆதார் விபரம் பெறப்பட்டது!", fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, color = UnionGreen)
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

                                OutlinedTextField(
                                    value = data.name,
                                    onValueChange = { extractedData = data.copy(name = it) },
                                    label = { Text("பெயர் (Name)", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = data.fatherName,
                                        onValueChange = { extractedData = data.copy(fatherName = it) },
                                        label = { Text("தந்தை / கணவர்", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = data.age,
                                        onValueChange = { extractedData = data.copy(age = it) },
                                        label = { Text("வயது", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(0.5f)
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = data.address,
                                    onValueChange = { newAddr ->
                                        val detectedDist = DistrictCodeHelper.detectDistrictFromText(newAddr)
                                        val newId = DistrictCodeHelper.generateDistrictMemberId(detectedDist, currentCard.cardType)
                                        extractedData = data.copy(address = newAddr, district = detectedDist, memberId = newId)
                                    },
                                    label = { Text("ஆதார் முகவரி (Address - தேவைக்கேற்ப மாற்றலாம்)", fontSize = 10.sp) },
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    OutlinedTextField(
                                        value = data.district,
                                        onValueChange = { newDist ->
                                            val newId = DistrictCodeHelper.generateDistrictMemberId(newDist, currentCard.cardType)
                                            extractedData = data.copy(district = newDist, memberId = newId)
                                        },
                                        label = { Text("மாவட்டம் (District)", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = data.aadhaarNumber,
                                        onValueChange = { extractedData = data.copy(aadhaarNumber = it) },
                                        label = { Text("ஆதார் எண்", fontSize = 10.sp) },
                                        singleLine = true,
                                        modifier = Modifier.weight(1.2f)
                                    )
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
                                Toast.makeText(context, "ஆதார் விபரம் & புதிய முகவரி அட்டையில் வெற்றிகரமாக இணைக்கப்பட்டது!", Toast.LENGTH_LONG).show()
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
                            Text("அட்டையில் தானாக பூர்த்தி செய் (Apply Details)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
