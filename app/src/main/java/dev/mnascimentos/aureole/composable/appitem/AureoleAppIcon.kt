package dev.mnascimentos.aureole.composable.appitem

import android.content.ComponentName
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private const val THEMED_BOX_ALPHA = 0.85f
private const val THEMED_BOX_PADDING_RATIO = 0.19f

/**
 * The only way the UI draws an app icon. Full bleed (adaptive) icons are clipped with
 * `radiusAppIcon`; themed icons sit on a tile with `radiusSmall`; legacy bitmaps keep their own look.
 * [size] may be left unspecified when [modifier] already carries the size.
 */
@Composable
fun AureoleAppIcon(
    app: AppInfo,
    modifier: Modifier = Modifier,
    size: Dp = Dp.Unspecified,
    params: AureoleAppIconParams = AureoleAppIconParams()
) {
    val sized = if (size == Dp.Unspecified) modifier else modifier.size(size)
    val bitmap = app.getDisplayIconBitmap(isThemed = params.isThemed, tintColor = AureoleDS.colors.onSurfaceMedium)

    if (params.isThemed && params.showThemedBox) {
        val padding = if (size == Dp.Unspecified) AureoleDS.dimens.xSmall else size * THEMED_BOX_PADDING_RATIO
        Box(
            modifier = sized
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = THEMED_BOX_ALPHA))
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Image(bitmap = bitmap, contentDescription = params.contentDescription, modifier = Modifier.fillMaxSize())
        }
    } else {
        val shape = if (app.isFullBleedIcon && !params.isThemed) {
            RoundedCornerShape(AureoleDS.dimens.radiusAppIcon)
        } else {
            params.legacyShape
        }
        Image(bitmap = bitmap, contentDescription = params.contentDescription, modifier = sized.clip(shape))
    }
}

@AureolePreview
@Composable
private fun AureoleAppIconPreview() {
    val app = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    AureoleLauncherTheme {
        AureoleAppIcon(app = app, size = 42.dp)
    }
}
