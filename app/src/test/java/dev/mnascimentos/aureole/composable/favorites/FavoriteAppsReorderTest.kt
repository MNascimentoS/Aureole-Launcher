package dev.mnascimentos.aureole.composable.favorites

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class FavoriteAppsReorderTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun app(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.BLUE)
    )

    @Test
    fun `moving a favorite saves the new order once, keeping apps that are not installed in place`() {
        val saved = mutableListOf<Pair<String?, List<String>>>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FavoriteAppsSheetContent(
                    config = FavoriteAppsDialogConfig(
                        allApps = listOf(app("Uno", "a"), app("Dos", "b")),
                        favoriteAppPackages = listOf("a", "gone", "b"),
                        containerId = "c1"
                    ),
                    actions = FavoriteAppsDialogActions(onUpdateFavoritePackages = { id, list -> saved += id to list })
                )
            }
        }

        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))[0]
            .performCustomAccessibilityActionWithLabel("Move down")

        assertEquals(listOf("c1" to listOf("b", "gone", "a")), saved)
    }
}
