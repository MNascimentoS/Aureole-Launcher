package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.undo.UndoController
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.wallpaperEditorCloseOf
import dev.mnascimentos.aureole.feature.settings.screens.SettingsScreen
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * The photo editor inside the settings: picking a photo opens it, "Edit photo" reopens it, closing saves with an
 * Undo only when something changed. The view model is replaced by a small fake that runs the same pure decision
 * (`wallpaperEditorCloseOf`) and a real [UndoController] (agents/aureole_testing.md §6).
 */
@RunWith(RobolectricTestRunner::class)
class WallpaperEditorFlowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var uiState by mutableStateOf(SettingsUiState())
    private val undo = UndoController()
    private var closed: Pair<WallpaperAdjustments, WallpaperAdjustments>? = null
    private var editorOpenedReported = false

    private val actions = SettingsScreenActions(
        onChangeWallpaperClick = {
            // What the view model does once the gallery hands a photo back.
            uiState = uiState.copy(
                isCustomWallpaperSet = true,
                customWallpaperPath = "",
                isWallpaperEditorRequested = true
            )
        },
        onWallpaperEditorOpened = {
            editorOpenedReported = true
            uiState = uiState.copy(isWallpaperEditorRequested = false)
        },
        onWallpaperEditorClosed = { before, after -> finishEditor(before, after) },
        onUndo = {
            undo.undo()
            uiState = uiState.copy(undoSnackbar = undo.state.value)
        }
    )

    private fun finishEditor(before: WallpaperAdjustments, after: WallpaperAdjustments) {
        closed = before to after
        val close = wallpaperEditorCloseOf(before, after, photoSwapped = false)
        if (close.save) uiState = uiState.copy(wallpaperAdjustments = after)
        close.message?.let { message ->
            undo.show(TextResource.fromResId(message)) { uiState = uiState.copy(wallpaperAdjustments = before) }
            uiState = uiState.copy(undoSnackbar = undo.state.value)
        }
    }

    private fun setSettings(initial: SettingsUiState) {
        uiState = initial
        composeTestRule.setContent {
            AureoleLauncherTheme { SettingsScreen(uiState = uiState, actions = actions) }
        }
    }

    private fun openBackgroundSheet() {
        composeTestRule.onNodeWithText("Appearance").performScrollTo().performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Set background").performScrollTo().performClick()
        composeTestRule.waitForIdle()
    }

    private fun openEditorFromCard() {
        composeTestRule.onNodeWithContentDescription("Edit photo").performClick()
        composeTestRule.waitForIdle()
    }

    private fun setDim(fraction: Float) {
        composeTestRule.onNodeWithText("Dim").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Dim").performSemanticsAction(SemanticsActions.SetProgress) {
            it(fraction)
        }
        composeTestRule.waitForIdle()
    }

    private val withPhoto = SettingsUiState(isCustomWallpaperSet = true, customWallpaperPath = "")

    @Test
    fun `picking a photo opens the editor on it`() {
        setSettings(SettingsUiState())
        openBackgroundSheet()

        composeTestRule.onNodeWithText("Custom image").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Edit photo").assertExists()
        composeTestRule.onNodeWithText("Frame").assertExists()
        composeTestRule.onNodeWithText("Rotate 90°").assertExists()
        assertTrue(editorOpenedReported)
        assertFalse(uiState.isWallpaperEditorRequested)
    }

    @Test
    fun `closing with a change saves and offers Undo`() {
        setSettings(withPhoto)
        openBackgroundSheet()
        openEditorFromCard()

        setDim(0.5f)
        composeTestRule.closeOverlayWithBack()

        val (before, after) = requireNotNull(closed)
        assertEquals(WallpaperAdjustments.DEFAULT, before)
        assertEquals(0.5f, after.dim, 0.001f)
        assertEquals(0.5f, uiState.wallpaperAdjustments.dim, 0.001f)
        composeTestRule.onNodeWithText("Background photo adjusted").assertExists()
        composeTestRule.onNodeWithText("Undo").assertExists()
        composeTestRule.onNodeWithText("Set background").assertExists()
    }

    @Test
    fun `closing without a change shows no Undo`() {
        setSettings(withPhoto)
        openBackgroundSheet()
        openEditorFromCard()

        composeTestRule.closeOverlayWithBack()

        val (before, after) = requireNotNull(closed)
        assertEquals(before, after)
        assertNull(uiState.undoSnackbar)
        composeTestRule.onNodeWithText("Background photo adjusted").assertDoesNotExist()
        composeTestRule.onNodeWithText("Set background").assertExists()
    }

    @Test
    fun `Undo brings the previous adjustments back`() {
        setSettings(withPhoto)
        openBackgroundSheet()
        openEditorFromCard()
        setDim(0.6f)
        composeTestRule.closeOverlayWithBack()
        assertNotNull(uiState.undoSnackbar)

        composeTestRule.onNodeWithText("Undo").performClick()
        composeTestRule.waitForIdle()

        assertEquals(WallpaperAdjustments.DEFAULT, uiState.wallpaperAdjustments)
        openEditorFromCard()
        composeTestRule.onNodeWithText("Dim").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithContentDescription("Dim")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.StateDescription, "30%"))
    }
}
