package com.example.ui.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.MemberCardEntity

@Composable
fun MemberCardBackView(
    card: MemberCardEntity,
    modifier: Modifier = Modifier,
    onLogoClick: (() -> Unit)? = null,
    onNameOrAddressClick: (() -> Unit)? = null
) {
    ProfessionalMemberIdCardLayout(
        card = card,
        modifier = modifier,
        isBackSide = true,
        onLogoClick = onLogoClick,
        onNameOrAddressClick = onNameOrAddressClick
    )
}
