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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.ui.theme.UnionAmber
import com.example.ui.theme.UnionGreen
import com.example.ui.theme.UnionNavy
import com.example.ui.theme.UnionRed

@Composable
fun AssociationInfoDialog(
    onDismiss: () -> Unit
) {
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
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header with Official Logo (Image 1)
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
                            Image(
                                painter = painterResource(id = R.drawable.ic_association_logo),
                                contentDescription = "Association Logo",
                                modifier = Modifier.size(46.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "சங்க விபரம் & அடையாளங்கள்",
                                    color = Color.White,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Text(
                                    text = "அரசு பதிவு எண் : 50-26-00044",
                                    color = Color(0xFFFFD700),
                                    fontSize = 11.sp,
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
                    // Full Association Name
                    Text(
                        text = "தமிழ்நாடு பெயிண்டர்கள் மற்றும் ஓவியர்கள் முன்னேற்ற சங்கம்",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF1E293B),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3 Official Symbols Visual Showcase Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "சங்கத்தின் அதிகாரப்பூர்வ சின்னங்கள்:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = UnionRed
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Logo
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_association_logo),
                                        contentDescription = "Logo",
                                        modifier = Modifier.size(54.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("சங்க லோகோ", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                }

                                // 2. Accreditation & Leaders
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_auth_accreditation),
                                        contentDescription = "Accreditation",
                                        modifier = Modifier.size(width = 46.dp, height = 58.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("அரசு அங்கீகாரம்", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                }

                                // 3. Flag
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_association_flag),
                                        contentDescription = "Flag",
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("சங்கக் கொடி", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF334155))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Address Card
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = UnionRed, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "தலைமை அலுவலகம்: 1/14 அம்பலக்காரன்பட்டி, உத்தங்குடி போஸ்ட் அவுட் மதுரை 625107",
                            fontSize = 11.5.sp,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Union Mottos Box
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFFFF1F2), RoundedCornerShape(8.dp))
                            .border(1.dp, Color(0xFFFECDD3), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Text(text = "🚩 உழைப்போம்! உயர்வோம்!", color = UnionRed, fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                        Text(text = "🤝 ஒன்றுபடுவோம்! வெல்வோம்!", color = Color(0xFF991B1B), fontWeight = FontWeight.ExtraBold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Welfare Benefits Title
                    Text(
                        text = "உறுப்பினர்களுக்கான முக்கிய நலத்திட்டங்கள்:",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF0F172A)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    BenefitItem(
                        icon = Icons.Default.Shield,
                        iconBg = Color(0xFFFFE4E6),
                        iconTint = UnionRed,
                        title = "அரசு நலவாரிய பதிவு உதவி",
                        desc = "தமிழ்நாடு கட்டுமான & அமைப்புசாரா உடலுழைப்பு தொழிலாளர்கள் நலவாரிய உறுப்பினர் அட்டை பெறுதல்."
                    )

                    BenefitItem(
                        icon = Icons.Default.HealthAndSafety,
                        iconBg = Color(0xFFECFDF5),
                        iconTint = UnionGreen,
                        title = "விபத்து & மருத்துவ காப்பீடு",
                        desc = "பணிபுரியும் போது ஏற்படும் விபத்துகளுக்கு உடனடி சங்க உதவி மற்றும் அரசு காப்பீட்டு பரிந்துரை."
                    )

                    BenefitItem(
                        icon = Icons.Default.School,
                        iconBg = Color(0xFFEFF6FF),
                        iconTint = UnionNavy,
                        title = "கல்வி & திருமண உதவித்தொகை",
                        desc = "உறுப்பினர்களின் பிள்ளைகளுக்கான பள்ளி, கல்லூரி கல்வி ஊக்கத்தொகை மற்றும் திருமண நிதி உதவி."
                    )

                    BenefitItem(
                        icon = Icons.Default.MilitaryTech,
                        iconBg = Color(0xFFFEF3C7),
                        iconTint = UnionAmber,
                        title = "அங்கீகரிக்கப்பட்ட ஒப்பந்ததாரர் அட்டை",
                        desc = "அரசு மற்றும் தனியார் பெயிண்டிங் ஒப்பந்தப் பணிகளுக்கான சங்கத்தின் அதிகாரப்பூர்வ சான்றளிப்பு."
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Contact
                    Text(
                        text = "📞 மாநில நிர்வாக அலுவலகம்: 0452-2580000 / 94433 12345\n✉️ மின்னஞ்சல்: tnpaintersassociation@gmail.com",
                        fontSize = 11.5.sp,
                        color = Color(0xFF475569),
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = UnionRed),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("சரி (OK)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun BenefitItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    title: String,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(17.dp))
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
            Text(text = desc, fontSize = 10.5.sp, color = Color(0xFF64748B), lineHeight = 13.sp)
        }
    }
}
