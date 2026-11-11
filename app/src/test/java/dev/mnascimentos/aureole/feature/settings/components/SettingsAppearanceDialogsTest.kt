package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.core.designsystem.palette.ThemePalette
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class SettingsAppearanceDialogsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun setBackgroundSheet(
        isCustomImageSet: Boolean = false,
        onSolid: (Int) -> Unit = {},
        onImage: () -> Unit = {},
        onDismiss: () -> Unit = {}
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                SetBackgroundBottomSheet(
                    SetBackgroundParams(
                        currentColor = RecommendedColors[1],
                        isCustomImageSet = isCustomImageSet,
                        onSolidColorSelected = onSolid,
                        onCustomImageClick = onImage,
                        onDismissRequest = onDismiss
                    )
                )
            }
        }
    }

    @Test
    fun `when applying the solid color type, should save the current color and not open the picker`() {
        var color: Int? = null
        var imageClicked = false
        setBackgroundSheet(onSolid = { color = it }, onImage = { imageClicked = true })

        composeTestRule.onNodeWithText("Set background").assertIsDisplayed()
        composeTestRule.onNodeWithText("Apply").performClick()

        assertEquals(RecommendedColors[1], color)
        assertFalse(imageClicked)
    }

    @Test
    fun `when picking a swatch and applying, should save the swatch color`() {
        var color: Int? = null
        setBackgroundSheet(onSolid = { color = it })

        // The swatch can sit outside the test window, where a touch would hit the scrim: use the semantic click.
        composeTestRule.onAllNodes(isSelectable())[4].performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.onNodeWithText("Apply").performClick()

        assertEquals(RecommendedColors[2], color)
    }

    @Test
    fun `when choosing the custom image type and applying, should open the photo picker`() {
        var color: Int? = null
        var imageClicked = false
        setBackgroundSheet(onSolid = { color = it }, onImage = { imageClicked = true })

        composeTestRule.onNodeWithText("Custom image").performClick()
        composeTestRule.onNodeWithText("Apply").performClick()

        assertTrue(imageClicked)
        assertNull(color)
    }

    @Test
    fun `when a custom image is already set, should open on the custom image type without swatches`() {
        setBackgroundSheet(isCustomImageSet = true)

        composeTestRule.onNodeWithText("COLOR").assertDoesNotExist()
    }

    @Test
    fun `when cancelling, should close without saving anything`() {
        var color: Int? = null
        var dismissed = false
        setBackgroundSheet(onSolid = { color = it }, onDismiss = { dismissed = true })

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertTrue(dismissed)
        assertNull(color)
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
        composeTestRule.onNodeWithText("Frostbite").performClick()
        composeTestRule.onNodeWithText("Apply").performClick()
        assertNotNull(selectedPalette)
    }
}
