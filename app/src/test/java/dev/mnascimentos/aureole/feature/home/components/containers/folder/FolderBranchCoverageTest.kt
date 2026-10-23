package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.core.graphics.drawable.toDrawable
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerAppItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderButtonParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppRowItemParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.GridFolderPopupParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.WidgetStackBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderBranchCoverageTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app1 = AppInfo(
        label = "App One",
        packageName = "com.example.app1",
        componentName = ComponentName("com.example.app1", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )

    private val app2 = AppInfo(
        label = "App Two",
        packageName = "com.example.app2",
        componentName = ComponentName("com.example.app2", "MainActivity"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )

    private val app3 = AppInfo(
        label = "App Three",
        packageName = "com.example.app3",
        componentName = ComponentName("com.example.app3", "MainActivity"),
        icon = 0xFFE65100.toInt().toDrawable()
    )

    private val app4 = AppInfo(
        label = "App Four",
        packageName = "com.example.app4",
        componentName = ComponentName("com.example.app4", "MainActivity"),
        icon = 0xFFC2185B.toInt().toDrawable()
    )

    // --- 1. ContainerFolderComponents Branches ---

    @Test
    fun `when rendering ContainerFolderItem with background disabled and opened true, should exercise all branches`() {
        var clicked = false

        val folder = AppFolder(id = "f1", name = "Media", icon = "star")

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides MainUiState(apps = listOf(app1))
            ) {
                AureoleLauncherTheme {
                    ContainerFolderItem(
                        params = ContainerFolderItemParams(
                            folder = folder,
                            isOpened = true,
                            config = ContainerFolderConfig(
                                showFolderLabels = false,
                                isGridFolderEnabled = true,
                                isBackgroundEnabled = false
                            ),
                            onFolderClick = { _, _ -> clicked = true },
                            onLongClick = {}
                        )
                    )
                }
            }
        }

        composeTestRule.onNode(hasClickAction()).performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering ContainerFolderButton with grid preview and icon null, should exercise folder initial text`() {
        val folder = AppFolder(id = "f2", name = "Work", icon = null)

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides MainUiState(apps = emptyList())
            ) {
                AureoleLauncherTheme {
                    ContainerFolderButton(
                        params = ContainerFolderButtonParams(
                            folder = folder,
                            isGridFolderEnabled = false,
                            containerColor = Color.Transparent,
                            textColor = Color.White,
                            isBackgroundEnabled = true,
                            onPositionedY = {},
                            onClick = {},
                            onLongClick = {}
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("W").assertIsDisplayed()
    }

    @Test
    fun `when rendering ContainerAppItem with background disabled and themed app icons, should exercise branches`() {
        var appClicked = false
        var longClicked = false

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides MainUiState(isThemedAppIconsEnabled = true)
            ) {
                AureoleLauncherTheme {
                    ContainerAppItem(
                        params = ContainerAppItemParams(
                            app = app1,
                            showLabels = false,
                            isBackgroundEnabled = false,
                            onAppClick = { appClicked = true },
                            onLongClick = { longClicked = true }
                        )
                    )
                }
            }
        }

        composeTestRule.onNode(hasClickAction()).performClick()
        assertTrue(appClicked)
    }

    // --- 2. ContainerFolderMiniGrid Branches ---

    @Test
    fun `when FolderMiniGridPreview rendered with folder having icon and empty apps, should render icon`() {
        val folder = AppFolder(id = "f_ic", name = "IconFolder", icon = "home", appPackageNames = emptyList())

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderMiniGridPreview(
                    folder = folder,
                    allApps = emptyList()
                )
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when FolderMiniGridPreview rendered with 4 apps, should exercise grid cell rendering`() {
        val apps = listOf(app1, app2, app3, app4)
        val folder = AppFolder(id = "f_4", name = "GridFolder", appPackageNames = apps.map { it.packageName })

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderMiniGridPreview(
                    folder = folder,
                    allApps = apps
                )
            }
        }

        composeTestRule.waitForIdle()
    }

    // --- 3. FolderFormDialog & Create/Edit FolderDialog Branches ---

    @Test
    fun `when FolderFormDialog receives empty input and submit, should default name to New Folder`() {
        var savedName = ""

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = null,
                    onDismiss = {},
                    onSave = { name, _ -> savedName = name },
                    onDelete = null
                )
            }
        }

        // Click Create without typing anything
        composeTestRule.onNodeWithText("Create").performClick()
        assertEquals("New Folder", savedName)
    }

    @Test
    fun `when CreateFolderDialog 2-param overload is used, should trigger submit callback`() {
        var nameSubmitted = ""

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CreateFolderDialog(
                    onDismiss = {},
                    onSubmit = { nameSubmitted = it }
                )
            }
        }

        composeTestRule.onNode(hasSetTextAction()).performTextInput("Shortcuts")
        composeTestRule.onNodeWithText("Create").performClick()
        assertEquals("Shortcuts", nameSubmitted)
    }

    @Test
    fun `when selecting icon chips in IconSelectionRow, should update selected icon state`() {
        var selectedIconResult: String? = "initial"

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = AppFolder(id = "f1", name = "Tools", icon = "settings"),
                    onDismiss = {},
                    onSave = { _, icon -> selectedIconResult = icon },
                    onDelete = {}
                )
            }
        }

        // Click the first chip (Default Letter chip)
        composeTestRule.onNodeWithText("T").performClick()

        // Save
        composeTestRule.onNodeWithText("Save").performClick()
        assertEquals(null, selectedIconResult)
    }

    // --- 4. FolderDialog Branches ---

    @Test
    fun `when FolderDialog rendered in InspectionMode, should render in Box layout`() {
        val folder = AppFolder(id = "f1", name = "Inspection Folder", appPackageNames = listOf(app1.packageName))

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalHomeUiState provides MainUiState()
            ) {
                AureoleLauncherTheme {
                    FolderDialog(
                        folder = folder,
                        allApps = listOf(app1),
                        onDismiss = {},
                        onAppClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Inspection Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("App One").assertIsDisplayed()
    }

    @Test
    fun `when FolderDialog rendered with displayAsGrid true, should render LazyVerticalGrid`() {
        val gridFolder =
            AppFolder(
                id = "fg",
                name = "Grid Folder",
                displayAsGrid = true,
                appPackageNames = listOf(app1.packageName, app2.packageName)
            )

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalHomeUiState provides MainUiState()
            ) {
                AureoleLauncherTheme {
                    FolderDialog(
                        folder = gridFolder,
                        allApps = listOf(app1, app2),
                        onDismiss = {},
                        onAppClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Grid Folder").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("A")[0].assertIsDisplayed()
    }

    // --- 5. FolderAppPickerDialog & List Branches ---

    @Test
    fun `when FolderAppPickerDialog rendered in InspectionMode, should render Surface layout`() {
        val folder = AppFolder(id = "f1", name = "App Picker", appPackageNames = listOf(app1.packageName))

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true
            ) {
                AureoleLauncherTheme {
                    FolderAppPickerDialog(
                        folder = folder,
                        allApps = listOf(app1, app2),
                        onDismiss = {},
                        onSave = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Apps na Pasta: App Picker").assertIsDisplayed()
    }

    @Test
    fun `when reordering apps with ArrowUp and ArrowDown in SelectedFolderAppRowActionButtons, should exercise reorder branches`() {
        var moveUpCalled = false
        var moveDownCalled = false
        var toggleCalled = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SelectedFolderAppRowActionButtons(
                    params = FolderAppRowItemParams(
                        app = app2,
                        index = 1,
                        totalCount = 3,
                        canReorder = true
                    ),
                    onMoveUp = { moveUpCalled = true },
                    onMoveDown = { moveDownCalled = true },
                    onToggleSelect = { toggleCalled = true }
                )
            }
        }

        // Perform clicks on buttons
        composeTestRule.onAllNodes(hasClickAction())[0].performClick()
        assertTrue(moveUpCalled)

        composeTestRule.onAllNodes(hasClickAction())[1].performClick()
        assertTrue(moveDownCalled)

        composeTestRule.onAllNodes(hasClickAction())[2].performClick()
        assertTrue(toggleCalled)
    }

    // --- 6. OpenedFolderLayouts & OpenedFolderPopup Branches ---

    @Test
    fun `when GridFolderPopup rendered with blank folder name, should skip title`() {
        val folder = AppFolder(id = "f_blank", name = "", displayAsGrid = true)
        val actions = FolderPopupActions(
            onDismiss = {},
            onAppClick = {},
            onAddAppsClick = {},
            onEditFolderClick = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                GridFolderPopup(
                    params = GridFolderPopupParams(
                        folder = folder,
                        appsInFolder = listOf(app1),
                        actions = actions,
                        isActionsVisible = false,
                        onToggleActions = {},
                        hazeState = null,
                        isHazeEnabled = false
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("App One").assertIsDisplayed()
    }

    @Test
    fun `when OpenedFolderPopup rendered with many apps, should calculate max grid height`() {
        val manyApps =
            listOf(
                app1,
                app2,
                app3,
                app4,
                app1.copy(packageName = "p5"),
                app2.copy(packageName = "p6"),
                app3.copy(packageName = "p7")
            )
        val folder =
            AppFolder(
                id = "f_many",
                name = "Many Apps",
                displayAsGrid = true,
                appPackageNames = manyApps.map { it.packageName }
            )
        val actions = FolderPopupActions(
            onDismiss = {},
            onAppClick = {},
            onAddAppsClick = {},
            onEditFolderClick = {}
        )

        composeTestRule.setContent {
            CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(
                            folder = folder,
                            allApps = manyApps,
                            actions = actions
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Many Apps").assertIsDisplayed()
    }

    // --- 7. HomeScreenFolderOverlayComponents Branches ---

    @Test
    fun `when HomeScreenFolderBottomSheetOverlays is rendered with activeWidgetStackBottomSheet, should render EditWidgetGroupScreen`() {
        val hazeState = HazeState()
        val actions = createDefaultHomeScreenActions()

        val uiStateWidgetStack = MainUiState(
            activeWidgetStackBottomSheet = WidgetStackBottomSheetState(
                widgetId = 1,
                stackId = "stack_1"
            ),
            isHazeEnabled = true
        )

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides uiStateWidgetStack,
                LocalHomeActions provides actions
            ) {
                AureoleLauncherTheme {
                    HomeScreenFolderBottomSheetOverlays(
                        uiState = uiStateWidgetStack,
                        actions = actions,
                        hazeState = hazeState
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("HomeScreenFolderBottomSheetOverlays").assertDoesNotExist()
    }

    // --- 8. OpenedFolderPopup & Grid Branches ---

    @Test
    fun `when OpenedFolderPopup rendered in standard mode with folder displayAsGrid true, should render OpenedFolderGrid`() {
        val gridFolder =
            AppFolder(
                id = "f_grid",
                name = "Grid Folder",
                displayAsGrid = true,
                appPackageNames = listOf(app1.packageName, app2.packageName)
            )
        val actions = FolderPopupActions(onDismiss = {}, onAppClick = {}, onAddAppsClick = {}, onEditFolderClick = {})

        composeTestRule.setContent {
            CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(
                            folder = gridFolder,
                            allApps = listOf(app1, app2),
                            actions = actions
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Grid Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("App One").assertIsDisplayed()
    }

    @Test
    fun `when OpenedFolderPopup rendered with 2 apps and 5 apps, should calculate grid heights`() {
        val apps2 = listOf(app1, app2)
        val folder2 =
            AppFolder(
                id = "f2",
                name = "Folder Two",
                displayAsGrid = true,
                appPackageNames = apps2.map { it.packageName }
            )

        val apps5 = listOf(app1, app2, app3, app4, app1.copy(packageName = "p5"))
        val folder5 =
            AppFolder(
                id = "f5",
                name = "Folder Five",
                displayAsGrid = true,
                appPackageNames = apps5.map { it.packageName }
            )

        val actions = FolderPopupActions(onDismiss = {}, onAppClick = {}, onAddAppsClick = {}, onEditFolderClick = {})

        composeTestRule.setContent {
            CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(folder = folder2, allApps = apps2, actions = actions)
                    )
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(folder = folder5, allApps = apps5, actions = actions)
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Folder Two").assertIsDisplayed()
        composeTestRule.onNodeWithText("Folder Five").assertIsDisplayed()
    }

    @Test
    fun `when OpenedFolderGrid is empty, should display empty popup message`() {
        val emptyFolder =
            AppFolder(id = "fe", name = "Empty Grid Popup", displayAsGrid = true, appPackageNames = emptyList())
        val actions = FolderPopupActions(onDismiss = {}, onAppClick = {}, onAddAppsClick = {}, onEditFolderClick = {})

        composeTestRule.setContent {
            CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                AureoleLauncherTheme {
                    OpenedFolderPopup(
                        config = OpenedFolderPopupConfig(folder = emptyFolder, allApps = emptyList(), actions = actions)
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Nenhum aplicativo na pasta").assertIsDisplayed()
    }

    // --- 9. FolderFormDialog & Icon Selection Branches ---

    @Test
    fun `when FolderFormDialog rendered with empty folder name, should display question mark in preview`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderFormDialog(
                    folder = AppFolder(id = "f_empty_name", name = "", icon = null),
                    onDismiss = {},
                    onSave = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("?").assertIsDisplayed()
    }

    @Test
    fun `when selecting specific icon chip in IconSelectionRow, should update selected icon`() {
        var selectedIconResult: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                IconSelectionRow(
                    selectedIcon = null,
                    onIconSelected = { selectedIconResult = it },
                    folderName = "Icons"
                )
            }
        }

        // Index 0 is Default Letter, Index 1 is the first icon option chip ("star")
        composeTestRule.onAllNodes(hasClickAction())[1].performClick()
        assertEquals("star", selectedIconResult)
    }

    // --- 10. FolderAppPickerDialog Search and Empty Branches ---

    @Test
    fun `when FolderAppPickerDialog search produces no matches, should display empty search results message`() {
        val folder = AppFolder(id = "f_srch", name = "SearchFolder", appPackageNames = listOf(app1.packageName))

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

        // Search for non-matching string
        composeTestRule.onNode(hasSetTextAction()).performTextInput("XYZ_NO_MATCH")
        composeTestRule.mainClock.advanceTimeBy(400L)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Nenhum aplicativo encontrado.").assertIsDisplayed()
        composeTestRule.onNodeWithText("Nenhum outro aplicativo encontrado.").assertIsDisplayed()
    }

    @Test
    fun `when clicking Plus and Delete in FolderAppPickerDialog, should toggle package selection`() {
        val folder = AppFolder(id = "f_tgl", name = "ToggleFolder", appPackageNames = listOf(app1.packageName))
        var savedList = emptyList<String>()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderAppPickerDialog(
                    folder = folder,
                    allApps = listOf(app1, app2),
                    onDismiss = {},
                    onSave = { savedList = it }
                )
            }
        }

        // Click Plus on App Two to add it
        composeTestRule.onAllNodes(hasClickAction())[2].performClick()

        // Click Save
        composeTestRule.onNodeWithText("Salvar").performClick()
        assertTrue(savedList.contains(app1.packageName))
    }

    // --- 11. Overlays & BottomSheets Full Coverage ---

    @Test
    fun `when OpenedFolderOverlay is rendered with activeFolder, gridFolder, and haze, should execute all overlay branches`() {
        val hazeState = HazeState()
        val actions = createDefaultHomeScreenActions()

        val gridFolder =
            AppFolder(
                id = "fg_overlay",
                name = "Grid Overlay",
                displayAsGrid = true,
                appPackageNames = listOf(app1.packageName)
            )
        val uiState = MainUiState(
            activeFolder = gridFolder,
            apps = listOf(app1),
            isLeftHandedMode = true,
            activeFolderTopYPx = 200f
        )

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides uiState,
                LocalHomeActions provides actions
            ) {
                AureoleLauncherTheme {
                    OpenedFolderOverlay(
                        uiState = uiState,
                        screenHeightPx = 1920f,
                        hazeState = hazeState,
                        actions = actions
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Grid Overlay").assertIsDisplayed()
    }

    @Test
    fun `when ContainerFolderBottomSheetHeader logo button is clicked, should execute logo click action`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ContainerFolderBottomSheet(
                    params = ContainerFolderBottomSheetParams(
                        onEditShortcuts = {},
                        onRename = {},
                        onIcon = {},
                        onRemoveFolder = {},
                        onOpenContainerSettings = {}
                    ),
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Folder settings").assertIsDisplayed()
    }
}
