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
import dev.mnascimentos.aureole.testing.closeOverlayWithBack
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

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
    fun `when toggling the stroke, should not write the button until the sheet closes`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()
        composeTestRule.onNodeWithText("STROKE COLOR").assertExists()
        assertNull(saved)

        composeTestRule.closeOverlayWithBack()
        assertTrue(saved!!.strokeEnabled)
    }

    @Test
    fun `when closing after editing, should save the draft once and close`() {
        var saved: QuickButtonAppearance? = null
        val dismissed = mutableListOf<Unit>()
        setAppearanceSheet(
            createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }),
            dismissed
        )

        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()
        composeTestRule.closeOverlayWithBack()

        assertTrue(saved!!.strokeEnabled)
        assertEquals(1, dismissed.size)
    }

    @Test
    fun `when an icon of the inline grid is tapped, should save it when the sheet closes and not before`() {
        var saved: QuickButtonAppearance? = null
        setAppearanceSheet(createDefaultHomeScreenActions().copy(onUpdateQuickButtonAppearance = { saved = it }))

        composeTestRule.onNodeWithContentDescription(QuickButtonIcon.STAR.name).performScrollTo().performClick()
        assertNull(saved)

        composeTestRule.closeOverlayWithBack()
        assertEquals(QuickButtonIcon.STAR, saved!!.icon)
    }
}
