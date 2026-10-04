package com.example.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberCardEntity
import com.example.ui.theme.BloodRed
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.DistrictCodeHelper

val TAMIL_NADU_DISTRICTS = listOf(
    "மதுரை", "சென்னை", "கோயம்புத்தூர்",
    "திருச்சி", "சேலம்", "திண்டுக்கல்",
    "திருநெல்வேலி", "தஞ்சாவூர்", "ஈரோடு",
    "திருப்பூர்", "வேலூர்", "விருதுநகர்",
    "சிவகங்கை", "தேனி", "ராமநாதபுரம்",
    "புதுக்கோட்டை", "கரூர்", "நாமக்கல்",
    "கன்னியாகுமரி", "தூத்துக்குடி", "கடலூர்",
    "காஞ்சிபுரம்", "செங்கல்பட்டு", "கள்ளக்குறிச்சி", "விழுப்புரம்"
)

val PAINTER_TRADES = listOf(
    "வண்ணப் பூச்சாளர் (Wall Painter)",
    "கலை ஓவியர் (Artist & Muralist)",
    "ஸ்ப்ரே பெயிண்டர் (Spray Painter)",
    "பலகை ஓவியர் (Signboard Artist)",
    "வூட் பாலிஷர் (Wood Polisher)",
    "டெக்ஸ்சர் ஆர்ட்டிஸ்ட் (Texture Artist)",
    "கட்டுமான பெயிண்டர் (Building Painter)",
    "பொது ஓவியக் கலைஞர் (General Artist)"
)

val BLOOD_GROUPS = listOf("O +ve", "A +ve", "B +ve", "AB +ve", "O -ve", "A -ve", "B -ve", "AB -ve")

