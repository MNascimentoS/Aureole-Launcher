package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockHomeScreenActions() = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )

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

    private val samplePanel = ContainerModel(id = "p1", orientation = "Vertical")

    @Test
    fun `when EditContainerScreen renders with Vertical orientation, should render overlay title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    EditContainerScreen(
                        params = EditContainerScreenParams(
                            panel = samplePanel,
                            allApps = listOf(sampleApp),
                            onDismiss = {},
                            onSave = {}
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Container settings").assertIsDisplayed()
    }

    @Test
    fun `when EditContainerScreen renders with Horizontal orientation, should render overlay title`() {
        val horizontalPanel = samplePanel.copy(orientation = "Horizontal")

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalInspectionMode provides true,
                    LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    EditContainerScreen(
                        params = EditContainerScreenParams(
                            panel = horizontalPanel,
                            allApps = listOf(sampleApp),
                            onDismiss = {},
                            onSave = {}
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Container settings").assertIsDisplayed()
    }

    @Test
    fun `when MainSettingsContent renders, should show options and trigger callbacks`() {
        var appearanceClicked = false
        var behaviorClicked = false
        var manageItemsClicked = false
        var saveClicked = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                MainSettingsContent(
                    onAppearanceClick = { appearanceClicked = true },
                    onBehaviorClick = { behaviorClicked = true },
                    onManageItemsClick = { manageItemsClicked = true },
                    onSave = { saveClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed().performClick()
        assertTrue(appearanceClicked)

        composeTestRule.onNodeWithText("Behavior").assertIsDisplayed().performClick()
        assertTrue(behaviorClicked)

        composeTestRule.onNodeWithText("Manage Items").assertIsDisplayed().performClick()
        assertTrue(manageItemsClicked)

        composeTestRule.onNodeWithText("Salvar").assertIsDisplayed().performClick()
        assertTrue(saveClicked)
    }

    @Test
    fun `when rendering AppearanceSettingsContent in behavior mode, should display behavior switches`() {
        var backClicked = false
        val state = EditContainerState(
            panel = samplePanel,
            options = createDefaultOptionsState(),
            itemsList = emptyList(),
            folders = emptyList()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceSettingsContent(
                    state = state,
                    isBehaviorMode = true,
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Expand cells").assertIsDisplayed()
        composeTestRule.onNodeWithText("Grid folders").assertIsDisplayed()
        composeTestRule.onNodeWithText("Voltar").assertIsDisplayed().performClick()
        assertTrue(backClicked)
    }

    @Test
    fun `when rendering AppearanceSettingsContent in appearance mode, should display appearance switches`() {
        var backClicked = false
        val state = EditContainerState(
            panel = samplePanel,
            options = createDefaultOptionsState(),
            itemsList = emptyList(),
            folders = emptyList()
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppearanceSettingsContent(
                    state = state,
                    isBehaviorMode = false,
                    onBack = { backClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Background enabled").assertIsDisplayed()
        composeTestRule.onNodeWithText("Show folder labels").assertIsDisplayed()
        composeTestRule.onNodeWithText("Voltar").assertIsDisplayed().performClick()
        assertTrue(backClicked)
    }
}
