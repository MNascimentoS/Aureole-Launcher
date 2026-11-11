package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerFooterParams
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class FolderAppPickerTrayTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app1 = app("App One", "com.example.app1")
    private val app2 = app("App Two", "com.example.app2")

    private fun app(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )

    @Test
    fun `picker tray reorders through its move action and removes with the badge`() {
        val moves = mutableListOf<Pair<Int, Int>>()
        val removed = mutableListOf<String>()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderAppPickerFooter(
                    params = FolderAppPickerFooterParams(
                        trayApps = listOf(app1, app2),
                        searchQuery = ""
                    ),
                    actions = FolderAppPickerFooterActions(
                        onQueryChange = {},
                        onSearch = {},
                        onRemove = { removed += it },
                        onMove = { from, to -> moves += from to to }
                    )
                )
            }
        }

        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))[0]
            .performCustomAccessibilityActionWithLabel("Move right")
        composeTestRule.onAllNodesWithContentDescription("Remove")[1].performClick()

        assertEquals(listOf(0 to 1), moves)
        assertEquals(listOf(app2.packageName), removed)
    }

    @Test
    fun `picker tray shows the empty hint while browsing without apps`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderAppPickerFooter(
                    params = FolderAppPickerFooterParams(trayApps = emptyList(), searchQuery = ""),
                    actions = FolderAppPickerFooterActions(
                        onQueryChange = {},
                        onSearch = {},
                        onRemove = {},
                        onMove = { _, _ -> }
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("No apps in the folder.").assertIsDisplayed()
    }
}