val EXECUTIVE_POSTS = listOf(
    "மாநிலத் தலைவர் (State President)",
    "மாநில பொதுச்செயலாளர் (Gen Secretary)",
    "மாநில பொருளாளர் (State Treasurer)",
    "மாநில துணைத் தலைவர் (Vice President)",
    "மாவட்ட தலைவர் (District President)",
    "மாவட்ட செயலாளர் (District Secretary)",
    "மாவட்ட பொருளாளர் (District Treasurer)",
    "கிளைத் தலைவர் (Branch President)",
    "செயற்குழு உறுப்பினர் (Executive Member)"
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CardFormEditor(
    card: MemberCardEntity,
    onCardChange: (MemberCardEntity) -> Unit,
    onSave: () -> Unit,
    onReset: () -> Unit,
    onOpenPayment: () -> Unit,
    onOpenAadhaarScan: () -> Unit,
    onOpenPhotoPicker: (() -> Unit)? = null,
    onOpenSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onCardChange(card.copy(photoUri = uri.toString()))
        }
    }

    val sealPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            onCardChange(card.copy(customSealUri = uri.toString()))
        }
    }

    var tradeExpanded by remember { mutableStateOf(false) }
    var districtExpanded by remember { mutableStateOf(false) }
    var bloodExpanded by remember { mutableStateOf(false) }
    var postExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("card_form_editor"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Approval & Payment Status Banner
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (card.approvalStatus) {
                        "APPROVED" -> Color(0xFFECFDF5)
                        "REJECTED" -> Color(0xFFFEF2F2)
                        else -> Color(0xFFFEF3C7)
                    }
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (card.approvalStatus) {
                        "APPROVED" -> Color(0xFFA7F3D0)
                        "REJECTED" -> Color(0xFFFECDD3)
                        else -> Color(0xFFFDE68A)
                    }
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = when (card.approvalStatus) {
                                "APPROVED" -> Icons.Default.CheckCircle
                                "REJECTED" -> Icons.Default.Delete
                                else -> Icons.Default.HourglassTop
                            },
                            contentDescription = null,
                            tint = when (card.approvalStatus) {
                                "APPROVED" -> UnionGreen
                                "REJECTED" -> Color.Red
                                else -> Color(0xFFB45309)
                            },
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = when (card.approvalStatus) {
                                    "APPROVED" -> "அங்கீகரிக்கப்பட்டது (Approved)"
                                    "REJECTED" -> "நிராகரிக்கப்பட்டது (Rejected)"
                                    else -> "சூப்பர் அட்மின் ஒப்புதல் நிலுவை"
                                },
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (card.approvalStatus) {
                                    "APPROVED" -> UnionGreen
                                    "REJECTED" -> Color.Red
                                    else -> Color(0xFFB45309)
                                }
                            )
                            Text(
                                text = if (card.utrNumber.isNotBlank()) "UTR: ${card.utrNumber}" else "₹100 செலுத்தி UTR பதிவு செய்க (Pay ₹100)",
                                fontSize = 10.5.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Button(
                        onClick = onOpenPayment,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (card.approvalStatus == "APPROVED") UnionGreen else UnionRed
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("btn_open_payment_modal")
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (card.approvalStatus == "APPROVED") "ரசீது (₹100)" else "₹100 செலுத்து",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Section Header with Aadhaar OCR Button
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Badge,
                            contentDescription = null,
                            tint = UnionRed,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "அடையாள அட்டை விவரங்கள்",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                        if (onOpenSettings != null) {
                            OutlinedButton(
                                onClick = onOpenSettings,
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF334155))
                            ) {
                                Icon(Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("அமைப்புகள்", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Button(
                            onClick = onOpenAadhaarScan,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E3A8A)),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("btn_aadhaar_ocr_autofill")
                        ) {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ஆதார் OCR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Card Type Switcher Chips
            Text(
                text = "அட்டை வகை (Card Type):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = card.cardType == "MEMBER",
                    onClick = {
                        val newId = DistrictCodeHelper.generateDistrictMemberId(card.district, "MEMBER")
                        onCardChange(card.copy(cardType = "MEMBER", memberId = newId, themeColorHex = "#D3121B"))
                    },
                    label = { Text("உறுப்பினர் (Member)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        if (card.cardType == "MEMBER") Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFE4E6),
                        selectedLabelColor = UnionRed
                    ),
                    modifier = Modifier.testTag("chip_member")
                )

                FilterChip(
                    selected = card.cardType == "EXECUTIVE",
                    onClick = {
                        val newId = DistrictCodeHelper.generateDistrictMemberId(card.district, "EXECUTIVE")
                        onCardChange(card.copy(cardType = "EXECUTIVE", memberId = newId, themeColorHex = "#D3121B"))
                    },
                    label = { Text("பொறுப்பாளர் (Executive)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        if (card.cardType == "EXECUTIVE") Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEFF6FF),
                        selectedLabelColor = UnionNavy
                    ),
                    modifier = Modifier.testTag("chip_executive")
                )

                FilterChip(
                    selected = card.cardType == "CONTRACTOR",
                    onClick = {
                        val newId = DistrictCodeHelper.generateDistrictMemberId(card.district, "CONTRACTOR")
                        onCardChange(card.copy(cardType = "CONTRACTOR", memberId = newId, themeColorHex = "#B45309"))
                    },
                    label = { Text("ஒப்பந்ததாரர் (Contractor)", fontSize = 12.sp, fontWeight = FontWeight.Bold) },
                    leadingIcon = {
                        if (card.cardType == "CONTRACTOR") Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEF3C7),
                        selectedLabelColor = Color(0xFFB45309)
                    ),
                    modifier = Modifier.testTag("chip_contractor")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Photo Selection Box
            Text(
                text = "உறுப்பினர் புகைப்படம் (Member Photo):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardPhotoFrame(
                    photoUri = card.photoUri,
                    avatarPreset = card.avatarPreset,
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    borderColor = UnionRed,
                    borderWidth = 1.5.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Button(
                        onClick = {
                            if (onOpenPhotoPicker != null) {
                                onOpenPhotoPicker()
                            } else {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF1E293B)),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_pick_photo")
                    ) {
                        Icon(imageVector = Icons.Default.AddPhotoAlternate, contentDescription = null, tint = UnionRed, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("கேமரா / கேலரி படம் (Choose Photo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    if (card.photoUri != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { onCardChange(card.copy(photoUri = null)) },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("படத்தை நீக்குக (Remove Photo)", fontSize = 11.sp, color = Color.Red)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Preset Avatar Pickers
            Text(
                text = "அல்லது முன்மாதிரி சின்னத்தை தேர்ந்தெடுக்கவும் (Or Choose Avatar):",
                fontSize = 10.5.sp,
                color = Color(0xFF64748B)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                (1..5).forEach { preset ->
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (card.photoUri == null && card.avatarPreset == preset) Color(0xFFFFD700) else Color(0xFFE2E8F0))
                            .border(
                                width = if (card.photoUri == null && card.avatarPreset == preset) 2.dp else 1.dp,
                                color = if (card.photoUri == null && card.avatarPreset == preset) UnionRed else Color(0xFFCBD5E1),
                                shape = CircleShape
                            )
                            .clickable {
                                onCardChange(card.copy(avatarPreset = preset, photoUri = null))
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (preset) {
                                1 -> Icons.Default.FormatPaint
                                2 -> Icons.Default.Palette
                                3 -> Icons.Default.Brush
                                4 -> Icons.Default.SupervisorAccount
                                else -> Icons.Default.Person
                            },
                            contentDescription = "Preset $preset",
                            tint = if (card.photoUri == null && card.avatarPreset == preset) UnionRed else Color(0xFF475569),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Identification Number (Auto District-Coded)
            OutlinedTextField(
                value = card.memberId,
                onValueChange = { onCardChange(card.copy(memberId = it)) },
                label = {
                    Text(
                        when (card.cardType) {
                            "EXECUTIVE" -> "பொறுப்பாளர் எண் (Auto District Code: TN-EXEC-DIST-XXXX)"
                            "CONTRACTOR" -> "ஒப்பந்ததாரர் எண் (Auto District Code: TN-CON-DIST-XXXX)"
                            else -> "உறுப்பினர் எண் (Auto District Code: TN-DIST-XXXX)"
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null, tint = UnionRed) },
                trailingIcon = {
                    IconButton(onClick = {
                        val newId = DistrictCodeHelper.generateDistrictMemberId(card.district, card.cardType)
                        onCardChange(card.copy(memberId = newId))
                    }) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Regenerate", tint = UnionAmber)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_card_id"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UnionRed,
                    focusedLabelColor = UnionRed
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Full Name (Clickable to open Aadhaar Scanner)
            OutlinedTextField(
                value = card.name,
                onValueChange = { onCardChange(card.copy(name = it)) },
                label = {
                    Text(
                        when (card.cardType) {
                            "EXECUTIVE" -> "பொறுப்பாளர் பெயர் (Officer Name)"
                            "CONTRACTOR" -> "ஒப்பந்ததாரர் பெயர் (Contractor Name)"
                            else -> "உறுப்பினர் பெயர் (Member Name)"
                        }
                    )
                },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = UnionRed) },
                trailingIcon = {
                    IconButton(onClick = onOpenAadhaarScan) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = "Aadhaar OCR", tint = UnionNavy)
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_card_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UnionRed,
                    focusedLabelColor = UnionRed
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Trade / Designation / Firm
            when (card.cardType) {
                "EXECUTIVE" -> {
                    ExposedDropdownMenuBox(
                        expanded = postExpanded,
                        onExpandedChange = { postExpanded = !postExpanded }
                    ) {
                        OutlinedTextField(
                            value = card.designation,
                            onValueChange = { onCardChange(card.copy(designation = it)) },
                            label = { Text("சங்கப் பதவி (Union Post)") },
                            leadingIcon = { Icon(Icons.Default.SupervisorAccount, contentDescription = null, tint = UnionRed) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = postExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("input_designation"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                focusedLabelColor = UnionRed
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = postExpanded,
                            onDismissRequest = { postExpanded = false }
                        ) {
                            EXECUTIVE_POSTS.forEach { post ->
                                DropdownMenuItem(
                                    text = { Text(post) },
                                    onClick = {
                                        onCardChange(card.copy(designation = post.substringBefore(" (")))
                                        postExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                "CONTRACTOR" -> {
                    OutlinedTextField(
                        value = card.firmName,
                        onValueChange = { onCardChange(card.copy(firmName = it)) },
                        label = { Text("நிறுவனத்தின் பெயர் (Firm / Agency Name)") },
                        leadingIcon = { Icon(Icons.Default.Business, contentDescription = null, tint = UnionAmber) },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_firm_name"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnionAmber,
                            focusedLabelColor = UnionAmber
                        )
                    )
                }
                else -> {
                    ExposedDropdownMenuBox(
                        expanded = tradeExpanded,
                        onExpandedChange = { tradeExpanded = !tradeExpanded }
                    ) {
                        OutlinedTextField(
                            value = card.jobTitle,
                            onValueChange = { onCardChange(card.copy(jobTitle = it)) },
                            label = { Text("உறுப்பினர் தொழில் / பணி (Occupation)") },
                            leadingIcon = { Icon(Icons.Default.FormatPaint, contentDescription = null, tint = UnionRed) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tradeExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("input_job_title"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                focusedLabelColor = UnionRed
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = tradeExpanded,
                            onDismissRequest = { tradeExpanded = false }
                        ) {
                            PAINTER_TRADES.forEach { trade ->
                                DropdownMenuItem(
                                    text = { Text(trade) },
                                    onClick = {
                                        onCardChange(card.copy(jobTitle = trade.substringBefore(" (")))
                                        tradeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Contact Phone
            OutlinedTextField(
                value = card.phone,
                onValueChange = { onCardChange(card.copy(phone = it)) },
                label = { Text("தொடர்பு கைபேசி எண் (Mobile No)") },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = UnionRed) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_phone"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UnionRed,
                    focusedLabelColor = UnionRed
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // District Dropdown (Auto-updates 4-digit district coded memberId!)
            ExposedDropdownMenuBox(
                expanded = districtExpanded,
                onExpandedChange = { districtExpanded = !districtExpanded }
            ) {
                OutlinedTextField(
                    value = card.district,
                    onValueChange = { onCardChange(card.copy(district = it)) },
                    label = { Text("மாவட்டம் (District - தமிழ்நாடு)") },
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = UnionRed) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                        .testTag("input_district"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnionRed,
                        focusedLabelColor = UnionRed
                    )
                )
                ExposedDropdownMenu(
                    expanded = districtExpanded,
                    onDismissRequest = { districtExpanded = false }
                ) {
                    TAMIL_NADU_DISTRICTS.forEach { dist ->
                        DropdownMenuItem(
                            text = { Text(dist) },
                            onClick = {
                                val updatedMemberId = DistrictCodeHelper.generateDistrictMemberId(dist, card.cardType)
                                onCardChange(card.copy(district = dist, memberId = updatedMemberId))
                                districtExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Back side specific fields
            if (card.cardType == "MEMBER") {
                OutlinedTextField(
                    value = card.fatherName,
                    onValueChange = { onCardChange(card.copy(fatherName = it)) },
                    label = { Text("தந்தை / கணவர் பெயர் (Father's Name)") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = UnionRed) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnionRed,
                        focusedLabelColor = UnionRed
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = card.age,
                        onValueChange = { onCardChange(card.copy(age = it)) },
                        label = { Text("வயது (Age)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(0.45f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnionRed,
                            focusedLabelColor = UnionRed
                        )
                    )

                    ExposedDropdownMenuBox(
                        expanded = bloodExpanded,
                        onExpandedChange = { bloodExpanded = !bloodExpanded },
                        modifier = Modifier.weight(0.55f)
                    ) {
                        OutlinedTextField(
                            value = card.bloodGroup,
                            onValueChange = { onCardChange(card.copy(bloodGroup = it)) },
                            label = { Text("ரத்த வகை (Blood)") },
                            leadingIcon = { Icon(Icons.Default.Opacity, contentDescription = null, tint = BloodRed) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodExpanded) },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = BloodRed,
                                focusedLabelColor = BloodRed
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = bloodExpanded,
                            onDismissRequest = { bloodExpanded = false }
                        ) {
                            BLOOD_GROUPS.forEach { bg ->
                                DropdownMenuItem(
                                    text = { Text(bg) },
                                    onClick = {
                                        onCardChange(card.copy(bloodGroup = bg))
                                        bloodExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = card.emergencyPhone,
                    onValueChange = { onCardChange(card.copy(emergencyPhone = it)) },
                    label = { Text("அவசர உதவி எண் (Emergency Contact)") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFDC2626)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnionRed,
                        focusedLabelColor = UnionRed
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Address (Clickable to open Aadhaar Scanner)
            OutlinedTextField(
                value = card.address,
                onValueChange = { onCardChange(card.copy(address = it)) },
                label = { Text("முழு இருப்பிடம் / முகவரி (Full Address)") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = UnionRed) },
                trailingIcon = {
                    IconButton(onClick = onOpenAadhaarScan) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = "Aadhaar OCR", tint = UnionNavy)
                    }
                },
                maxLines = 2,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_address"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UnionRed,
                    focusedLabelColor = UnionRed
                )
            )

            // -------------------------------------------------------------
            // Government Accreditation Seal / Logo Section (அரசு அங்கீகார முத்திரை)
            // -------------------------------------------------------------
            Spacer(modifier = Modifier.height(14.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "🏛️ தமிழ்நாடு அரசு அனுமதி பெற்ற சங்கம் முத்திரை:",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(8.dp))
                                .background(Color.White),
                            contentAlignment = Alignment.Center
                        ) {
                            if (card.customSealUri != null) {
                                coil.compose.AsyncImage(
                                    model = card.customSealUri,
                                    contentDescription = "Custom Seal",
                                    modifier = Modifier.size(44.dp)
                                )
                            } else {
                                androidx.compose.foundation.Image(
                                    painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_auth_accreditation),
                                    contentDescription = "Default Seal",
                                    modifier = Modifier.size(44.dp)
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Button(
                                    onClick = {
                                        sealPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                    modifier = Modifier.height(34.dp)
                                ) {
                                    Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("கேலரியில் மாற்று", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                                }

                                if (card.customSealUri != null) {
                                    OutlinedButton(
                                        onClick = { onCardChange(card.copy(customSealUri = null)) },
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("இயல்புநிலை (Reset)", fontSize = 10.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Save & New Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_save_card")
                ) {
                    Icon(imageVector = Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("சேமிக்க (Save Card)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .height(48.dp)
                        .testTag("btn_reset_card")
                ) {
                    Text("புதிய அட்டை (New)", fontWeight = FontWeight.Bold, color = Color(0xFF475569))
                }
            }
        }
    }
}
