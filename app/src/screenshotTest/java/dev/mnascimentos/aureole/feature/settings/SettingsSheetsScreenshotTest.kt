package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.SettingsSheetHost
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen

/** The settings root with [sheet] open on top, as the user sees it. */
@Composable
internal fun SettingsSheetScreenshot(
    sheet: SettingsSheet,
    uiState: SettingsUiState = SettingsUiState(),
    scroll: Int = 0
) {
    AureoleLauncherTheme {
        SettingsSheetHost(
            uiState = uiState,
            actions = SettingsScreenActions(),
            onNavigateToLicenses = {},
            initialSheet = sheet,
            initialScroll = scroll
        ) { onOpenArea ->
            SettingsRootScreen(
                uiState = uiState,
                onNavigateToBehavior = { onOpenArea(SettingsArea.BEHAVIOR) },
                onNavigateToAppearance = { onOpenArea(SettingsArea.APPEARANCE) },
                onNavigateToAdvanced = { onOpenArea(SettingsArea.ADVANCED) }
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsAppearanceSheetScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.APPEARANCE))
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsBehaviorSheetScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.BEHAVIOR))
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsAdvancedSheetScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.ADVANCED))
}

/** Scrolled to the end: the Care zone, last, with Reset and Uninstall. */
@PreviewTest
@AureolePreview
@Composable
private fun SettingsAdvancedSheetCareZoneScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.ADVANCED), scroll = Int.MAX_VALUE)
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsAdvancedSheetLeftHandedScreenshotTest() {
    SettingsSheetScreenshot(
        sheet = SettingsSheet.Area(SettingsArea.ADVANCED),
        uiState = SettingsUiState(isLeftHandedMode = true)
    )
}

@PreviewTest
@AureolePreview
@Composable
private fun SettingsContributorsSheetScreenshotTest() {
    SettingsSheetScreenshot(
        sheet = SettingsSheet.Child(SettingsChildKind.CONTRIBUTORS, returnTo = SettingsArea.ADVANCED)
    )
}
