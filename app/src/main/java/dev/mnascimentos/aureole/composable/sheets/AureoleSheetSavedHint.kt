package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * One quiet line above the tab bar of a sheet with no save button: "✓ Your changes are saved automatically when you
 * close" (the clock editor, the group editor). [onClick], when given, makes the line a button (the clock hides it
 * for good on tap).
 */
@Composable
fun AureoleSheetSavedHint(modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        AureoleDS.icons.Check(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
        AureoleText(
            text = stringResource(R.string.overlay_sheet_saved_hint),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleSheetSavedHintPreview() {
    AureoleLauncherTheme {
        AureoleSheetSavedHint(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        )
    }
}
