package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MemberCardEntity
import com.example.ui.components.CardFace
import com.example.ui.components.FlippableCardContainer
import com.example.ui.dialogs.CardDownloadExportDialog
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.BitmapRendererHelper
import com.example.util.CardExporter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun CardPreviewScreen(
    card: MemberCardEntity,
    cardFace: CardFace,
    onFlip: () -> Unit,
    onEdit: () -> Unit,
    onSave: () -> Unit,
    onOpenPayment: (() -> Unit)? = null,
    onOpenSuperAdmin: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var isSharingPdf by remember { mutableStateOf(false) }
    var showPendingApprovalDialog by remember { mutableStateOf(false) }

    val isApproved = card.approvalStatus == "APPROVED"

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // SUPER ADMIN APPROVAL STATUS BANNER
        if (!isApproved) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFF59E0B))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.HourglassTop,
                                contentDescription = null,
                                tint = Color(0xFFB45309),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "சூப்பர் அட்மின் ஒப்புதல் நிலுவையில் உள்ளது",
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF92400E)
                            )
                        }

                        Text(
                            text = "₹100 கட்டணம்",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            color = UnionRed
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "7010131915 இந்த எண்ணிற்கு ₹100 பணம் செலுத்தி UTR சமர்ப்பிக்கவும். சூப்பர் அட்மின் ஒப்புதலுக்கு பிறகு மட்டுமே இந்த அடையாள அட்டை பதிவிறக்கம் செய்ய இயலும்.",
                        fontSize = 10.5.sp,
                        color = Color(0xFF78350F),
                        lineHeight = 14.sp
                    )

                    if (card.utrNumber.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "சமர்ப்பிக்கப்பட்ட UTR: ${card.utrNumber} (சரிபார்ப்பு நடப்பில்)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = UnionNavy
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (onOpenPayment != null) {
                            Button(
                                onClick = onOpenPayment,
                                colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("₹100 செலுத்து / UTR உள்ளிடு", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        if (onOpenSuperAdmin != null) {
                            OutlinedButton(
                                onClick = onOpenSuperAdmin,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(38.dp)
                            ) {
                                Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = UnionNavy, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("சூப்பர் அட்மின்", fontSize = 10.5.sp, color = UnionNavy, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        } else {
            // Approved Success Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFECFDF5)),
                border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFF34D399))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = null,
                        tint = UnionGreen,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "அதிகாரப்பூர்வமாக அங்கீகரிக்கப்பட்ட அட்டை ✅",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF065F46)
                        )
                        Text(
                            text = "சூப்பர் அட்மின் ஒப்புதல் அளிக்கப்பட்டு முழு பதிவிறக்க வசதி திறக்கப்பட்டுள்ளது.",
                            fontSize = 10.5.sp,
                            color = Color(0xFF047857)
                        )
                    }
                }
            }
        }

        // Top Card Stage Frame
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp)),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isApproved) UnionGreen else Color(0xFFD97706))
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isApproved) "அங்கீகரிக்கப்பட்ட PVC அட்டை" else "ஒப்புதல் நிலுவை அட்டை (Pending)",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF475569)
                        )
                    }

                    if (card.cardType == "MEMBER") {
                        FilledTonalButton(
                            onClick = onFlip,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("btn_flip_card")
                        ) {
                            Icon(Icons.Default.Flip, contentDescription = null, modifier = Modifier.size(16.dp), tint = UnionRed)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (cardFace == CardFace.Front) "பின்பக்கம் (Flip)" else "முன்பக்கம் (Flip)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnionRed
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Flippable PVC Card View
                FlippableCardContainer(
                    card = card,
                    cardFace = cardFace,
                    onFlip = onFlip,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )

                if (card.cardType == "MEMBER") {
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "💡 அட்டையைத் தொட்டு முன்பக்கம் / பின்பக்கம் சுழற்றலாம் (Tap card to flip)",
                        fontSize = 10.5.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card Info Strip
        Card(
            modifier = Modifier.fillMaxWidth(),
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
                Column {
                    Text(
                        text = card.name.ifBlank { "உறுப்பினர் பெயர்" },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Text(
                        text = "${card.memberId} | ${card.district}",
                        fontSize = 12.sp,
                        color = UnionRed,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isApproved) Icons.Default.Verified else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isApproved) UnionGreen else Color(0xFFB45309),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isApproved) "அங்கீகரிக்கப்பட்டது" else "ஒப்புதல் நிலுவை",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isApproved) UnionGreen else Color(0xFFB45309)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // =========================================================================
        // ADMINISTRATOR INSTANT SHARE & EXPORT PANEL
        // =========================================================================
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.2.dp, if (isApproved) Color(0xFFBBF7D0) else Color(0xFFFDE68A), RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = if (isApproved) Color(0xFFF0FDF4) else Color(0xFFFFFBEB)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(if (isApproved) UnionGreen else Color(0xFFD97706)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "அடையாள அட்டை ஏற்றுமதி & பகிர்வு (Export & Share)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isApproved) Color(0xFF166534) else Color(0xFF92400E)
                            )
                            Text(
                                text = if (isApproved) "வாட்ஸ்அப் & PDF உயர் தெளிவுத்திறன் பகிர்வு" else "சூப்பர் அட்மின் ஒப்புதலுக்குப் பின் திறக்கப்படும்",
                                fontSize = 10.sp,
                                color = if (isApproved) Color(0xFF15803D) else Color(0xFFB45309)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action 1: Direct High-Quality PDF Share
                Button(
                    onClick = {
                        if (!isApproved) {
                            showPendingApprovalDialog = true
                        } else {
                            scope.launch {
                                isSharingPdf = true
                                withContext(Dispatchers.IO) {
                                    CardExporter.shareMemberCardPdfDirectly(context, card)
                                }
                                isSharingPdf = false
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isApproved) UnionNavy else Color(0xFF64748B)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_pdf_direct")
                ) {
                    Icon(
                        imageVector = if (isApproved) Icons.Default.PictureAsPdf else Icons.Default.Lock,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSharingPdf) "PDF தயாராகிறது..." else if (isApproved) "PDF ஆவணமாக பகிர்க (Share Official PDF)" else "PDF பகிர்வு (சூப்பர் அட்மின் ஒப்புதல் தேவை)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action 2 & 3: Direct Messaging Share Row (Front Card / Dual-Side Sheet)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            if (!isApproved) {
                                showPendingApprovalDialog = true
                            } else {
                                val bmp = BitmapRendererHelper.renderCardToBitmap(
                                    context,
                                    card,
                                    isBack = (cardFace == CardFace.Back && card.cardType == "MEMBER")
                                )
                                CardExporter.shareCardBitmap(
                                    context,
                                    bmp,
                                    "${card.name} - ${card.memberId}"
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (isApproved) UnionGreen else Color(0xFF64748B)),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("btn_share_image_messaging")
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("படம் பகிர்க (WhatsApp)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    if (card.cardType == "MEMBER") {
                        Button(
                            onClick = {
                                if (!isApproved) {
                                    showPendingApprovalDialog = true
                                } else {
                                    CardExporter.shareMemberCardDualSheetDirectly(context, card)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = if (isApproved) Color(0xFF047857) else Color(0xFF475569)),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("btn_share_dual_sheet")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("இருபக்க தாள் (Dual Sheet)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Secondary Action Buttons (Download Dialog, Edit & Save)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    if (!isApproved) {
                        showPendingApprovalDialog = true
                    } else {
                        showExportDialog = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isApproved) UnionRed else Color(0xFF64748B)),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_download_hd")
            ) {
                Icon(
                    imageVector = if (isApproved) Icons.Default.Download else Icons.Default.Lock,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isApproved) "பதிவிறக்க மெனு (Export)" else "பதிவிறக்கம் (Lock)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.5.sp
                )
            }

            OutlinedButton(
                onClick = onEdit,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_edit_from_preview")
            ) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("விவரம் திருத்து (Edit)", fontWeight = FontWeight.Bold, color = Color(0xFF2563EB), fontSize = 11.5.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onSave,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("btn_save_from_preview")
        ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = UnionAmber, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("பதிவேட்டில் சேமி (Save to Registry)", fontWeight = FontWeight.Bold, color = UnionAmber, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Pending Approval Warning Dialog
    if (showPendingApprovalDialog) {
        AlertDialog(
            onDismissRequest = { showPendingApprovalDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = UnionRed,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "சூப்பர் அட்மின் ஒப்புதல் தேவை!",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column {
                    Text(
                        text = "38 மாவட்ட மற்றும் மாநில பொறுப்பாளர்கள் கவனத்திற்கு:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "7010131915 இந்த எண்ணிற்கு ₹100 பணம் அனுப்பிய பின், கிடைக்கும் UTR எண்ணை பதிவு செய்து சமர்ப்பிக்கவும். சூப்பர் அட்மின் ஒப்புதல் அளித்த பிறகே அடையாள அட்டை PDF மற்றும் படங்கள் பதிவிறக்கம் செய்ய முடியும்.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showPendingApprovalDialog = false
                        onOpenPayment?.invoke()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnionRed)
                ) {
                    Text("₹100 செலுத்து & UTR பதிவு செய்")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPendingApprovalDialog = false }) {
                    Text("சரி (OK)")
                }
            }
        )
    }

    // Export & Download Dialog
    if (showExportDialog) {
        CardDownloadExportDialog(
            card = card,
            onDismiss = { showExportDialog = false }
        )
    }
}
