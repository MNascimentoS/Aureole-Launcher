package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

private const val CONTAINER_HAZE_ALPHA_MULTIPLIER = 0.7f
private const val CONTAINER_MIN_ALPHA = 0.2f
private const val CONTAINER_MAX_ALPHA = 0.95f
private const val CONTAINER_DEFAULT_ALPHA = 0.95f

@Composable
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
            val backgroundAlpha = (hazeOpacity * CONTAINER_HAZE_ALPHA_MULTIPLIER)
                .coerceIn(CONTAINER_MIN_ALPHA, CONTAINER_MAX_ALPHA)
            surfaceColor.copy(alpha = backgroundAlpha)
        } else {
            surfaceColor.copy(alpha = CONTAINER_DEFAULT_ALPHA)
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

private fun getHorizontalArrangement(position: String): Arrangement.Horizontal {
    return when (position) {
        "Top", "Left" -> Arrangement.Start
        "Center" -> Arrangement.Center
        "Bottom", "Right" -> Arrangement.End
        "Space Evenly" -> Arrangement.SpaceEvenly
        else -> Arrangement.SpaceBetween
    }
}

@Composable
fun Container(
    config: ContainerConfig,
    onFolderClick: (AppFolder, Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState = LocalHomeUiState.current
    val surfaceColor = AureoleDS.colors.surface
    val hasHaze = config.isBackgroundEnabled && uiState.isHazeEnabled && (config.hazeState != null)

    val hazeModifier = Modifier.hazeModifier(hasHaze, config.hazeState, uiState.hazeOpacity, surfaceColor)
    val backgroundColor = getBackgroundColor(config.isBackgroundEnabled, hasHaze, uiState.hazeOpacity, surfaceColor)
    val shouldExpand = config.isExpandCell

    val boxModifier = modifier.then(if (shouldExpand) Modifier.fillMaxSize() else Modifier)

    Box(
        modifier = boxModifier
    ) {
        if (config.orientation.equals("Horizontal", ignoreCase = true)) {
            val horizontalArrangement = getHorizontalArrangement(config.position)
            ContainerRow(
                config = config,
                rowConfig = ContainerRowConfig(
                    backgroundColor = backgroundColor,
                    horizontalArrangement = horizontalArrangement,
                    onFolderClick = onFolderClick,
                    shouldExpand = shouldExpand
                ),
                modifier = if (config.isBackgroundEnabled) hazeModifier else Modifier
            )
        } else {
            val verticalArrangement = getVerticalArrangement(config.position)
            ContainerColumn(
                config = config,
                columnConfig = ContainerColumnConfig(
                    backgroundColor = backgroundColor,
                    verticalArrangement = verticalArrangement,
                    onFolderClick = onFolderClick,
                    shouldExpand = shouldExpand
                ),
                modifier = if (config.isBackgroundEnabled) hazeModifier else Modifier
            )
        }
    }
}

@AureolePreview
@Composable
private fun ContainerPreview() {
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Container(
                config = ContainerConfig(
                    panelId = "preview_container",
                    position = "Center",
                    orientation = "Horizontal",
                    isBackgroundEnabled = true
                ),
                onFolderClick = { _, _ -> }
            )
        }
    }
}
