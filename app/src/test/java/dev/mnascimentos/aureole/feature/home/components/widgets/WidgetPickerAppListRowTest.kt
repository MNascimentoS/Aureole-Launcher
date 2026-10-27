package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WidgetPickerAppListRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering AppGroupHeader, should display app name and widget count`() {
        var clicked = false
        val group = WidgetAppGroup(
            appId = "com.example.calendar",
            appName = "Calendário",
            availableWidgets = listOf(
                WidgetVariant(widgetId = "cal_1", title = "Mês"),
                WidgetVariant(widgetId = "cal_2", title = "Agenda"),
            ),
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppGroupHeader(group = group) { clicked = true }
            }
        }

        composeTestRule.onNodeWithText("Calendário").assertIsDisplayed()
        composeTestRule.onNodeWithText("2 Widgets").assertIsDisplayed()

        composeTestRule.onNodeWithText("Calendário").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering AppGroupHeader with appIcon, should render appIcon`() {
        val dummyDrawable = ColorDrawable(Color.RED)
        val group = WidgetAppGroup(
            appId = "com.example.clock",
            appName = "Relógio",
            appIcon = dummyDrawable,
            availableWidgets = listOf(WidgetVariant(widgetId = "clock_1", title = "Relógio")),
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppGroupHeader(group = group) { }
            }
        }

        composeTestRule.onNodeWithText("Relógio").assertIsDisplayed()
    }

    @Test
    fun `when rendering AppGroupHeader with single widget, should show singular text`() {
        val group = WidgetAppGroup(
            appId = "com.example.notes",
            appName = "Notas",
            availableWidgets = listOf(
                WidgetVariant(widgetId = "note_1", title = "Minhas Notas"),
            ),
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppGroupHeader(group = group) { }
            }
        }

        composeTestRule.onNodeWithText("Notas").assertIsDisplayed()
        composeTestRule.onNodeWithText("1 Widget").assertIsDisplayed()
    }

    @Test
    fun `when rendering WidgetPreviewCard with fallback text, should display fallback text`() {
        var clicked = false
        val variant = WidgetVariant(
            widgetId = "widget_1",
            title = "Visão Geral",
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetPreviewCard(
                    widget = variant,
                    fallbackAppIcon = null,
                    onClick = { clicked = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Visão Geral").assertIsDisplayed()
        composeTestRule.onNodeWithText("About the widget here").assertIsDisplayed()
        composeTestRule.onNodeWithText("Widget").assertIsDisplayed()

        composeTestRule.onNodeWithText("Visão Geral").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when rendering WidgetPreviewCard with previewImage, should render previewImage`() {
        val dummyDrawable = ColorDrawable(Color.BLUE)
        val variant = WidgetVariant(
            widgetId = "widget_2",
            title = "Imagem Preview",
            previewImage = dummyDrawable,
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetPreviewCard(
                    widget = variant,
                    fallbackAppIcon = null,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Imagem Preview").assertIsDisplayed()
    }

    @Test
    fun `when rendering WidgetPreviewCard with fallbackAppIcon, should render fallbackAppIcon`() {
        val dummyDrawable = ColorDrawable(Color.GREEN)
        val variant = WidgetVariant(
            widgetId = "widget_3",
            title = "Fallback Icon Widget",
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetPreviewCard(
                    widget = variant,
                    fallbackAppIcon = dummyDrawable,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Fallback Icon Widget").assertIsDisplayed()
    }

    @Test
    fun `when rendering WidgetPreviewCard with previewLayoutRes, should attempt preview layout rendering`() {
        val dummyDrawable = ColorDrawable(Color.YELLOW)
        val info = AppWidgetProviderInfo().apply {
            provider = ComponentName("dev.mnascimentos.aureole", "dev.mnascimentos.aureole.Widget")
        }
        val variant = WidgetVariant(
            widgetId = "widget_layout",
            title = "Layout Widget",
            previewLayoutRes = 12345,
            providerInfo = info,
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                WidgetPreviewCard(
                    widget = variant,
                    fallbackAppIcon = dummyDrawable,
                    onClick = {},
                )
            }
        }

        composeTestRule.onNodeWithText("Layout Widget").assertIsDisplayed()
    }
}
