package dev.mnascimentos.aureole.telas.itemgrade

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.EditWidgetGroupScreen
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

/** Grupo de widgets: carrossel com três widgets, o "+" no fim e as setas para mover. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(createSampleHomeData().uiState) {
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
