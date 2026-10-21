package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerColumnConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerRowConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContainerLayoutsTest {

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
    fun `when rendering ContainerColumn with app item, should display app label`() {
        val app = AppInfo(
            label = "Test App Column",
            packageName = "com.example.col",
            componentName = ComponentName("com.example.col", "MainActivity"),
            icon = ColorDrawable(0xFF1B65C0.toInt())
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(apps = listOf(app)),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    ContainerColumn(
                        config = ContainerConfig(
                            panelId = "test_col_app",
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

        composeTestRule.onNodeWithText("Test App Column").assertIsDisplayed()
    }

    @Test
    fun `when rendering ContainerRow with app item, should display app label`() {
        val app = AppInfo(
            label = "Test App Row",
            packageName = "com.example.row",
            componentName = ComponentName("com.example.row", "MainActivity"),
            icon = ColorDrawable(0xFF1B65C0.toInt())
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides MainUiState(apps = listOf(app)),
                    LocalHomeActions provides mockHomeScreenActions()
                ) {
                    ContainerRow(
                        config = ContainerConfig(
                            panelId = "test_row_app",
                            appPackageNames = listOf("com.example.row"),
                            showFolderLabels = true
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

        composeTestRule.onNodeWithText("Test App Row").assertIsDisplayed()
    }

    @Test
    fun `when resolving container render items with empty items should return folders and apps`() {
        val app = AppInfo(
            label = "App1",
            packageName = "com.example.app1",
            componentName = ComponentName("com.example.app1", "MainActivity"),
            icon = ColorDrawable(0xFF1B65C0.toInt())
        )
        val config = ContainerConfig(
            panelId = "p1",
            appPackageNames = listOf("com.example.app1")
        )

        val renderItems = resolveContainerRenderItems(config, listOf(app))
        assertEquals(1, renderItems.size)
    }
}
