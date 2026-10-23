package dev.mnascimentos.aureole.feature.home.components.quickbutton.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Stroke
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.AppearanceColorRow
import dev.mnascimentos.aureole.feature.home.components.quickbutton.AppearanceIconRow
import dev.mnascimentos.aureole.feature.home.components.quickbutton.AppearanceToggleRow
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonIconPickerDialog

@AureolePreview
@Composable
private fun AppearanceColorRowPreview() {
    AureoleLauncherTheme {
        AppearanceColorRow(
            title = "Preview Color Row",
            icon = { m -> AureoleDS.icons.ColorFill(m) },
            colorValue = Color.Red,
            onClick = {}
        )
    }
}

@AureolePreview
@Composable
private fun AppearanceToggleRowCheckedPreview() {
    AureoleLauncherTheme {
        AppearanceToggleRow(
            title = "Preview Toggle Row Checked",
            icon = { m -> AureoleDS.icons.Stroke(m) },
            isChecked = true,
            onToggle = {}
        )
    }
}

@AureolePreview
@Composable
private fun AppearanceToggleRowUncheckedPreview() {
    AureoleLauncherTheme {
        AppearanceToggleRow(
            title = "Preview Toggle Row Unchecked",
            icon = { m -> AureoleDS.icons.Stroke(m) },
            isChecked = false,
            onToggle = {}
        )
    }
}

@AureolePreview
@Composable
private fun AppearanceIconRowPreview() {
    AureoleLauncherTheme {
        AppearanceIconRow(
            title = "Preview Icon Row",
            icon = { m -> AureoleDS.icons.Smile(m) },
            selectedIcon = QuickButtonIcon.STAR,
            appInfo = null,
            onClick = {}
        )
    }
}

@AureolePreview
@Composable
private fun QuickButtonIconPickerDialogPreview() {
    AureoleLauncherTheme {
        QuickButtonIconPickerDialog(
            selectedIcon = QuickButtonIcon.STAR,
            onIconSelected = {},
            onDismiss = {}
        )
    }
}
