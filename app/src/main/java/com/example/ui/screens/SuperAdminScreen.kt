package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
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
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed

val DEFAULT_ADMIN_KEYS = listOf("TNPA7010", "7010131915", "admin123", "TNPA2024")

@Composable
fun SuperAdminScreen(
    cards: List<MemberCardEntity>,
    onApproveCard: (MemberCardEntity) -> Unit,
    onRejectCard: (MemberCardEntity, String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isAuthenticated by remember { mutableStateOf(false) }
    var enteredKey by remember { mutableStateOf("") }
    var customMasterKey by remember { mutableStateOf("TNPA7010") }
    var keyError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }

    var selectedTab by remember { mutableStateOf(0) } // 0: Pending, 1: Approved, 2: Rejected
    var adminSearchQuery by remember { mutableStateOf("") }
    var cardToReject by remember { mutableStateOf<MemberCardEntity?>(null) }
    var rejectReason by remember { mutableStateOf("") }
    var showKeyChangeDialog by remember { mutableStateOf(false) }

    if (!isAuthenticated) {
        // SUPER ADMIN LOGIN GATEWAY
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp)
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
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFE4E6)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = UnionRed,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "சூப்பர் அட்மின் போர்ட்டல்",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "Super Admin Approval & Verification Gateway",
                        fontSize = 11.5.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    OutlinedTextField(
                        value = enteredKey,
                        onValueChange = {
                            enteredKey = it
                            keyError = false
                        },
                        label = { Text("அட்மின் ரகசிய சாவி (Super Admin Key)") },
                        placeholder = { Text("TNPA7010 அல்லது 7010131915") },
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
                        singleLine = true,
                        isError = keyError,
                        supportingText = {
                            if (keyError) {
                                Text("தவறான அட்மின் சாவி (Incorrect Admin Key)", color = Color.Red)
                            } else {
                                Text("இயல்புநிலை சாவி: TNPA7010 அல்லது 7010131915", fontSize = 10.sp, color = Color(0xFF64748B))
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
                                Toast.makeText(context, "சூப்பர் அட்மின் வெற்றிகரமாக இணைக்கப்பட்டது", Toast.LENGTH_SHORT).show()
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
                        Text("உள்நுழைக (Enter Portal)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
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

        Column(modifier = modifier.fillMaxSize()) {
            // Header Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(UnionRed)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
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
                                text = "சூப்பர் அட்மின் கட்டுப்பாடு",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "₹100 கட்டணம் & அடையாள அட்டை ஒப்புதல்",
                                color = Color(0xFFFFD700),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { showKeyChangeDialog = true }) {
                            Icon(Icons.Default.Key, contentDescription = "Change Key", tint = Color.White)
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
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminStatBox(
                    title = "நிலுவை",
                    count = pendingCards.size.toString(),
                    color = Color(0xFFB45309),
                    bgColor = Color(0xFFFEF3C7),
                    modifier = Modifier.weight(1f)
                )
                AdminStatBox(
                    title = "அப்ரூவ்டு",
                    count = approvedCards.size.toString(),
                    color = UnionGreen,
                    bgColor = Color(0xFFECFDF5),
                    modifier = Modifier.weight(1f)
                )
                AdminStatBox(
                    title = "வருவாய்",
                    count = "₹$totalRevenue",
                    color = UnionRed,
                    bgColor = Color(0xFFFFE4E6),
                    modifier = Modifier.weight(1.2f)
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
                    text = { Text("நிலுவை (${pendingCards.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("அப்ரூவ்டு (${approvedCards.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("நிராகரிப்பு (${rejectedCards.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }

            // Active list
            val rawList = when (selectedTab) {
                0 -> pendingCards
                1 -> approvedCards
                else -> rejectedCards
            }

            val displayList = remember(rawList, adminSearchQuery) {
                if (adminSearchQuery.isBlank()) {
                    rawList
                } else {
                    val query = adminSearchQuery.trim().lowercase()
                    rawList.filter {
                        it.name.lowercase().contains(query) ||
                        it.memberId.lowercase().contains(query) ||
                        it.district.lowercase().contains(query) ||
                        it.utrNumber.lowercase().contains(query) ||
                        it.phone.contains(query)
                    }
                }
            }

            // Search Bar for Admin
            OutlinedTextField(
                value = adminSearchQuery,
                onValueChange = { adminSearchQuery = it },
                placeholder = { Text("பெயர், உறுப்பினர் எண், UTR மூலம் தேடுக...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = UnionRed, modifier = Modifier.size(18.dp)) },
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
                    unfocusedBorderColor = Color(0xFFCBD5E1)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            )

            if (displayList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = when (selectedTab) {
                            0 -> "நிலுவையில் எந்த அடையாள அட்டையும் இல்லை 🎉"
                            1 -> "அங்கீகரிக்கப்பட்ட அட்டைகள் எதுவும் இல்லை"
                            else -> "நிராகரிக்கப்பட்ட அட்டைகள் எதுவும் இல்லை"
                        },
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(displayList, key = { it.id }) { card ->
                        AdminApprovalCardItem(
                            card = card,
                            onApprove = { onApproveCard(card) },
                            onReject = { cardToReject = card }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(40.dp))
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
            title = { Text("அடையாள அட்டையை நிராகரிக்கவா?") },
            text = {
                Column {
                    Text("${target.name} (${target.memberId}) அவர்களின் விண்ணப்பத்தை நிராகரிப்பதற்கான காரணத்தை உள்ளிடவும்:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        placeholder = { Text("எ.கா. தவறான UTR எண் அல்லது பணம் வரவில்லை") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onRejectCard(target, rejectReason.ifBlank { "UTR எண் சரிபார்க்கப்படவில்லை" })
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

    // Key Change Dialog
    if (showKeyChangeDialog) {
        var newKey by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { showKeyChangeDialog = false },
            title = { Text("சூப்பர் அட்மின் சாவியை மாற்றவும்") },
            text = {
                Column {
                    Text("புதிய ரகசிய அட்மின் சாவியை உள்ளிடவும்:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newKey,
                        onValueChange = { newKey = it },
                        placeholder = { Text("புதிய சாவி") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newKey.isNotBlank()) {
                            customMasterKey = newKey.trim()
                            Toast.makeText(context, "சாவி மாற்றப்பட்டது!", Toast.LENGTH_SHORT).show()
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
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = count, fontSize = 16.sp, fontWeight = FontWeight.Black, color = color)
            Text(text = title, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF475569))
        }
    }
}

@Composable
private fun AdminApprovalCardItem(
    card: MemberCardEntity,
    onApprove: () -> Unit,
    onReject: () -> Unit
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
        Column(modifier = Modifier.padding(14.dp)) {
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

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = card.name,
                            fontSize = 15.sp,
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
                        text = "${card.memberId} | ${card.jobTitle}",
                        fontSize = 12.sp,
                        color = Color(0xFF475569)
                    )

                    if (card.phone.isNotBlank()) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = card.phone, fontSize = 11.sp, color = Color(0xFF64748B))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Payment & UTR Information Highlight Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F5F9))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "கட்டணம்: ₹100 (7010131915)", fontSize = 10.5.sp, color = Color(0xFF475569), fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "UTR: ", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
                            Text(
                                text = card.utrNumber.ifBlank { "நிலுவை (No UTR)" },
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = if (card.utrNumber.isNotBlank()) UnionNavy else Color.Red
                            )
                        }
                    }

                    if (card.utrNumber.isNotBlank()) {
                        Button(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("UTR", card.utrNumber)
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "UTR நகலெடுக்கப்பட்டது", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE2E8F0), contentColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.size(width = 65.dp, height = 30.dp)
                        ) {
                            Text("Copy", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            if (card.rejectionReason != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "காரணம்: ${card.rejectionReason}",
                    fontSize = 11.sp,
                    color = Color.Red,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons
            if (card.approvalStatus == "PENDING") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                            .testTag("btn_approve_${card.id}")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("அப்ரூவல் செய் (Approve)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(0.7f)
                            .height(42.dp)
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
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("அங்கீகரிக்கப்பட்டது (Approved)", fontSize = 12.sp, color = UnionGreen, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text("ரத்து செய் (Revoke)", fontSize = 10.sp, color = Color.Red)
                    }
                }
            } else {
                // Rejected
                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp)
                ) {
                    Text("மீண்டும் அப்ரூவல் செய்க (Re-Approve)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
