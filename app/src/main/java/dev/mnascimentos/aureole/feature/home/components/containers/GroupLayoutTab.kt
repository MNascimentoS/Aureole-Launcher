package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.composable.controls.model.SegmentStyle
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.composable.optiontile.AureoleOptionTile
import dev.mnascimentos.aureole.composable.tips.TipPulse
import dev.mnascimentos.aureole.core.data.model.ContainerItemSpacing
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupAlignment
import dev.mnascimentos.aureole.feature.home.components.containers.model.alignmentIndexOf
import dev.mnascimentos.aureole.feature.home.components.containers.model.alignmentLabelRes
import dev.mnascimentos.aureole.feature.home.components.containers.model.isLyingDown
import dev.mnascimentos.aureole.feature.home.components.containers.model.orientationOf

/**
 * The Layout tab (RF-D1 to RF-D3): POSITION (standing on the side or lying down, as a dock), ALIGNMENT with labels
 * that follow the position, SPACE BETWEEN ITEMS, and "Fill the whole cell" (D-15). [isPositionPulsing] rings the
 * position tiles while tip 3 shows; [onPositionPicked] ends it.
 */
@Composable
internal fun ColumnScope.GroupLayoutTab(
    state: EditContainerState,
    isPositionPulsing: Boolean,
    onPositionPicked: () -> Unit
) {
    val lyingDown = isLyingDown(state.orientation)
    GroupSectionLabel(text = stringResource(R.string.home_container_group_position_label))
    TipPulse(isActive = isPositionPulsing) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
            listOf(false, true).forEach { option ->
                GroupPositionTile(
                    isLyingDown = option,
                    isSelected = option == lyingDown,
                    onClick = {
                        state.orientation = orientationOf(option)
                        onPositionPicked()
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
    GroupSectionLabel(
        text = stringResource(R.string.home_container_group_alignment_label),
        modifier = Modifier.padding(top = AureoleDS.dimens.medium)
    )
    GroupHelpText(text = stringResource(R.string.home_container_group_alignment_help))
    AureoleSegmentedControl(
        options = GroupAlignment.entries.map { stringResource(alignmentLabelRes(it, lyingDown)) },
        selectedIndex = alignmentIndexOf(state.position),
        onSelect = { state.position = GroupAlignment.entries[it].position },
        style = SegmentStyle(role = Role.RadioButton)
    )
    GroupSectionLabel(
        text = stringResource(R.string.home_container_group_spacing_label),
        modifier = Modifier.padding(top = AureoleDS.dimens.medium)
    )
    AureoleSegmentedControl(
        options = listOf(
            stringResource(R.string.home_container_group_spacing_tight),
            stringResource(R.string.home_container_group_spacing_normal),
            stringResource(R.string.home_container_group_spacing_wide)
        ),
        selectedIndex = ContainerItemSpacing.entries.indexOf(state.itemSpacing),
        onSelect = { state.itemSpacing = ContainerItemSpacing.entries[it] },
        style = SegmentStyle(role = Role.RadioButton)
    )
    AureoleSwitchRow(
        SwitchRowParams(
            title = stringResource(R.string.home_container_group_expand_cell),
            checked = state.isExpandCell,
            onCheckedChange = { state.isExpandCell = it }
        ),
        modifier = Modifier.padding(top = AureoleDS.dimens.xSmall)
    )
}

/** A line of help under a section label. */
@Composable
internal fun GroupHelpText(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
    )
}

/** A position tile: three squares standing or lying, the title and what it means (RF-D1). */
@Composable
private fun GroupPositionTile(isLyingDown: Boolean, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val title = stringResource(if (isLyingDown) R.string.home_container_group_lying else R.string.home_container_group_upright)
    val line = stringResource(
        if (isLyingDown) R.string.home_container_group_lying_line else R.string.home_container_group_upright_line
    )
    AureoleOptionTile(
        sample = {
            Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)) {
                GroupPositionMiniature(isLyingDown = isLyingDown)
                AureoleText(
                    text = title,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                )
            }
        },
        label = line,
        selected = isSelected,
        onClick = onClick,
        modifier = modifier
    )
}

/** Three small squares in a column (standing up) or in a row (lying down). */
@Composable
private fun GroupPositionMiniature(isLyingDown: Boolean) {
    val square: @Composable () -> Unit = {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconSmall)
                .background(AureoleDS.colors.onSurfaceMedium, RoundedCornerShape(AureoleDS.dimens.radiusMinimal))
        )
    }
    if (isLyingDown) {
        Row(horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal)) { repeat(MINIATURE_SQUARES) { square() } }
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal)) { repeat(MINIATURE_SQUARES) { square() } }
    }
}

private const val MINIATURE_SQUARES = 3
