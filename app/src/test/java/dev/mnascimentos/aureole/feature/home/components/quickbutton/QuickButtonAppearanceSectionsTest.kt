package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorPickerTarget
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class QuickButtonAppearanceSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering sheet content with stroke enabled, should display background, stroke, icon rows`() {
        // Arrange
        var pickedTarget: QuickButtonColorPickerTarget? = null
        var iconPickerOpened = false
        val model = QuickButtonModel(
            appearance = QuickButtonAppearance(strokeEnabled = true)
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceSheetContent(
                    activeModel = model,
                    activeAppInfo = null,
                    onUpdateAppearance = {},
                    onPickColor = { target -> pickedTarget = target },
                    onOpenIconPicker = { iconPickerOpened = true }
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Background color").assertIsDisplayed().performClick()
        assertEquals(QuickButtonColorPickerTarget.BACKGROUND, pickedTarget)

        composeTestRule.onNodeWithText("Stroke color").assertIsDisplayed().performClick()
        assertEquals(QuickButtonColorPickerTarget.STROKE, pickedTarget)

        composeTestRule.onNodeWithText("Icon").assertIsDisplayed().performClick()
        assertTrue("Expected onOpenIconPicker to be called", iconPickerOpened)

        composeTestRule.onNodeWithText("Icon color").assertIsDisplayed().performClick()
        assertEquals(QuickButtonColorPickerTarget.ICON, pickedTarget)
    }

    @Test
    fun `when stroke is disabled, should hide stroke color row and toggle stroke on click`() {
        // Arrange
        var updatedAppearance: QuickButtonAppearance? = null
        val model = QuickButtonModel(
            appearance = QuickButtonAppearance(strokeEnabled = false)
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceSheetContent(
                    activeModel = model,
                    activeAppInfo = null,
                    onUpdateAppearance = { updatedAppearance = it },
                    onPickColor = {},
                    onOpenIconPicker = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Stroke color").assertDoesNotExist()
        composeTestRule.onNodeWithText("Stroke").assertIsDisplayed().performClick()

        assertNotNull(updatedAppearance)
        assertTrue(updatedAppearance!!.strokeEnabled)
    }

    @Test
    fun `when rendering icon row with app info, should render app info icon without failure`() {
        // Arrange
        val appInfo = AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "CameraActivity"),
            icon = ColorDrawable(Color.BLUE)
        )

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceIconRow(
                    title = "App Icon",
                    icon = { _ -> AureoleText("I") },
                    selectedIcon = QuickButtonIcon.STAR,
                    appInfo = appInfo,
                    onClick = {}
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("App Icon").assertIsDisplayed()
    }

    @Test
    fun `when icon picker dialog is displayed, should allow selecting an icon and dismissing`() {
        // Arrange
        var dismissed = false

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonIconPickerDialog(
                    selectedIcon = QuickButtonIcon.STAR,
                    onIconSelected = {},
                    onDismiss = { dismissed = true }
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Select Icon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(OVERLAY_CLOSE_MS)
        assertTrue("Expected onDismiss to be called on cancel click", dismissed)
    }

    @Test
    fun `when icon picker is confirmed, should report the selected icon once and close`() {
        // Arrange
        val selected = mutableListOf<QuickButtonIcon>()
        var dismissed = false

        // Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonIconPickerDialog(
                    selectedIcon = QuickButtonIcon.STAR,
                    onIconSelected = { selected += it },
                    onDismiss = { dismissed = true }
                )
            }
        }
        composeTestRule.onNodeWithText("Confirm").performClick()
        composeTestRule.mainClock.advanceTimeBy(OVERLAY_CLOSE_MS)

        // Assert
        assertEquals(listOf(QuickButtonIcon.STAR), selected)
        assertTrue(dismissed)
    }

    private companion object {
        const val OVERLAY_CLOSE_MS = 600L
    }
}
