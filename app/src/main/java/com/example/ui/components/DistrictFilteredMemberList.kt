package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Badge
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.BitmapRendererHelper
import com.example.util.CardExporter

const val ALL_DISTRICTS_FILTER = "அனைத்து மாவட்டங்கள் (All Districts)"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DistrictFilteredMemberList(
    cards: List<MemberCardEntity>,
    onSelectCardForPreview: (MemberCardEntity) -> Unit,
    onSelectCardForEdit: (MemberCardEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // State for Dropdown Menu
    var dropdownExpanded by remember { mutableStateOf(false) }
    var selectedDistrict by remember { mutableStateOf(ALL_DISTRICTS_FILTER) }
    var searchQuery by remember { mutableStateOf("") }
    var cardTypeFilter by remember { mutableStateOf("ALL") } // "ALL", "MEMBER", "EXECUTIVE", "CONTRACTOR"

    // Group cards by district
    val districtGroups = remember(cards) {
        cards.groupBy { it.district.trim().ifBlank { "பிற மாவட்டங்கள்" } }
    }

    // Available districts options for dropdown menu
    val availableDistricts = remember(cards) {
        val existing = districtGroups.keys.sorted()
        val allTN = TAMIL_NADU_DISTRICTS.map { it.substringBefore(" (") }
        listOf(ALL_DISTRICTS_FILTER) + (existing + allTN).distinct().sorted()
    }

    // Filter cards based on selected district, card type, and search query
    val filteredCards = remember(cards, selectedDistrict, cardTypeFilter, searchQuery) {
        cards.filter { card ->
            val matchesDistrict = if (selectedDistrict == ALL_DISTRICTS_FILTER) {
                true
            } else {
                card.district.trim().equals(selectedDistrict.trim(), ignoreCase = true)
            }

            val matchesType = when (cardTypeFilter) {
                "MEMBER" -> card.cardType == "MEMBER"
                "EXECUTIVE" -> card.cardType == "EXECUTIVE"
                "CONTRACTOR" -> card.cardType == "CONTRACTOR"
                else -> true
            }

            val matchesSearch = searchQuery.isBlank() ||
                    card.name.contains(searchQuery, ignoreCase = true) ||
                    card.memberId.contains(searchQuery, ignoreCase = true) ||
                    card.jobTitle.contains(searchQuery, ignoreCase = true) ||
                    card.district.contains(searchQuery, ignoreCase = true)

            matchesDistrict && matchesType && matchesSearch
        }
    }

    // Group the filtered results by district for categorized LazyColumn rendering
    val categorizedResults = remember(filteredCards) {
        filteredCards.groupBy { it.district.trim().ifBlank { "பிற மாவட்டங்கள்" } }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("district_filtered_member_list")
    ) {
        // 1. TOP FILTER CONTROLS (Dropdown Menu & Search)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Section Title with District Icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFE4E6)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationCity,
                                contentDescription = null,
                                tint = UnionRed,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "மாவட்ட வாரியான வடிகட்டி",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )
                    }

                    // Count Badge
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(UnionRed)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${filteredCards.size} உறுப்பினர்கள்",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // DROPDOWN MENU FOR SELECTING DISTRICT
                ExposedDropdownMenuBox(
                    expanded = dropdownExpanded,
                    onExpandedChange = { dropdownExpanded = !dropdownExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedDistrict,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("மாவட்டத்தை தேர்வு செய்க (Select District)") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "District",
                                tint = UnionRed
                            )
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded)
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnionRed,
                            focusedLabelColor = UnionRed,
                            unfocusedBorderColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                            .testTag("dropdown_district_selector")
                    )

                    ExposedDropdownMenu(
                        expanded = dropdownExpanded,
                        onDismissRequest = { dropdownExpanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        availableDistricts.forEach { district ->
                            val count = if (district == ALL_DISTRICTS_FILTER) {
                                cards.size
                            } else {
                                districtGroups[district]?.size ?: 0
                            }

                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = district,
                                            fontWeight = if (district == selectedDistrict) FontWeight.Black else FontWeight.Medium,
                                            color = if (district == selectedDistrict) UnionRed else Color(0xFF1E293B),
                                            fontSize = 13.sp
                                        )
                                        if (count > 0) {
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(4.dp))
                                                    .background(if (district == selectedDistrict) Color(0xFFFFE4E6) else Color(0xFFF1F5F9))
                                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (district == selectedDistrict) UnionRed else Color(0xFF64748B)
                                                )
                                            }
                                        }
                                    }
                                },
                                onClick = {
                                    selectedDistrict = district
                                    dropdownExpanded = false
                                },
                                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar inside filter
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("பெயர், அட்டை எண் மூலம் தேடுக...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF64748B)) },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnionRed,
                        unfocusedBorderColor = Color(0xFFE2E8F0)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Card Type Filter Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = cardTypeFilter == "ALL",
                        onClick = { cardTypeFilter = "ALL" },
                        label = { Text("அனைத்தும்", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = UnionRed,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = cardTypeFilter == "MEMBER",
                        onClick = { cardTypeFilter = "MEMBER" },
                        label = { Text("உறுப்பினர்", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFFE4E6),
                            selectedLabelColor = UnionRed
                        )
                    )
                    FilterChip(
                        selected = cardTypeFilter == "EXECUTIVE",
                        onClick = { cardTypeFilter = "EXECUTIVE" },
                        label = { Text("பொறுப்பாளர்", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFEFF6FF),
                            selectedLabelColor = UnionNavy
                        )
                    )
                    FilterChip(
                        selected = cardTypeFilter == "CONTRACTOR",
                        onClick = { cardTypeFilter = "CONTRACTOR" },
                        label = { Text("ஒப்பந்ததாரர்", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFFFEF3C7),
                            selectedLabelColor = Color(0xFFB45309)
                        )
                    )
                }
            }
        }

        // 2. LAZYCOLUMN DISPLAYING MEMBERS CATEGORIZED BY DISTRICTS
        if (filteredCards.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = null,
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(44.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (selectedDistrict != ALL_DISTRICTS_FILTER) {
                            "'$selectedDistrict' மாவட்டத்தில் உறுப்பினர்கள் இல்லை"
                        } else {
                            "தேடலுக்குரிய உறுப்பினர்கள் இல்லை"
                        },
                        color = Color(0xFF64748B),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
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
                // Categorized District Sections
                categorizedResults.forEach { (districtName, districtMembers) ->
                    // District Header Category Pill
                    item(key = "header_$districtName") {
                        DistrictCategoryHeader(
                            districtName = districtName,
                            memberCount = districtMembers.size
                        )
                    }

                    // Members in this district category
                    items(districtMembers, key = { it.id }) { card ->
                        DistrictMemberCardRow(
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
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }
}

@Composable
private fun DistrictCategoryHeader(
    districtName: String,
    memberCount: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(UnionRed)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "$districtName மாவட்டம்",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color(0xFF0F172A)
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFFE2E8F0))
                .padding(horizontal = 8.dp, vertical = 2.dp)
        ) {
            Text(
                text = "$memberCount அட்டைகள்",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF475569)
            )
        }
    }
}

