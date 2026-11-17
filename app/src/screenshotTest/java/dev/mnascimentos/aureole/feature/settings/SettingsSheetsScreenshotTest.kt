package dev.mnascimentos.aureole.feature.settings

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.SettingsSheetHost
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
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

/** Palette inside the Appearance sheet (D-09): the same sheet and preview, the grid in place of the list. */
@PreviewTest
@AureolePreview
@Composable
private fun SettingsAppearancePaletteInnerScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.PALETTE))
}

/** Font inside the Appearance sheet. */
@PreviewTest
@AureolePreview
@Composable
private fun SettingsAppearanceFontInnerScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.FONT))
}

/** Themed icons on: the apps of the preview take the themed look of the home. */
@PreviewTest
@AureolePreview
@Composable
private fun SettingsAppearanceThemedIconsScreenshotTest() {
    SettingsSheetScreenshot(
        sheet = SettingsSheet.Area(SettingsArea.APPEARANCE),
        uiState = SettingsUiState(isThemedAppIconsEnabled = true)
    )
}

/** Advanced with the `large` space above every section label but the first (RF-15), down to the Care zone. */
@PreviewTest
@AureolePreview
@Composable
private fun SettingsAdvancedSheetSectionSpacingScreenshotTest() {
    SettingsSheetScreenshot(sheet = SettingsSheet.Area(SettingsArea.ADVANCED), scroll = ADVANCED_MID_SCROLL)
}

/** Scroll that puts the About and Care zone labels on screen. */
private const val ADVANCED_MID_SCROLL = 400
