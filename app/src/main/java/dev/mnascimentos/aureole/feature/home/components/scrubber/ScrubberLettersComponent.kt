package dev.mnascimentos.aureole.feature.home.components.scrubber

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberDimensions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberRenderParams
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterCenterY
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBulgeWeight
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberLetterAlpha
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberLetterScale
import kotlin.math.abs
import kotlin.math.roundToInt

private const val INACTIVE_LETTER_ALPHA = 0.6f
private const val PREVIEW_TOUCH_INDEX = 3

/**
 * The letters of the scrubber, bending away from the finger (bulge) while a drag runs. With the prominent
 * selection the letter under the finger stays in the strip and grows, its neighbours growing less and less.
 */
@Composable
fun BoxScope.ScrubberAlphabetItems(
    alphabet: List<Char>,
    containerHeightPx: Float,
    params: ScrubberRenderParams,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val dimens = AureoleDS.dimens
    val amplitudePx = with(density) { ScrubberDimensions.BulgeMax.toPx() }
    val sigmaPx = with(density) { ScrubberDimensions.Spread.toPx() }
    val growthSpreadPx = with(density) { ScrubberDimensions.GrowthSpread.toPx() }

    alphabet.forEachIndexed { index, char ->
        val itemCenterY = letterCenterY(index, containerHeightPx, alphabet.size)
        val isActive = params.isDragging && index == params.selectedIndex
        val charStr = remember(char) { char.toString() }

        AureoleText(
            text = charStr,
            color = if (isActive) {
                AureoleDS.colors.onSurfaceHigh
            } else {
                AureoleDS.colors.onSurfaceHigh.copy(alpha = INACTIVE_LETTER_ALPHA)
            },
            modifier = modifier
                .align(params.alignment)
                .padding(
                    start = if (isLeftHandedMode) ScrubberDimensions.LabelOffset else dimens.zero,
                    end = if (isLeftHandedMode) dimens.zero else ScrubberDimensions.LabelOffset
                )
                .graphicsLayer {
                    val currentTouchY = params.touchYPx
                    val isBending = params.isDragging && currentTouchY >= 0f
                    val distance = abs(itemCenterY - currentTouchY)
                    val magnitude = if (isBending) amplitudePx * scrubberBulgeWeight(distance, sigmaPx) else 0f
                    translationX = if (isLeftHandedMode) magnitude else -magnitude
                    translationY = itemCenterY - (size.height / 2f)
                    val scale = if (isBending && params.isProminentSelection) {
                        scrubberLetterScale(distance, growthSpreadPx, isActive)
                    } else {
                        1f
                    }
                    scaleX = scale
                    scaleY = scale
                    alpha = scrubberLetterAlpha(
                        isSelected = isActive,
                        isDragging = params.isDragging,
                        scrubberAlpha = params.scrubberAlpha,
                        isBadgeOnLetter = !params.isProminentSelection
                    )
                },
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.labelSmall,
                fontWeight = if (isActive) FontWeight.ExtraBold else FontWeight.Medium
            )
        )
    }
}

/** Small badge drawn on the selected letter (pickers); the strip hides its own copy of the letter meanwhile. */
@Composable
fun BoxScope.ScrubberSelectedBadge(
    selectedChar: Char,
    badgeYPx: Float,
    badgeXPx: Float,
    alignment: Alignment,
    modifier: Modifier = Modifier
) {
    val dimens = AureoleDS.dimens
    Surface(
        shape = CircleShape,
        color = AureoleDS.colors.onSurfaceHigh,
        contentColor = AureoleDS.colors.surface,
        shadowElevation = ScrubberDimensions.BadgeElevation,
        modifier = modifier
            .size(dimens.minTouchTarget)
            .align(alignment)
            .offset { IntOffset(badgeXPx.roundToInt(), badgeYPx.roundToInt()) }
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            AureoleText(
                text = selectedChar.toString(),
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@AureolePreview
@Composable
private fun ScrubberAlphabetItemsPreview() {
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(ScrubberDimensions.ProminentBadgeInset)
                .background(AureoleDS.colors.background)
        ) {
            val alphabet = ('A'..'J').toList()
            val heightPx = with(
                LocalDensity.current
            ) { ScrubberDimensions.ProminentBadgeSize.toPx() * alphabet.size / 2 }
            ScrubberAlphabetItems(
                alphabet = alphabet,
                containerHeightPx = heightPx,
                params = ScrubberRenderParams(
                    touchYPx = letterCenterY(PREVIEW_TOUCH_INDEX, heightPx, alphabet.size),
                    isDragging = true,
                    selectedIndex = PREVIEW_TOUCH_INDEX,
                    scrubberAlpha = 1f,
                    alignment = Alignment.TopEnd,
                    isProminentSelection = true
                ),
                isLeftHandedMode = false
            )
        }
    }
}
