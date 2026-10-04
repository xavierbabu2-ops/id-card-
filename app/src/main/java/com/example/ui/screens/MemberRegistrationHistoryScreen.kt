package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberCardEntity
import com.example.data.MemberRegistrationHistoryEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.components.TAMIL_NADU_DISTRICTS
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGold
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.DistrictCodeHelper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MemberRegistrationHistoryScreen(
    historyList: List<MemberRegistrationHistoryEntity>,
    allCards: List<MemberCardEntity>,
    onSelectCardForPreview: (MemberCardEntity) -> Unit,
    onSelectCardForEdit: (MemberCardEntity) -> Unit,
    onClearAllHistory: () -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedActionFilter by remember { mutableStateOf("ALL") }
    var selectedDistrictFilter by remember { mutableStateOf<String?>(null) }
    var districtDropdownExpanded by remember { mutableStateOf(false) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val filteredHistory = remember(historyList, searchQuery, selectedActionFilter, selectedDistrictFilter) {
        historyList.filter { item ->
            val matchQuery = searchQuery.isBlank() ||
                    item.memberName.contains(searchQuery, ignoreCase = true) ||
                    item.memberId.contains(searchQuery, ignoreCase = true) ||
                    item.district.contains(searchQuery, ignoreCase = true) ||
                    item.details.contains(searchQuery, ignoreCase = true) ||
                    item.actionTitleTamil.contains(searchQuery, ignoreCase = true)

            val matchAction = when (selectedActionFilter) {
                "NEW" -> item.actionType == "NEW_REGISTRATION"
                "AADHAAR" -> item.actionType == "AADHAAR_AUTO_FILL"
                "APPROVED" -> item.actionType == "APPROVED"
                "UPDATED" -> item.actionType == "CARD_UPDATED"
                else -> true
            }

            val matchDistrict = selectedDistrictFilter == null || item.district.trim().equals(selectedDistrictFilter!!.trim(), ignoreCase = true)

            matchQuery && matchAction && matchDistrict
        }
    }

    val districtCounts = remember(historyList) {
        historyList.groupBy { it.district.trim() }
    }

    val dateFormatter = remember { SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale("ta", "IN")) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFC))
            .testTag("member_registration_history_screen")
    ) {
        // Header Banner
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (onBack != null) {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = UnionRed)
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                        }

                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.History, contentDescription = null, tint = UnionRed, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "உறுப்பினர் பதிவு வரலாறு (History Log)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "மாவட்டம் வாரியாக பதிவு செய்யப்பட்ட விபரங்களின் காலவரிசை",
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(
                            onClick = {
                                shareHistoryReport(context, historyList)
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share Report", tint = UnionNavy)
                        }

                        IconButton(
                            onClick = { showClearConfirmDialog = true }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear All", tint = Color(0xFFEF4444))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatCard(
                        title = "மொத்த பதிவுகள்",
                        count = historyList.size.toString(),
                        bg = Color(0xFFEFF6FF),
                        textColor = UnionNavy,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = "பதிவான மாவட்டங்கள்",
                        count = "${districtCounts.size} / 38",
                        bg = Color(0xFFECFDF5),
                        textColor = UnionGreen,
                        modifier = Modifier.weight(1.2f)
                    )
                    StatCard(
                        title = "ஆதார் ஸ்கேன்",
                        count = historyList.count { it.actionType == "AADHAAR_AUTO_FILL" }.toString(),
                        bg = Color(0xFFFFFBEB),
                        textColor = Color(0xFFB45309),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Search & Filters Row
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("பெயர் / உறுப்பினர் எண் / மாவட்டம் தேடுக...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UnionRed, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color.Gray)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UnionRed,
                    unfocusedBorderColor = Color(0xFFE2E8F0),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("history_search_input")
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Action Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedActionFilter == "ALL",
                    onClick = { selectedActionFilter = "ALL" },
                    label = { Text("அனைத்தும் (${historyList.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFFE4E6),
                        selectedLabelColor = UnionRed
                    )
                )

                FilterChip(
                    selected = selectedActionFilter == "NEW",
                    onClick = { selectedActionFilter = "NEW" },
                    label = { Text("புதிய பதிவு", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFDCFCE7),
                        selectedLabelColor = UnionGreen
                    )
                )

                FilterChip(
                    selected = selectedActionFilter == "AADHAAR",
                    onClick = { selectedActionFilter = "AADHAAR" },
                    label = { Text("ஆதார் ஸ்கேன்", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFEFF6FF),
                        selectedLabelColor = UnionNavy
                    )
                )

                FilterChip(
                    selected = selectedActionFilter == "APPROVED",
                    onClick = { selectedActionFilter = "APPROVED" },
                    label = { Text("அங்கீகரிக்கப்பட்டது", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = Color(0xFFFEF3C7),
                        selectedLabelColor = Color(0xFFB45309)
                    )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // District Filter Dropdown
            ExposedDropdownMenuBox(
                expanded = districtDropdownExpanded,
                onExpandedChange = { districtDropdownExpanded = !districtDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = if (selectedDistrictFilter == null) "அனைத்து 38 மாவட்டங்கள் (All Districts)" else "மாவட்டம்: $selectedDistrictFilter",
                    onValueChange = {},
                    readOnly = true,
                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = UnionRed, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (selectedDistrictFilter != null) {
                                IconButton(onClick = { selectedDistrictFilter = null }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear District", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                }
                            }
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtDropdownExpanded)
                        }
                    },
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = UnionRed,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = districtDropdownExpanded,
                    onDismissRequest = { districtDropdownExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("அனைத்து 38 மாவட்டங்கள் (All Districts)", fontWeight = FontWeight.Bold) },
                        onClick = {
                            selectedDistrictFilter = null
                            districtDropdownExpanded = false
                        }
                    )

                    DistrictCodeHelper.ALL_38_DISTRICT_DETAILS.forEach { dist ->
                        val count = districtCounts[dist.tamilName]?.size ?: 0
                        DropdownMenuItem(
                            text = {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("${dist.tamilName} (${dist.code})", fontSize = 12.sp)
                                    if (count > 0) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(Color(0xFFFFE4E6))
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("$count பதிவுகள்", color = UnionRed, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            },
                            onClick = {
                                selectedDistrictFilter = dist.tamilName
                                districtDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // History Timeline List
        if (filteredHistory.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(30.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.History, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(54.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "பதிவு வரலாறு எதுவும் இல்லை",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B)
                    )
                    Text(
                        text = "உறுப்பினர்களை பதிவு செய்யும்போது வரலாறு தானாக சேமிக்கப்படும்.",
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "பதிவு செய்யப்பட்ட விவரங்கள் (${filteredHistory.size}):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )
                }

                items(filteredHistory, key = { it.id }) { item ->
                    val matchingCard = allCards.firstOrNull { it.memberId == item.memberId || it.id == item.cardId }

                    HistoryItemCard(
                        item = item,
                        dateFormatted = dateFormatter.format(Date(item.timestamp)),
                        matchingCard = matchingCard,
                        onViewCard = {
                            if (matchingCard != null) {
                                onSelectCardForPreview(matchingCard)
                            } else {
                                Toast.makeText(context, "அட்டை விவரம்: ${item.memberName} (${item.memberId})", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onEditCard = {
                            if (matchingCard != null) {
                                onSelectCardForEdit(matchingCard)
                            }
                        },
                        onShareItem = {
                            shareSingleHistoryItem(context, item)
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }

    // Clear Confirmation Dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = { Text("வரலாற்றை அழிக்கவா?", fontWeight = FontWeight.Bold) },
            text = { Text("பதிவு செய்யப்பட்ட அனைத்து உறுப்பினர் ஹிஸ்டரி பதிவுகளும் நீக்கப்படும். அடையாள அட்டைகள் அழியாது.") },
            confirmButton = {
                Button(
                    onClick = {
                        onClearAllHistory()
                        showClearConfirmDialog = false
                        Toast.makeText(context, "வரலாறு வெற்றிகரமாக அழிக்கப்பட்டது.", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("அழித்திடுக (Clear)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("ரத்து செய்")
                }
            }
        )
    }
}

@Composable
private fun HistoryItemCard(
    item: MemberRegistrationHistoryEntity,
    dateFormatted: String,
    matchingCard: MemberCardEntity?,
    onViewCard: () -> Unit,
    onEditCard: () -> Unit,
    onShareItem: () -> Unit
) {
    val (badgeBg, badgeText, badgeIcon) = when (item.actionType) {
        "NEW_REGISTRATION" -> Triple(Color(0xFFDCFCE7), UnionGreen, Icons.Default.CheckCircle)
        "AADHAAR_AUTO_FILL" -> Triple(Color(0xFFEFF6FF), UnionNavy, Icons.Default.AutoAwesome)
        "APPROVED" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), Icons.Default.Badge)
        "CARD_UPDATED" -> Triple(Color(0xFFF3E8FF), Color(0xFF7E22CE), Icons.Default.Description)
        "DELETED" -> Triple(Color(0xFFFFE4E6), Color(0xFFDC2626), Icons.Default.Delete)
        else -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Icons.Default.History)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("history_card_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Action Title + Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(badgeBg)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(badgeIcon, contentDescription = null, tint = badgeText, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = item.actionTitleTamil,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeText
                            )
                        }
                    }
                }

                Text(
                    text = dateFormatted,
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Row: Photo + Member Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CardPhotoFrame(
                    photoUri = item.photoUri ?: matchingCard?.photoUri,
                    avatarPreset = matchingCard?.avatarPreset ?: 1,
                    modifier = Modifier
                        .size(52.dp, 64.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    borderColor = UnionRed,
                    borderWidth = 1.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = item.memberName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(UnionRed)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = item.memberId,
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFFFEF3C7))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "மாவட்டம்: ${item.district} (${DistrictCodeHelper.getDistrictCode(item.district)})",
                                color = Color(0xFF92400E),
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (item.jobTitle.isNotBlank()) {
                            Text(
                                text = item.jobTitle,
                                fontSize = 10.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    if (item.details.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = item.details,
                            fontSize = 10.5.sp,
                            color = Color(0xFF334155),
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "பதிவு முறை: ${item.actor}",
                    fontSize = 10.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (matchingCard != null) {
                        OutlinedButton(
                            onClick = onViewCard,
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = null, tint = UnionNavy, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("அட்டை", fontSize = 10.sp, color = UnionNavy, fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onShareItem,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = Color(0xFF64748B), modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    count: String,
    bg: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bg)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(count, fontSize = 14.sp, fontWeight = FontWeight.Black, color = textColor)
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Medium, color = textColor.copy(alpha = 0.85f))
        }
    }
}

private fun shareHistoryReport(context: Context, historyList: List<MemberRegistrationHistoryEntity>) {
    try {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val sb = StringBuilder()
        sb.append("தமிழ்நாடு பெயிண்டர்கள் & ஓவியர்கள் முன்னேற்ற சங்கம்\n")
        sb.append("உறுப்பினர் பதிவு வரலாற்று அறிக்கை (Member Registration History Log)\n")
        sb.append("தேதி: ${sdf.format(Date())}\n")
        sb.append("மொத்த பதிவுகள்: ${historyList.size}\n")
        sb.append("=========================================\n\n")

        historyList.forEachIndexed { index, item ->
            sb.append("${index + 1}. [${item.memberId}] ${item.memberName}\n")
            sb.append("   - மாவட்டம்: ${item.district} (${DistrictCodeHelper.getDistrictCode(item.district)})\n")
            sb.append("   - பதிவு வகை: ${item.actionTitleTamil}\n")
            sb.append("   - நேரம்: ${sdf.format(Date(item.timestamp))}\n")
            sb.append("   - விவரம்: ${item.details}\n")
            sb.append("   - பதிவாளர்: ${item.actor}\n\n")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "உறுப்பினர் பதிவு வரலாற்றை பகிர்க"))
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "பகிர்வதில் பிழை ஏற்பட்டது.", Toast.LENGTH_SHORT).show()
    }
}

private fun shareSingleHistoryItem(context: Context, item: MemberRegistrationHistoryEntity) {
    try {
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm a", Locale.getDefault())
        val text = """
            தமிழ்நாடு பெயிண்டர்கள் & ஓவியர்கள் முன்னேற்ற சங்கம்
            உறுப்பினர் பதிவு விபரம்:
            ------------------------------------
            உறுப்பினர் எண்: ${item.memberId}
            பெயர்: ${item.memberName}
            மாவட்டம்: ${item.district}
            பதிவு நடவடிக்கை: ${item.actionTitleTamil}
            விவரம்: ${item.details}
            பதிவு நேரம்: ${sdf.format(Date(item.timestamp))}
            பதிவாளர்: ${item.actor}
        """.trimIndent()

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, text)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "${item.memberName} பதிவு விபரம்"))
    } catch (e: Exception) {
        e.printStackTrace()
    }
}
