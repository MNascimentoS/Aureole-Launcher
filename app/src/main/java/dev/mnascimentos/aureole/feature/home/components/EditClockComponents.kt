package dev.mnascimentos.aureole.feature.home.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme

@Composable
internal fun EditClockSectionHeader(title: String) {
    AureoleText(
        text = title,
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = 4.dp)
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
        shape = RoundedCornerShape(16.dp),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
            borderColor = AureoleTheme.colors.onSurfaceMedium.copy(alpha = 0.3f),
            selectedBorderWidth = 1.5.dp,
            borderWidth = 1.dp
        ),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.6f),
            labelColor = AureoleTheme.colors.onSurfaceHigh
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
            shape = RoundedCornerShape(16.dp),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            AureoleText(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AureoleTheme.colors.onSurfaceMedium.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = AureoleTheme.colors.surfaceVariant.copy(alpha = 0.6f),
                contentColor = AureoleTheme.colors.onSurfaceHigh
            )
        ) {
            AureoleText(
                text = text,
                color = AureoleTheme.colors.onSurfaceMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
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
    val displayColor = if (colorValue == 0) MaterialTheme.colorScheme.surfaceVariant else Color(colorValue)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(displayColor)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        AureoleText(
            text = name,
            style = AureoleTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else AureoleTheme.colors.onSurfaceMedium
        )
    }
}
