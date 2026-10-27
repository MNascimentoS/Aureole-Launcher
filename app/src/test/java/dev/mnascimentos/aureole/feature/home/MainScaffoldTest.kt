package dev.mnascimentos.aureole.feature.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering MainScaffold with default state, should render home screen`() {
        val uiState = MainUiState()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainScaffold(
                        appWidgetHost = null,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }

    @Test
    fun `when showWidgetPicker is true, should render widget picker overlay`() {
        val uiState = MainUiState(showWidgetPicker = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainScaffold(
                        appWidgetHost = null,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }

    @Test
    fun `when showFavoritePickerDialog is true, should render favorite picker overlay`() {
        val uiState = MainUiState(showFavoritePickerDialog = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainScaffold(
                        appWidgetHost = null,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }

    @Test
    fun `when showUpdateAvailableDialog is true, should render update available overlay`() {
        val uiState = MainUiState(showUpdateAvailableDialog = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainScaffold(
                        appWidgetHost = null,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }

    @Test
    fun `when showUpdateDownloadedDialog is true, should render update downloaded overlay`() {
        val uiState = MainUiState(showUpdateDownloadedDialog = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainScaffold(
                        appWidgetHost = null,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }

    @Test
    fun `when showReleaseNotesBottomSheet is true, should render release notes overlay`() {
        val uiState = MainUiState(showReleaseNotesBottomSheet = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainScaffold(
                        appWidgetHost = null,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }
}
