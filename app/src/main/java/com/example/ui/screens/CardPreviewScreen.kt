package com.example.ui.screens

import android.content.Context
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Flip
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showExportDialog by remember { mutableStateOf(false) }
    var isSharingPdf by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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
                                .background(UnionGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "300 DPI PVC அட்டை முன்னோட்டம் (Live Preview)",
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
                    Icon(Icons.Default.Verified, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (card.approvalStatus == "APPROVED") "அங்கீகரிக்கப்பட்டது" else "ஒப்புதல் நிலுவை",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (card.approvalStatus == "APPROVED") UnionGreen else Color(0xFFB45309)
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
                .border(1.2.dp, Color(0xFFBBF7D0), RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
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
                                .background(UnionGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "நிர்வாக பகிர்வு & ஏற்றுமதி (Admin Share)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF166534)
                            )
                            Text(
                                text = "வாட்ஸ்அப் / மெசேஜிங் ஆப்கள் & PDF நேரடி பகிர்வு",
                                fontSize = 10.sp,
                                color = Color(0xFF15803D)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action 1: Direct High-Quality PDF Share
                Button(
                    onClick = {
                        scope.launch {
                            isSharingPdf = true
                            withContext(Dispatchers.IO) {
                                CardExporter.shareMemberCardPdfDirectly(context, card)
                            }
                            isSharingPdf = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnionNavy),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("btn_share_pdf_direct")
                ) {
                    Icon(Icons.Default.PictureAsPdf, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSharingPdf) "PDF தயாராகிறது..." else "PDF ஆவணமாக பகிர்க (Share as High-Quality PDF)",
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
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
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
                                CardExporter.shareMemberCardDualSheetDirectly(context, card)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF047857)),
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
                onClick = { showExportDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .testTag("btn_download_hd")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("பதிவிறக்க மெனு (Export)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
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

    // Export & Download Dialog
    if (showExportDialog) {
        CardDownloadExportDialog(
            card = card,
            onDismiss = { showExportDialog = false }
        )
    }
}