@Composable
private fun DistrictMemberCardRow(
    card: MemberCardEntity,
    onPreview: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onDownload: () -> Unit
) {
    val (badgeBg, badgeColor, typeLabel) = when (card.cardType) {
        "EXECUTIVE" -> Triple(Color(0xFFEFF6FF), UnionNavy, "பொறுப்பாளர்")
        "CONTRACTOR" -> Triple(Color(0xFFFEF3C7), Color(0xFFB45309), "ஒப்பந்ததாரர்")
        else -> Triple(Color(0xFFFFE4E6), UnionRed, "உறுப்பினர்")
    }
    val isApproved = card.approvalStatus == "APPROVED"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("district_member_item_${card.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Thumbnail
                CardPhotoFrame(
                    photoUri = card.photoUri,
                    avatarPreset = card.avatarPreset,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    borderColor = badgeColor,
                    borderWidth = 1.2.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.name,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )

                        // Type Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(badgeBg)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = typeLabel,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = badgeColor
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${card.memberId} | ${if (card.cardType == "CONTRACTOR") card.firmName else card.jobTitle}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = UnionRed, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = card.district, fontSize = 10.5.sp, color = Color(0xFF64748B))

                            if (card.phone.isNotBlank()) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(11.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = card.phone, fontSize = 10.5.sp, color = Color(0xFF64748B))
                            }
                        }

                        // Approval Status Indicator
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isApproved) Icons.Default.CheckCircle else Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = if (isApproved) UnionGreen else Color(0xFFB45309),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = if (isApproved) "அப்ரூவ்டு" else "நிலுவை",
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isApproved) UnionGreen else Color(0xFFB45309)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFAFAFA), RoundedCornerShape(6.dp))
                    .border(width = 0.8.dp, color = Color(0xFFF1F5F9), shape = RoundedCornerShape(6.dp))
                    .padding(horizontal = 4.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onPreview, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = UnionRed, modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color(0xFF2563EB), modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onShare, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = UnionGreen, modifier = Modifier.size(17.dp))
                }
                IconButton(onClick = onDownload, modifier = Modifier.size(32.dp)) {
                    Icon(Icons.Default.Download, contentDescription = "Download", tint = UnionAmber, modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}
