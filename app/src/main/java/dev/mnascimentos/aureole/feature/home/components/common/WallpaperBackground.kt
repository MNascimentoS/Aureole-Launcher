package dev.mnascimentos.aureole.feature.home.components.common

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.mnascimentos.aureole.composable.wallpaper.FramedWallpaperImage
import dev.mnascimentos.aureole.composable.wallpaper.rememberBlurredWallpaperFile
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import java.io.File

@Composable
fun WallpaperBackground(
    customWallpaperPath: String?,
    hazeState: HazeState,
    modifier: Modifier = Modifier,
    wallpaperScaleType: String = "Crop",
    adjustments: WallpaperAdjustments = WallpaperAdjustments.DEFAULT
) {
    val hazeModifier = Modifier.hazeSource(state = hazeState)

    Box(modifier = modifier.fillMaxSize().then(hazeModifier)) {
        if (!customWallpaperPath.isNullOrEmpty()) {
            CustomWallpaper(File(customWallpaperPath), wallpaperScaleType, adjustments)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AureoleDS.colors.background)
            )
        }
    }
}

/**
 * The photo with its saved adjustments: the blur wraps the photo alone (live effect on Android 12+, a pre-blurred
 * copy below), then the dim layer goes on top. Without framing the saved "Image scale" is honored as before.
 */
@Composable
private fun CustomWallpaper(file: File, scaleType: String, adjustments: WallpaperAdjustments) {
    val density = LocalDensity.current
    val blurPx = with(density) { adjustments.blurDp.dp.toPx() }
    val liveBlur = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val source = if (liveBlur) file else rememberBlurredWallpaperFile(file, blurPx.toInt())
    val blurModifier = if (liveBlur && blurPx > 0f) {
        Modifier.clipToBounds().graphicsLayer {
            renderEffect = BlurEffect(blurPx, blurPx, TileMode.Clamp)
        }
    } else {
        Modifier
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().then(blurModifier)) {
            if (adjustments.hasFraming) {
                FramedWallpaperImage(file = source, adjustments = adjustments, modifier = Modifier.fillMaxSize())
            } else {
                AsyncImage(
                    model = source,
                    contentDescription = null,
                    contentScale = when (scaleType) {
                        "Fit" -> ContentScale.Fit
                        "Fill" -> ContentScale.FillBounds
                        else -> ContentScale.Crop
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = adjustments.dim))
        )
    }
}

@AureolePreview
@Composable
private fun WallpaperBackgroundPreview() {
    AureoleLauncherTheme {
        WallpaperBackground(
            customWallpaperPath = null,
            hazeState = HazeState()
        )
    }
}
