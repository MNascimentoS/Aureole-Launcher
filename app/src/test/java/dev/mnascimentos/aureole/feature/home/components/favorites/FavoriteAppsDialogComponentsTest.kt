package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppRemoveButton
import dev.mnascimentos.aureole.composable.favorites.FavoriteAppRowInfo
import dev.mnascimentos.aureole.composable.favorites.RemainingAppRow
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FavoriteAppsDialogComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.BLUE)
    )

    @Test
    fun `when rendering FavoriteAppRowInfo, should display app label`() {
        val app = createApp("Meu App Teste", "com.example.test")
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppRowInfo(app = app)
            }
        }

        composeTestRule.onNodeWithText("Meu App Teste").assertIsDisplayed()
    }

    @Test
    fun `when rendering FavoriteAppRemoveButton, should handle the click`() {
        var removed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppRemoveButton(onClick = { removed = true })
            }
        }

        composeTestRule.onNodeWithContentDescription("Remove from favorites").performClick()
        assertTrue(removed)
    }

    @Test
    fun `when rendering RemainingAppRow, should display label and respond to toggle click`() {
        var toggled = false
        val app = createApp("Outro App", "com.example.other")

        composeTestRule.setContent {
            AureoleLauncherTheme {
                RemainingAppRow(
                    app = app,
                    onToggleFavorite = { toggled = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Outro App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Outro App").performClick()
        assertTrue(toggled)
    }
}
