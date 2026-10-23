package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.core.graphics.drawable.toDrawable
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.ContainerAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.ContainerFolderBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.FolderAppBottomSheetState
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenFolderOverlaysTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )

    private val sampleFolder = AppFolder(
        id = "f1",
        name = "Media",
        appPackageNames = listOf(sampleApp.packageName)
    )

    @Test
    fun `when CreateFolderOverlay is visible, should display CreateFolderDialog`() {
        var createClosed = false
        val actions = createDefaultHomeScreenActions().copy(
            onFolderIntent = { createClosed = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CreateFolderOverlay(
                    isVisible = true,
                    onCloseFolder = { actions.onFolderIntent(FolderViewIntent.CloseFolder) },
                    onSubmitName = { _, _ -> }
                )
            }
        }

        composeTestRule.onAllNodesWithText("New Folder")[0].assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(createClosed)
    }

    @Test
    fun `when CreateFolderOverlay is not visible, should not render dialog`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CreateFolderOverlay(
                    isVisible = false,
                    onCloseFolder = {},
                    onSubmitName = { _, _ -> }
                )
            }
        }

        composeTestRule.onNodeWithText("New Folder").assertDoesNotExist()
    }

    @Test
    fun `when AddAppToFolderOverlay is visible, should display FolderAppPickerDialog`() {
        var addAppClosed = false
        val actions = createDefaultHomeScreenActions().copy(
            onSetAddAppToFolderDialogVisible = { addAppClosed = true }
        )

        val uiState = MainUiState(
            isAddAppToFolderDialogVisible = true,
            activeFolder = sampleFolder,
            apps = listOf(sampleApp)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddAppToFolderOverlay(
                    uiState = uiState,
                    actions = actions
                )
            }
        }

        composeTestRule.onNodeWithText("Apps na Pasta: Media").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(addAppClosed)
    }

    @Test
    fun `when RenameFolderOverlay is visible, should display EditFolderDialog`() {
        var renameClosed = false
        val actions = createDefaultHomeScreenActions().copy(
            onSetRenameFolderDialogVisible = { renameClosed = true }
        )

        val uiState = MainUiState(
            isRenameFolderDialogVisible = true,
            activeFolder = sampleFolder
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                RenameFolderOverlay(
                    uiState = uiState,
                    actions = actions
                )
            }
        }

        composeTestRule.onNodeWithText("Edit Folder").assertIsDisplayed()
        composeTestRule.onNodeWithText("Cancel").performClick()
        assertTrue(renameClosed)
    }

    @Test
    fun `when OpenedFolderOverlay is visible in standard mode, should render OpenedFolderPopup`() {
        val hazeState = HazeState()
        val actions = createDefaultHomeScreenActions()

        val uiState = MainUiState(
            activeFolder = sampleFolder,
            apps = listOf(sampleApp)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                OpenedFolderOverlay(
                    uiState = uiState,
                    screenHeightPx = 1920f,
                    hazeState = hazeState,
                    actions = actions
                )
            }
        }

        composeTestRule.onNodeWithText("Media").assertIsDisplayed()
        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
    }

    @Test
    fun `when OpenedFolderOverlay is visible in grid mode, should render GridFolderPopup`() {
        val hazeState = HazeState()
        val actions = createDefaultHomeScreenActions()

        val gridFolder = sampleFolder.copy(displayAsGrid = true)
        val uiState = MainUiState(
            activeFolder = gridFolder,
            apps = listOf(sampleApp)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                OpenedFolderOverlay(
                    uiState = uiState,
                    screenHeightPx = 1920f,
                    hazeState = hazeState,
                    actions = actions
                )
            }
        }

        composeTestRule.onNodeWithText("Media").assertIsDisplayed()
        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
    }

    @Test
    fun `when ContainerAppAndFolderOverlays is rendered with active bottom sheets, should render bottom sheets and trigger callbacks`() {
        var closedContainerFolderSheet = false
        var closedFolderAppSheet = false
        var closedContainerAppSheet = false

        val actions = createDefaultHomeScreenActions().copy(
            onCloseContainerFolderBottomSheet = { closedContainerFolderSheet = true },
            onCloseFolderAppBottomSheet = { closedFolderAppSheet = true },
            onCloseContainerAppBottomSheet = { closedContainerAppSheet = true }
        )

        val uiStateFolderSheet = MainUiState(
            activeContainerFolderBottomSheet = ContainerFolderBottomSheetState(
                folder = sampleFolder,
                panelId = "p1"
            )
        )

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides uiStateFolderSheet,
                LocalHomeActions provides actions
            ) {
                AureoleLauncherTheme {
                    HomeScreenFolderOverlays(
                        screenHeightPx = 1920f,
                        hazeState = HazeState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Folder settings").assertIsDisplayed()
    }

    @Test
    fun `when activeFolderAppBottomSheet is present, should render AppItemBottomSheet for folder app`() {
        var closedFolderAppSheet = false
        val actions = createDefaultHomeScreenActions().copy(
            onCloseFolderAppBottomSheet = { closedFolderAppSheet = true }
        )

        val uiStateFolderAppSheet = MainUiState(
            activeFolderAppBottomSheet = FolderAppBottomSheetState(
                folder = sampleFolder,
                app = sampleApp
            )
        )

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides uiStateFolderAppSheet,
                LocalHomeActions provides actions
            ) {
                AureoleLauncherTheme {
                    HomeScreenFolderOverlays(
                        screenHeightPx = 1920f,
                        hazeState = HazeState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Remover da Pasta").assertIsDisplayed()
        composeTestRule.onNodeWithText("Configurações da Pasta").assertIsDisplayed()
    }

    @Test
    fun `when activeContainerAppBottomSheet is present, should render AppItemBottomSheet for container app`() {
        var closedContainerAppSheet = false
        val actions = createDefaultHomeScreenActions().copy(
            onCloseContainerAppBottomSheet = { closedContainerAppSheet = true }
        )

        val uiStateContainerAppSheet = MainUiState(
            activeContainerAppBottomSheet = ContainerAppBottomSheetState(
                app = sampleApp,
                panelId = "p1"
            )
        )

        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalHomeUiState provides uiStateContainerAppSheet,
                LocalHomeActions provides actions
            ) {
                AureoleLauncherTheme {
                    HomeScreenFolderOverlays(
                        screenHeightPx = 1920f,
                        hazeState = HazeState()
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("App Info").assertIsDisplayed()
        composeTestRule.onNodeWithText("Configurações do Container").assertIsDisplayed()
    }
}
