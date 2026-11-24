package dev.mnascimentos.aureole.composable.wallpaper

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.data.model.WallpaperGeometry
import dev.mnascimentos.aureole.core.image.BlurredWallpaperCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import coil.size.Size as CoilSize

/**
 * The photo drawn through [WallpaperGeometry.visibleRect]: rotated, scaled so the view just covers its visible part
 * and moved to the saved focus. Portrait and landscape share the same focus.
 */
@Composable
fun FramedWallpaperImage(file: File, adjustments: WallpaperAdjustments, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val request = remember(file) {
        ImageRequest.Builder(context).data(file).size(CoilSize.ORIGINAL).build()
    }
    val painter = rememberAsyncImagePainter(model = request)
    Canvas(modifier = modifier) { drawFramedWallpaper(painter, adjustments) }
}

/**
 * Draws [painter] over the whole draw area through [WallpaperGeometry.visibleRect]; nothing while its size is not
 * known yet. Any resolution of the same photo gives the same picture (the editor draws a small blurred copy).
 */
fun DrawScope.drawFramedWallpaper(painter: Painter, adjustments: WallpaperAdjustments) {
    val image = painter.intrinsicSize
    if (image == Size.Unspecified || image.width <= 0f || image.height <= 0f) return
    val rotated = WallpaperGeometry.rotatedSize(image, adjustments.rotation)
    val visible = WallpaperGeometry.visibleRect(image, size, adjustments)
    val scale = size.width / (visible.width * rotated.width)
    translate(left = -visible.left * rotated.width * scale, top = -visible.top * rotated.height * scale) {
        val center = Offset(rotated.width / 2f, rotated.height / 2f)
        scale(scale, scale, pivot = Offset.Zero) {
            rotate(adjustments.rotation.toFloat(), pivot = center) {
                translate((rotated.width - image.width) / 2f, (rotated.height - image.height) / 2f) {
                    with(painter) { draw(image) }
                }
            }
        }
    }
}

/**
 * The pre-blurred copy of [file] for Android 9-11, made in the background and cached; [file] itself while the copy
 * is not ready or when [blurPx] is zero.
 */
@Composable
fun rememberBlurredWallpaperFile(file: File, blurPx: Int): File {
    val context = LocalContext.current
    val blurred by produceState(initialValue = file, file, blurPx) {
        value = if (blurPx < 1) {
            file
        } else {
            withContext(Dispatchers.Default) { BlurredWallpaperCache.obtain(context, file, blurPx) } ?: file
        }
    }
    return blurred
}
