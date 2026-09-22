package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import dev.mnascimentos.aureole.feature.home.model.FolderViewIntent
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val SIDE_PANEL_HAZE_ALPHA_MULTIPLIER = 0.7f
private const val SIDE_PANEL_MIN_ALPHA = 0.2f
private const val SIDE_PANEL_MAX_ALPHA = 0.95f
private const val SIDE_PANEL_DEFAULT_ALPHA = 0.95f

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
            surfaceColor.copy(alpha = SIDE_PANEL_DEFAULT_ALPHA)
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

@OptIn(ExperimentalFoundationApi::class)
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
            .combinedClickable(
                onClick = {},
                onLongClick = { actions.onOpenEditSidePanelDialog(config.panelId) }
            )
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
                    isGridFolderEnabled = config.isGridFolderEnabled,
                    onFolderClick = { f, y ->
                        val updatedFolder = f.copy(
                            panelId = config.panelId,
                            displayAsGrid = config.isGridFolderEnabled || f.displayAsGrid
                        )
                        onFolderClick(updatedFolder, y)
                    }
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
