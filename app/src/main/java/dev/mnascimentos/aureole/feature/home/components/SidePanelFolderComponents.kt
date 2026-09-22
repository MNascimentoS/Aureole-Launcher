package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.folder.FolderIconRegistry

private const val MAX_PREVIEW_APPS = 9
private const val GRID_ROWS = 3
private const val GRID_COLS = 3
private const val FOLDER_BG_INACTIVE_ALPHA = 0.7f

data class SidePanelFolderButtonParams(
    val folder: AppFolder,
    val isGridFolderEnabled: Boolean,
    val containerColor: Color,
    val textColor: Color
)

@Composable
fun SidePanelAddFolderButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = FOLDER_BG_INACTIVE_ALPHA))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Criar Pasta",
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
fun SidePanelFolderItem(
    folder: AppFolder,
    isOpened: Boolean,
    showFolderLabels: Boolean,
    isGridFolderEnabled: Boolean,
    onFolderClick: (AppFolder, Float) -> Unit,
) {
    var itemYInWindow by remember { mutableFloatStateOf(0f) }

    val containerColor by animateColorAsState(
        targetValue = if (isOpened) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = FOLDER_BG_INACTIVE_ALPHA)
        },
        label = "folder_bg"
    )
    val textColor = if (isOpened) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .widthIn(max = 60.dp),
    ) {
        SidePanelFolderButton(
            params = SidePanelFolderButtonParams(
                folder = folder,
                isGridFolderEnabled = isGridFolderEnabled,
                containerColor = containerColor,
                textColor = textColor
            ),
            onPositionedY = { y -> itemYInWindow = y },
            onClick = { onFolderClick(folder, itemYInWindow) }
        )

        if (showFolderLabels) {
            SidePanelFolderLabel(name = folder.name)
        }
    }
}

@Composable
fun SidePanelFolderButton(
    params: SidePanelFolderButtonParams,
    onPositionedY: (Float) -> Unit,
    onClick: () -> Unit
) {
    val uiState = LocalHomeUiState.current
    val showGridPreview = params.isGridFolderEnabled || params.folder.displayAsGrid

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(params.containerColor)
            .onGloballyPositioned { coordinates ->
                onPositionedY(coordinates.positionInWindow().y)
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (showGridPreview) {
            FolderMiniGridPreview(
                folder = params.folder,
                allApps = uiState.apps
            )
        } else {
            val iconVector = FolderIconRegistry.getIcon(params.folder.icon)
            if (iconVector != null) {
                Icon(
                    imageVector = iconVector,
                    contentDescription = params.folder.name,
                    tint = params.textColor,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = params.folder.name.take(1).uppercase(),
                    color = params.textColor,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FolderMiniGridPreview(
    folder: AppFolder,
    allApps: List<AppInfo>,
    modifier: Modifier = Modifier
) {
    val folderApps = remember(folder.appPackageNames, allApps) {
        folder.appPackageNames.take(MAX_PREVIEW_APPS).mapNotNull { pkg ->
            allApps.find { it.packageName == pkg }
        }
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
            .padding(3.dp),
        contentAlignment = Alignment.Center
    ) {
        if (folderApps.isEmpty()) {
            EmptyFolderMiniGridPreview(folder = folder)
        } else {
            PopulatedFolderMiniGrid(folderApps = folderApps)
        }
    }
}

@Composable
private fun EmptyFolderMiniGridPreview(folder: AppFolder) {
    val iconVector = FolderIconRegistry.getIcon(folder.icon)
    if (iconVector != null) {
        Icon(
            imageVector = iconVector,
            contentDescription = folder.name,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
    } else {
        Text(
            text = folder.name.take(1).uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun PopulatedFolderMiniGrid(folderApps: List<AppInfo>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (row in 0 until GRID_ROWS) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (col in 0 until GRID_COLS) {
                    val index = row * GRID_COLS + col
                    if (index < folderApps.size) {
                        val app = folderApps[index]
                        val bitmap = remember(app.packageName) { app.getIconBitmap() }
                        Image(
                            bitmap = bitmap,
                            contentDescription = app.label,
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                        )
                    } else {
                        Spacer(modifier = Modifier.size(10.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun SidePanelFolderLabel(name: String) {
    Spacer(modifier = Modifier.height(2.dp))
    Text(
        text = name,
        color = MaterialTheme.colorScheme.onSurface,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Medium,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        textAlign = TextAlign.Center,
        modifier = Modifier.widthIn(max = 56.dp)
    )
}
