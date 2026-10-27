package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerColumnConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRenderItem
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRowConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContainerLayoutsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockHomeScreenActions(
        onAppClick: (AppInfo) -> Unit = {},
        onOpenEditContainerDialog: (String) -> Unit = {},
        onOpenContainerFolderBottomSheet: (AppFolder, String) -> Unit = { _, _ -> },
        onOpenContainerAppBottomSheet: (AppInfo, String) -> Unit = { _, _ -> }
    ) = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = onAppClick,
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onOpenEditContainerDialog = onOpenEditContainerDialog,
        onOpenContainerFolderBottomSheet = onOpenContainerFolderBottomSheet,
        onOpenContainerAppBottomSheet = onOpenContainerAppBottomSheet
    )

    private val sampleApp = AppInfo(
        label = "Test App Column",
        packageName = "com.example.col",
        componentName = ComponentName("com.example.col", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    private val sampleFolder = AppFolder(
        id = "f1",
        panelId = "p1",
        name = "Work Folder"
    )

    @Test
    fun `when rendering empty ContainerColumn with showAddFolderButton, should render column`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    ContainerColumn(
                        config = ContainerConfig(
                            panelId = "test_col",
                            isBackgroundEnabled = true,
                            showAddFolderButton = true
                        ),
                        columnConfig = ContainerColumnConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            verticalArrangement = Arrangement.Center,
                            onFolderClick = { _, _ -> }
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `when rendering empty ContainerColumn without showAddFolderButton, should render empty column`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    ContainerColumn(
                        config = ContainerConfig(
                            panelId = "test_col_empty",
                            isBackgroundEnabled = false,
                            showAddFolderButton = false
                        ),
                        columnConfig = ContainerColumnConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            verticalArrangement = Arrangement.Center,
                            onFolderClick = { _, _ -> }
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `when rendering ContainerColumn with app and folder items, should display items and handle clicks`() {
        var clickedApp: AppInfo? = null
        var clickedFolder: AppFolder? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
                    LocalHomeActions provides mockHomeScreenActions(
                        onAppClick = { clickedApp = it }
                    )
                ) {
                    ContainerColumn(
                        config = ContainerConfig(
                            panelId = "test_col_app",
                            appPackageNames = listOf("com.example.col"),
                            folders = listOf(sampleFolder),
                            showFolderLabels = true
                        ),
                        columnConfig = ContainerColumnConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            verticalArrangement = Arrangement.Center,
                            onFolderClick = { folder, _ -> clickedFolder = folder }
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Test App Column").assertIsDisplayed().performClick()
        assertEquals(sampleApp.packageName, clickedApp?.packageName)

        // Click folder button (shows "W" inside button)
        composeTestRule.onNodeWithText("W").assertIsDisplayed().performClick()
        assertEquals("f1", clickedFolder?.id)
    }

    @Test
    fun `when long pressing app item in ContainerColumn, should open app bottom sheet`() {
        var appBottomSheetPanelId: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
                    LocalHomeActions provides mockHomeScreenActions(
                        onOpenContainerAppBottomSheet = { _, panelId -> appBottomSheetPanelId = panelId }
                    )
                ) {
                    ContainerColumn(
                        config = ContainerConfig(
                            panelId = "p_col_app_bs",
                            appPackageNames = listOf("com.example.col"),
                            showFolderLabels = true
                        ),
                        columnConfig = ContainerColumnConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            verticalArrangement = Arrangement.Center,
                            onFolderClick = { _, _ -> }
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithContentDescription("Test App Column").performTouchInput { longClick() }
        assertEquals("p_col_app_bs", appBottomSheetPanelId)
    }

    @Test
    fun `when long pressing folder item in ContainerColumn, should open folder bottom sheet`() {
        var folderBottomSheetPanelId: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides mockHomeScreenActions(
                        onOpenContainerFolderBottomSheet = { _, panelId -> folderBottomSheetPanelId = panelId }
                    )
                ) {
                    ContainerColumn(
                        config = ContainerConfig(
                            panelId = "p_col_folder_bs",
                            folders = listOf(sampleFolder),
                            showFolderLabels = true
                        ),
                        columnConfig = ContainerColumnConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            verticalArrangement = Arrangement.Center,
                            onFolderClick = { _, _ -> }
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("W").performTouchInput { longClick() }
        assertEquals("p_col_folder_bs", folderBottomSheetPanelId)
    }

    @Test
    fun `when rendering ContainerRow with app and folder items, should display items`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    ContainerRow(
                        config = ContainerConfig(
                            panelId = "test_row_app",
                            appPackageNames = listOf("com.example.col"),
                            folders = listOf(sampleFolder),
                            showFolderLabels = true,
                            isBackgroundEnabled = true
                        ),
                        rowConfig = ContainerRowConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            horizontalArrangement = Arrangement.Center,
                            onFolderClick = { _, _ -> },
                            shouldExpand = true
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Test App Column").assertIsDisplayed()
        composeTestRule.onNodeWithText("Work Folder").assertIsDisplayed()
    }

    @Test
    fun `when rendering empty ContainerRow without showAddFolderButton, should render empty row`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    ContainerRow(
                        config = ContainerConfig(
                            panelId = "test_row_empty",
                            isBackgroundEnabled = false,
                            showAddFolderButton = false
                        ),
                        rowConfig = ContainerRowConfig(
                            backgroundColor = AureoleDS.colors.surface,
                            horizontalArrangement = Arrangement.Center,
                            onFolderClick = { _, _ -> }
                        )
                    )
                }
            }
        }
    }

    @Test
    fun `when resolving container render items with explicit items list, should resolve item entities correctly`() {
        val folderEntity = ContainerItemEntity(
            id = "e1",
            panelId = "p1",
            itemType = ContainerItemType.FOLDER,
            folderId = "f1"
        )
        val appEntity = ContainerItemEntity(
            id = "e2",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "com.example.col"
        )
        val missingEntity = ContainerItemEntity(
            id = "e3",
            panelId = "p1",
            itemType = ContainerItemType.APP,
            packageName = "com.missing"
        )

        val config = ContainerConfig(
            panelId = "p1",
            items = listOf(folderEntity, appEntity, missingEntity),
            folders = listOf(sampleFolder)
        )

        val resolved = resolveContainerRenderItems(config, listOf(sampleApp))
        assertEquals(2, resolved.size)
        assertTrue(resolved[0] is ContainerRenderItem.Folder)
        assertTrue(resolved[1] is ContainerRenderItem.App)
    }
}
