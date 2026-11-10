package dev.mnascimentos.aureole.composable.appitem

import android.animation.ValueAnimator
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val SHIMMER_MS = 900
private const val SHIMMER_LOW = 0.45f
private const val SHIMMER_HIGH = 0.9f
private const val ICON_ALPHA = 0.8f
private const val BAR_ALPHA = 0.55f
private const val DEFAULT_ROW_COUNT = 8

// Label widths step through LABEL_WIDTH_STEPS sizes out of order, so the rows do not look stamped.
private const val LABEL_MIN_WIDTH = 0.4f
private const val LABEL_WIDTH_STEP = 0.05f
private const val LABEL_WIDTH_STEPS = 7

private fun labelWidth(index: Int): Float = LABEL_MIN_WIDTH + ((index * 2 + 1) % LABEL_WIDTH_STEPS) * LABEL_WIDTH_STEP

/**
 * Placeholder rows for an app list whose apps have not arrived yet. The rows breathe while the
 * system animations are on and stay still otherwise (and in previews).
 */
@Composable
fun AppListSkeletonComponent(
    modifier: Modifier = Modifier,
    rowCount: Int = DEFAULT_ROW_COUNT
) {
    val isInspection = LocalInspectionMode.current
    val isAnimated = remember(isInspection) { !isInspection && ValueAnimator.areAnimatorsEnabled() }
    val shimmer = if (isAnimated) {
        rememberInfiniteTransition(label = "appListSkeleton").animateFloat(
            initialValue = SHIMMER_LOW,
            targetValue = SHIMMER_HIGH,
            animationSpec = infiniteRepeatable(tween(SHIMMER_MS), RepeatMode.Reverse),
            label = "appListSkeletonAlpha"
        ).value
    } else {
        SHIMMER_HIGH
    }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        repeat(rowCount) { index ->
            SkeletonRow(labelWidth = labelWidth(index), alpha = shimmer)
        }
        AureoleText(
            text = stringResource(R.string.home_apps_loading),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = AureoleDS.dimens.small)
                .semantics { liveRegion = LiveRegionMode.Polite },
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )
    }
}

@Composable
private fun SkeletonRow(labelWidth: Float, alpha: Float) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconXLarge)
                .background(
                    AureoleDS.colors.surfaceVariant.copy(alpha = ICON_ALPHA),
                    RoundedCornerShape(AureoleDS.dimens.radiusSmall)
                )
        )
        Box(
            modifier = Modifier
                .fillMaxWidth(labelWidth)
                .height(AureoleDS.dimens.small)
                .background(
                    AureoleDS.colors.outline.copy(alpha = BAR_ALPHA),
                    RoundedCornerShape(AureoleDS.dimens.radiusFull)
                )
        )
    }
}

@AureolePreview
@Composable
private fun AppListSkeletonPreview() {
    AureoleLauncherTheme {
        AppListSkeletonComponent(modifier = Modifier.padding(AureoleDS.dimens.large))
    }
}
