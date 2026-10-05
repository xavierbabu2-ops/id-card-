package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MemberCardEntity
import com.example.ui.components.CardFace
import com.example.ui.components.CardPhotoFrame
import com.example.ui.components.FlippableCardContainer
import com.example.ui.components.TAMIL_NADU_DISTRICTS
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGold
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed

val DEFAULT_ADMIN_KEYS = listOf("7010131915", "7010", "TNPA7010", "admin123", "admin", "Admin", "TNPA", "tnpa", "TNPA2026", "1915", "painter")

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SuperAdminScreen(
    cards: List<MemberCardEntity>,
    onApproveCard: (MemberCardEntity) -> Unit,
    onRejectCard: (MemberCardEntity, String) -> Unit,
    onBack: () -> Unit,
    onApproveAll: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBack()
    }

    val context = LocalContext.current
    val prefs = remember { context.getSharedPreferences("super_admin_prefs", Context.MODE_PRIVATE) }
    var customMasterKey by remember {
        mutableStateOf(prefs.getString("master_key", "7010131915") ?: "7010131915")
    }

    var isAuthenticated by remember { mutableStateOf(false) }
    var enteredKey by remember { mutableStateOf("") }
    var keyError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: Approved, 2: Rejected, 3: All
    var adminSearchQuery by remember { mutableStateOf("") }
    var selectedDistrictFilter by remember { mutableStateOf<String?>(null) }
    var districtDropdownExpanded by remember { mutableStateOf(false) }
    var selectedCardTypeFilter by remember { mutableStateOf("ALL") }

    var cardToReject by remember { mutableStateOf<MemberCardEntity?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    var previewCardModal by remember { mutableStateOf<MemberCardEntity?>(null) }
    var previewCardFace by remember { mutableStateOf(CardFace.Front) }
    var showKeyChangeDialog by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        // SUPER ADMIN LOGIN GATEWAY WITH PASSWORD PROTECTION
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFFF1F5F9))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp)
                    .testTag("admin_login_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = UnionRed,
                            modifier = Modifier.size(38.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "சூப்பர் அட்மின் நிர்வாக தளம்",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "38 மாவட்டங்கள் & மாநில பொறுப்பாளர்கள் ஒப்புதல் தளம்",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color(0xFFB45309), modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "7010131915 எண்ணிற்கு பெறப்பட்ட ₹100 UTR கட்டணத்தை சரிபார்த்து அடையாள அட்டைகளுக்கு ஒப்புதல் அளிக்கவும்.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF92400E),
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    OutlinedTextField(
                        value = enteredKey,
                        onValueChange = {
                            enteredKey = it
                            keyError = false
                        },
                        label = { Text("அட்மின் கடவுச்சொல் (Admin Password)") },
                        placeholder = { Text("கடவுச்சொல்லை உள்ளிடவும்") },
                        leadingIcon = { Icon(Icons.Default.Key, contentDescription = null, tint = UnionRed) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        isError = keyError,
                        supportingText = {
                            if (keyError) {
                                Text("தவறான கடவுச்சொல்! (Incorrect Password)", color = Color.Red, fontWeight = FontWeight.Bold)
                            } else {
                                Text("இயல்பு கடவுச்சொல்: 7010131915 அல்லது 7010", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnionRed,
                            focusedLabelColor = UnionRed
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_admin_key")
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val key = enteredKey.trim()
                            if (key == customMasterKey || DEFAULT_ADMIN_KEYS.contains(key)) {
                                isAuthenticated = true
                                Toast.makeText(context, "சூப்பர் அட்மின் வெற்றிகரமாக இணைக்கப்பட்டது!", Toast.LENGTH_SHORT).show()
                            } else {
                                keyError = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_admin_login")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("உள்நுழைக (Enter Super Admin)", fontWeight = FontWeight.Bold, fontSize = 13.5.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextButton(onClick = onBack) {
                        Text("பின்செல்ல (Back to App)", color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    } else {
        // SUPER ADMIN DASHBOARD
        val pendingCards = cards.filter { it.approvalStatus == "PENDING" }
        val approvedCards = cards.filter { it.approvalStatus == "APPROVED" }
        val rejectedCards = cards.filter { it.approvalStatus == "REJECTED" }
        val totalRevenue = approvedCards.size * 100

        Column(modifier = modifier.fillMaxSize().background(Color(0xFFF1F5F9))) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UnionRed)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text(
                                text = "சூப்பர் அட்மின் கட்டுப்பாட்டு தளம்",
                                color = Color.White,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "7010131915 - ₹100 கட்டணம் & 38 மாவட்ட ஒப்புதல்",
                                color = Color(0xFFFFD700),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { showKeyChangeDialog = true }) {
                            Icon(Icons.Default.Key, contentDescription = "Change Password", tint = Color.White)
                        }
                        IconButton(onClick = { isAuthenticated = false }) {
                            Icon(Icons.Default.Lock, contentDescription = "Lock", tint = Color.White)
                        }
                    }
                }
            }

            // Stats Cards
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                AdminStatBox(
                    title = "ஒப்புதல் நிலுவை",
                    count = pendingCards.size.toString(),
                    color = Color(0xFFB45309),
                    bgColor = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f)
                )
                AdminStatBox(
                    title = "அங்கீகரிப்பு",
                    count = approvedCards.size.toString(),
                    color = UnionGreen,
                    bgColor = Color(0xFFECFDF5),
                    modifier = Modifier.weight(1f)
                )
                AdminStatBox(
                    title = "நிராகரிப்பு",
                    count = rejectedCards.size.toString(),
                    color = Color(0xFFDC2626),
                    bgColor = Color(0xFFFEE2E2),
                    modifier = Modifier.weight(1f)
                )
                AdminStatBox(
                    title = "மொத்த நிதி (₹100)",
                    count = "₹$totalRevenue",
                    color = UnionRed,
                    bgColor = Color(0xFFFFE4E6),
                    modifier = Modifier.weight(1.3f)
                )
            }

            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = UnionRed,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = UnionRed
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        BadgedBox(badge = {
                            if (pendingCards.isNotEmpty()) {
                                Badge(containerColor = UnionRed) { Text("${pendingCards.size}") }
                            }
                        }) {
                            Text("நிலுவை", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("அங்கீகரிப்பு (${approvedCards.size})", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("நிராகரிப்பு (${rejectedCards.size})", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) }
                )
                Tab(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    text = { Text("அனைத்தும் (${cards.size})", fontWeight = FontWeight.Bold, fontSize = 11.5.sp) }
                )
            }

            // Active list
            val rawList = when (selectedTab) {
                0 -> pendingCards
                1 -> approvedCards
                2 -> rejectedCards
                else -> cards
            }

            // Filter Chips (Role Filter & District Filter)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // Card Type Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilterChip(
                        selected = selectedCardTypeFilter == "ALL",
                        onClick = { selectedCardTypeFilter = "ALL" },
                        label = { Text("அனைத்து அட்டை", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UnionRed, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = selectedCardTypeFilter == "MEMBER",
                        onClick = { selectedCardTypeFilter = "MEMBER" },
                        label = { Text("உறுப்பினர்", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UnionRed, selectedLabelColor = Color.White)
                    )
                    FilterChip(
                        selected = selectedCardTypeFilter == "EXECUTIVE",
                        onClick = { selectedCardTypeFilter = "EXECUTIVE" },
                        label = { Text("பொறுப்பாளர்", fontSize = 10.5.sp) },
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = UnionRed, selectedLabelColor = Color.White)
                    )
                }

                // District Dropdown Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ExposedDropdownMenuBox(
                        expanded = districtDropdownExpanded,
                        onExpandedChange = { districtDropdownExpanded = !districtDropdownExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedDistrictFilter ?: "அனைத்து 38 மாவட்டங்கள்",
                            onValueChange = {},
                            readOnly = true,
                            leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null, tint = UnionRed, modifier = Modifier.size(16.dp)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtDropdownExpanded) },
                            singleLine = true,
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = UnionRed,
                                unfocusedBorderColor = Color(0xFFCBD5E1)
                            ),
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )

                        ExposedDropdownMenu(
                            expanded = districtDropdownExpanded,
                            onDismissRequest = { districtDropdownExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("அனைத்து 38 மாவட்டங்கள்") },
                                onClick = {
                                    selectedDistrictFilter = null
                                    districtDropdownExpanded = false
                                }
                            )
                            TAMIL_NADU_DISTRICTS.forEach { district ->
                                DropdownMenuItem(
                                    text = { Text(district) },
                                    onClick = {
                                        selectedDistrictFilter = district
                                        districtDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (selectedDistrictFilter != null) {
                        IconButton(onClick = { selectedDistrictFilter = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear District", tint = Color.Red, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            val displayList = remember(rawList, adminSearchQuery, selectedDistrictFilter, selectedCardTypeFilter) {
                rawList.filter { card ->
                    val matchesQuery = adminSearchQuery.isBlank() ||
                            card.name.contains(adminSearchQuery, ignoreCase = true) ||
                            card.memberId.contains(adminSearchQuery, ignoreCase = true) ||
                            card.district.contains(adminSearchQuery, ignoreCase = true) ||
                            card.utrNumber.contains(adminSearchQuery, ignoreCase = true) ||
                            card.phone.contains(adminSearchQuery)

                    val matchesDistrict = selectedDistrictFilter == null || card.district == selectedDistrictFilter
                    val matchesType = selectedCardTypeFilter == "ALL" || card.cardType == selectedCardTypeFilter

                    matchesQuery && matchesDistrict && matchesType
                }
            }

            // Search Bar for Admin
            OutlinedTextField(
                value = adminSearchQuery,
                onValueChange = { adminSearchQuery = it },
                placeholder = { Text("பெயர், உறுப்பினர் எண், UTR மூலம் தேடுக...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = UnionRed, modifier = Modifier.size(18.dp)) },
                trailingIcon = {
                    if (adminSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { adminSearchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UnionRed,
                    unfocusedBorderColor = Color(0xFFCBD5E1),
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            )

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(30.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = when (selectedTab) {
                                0 -> "ஒப்புதலுக்கு நிலுவையில் எந்த அடையாள அட்டையும் இல்லை 🎉"
                                1 -> "அங்கீகரிக்கப்பட்ட அட்டைகள் எதுவும் இல்லை"
                                2 -> "நிராகரிக்கப்பட்ட அட்டைகள் எதுவும் இல்லை"
                                else -> "அட்டைகள் எதுவும் கிடைக்கவில்லை"
                            },
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF64748B),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (selectedTab == 0 && displayList.isNotEmpty() && onApproveAll != null) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF34D399))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "சூப்பர் அட்மின் ஒட்டுமொத்த ஒப்புதல்",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color(0xFF065F46)
                                        )
                                        Text(
                                            text = "${displayList.size} நிலுவை அட்டைகளும் காத்திருக்கின்றன",
                                            fontSize = 10.5.sp,
                                            color = Color(0xFF047857)
                                        )
                                    }
                                    Button(
                                        onClick = onApproveAll,
                                        colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("அனைத்தும் ஒப்புதல்", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    items(displayList, key = { it.id }) { card ->
                        AdminApprovalCardItem(
                            card = card,
                            onApprove = { onApproveCard(card) },
                            onReject = { cardToReject = card },
                            onPreview = {
                                previewCardModal = card
                                previewCardFace = CardFace.Front
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }
    }

    // Modal Live PVC Preview Dialog
    if (previewCardModal != null) {
        val targetCard = previewCardModal!!
        Dialog(onDismissRequest = { previewCardModal = null }) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "அட்டை முன்னோட்டம் (Admin Preview)",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1E293B)
                        )
                        IconButton(onClick = { previewCardModal = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    FlippableCardContainer(
                        card = targetCard,
                        cardFace = previewCardFace,
                        onFlip = {
                            previewCardFace = if (previewCardFace == CardFace.Front) CardFace.Back else CardFace.Front
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (targetCard.cardType == "MEMBER") {
                        TextButton(onClick = {
                            previewCardFace = if (previewCardFace == CardFace.Front) CardFace.Back else CardFace.Front
                        }) {
                            Text("அட்டையை சுழற்றுக (Flip Card Face)", fontWeight = FontWeight.Bold, color = UnionRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (targetCard.approvalStatus == "PENDING") {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    onApproveCard(targetCard)
                                    previewCardModal = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ஒப்புதல் அளி (Approve)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    cardToReject = targetCard
                                    previewCardModal = null
                                },
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.weight(1f).height(42.dp)
                            ) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("நிராகரி", fontSize = 11.5.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }

    // Rejection Dialog
    if (cardToReject != null) {
        val target = cardToReject!!
        AlertDialog(
            onDismissRequest = { cardToReject = null },
            title = {
                Text(
                    text = "அடையாள அட்டையை நிராகரிக்கவா?",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Red
                )
            },
            text = {
                Column {
                    Text("${target.name} (${target.memberId}) அவர்களின் விண்ணப்பத்தை நிராகரிப்பதற்கான காரணத்தை உள்ளிடவும்:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("எ.கா. 7010131915 எண்ணிற்கு ₹100 கட்டணம் வரவில்லை / UTR பொருந்தவில்லை") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectCard(target, rejectReason.ifBlank { "7010131915 எண்ணிற்கு ₹100 UTR சரிபார்க்கப்படவில்லை" })
                        cardToReject = null
                        rejectReason = ""
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("நிராகரி (Reject)")
                }
            },
            dismissButton = {
                TextButton(onClick = { cardToReject = null }) {
                    Text("ரத்து (Cancel)")
                }
            }
        )
    }

    // Master Password Change Dialog
    if (showKeyChangeDialog) {
        var newKeyInput by remember { mutableStateOf("") }
        var confirmKeyInput by remember { mutableStateOf("") }
        var changeError by remember { mutableStateOf<String?>(null) }

        AlertDialog(
            onDismissRequest = { showKeyChangeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = UnionRed)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("அட்மின் கடவுச்சொல் மாற்றம்", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Text("புதிய அட்மின் கடவுச்சொல்லை உள்ளிடவும்:", fontSize = 12.sp, color = Color(0xFF475569))
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = newKeyInput,
                        onValueChange = { newKeyInput = it; changeError = null },
                        label = { Text("புதிய கடவுச்சொல்") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = confirmKeyInput,
                        onValueChange = { confirmKeyInput = it; changeError = null },
                        label = { Text("மீண்டும் உறுதி செய்க") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (changeError != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(changeError!!, color = Color.Red, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKeyInput.trim().length < 4) {
                            changeError = "கடவுச்சொல் குறைந்தது 4 எழுத்துக்கள் இருக்க வேண்டும்."
                        } else if (newKeyInput.trim() != confirmKeyInput.trim()) {
                            changeError = "இரு கடவுச்சொற்களும் பொருந்தவில்லை."
                        } else {
                            val savedKey = newKeyInput.trim()
                            customMasterKey = savedKey
                            prefs.edit().putString("master_key", savedKey).apply()
                            Toast.makeText(context, "கடவுச்சொல் வெற்றிகரமாக மாற்றப்பட்டது!", Toast.LENGTH_SHORT).show()
                            showKeyChangeDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnionRed)
                ) {
                    Text("சேமிக்க (Save)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showKeyChangeDialog = false }) {
                    Text("ரத்து (Cancel)")
                }
            }
        )
    }
}

@Composable
private fun AdminStatBox(
    title: String,
    count: String,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 15.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = title, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569), textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun AdminApprovalCardItem(
    card: MemberCardEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onPreview: () -> Unit
) {
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_card_${card.id}"),
        shape = RoundedCornerShape(14.dp),
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
                        .size(54.dp)
                        .clip(RoundedCornerShape(8.dp))
                )

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.name.ifBlank { "உறுப்பினர் பெயர்" },
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1E293B)
                        )

                        Text(
                            text = card.district,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnionRed
                        )
                    }

                    Text(
                        text = "${card.memberId} • ${if (card.cardType == "EXECUTIVE") "பொறுப்பாளர் (${card.designation.ifBlank { card.jobTitle }})" else "உறுப்பினர் (${card.jobTitle})"}",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569),
                        fontWeight = FontWeight.Medium
                    )

                    if (card.phone.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(11.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = card.phone, fontSize = 10.5.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Payment & UTR Information Highlight Box (7010131915)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "கட்டணம்: ₹100 (7010131915 எண்ணிற்கு)", fontSize = 10.sp, color = Color(0xFF64748B), fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "UTR: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text(
                                text = card.utrNumber.ifBlank { "நிலுவை (No UTR)" },
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Black,
                                color = if (card.utrNumber.isNotBlank()) UnionNavy else Color.Red
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        if (card.utrNumber.isNotBlank()) {
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("UTR", card.utrNumber)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "UTR நகலெடுக்கப்பட்டது", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(30.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy UTR", tint = Color(0xFF475569), modifier = Modifier.size(16.dp))
                            }
                        }

                        IconButton(
                            onClick = onPreview,
                            modifier = Modifier.size(30.dp)
                        ) {
                            Icon(Icons.Default.Visibility, contentDescription = "Preview Card", tint = UnionNavy, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            if (card.rejectionReason != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "நிராகரிப்பு காரணம்: ${card.rejectionReason}",
                    fontSize = 10.5.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            if (card.approvalStatus == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("btn_approve_${card.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("அங்கீகரி (Approve)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.7f)
                            .height(40.dp)
                            .testTag("btn_reject_${card.id}")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color.Red, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("நிராகரி", fontSize = 11.sp, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (card.approvalStatus == "APPROVED") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("அங்கீகரிக்கப்பட்டது (Approved)", fontSize = 11.5.sp, color = UnionGreen, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("ரத்து (Revoke)", fontSize = 10.sp, color = Color.Red)
                    }
                }
            } else {
                // Rejected
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                ) {
                    Text("மீண்டும் அங்கீகரிக்க (Re-Approve)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
