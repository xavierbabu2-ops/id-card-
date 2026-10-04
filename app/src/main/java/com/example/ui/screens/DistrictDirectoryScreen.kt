package com.example.ui.screens

import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.components.DistrictFilteredMemberList
import com.example.ui.components.TAMIL_NADU_DISTRICTS
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.BitmapRendererHelper
import com.example.util.CardExporter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DistrictDirectoryScreen(
    cards: List<MemberCardEntity>,
    onSelectCardForPreview: (MemberCardEntity) -> Unit,
    onSelectCardForEdit: (MemberCardEntity) -> Unit,
    onAddNewCardForDistrict: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var viewMode by remember { mutableStateOf(0) } // 0: Dropdown Filter List, 1: District Grid Summary
    var selectedDistrictDetail by remember { mutableStateOf<String?>(null) }
    var districtSearchQuery by remember { mutableStateOf("") }

    val districtGroups = remember(cards) {
        cards.groupBy { it.district.trim() }
    }

    val allActiveDistricts = remember(cards, districtSearchQuery) {
        val existingDistricts = districtGroups.keys.toList()
        val predefined = TAMIL_NADU_DISTRICTS.map { it.substringBefore(" (") }
        (existingDistricts + predefined).distinct().filter {
            districtSearchQuery.isBlank() || it.contains(districtSearchQuery, ignoreCase = true)
        }.sortedByDescending { districtGroups[it]?.size ?: 0 }
    }

    Column(modifier = modifier.fillMaxSize()) {
        // Top View Mode Switcher Tab
        TabRow(
            selectedTabIndex = viewMode,
            containerColor = Color.White,
            contentColor = UnionRed,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[viewMode]),
                    color = UnionRed
                )
            }
        ) {
            Tab(
                selected = viewMode == 0,
                onClick = {
                    viewMode = 0
                    selectedDistrictDetail = null
                },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FilterList, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("மாவட்ட வடிகட்டி & பட்டியல்", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            )
            Tab(
                selected = viewMode == 1,
                onClick = { viewMode = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.GridView, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("மாவட்ட அட்டவணை (${districtGroups.size})", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            )
        }

        if (viewMode == 0) {
            // UI COMPONENT: LazyColumn with Dropdown Menu to Filter and Display Members Categorized by Districts
            DistrictFilteredMemberList(
                cards = cards,
                onSelectCardForPreview = onSelectCardForPreview,
                onSelectCardForEdit = onSelectCardForEdit
            )
        } else {
            // DISTRICT GRID & EXPLORER VIEW
            if (selectedDistrictDetail == null) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "மாவட்ட வாரியான அட்டவணை",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = UnionRed
                                    )
                                    Text(
                                        text = "தமிழ்நாடு முழுவதிலும் உள்ள உறுப்பினர் விபரங்கள்",
                                        fontSize = 11.sp,
                                        color = Color(0xFF475569)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(UnionRed)
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = "${districtGroups.size} மாவட்டங்கள்",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = districtSearchQuery,
                            onValueChange = { districtSearchQuery = it },
                            placeholder = { Text("மாவட்டம் மூலம் தேடுக (Search District)...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UnionRed) },
                            trailingIcon = {
                                if (districtSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { districtSearchQuery = "" }) {
                                        Icon(Icons.Default.Clear, contentDescription = null)
                                    }
                                }
                            },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    items(allActiveDistricts) { districtName ->
                        val districtCards = districtGroups[districtName] ?: emptyList()
                        val memberCount = districtCards.count { it.cardType == "MEMBER" }
                        val execCount = districtCards.count { it.cardType == "EXECUTIVE" }
                        val conCount = districtCards.count { it.cardType == "CONTRACTOR" }
                        val pendingCount = districtCards.count { it.approvalStatus == "PENDING" }

                        DistrictGroupSummaryCard(
                            districtName = districtName,
                            totalCount = districtCards.size,
                            memberCount = memberCount,
                            execCount = execCount,
                            conCount = conCount,
                            pendingCount = pendingCount,
                            onClick = { selectedDistrictDetail = districtName }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            } else {
                val districtName = selectedDistrictDetail!!
                val districtCards = districtGroups[districtName] ?: emptyList()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = { selectedDistrictDetail = null }) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = UnionRed)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = "$districtName மாவட்டம்",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "மொத்த அடையாள அட்டைகள்: ${districtCards.size}",
                                    fontSize = 12.sp,
                                    color = UnionRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (onAddNewCardForDistrict != null) {
                                Button(
                                    onClick = { onAddNewCardForDistrict(districtName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("புதிய அட்டை", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            if (districtCards.isNotEmpty()) {
                                OutlinedButton(
                                    onClick = {
                                        shareDistrictReport(context, districtName, districtCards)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(Icons.Default.Description, contentDescription = null, tint = UnionNavy, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("மாவட்ட அறிக்கை", fontSize = 11.sp, color = UnionNavy, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }

                    if (districtCards.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "இந்த மாவட்டத்தில் இன்னும் அடையாள அட்டைகள் உருவாக்கப்படவில்லை.",
                                    color = Color(0xFF64748B),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    items(districtCards, key = { it.id }) { card ->
                        DistrictSingleMemberCard(
                            card = card,
                            onPreview = { onSelectCardForPreview(card) },
                            onEdit = { onSelectCardForEdit(card) },
                            onShare = {
                                val bmp = BitmapRendererHelper.renderCardToBitmap(context, card, isBack = false)
                                CardExporter.shareCardBitmap(context, bmp, "${card.name} - ${card.memberId}")
                            },
                            onDownload = {
                                val bmp = BitmapRendererHelper.renderCardToBitmap(context, card, isBack = false)
                                CardExporter.saveBitmapToGallery(context, bmp, "TNPA_${card.memberId}_${card.name.replace(" ", "_")}")
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictGroupSummaryCard(
    districtName: String,
    totalCount: Int,
    memberCount: Int,
    execCount: Int,
    conCount: Int,
    pendingCount: Int,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (totalCount > 0) Color(0xFFFFE4E6) else Color(0xFFF1F5F9)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = if (totalCount > 0) UnionRed else Color(0xFF94A3B8),
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = districtName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(text = "உறுப்பினர்: $memberCount", fontSize = 11.sp, color = Color(0xFF475569))
                        if (execCount > 0) Text(text = "பொறுப்பாளர்: $execCount", fontSize = 11.sp, color = UnionNavy, fontWeight = FontWeight.Bold)
                        if (conCount > 0) Text(text = "ஒப்பந்ததாரர்: $conCount", fontSize = 11.sp, color = UnionAmber, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (pendingCount > 0) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFEF3C7))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = "$pendingCount நிலுவை", fontSize = 9.5.sp, color = Color(0xFFB45309), fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (totalCount > 0) UnionRed else Color(0xFFE2E8F0))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = totalCount.toString(),
                        color = if (totalCount > 0) Color.White else Color(0xFF475569),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))
                Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFF94A3B8))
            }
        }
    }
}

@Composable
private fun DistrictSingleMemberCard(
    card: MemberCardEntity,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardPhotoFrame(
                    photoUri = card.photoUri,
                    avatarPreset = card.avatarPreset,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(6.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = card.name, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF1E293B))

                        val isApproved = card.approvalStatus == "APPROVED"
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isApproved) Color(0xFFECFDF5) else Color(0xFFFEF3C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isApproved) "அங்கீகரிக்கப்பட்டது" else "ஒப்புதல் நிலுவை",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isApproved) UnionGreen else Color(0xFFB45309)
                            )
                        }
                    }

                    Text(
                        text = "${card.memberId} | ${if (card.cardType == "CONTRACTOR") card.firmName else card.jobTitle}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569)
                    )
                    if (card.utrNumber.isNotBlank()) {
                        Text(
                            text = "UTR: ${card.utrNumber}",
                            fontSize = 10.sp,
                            color = Color(0xFF64748B),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onPreview, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = UnionRed, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = UnionGreen, modifier = Modifier.size(18.dp))
                }
                IconButton(onClick = onDownload, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = UnionAmber, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

private fun shareDistrictReport(
    context: Context,
    districtName: String,
    cards: List<MemberCardEntity>
) {
    try {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", java.util.Locale.getDefault())
        val distCode = com.example.util.DistrictCodeHelper.getDistrictCode(districtName)
        val sb = StringBuilder()
        sb.append("தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள் முன்னேற்ற சங்கம்\n")
        sb.append("$districtName மாவட்டம் ($distCode) - உறுப்பினர் பட்டியல் அறிக்கை\n")
        sb.append("தேதி: ${sdf.format(java.util.Date())}\n")
        sb.append("மொத்த அடையாள அட்டைகள்: ${cards.size}\n")
        sb.append("=========================================\n\n")

        cards.forEachIndexed { index, card ->
            val status = if (card.approvalStatus == "APPROVED") "அங்கீகரிக்கப்பட்டது ✓" else "ஒப்புதலுக்கு காத்திருக்கிறது ⏳"
            sb.append("${index + 1}. [${card.memberId}] ${card.name}\n")
            sb.append("   - தொழில்: ${card.jobTitle}\n")
            sb.append("   - தொலைபேசி: ${card.phone}\n")
            sb.append("   - முகவரி: ${card.address}\n")
            sb.append("   - நிலை: $status\n\n")
        }

        val sendIntent = android.content.Intent().apply {
            action = android.content.Intent.ACTION_SEND
            putExtra(android.content.Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        context.startActivity(android.content.Intent.createChooser(sendIntent, "$districtName மாவட்ட உறுப்பினர் அறிக்கை"))
    } catch (e: Exception) {
        e.printStackTrace()
        android.widget.Toast.makeText(context, "அறிக்கை பகிர்வதில் பிழை ஏற்பட்டது.", android.widget.Toast.LENGTH_SHORT).show()
    }
}
