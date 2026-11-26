package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onboardingNewItemOutline
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingMotionSpec
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.newItemChipRes

/**
 * "New · Clock" (onboarding-redesenho RF-D1): the chip of the item a step just added, sitting on the top-start corner
 * of its dashed outline (`onboardingNewItemOutline`), half above it. Place it at the top-start of the item; it draws
 * nothing for a [type] no step adds. It never moves on its own, so it needs no reduced-motion variant (RF-D6).
 */
@Composable
fun OnboardingNewItemChip(type: LauncherItemType?, modifier: Modifier = Modifier) {
    val labelRes = newItemChipRes(type) ?: return
    val margin = OnboardingMotionSpec.NEW_ITEM_MARGIN
    AureoleText(
        text = stringResource(labelRes),
        color = AureoleDS.colors.surface,
        modifier = modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                val outset = margin.roundToPx()
                layout(placeable.width, placeable.height) {
                    placeable.placeRelative(-outset, -outset - placeable.height / 2)
                }
            }
            .background(AureoleDS.colors.onSurfaceHigh, RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xxSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.smallText, fontWeight = FontWeight.Bold)
    )
}

@AureolePreview
@Composable
private fun OnboardingNewItemChipPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.padding(AureoleDS.dimens.large)) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.sizeExtraLarge)
                    .onboardingNewItemOutline(isHighlighted = true)
            ) {
                OnboardingNewItemChip(type = LauncherItemType.CLOCK, modifier = Modifier.align(Alignment.TopStart))
            }
        }
    }
}
