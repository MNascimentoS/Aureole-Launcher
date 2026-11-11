package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.actions.AureoleActionRow
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetCarouselParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupActions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.EditWidgetGroupParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 300

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditWidgetGroupScreen(
    params: EditWidgetGroupParams,
    actions: EditWidgetGroupActions,
    modifier: Modifier = Modifier
) {
    val widgets = params.widgets
    var isVisible by remember { mutableStateOf(true) }
    // Follows the stack while a card is dragged; resets to the saved order whenever it changes (an undo included).
    var currentWidgets by remember(widgets) { mutableStateOf(widgets) }
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
                AureolePreviewFrame(label = stringResource(R.string.home_edit_preview_live)) {
                    EditWidgetGroupPreviewCarousel(
                        params = EditWidgetCarouselParams(
                            currentWidgets = currentWidgets,
                            appWidgetHost = params.appWidgetHost,
                            scrollState = scrollState,
                            isIndicatorEnabled = params.isIndicatorEnabled,
                            onRemoveWidget = { widgetId ->
                                currentWidgets = currentWidgets - widgetId
                                actions.onRemoveWidget(widgetId)
                            },
                            onAddWidgetClick = actions.onAddWidgetClick,
                            onMoveWidget = { from, to -> currentWidgets = move(currentWidgets, from, to) },
                            onMoveEnd = { if (currentWidgets != widgets) actions.onReorderWidgets(currentWidgets) }
                        )
                    )
                }
            },
            bottomBarContent = {
                EditWidgetGroupBottomBar(
                    onSave = { isVisible = false },
                    onAddWidget = actions.onAddWidgetClick,
                    isLeftHanded = isLeftHanded
                )
            },
            sheetContent = {
                AureoleSwitchRow(
                    title = stringResource(R.string.home_widget_show_indicator),
                    checked = params.isIndicatorEnabled,
                    onCheckedChange = { actions.onToggleIndicator() }
                )
            }
        )
    )
}

/** Add widget (secondary) and Save (primary), with Save on the thumb side. */
@Composable
private fun EditWidgetGroupBottomBar(onSave: () -> Unit, onAddWidget: () -> Unit, isLeftHanded: Boolean) {
    AureoleActionRow(
        params = ActionRowParams(
            primaryLabel = stringResource(R.string.home_container_edit_save),
            onPrimary = onSave,
            secondaryLabel = stringResource(R.string.home_widget_add),
            onSecondary = onAddWidget
        ),
        isLeftHanded = isLeftHanded
    )
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
