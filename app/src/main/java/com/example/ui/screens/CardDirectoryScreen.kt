package com.example.ui.screens

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SupervisorAccount
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.BitmapRendererHelper
import com.example.util.CardExporter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CardDirectoryScreen(
    cards: List<MemberCardEntity>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    selectedFilter: String, // "ALL", "MEMBER", "EXECUTIVE", "CONTRACTOR", "APPROVED", "PENDING"
    onFilterChange: (String) -> Unit,
    onSelectCardForEdit: (MemberCardEntity) -> Unit,
    onSelectCardForPreview: (MemberCardEntity) -> Unit,
    onDeleteCard: (MemberCardEntity) -> Unit,
    onDuplicateCard: (MemberCardEntity) -> Unit,
    onCreateNewCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var cardToDelete by remember { mutableStateOf<MemberCardEntity?>(null) }

    // Multi-field instant search & filter algorithm
    val filteredCards = remember(cards, searchQuery, selectedFilter) {
        val query = searchQuery.trim().lowercase()
        cards.filter { card ->
            val matchesFilter = when (selectedFilter) {
                "MEMBER" -> card.cardType == "MEMBER"
                "EXECUTIVE" -> card.cardType == "EXECUTIVE"
                "CONTRACTOR" -> card.cardType == "CONTRACTOR"
                "APPROVED" -> card.approvalStatus == "APPROVED"
                "PENDING" -> card.approvalStatus == "PENDING"
                else -> true
            }

            val matchesQuery = if (query.isBlank()) {
                true
            } else {
                card.name.lowercase().contains(query) ||
                card.memberId.lowercase().contains(query) ||
                card.district.lowercase().contains(query) ||
                card.fatherName.lowercase().contains(query) ||
                card.jobTitle.lowercase().contains(query) ||
                card.designation.lowercase().contains(query) ||
                card.firmName.lowercase().contains(query) ||
                card.phone.contains(query) ||
                (card.aadhaarNumber?.contains(query) == true) ||
                card.address.lowercase().contains(query) ||
                card.utrNumber.lowercase().contains(query)
            }

            matchesFilter && matchesQuery
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // 1. STATS SUMMARY CARDS
            item {
                StatsHeader(
                    totalCount = cards.size,
                    memberCount = cards.count { it.cardType == "MEMBER" },
                    executiveCount = cards.count { it.cardType == "EXECUTIVE" },
                    contractorCount = cards.count { it.cardType == "CONTRACTOR" }
                )
            }

            // 2. SEARCH BAR WITH CLEAR BUTTON & INSTANT QUERY
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = onSearchChange,
                            placeholder = {
                                Text(
                                    text = "பெயர் அல்லது பதிவு எண் மூலம் தேடுக...",
                                    fontSize = 12.5.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = UnionRed,
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { onSearchChange("") }) {
                                        Icon(
                                            imageVector = Icons.Default.Clear,
                                            contentDescription = "Clear Search",
                                            tint = Color(0xFF64748B)
                                        )
                                    }
                                }
                            },
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                            keyboardActions = KeyboardActions(onSearch = { focusManager.clearFocus() }),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                unfocusedBorderColor = Color(0xFFCBD5E1),
                                focusedLabelColor = UnionRed
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("search_directory_input")
                        )

                        // Quick Search Suggestion Tags
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "விரைவுத் தேடல்:",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF64748B)
                            )
                            listOf("மதுரை", "சென்னை", "TN-MDU", "TN-CHN", "ஓவியர்").forEach { tag ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (searchQuery == tag) UnionRed else Color(0xFFF1F5F9))
                                        .clickable { onSearchChange(if (searchQuery == tag) "" else tag) }
                                        .padding(horizontal = 7.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (searchQuery == tag) Color.White else Color(0xFF334155)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 3. FILTER CHIPS & RESULTS COUNTER
            item {
                Column {
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == "ALL",
                            onClick = { onFilterChange("ALL") },
                            label = { Text("அனைத்தும் (${cards.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = UnionRed,
                                selectedLabelColor = Color.White
                            ),
                            modifier = Modifier.testTag("filter_all")
                        )
                        FilterChip(
                            selected = selectedFilter == "MEMBER",
                            onClick = { onFilterChange("MEMBER") },
                            label = { Text("உறுப்பினர்கள் (${cards.count { it.cardType == "MEMBER" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFFE4E6),
                                selectedLabelColor = UnionRed
                            ),
                            modifier = Modifier.testTag("filter_member")
                        )
                        FilterChip(
                            selected = selectedFilter == "EXECUTIVE",
                            onClick = { onFilterChange("EXECUTIVE") },
                            label = { Text("பொறுப்பாளர்கள் (${cards.count { it.cardType == "EXECUTIVE" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFEFF6FF),
                                selectedLabelColor = UnionNavy
                            ),
                            modifier = Modifier.testTag("filter_executive")
                        )
                        FilterChip(
                            selected = selectedFilter == "CONTRACTOR",
                            onClick = { onFilterChange("CONTRACTOR") },
                            label = { Text("ஒப்பந்ததாரர்கள் (${cards.count { it.cardType == "CONTRACTOR" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFFEF3C7),
                                selectedLabelColor = Color(0xFFB45309)
                            ),
                            modifier = Modifier.testTag("filter_contractor")
                        )
                        FilterChip(
                            selected = selectedFilter == "APPROVED",
                            onClick = { onFilterChange("APPROVED") },
                            label = { Text("அப்ரூவ்டு (${cards.count { it.approvalStatus == "APPROVED" }})", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFECFDF5),
                                selectedLabelColor = UnionGreen
                            ),
                            modifier = Modifier.testTag("filter_approved")
                        )
                    }

                    // Search Results Count Bar
                    if (searchQuery.isNotBlank() || selectedFilter != "ALL") {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFEFF6FF), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "🔍 தேடல் முடிவுகள்: ${filteredCards.size} உறுப்பினர்கள்",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnionNavy
                            )

                            Text(
                                text = "தேடலை அழிக்க",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnionRed,
                                modifier = Modifier.clickable {
                                    onSearchChange("")
                                    onFilterChange("ALL")
                                }
                            )
                        }
                    }
                }
            }

            // 4. EMPTY SEARCH RESULTS STATE
            if (filteredCards.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 36.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.FilterList,
                                contentDescription = null,
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "‘$searchQuery’ என்ற பெயரில் உறுப்பினர்கள் காணப்படவில்லை" else "உறுப்பினர்கள் பதிவேடு காலியாக உள்ளது",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF475569)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "பெயர் அல்லது 4 இலக்க பதிவு எண்ணை சரிபார்த்து மீண்டும் தேடவும்",
                                fontSize = 11.sp,
                                color = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Button(
                                onClick = {
                                    onSearchChange("")
                                    onFilterChange("ALL")
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = UnionRed)
                            ) {
                                Text("அனைத்து உறுப்பினர்களையும் காட்டு", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 5. MEMBER CARDS DIRECTORY LIST
            items(filteredCards, key = { it.id }) { memberCard ->
                MemberDirectoryCardItem(
                    card = memberCard,
                    onEdit = { onSelectCardForEdit(memberCard) },
                    onPreview = { onSelectCardForPreview(memberCard) },
                    onDelete = { cardToDelete = memberCard },
                    onDuplicate = { onDuplicateCard(memberCard) },
                    onShare = {
                        val bmp = BitmapRendererHelper.renderCardToBitmap(context, memberCard, isBack = false)
                        CardExporter.shareCardBitmap(context, bmp, "${memberCard.name} - ${memberCard.memberId}")
                    }
                )
            }

            item {
                Spacer(modifier = Modifier.height(60.dp))
            }
        }

        // Floating Action Button to Add New Card
        FloatingActionButton(
            onClick = onCreateNewCard,
            containerColor = UnionRed,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("fab_add_card")
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Member Card")
        }
    }

    // Delete Confirmation Dialog
    if (cardToDelete != null) {
        val target = cardToDelete!!
        AlertDialog(
            onDismissRequest = { cardToDelete = null },
            title = {
                Text(
                    text = "அடையாள அட்டையை நீக்கவா?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "${target.name} (${target.memberId}) அவர்களின் அடையாள அட்டை விவரங்களை நிரந்தரமாக நீக்க விரும்புகிறீர்களா?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCard(target)
                        cardToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("நீக்குக (Delete)", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToDelete = null }) {
                    Text("ரத்து (Cancel)")
                }
            }
        )
    }
}

