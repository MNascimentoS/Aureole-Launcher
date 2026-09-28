package dev.mnascimentos.aureole.feature.home.widget

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.feature.home.widget.model.OpenedWidgetPopupConfig

private const val HAZE_MIN_ALPHA_POPUP = 0.25f
private const val HAZE_MAX_ALPHA_POPUP = 0.95f
private const val POPUP_HAZE_ALPHA_FACTOR = 0.8f

@Composable
fun OpenedWidgetPopup(
    config: OpenedWidgetPopupConfig,
    modifier: Modifier = Modifier
) {
    val hazeModifier = if (config.isHazeEnabled && (config.hazeState != null)) {
        Modifier.hazeEffect(
            state = config.hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = config.hazeOpacity))
            )
        ) {
            blurEnabled = config.isHazeEnabled
        }
    } else {
        Modifier
    }

    val popupAlpha = if (config.isHazeEnabled) {
        (config.hazeOpacity * POPUP_HAZE_ALPHA_FACTOR).coerceIn(HAZE_MIN_ALPHA_POPUP, HAZE_MAX_ALPHA_POPUP)
    } else {
        1f
    }

    Box(
        modifier = modifier
            .widthIn(min = 210.dp, max = 250.dp)
            .clip(RoundedCornerShape(18.dp))
            .then(hazeModifier)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = popupAlpha))
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OpenedWidgetPopupHeader(onDismiss = config.onDismiss)
            Spacer(modifier = Modifier.height(12.dp))
            OpenedWidgetPopupActions(
                onResizeClick = config.onResizeClick,
                onRemoveClick = config.onRemoveClick
            )
        }
    }
}

@Composable
private fun OpenedWidgetPopupHeader(onDismiss: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Widget Options",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun OpenedWidgetPopupActions(
    onResizeClick: () -> Unit,
    onRemoveClick: () -> Unit
) {
    Button(
        onClick = onResizeClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Edit,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Resize Widget",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    Button(
        onClick = onRemoveClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(40.dp),
        shape = RoundedCornerShape(10.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "Remove Widget",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}

@Composable
fun WidgetResizeDialog(
    currentHeightDp: Dp,
    onHeightSelected: (Dp) -> Unit,
    onDismiss: () -> Unit
) {
    val options = listOf(
        120.dp to "Compact (120 dp)",
        160.dp to "Standard (160 dp)",
        240.dp to "Large (240 dp)",
        360.dp to "Extra Large (360 dp)"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Resize Widget Row",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                options.forEach { (height, label) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { onHeightSelected(height) }
                            .padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (currentHeightDp == height),
                            onClick = { onHeightSelected(height) }
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
