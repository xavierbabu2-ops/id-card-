package com.example.ui.cards

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.data.MemberCardEntity

@Composable
fun MemberCardFrontView(
    card: MemberCardEntity,
    modifier: Modifier = Modifier,
    onLogoClick: (() -> Unit)? = null,
    onPhotoClick: (() -> Unit)? = null,
    onNameOrAddressClick: (() -> Unit)? = null
) {
    ProfessionalMemberIdCardLayout(
        card = card,
        modifier = modifier,
        isBackSide = false,
        onLogoClick = onLogoClick,
        onPhotoClick = onPhotoClick,
        onNameOrAddressClick = onNameOrAddressClick
    )
}
