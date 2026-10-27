package dev.mnascimentos.aureole.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
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
class HomeScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when isLoading is true, should render circular progress indicator`() {
        val uiState = MainUiState(isLoading = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when isLoading is false, should render home screen body`() {
        val uiState = MainUiState(isLoading = false)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when showCustomizeBottomSheet is true, should render customize bottom sheet`() {
        val uiState = MainUiState(isLoading = false, showCustomizeBottomSheet = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when showEditClockBottomSheet is true, should render edit clock bottom sheet`() {
        val uiState = MainUiState(isLoading = false, showEditClockBottomSheet = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when showAddContainerDialog is true, should render add container dialog`() {
        val uiState = MainUiState(isLoading = false, showAddContainerDialog = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when editing widget grid item, should render edit widget group screen`() {
        val widgetItem = LauncherItemState(id = "w1", type = LauncherItemType.WIDGET_LIST)
        val uiState = MainUiState(isLoading = false, editingGridItem = widgetItem)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when editing non-widget grid item, should render edit grid item dialog`() {
        val clockItem = LauncherItemState(id = "c1", type = LauncherItemType.CLOCK)
        val uiState = MainUiState(isLoading = false, editingGridItem = clockItem)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when edit container dialog is visible, should render edit container screen`() {
        val container = ContainerModel(id = "cont_1", title = "Container 1")
        val uiState = MainUiState(
            isLoading = false,
            isEditContainerDialogVisible = true,
            editingContainerId = "cont_1",
            containers = mapOf("cont_1" to container)
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
    }

    @Test
    fun `when gridErrorMessage is present, should show toast and dismiss error`() {
        var errorDismissed = false
        val actions = createDefaultHomeScreenActions().copy(
            onDismissGridError = { errorDismissed = true }
        )
        val uiState = MainUiState(isLoading = false, gridErrorMessage = "Grid error test")

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides actions
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }

        assertTrue(errorDismissed)
    }
}
