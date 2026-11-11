package dev.mnascimentos.aureole.feature.home.components.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
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
                isLeftHanded = LocalHomeUiState.current.isLeftHandedMode
            ),
            previewContent = {
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
            },
            bottomBarContent = {
                Button(
                    onClick = { isVisible = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.home_container_edit_save))
                }
            },
            sheetContent = {
                IndicatorToggleRow(
                    isEnabled = params.isIndicatorEnabled,
                    onToggle = actions.onToggleIndicator
                )
            }
        )
    )
}

@Composable
private fun IndicatorToggleRow(isEnabled: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = "•••",
                color = AureoleDS.colors.onSurfaceMedium,
                style = AureoleDS.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
            Text(
                stringResource(R.string.home_widget_show_indicator),
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = { onToggle() }
        )
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
