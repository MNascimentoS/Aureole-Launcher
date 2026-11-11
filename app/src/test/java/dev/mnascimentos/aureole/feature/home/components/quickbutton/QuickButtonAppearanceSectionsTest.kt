package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
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

    private fun setContent(model: QuickButtonModel, onUpdate: (QuickButtonAppearance) -> Unit = {}) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                QuickButtonAppearanceSheetContent(activeModel = model, onUpdateAppearance = onUpdate)
            }
        }
    }

    @Test
    fun `when stroke is enabled, should display the inline sections of icon, icon color, background and stroke`() {
        setContent(QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = true)))

        composeTestRule.onNodeWithText("ICON").assertIsDisplayed()
        composeTestRule.onNodeWithText("ICON COLOR").assertIsDisplayed()
        composeTestRule.onNodeWithText("BACKGROUND COLOR").assertIsDisplayed()
        composeTestRule.onNodeWithText("STROKE COLOR").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `when stroke is disabled, should hide the stroke color and toggle stroke on click`() {
        var updated: QuickButtonAppearance? = null
        setContent(QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = false))) { updated = it }

        composeTestRule.onNodeWithText("STROKE COLOR").assertDoesNotExist()
        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()

        assertNotNull(updated)
        assertTrue(updated!!.strokeEnabled)
    }

    @Test
    fun `when an icon is tapped, should report the appearance with that icon and keep the rest`() {
        var updated: QuickButtonAppearance? = null
        val appearance = QuickButtonAppearance(strokeEnabled = true, iconColor = 0xFF123456.toInt())
        setContent(QuickButtonModel(appearance = appearance)) { updated = it }

        composeTestRule.onNodeWithContentDescription(QuickButtonIcon.STAR.name).performScrollTo().performClick()

        assertEquals(appearance.copy(icon = QuickButtonIcon.STAR), updated)
    }
}
