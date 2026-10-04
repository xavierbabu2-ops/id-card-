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
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.util.DistrictDetail
import com.example.util.TamilAadhaarTransliterationHelper
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AadhaarScannerDialog(
    currentCard: MemberCardEntity,
    initialTamilMode: Boolean = true,
    onLanguageModeChange: ((Boolean) -> Unit)? = null,
    onAadhaarDataExtracted: (MemberCardEntity) -> Unit,
    onOpenSettings: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scannedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isScanning by remember { mutableStateOf(false) }
    var manualAadhaarText by remember { mutableStateOf("") }
    var extractedData by remember { mutableStateOf<AadhaarExtractedData?>(null) }
    var isTamilMode by remember { mutableStateOf(initialTamilMode) }
    var districtSearchQuery by remember { mutableStateOf("") }
    var districtDropdownExpanded by remember { mutableStateOf(false) }

    fun mapToExtractedData(parsed: com.example.util.AadhaarOcrResult, inTamil: Boolean): AadhaarExtractedData {
        val finalName = if (inTamil) TamilAadhaarTransliterationHelper.transliterateNameToTamil(parsed.name) else parsed.name
        val finalFather = if (inTamil) TamilAadhaarTransliterationHelper.transliterateNameToTamil(parsed.fatherName) else parsed.fatherName
        val finalGender = if (inTamil) TamilAadhaarTransliterationHelper.translateGenderToTamil(parsed.gender) else parsed.gender
        val finalDistrict = if (inTamil) TamilAadhaarTransliterationHelper.translateDistrictToTamil(parsed.district) else parsed.district
        val finalAddress = if (inTamil) TamilAadhaarTransliterationHelper.convertAddressToTamil(parsed.address) else parsed.address

        // Member ID calculated with district code starting from 0001
        val genMemberId = DistrictCodeHelper.generateDistrictMemberId(finalDistrict, currentCard.cardType, 1)

        return AadhaarExtractedData(
            name = finalName.ifBlank { currentCard.name },
            fatherName = finalFather.ifBlank { currentCard.fatherName },
            age = parsed.age.ifBlank { currentCard.age },
            dob = parsed.dob,
            gender = finalGender,
            address = finalAddress.ifBlank { currentCard.address },
            district = finalDistrict.ifBlank { currentCard.district },
            aadhaarNumber = parsed.aadhaarNumber,
            memberId = genMemberId
        )
    }

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
                    val data = mapToExtractedData(parsed, isTamilMode)
                    extractedData = data
                    Toast.makeText(context, "ஆதார் விவரங்கள் பெறப்பட்டது! புதிய எண்: ${data.memberId}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "படத்தில் எழுத்துக்கள் தெளிவாக இல்லை. மீண்டும் படம் எடுக்கவும் அல்லது கீழே மாதிரி மாவட்டத்தைத் தேர்ந்தெடுக்கவும்.", Toast.LENGTH_LONG).show()
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
                    val data = mapToExtractedData(parsed, isTamilMode)
                    extractedData = data
                    Toast.makeText(context, "ஆதார் விவரங்கள் பெறப்பட்டது! புதிய எண்: ${data.memberId}", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "படத்தில் எழுத்துக்கள் தெளிவாக இல்லை. மீண்டும் படம் தேர்வு செய்யவும்.", Toast.LENGTH_LONG).show()
                }
                isScanning = false
            }
        }
    }

    val filteredDistrictDetails = remember(districtSearchQuery) {
        if (districtSearchQuery.isBlank()) {
            DistrictCodeHelper.ALL_38_DISTRICT_DETAILS
        } else {
            val q = districtSearchQuery.trim().lowercase()
            DistrictCodeHelper.ALL_38_DISTRICT_DETAILS.filter {
                it.tamilName.contains(q, ignoreCase = true) ||
                it.englishName.contains(q, ignoreCase = true) ||
                it.code.contains(q, ignoreCase = true) ||
                it.sampleAddress.contains(q, ignoreCase = true)
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp)
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
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "ஆதார் அட்டை ஸ்கேனர் & 38 மாவட்ட உறுப்பினர் எண்",
                                    color = Color.White,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "38 மாவட்ட குறியுடன் 0001 இருந்து ஆரம்பம்",
                                    color = Color(0xFFFFE4E6),
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (onOpenSettings != null) {
                                IconButton(onClick = {
                                    onDismiss()
                                    onOpenSettings()
                                }) {
                                    Icon(Icons.Default.Settings, contentDescription = "Settings", tint = Color.White)
                                }
                            }
                            IconButton(onClick = onDismiss) {
                                Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                            }
                        }
                    }
                }

                // Language Mode Selector Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Language, contentDescription = null, tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "மொழிப் பெயர்ப்பு முறை:",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF334155)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isTamilMode) UnionRed else Color.White)
                                .clickable {
                                    isTamilMode = true
                                    onLanguageModeChange?.invoke(true)
                                    if (manualAadhaarText.isNotBlank()) {
                                        val parsed = AadhaarOcrParser.parseAadhaarText(manualAadhaarText, currentCard.cardType)
                                        extractedData = mapToExtractedData(parsed, true)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "தமிழ்",
                                color = if (isTamilMode) Color.White else Color(0xFF334155),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (!isTamilMode) UnionNavy else Color.White)
                                .clickable {
                                    isTamilMode = false
                                    onLanguageModeChange?.invoke(false)
                                    if (manualAadhaarText.isNotBlank()) {
                                        val parsed = AadhaarOcrParser.parseAadhaarText(manualAadhaarText, currentCard.cardType)
                                        extractedData = mapToExtractedData(parsed, false)
                                    }
                                }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "English",
                                color = if (!isTamilMode) Color.White else Color(0xFF334155),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Column(modifier = Modifier.padding(14.dp)) {
                    if (extractedData == null && !isScanning) {
                        // INSTRUCTIONS & SCAN TRIGGER BUTTONS
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2))
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📌 ஆதார் ஸ்கேன் & 38 மாவட்ட தானியங்கி வசதி:",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.5.sp,
                                    color = UnionRed
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "✓ ஆதார் அட்டை படம் எடுத்தவுடன் பெயர், முகவரி தானாக ஃபீல் செய்யப்படும்.\n✓ தமிழ்நாடு 38 மாவட்டத்திற்கு ஏற்ப மாவட்ட குறியுடன் 0001 இலிருந்து ஆரம்பிக்கும் உறுப்பினர் எண் (எ.கா. TN-MDU-0001, TN-CHN-0001, TN-CBE-0001) தானாக உருவாக்கப்படும்.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF334155),
                                    lineHeight = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Trigger Buttons (Camera & Gallery)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { cameraLauncher.launch(null) },
                                colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
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
                                    .height(48.dp)
                                    .testTag("btn_gallery_scan_aadhaar")
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("கேலரி படம்", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Manual Text Paste / Type Option
                        Text(
                            text = "✍️ அல்லது ஆதார் உரை / முகவரியை ஒட்டவும் (Paste text):",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = manualAadhaarText,
                            onValueChange = { manualAadhaarText = it },
                            placeholder = { Text("எ.கா: மு. கார்த்திகேயன், S/O முத்துசாமி, 12, காந்தி ரோடு, மதுரை - 625001", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                focusedLabelColor = UnionRed
                            )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Button(
                            onClick = {
                                if (manualAadhaarText.isNotBlank()) {
                                    val parsed = AadhaarOcrParser.parseAadhaarText(manualAadhaarText, currentCard.cardType)
                                    extractedData = mapToExtractedData(parsed, isTamilMode)
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
                            Text("உரையை ஆராய்ந்து ஃபீல் செய்க (Extract & Fill)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 38 DISTRICTS QUICK ONE-CLICK TESTING
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🏛️ 38 மாவட்டங்கள் விரைவு மாதிரி சோதனை (0001 ஆரம்பம்):",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "${filteredDistrictDetails.size} / 38",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnionRed
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))

                        // District Search Filter
                        OutlinedTextField(
                            value = districtSearchQuery,
                            onValueChange = { districtSearchQuery = it },
                            placeholder = { Text("38 மாவட்டங்களைத் தேடுக (எ.கா. மதுரை, சென்னை, MDU...)", fontSize = 11.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFF64748B)) },
                            trailingIcon = {
                                if (districtSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { districtSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                                    }
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                focusedLabelColor = UnionRed
                            )
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Display Districts list
                        filteredDistrictDetails.take(12).forEach { detail ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp)
                                    .clickable {
                                        isScanning = true
                                        scope.launch {
                                            delay(200)
                                            val genId = DistrictCodeHelper.generateDistrictMemberId(detail.tamilName, currentCard.cardType, 1)
                                            extractedData = AadhaarExtractedData(
                                                name = detail.sampleName,
                                                fatherName = detail.sampleFatherName,
                                                age = currentCard.age.ifBlank { "32" },
                                                dob = "15/06/1992",
                                                gender = "ஆண் (Male)",
                                                address = detail.sampleAddress,
                                                district = detail.tamilName,
                                                aadhaarNumber = "XXXX XXXX " + (1000..9999).random(),
                                                memberId = genId
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
                                        .padding(horizontal = 10.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${detail.tamilName} (${detail.englishName})",
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1E293B)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(UnionNavy)
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(detail.code, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                        Text(
                                            text = "ஆரம்ப எண்: ${DistrictCodeHelper.generateDistrictMemberId(detail.tamilName, currentCard.cardType, 1)}",
                                            fontSize = 10.5.sp,
                                            color = UnionRed,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(text = detail.sampleAddress, fontSize = 9.5.sp, color = Color(0xFF64748B), maxLines = 1)
                                    }
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = UnionAmber, modifier = Modifier.size(18.dp))
                                }
                            }
                        }

                        if (filteredDistrictDetails.size > 12) {
                            Text(
                                text = "மேலும் ${filteredDistrictDetails.size - 12} மாவட்டங்களை காண மேலே உள்ள தேடல் கட்டத்தில் தட்டச்சு செய்யவும்.",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }

                    } else if (isScanning) {
                        // SCANNING IN PROGRESS
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = UnionRed, strokeWidth = 3.dp)
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "ஆதார் அட்டை விவரங்கள் படிக்கப்படுகிறது...\n38 மாவட்ட குறியுடன் 0001 உறுப்பினர் எண் உருவாக்கப்படுகிறது",
                                    fontSize = 12.5.sp,
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
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("ஆதார் விபரம் பெறப்பட்டது!", fontSize = 12.5.sp, fontWeight = FontWeight.ExtraBold, color = UnionGreen)
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(UnionRed)
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(data.memberId, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Black)
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // District Code Badge
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Badge, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "38 மாவட்ட குறியீடு: ${DistrictCodeHelper.getDistrictCode(data.district)} | ஆரம்ப எண்: 0001",
                                        fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF92400E)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

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
                                        val newId = DistrictCodeHelper.generateDistrictMemberId(detectedDist, currentCard.cardType, 1)
                                        extractedData = data.copy(address = newAddr, district = detectedDist, memberId = newId)
                                    },
                                    label = { Text("ஆதார் முகவரி (Address)", fontSize = 10.sp) },
                                    maxLines = 2,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // District selector among all 38 districts
                                ExposedDropdownMenuBox(
                                    expanded = districtDropdownExpanded,
                                    onExpandedChange = { districtDropdownExpanded = !districtDropdownExpanded }
                                ) {
                                    OutlinedTextField(
                                        value = "${data.district} (${DistrictCodeHelper.getDistrictCode(data.district)})",
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("38 மாவட்டம் தேர்வு (District Code & 0001 ID)", fontSize = 10.sp) },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtDropdownExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                    )
                                    ExposedDropdownMenu(
                                        expanded = districtDropdownExpanded,
                                        onDismissRequest = { districtDropdownExpanded = false }
                                    ) {
                                        DistrictCodeHelper.ALL_38_DISTRICT_DETAILS.forEach { distDetail ->
                                            DropdownMenuItem(
                                                text = {
                                                    Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        horizontalArrangement = Arrangement.SpaceBetween,
                                                        verticalAlignment = Alignment.CenterVertically
                                                    ) {
                                                        Text("${distDetail.tamilName} (${distDetail.englishName})", fontSize = 12.sp)
                                                        Text(distDetail.code, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = UnionRed)
                                                    }
                                                },
                                                onClick = {
                                                    val newId = DistrictCodeHelper.generateDistrictMemberId(distDetail.tamilName, currentCard.cardType, 1)
                                                    extractedData = data.copy(
                                                        district = distDetail.tamilName,
                                                        memberId = newId
                                                    )
                                                    districtDropdownExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                OutlinedTextField(
                                    value = data.aadhaarNumber,
                                    onValueChange = { extractedData = data.copy(aadhaarNumber = it) },
                                    label = { Text("ஆதார் எண்", fontSize = 10.sp) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

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
                                Toast.makeText(context, "ஆதார் விவரங்கள் & எண் ${data.memberId} படிவத்தில் தானாக நிரப்பப்பட்டது!", Toast.LENGTH_LONG).show()
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
                            Text("மீண்டும் ஸ்கேன் செய்க (Rescan / Change District)", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
