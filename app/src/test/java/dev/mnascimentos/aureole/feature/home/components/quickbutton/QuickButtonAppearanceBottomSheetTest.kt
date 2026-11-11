package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
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
        composeTestRule.onNodeWithText("BACKGROUND COLOR").performScrollTo().assertIsDisplayed()
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
        composeTestRule.onNodeWithText("STROKE COLOR").assertExists()
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

    @Test
    fun `when an icon of the inline grid is tapped, should save it with Save and not before`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onNodeWithContentDescription(QuickButtonIcon.STAR.name).performScrollTo().performClick()
        assertNull(saved)

        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(QuickButtonIcon.STAR, saved!!.icon)
    }
}
