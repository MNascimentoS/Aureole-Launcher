package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonAppearanceMetrics

/**
 * Preview above the sheet: the quick button and a pill saying what the edited gesture does ("On tap: All apps").
 * [pillText] is already formatted by the caller.
 */
@Composable
internal fun ShortcutSelectionPreview(
    model: QuickButtonModel,
    pillText: String,
    modifier: Modifier = Modifier
) {
    AureolePreviewFrame(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            Box(modifier = Modifier.size(QuickButtonAppearanceMetrics.PreviewItemSize)) {
                QuickButtonGridItem(model = model, onAction = {}, onLongPress = {})
            }
            AureoleText(
                text = pillText,
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(AureoleDS.colors.surfaceVariant)
                    .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall),
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, maxLines = 1)
            )
        }
    }
}

@AureolePreview
@Composable
private fun ShortcutSelectionPreviewPreview() {
    AureoleLauncherTheme {
        Box {
            ShortcutSelectionPreview(
                model = QuickButtonModel(),
                pillText = stringResource(R.string.home_quickbutton_gesture_click, "All apps")
            )
        }
    }
}
