package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.core.graphics.drawable.toDrawable
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.composable.dialogs.CreateFolderDialog
import dev.mnascimentos.aureole.composable.dialogs.FolderFormDialog
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.common.HomeScreenFolderOverlays
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerAppItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.GridFolderPopupParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val DISMISS_ANIMATION_MS = 500L

private const val SHEET_ENTER_MS = 1000L

/** The sheet hugs a 60% share of the window: the default Robolectric window leaves the grid no height. */
private const val TALL_WINDOW = "w400dp-h900dp"

@RunWith(RobolectricTestRunner::class)
class FolderComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Test App",
        packageName = "com.example.test",
        componentName = ComponentName("com.example.test", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )

    private val sampleFolder = AppFolder(
        id = "f1",
        name = "Social",
        appPackageNames = listOf(sampleApp.packageName)
    )

    // --- ContainerFolderMiniGrid Tests ---

    @Test
    fun `when rendering folder mini grid preview with apps, should display populated mini grid`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderMiniGridPreview(
                    folder = sampleFolder,
                    allApps = listOf(sampleApp)
                )
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when rendering folder mini grid preview empty, should display folder initial or icon`() {
        val emptyFolder = AppFolder(id = "e1", name = "EmptyFolder", appPackageNames = emptyList())
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderMiniGridPreview(
                    folder = emptyFolder,
                    allApps = emptyList()
                )
            }
        }

        composeTestRule.onNodeWithText("E").assertIsDisplayed()
    }

    // --- ContainerFolderComponents Tests ---

    @Test
    fun `when clicking container add folder button, should trigger onClick`() {
        var clicked = false
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ContainerAddFolderButton(
                    onClick = { clicked = true },
                    isBackgroundEnabled = true
                )
            }
        }

        composeTestRule.onNode(hasClickAction()).performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering container folder item, should display folder name label`() {
        var clicked = false
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    ContainerFolderItem(
                        params = ContainerFolderItemParams(
                            folder = sampleFolder,
                            isOpened = false,
                            config = ContainerFolderConfig(
                                showFolderLabels = true,
                                isGridFolderEnabled = false,
                                isBackgroundEnabled = true
                            ),
                            onFolderClick = { _, _ -> clicked = true }
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Social").assertIsDisplayed()
        composeTestRule.onNodeWithText("S").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering container app item, should display app label and trigger click`() {
        var clicked = false
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    ContainerAppItem(
                        params = ContainerAppItemParams(
                            app = sampleApp,
                            showLabels = true,
                            isBackgroundEnabled = true,
                            onAppClick = { clicked = true }
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
        composeTestRule.onNode(hasContentDescription("Test App")).performClick()
        assertTrue(clicked)
    }

    // --- ContainerFolderBottomSheet Tests ---

    @Test
    fun `when container folder bottom sheet is rendered, should display header and menu items`() {
        var editShortcutsClicked = false
        var renameClicked = false
        var iconClicked = false
        var removeFolderClicked = false
        var openSettingsClicked = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ContainerFolderBottomSheet(
                    params = ContainerFolderBottomSheetParams(
                        onEditShortcuts = { editShortcutsClicked = true },
                        onRename = { renameClicked = true },
                        onIcon = { iconClicked = true },
                        onRemoveFolder = { removeFolderClicked = true },
                        onOpenContainerSettings = { openSettingsClicked = true }
                    ),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Folder settings").assertIsDisplayed()
        composeTestRule.onNodeWithText("Edit shortcuts").performScrollTo().performClick()
        assertTrue(editShortcutsClicked)

        composeTestRule.onNodeWithText("Rename").performScrollTo().performClick()
        assertTrue(renameClicked)

        composeTestRule.onNodeWithText("Icon").performScrollTo().performClick()
        assertTrue(iconClicked)

        composeTestRule.onNodeWithText("Remove folder").performScrollTo().performClick()
        assertTrue(removeFolderClicked)

        composeTestRule.onNodeWithText("Container settings").performScrollTo().performClick()
        assertTrue(openSettingsClicked)
    }

    // --- FolderFormDialog & Create/Edit FolderDialog Tests ---

    @Test
    fun `when rendering CreateFolderDialog, should display New Folder title and allow submit`() {
        var submittedName = ""
        var submittedIcon: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CreateFolderDialog(
                    onDismiss = {},
                    onSubmit = { name, icon ->
                        submittedName = name
                        submittedIcon = icon
                    }
                )
            }
        }

        composeTestRule.onAllNodesWithText("New Folder")[0].assertIsDisplayed()

        // Input new folder name
        composeTestRule.onNode(hasSetTextAction()).performTextInput("My New Folder")

        // Click Create
        composeTestRule.onNodeWithText("Create").performClick()

        assertEquals("My New Folder", submittedName)
        // A new folder opens with the folder icon selected, so saving without choosing keeps it.
        assertEquals("folder", submittedIcon)
    }

    @Test
    fun `when rendering EditFolderDialog, should display Edit Folder title and delete button`() {
        var savedName = ""
        var deleted = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditFolderDialog(
                    folder = sampleFolder,
                    onDismiss = {},
                    onSave = { name, _ -> savedName = name },
                    onDelete = { deleted = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Folder").assertIsDisplayed()

        // Click Delete
        composeTestRule.onNodeWithText("Delete folder").performClick()
        assertTrue(deleted)

        // Click Save
        composeTestRule.onNodeWithText("Save", substring = true).performClick()
        assertEquals("Social", savedName)
    }

    // --- FolderDialog Tests ---

    @Test
    fun `when rendering FolderDialog with apps, should display header and apps`() {
        var appClicked = false
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    FolderDialog(
                        folder = sampleFolder,
                        allApps = listOf(sampleApp),
                        onDismiss = {},
                        onAppClick = { appClicked = true }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Social").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test App").assertIsDisplayed().performClick()
        assertTrue(appClicked)
    }

    @Test
    fun `when rendering empty FolderDialog, should display empty message`() {
        val emptyFolder = AppFolder(id = "f_empty", name = "Empty", appPackageNames = emptyList())
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    FolderDialog(
                        folder = emptyFolder,
                        allApps = emptyList(),
                        onDismiss = {},
                        onAppClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Folder is empty").assertIsDisplayed()
    }

    // --- FolderAppPickerDialog Tests ---

    @Config(qualifiers = TALL_WINDOW)
    @Test
    fun `when rendering FolderAppPickerDialog, should display selected and remaining sections`() {
        val otherApp = AppInfo(
            label = "Browser",
            packageName = "com.example.browser",
            componentName = ComponentName("com.example.browser", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        )

        var savedPackages = emptyList<String>()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderAppPickerDialog(
                    folder = sampleFolder,
                    allApps = listOf(sampleApp, otherApp),
                    onDismiss = {},
                    onSave = { savedPackages = it }
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(SHEET_ENTER_MS)

        composeTestRule.onNodeWithText("Apps in Folder: Social").assertIsDisplayed()
        // The chosen app shows both in the grid (checked) and in the tray.
        composeTestRule.onAllNodesWithText("Test App").assertCountEquals(2)
        composeTestRule.onNodeWithText("Browser").assertIsDisplayed()

        // Click Save
        composeTestRule.onNodeWithText("Save", substring = true).performClick()
        assertEquals(listOf(sampleApp.packageName), savedPackages)
    }

    // --- OpenedFolderActionsAndList & OpenedFolderPopup Tests ---

    @Test
    fun `when rendering OpenedFolderPopup in list mode, should show no Add Apps or Edit Folder buttons`() {
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(
                            folder = sampleFolder,
                            allApps = listOf(sampleApp),
                            actions = FolderPopupActions(onDismiss = {}, onAppClick = {})
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed()
        composeTestRule.onNodeWithText("Add Apps").assertDoesNotExist()
        composeTestRule.onNodeWithText("Edit Folder").assertDoesNotExist()
    }

    @Test
    fun `when rendering an empty OpenedFolderPopup in grid mode, should show no Add Apps or Edit Folder buttons`() {
        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(
                            folder = sampleFolder.copy(appPackageNames = emptyList()),
                            allApps = listOf(sampleApp),
                            actions = FolderPopupActions(onDismiss = {}, onAppClick = {}),
                            isGridFolderEnabled = true
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Add Apps").assertDoesNotExist()
        composeTestRule.onNodeWithText("Edit Folder").assertDoesNotExist()
    }

    @Test
    fun `when rendering OpenedFolderPopup, should display header banner and app row`() {
        var appClicked = false
        val actions = FolderPopupActions(
            onDismiss = {},
            onAppClick = { appClicked = true }
        )

        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(
                            folder = sampleFolder,
                            allApps = listOf(sampleApp),
                            actions = actions
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Social").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test App").assertIsDisplayed().performClick()
        assertTrue(appClicked)
    }

    @Test
    fun `when rendering GridFolderPopup, should display grid layout and title`() {
        var appClicked = false
        val actions = FolderPopupActions(
            onDismiss = {},
            onAppClick = { appClicked = true }
        )

        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState()) {
                AureoleLauncherTheme {
                    GridFolderPopup(
                        params = GridFolderPopupParams(
                            folder = sampleFolder,
                            appsInFolder = listOf(sampleApp),
                            actions = actions,
                            hazeState = null,
                            isHazeEnabled = false
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Social").assertIsDisplayed()
        composeTestRule.onNodeWithText("Test App").assertIsDisplayed().performClick()
        assertTrue(appClicked)
    }

    // --- Additional Comprehensive UI & Branch Tests ---

    @Test
    fun `when rendering FolderFormDialog with icon selected and clicking cancel, should invoke dismiss`() {
        var dismissed = false
        val folderWithIcon = AppFolder(id = "f_ic", name = "IconFolder", icon = "star")

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = folderWithIcon,
                    onDismiss = { dismissed = true },
                    onSave = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(DISMISS_ANIMATION_MS)
        assertTrue(dismissed)
    }

    @Config(qualifiers = TALL_WINDOW)
    @Test
    fun `when searching in FolderAppPickerDialog, should filter apps list`() {
        val app1 = AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "MainActivity"),
            icon = 0xFF1B65C0.toInt().toDrawable()
        )
        val app2 = AppInfo(
            label = "Gallery",
            packageName = "com.example.gallery",
            componentName = ComponentName("com.example.gallery", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        )
        val folder = AppFolder(id = "f1", name = "Media", appPackageNames = listOf("com.example.camera"))

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderAppPickerDialog(
                    folder = folder,
                    allApps = listOf(app1, app2),
                    onDismiss = {},
                    onSave = {}
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(SHEET_ENTER_MS)

        // Search for "Gall"
        composeTestRule.onNode(hasSetTextAction()).performTextInput("Gall")
        composeTestRule.mainClock.advanceTimeBy(400L)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Gallery").assertIsDisplayed()
    }

    @Config(qualifiers = TALL_WINDOW)
    @Test
    fun `when FolderAppPickerDialog has empty selected or remaining apps, should display empty status text`() {
        val folder = AppFolder(id = "f_empty", name = "EmptyFolder", appPackageNames = emptyList())

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderAppPickerDialog(
                    folder = folder,
                    allApps = emptyList(),
                    onDismiss = {},
                    onSave = {}
                )
            }
        }
        composeTestRule.mainClock.advanceTimeBy(SHEET_ENTER_MS)

        composeTestRule.onNodeWithText("No apps in the folder.").assertIsDisplayed()
        composeTestRule.onNodeWithText("No apps found.").assertIsDisplayed()
    }

    @Test
    fun `when OpenedFolderAppList rendered in left handed mode, should display app row correctly`() {
        var clickedApp: AppInfo? = null

        composeTestRule.setContent {
            ProvideHomeUiState(MainUiState(isLeftHandedMode = true)) {
                AureoleLauncherTheme {
                    OpenedFolderAppList(
                        appsInFolder = listOf(sampleApp),
                        isLeftHandedMode = true,
                        onAppClick = { clickedApp = it }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Test App").assertIsDisplayed().performClick()
        assertEquals(sampleApp, clickedApp)
    }

    @Test
    fun `when OpenedFolderAppList is empty, should display empty message`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                OpenedFolderAppList(
                    appsInFolder = emptyList(),
                    isLeftHandedMode = false,
                    onAppClick = {}
                )
            }
        }

        composeTestRule.onNodeWithText("No apps in folder").assertIsDisplayed()
    }

    @Test
    fun `when GridFolderPopup is empty, should display empty message`() {
        val emptyFolder = AppFolder(id = "g_empty", name = "Empty Grid", displayAsGrid = true)
        val actions = FolderPopupActions(
            onDismiss = {},
            onAppClick = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridFolderPopup(
                    params = GridFolderPopupParams(
                        folder = emptyFolder,
                        appsInFolder = emptyList(),
                        actions = actions,
                        hazeState = null,
                        isHazeEnabled = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Empty Grid").assertIsDisplayed()
        composeTestRule.onNodeWithText("No apps in the folder.").assertIsDisplayed()
    }

    @Test
    fun `when HomeScreenFolderOverlays is rendered with active overlays, should display overlays`() {
        val hazeState = HazeState()
        val actions = createDefaultHomeScreenActions()

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides MainUiState(
                    isCreateFolderDialogVisible = true,
                    activeFolder = sampleFolder,
                    isAddAppToFolderDialogVisible = false,
                    isRenameFolderDialogVisible = false
                ),
                LocalHomeActions provides actions
            ) {
                AureoleLauncherTheme {
                    HomeScreenFolderOverlays(
                        screenHeightPx = 1920f,
                        hazeState = hazeState
                    )
                }
            }
        }

        composeTestRule.onAllNodesWithText("New Folder")[0].assertIsDisplayed()
    }
}
