package dev.mnascimentos.aureole.composable.menu

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MenuRowColorsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `title color is onSurfaceMedium for normal rows and error for destructive ones`() {
        var normal = Color.Unspecified
        var destructive = Color.Unspecified
        var medium = Color.Unspecified
        var error = Color.Unspecified
        composeTestRule.setContent {
            AureoleLauncherTheme {
                normal = menuRowTitleColor(isDestructive = false)
                destructive = menuRowTitleColor(isDestructive = true)
                medium = AureoleDS.colors.onSurfaceMedium
                error = AureoleDS.colors.error
            }
        }
        composeTestRule.waitForIdle()

        assertEquals(medium, normal)
        assertEquals(error, destructive)
        assertNotEquals(normal, destructive)
    }
}
