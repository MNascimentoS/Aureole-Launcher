package dev.mnascimentos.aureole.feature.home

import android.content.Context
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class MainScaffoldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)
        val appRepository = AppRepository(context)
        val widgetRepository = WidgetRepository(context)
        val folderRepository = FolderRepository(context)
        val favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository)
        val containerRepository = ContainerRepository(context)
        val gridRepository = GridRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = appRepository,
                widgetRepository = widgetRepository,
                settingsRepository = settingsRepository,
                folderRepository = folderRepository,
                favoriteContainerRepository = favoriteContainerRepository,
                containerRepository = containerRepository,
                gridRepository = gridRepository
            )
        )
    }

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
                        viewModel = viewModel,
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
                        viewModel = viewModel,
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
                        viewModel = viewModel,
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
                        viewModel = viewModel,
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
                        viewModel = viewModel,
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
                        viewModel = viewModel,
                        onWidgetSelected = {}
                    )
                }
            }
        }
    }
}
