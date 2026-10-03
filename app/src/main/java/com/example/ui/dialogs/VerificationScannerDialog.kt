package com.example.ui.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.MemberCardEntity
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionRed

@Composable
fun VerificationScannerDialog(
    allCards: List<MemberCardEntity>,
    onDismiss: () -> Unit
) {
    var searchId by remember { mutableStateOf("") }
    var verifiedCard by remember { mutableStateOf<MemberCardEntity?>(null) }
    var hasSearched by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFECFDF5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = UnionGreen,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "அட்டை சரிபார்ப்பு (Card Verification)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "TNPA² அதிகாரப்பூர்வ சரிபார்ப்பு",
                                fontSize = 10.5.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Search Box
                OutlinedTextField(
                    value = searchId,
                    onValueChange = {
                        searchId = it
                        hasSearched = false
                    },
                    label = { Text("உறுப்பினர் / ஒப்பந்ததாரர் எண் (ID / Reg No)") },
                    placeholder = { Text("எ.கா. TN-2024-001 அல்லது TN-CON-884") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = UnionGreen,
                        focusedLabelColor = UnionGreen
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        hasSearched = true
                        val query = searchId.trim()
                        val parsed = com.example.util.QRCodeGenerator.parseVerificationPayload(query)
                        val targetId = parsed["ID"] ?: query

                        verifiedCard = allCards.firstOrNull {
                            it.memberId.equals(targetId, ignoreCase = true) ||
                                    it.memberId.equals(query, ignoreCase = true) ||
                                    it.name.contains(query, ignoreCase = true) ||
                                    (parsed["NAME"] != null && it.name.contains(parsed["NAME"]!!, ignoreCase = true)) ||
                                    it.qrVerificationCode.contains(query, ignoreCase = true)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("உறுப்பினரை சரிபார்க்க (Verify Now)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick presets buttons to test verification easily
                Text(
                    text = "விரைவு சோதனை (Quick Test Samples):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF64748B)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    allCards.take(3).forEach { c ->
                        Button(
                            onClick = {
                                searchId = c.memberId
                                verifiedCard = c
                                hasSearched = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF1E293B)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(text = c.memberId, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Verification Result
                if (hasSearched) {
                    if (verifiedCard != null) {
                        val card = verifiedCard!!
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.5.dp, UnionGreen, RoundedCornerShape(12.dp)),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = UnionGreen, modifier = Modifier.size(24.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "அங்கீகரிக்கப்பட்ட அட்டை (VERIFIED)",
                                            fontSize = 12.5.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = UnionGreen
                                        )
                                    }

                                    Image(
                                        painter = painterResource(id = R.drawable.ic_official_stamp),
                                        contentDescription = "Stamp",
                                        modifier = Modifier.size(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Text(text = "பெயர் : ${card.name}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                                Text(text = "எண் : ${card.memberId}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = UnionRed)
                                Text(text = "வகை : ${card.cardType}", fontSize = 11.5.sp, color = Color(0xFF334155))
                                Text(text = "மாவட்டம் : ${card.district}", fontSize = 11.5.sp, color = Color(0xFF334155))
                                Text(text = "பதிவு எண் : 50-26-00044", fontSize = 11.5.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold)
                            }
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFFEF2F2), RoundedCornerShape(12.dp))
                                .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(12.dp))
                                .padding(14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "❌ இந்த எண் சங்க பதிவேட்டில் கிடைக்கவில்லை.\n(Card not found in registry)",
                                color = Color(0xFFDC2626),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}
