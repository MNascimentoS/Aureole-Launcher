package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerItemRowActions
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerItemRowParams
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerAppItemTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp1 = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    private val sampleApp2 = AppInfo(
        label = "Gallery",
        packageName = "com.example.gallery",
        componentName = ComponentName("com.example.gallery", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    @Test
    fun `when rendering EditContainerItemRow for folder, should display title and folder tag`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerItemRow(
                    params = EditContainerItemRowParams(
                        title = "Work Folder",
                        iconName = "briefcase",
                        isFolder = true,
                        canMoveUp = true,
                        canMoveDown = true
                    ),
                    actions = EditContainerItemRowActions(
                        onMoveUp = {},
                        onMoveDown = {},
                        onRemove = {}
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Work Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Pasta").assertIsDisplayed()
    }

    @Test
    fun `when rendering EditContainerItemRow for app, should display title and app tag`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerItemRow(
                    params = EditContainerItemRowParams(
                        title = "Calculator",
                        iconName = null,
                        isFolder = false,
                        canMoveUp = false,
                        canMoveDown = false
                    ),
                    actions = EditContainerItemRowActions(
                        onMoveUp = {},
                        onMoveDown = {},
                        onRemove = {}
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Calculator").assertIsDisplayed()
        composeTestRule.onNodeWithText("App").assertIsDisplayed()
    }

    @Test
    fun `when typing in EditContainerAddAppDropdown, should filter apps and handle selection`() {
        var selectedPkg: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerAddAppDropdown(
                    allApps = listOf(sampleApp1, sampleApp2),
                    onAppSelected = { selectedPkg = it },
                    label = "Adicionar App..."
                )
            }
        }

        val inputNode = composeTestRule.onNodeWithText("Adicionar App...")
        inputNode.assertIsDisplayed()
        inputNode.performTextInput("Cam")

        composeTestRule.onNodeWithText("Camera").assertIsDisplayed().performClick()
        assertEquals("com.example.camera", selectedPkg)
    }

    @Test
    fun `when searchQuery matches no apps in EditContainerAddAppDropdown, should handle gracefully`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerAddAppDropdown(
                    allApps = listOf(sampleApp1, sampleApp2),
                    onAppSelected = {},
                    label = "Adicionar App..."
                )
            }
        }

        composeTestRule.onNodeWithText("Adicionar App...").performTextInput("XYZNonExistent")
    }
}
