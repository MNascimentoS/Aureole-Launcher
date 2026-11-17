package dev.mnascimentos.aureole.feature.settings.components

import androidx.activity.compose.BackHandler
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.SettingsNavGraph
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The settings root with its areas as sheets: opening, swapping in place, the full-screen licenses and back. */
@RunWith(RobolectricTestRunner::class)
class SettingsSheetHostTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /** Set when back reaches past the settings, which is where the activity would finish. */
    private var leftSettings = false

    private fun setSettings(
        uiState: SettingsUiState = SettingsUiState(),
        actions: SettingsScreenActions = SettingsScreenActions()
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                // Registered first, so it only runs when nothing in the settings takes the back.
                BackHandler { leftSettings = true }
                SettingsNavGraph(uiState = uiState, actions = actions)
            }
        }
    }

    private fun openArea(title: String) {
        composeTestRule.onNodeWithText(title).performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    @Test
    fun `tapping Appearance opens its sheet with the mini-home preview`() {
        setSettings()
        composeTestRule.onNodeWithText("09:41").assertDoesNotExist()

        openArea("Appearance")

        composeTestRule.onNodeWithText("09:41").assertExists()
        composeTestRule.onNodeWithText("Color palette").assertExists()
        composeTestRule.onNodeWithText("Glass effect").assertExists()
    }

    @Test
    fun `Behavior and Advanced open without a preview`() {
        setSettings()
        openArea("Behavior")
        composeTestRule.onNodeWithText("Header offset").assertExists()
        composeTestRule.onNodeWithText("09:41").assertDoesNotExist()

        composeTestRule.closeOverlayWithBack()
        openArea("Advanced")
        composeTestRule.onNodeWithText("Reset launcher").assertExists()
        composeTestRule.onNodeWithText("09:41").assertDoesNotExist()
    }

    @Test
    fun `Palette swaps in place of Appearance and closing brings Appearance back`() {
        setSettings()
        openArea("Appearance")

        composeTestRule.onNodeWithText("Color palette").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Pick a palette").assertExists()
        composeTestRule.onNodeWithText("Themed icons").assertDoesNotExist()

        composeTestRule.closeOverlayWithBack()
        composeTestRule.onNodeWithText("Pick a palette").assertDoesNotExist()
        composeTestRule.onNodeWithText("Themed icons").assertExists()
    }

    @Test
    fun `coming back from a swapped sheet keeps the scroll of the area`() {
        setSettings()
        openArea("Appearance")
        composeTestRule.onNodeWithText("Glass effect").performScrollTo()
        composeTestRule.waitForIdle()
        val before = glassBelowSheetTitle()

        composeTestRule.onNodeWithText("Glass effect").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Cards and glass").assertExists()
        composeTestRule.closeOverlayWithBack()

        // The sheet itself may come back at another height; the content keeps its scroll under the title.
        assertEquals(before, glassBelowSheetTitle())
    }

    /** How far the "Glass effect" row sits below the title of the Appearance sheet (the last "Appearance"). */
    private fun glassBelowSheetTitle(): Dp {
        val titleTop = composeTestRule.onAllNodesWithText("Appearance").fetchSemanticsNodes().last().boundsInRoot.top
        val density = composeTestRule.density
        val glassTop = composeTestRule.onNodeWithText("Glass effect").getUnclippedBoundsInRoot().top
        return glassTop - with(density) { titleTop.toDp() }
    }

    @Test
    fun `Custom colors opens the color picker in place of Appearance`() {
        setSettings()
        openArea("Appearance")
        composeTestRule.onNodeWithText("Custom colors").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Choose Primary Color").assertExists()
        composeTestRule.onNodeWithText("Color palette").assertDoesNotExist()
    }

    @Test
    fun `Appearance rows that act on the spot still work in the sheet`() {
        var scale = ""
        var themedIcons = false
        setSettings(
            uiState = SettingsUiState(isCustomWallpaperSet = true, wallpaperScaleType = "Crop"),
            actions = SettingsScreenActions(
                onSelectWallpaperScaleType = { scale = it },
                onToggleThemedAppIcons = { themedIcons = true }
            )
        )
        openArea("Appearance")

        composeTestRule.onNodeWithText("Image scale").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Themed icons").performScrollTo().performClick()

        assertEquals("Fit", scale)
        assertTrue(themedIcons)
    }

    @Test
    fun `Contributors opens as a sheet in place of Advanced`() {
        setSettings()
        openArea("Advanced")

        composeTestRule.onNodeWithText("Contributors").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Aislan Tavares").assertExists()
        composeTestRule.onNodeWithText("Mateus Nascimento").assertExists()
        composeTestRule.onNodeWithText("Reset launcher").assertDoesNotExist()

        composeTestRule.closeOverlayWithBack()
        composeTestRule.onNodeWithText("Aislan Tavares").assertDoesNotExist()
        composeTestRule.onNodeWithText("Reset launcher").assertExists()
    }

    @Test
    fun `What's new opens the release notes in place of Advanced`() {
        setSettings()
        openArea("Advanced")
        composeTestRule.onNodeWithText("What's new").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Got it").assertExists()
        composeTestRule.onNodeWithText("Reset launcher").assertDoesNotExist()
    }

    @Test
    fun `Open source licenses goes full screen and back returns to Advanced`() {
        setSettings()
        openArea("Advanced")

        composeTestRule.onNodeWithText("Open source licenses").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("LIBRARIES").assertExists()
        composeTestRule.onNodeWithText("Reset launcher").assertDoesNotExist()

        composeTestRule.closeOverlayWithBack()
        composeTestRule.onNodeWithText("LIBRARIES").assertDoesNotExist()
        composeTestRule.onNodeWithText("Reset launcher").assertExists()
        assertFalse(leftSettings)
    }

    @Test
    fun `back walks from the child to the area, to the root and then out`() {
        setSettings()
        openArea("Appearance")
        composeTestRule.onNodeWithText("Change font").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Pick Font").assertExists()

        composeTestRule.closeOverlayWithBack()
        composeTestRule.onNodeWithText("Pick Font").assertDoesNotExist()
        composeTestRule.onNodeWithText("Color palette").assertExists()
        assertFalse(leftSettings)

        composeTestRule.closeOverlayWithBack()
        composeTestRule.onNodeWithText("Color palette").assertDoesNotExist()
        composeTestRule.onNodeWithText("Thank you!").assertExists()
        assertFalse(leftSettings)

        composeTestRule.closeOverlayWithBack()
        assertTrue(leftSettings)
    }

    @Test
    fun `the area cards tell TalkBack they open a panel`() {
        setSettings()
        val opensPanel = SemanticsMatcher("click label is open panel") { node ->
            node.config.getOrNull(SemanticsActions.OnClick)?.label == "open panel"
        }
        listOf("Appearance", "Behavior", "Advanced").forEach { title ->
            composeTestRule.onNode(hasText(title) and hasClickAction(), useUnmergedTree = false).assert(opensPanel)
        }
    }
}
