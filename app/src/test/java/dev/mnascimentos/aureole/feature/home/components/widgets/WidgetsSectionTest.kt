package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.widget.TextView
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class WidgetsSectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when AddWidgetButton is rendered without haze, should display text and handle click`() {
        var clicked = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddWidgetButton(
                    onClick = { clicked = true },
                    isHazeEnabled = false
                )
            }
        }

        composeTestRule.onNodeWithText("Adicionar Widget").assertIsDisplayed()
        composeTestRule.onNodeWithText("Adicionar Widget").performClick()

        assertTrue(clicked)
    }

    @Test
    fun `when AddWidgetButton is rendered with haze enabled, should display text`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddWidgetButton(
                    onClick = {},
                    hazeState = HazeState(),
                    isHazeEnabled = true,
                    hazeOpacity = 0.8f
                )
            }
        }

        composeTestRule.onNodeWithText("Adicionar Widget").assertIsDisplayed()
    }

    @Test
    fun `when WidgetPositionIndicatorBar is rendered, should render without error`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetPositionIndicatorBar(
                    pageCount = 3,
                    currentPage = 1
                )
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when StackedWidgetSection rendered with widgets, should display pager`() {
        val config = StackedWidgetConfig(
            topWidgetIds = listOf(1, 2),
            currentHeightDp = 160.dp,
            currentHeightPx = 480f
        )
        val state = MainUiState(
            widgetStacks = mapOf("top_widgets" to listOf(1, 2))
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides state,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    StackedWidgetSection(
                        config = config,
                        stackId = "top_widgets"
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when StackedWidgetSection rendered with empty widgets, should render spacer without error`() {
        val config = StackedWidgetConfig(
            topWidgetIds = emptyList(),
            currentHeightDp = 160.dp,
            currentHeightPx = 480f
        )
        val state = MainUiState(
            widgetStacks = mapOf("top_widgets" to emptyList())
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides state,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    StackedWidgetSection(
                        config = config,
                        stackId = "top_widgets"
                    )
                }
            }
        }

        composeTestRule.waitForIdle()
    }

    @Test
    fun `when createErrorWidgetView is called, should return TextView with correct parameters`() {
        val context: Context = RuntimeEnvironment.getApplication()
        val errorView: TextView = createErrorWidgetView(context, "Test Error Message")

        assertNotNull(errorView)
        assertEquals("Test Error Message", errorView.text.toString())
        assertEquals(Color.WHITE, errorView.currentTextColor)
        assertEquals(Gravity.CENTER, errorView.gravity)
    }

    @Test
    fun `when createWidgetHostView with null host, should return error view`() {
        val context: Context = RuntimeEnvironment.getApplication()
        val view = createWidgetHostView(context, appWidgetHost = null, widgetId = 999)

        assertTrue(view is TextView)
        assertEquals("Widget indisponível", (view as TextView).text.toString())
    }

    @Test
    fun `when applyWidgetOptionsIfNeeded is called twice, should skip update on second call if tag matches`() {
        val context = RuntimeEnvironment.getApplication()
        val hostView = AppWidgetHostView(context)
        hostView.layout(0, 0, 300, 200)
        val appWidgetManager = AppWidgetManager.getInstance(context)
        val info = AppWidgetProviderInfo().apply {
            provider = ComponentName("com.example", "com.example.Widget")
            minWidth = 100
            minHeight = 100
        }

        applyWidgetOptionsIfNeeded(hostView, widgetId = 1, appWidgetManager = appWidgetManager, appWidgetInfo = info)
        val tagAfterFirstCall = hostView.tag
        assertNotNull(tagAfterFirstCall)

        // Second call with same dimensions should retain tag and skip updating options
        applyWidgetOptionsIfNeeded(hostView, widgetId = 1, appWidgetManager = appWidgetManager, appWidgetInfo = info)
        assertEquals(tagAfterFirstCall, hostView.tag)
    }
}
