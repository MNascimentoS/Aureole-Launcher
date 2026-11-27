package dev.mnascimentos.aureole.telas.widgets

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.EditWidgetGroupScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.WidgetSelectorScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorScreenParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

private const val CALENDAR_ID = "com.example.calendar"

/** Seletor de widgets com um app aberto e duas variantes. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() {
    val group = WidgetAppGroup(
        appId = CALENDAR_ID,
        appName = "Calendar",
        availableWidgets = listOf(
            WidgetVariant(widgetId = "calendar_1", title = "Month View", minSpanX = 2, minSpanY = 2),
            WidgetVariant(widgetId = "calendar_2", title = "Agenda View", minSpanX = 4, minSpanY = 2)
        )
    )
    val state = WidgetSelectorState(
        isLoading = false,
        searchQuery = "",
        widgetGroups = listOf(group),
        expandedAppIds = setOf(CALENDAR_ID)
    )
    HomeCase(createSampleHomeData().uiState) {
        WidgetSelectorScreen(
            params = WidgetSelectorScreenParams(state = state, onIntent = {}, onWidgetSelected = {}, onDismiss = {})
        )
    }
}

/** Grupo de widgets: carrossel com três widgets, o "+" no fim e as setas para mover. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun GrupoScreenshotTest() = HomeCase(createSampleHomeData().uiState) {
    EditWidgetGroupScreen(
        params = EditWidgetGroupParams(
            item = LauncherItemState(
                id = "widget_group_1",
                type = LauncherItemType.WIDGET_LIST,
                col = 0,
                row = 0,
                colSpan = 2,
                rowSpan = 2
            ),
            widgets = listOf(1, 2, 3),
            appWidgetHost = null,
            isIndicatorEnabled = true
        ),
        actions = EditWidgetGroupActions(
            onToggleIndicator = {},
            onRemoveWidget = {},
            onAddWidgetClick = {},
            onDismissRequest = {}
        )
    )
}
