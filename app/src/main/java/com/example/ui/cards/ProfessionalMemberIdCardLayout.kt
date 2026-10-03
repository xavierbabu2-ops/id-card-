package com.example.ui.cards

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.MemberCardEntity
import com.example.ui.components.CardPhotoFrame

val OFFICIAL_CARD_RED = Color(0xFFDC141E)
val OFFICIAL_LABEL_RED = Color(0xFFC00000)

/**
 * 1:1 Pixel-Perfect Official TNPA² Member Identity Card Layout.
 * Complete visibility of all member information (Name, Father Name, Age, Blood Group, Address).
 * Authentic Association Logo & Government Accreditation Emblem without synthetic cartoon avatars.
 */
@Composable
fun ProfessionalMemberIdCardLayout(
    card: MemberCardEntity,
    modifier: Modifier = Modifier,
    isBackSide: Boolean = false,
    onLogoClick: (() -> Unit)? = null,
    onPhotoClick: (() -> Unit)? = null,
    onNameOrAddressClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1.586f) // Exact ISO CR80 PVC card aspect ratio (85.6mm x 53.98mm)
            .shadow(10.dp, RoundedCornerShape(8.dp))
            .testTag("professional_member_id_card"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // =========================================================================
            // 1. TOP RED BANNER (Exact match with official uploaded template)
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.26f)
                    .background(OFFICIAL_CARD_RED)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Official TNPA² Round Logo
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(1.dp)
                            .then(if (onLogoClick != null) Modifier.clickable { onLogoClick() } else Modifier)
                    ) {
                        if (card.customLogoUri != null) {
                            AsyncImage(
                                model = card.customLogoUri,
                                contentDescription = "TNPA Logo",
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

                    // Center Official Title, Govt Registration & Headquarters Address
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 2.dp)
                    ) {
                        Text(
                            text = "தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள்",
                            color = Color.White,
                            fontSize = 10.8.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 12.sp,
                            maxLines = 1
                        )
                        Text(
                            text = "முன்னேற்ற சங்கம்",
                            color = Color.White,
                            fontSize = 11.8.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center,
                            lineHeight = 13.sp,
                            maxLines = 1
                        )
                        Spacer(modifier = Modifier.height(0.5.dp))
                        Text(
                            text = "அரசு பதிவு எண்  TNMDUJCLMDUTU-50-26-00044",
                            color = Color.White,
                            fontSize = 7.2.sp,
                            fontWeight = FontWeight.ExtraBold,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                        Text(
                            text = "1/14 அம்பலக்காரன் பட்டி உத்தங்குடி மதுரை 625107",
                            color = Color.White,
                            fontSize = 7.2.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }

                    // Right Official TNPA² Round Logo
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(1.dp)
                            .then(if (onLogoClick != null) Modifier.clickable { onLogoClick() } else Modifier)
                    ) {
                        if (card.customLogoUri != null) {
                            AsyncImage(
                                model = card.customLogoUri,
                                contentDescription = "TNPA Logo",
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

            // =========================================================================
            // 2. MAIN BODY SECTION (FRONT OR BACK)
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(Color.White)
            ) {
                // Center Background Watermark (TNPA² Logo)
                Image(
                    painter = painterResource(id = R.drawable.ic_association_logo),
                    contentDescription = null,
                    modifier = Modifier
                        .size(125.dp)
                        .align(Alignment.Center)
                        .alpha(0.09f)
                )

                if (!isBackSide) {
                    // -------------------------------------------------------------
                    // FRONT SIDE BODY: 3 Red Labels on Left, Photo Box on Right, Signatures below
                    // -------------------------------------------------------------
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(start = 10.dp, end = 10.dp, top = 6.dp, bottom = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left 3 Details in Bold Red
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 6.dp)
                                    .then(
                                        if (onNameOrAddressClick != null) Modifier.clickable { onNameOrAddressClick() } else Modifier
                                    ),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                FrontDetailRow(
                                    label = "உறுப்பினர் எண்",
                                    value = card.memberId.ifBlank { "TN-MDU-1024" }
                                )
                                FrontDetailRow(
                                    label = "உறுப்பினர் பெயர்",
                                    value = card.name.ifBlank { "மு. கார்த்திகேயன்" }
                                )
                                FrontDetailRow(
                                    label = "உறுப்பினர் தொழில்",
                                    value = card.jobTitle.ifBlank { "வண்ணப் பூச்சாளர்" }
                                )
                            }

                            // Right Rectangular Passport Photo Frame
                            Box(
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .then(if (onPhotoClick != null) Modifier.clickable { onPhotoClick() } else Modifier)
                            ) {
                                CardPhotoFrame(
                                    photoUri = card.photoUri,
                                    avatarPreset = 0,
                                    modifier = Modifier
                                        .width(70.dp)
                                        .height(84.dp),
                                    borderColor = Color.Black,
                                    borderWidth = 1.4.dp,
                                    shape = RoundedCornerShape(1.dp)
                                )
                            }
                        }

                        // 3 Executive Signatures Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 2.dp, bottom = 1.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SignatureBlock(
                                title = "மாநிலத் தலைவர்",
                                signatureRes = R.drawable.ic_sig_president
                            )
                            SignatureBlock(
                                title = "மாநில பொதுச்செயலாளர்",
                                signatureRes = R.drawable.ic_sig_secretary
                            )
                            SignatureBlock(
                                title = "மாநில பொருளாளர்",
                                signatureRes = R.drawable.ic_sig_treasurer
                            )
                        }
                    }
                } else {
                    // -------------------------------------------------------------
                    // BACK SIDE BODY: 4 Red Labels on Left, Govt Seal on Right
                    // -------------------------------------------------------------
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Left 4 Details (Full visibility without ellipses truncation)
                        Column(
                            modifier = Modifier
                                .weight(1.35f)
                                .padding(end = 4.dp)
                                .then(
                                    if (onNameOrAddressClick != null) Modifier.clickable { onNameOrAddressClick() } else Modifier
                                ),
                            verticalArrangement = Arrangement.spacedBy(4.5.dp)
                        ) {
                            BackDetailRow(
                                label = "தந்தை பெயர்",
                                value = card.fatherName.ifBlank { "முத்துசாமி" }
                            )
                            BackDetailRow(
                                label = "வயது",
                                value = card.age.ifBlank { "34" }
                            )
                            BackDetailRow(
                                label = "ரத்த வகை",
                                value = card.bloodGroup.ifBlank { "O +ve" }
                            )
                            BackDetailRow(
                                label = "இருப்பிடம்",
                                value = card.address.ifBlank { "1/14 அம்பலக்காரன் பட்டி, மதுரை" },
                                isAddress = true
                            )
                        }

                        // Right Govt Accreditation Seal Section
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .weight(0.75f)
                                .padding(start = 2.dp)
                        ) {
                            Text(
                                text = "தமிழ்நாடு அரசு அனுமதி\nபெற்ற சங்கம்",
                                color = OFFICIAL_LABEL_RED,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 9.5.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_auth_accreditation),
                                contentDescription = "Tamil Nadu Govt Recognition Seal",
                                modifier = Modifier.size(width = 62.dp, height = 66.dp)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ஒன்றுபடுவோம்!\nஉரிமையை மீட்போம்.",
                                color = OFFICIAL_LABEL_RED,
                                fontSize = 7.5.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                lineHeight = 9.sp
                            )
                        }
                    }
                }
            }

            // =========================================================================
            // 3. BOTTOM RED BANNER (Exact match with uploaded template)
            // =========================================================================
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.12f)
                    .background(OFFICIAL_CARD_RED)
                    .padding(horizontal = 12.dp, vertical = 1.dp)
            ) {
                if (!isBackSide) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "உழைப்போம்.......",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "உயர்வோம் ......",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FrontDetailRow(
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = OFFICIAL_LABEL_RED,
            fontWeight = FontWeight.Black,
            fontSize = 11.2.sp,
            modifier = Modifier.width(110.dp)
        )
        Text(
            text = ": ",
            color = OFFICIAL_LABEL_RED,
            fontWeight = FontWeight.Black,
            fontSize = 11.2.sp
        )
        Text(
            text = value,
            color = Color(0xFF111827),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 11.2.sp,
            lineHeight = 13.sp,
            maxLines = 2,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun BackDetailRow(
    label: String,
    value: String,
    isAddress: Boolean = false
) {
    Row(
        verticalAlignment = if (isAddress) Alignment.Top else Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            color = OFFICIAL_LABEL_RED,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp,
            modifier = Modifier.width(82.dp)
        )
        Text(
            text = ": ",
            color = OFFICIAL_LABEL_RED,
            fontWeight = FontWeight.Black,
            fontSize = 10.5.sp
        )
        Text(
            text = value,
            color = Color(0xFF111827),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 10.5.sp,
            lineHeight = 12.5.sp,
            maxLines = if (isAddress) 2 else 1,
            overflow = TextOverflow.Clip
        )
    }
}

@Composable
private fun SignatureBlock(
    title: String,
    signatureRes: Int
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(86.dp)
    ) {
        Text(
            text = title,
            fontSize = 7.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF7F1D1D),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Image(
            painter = painterResource(id = signatureRes),
            contentDescription = title,
            modifier = Modifier
                .height(18.dp)
                .width(68.dp)
        )
    }
}
