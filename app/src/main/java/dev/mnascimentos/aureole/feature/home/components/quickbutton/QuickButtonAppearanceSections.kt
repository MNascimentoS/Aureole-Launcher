package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorSwatches
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonIcon
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Stroke
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QUICK_BUTTON_ICON_VISIBLE_ROWS
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorTarget
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.colorOf
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.withColor

/**
 * Body of the quick button appearance editor: the icon grid and the color swatches are inline (no picker opens),
 * and every tap updates the draft through [onUpdateAppearance]. The stroke color only shows with the stroke on.
 */
@Composable
internal fun QuickButtonAppearanceSheetContent(
    activeModel: QuickButtonModel,
    onUpdateAppearance: (QuickButtonAppearance) -> Unit
) {
    val appearance = activeModel.appearance
    Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
        AureoleMenuSectionLabel(text = stringResource(R.string.home_quickbutton_icon))
        LeftHandMirror {
            QuickButtonIconGrid(
                selectedIcon = appearance.icon,
                onIconSelected = { onUpdateAppearance(appearance.copy(icon = it)) }
            )
        }
        ColorSection(
            labelRes = R.string.home_quickbutton_icon_color,
            target = QuickButtonColorTarget.ICON,
            appearance = appearance,
            onUpdateAppearance = onUpdateAppearance
        )
        ColorSection(
            labelRes = R.string.home_quickbutton_bg_color,
            target = QuickButtonColorTarget.BACKGROUND,
            appearance = appearance,
            onUpdateAppearance = onUpdateAppearance
        )
        AppearanceToggleRow(
            title = stringResource(R.string.home_quickbutton_stroke),
            icon = { m -> AureoleDS.icons.Stroke(m) },
            isChecked = appearance.strokeEnabled,
            onToggle = { onUpdateAppearance(appearance.copy(strokeEnabled = !appearance.strokeEnabled)) }
        )
        if (appearance.strokeEnabled) {
            ColorSection(
                labelRes = R.string.home_quickbutton_stroke_color,
                target = QuickButtonColorTarget.STROKE,
                appearance = appearance,
                onUpdateAppearance = onUpdateAppearance
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@Composable
private fun ColorSection(
    labelRes: Int,
    target: QuickButtonColorTarget,
    appearance: QuickButtonAppearance,
    onUpdateAppearance: (QuickButtonAppearance) -> Unit
) {
    AureoleMenuSectionLabel(text = stringResource(labelRes))
    LeftHandMirror {
        AureoleColorSwatches(
            colors = RecommendedColors,
            selected = appearance.colorOf(target),
            onSelect = { onUpdateAppearance(appearance.withColor(target, it)) },
            modifier = Modifier.padding(horizontal = AureoleDS.dimens.xxSmall)
        )
    }
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

/** Every quick button icon, inline: [QUICK_BUTTON_ICON_VISIBLE_ROWS] rows visible, scrolling on its own beyond that. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun QuickButtonIconGrid(
    selectedIcon: QuickButtonIcon,
    onIconSelected: (QuickButtonIcon) -> Unit,
    modifier: Modifier = Modifier
) {
    val tile = AureoleDS.dimens.xxLarge
    val gap = AureoleDS.dimens.small
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = tile * QUICK_BUTTON_ICON_VISIBLE_ROWS + gap * 2)
            .verticalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(gap, Alignment.CenterHorizontally),
        verticalArrangement = Arrangement.spacedBy(gap)
    ) {
        QuickButtonIcon.entries.forEach { iconItem ->
            val isSelected = iconItem == selectedIcon
            val shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
            Box(
                modifier = Modifier
                    .size(tile)
                    .clip(shape)
                    .background(AureoleDS.colors.surfaceVariant)
                    .border(
                        width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.zero,
                        color = AureoleDS.colors.onSurfaceHigh,
                        shape = shape
                    )
                    .clickable(role = Role.RadioButton) { onIconSelected(iconItem) }
                    .semantics { this.selected = isSelected },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = iconItem.iconRes),
                    contentDescription = iconItem.name,
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }
        }
    }
}

@AureolePreview
@Composable
private fun QuickButtonAppearanceSheetContentPreview() {
    AureoleLauncherTheme {
        QuickButtonAppearanceSheetContent(
            activeModel = QuickButtonModel(appearance = QuickButtonAppearance(strokeEnabled = true)),
            onUpdateAppearance = {}
        )
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
