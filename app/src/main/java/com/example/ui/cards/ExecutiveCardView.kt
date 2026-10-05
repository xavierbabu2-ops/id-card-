package com.example.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionDarkRed
import com.example.ui.theme.UnionRed

@Composable
fun ExecutiveCardView(
    card: MemberCardEntity,
    modifier: Modifier = Modifier,
    onLogoClick: (() -> Unit)? = null,
    onFlagClick: (() -> Unit)? = null,
    onPhotoClick: (() -> Unit)? = null,
    onNameOrAddressClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f) // Square portrait executive format
            .shadow(12.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .border(1.2.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp)),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // TOP RED GRADIENT HEADER WITH LOGO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(UnionDarkRed, UnionRed)
                        )
                    )
                    .padding(vertical = 6.dp, horizontal = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .then(
                                if (onLogoClick != null) Modifier.clickable { onLogoClick() } else Modifier
                            )
                    ) {
                        if (card.customLogoUri != null) {
                            AsyncImage(
                                model = card.customLogoUri,
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_association_logo),
                                contentDescription = "TNPA Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }

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
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Text(
                            text = "முன்னேற்ற சங்கம்",
                            color = Color.White,
                            fontSize = 12.8.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(1.dp))
                        Text(
                            text = "அரசு பதிவு எண் : TNMDUJCLMDUTU-50-26-00044",
                            color = Color.White,
                            fontSize = 7.2.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .then(
                                if (onLogoClick != null) Modifier.clickable { onLogoClick() } else Modifier
                            )
                    ) {
                        if (card.customLogoUri != null) {
                            AsyncImage(
                                model = card.customLogoUri,
                                contentDescription = "Logo",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_association_logo),
                                contentDescription = "TNPA Logo",
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                    }
                }
            }

            // MIDDLE STAGE WITH OFFICIAL FLAG IN BACKGROUND & LEADER PHOTO
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                // Background Flag (Clickable to change flag)
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .align(Alignment.Center)
                        .then(
                            if (onFlagClick != null) Modifier.clickable { onFlagClick() } else Modifier
                        )
                ) {
                    if (card.customFlagUri != null) {
                        AsyncImage(
                            model = card.customFlagUri,
                            contentDescription = "Custom Flag",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.22f)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.ic_association_flag),
                            contentDescription = "Association Flag",
                            modifier = Modifier
                                .fillMaxSize()
                                .alpha(0.18f)
                        )
                    }
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    // Photo Frame with gold border (Clickable for Camera / Gallery)
                    Box(
                        modifier = Modifier
                            .then(
                                if (onPhotoClick != null) Modifier.clickable { onPhotoClick() } else Modifier
                            )
                    ) {
                        CardPhotoFrame(
                            photoUri = card.photoUri,
                            avatarPreset = card.avatarPreset,
                            modifier = Modifier
                                .width(105.dp)
                                .height(125.dp)
                                .shadow(6.dp, RoundedCornerShape(6.dp)),
                            borderColor = UnionAmber,
                            borderWidth = 2.5.dp,
                            shape = RoundedCornerShape(6.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Name & Post (Clickable to open Aadhaar Scanner)
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.then(
                            if (onNameOrAddressClick != null) Modifier.clickable { onNameOrAddressClick() } else Modifier
                        )
                    ) {
                        Text(
                            text = card.name.ifBlank { "S. மைக்கேல் ஆல்வின்" },
                            color = Color(0xFFB45309),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = card.designation.ifBlank { "மாநிலத் தலைவர்" },
                            color = UnionRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )

                        if (card.district.isNotBlank()) {
                            Text(
                                text = "${card.memberId} | ${card.district}",
                                color = Color(0xFF475569),
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }

            // FOOTER BAR WITH OFFICIAL ACCREDITATION SEAL
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC))
                    .border(width = 1.dp, color = Color(0xFFE2E8F0))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "அரசு பதிவு எண்:",
                        color = UnionRed,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TNMDUJCLMDUTU-50-26-00044",
                        color = Color(0xFF1E293B),
                        fontSize = 8.8.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Image(
                    painter = painterResource(id = R.drawable.ic_auth_accreditation),
                    contentDescription = "Accreditation Seal",
                    modifier = Modifier.size(width = 30.dp, height = 36.dp)
                )
            }
        }
    }
}
