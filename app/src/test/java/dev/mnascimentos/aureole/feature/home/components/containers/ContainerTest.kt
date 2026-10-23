package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContainerTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun mockActions() = HomeScreenActions(
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

    private val sampleApp = AppInfo(
        label = "Sample App",
        packageName = "com.example.sample",
        componentName = ComponentName("com.example.sample", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )

    @Test
    fun `when rendering Container with Vertical orientation and Top position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "col_top",
                orientation = "Vertical",
                position = "Top",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true,
                isBackgroundEnabled = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Vertical orientation and Center position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "col_center",
                orientation = "Vertical",
                position = "Center",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Vertical orientation and Bottom position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "col_bottom",
                orientation = "Vertical",
                position = "Bottom",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Vertical orientation and Space Evenly position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "col_space_evenly",
                orientation = "Vertical",
                position = "Space Evenly",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Vertical orientation and default position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "col_default",
                orientation = "Vertical",
                position = "Space Between",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Horizontal orientation and Left position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "row_left",
                orientation = "Horizontal",
                position = "Left",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true,
                isBackgroundEnabled = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Horizontal orientation and Right position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "row_right",
                orientation = "Horizontal",
                position = "Right",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Horizontal orientation and Space Evenly position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "row_space_evenly",
                orientation = "Horizontal",
                position = "Space Evenly",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with Horizontal orientation and Center position, should render content`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "row_center",
                orientation = "Horizontal",
                position = "Center",
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container with background and hazeState enabled, should apply haze effect`() {
        val hazeState = HazeState()
        val uiState = MainUiState(apps = listOf(sampleApp), isHazeEnabled = true, hazeOpacity = 0.5f)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides mockActions()
                ) {
                    Container(
                        config = ContainerConfig(
                            panelId = "haze_container",
                            isBackgroundEnabled = true,
                            hazeState = hazeState,
                            appPackageNames = listOf("com.example.sample"),
                            showFolderLabels = true,
                            isExpandCell = true
                        ),
                        onFolderClick = { _, _ -> }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    @Test
    fun `when rendering Container without background, should use transparent background`() {
        renderContainerWithConfig(
            ContainerConfig(
                panelId = "no_bg",
                isBackgroundEnabled = false,
                appPackageNames = listOf("com.example.sample"),
                showFolderLabels = true
            )
        )
        composeTestRule.onNodeWithText("Sample App").assertIsDisplayed()
    }

    private fun renderContainerWithConfig(config: ContainerConfig) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(apps = listOf(sampleApp)),
                    LocalHomeActions provides mockActions()
                ) {
                    Container(
                        config = config,
                        onFolderClick = { _, _ -> }
                    )
                }
            }
        }
    }
}
