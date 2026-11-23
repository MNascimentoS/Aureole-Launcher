package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Onboarding notice of the clock editor (RF-D2, D-07): the sheet has no save button, so it says it closes by
 * dragging down and that every change is already saved. "Got it" ([onDismiss]) hides it for good.
 */
@Composable
internal fun EditClockSaveHint(onDismiss: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .padding(start = AureoleDS.dimens.medium, end = AureoleDS.dimens.xSmall)
            .heightIn(min = AureoleDS.dimens.minTouchTarget),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        AureoleDS.icons.ArrowDown(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(vertical = AureoleDS.dimens.small)
        ) {
            AureoleText(
                text = stringResource(R.string.home_clock_save_hint_title),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.Bold)
            )
            AureoleText(
                text = stringResource(R.string.home_clock_save_hint_body),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }
        TextButton(onClick = onDismiss) {
            AureoleText(
                text = stringResource(R.string.home_clock_save_hint_got_it),
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(fontWeight = FontWeight.Bold, maxLines = 1)
            )
        }
    }
}

@AureolePreview
@Composable
private fun EditClockSaveHintPreview() {
    AureoleLauncherTheme {
        EditClockSaveHint(
            onDismiss = {},
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        )
    }
}
