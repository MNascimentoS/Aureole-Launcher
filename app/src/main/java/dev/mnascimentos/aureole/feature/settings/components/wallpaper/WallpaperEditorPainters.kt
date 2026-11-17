package dev.mnascimentos.aureole.feature.settings.components.wallpaper

import android.graphics.Bitmap
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size as CoilSize
import dev.mnascimentos.aureole.core.image.WallpaperPreviewBlur
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/** Whether this Android blurs live (12+); below it the editor shows a small pre-blurred copy. */
internal val hasLiveBlur: Boolean get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

/**
 * The original photo at full size, with the same request as the home's background, so it comes from the cache.
 * Null without a photo.
 */
@Composable
internal fun rememberEditorPhotoPainter(path: String?): Painter? {
    val context = LocalContext.current
    val request = remember(path) {
        path?.let { ImageRequest.Builder(context).data(File(it)).size(CoilSize.ORIGINAL).build() }
    }
    return rememberAsyncImagePainter(model = request).takeIf { request != null }
}

/**
 * On Android 9-11, the photo blurred by [blurPx] as a small copy, made in the background each time the blur
 * changes (the previous one stays on screen meanwhile); null on Android 12+, without blur or while the first copy
 * is not ready.
 */
@Composable
internal fun rememberPreviewBlurPainter(path: String?, blurPx: Int): Painter? {
    val photo = path.takeIf { !hasLiveBlur }
    val source by produceState<WallpaperPreviewBlur.Source?>(initialValue = null, photo) {
        value = photo?.let { withContext(Dispatchers.IO) { WallpaperPreviewBlur.decode(it) } }
    }
    val blurred by produceState<Bitmap?>(initialValue = null, source, blurPx) {
        val small = source
        value = if (small == null || blurPx < 1) null else withContext(Dispatchers.Default) {
            WallpaperPreviewBlur.blurred(small, blurPx)
        }
    }
    return remember(blurred) { blurred?.let { BitmapPainter(it.asImageBitmap()) } }
}
