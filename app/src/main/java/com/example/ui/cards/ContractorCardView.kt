package com.example.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.theme.UnionAmber

@Composable
fun ContractorCardView(
    card: MemberCardEntity,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f)
            .shadow(12.dp, RoundedCornerShape(10.dp)),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.dp, Color(0xFFD1D5DB), RoundedCornerShape(10.dp))
        ) {
            // HEADER BANNER
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF78350F), Color(0xFFB45309))
                        )
                    )
                    .padding(horizontal = 8.dp, vertical = 5.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_association_logo),
                        contentDescription = "TNPA Logo",
                        modifier = Modifier.size(34.dp)
                    )

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 4.dp)
                    ) {
                        Text(
                            text = "தமிழ்நாடு பெயிண்டர்கள் ஓவியர்கள்",
                            color = Color.White,
                            fontSize = 11.2.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "முன்னேற்ற சங்கம்",
                            color = Color.White,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "அங்கீகரிக்கப்பட்ட பெயிண்டிங் ஒப்பந்ததாரர் அட்டை",
                            color = Color.White,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Image(
                        painter = painterResource(id = R.drawable.ic_association_logo),
                        contentDescription = "TNPA Logo",
                        modifier = Modifier.size(34.dp)
                    )
                }
            }

            // BODY
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_association_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.Center)
                        .alpha(0.08f)
                )

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 6.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        ContractorDataRow(
                            label = "ஒப்பந்ததாரர் எண்",
                            value = card.memberId.ifBlank { "TN-CON-884" }
                        )
                        Spacer(modifier = Modifier.height(3.5.dp))
                        ContractorDataRow(
                            label = "ஒப்பந்ததாரர் பெயர்",
                            value = card.name.ifBlank { "ஆர். சண்முகம்" }
                        )
                        Spacer(modifier = Modifier.height(3.5.dp))
                        ContractorDataRow(
                            label = "நிறுவனப் பெயர்",
                            value = card.firmName.ifBlank { "ஸ்ரீ முருகன் பெயிண்டர்ஸ்" }
                        )
                        Spacer(modifier = Modifier.height(3.5.dp))
                        ContractorDataRow(
                            label = "தொடர்பு எண்",
                            value = card.phone.ifBlank { "9876543210" }
                        )
                    }

                    CardPhotoFrame(
                        photoUri = card.photoUri,
                        avatarPreset = 0,
                        modifier = Modifier
                            .width(68.dp)
                            .height(82.dp),
                        borderColor = UnionAmber,
                        borderWidth = 1.5.dp
                    )
                }
            }

            // SIGNATURES ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ContractorSigBlock(
                    title = "மாநிலத் தலைவர்",
                    signatureRes = R.drawable.ic_sig_president
                )
                ContractorSigBlock(
                    title = "பொதுச்செயலாளர்",
                    signatureRes = R.drawable.ic_sig_secretary
                )
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(78.dp)
                ) {
                    Text(
                        text = "அங்கீகரிக்கப்பட்ட நாள்",
                        fontSize = 6.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF9A3412)
                    )
                    Text(
                        text = card.authorizedDate.ifBlank { "01-04-2024" },
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B)
                    )
                }
            }

            // FOOTER BANNER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF78350F))
                    .padding(horizontal = 12.dp, vertical = 3.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "உழைப்போம்.......",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "உயர்வோம் ......",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ContractorDataRow(
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = Color(0xFF9A3412),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 9.5.sp,
            modifier = Modifier.width(102.dp)
        )
        Text(
            text = ": ",
            color = Color(0xFF9A3412),
            fontWeight = FontWeight.Black,
            fontSize = 9.5.sp
        )
        Text(
            text = value,
            color = Color(0xFF000000),
            fontWeight = FontWeight.Black,
            fontSize = 10.2.sp,
            maxLines = 1
        )
    }
}

@Composable
private fun ContractorSigBlock(
    title: String,
    signatureRes: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(78.dp)
    ) {
        Text(
            text = title,
            fontSize = 6.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9A3412)
        )
        Image(
            painter = painterResource(id = signatureRes),
            contentDescription = title,
            modifier = Modifier
                .height(14.dp)
                .width(55.dp)
        )
    }
}
