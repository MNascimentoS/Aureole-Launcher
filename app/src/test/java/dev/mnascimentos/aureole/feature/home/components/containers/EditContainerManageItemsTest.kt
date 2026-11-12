package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalTestApi::class)
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

    private fun setManageItems(state: EditContainerState, onBack: () -> Unit = {}) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemsContent(state = state, allApps = listOf(sampleApp), onBack = onBack)
            }
        }
    }

    private fun emptyState() = EditContainerState(
        panel = ContainerModel(id = "p1"),
        options = createDefaultOptionsState(),
        itemsList = emptyList(),
        folders = emptyList()
    )

    @Test
    fun `when creating a folder from the form, should add it to the list and close the form`() {
        val state = emptyState()
        setManageItems(state)

        composeTestRule.onNodeWithText("Folder").performClick()
        composeTestRule.onNodeWithText("Create").performClick()

        assertEquals(1, state.folders.size)
        assertEquals(1, state.itemsList.size)
        composeTestRule.onAllNodesWithText("Create").assertCountEquals(0)
    }

    @Test
    fun `when cancelling the folder form, should close only the form and keep the content`() {
        var backClicked = false
        val state = emptyState()
        setManageItems(state, onBack = { backClicked = true })

        composeTestRule.onNodeWithText("Folder").performClick()
        composeTestRule.onAllNodesWithText("New Folder")[0].assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()

        composeTestRule.onAllNodesWithText("New Folder").assertCountEquals(0)
        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
        assertTrue(state.folders.isEmpty())
        assertTrue(!backClicked)
    }

    @Test
    fun `when ManageItemRow renders for FOLDER, should resolve title correctly`() {
        val folderItem = ContainerItemEntity(
            id = "e1",
            panelId = "p1",
            itemType = ContainerItemType.FOLDER,
            folderId = "f1"
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemRow(
                    params = ManageItemRowParams(
                        item = folderItem,
                        folders = listOf(sampleFolder),
                        allApps = listOf(sampleApp)
                    )
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
                        item = unknownAppItem,
                        folders = emptyList(),
                        allApps = emptyList()
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("com.unknown").assertIsDisplayed()
    }

    @Test
    fun `when items are listed, should reorder through the move action and remove with the badge`() {
        val first = ContainerItemEntity(id = "a", panelId = "p1", itemType = ContainerItemType.APP, packageName = "one")
        val second = ContainerItemEntity(
            id = "b",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "two"
        )
        val state = EditContainerState(
            panel = ContainerModel(id = "p1"),
            options = createDefaultOptionsState(),
            itemsList = listOf(first, second),
            folders = emptyList()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemsContent(state = state, allApps = emptyList(), onBack = {})
            }
        }

        composeTestRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions))[0]
            .performCustomAccessibilityActionWithLabel("Move down")
        assertEquals(listOf("b", "a"), state.itemsList.map { it.id })

        composeTestRule.onAllNodesWithContentDescription("Remove")[0].performClick()
        assertEquals(listOf("a"), state.itemsList.map { it.id })
    }
}
