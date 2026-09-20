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
import dev.mnascimentos.aureole.R
import java.io.File

@Composable
fun WallpaperBackground(
    isCustomWallpaperSet: Boolean,
    customWallpaperPath: String?,
    hazeState: HazeState,
    isHazeEnabled: Boolean
) {
    val model: Any = if (isCustomWallpaperSet && !customWallpaperPath.isNullOrEmpty()) {
        File(customWallpaperPath)
    } else {
        R.drawable.default_wallpaper
    }

    val hazeModifier = if (isHazeEnabled) {
        Modifier.hazeSource(state = hazeState)
    } else {
        Modifier
    }

    Box(modifier = Modifier.fillMaxSize().then(hazeModifier)) {
        AsyncImage(
            model = model,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.3f))
        )
    }
}
