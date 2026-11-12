package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetManager
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetCarouselParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetSheetParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetMoveParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackDraft
import dev.mnascimentos.aureole.feature.home.components.widgets.model.widgetStepTarget
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWidgetGroupScreen(
    params: EditWidgetGroupParams,
    actions: EditWidgetGroupActions,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }
    val draft = remember { WidgetStackDraft(params.widgets) }
    remember(params.widgets) { draft.sync(params.widgets) }
    val scrollState = rememberScrollState()
    val isLeftHanded = LocalHomeUiState.current.isLeftHandedMode

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(ANIMATION_DURATION_MS.toLong())
            actions.onDismissRequest()
        }
    }

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.home_widget_stack_title),
                hazeState = params.hazeState,
                onSettingsClick = actions.onSettingsClick,
                isLeftHanded = isLeftHanded
            ),
            previewContent = {
                AureolePreviewFrame { StackPreview(params, actions, draft, scrollState) }
            },
            bottomBarContent = null,
            sheetContent = {
                EditWidgetGroupSheetContent(
                    params = EditWidgetSheetParams(
                        widgets = draft.widgets,
                        selected = draft.selected,
                        isIndicatorEnabled = params.isIndicatorEnabled
                    ),
                    onToggleIndicator = actions.onToggleIndicator,
                    onMove = { id, target ->
                        draft.moveTo(id, target)
                        actions.onReorderWidgets(draft.widgets)
                    }
                )
            }
        )
    )
}

/** The carousel of the stack, wired to the [draft]. */
@Composable
private fun StackPreview(
    params: EditWidgetGroupParams,
    actions: EditWidgetGroupActions,
    draft: WidgetStackDraft,
    scrollState: ScrollState
) {
    EditWidgetGroupPreviewCarousel(
        params = EditWidgetCarouselParams(
            currentWidgets = draft.widgets,
            appWidgetHost = params.appWidgetHost,
            scrollState = scrollState,
            isIndicatorEnabled = params.isIndicatorEnabled,
            onRemoveWidget = { widgetId ->
                draft.remove(widgetId)
                actions.onRemoveWidget(widgetId)
            },
            onAddWidgetClick = actions.onAddWidgetClick,
            selectedWidgetId = draft.selected,
            revealToken = draft.revealToken,
            onSelectWidget = draft::select,
            onMoveWidget = draft::drag,
            onMoveEnd = { if (draft.widgets != params.widgets) actions.onReorderWidgets(draft.widgets) }
        )
    )
}

/** The move row of the selected widget and the position indicator switch. */
@Composable
private fun EditWidgetGroupSheetContent(
    params: EditWidgetSheetParams,
    onToggleIndicator: () -> Unit,
    onMove: (id: Int, target: Int) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = AureoleDS.dimens.xLarge)) {
        params.selected?.let { id ->
            WidgetMoveRow(params = widgetMoveParams(params.widgets, id) { target -> onMove(id, target) })
        }
        AureoleSwitchRow(
            SwitchRowParams(
                title = stringResource(R.string.home_widget_show_indicator),
                checked = params.isIndicatorEnabled,
                onCheckedChange = { onToggleIndicator() },
                icon = { m, c -> AureoleDS.icons.Dots(modifier = m, tint = c) }
            )
        )
    }
}

/** What the move row shows for [id]; [onMove] receives the index the widget goes to. */
@Composable
private fun widgetMoveParams(widgets: List<Int>, id: Int, onMove: (Int) -> Unit): WidgetMoveParams {
    val name = rememberWidgetName(id)
    val before = widgetStepTarget(widgets, id, towardsStart = true)
    val after = widgetStepTarget(widgets, id, towardsStart = false)
    return WidgetMoveParams(
        name = name,
        position = widgets.indexOf(id) + 1,
        count = widgets.size,
        onPrevious = before?.let { { onMove(it) } },
        onNext = after?.let { { onMove(it) } }
    )
}

/** The label of the widget's provider, or the generic "Widget" when the system has no info for it. */
@Composable
private fun rememberWidgetName(widgetId: Int): String {
    val context = LocalContext.current
    val fallback = stringResource(R.string.home_widgets_default_title)
    return remember(widgetId, fallback) {
        runCatching {
            AppWidgetManager.getInstance(context).getAppWidgetInfo(widgetId)?.loadLabel(context.packageManager)
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: fallback
    }
}

@AureolePreview
@Composable
private fun EditWidgetGroupScreenPreview() {
    AureoleLauncherTheme {
        EditWidgetGroupScreen(
            params = EditWidgetGroupParams(
                item = LauncherItemState(id = "preview"),
                widgets = listOf(1, 2)
            ),
            actions = EditWidgetGroupActions(
                onToggleIndicator = {},
                onRemoveWidget = {},
                onAddWidgetClick = {},
                onDismissRequest = {}
            )
        )
    }
}
