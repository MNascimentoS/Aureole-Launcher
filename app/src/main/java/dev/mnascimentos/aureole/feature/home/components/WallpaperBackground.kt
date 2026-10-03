package dev.mnascimentos.aureole.feature.home.components

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
import java.io.File

@Composable
fun WallpaperBackground(
    isCustomWallpaperSet: Boolean,
    customWallpaperPath: String?,
    hazeState: HazeState,
    isHazeEnabled: Boolean,
    wallpaperScaleType: String = "Crop"
) {
    val hazeModifier = if (isHazeEnabled) {
        Modifier.hazeSource(state = hazeState)
    } else {
        Modifier
    }

    val contentScale = when (wallpaperScaleType) {
        "Fit" -> ContentScale.Fit
        "Fill" -> ContentScale.FillBounds
        else -> ContentScale.Crop
    }

    Box(modifier = Modifier.fillMaxSize().then(hazeModifier)) {
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
