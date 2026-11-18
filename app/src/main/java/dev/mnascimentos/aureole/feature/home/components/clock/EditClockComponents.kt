package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.runtime.Composable
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** Small caps section title ("STYLE", "ALIGNMENT"), always on the left like the other sheets. */
@Composable
internal fun EditClockSectionHeader(title: String) {
    AureoleMenuSectionLabel(text = title)
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

@AureolePreview
@Composable
private fun EditClockSectionHeaderPreview() {
    AureoleLauncherTheme {
        EditClockSectionHeader(title = "Estilo do Relógio")
    }
}
