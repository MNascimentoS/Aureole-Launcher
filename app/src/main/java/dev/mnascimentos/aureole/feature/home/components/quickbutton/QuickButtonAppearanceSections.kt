package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleThemeColorPicker
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.icons.AureoleIconCatalogGrid
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Stroke
import dev.mnascimentos.aureole.core.designsystem.icons.Text
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QUICK_BUTTON_COLOR_TARGETS
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorTarget
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonSheetTab
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.colorOf
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.quickButtonColorTargetAt
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.quickButtonColorTargetIndex
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.quickButtonSheetTabAt
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.quickButtonSheetTabIndex
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.withColor

/**
 * Body of the quick button sheet (botao-rapido RF-03): a segmented Icon | Colors on top and, under it, one tab with
 * its own single scroll. Icon is the shared catalog grid (RF-04); Colors holds "Icon color | Background color" with
 * the shared theme color picker (RF-05), the stroke and "Show name on the home" (RF-02). Every choice updates the
 * draft through [onUpdateAppearance]. Give it a bounded height (the sheet does), since the grid scrolls by itself.
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
        AureoleSegmentedControl(
            options = listOf(
                stringResource(R.string.home_quickbutton_tab_icon),
                stringResource(R.string.home_quickbutton_tab_colors)
            ),
            selectedIndex = tabIndex,
            onSelect = { tabIndex = it },
            modifier = Modifier.semantics { contentDescription = tabsLabel }
        )
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
    }
}

@Composable
private fun ColumnScope.QuickButtonColorsTab(
    appearance: QuickButtonAppearance,
    onUpdateAppearance: (QuickButtonAppearance) -> Unit
) {
    var targetIndex by rememberSaveable { mutableIntStateOf(0) }
    val target = quickButtonColorTargetAt(targetIndex)
    val targetLabel = stringResource(R.string.home_quickbutton_color_target_label)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f, fill = false)
            .verticalScroll(rememberScrollState())
            .padding(bottom = AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        AureoleSegmentedControl(
            options = QUICK_BUTTON_COLOR_TARGETS.map { stringResource(colorTargetLabelRes(it)) },
            selectedIndex = quickButtonColorTargetIndex(target),
            onSelect = { targetIndex = it },
            modifier = Modifier.semantics { contentDescription = targetLabel }
        )
        AureoleThemeColorPicker(
            selected = appearance.colorOf(target),
            onSelect = { onUpdateAppearance(appearance.withColor(target, it)) }
        )
        AppearanceToggleRow(
            title = stringResource(R.string.home_quickbutton_stroke),
            icon = { m -> AureoleDS.icons.Stroke(m) },
            isChecked = appearance.strokeEnabled,
            onToggle = { onUpdateAppearance(appearance.copy(strokeEnabled = !appearance.strokeEnabled)) }
        )
        if (appearance.strokeEnabled) {
            AureoleMenuSectionLabel(text = stringResource(R.string.home_quickbutton_stroke_color))
            AureoleThemeColorPicker(
                selected = appearance.colorOf(QuickButtonColorTarget.STROKE),
                onSelect = { onUpdateAppearance(appearance.withColor(QuickButtonColorTarget.STROKE, it)) }
            )
        }
        AppearanceToggleRow(
            title = stringResource(R.string.home_quickbutton_show_name),
            icon = { m -> AureoleDS.icons.Text(m) },
            isChecked = appearance.showLabel,
            onToggle = { onUpdateAppearance(appearance.copy(showLabel = !appearance.showLabel)) }
        )
    }
}

private fun colorTargetLabelRes(target: QuickButtonColorTarget): Int = when (target) {
    QuickButtonColorTarget.ICON -> R.string.home_quickbutton_icon_color
    else -> R.string.home_quickbutton_bg_color
}

@Composable
internal fun AppearanceToggleRow(
    title: String,
    icon: @Composable (Modifier) -> Unit,
    isChecked: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    LeftHandMirror {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .clickable { onToggle() }
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            icon(Modifier.size(AureoleDS.dimens.iconMedium))
            Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
            AureoleText(
                text = title,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                ),
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconLarge)
                    .border(
                        width = AureoleDS.dimens.minimal,
                        color = if (isChecked) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceLow,
                        shape = RoundedCornerShape(AureoleDS.dimens.radiusMinimal)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isChecked) {
                    Box(
                        modifier = Modifier.size(
                            AureoleDS.dimens.iconSmallCompact
                        ).background(AureoleDS.colors.onSurfaceHigh, RoundedCornerShape(AureoleDS.dimens.minimal))
                    )
                }
            }
        }
    }
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

@AureolePreview
@Composable
private fun AppearanceToggleRowPreview() {
    AureoleLauncherTheme {
        Column {
            AppearanceToggleRow(
                title = "Preview Toggle Row Checked",
                icon = { m -> AureoleDS.icons.Stroke(m) },
                isChecked = true,
                onToggle = {},
            )
            AppearanceToggleRow(
                title = "Preview Toggle Row Unchecked",
                icon = { m -> AureoleDS.icons.Stroke(m) },
                isChecked = false,
                onToggle = {},
            )
        }
    }
}
