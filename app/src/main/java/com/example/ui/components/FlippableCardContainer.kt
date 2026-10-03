package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.MemberCardEntity
import com.example.ui.cards.ContractorCardView
import com.example.ui.cards.ExecutiveCardView
import com.example.ui.cards.ProfessionalMemberIdCardLayout

enum class CardFace {
    Front,
    Back
}

@Composable
fun FlippableCardContainer(
    card: MemberCardEntity,
    cardFace: CardFace,
    onFlip: () -> Unit,
    modifier: Modifier = Modifier,
    onLogoClick: (() -> Unit)? = null,
    onFlagClick: (() -> Unit)? = null,
    onPhotoClick: (() -> Unit)? = null,
    onNameOrAddressClick: (() -> Unit)? = null,
    onSealClick: (() -> Unit)? = null
) {
    val rotation by animateFloatAsState(
        targetValue = if (cardFace == CardFace.Back) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "card_flip_anim"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .testTag("flippable_card_container")
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12f * density
            }
    ) {
        when (card.cardType) {
            "EXECUTIVE" -> {
                ExecutiveCardView(
                    card = card,
                    onLogoClick = onLogoClick,
                    onFlagClick = onFlagClick,
                    onPhotoClick = onPhotoClick,
                    onNameOrAddressClick = onNameOrAddressClick
                )
            }
            "CONTRACTOR" -> {
                ContractorCardView(card = card)
            }
            else -> {
                // Member Card: Supports Professional Layout with Front & Back 3D Flip & Touch Triggers
                if (rotation <= 90f) {
                    ProfessionalMemberIdCardLayout(
                        card = card,
                        isBackSide = false,
                        onLogoClick = onLogoClick,
                        onPhotoClick = onPhotoClick,
                        onNameOrAddressClick = onNameOrAddressClick,
                        onSealClick = onSealClick
                    )
                } else {
                    Box(
                        modifier = Modifier.graphicsLayer {
                            rotationY = 180f // Counter-rotate so back text is not mirrored
                        }
                    ) {
                        ProfessionalMemberIdCardLayout(
                            card = card,
                            isBackSide = true,
                            onLogoClick = onLogoClick,
                            onPhotoClick = onPhotoClick,
                            onNameOrAddressClick = onNameOrAddressClick,
                            onSealClick = onSealClick
                        )
                    }
                }
            }
        }
    }
}
