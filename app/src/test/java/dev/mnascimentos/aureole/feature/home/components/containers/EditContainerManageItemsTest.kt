package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowActions
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowParams
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerManageItemsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun createDefaultOptionsState() = ContainerOptionsState(
        position = "Center",
        orientation = "Vertical",
        isBackgroundEnabled = true,
        isExpandCell = false,
        showAddFolderButton = true,
        showFolderLabels = true,
        isGridFolderEnabled = false,
        hideText = false,
        textColor = null,
        backgroundColor = null,
        strokeEnabled = false,
        strokeColor = null,
        folderColor = null,
        folderStrokeEnabled = false,
        scrollUpShortcut = null,
        scrollDownShortcut = null
    )

    private val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    private val sampleFolder = AppFolder(
        id = "f1",
        panelId = "p1",
        name = "My Work Folder"
    )

    @Test
    fun `when ManageItemsContent renders with empty list, should display empty message and handle back button`() {
        var backClicked = false
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = emptyList(),
            folders = emptyList()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemsContent(
                    state = state,
                    allApps = listOf(sampleApp),
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("No component added yet.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Back").assertIsDisplayed().performClick()
        assertTrue(backClicked)
    }

    @Test
    fun `when clicking Pasta button in ManageItemsContent, should open CreateFolderDialog`() {
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = emptyList(),
            folders = emptyList()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemsContent(
                    state = state,
                    allApps = listOf(sampleApp),
                    onBack = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Folder").assertIsDisplayed().performClick()

        composeTestRule.onAllNodesWithText("New Folder")[0].assertIsDisplayed()
    }

    @Test
    fun `when ManageItemRow renders for FOLDER, should resolve title correctly`() {
        val folderItem = ContainerItemEntity(
            id = "e1",
            panelId = "p1",
            itemType = ContainerItemType.FOLDER,
            folderId = "f1"
        )

        val actions = ManageItemRowActions(
            onMoveUp = {},
            onMoveDown = {},
            onRemove = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemRow(
                    params = ManageItemRowParams(
                        index = 0,
                        item = folderItem,
                        totalItems = 2,
                        folders = listOf(sampleFolder),
                        allApps = listOf(sampleApp)
                    ),
                    actions = actions
                )
            }
        }

        composeTestRule.onNodeWithText("My Work Folder").assertIsDisplayed()
    }

    @Test
    fun `when ManageItemRow renders for unknown app package, should fallback to packageName`() {
        val unknownAppItem = ContainerItemEntity(
            id = "e3",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "com.unknown"
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemRow(
                    params = ManageItemRowParams(
                        index = 1,
                        item = unknownAppItem,
                        totalItems = 2,
                        folders = emptyList(),
                        allApps = emptyList()
                    ),
                    actions = ManageItemRowActions(
                        onMoveUp = {},
                        onMoveDown = {},
                        onRemove = {}
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("com.unknown").assertIsDisplayed()
    }
}
