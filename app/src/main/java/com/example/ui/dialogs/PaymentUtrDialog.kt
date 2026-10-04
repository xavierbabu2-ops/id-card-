package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.MemberCardEntity
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed
import com.example.util.QRCodeGenerator

const val REGISTERED_UPI_NUMBER = "7010131915"
const val REGISTERED_UPI_ID = "7010131915@upi"
const val REGISTRATION_FEE_INR = "100"

@Composable
fun PaymentUtrDialog(
    card: MemberCardEntity,
    onSubmitUtr: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var utrInput by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }

    val upiQrBitmap = remember {
        val upiUri = "upi://pay?pa=$REGISTERED_UPI_NUMBER@paytm&pn=TN_Painters_Association&am=100.00&cu=INR&tn=ID_Card_Fee_${card.memberId}"
        QRCodeGenerator.generateQRBitmap(upiUri, 220)
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(UnionRed)
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = if (card.cardType == "EXECUTIVE") "பொறுப்பாளர் அட்டை கட்டணம் & UTR" else "உறுப்பினர் அட்டை கட்டணம் & UTR",
                                    color = Color.White,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "அடையாள அட்டை கட்டணம்: ₹100",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    // Applicant details banner
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF1F2)),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = card.name.ifBlank { "விண்ணப்பதாரர்" },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF1E293B)
                                )
                                Text(
                                    text = "${card.memberId} | ${card.district} • ${if (card.cardType == "EXECUTIVE") "பொறுப்பாளர் அட்டை" else "உறுப்பினர் அட்டை"}",
                                    fontSize = 11.sp,
                                    color = UnionRed,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "₹100",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = UnionRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 1: Payment instructions
                    Text(
                        text = "படி 1: ₹100 கட்டணம் செலுத்தவும் (GPay / PhonePe / Paytm)",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Registered Number Copy Box
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = androidx.compose.foundation.BorderStroke(1.2.dp, Color(0xFFCBD5E1))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "சங்கத்தின் பதிவு செய்யப்பட்ட மொபைல் எண்:",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B),
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = REGISTERED_UPI_NUMBER,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    color = UnionRed,
                                    letterSpacing = 1.sp
                                )

                                Button(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("UPI Number", REGISTERED_UPI_NUMBER)
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "எண் நகலெடுக்கப்பட்டது: $REGISTERED_UPI_NUMBER", Toast.LENGTH_SHORT).show()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF1F5F9), contentColor = Color(0xFF1E293B)),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Copy", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "GPay / PhonePe / Paytm செயலிகளில் இந்த எண்ணிற்கு ₹100 அனுப்பவும்.",
                                fontSize = 10.5.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Pay via UPI App Direct Button & QR Option
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val uri = Uri.parse("upi://pay?pa=$REGISTERED_UPI_NUMBER@paytm&pn=TN_Painters_Association&am=100.00&cu=INR&tn=ID_Card_${card.memberId}")
                                    val intent = Intent(Intent.ACTION_VIEW, uri)
                                    context.startActivity(Intent.createChooser(intent, "Pay ₹100 via UPI"))
                                } catch (e: Exception) {
                                    Toast.makeText(context, "UPI செயலி கிடைக்கவில்லை, $REGISTERED_UPI_NUMBER எண்ணிற்கு அனுப்பவும்", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UnionGreen),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("btn_pay_upi_direct")
                        ) {
                            Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("UPI செயலியில் திறக்க", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // QR Code Center
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            bitmap = upiQrBitmap.asImageBitmap(),
                            contentDescription = "UPI QR Code",
                            modifier = Modifier
                                .size(64.dp)
                                .border(1.dp, Color(0xFFCBD5E1), RoundedCornerShape(4.dp))
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "QR ஸ்கேன் செய்து ₹100 செலுத்தலாம்",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E293B)
                            )
                            Text(
                                text = "பணம் செலுத்தியதும் கிடைக்கும் 12 இலக்க UTR எண்ணை கீழே உள்ளிடவும்.",
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                lineHeight = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Step 2: UTR Number Input
                    Text(
                        text = "படி 2: UTR / Reference எண்ணை பதிவு செய்யவும்:",
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = utrInput,
                        onValueChange = {
                            utrInput = it
                            isError = false
                        },
                        label = { Text("12 இலக்க UTR / Transaction ID") },
                        placeholder = { Text("எ.கா. 428910839211 அல்லது REF123456") },
                        singleLine = true,
                        isError = isError,
                        supportingText = {
                            if (isError) {
                                Text("சரியான UTR எண்ணை உள்ளிடவும் (Please enter valid UTR)", color = Color.Red)
                            } else {
                                Text("கூகுள் பே / போன்பே ரசீதில் உள்ள UTR எண்", fontSize = 10.sp, color = Color(0xFF64748B))
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UnionRed,
                            focusedLabelColor = UnionRed
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_utr_number")
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Submit Button -> Submits to Super Admin Approval
                    Button(
                        onClick = {
                            if (utrInput.trim().length < 4) {
                                isError = true
                            } else {
                                onSubmitUtr(utrInput.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("btn_submit_utr")
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("அட்மின் ஒப்புதலுக்கு சமர்ப்பிக்க (Submit for Approval)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
