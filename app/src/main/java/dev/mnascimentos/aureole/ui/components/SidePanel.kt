package dev.mnascimentos.aureole.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.data.model.AppFolder

@Composable
fun SidePanel(
    config: SidePanelConfig,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .padding(
                start = if (config.isLeftHandedMode) 16.dp else 8.dp,
                end = if (config.isLeftHandedMode) 8.dp else 16.dp,
                top = 8.dp,
                bottom = 8.dp
            )
            .windowInsetsPadding(
                when (config.position) {
                    "Top" -> WindowInsets.statusBars
                    "Bottom" -> WindowInsets.navigationBars
                    else -> WindowInsets(0, 0, 0, 0)
                }
            )
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f))
                .padding(8.dp)
        ) {
            config.folders.forEach { folder ->
                SidePanelFolderItem(
                    folder = folder,
                    isOpened = folder.id == config.openedFolderId,
                    onFolderClick = onFolderClick
                )
            }
        }
    }
}

data class SidePanelConfig(
    val folders: List<AppFolder>,
    val openedFolderId: String?,
    val isLeftHandedMode: Boolean,
    val position: String = "Center"
)

// Convenience overload
@Suppress("LongParameterList")
@Composable
fun SidePanel(
    folders: List<AppFolder>,
    openedFolderId: String?,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier,
    position: String = "Center",
    onFolderClick: (AppFolder, Float) -> Unit
) {
    SidePanel(
        config = SidePanelConfig(
            folders = folders,
            openedFolderId = openedFolderId,
            isLeftHandedMode = isLeftHandedMode,
            position = position
        ),
        onFolderClick = onFolderClick,
        modifier = modifier
    )
}

@Composable
private fun SidePanelFolderItem(
    folder: AppFolder,
    isOpened: Boolean,
    onFolderClick: (AppFolder, Float) -> Unit
) {
    var itemYInWindow by remember { mutableFloatStateOf(0f) }

    val containerColor by animateColorAsState(
        targetValue = if (isOpened) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
        },
        label = "folder_bg"
    )
    val textColor = if (isOpened) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = Modifier
            .padding(vertical = 4.dp)
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .onGloballyPositioned { coordinates ->
                itemYInWindow = coordinates.positionInWindow().y
            }
            .clickable { onFolderClick(folder, itemYInWindow) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = folder.name.take(1).uppercase(),
            color = textColor,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
