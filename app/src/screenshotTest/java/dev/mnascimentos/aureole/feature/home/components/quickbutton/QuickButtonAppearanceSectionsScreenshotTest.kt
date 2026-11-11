package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonIconPickerDialogScreenshotTest() {
    AureoleLauncherTheme {
        QuickButtonIconPickerDialog(
            selectedIcon = QuickButtonIcon.STAR,
            onIconSelected = {},
            onDismiss = {},
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun QuickButtonAppearanceSheetContentScreenshotTest() {
    AureoleLauncherTheme {
        QuickButtonAppearanceSheetContent(
            activeModel = QuickButtonModel(),
            activeAppInfo = null,
            onUpdateAppearance = {},
            onPickColor = {},
            onOpenIconPicker = {},
        )
    }
}
