package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.di.appModule
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetPickerScreenParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorScreenParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WidgetPickerScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setUp() {
        val application = RuntimeEnvironment.getApplication()
        if (GlobalContext.getOrNull() == null) {
            startKoin {
                androidContext(application)
                modules(appModule)
            }
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `when WidgetPickerScreen is rendered, should inject viewModel and render screen`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                    WidgetPickerScreen(
                        params = WidgetPickerScreenParams(
                            onWidgetSelected = {},
                            onDismiss = {},
                            isBlurEnabled = true
                        )
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when state is loading, should render progress indicator`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                    WidgetSelectorScreen(
                        params = WidgetSelectorScreenParams(
                            state = WidgetSelectorState(isLoading = true, widgetGroups = emptyList()),
                            onIntent = {},
                            onWidgetSelected = {},
                            onDismiss = {},
                        )
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when state is empty and query is empty, should display empty message`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                    WidgetSelectorScreen(
                        params = WidgetSelectorScreenParams(
                            state = WidgetSelectorState(isLoading = false, widgetGroups = emptyList()),
                            onIntent = {},
                            onWidgetSelected = {},
                            onDismiss = {},
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("No widget found.").assertIsDisplayed()
    }

    @Test
    fun `when state has widgets, should render widget group list`() {
        val sampleState = WidgetSelectorState(
            isLoading = false,
            widgetGroups = listOf(
                WidgetAppGroup(
                    appId = "com.example.calendar",
                    appName = "Calendar",
                    availableWidgets = listOf(
                        WidgetVariant(widgetId = "cal_1", title = "Agenda")
                    )
                )
            )
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
                    WidgetSelectorScreen(
                        params = WidgetSelectorScreenParams(
                            state = sampleState,
                            hazeState = HazeState(),
                            onIntent = {},
                            onWidgetSelected = {},
                            onDismiss = {}
                        )
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
    }
}
