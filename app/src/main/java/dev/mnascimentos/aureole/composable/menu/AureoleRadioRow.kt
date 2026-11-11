package dev.mnascimentos.aureole.composable.menu

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

private const val SELECTED_ROW_ALPHA = 0.5f

/**
 * One radio row (56 dp): optional [icon] at the start, the label and the marker at the end. Mirrored for
 * left-handed users, so the marker sits at the thumb side and the icon at the opposite side.
 */
@Composable
fun AureoleRadioRow(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: (@Composable () -> Unit)? = null
) {
    val rowColor = if (isSelected) {
        AureoleDS.colors.surfaceVariant.copy(alpha = SELECTED_ROW_ALPHA)
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = 0f)
    }
    // With an icon the label hugs it: at the end of the mirrored row, away from the marker (Start when right-handed).
    val labelAlign = if (icon != null && LocalLeftHanded.current) TextAlign.End else TextAlign.Start
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
            icon?.invoke()
            AureoleText(
                text = label,
                color = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyLarge.copy(textDirection = TextDirection.Content),
                    textAlign = labelAlign
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
