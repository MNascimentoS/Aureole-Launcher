package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionOptions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

private fun searchApp(label: String, pkg: String, color: Int) = AppInfo(
    label = label,
    packageName = pkg,
    componentName = ComponentName(pkg, "Main"),
    icon = color.toDrawable()
)

@Composable
private fun ShortcutSearchCase(query: String, isLeftHanded: Boolean) {
    AureoleLauncherTheme {
        ShortcutSelectionBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel(),
                editingShortcutType = "click",
                isLeftHandedMode = isLeftHanded,
                apps = listOf(
                    searchApp("Camera Pro", "com.pro", 0xFF1B65C0.toInt()),
                    searchApp("Camcorder", "com.camcorder", 0xFF2E7D32.toInt()),
                    searchApp("Maps", "com.maps", 0xFFC62828.toInt())
                )
            ),
            actions = createDefaultHomeScreenActions(),
            onDismissRequest = {},
            options = ShortcutSelectionOptions(initialSearchQuery = query)
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSearchRightHandedScreenshotTest() = ShortcutSearchCase("cam", isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSearchLeftHandedScreenshotTest() = ShortcutSearchCase("cam", isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun ShortcutSearchEmptyScreenshotTest() = ShortcutSearchCase("zzz", isLeftHanded = false)
