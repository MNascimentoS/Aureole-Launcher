package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val DISMISS_ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
class QuickButtonAppearanceBottomSheetTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering appearance bottom sheet with open app shortcut, should resolve active app info`() {
        // Arrange
        val appInfo = AppInfo(
            label = "Gallery",
            packageName = "com.example.gallery",
            componentName = ComponentName("com.example.gallery", "MainActivity"),
            icon = ColorDrawable(Color.GREEN)
        )
        val model = QuickButtonModel(
            shortcuts = QuickButtonShortcuts(
                click = ShortcutAction.openApp("com.example.gallery")
            )
        )
        val uiState = MainUiState(
            activeQuickButtonModel = model,
            apps = listOf(appInfo),
            isHazeEnabled = true
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Quick button").assertIsDisplayed()
        composeTestRule.onNodeWithText("Background color").assertIsDisplayed()
    }

    @Test
    fun `when background color row is clicked, should show color picker dialog`() {
        // Arrange
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel(),
            isHazeEnabled = false
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Background color").performClick()
        composeTestRule.onNodeWithText("Choose Primary Color").assertIsDisplayed()
    }

    @Test
    fun `when icon row is clicked, should show icon picker dialog`() {
        // Arrange
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel()
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = createDefaultHomeScreenActions(),
                    onDismissRequest = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Icon").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Select Icon").assertIsDisplayed()
    }

    private fun setAppearanceSheet(actions: HomeScreenActions, dismissed: MutableList<Unit> = mutableListOf()) {
        val uiState = MainUiState(
            activeQuickButtonModel = QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = false))
        )
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceBottomSheet(
                    uiState = uiState,
                    actions = actions,
                    onDismissRequest = { dismissed += Unit }
                )
            }
        }
    }

    @Test
    fun `when toggling the stroke, should not write the button until Save is tapped`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Stroke color").assertExists()
        assertNull(saved)

        composeTestRule.onNodeWithText("Save").performClick()
        assertTrue(saved!!.strokeEnabled)
    }

    @Test
    fun `when cancelling after editing, should drop the draft and close`() {
        var saved: QuickButtonAppearance? = null
        val dismissed = mutableListOf<Unit>()
        setAppearanceSheet(
            createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }),
            dismissed
        )

        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)

        assertNull(saved)
        assertEquals(1, dismissed.size)
    }
}
