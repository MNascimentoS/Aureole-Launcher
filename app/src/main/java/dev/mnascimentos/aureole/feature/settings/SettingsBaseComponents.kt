package dev.mnascimentos.aureole.feature.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.settings.model.PreferenceItemConfig

private const val ALPHA_DISABLED = 0.38f
private const val ALPHA_SEMI_TRANSPARENT = 0.5f

@Composable
fun PreferenceCategoryHeader(title: String) {
    Text(
        text = title.uppercase(),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(start = 8.dp, top = 4.dp, bottom = 2.dp)
    )
}

@Composable
fun PreferenceCard(
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = ALPHA_SEMI_TRANSPARENT)
        )
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            content()
        }
    }
}

@Composable
fun PreferenceRowItem(
    config: PreferenceItemConfig,
    trailingContent: (@Composable () -> Unit)? = null,
    onClick: () -> Unit
) {
    val titleColor = if (config.enabled) {
        MaterialTheme.colorScheme.onSurface
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
    }

    val subtitleColor = if (config.enabled) {
        MaterialTheme.colorScheme.onSurfaceVariant
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = ALPHA_DISABLED)
    }

    val iconTint = if (config.enabled) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = ALPHA_DISABLED)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = config.enabled, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (config.leadingIcon != null) {
            Icon(
                imageVector = config.leadingIcon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = config.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = titleColor
            )
            if (config.subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = config.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = subtitleColor
                )
            }
        }
        if (trailingContent != null) {
            Spacer(modifier = Modifier.width(12.dp))
            trailingContent()
        }
    }
}

@Composable
fun PreferenceSwitchRow(
    config: PreferenceItemConfig,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    PreferenceRowItem(
        config = config,
        trailingContent = {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                enabled = config.enabled
            )
        },
        onClick = { if (config.enabled) onCheckedChange(!checked) }
    )
}
