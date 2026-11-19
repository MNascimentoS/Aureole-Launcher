package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.palette.allPalettes
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetHostParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsAppearanceDialogsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /** The background picker inside the Appearance sheet, the way the settings open it. */
    private fun setBackgroundSheet(
        isCustomImageSet: Boolean = false,
        onSolid: (Int) -> Unit = {},
        onImage: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsSheetHost(
                    params = SettingsSheetHostParams(
                        uiState = SettingsUiState(
                            manualSeedColor = RecommendedColors[1],
                            isCustomWallpaperSet = isCustomImageSet
                        ),
                        actions = SettingsScreenActions(
                            onSelectBackgroundColor = onSolid,
                            onChangeWallpaperClick = onImage
                        ),
                        onNavigateToLicenses = {},
                        initialSheet = SettingsSheet.Area(SettingsArea.APPEARANCE, inner = SettingsInnerKind.BACKGROUND)
                    )
                ) {}
            }
        }
    }

    @Test
    fun `when going back without a change, should save nothing and not open the picker`() {
        var color: Int? = null
        var imageClicked = false
        setBackgroundSheet(onSolid = { color = it }, onImage = { imageClicked = true })

        composeTestRule.onNodeWithText("Set background").assertIsDisplayed()
        composeTestRule.closeOverlayWithBack()

        assertNull(color)
        assertFalse(imageClicked)
    }

    @Test
    fun `when picking a swatch and closing, should save the swatch color`() {
        var color: Int? = null
        setBackgroundSheet(onSolid = { color = it })

        // The swatch can sit outside the test window, where a touch would hit the scrim: use the semantic click.
        composeTestRule.onAllNodes(isSelectable()).onLast().performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.closeOverlayWithBack()

        assertNotNull(color)
        assertNotEquals(RecommendedColors[1], color)
    }

    @Test
    fun `when choosing the custom image type, should open the photo picker right away and save no color`() {
        var color: Int? = null
        var imageClicked = false
        setBackgroundSheet(onSolid = { color = it }, onImage = { imageClicked = true })

        composeTestRule.onNodeWithText("Custom image").performClick()
        assertTrue(imageClicked)

        composeTestRule.closeOverlayWithBack()

        assertNull(color)
    }

    @Test
    fun `when a custom image is already set, should open on the custom image type without swatches`() {
        setBackgroundSheet(isCustomImageSet = true)

        composeTestRule.onNodeWithText("COLOR").assertDoesNotExist()
    }

    @Test
    fun `when rendering PickPaletteBottomSheet, should display title and allow selecting palette`() {
        var selectedPalette: ThemePalette? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                PickPaletteBottomSheet(
                    selectedPaletteName = "Frostbite",
                    onPaletteSelected = { selectedPalette = it },
                    onDismissRequest = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Pick a palette").assertIsDisplayed()
        composeTestRule.onNodeWithText(allPalettes[1].name).performClick()
        composeTestRule.closeOverlayWithBack()
        assertNotNull(selectedPalette)
    }
}
