package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorField
import dev.mnascimentos.aureole.composable.color.model.ColorFieldParams
import dev.mnascimentos.aureole.composable.controls.AureoleSheetTabBar
import dev.mnascimentos.aureole.composable.icons.AureoleIconCatalogGrid
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.icons.Stroke
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorTarget
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonSheetTab
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.colorOf
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.quickButtonSheetTabAt
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.quickButtonSheetTabIndex
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.withColor

/**
 * Body of the quick button sheet (botao-rapido RF-03): one tab with its own single scroll and, fixed under it, the
 * DS tab bar Icon | Colors (RF-E1). Icon is the shared catalog grid (RF-04); Colors holds
 * "Icon color | Background color" with the shared theme color picker (RF-05), the stroke and "Show name on the
 * home" (RF-02). Every choice updates the draft through [onUpdateAppearance]. Give it a bounded height (the sheet
 * does), since the grid scrolls by itself.
 */
@Composable
internal fun QuickButtonAppearanceSheetContent(
    activeModel: QuickButtonModel,
    onUpdateAppearance: (QuickButtonAppearance) -> Unit,
    modifier: Modifier = Modifier,
    initialTab: QuickButtonSheetTab = QuickButtonSheetTab.ICON
) {
    var tabIndex by rememberSaveable { mutableIntStateOf(quickButtonSheetTabIndex(initialTab)) }
    val tabsLabel = stringResource(R.string.home_quickbutton_tabs_label)
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        when (quickButtonSheetTabAt(tabIndex)) {
            QuickButtonSheetTab.ICON -> AureoleIconCatalogGrid(
                selectedKey = activeModel.appearance.icon,
                onSelect = { onUpdateAppearance(activeModel.appearance.copy(icon = it)) },
                modifier = Modifier.weight(1f, fill = false)
            )
            QuickButtonSheetTab.COLORS -> QuickButtonColorsTab(
                appearance = activeModel.appearance,
                onUpdateAppearance = onUpdateAppearance
            )
        }
        AureoleSheetTabBar(
            labels = listOf(
                stringResource(R.string.home_quickbutton_tab_icon),
                stringResource(R.string.home_quickbutton_tab_colors)
            ),
            selectedIndex = tabIndex,
            description = tabsLabel,
            onSelect = { tabIndex = it }
        )
    }
}

@Composable
private fun ColumnScope.QuickButtonColorsTab(
    appearance: QuickButtonAppearance,
    onUpdateAppearance: (QuickButtonAppearance) -> Unit
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .fadingEdges(scrollState, AureoleDS.dimens.fadingEdge)
            .verticalScroll(scrollState)
            .padding(bottom = AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        QuickButtonColorTarget.entries.filter { it != QuickButtonColorTarget.STROKE }.forEach { target ->
            QuickButtonColorField(
                title = stringResource(colorTargetLabelRes(target)),
                target = target,
                appearance = appearance,
                onUpdateAppearance = onUpdateAppearance
            )
        }
        AureoleSwitchRow(
            SwitchRowParams(
                title = stringResource(R.string.home_quickbutton_stroke),
                checked = appearance.strokeEnabled,
                onCheckedChange = { onUpdateAppearance(appearance.copy(strokeEnabled = it)) },
                icon = { m, tint -> AureoleDS.icons.Stroke(m, tint = tint) }
            )
        )
        if (appearance.strokeEnabled) {
            QuickButtonColorField(
                title = stringResource(R.string.home_quickbutton_stroke_color),
                target = QuickButtonColorTarget.STROKE,
                appearance = appearance,
                onUpdateAppearance = onUpdateAppearance
            )
        }
        AureoleSwitchRow(
            SwitchRowParams(
                title = stringResource(R.string.home_quickbutton_show_name),
                checked = appearance.showLabel,
                onCheckedChange = { onUpdateAppearance(appearance.copy(showLabel = it)) },
                icon = { m, tint -> AureoleDS.icons.Text(m, tint = tint) }
            )
        )
    }
}

/** One color of the quick button as the shared color row; "Theme" goes back to that color's default. */
@Composable
private fun QuickButtonColorField(
    title: String,
    target: QuickButtonColorTarget,
    appearance: QuickButtonAppearance,
    onUpdateAppearance: (QuickButtonAppearance) -> Unit
) {
    val default = QuickButtonAppearance().colorOf(target)
    AureoleColorField(
        params = ColorFieldParams(
            title = title,
            selected = appearance.colorOf(target),
            themeColor = default.resolve(AureoleDS.colors),
            role = if (target == QuickButtonColorTarget.ICON) ComponentColorRole.TEXT else ComponentColorRole.CARD,
            onSelect = { onUpdateAppearance(appearance.withColor(target, it ?: default)) }
        )
    )
}

private fun colorTargetLabelRes(target: QuickButtonColorTarget): Int = when (target) {
    QuickButtonColorTarget.ICON -> R.string.home_quickbutton_icon_color
    else -> R.string.home_quickbutton_bg_color
}

@AureolePreview
@Composable
private fun QuickButtonAppearanceSheetContentPreview() {
    AureoleLauncherTheme {
        Column {
            QuickButtonAppearanceSheetContent(
                activeModel = QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = true)),
                onUpdateAppearance = {},
                initialTab = QuickButtonSheetTab.COLORS
            )
        }
    }
}
