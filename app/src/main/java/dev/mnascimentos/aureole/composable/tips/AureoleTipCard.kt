package dev.mnascimentos.aureole.composable.tips

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.tips.model.TipCardContent
import dev.mnascimentos.aureole.composable.tips.model.TipCardMetrics
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * A guided tip at the top of a sheet's content (grupo-apps-pastas RF-F2): the step marks (the current one wider),
 * "Skip tips" at the end, the title, the text and, when there is one, a white pill button. TalkBack announces it as
 * a region ("Tip 1 of 3") when it shows (RF-F5).
 */
@Composable
fun AureoleTipCard(content: TipCardContent, modifier: Modifier = Modifier) {
    val region = stringResource(R.string.home_container_tip_step, content.step, content.stepCount)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(TipCardMetrics.CornerRadius))
            .background(AureoleDS.colors.surfaceVariant)
            .semantics {
                paneTitle = region
                liveRegion = LiveRegionMode.Polite
            }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            TipStepMarks(step = content.step, count = content.stepCount, modifier = Modifier.weight(1f))
            TextButton(onClick = content.onSkip) {
                AureoleText(
                    text = stringResource(R.string.home_container_tip_skip),
                    color = AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium)
                )
            }
        }
        AureoleText(
            text = content.title,
            color = AureoleDS.colors.onSurfaceHigh,
            format = AureoleTextFormatParams(style = AureoleDS.typography.titleSmall, fontWeight = FontWeight.Bold)
        )
        AureoleText(
            text = content.text,
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
        content.actionLabel?.let { label -> TipActionPill(label = label, onClick = content.onAction) }
    }
}

@Composable
private fun TipStepMarks(step: Int, count: Int, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)) {
        repeat(count) { index ->
            val isCurrent = index + 1 == step
            Box(
                modifier = Modifier
                    .size(
                        width = if (isCurrent) TipCardMetrics.CurrentStepWidth else TipCardMetrics.StepWidth,
                        height = TipCardMetrics.StepHeight
                    )
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
                    .background(if (isCurrent) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceLow)
            )
        }
    }
}

@Composable
private fun TipActionPill(label: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .padding(top = AureoleDS.dimens.xxSmall)
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.onSurfaceHigh)
            .semantics { role = Role.Button }
    ) {
        AureoleText(
            text = label,
            color = AureoleDS.colors.surface,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, fontWeight = FontWeight.Bold)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleTipCardPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)) {
            AureoleTipCard(
                content = TipCardContent(
                    step = 1,
                    stepCount = 3,
                    title = "This is your app & folder group",
                    text = "It sits on the home and brings apps, folders and quick buttons together.",
                    onSkip = {},
                    actionLabel = "Add app"
                )
            )
        }
    }
}
