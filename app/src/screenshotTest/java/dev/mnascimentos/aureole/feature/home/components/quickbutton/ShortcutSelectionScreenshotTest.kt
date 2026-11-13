package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutCategory
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSearchCandidate
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

private fun sampleApp(label: String, pkg: String, color: Int) = AppInfo(
    label = label,
    packageName = pkg,
    componentName = ComponentName(pkg, "Main"),
    icon = color.toDrawable()
)

@Composable
private fun ShortcutSheetCase(isLeftHanded: Boolean, click: ShortcutAction) {
    AureoleLauncherTheme {
        ShortcutSelectionBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel(shortcuts = QuickButtonShortcuts(click = click)),
                editingShortcutType = "click",
                isLeftHandedMode = isLeftHanded,
                apps = listOf(
                    sampleApp("Camera Pro", "com.pro", 0xFF1B65C0.toInt()),
                    sampleApp("Maps", "com.maps", 0xFFC62828.toInt())
                )
            ),
            actions = createDefaultHomeScreenActions(),
            onDismissRequest = {}
        )
    }
}

@Composable
private fun PartsCase(isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalLeftHanded provides isLeftHanded) {
            Column {
                ShortcutCategoryChips(selected = ShortcutCategory.SYSTEM, onSelect = {})
                ShortcutActionRow(ShortcutSearchCandidate(ShortcutAction.AllApps, "All apps"), isSelected = true, onClick = {})
                ShortcutActionRow(ShortcutSearchCandidate(ShortcutAction.Settings, "Settings"), isSelected = false, onClick = {})
                ShortcutSelectionPreview(model = QuickButtonModel(), pillText = "On tap: All apps")
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSheetRightHandedScreenshotTest() = ShortcutSheetCase(false, ShortcutAction.AllApps)

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSheetLeftHandedScreenshotTest() = ShortcutSheetCase(true, ShortcutAction.AllApps)

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSheetSystemRightHandedScreenshotTest() =
    ShortcutSheetCase(false, ShortcutAction.systemIntent("Camera"))

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutPartsRightHandedScreenshotTest() = PartsCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutPartsLeftHandedScreenshotTest() = PartsCase(isLeftHanded = true)
