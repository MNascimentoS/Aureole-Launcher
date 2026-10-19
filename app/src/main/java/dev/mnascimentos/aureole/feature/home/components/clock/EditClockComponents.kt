package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
internal fun EditClockSectionHeader(title: String) {
    AureoleText(
        text = title,
        color = AureoleDS.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.SemiBold)
    )
}

@Composable
internal fun EditClockLayoutChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { AureoleText(label) },
        shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = AureoleDS.colors.onSurfaceHigh,
            borderColor = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.3f),
            selectedBorderWidth = AureoleDS.dimens.borderMax,
            borderWidth = AureoleDS.dimens.borderMinimal
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = AureoleDS.colors.onSurfaceHigh,
            selectedLabelColor = AureoleDS.colors.surface,
            containerColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f),
            labelColor = AureoleDS.colors.onSurfaceHigh
        )
    )
}

@Composable
internal fun EditClockChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
            contentPadding = PaddingValues(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xSmall),
            colors = ButtonDefaults.buttonColors(
                containerColor = AureoleDS.colors.onSurfaceHigh,
                contentColor = AureoleDS.colors.surface
            )
        ) {
            AureoleText(
                text = text,
                color = AureoleDS.colors.surface,
                format = AureoleTextFormatParams(maxLines = 1, overflow = TextOverflow.Ellipsis)
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
            border = BorderStroke(AureoleDS.dimens.borderMinimal, AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xSmall),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f),
                contentColor = AureoleDS.colors.onSurfaceHigh
            )
        ) {
            AureoleText(
                text = text,
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(maxLines = 1, overflow = TextOverflow.Ellipsis)
            )
        }
    }
}

@Composable
internal fun EditClockColorSwatchItem(
    name: String,
    colorValue: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displayColor = if (colorValue == 0) AureoleDS.colors.surfaceVariant else Color(colorValue)
    val borderColor = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(AureoleDS.dimens.borderMax, borderColor, CircleShape)
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(AureoleDS.dimens.iconSmall)
                .clip(CircleShape)
                .background(displayColor)
                .border(AureoleDS.dimens.borderMinimal, Color.White.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
        AureoleText(
            text = name,
            color = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
    }
}

@AureolePreview
@Composable
private fun EditClockSectionHeaderPreview() {
    AureoleLauncherTheme {
        EditClockSectionHeader(title = "Estilo do Relógio")
    }
}
