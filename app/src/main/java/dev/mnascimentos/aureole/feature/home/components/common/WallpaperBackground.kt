package dev.mnascimentos.aureole.feature.home.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import java.io.File

@Composable
fun WallpaperBackground(
    isCustomWallpaperSet: Boolean,
    customWallpaperPath: String?,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    wallpaperScaleType: String = "Crop"
) {
    val hazeModifier = Modifier.hazeSource(state = hazeState)

    val contentScale = when (wallpaperScaleType) {
        "Fit" -> ContentScale.Fit
        "Fill" -> ContentScale.FillBounds
        else -> ContentScale.Crop
    }

    Box(modifier = modifier.fillMaxSize().then(hazeModifier)) {
        if (isCustomWallpaperSet && !customWallpaperPath.isNullOrEmpty()) {
            AsyncImage(
                model = File(customWallpaperPath),
                contentDescription = null,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.3f))
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AureoleDS.colors.background)
            )
        }
    }
}

@AureolePreview
@Composable
private fun WallpaperBackgroundPreview() {
    AureoleLauncherTheme {
        WallpaperBackground(
            isCustomWallpaperSet = false,
            customWallpaperPath = null,
            hazeState = HazeState()
        )
    }
}
