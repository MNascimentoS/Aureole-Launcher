package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.isSelectable
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.icons.AureoleIconCatalog
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
                Column {
                    QuickButtonAppearanceSheetContent(activeModel = model, onUpdateAppearance = onUpdate)
                }
            }
        }
    }

    /** Selectable nodes of the Colors tab: 2 tabs, 2 color targets, then the theme roles in [ThemeColorRole] order. */
    private fun tapThemeRole(role: ThemeColorRole) {
        composeTestRule.onAllNodes(isSelectable())[4 + role.ordinal].performClick()
    }

    private fun openColors() = composeTestRule.onNodeWithText("Colors").performClick()

    @Test
    fun `when opening, should show the Icon tab with the catalog and none of the color controls`() {
        setContent(QuickButtonModel())

        composeTestRule.onNodeWithText("Icon").assertIsDisplayed()
        composeTestRule.onNodeWithText("Colors").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stroke").assertDoesNotExist()
        composeTestRule.onNodeWithText("Show name on the home").assertDoesNotExist()
    }

    @Test
    fun `when the Colors tab is chosen, should show the color target, the picker, stroke and show name`() {
        setContent(QuickButtonModel())

        openColors()

        composeTestRule.onNodeWithText("Icon color").assertIsDisplayed()
        composeTestRule.onNodeWithText("Background color").assertIsDisplayed()
        composeTestRule.onNodeWithText("FROM YOUR THEME").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stroke").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithText("Show name on the home").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `when an icon of the catalog is tapped, should report the appearance with its key and keep the rest`() {
        var updated: QuickButtonAppearance? = null
        val appearance = QuickButtonAppearance(iconColor = ThemeColorRef.Fixed(0xFF123456.toInt()))
        setContent(QuickButtonModel(appearance = appearance)) { updated = it }

        // The chips of the categories come first, then the tiles; the first tile is the marked default icon.
        val firstOtherTile = AureoleIconCatalog.categories.size + 1
        composeTestRule.onAllNodes(isSelectable())[firstOtherTile].performClick()

        val icon = updated!!.icon
        assertTrue(AureoleIconCatalog.entries.any { it.key == icon })
        assertFalse(icon == appearance.icon)
        assertEquals(appearance.copy(icon = icon), updated)
    }

    @Test
    fun `when a theme color is chosen for the icon, should save it as a role and keep the other colors`() {
        var updated: QuickButtonAppearance? = null
        val appearance = QuickButtonAppearance(backgroundColor = ThemeColorRef.Fixed(1))
        setContent(QuickButtonModel(appearance = appearance)) { updated = it }
        openColors()

        tapThemeRole(ThemeColorRole.ACCENT)

        assertEquals(appearance.copy(iconColor = ThemeColorRef.Role(ThemeColorRole.ACCENT)), updated)
    }

    @Test
    fun `when Background color is the target, should change the background and not the icon`() {
        var updated: QuickButtonAppearance? = null
        val appearance = QuickButtonAppearance(iconColor = ThemeColorRef.Fixed(2))
        setContent(QuickButtonModel(appearance = appearance)) { updated = it }
        openColors()

        composeTestRule.onNodeWithText("Background color").performClick()
        tapThemeRole(ThemeColorRole.SURFACE)

        assertEquals(appearance.copy(backgroundColor = ThemeColorRef.Role(ThemeColorRole.SURFACE)), updated)
    }

    @Test
    fun `when stroke is disabled, should hide the stroke color and toggle stroke on click`() {
        var updated: QuickButtonAppearance? = null
        setContent(QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = false))) { updated = it }
        openColors()

        composeTestRule.onNodeWithText("STROKE COLOR").assertDoesNotExist()
        composeTestRule.onNodeWithText("Stroke").performScrollTo().performClick()

        assertNotNull(updated)
        assertTrue(updated!!.strokeEnabled)
    }

    @Test
    fun `when stroke is enabled, should show the stroke color picker`() {
        setContent(QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = true)))
        openColors()

        composeTestRule.onNodeWithText("STROKE COLOR").performScrollTo().assertIsDisplayed()
    }

    @Test
    fun `when Show name is toggled, should flip showLabel and keep the label`() {
        var updated: QuickButtonAppearance? = null
        val appearance = QuickButtonAppearance(label = "Mail")
        setContent(QuickButtonModel(appearance = appearance)) { updated = it }
        openColors()

        composeTestRule.onNodeWithText("Show name on the home").performScrollTo().performClick()

        assertEquals(appearance.copy(showLabel = true), updated)
    }
}
