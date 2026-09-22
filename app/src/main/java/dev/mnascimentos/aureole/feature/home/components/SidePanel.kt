package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
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
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.SidePanelConfig
import dev.mnascimentos.aureole.feature.home.folder.FolderIconRegistry
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val SIDE_PANEL_HAZE_ALPHA_MULTIPLIER = 0.7f
private const val SIDE_PANEL_MIN_ALPHA = 0.2f
private const val SIDE_PANEL_MAX_ALPHA = 0.95f
private const val FOLDER_BG_INACTIVE_ALPHA = 0.7f

private fun Modifier.hazeModifier(
    hasHaze: Boolean,
    hazeState: HazeState?,
    hazeOpacity: Float,
    surfaceColor: Color
): Modifier {
    return if (hasHaze && hazeState != null) {
        this.then(
            Modifier.hazeEffect(
                state = hazeState,
                style = HazeStyle(
                    blurRadius = 20.dp,
                    tint = HazeTint(surfaceColor.copy(alpha = hazeOpacity))
                )
            ) {
                blurEnabled = true
            }
        )
    } else {
        this
    }
}

private fun getBackgroundColor(
    isBgEnabled: Boolean,
    hasHaze: Boolean,
    hazeOpacity: Float,
    surfaceColor: Color
): Color {
    return if (isBgEnabled) {
        if (hasHaze) {
            val backgroundAlpha = (hazeOpacity * SIDE_PANEL_HAZE_ALPHA_MULTIPLIER)
                .coerceIn(SIDE_PANEL_MIN_ALPHA, SIDE_PANEL_MAX_ALPHA)
            surfaceColor.copy(alpha = backgroundAlpha)
        } else {
            surfaceColor.copy(alpha = 0.95f)
        }
    } else {
        Color.Transparent
    }
}

private fun getVerticalArrangement(position: String): Arrangement.Vertical {
    return when (position) {
        "Top" -> Arrangement.Top
        "Center" -> Arrangement.Center
        "Bottom" -> Arrangement.Bottom
        "Space Evenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.SpaceBetween
    }
}

@Composable
fun SidePanel(
    config: SidePanelConfig,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = LocalHomeUiState.current
    val surfaceColor = MaterialTheme.colorScheme.surfaceContainerHigh
    val hasHaze = config.isBackgroundEnabled && uiState.isHazeEnabled && (config.hazeState != null)

    val hazeModifier = Modifier.hazeModifier(hasHaze, config.hazeState, uiState.hazeOpacity, surfaceColor)
    val backgroundColor = getBackgroundColor(config.isBackgroundEnabled, hasHaze, uiState.hazeOpacity, surfaceColor)
    val verticalArrangement = getVerticalArrangement(config.position)

    val boxModifier = modifier.then(if (config.isExpandCell) Modifier.fillMaxSize() else Modifier)

    Box(
        modifier = boxModifier
    ) {
        SidePanelColumn(
            config = config,
            backgroundColor = backgroundColor,
            verticalArrangement = verticalArrangement,
            onFolderClick = onFolderClick,
            modifier = if (config.isBackgroundEnabled) hazeModifier else Modifier
        )
    }
}

@Composable
private fun SidePanelColumn(
    config: SidePanelConfig,
    backgroundColor: Color,
    verticalArrangement: Arrangement.Vertical,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val actions = LocalHomeActions.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = verticalArrangement,
        modifier = Modifier
            .clip(RoundedCornerShape(18.dp))
            .then(if (config.isExpandCell) Modifier.fillMaxSize() else Modifier)
            .then(modifier)
            .background(backgroundColor)
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        if (config.folders.isEmpty()) {
            if (config.showAddFolderButton) {
                SidePanelAddFolderButton(
                    onClick = { actions.onFolderIntent(FolderViewIntent.OpenCreateFolderDialog) }
                )
            }
        } else {
            config.folders.forEach { folder ->
                SidePanelFolderItem(
                    folder = folder,
                    isOpened = folder.id == config.openedFolderId,
                    showFolderLabels = config.showFolderLabels,
                    onFolderClick = onFolderClick
                )
            }
            if (config.showAddFolderButton) {
                Spacer(modifier = Modifier.height(4.dp))
                SidePanelAddFolderButton(
                    onClick = { actions.onFolderIntent(FolderViewIntent.OpenCreateFolderDialog) }
                )
            }
        }
    }
}

@Composable
private fun SidePanelAddFolderButton(
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

@AureolePreview
@Composable
fun SidePanelPreview() {
    val mockFolder = AppFolder(id = "1", name = "Social")
    val config = SidePanelConfig(
        folders = listOf(mockFolder),
        openedFolderId = null,
        showFolderLabels = true
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            SidePanel(
                config = config,
                onFolderClick = { _, _ -> }
            )
        }
    }
}
