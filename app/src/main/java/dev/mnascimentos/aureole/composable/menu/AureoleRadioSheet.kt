package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextDirection
import dev.mnascimentos.aureole.composable.menu.model.RadioOption
import dev.mnascimentos.aureole.composable.menu.model.RadioSheetParams
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val SELECTED_ROW_ALPHA = 0.5f

/**
 * Menu sheet with one radio row per option (56 dp each). The marker sits at the thumb side: the right end for
 * right-handed users and the left end for left-handed users. Picking a row calls [onSelect] and closes the sheet.
 */
@Composable
fun AureoleRadioSheet(
    params: RadioSheetParams,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AureoleMenuSheet(title = params.title, onDismiss = onDismiss, modifier = modifier) {
        params.options.forEach { option ->
            AureoleRadioRow(
                label = option.label,
                isSelected = option.value == params.selectedValue,
                onClick = {
                    onSelect(option.value)
                    onDismiss()
                }
            )
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@Composable
private fun AureoleRadioRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val rowColor = if (isSelected) {
        AureoleDS.colors.surfaceVariant.copy(alpha = SELECTED_ROW_ALPHA)
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = 0f)
    }
    LeftHandMirror {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .heightIn(min = AureoleDS.dimens.menuRowHeight)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .background(rowColor)
                .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
                .padding(horizontal = AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            AureoleText(
                text = label,
                color = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyLarge.copy(textDirection = TextDirection.Content)
                )
            )
            RadioMarker(isSelected = isSelected)
        }
    }
}

@Composable
private fun RadioMarker(isSelected: Boolean) {
    val base = Modifier
        .size(AureoleDS.dimens.iconLarge)
        .clip(CircleShape)
    if (isSelected) {
        Box(
            modifier = base.background(AureoleDS.colors.onSurfaceHigh),
            contentAlignment = Alignment.Center
        ) {
            AureoleDS.icons.Check(
                tint = AureoleDS.colors.surface,
                modifier = Modifier.size(AureoleDS.dimens.iconSmall)
            )
        }
    } else {
        Box(
            modifier = base.border(
                BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.onSurfaceLow),
                CircleShape
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleRadioSheetPreview() {
    AureoleLauncherTheme {
        AureoleRadioSheet(
            params = RadioSheetParams(
                title = "Settings Button Position",
                options = listOf(RadioOption("Left", "Left"), RadioOption("Right", "Right")),
                selectedValue = "Right"
            ),
            onSelect = {},
            onDismiss = {}
        )
    }
}
