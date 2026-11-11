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
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenMode
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.backTarget
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val ANIMATION_MS = 500L

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
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

        composeTestRule.setContent {
            AureoleLauncherTheme {
                MainSettingsContent(
                    componentCount = 4,
                    onAppearanceClick = { appearanceClicked = true },
                    onBehaviorClick = { behaviorClicked = true },
                    onManageItemsClick = { manageItemsClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed().performClick()
        assertTrue(appearanceClicked)

        composeTestRule.onNodeWithText("Behavior").assertIsDisplayed().performClick()
        assertTrue(behaviorClicked)

        composeTestRule.onNodeWithText("Manage Items").assertIsDisplayed().performClick()
        assertTrue(manageItemsClicked)

        composeTestRule.onNodeWithText("OPTIONS").assertIsDisplayed()
        composeTestRule.onNodeWithText("4 components").assertIsDisplayed()
        composeTestRule.onNodeWithText("Orientation and alignment").assertIsDisplayed()
    }

    private fun setScreen(onSave: (ContainerModel) -> Unit, onDismiss: () -> Unit) {
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
                            onDismiss = onDismiss,
                            onSave = onSave
                        )
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(ANIMATION_MS)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when Save is tapped on the main screen, should save the draft and dismiss`() {
        var saved: ContainerModel? = null
        var dismissed = false
        setScreen(onSave = { saved = it }, onDismiss = { dismissed = true })

        composeTestRule.onNodeWithText("LIVE PREVIEW").assertIsDisplayed()
        composeTestRule.onNodeWithText("Save").performClick()
        composeTestRule.mainClock.advanceTimeBy(ANIMATION_MS)
        composeTestRule.waitForIdle()

        assertEquals("p1", saved?.id)
        assertTrue(dismissed)
    }

    @Test
    fun `when Cancel is tapped on the main screen, should dismiss without saving`() {
        var saved = false
        var dismissed = false
        setScreen(onSave = { saved = true }, onDismiss = { dismissed = true })

        composeTestRule.onNodeWithText("Cancel").performClick()
        composeTestRule.mainClock.advanceTimeBy(ANIMATION_MS)
        composeTestRule.waitForIdle()

        assertFalse(saved)
        assertTrue(dismissed)
    }

    @Test
    fun `when opening a sub screen, should hide the Cancel and Save bar`() {
        setScreen(onSave = {}, onDismiss = {})

        composeTestRule.onNodeWithText("Behavior").performClick()
        composeTestRule.mainClock.advanceTimeBy(ANIMATION_MS)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Save").assertDoesNotExist()
        composeTestRule.onNodeWithText("Cancel").assertDoesNotExist()
        composeTestRule.onNodeWithText("Back").assertExists()
    }

    @Test
    fun `backTarget goes to the main screen from a sub screen and closes from the main one`() {
        assertNull(EditContainerScreenMode.MAIN.backTarget())
        assertEquals(EditContainerScreenMode.MAIN, EditContainerScreenMode.APPEARANCE.backTarget())
        assertEquals(EditContainerScreenMode.MAIN, EditContainerScreenMode.BEHAVIOR.backTarget())
        assertEquals(EditContainerScreenMode.MAIN, EditContainerScreenMode.MANAGE_ITEMS.backTarget())
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
        composeTestRule.onNodeWithText("Back").assertIsDisplayed().performClick()
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
        composeTestRule.onNodeWithText("Back").assertIsDisplayed().performClick()
        assertTrue(backClicked)
    }
}
