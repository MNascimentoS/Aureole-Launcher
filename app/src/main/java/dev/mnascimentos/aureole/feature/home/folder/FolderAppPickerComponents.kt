package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.*
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.folder.model.FolderAppRowItemParams

@Composable
fun FolderAppPickerHeader(
    folderName: String,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = "Apps na Pasta: $folderName",
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        IconButton(onClick = onDismiss) {
            AureoleDS.icons.Close(
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FolderAppPickerFooter(
    onDismiss: () -> Unit,
    onSave: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedButton(onClick = onDismiss) {
            AureoleText("Cancelar")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = onSave) {
            AureoleText("Salvar")
        }
    }
}

@Composable
fun SelectedFolderAppRowActionButtons(
    params: FolderAppRowItemParams,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleSelect: () -> Unit
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (params.canReorder) {
            val upTint = if (params.index > 0) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
            IconButton(
                onClick = onMoveUp,
                enabled = params.index > 0,
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.ArrowUp(
                    tint = upTint
                )
            }

            val downTint = if (params.index < params.totalCount - 1) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }
            IconButton(
                onClick = onMoveDown,
                enabled = params.index < params.totalCount - 1,
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.ArrowDown(
                    tint = downTint
                )
            }
        }

        IconButton(
            onClick = onToggleSelect,
            modifier = Modifier.size(32.dp)
        ) {
            AureoleDS.icons.Delete(
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun RemainingFolderAppRow(
    app: AppInfo,
    onToggleSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
            )
            Spacer(modifier = Modifier.width(8.dp))
            AureoleText(
                text = app.label,
                style = AureoleDS.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        IconButton(
            onClick = onToggleSelect,
            modifier = Modifier.size(32.dp)
        ) {
            AureoleDS.icons.Add(
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
