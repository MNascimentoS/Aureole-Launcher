package dev.mnascimentos.aureole.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.data.model.AppFolder

@Suppress("MagicNumber")
@Composable
fun SidePanel(
    config: SidePanelConfig,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val hazeModifier = if (config.isHazeEnabled && (config.hazeState != null)) {
        Modifier.hazeEffect(
            state = config.hazeState,
            style = HazeStyle(
                blurRadius = 20.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = config.hazeOpacity))
            )
        ) {
            blurEnabled = config.isHazeEnabled
        }
    } else {
        Modifier
    }

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
                .then(hazeModifier)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                        alpha = if (config.isHazeEnabled) (config.hazeOpacity * 0.7f).coerceIn(0.2f, 0.95f) else 0.85f
                    )
                )
                .padding(8.dp)
        ) {
            config.folders.forEach { folder ->
                SidePanelFolderItem(
                    folder = folder,
                    isOpened = folder.id == config.openedFolderId,
                    showFolderLabels = config.showFolderLabels,
                    onFolderClick = onFolderClick
                )
            }
        }
    }
}

@Suppress("LongParameterList")
data class SidePanelConfig(
    val folders: List<AppFolder>,
    val openedFolderId: String?,
    val isLeftHandedMode: Boolean,
    val position: String = "Center",
    val showFolderLabels: Boolean = false,
    val hazeState: HazeState? = null,
    val isHazeEnabled: Boolean = false,
    val hazeOpacity: Float = 0.5f,
)

@Composable
private fun SidePanelFolderItem(
    folder: AppFolder,
    isOpened: Boolean,
    showFolderLabels: Boolean,
    onFolderClick: (AppFolder, Float) -> Unit,
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

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(vertical = 4.dp)
            .widthIn(max = 60.dp),
    ) {
        SidePanelFolderButton(
            folder = folder,
            containerColor = containerColor,
            textColor = textColor,
            onPositionedY = { y -> itemYInWindow = y },
            onClick = { onFolderClick(folder, itemYInWindow) }
        )

        if (showFolderLabels) {
            SidePanelFolderLabel(name = folder.name)
        }
    }
}

@Composable
private fun SidePanelFolderButton(
    folder: AppFolder,
    containerColor: Color,
    textColor: Color,
    onPositionedY: (Float) -> Unit,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(containerColor)
            .onGloballyPositioned { coordinates ->
                onPositionedY(coordinates.positionInWindow().y)
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        val iconVector = FolderIconRegistry.getIcon(folder.icon)
        if (iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = folder.name,
                tint = textColor,
                modifier = Modifier.size(24.dp)
            )
        } else {
            Text(
                text = folder.name.take(1).uppercase(),
                color = textColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SidePanelFolderLabel(name: String) {
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