@Composable
private fun StatsHeader(
    totalCount: Int,
    memberCount: Int,
    executiveCount: Int,
    contractorCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatPill("மொத்தம்", totalCount.toString(), UnionRed, Modifier.weight(1f))
        StatPill("உறுப்பினர்", memberCount.toString(), Color(0xFFDC2626), Modifier.weight(1f))
        StatPill("நிர்வாகி", executiveCount.toString(), UnionNavy, Modifier.weight(1f))
        StatPill("ஒப்பந்ததாரர்", contractorCount.toString(), Color(0xFFB45309), Modifier.weight(1.1f))
    }
}

@Composable
private fun StatPill(
    label: String,
    count: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count,
                color = color,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = label,
                color = Color(0xFF64748B),
                fontSize = 9.5.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun MemberDirectoryCardItem(
    card: MemberCardEntity,
    onEdit: () -> Unit,
    onPreview: () -> Unit,
    onDelete: () -> Unit,
    onDuplicate: () -> Unit,
    onShare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("member_card_item_${card.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Card Type & Approval Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                when (card.cardType) {
                                    "EXECUTIVE" -> UnionNavy
                                    "CONTRACTOR" -> Color(0xFFB45309)
                                    else -> UnionRed
                                }
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (card.cardType) {
                                "EXECUTIVE" -> "பொறுப்பாளர்"
                                "CONTRACTOR" -> "ஒப்பந்ததாரர்"
                                else -> "உறுப்பினர்"
                            },
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Approval Chip
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (card.approvalStatus == "APPROVED") Color(0xFFECFDF5) else Color(0xFFFEF3C7)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (card.approvalStatus == "APPROVED") "✓ அப்ரூவ்டு" else "⏳ நிலுவை",
                            color = if (card.approvalStatus == "APPROVED") UnionGreen else Color(0xFFB45309),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Member ID (District Coded)
                Text(
                    text = card.memberId,
                    color = UnionRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Frame (No synthetic avatars)
                CardPhotoFrame(
                    photoUri = card.photoUri,
                    avatarPreset = 0,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    borderColor = UnionRed,
                    borderWidth = 1.2.dp
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = card.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = when (card.cardType) {
                            "EXECUTIVE" -> card.designation
                            "CONTRACTOR" -> card.firmName.ifBlank { "பெயிண்டிங் ஒப்பந்ததாரர்" }
                            else -> card.jobTitle
                        },
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = UnionRed, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = card.district, fontSize = 10.5.sp, color = Color(0xFF475569))
                        }

                        if (card.phone.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(2.dp))
                                Text(text = card.phone, fontSize = 10.5.sp, color = Color(0xFF475569))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(onClick = onPreview, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Visibility, contentDescription = "Preview", tint = UnionNavy, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onEdit, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = UnionAmber, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = UnionGreen, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDuplicate, modifier = Modifier.size(34.dp)) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", tint = Color(0xFF64748B), modifier = Modifier.size(18.dp))
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}
