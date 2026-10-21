package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetPickerAppListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleProviderInfo = AppWidgetProviderInfo().apply {
        provider = ComponentName("com.example.clock", "com.example.clock.ClockWidget")
    }

    private val sampleGroups = listOf(
        WidgetAppGroup(
            appId = "com.example.clock",
            appName = "Relógio",
            availableWidgets = listOf(
                WidgetVariant(widgetId = "clock_1", title = "Relógio Analógico", providerInfo = sampleProviderInfo),
                WidgetVariant(widgetId = "clock_2", title = "Relógio Digital", providerInfo = sampleProviderInfo),
            ),
        ),
        WidgetAppGroup(
            appId = "com.example.weather",
            appName = "Tempo",
            availableWidgets = listOf(
                WidgetVariant(widgetId = "weather_1", title = "Previsão Diária", providerInfo = sampleProviderInfo)
            )
        )
    )

    @Test
    fun `when rendering WidgetGroupList, should display section headers and app groups`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetGroupList(
                    state = WidgetSelectorState(
                        widgetGroups = sampleGroups,
                        expandedAppIds = emptySet()
                    ),
                    onIntent = {},
                    onWidgetSelected = {},
                    onDismiss = {}
                )
            }
        }

        // Section headers are uppercase first letter: 'R' for Relógio, 'T' for Tempo
        composeTestRule.onAllNodesWithText("R")[0].assertIsDisplayed()
        composeTestRule.onNodeWithText("Relógio").assertIsDisplayed()
        composeTestRule.onAllNodesWithText("T")[0].assertIsDisplayed()
        composeTestRule.onNodeWithText("Tempo").assertIsDisplayed()
    }

    @Test
    fun `when app group is clicked, should invoke ToggleAppGroup intent`() {
        var intentProcessed: WidgetSelectorIntent? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetGroupList(
                    state = WidgetSelectorState(
                        widgetGroups = sampleGroups,
                        expandedAppIds = emptySet()
                    ),
                    onIntent = { intentProcessed = it },
                    onWidgetSelected = {},
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Relógio").performClick()

        assertTrue(intentProcessed is WidgetSelectorIntent.ToggleAppGroup)
        assertEquals("com.example.clock", (intentProcessed as WidgetSelectorIntent.ToggleAppGroup).appId)
    }

    @Test
    fun `when app group is expanded, should render preview row with widgets and handle selection`() {
        var selectedProvider: AppWidgetProviderInfo? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetGroupList(
                    state = WidgetSelectorState(
                        widgetGroups = sampleGroups,
                        expandedAppIds = setOf("com.example.clock")
                    ),
                    onIntent = {},
                    onWidgetSelected = { selectedProvider = it },
                    onDismiss = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Relógio Analógico").assertIsDisplayed()
        composeTestRule.onNodeWithText("Relógio Digital").assertIsDisplayed()

        composeTestRule.onNodeWithText("Relógio Analógico").performClick()

        assertNotNull(selectedProvider)
        assertEquals("com.example.clock", selectedProvider?.provider?.packageName)
    }
}
