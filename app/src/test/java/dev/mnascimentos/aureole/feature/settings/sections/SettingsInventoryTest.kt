package dev.mnascimentos.aureole.feature.settings.sections

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.components.SettingsSheetHost
import dev.mnascimentos.aureole.feature.settings.model.AppearanceMenuDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsAdvancedDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetHostParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.screens.SettingsRootScreen
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Nothing leaves the app: every item of the inventory in `specs/ajustes-redesign/IDEIA.md` is still on screen (26
 * since "Custom colors" left, D-08: the base color stays in Set background → Solid color).
 */
@RunWith(RobolectricTestRunner::class)
class SettingsInventoryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val appearanceItems = listOf(
        "Color palette",
        "Change font",
        "Themed icons",
        "Set Background",
        "Image scale",
        "Restore background",
        "Change Border",
        "Glass effect"
    )
    private val behaviorItems = listOf(
        "Home opens all apps",
        "All apps on left side",
        "Disable alphabet scrubber",
        "Show widget indicator",
        "Header offset"
    )
    private val advancedItems = listOf(
        "App info", "What's new", "Default launcher", "Check for updates",
        "Contributors", "Privacy policy", "Terms of service", "Open source licenses",
        "Reset launcher", "Uninstall launcher"
    )
    private val rootItems = listOf("Appearance", "Behavior", "Advanced")

    @Test
    fun `the three sections and the root still show all 26 items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    AppearanceSections(
                        uiState = SettingsUiState(isCustomWallpaperSet = true),
                        actions = SettingsScreenActions(),
                        dialogActions = AppearanceMenuDialogActions({}, {}, {}, {})
                    )
                    BehaviorSections(uiState = SettingsUiState(), actions = SettingsScreenActions())
                    AdvancedSections(
                        uiState = SettingsUiState(),
                        actions = SettingsScreenActions(),
                        dialogActions = SettingsAdvancedDialogActions({}, {}, {})
                    )
                }
            }
        }
        val all = appearanceItems + behaviorItems + advancedItems
        all.forEach { composeTestRule.onNodeWithText(it).assertExists() }
        assertEquals(23, all.size)
    }

    @Test
    fun `the root keeps the three area cards`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsRootScreen(SettingsUiState(), {}, {}, {})
            }
        }
        rootItems.forEach { composeTestRule.onNodeWithText(it).assertExists() }
        assertEquals(26, appearanceItems.size + behaviorItems.size + advancedItems.size + rootItems.size)
    }

    @Test
    fun `each area sheet of the host shows its items`() {
        val areas = listOf(
            SettingsArea.APPEARANCE to appearanceItems,
            SettingsArea.BEHAVIOR to behaviorItems,
            SettingsArea.ADVANCED to advancedItems
        )
        var area by mutableStateOf(SettingsArea.APPEARANCE)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                key(area) {
                    SettingsSheetHost(
                        params = SettingsSheetHostParams(
                            uiState = SettingsUiState(isCustomWallpaperSet = true),
                            actions = SettingsScreenActions(),
                            onNavigateToLicenses = {},
                            initialSheet = SettingsSheet.Area(area)
                        )
                    ) { }
                }
            }
        }
        areas.forEach { (shown, items) ->
            area = shown
            composeTestRule.waitForIdle()
            items.forEach { composeTestRule.onNodeWithText(it).assertExists() }
        }
    }
}
