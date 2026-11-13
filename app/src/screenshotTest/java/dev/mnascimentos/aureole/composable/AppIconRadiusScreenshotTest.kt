package dev.mnascimentos.aureole.composable

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.InsetDrawable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private val IconSize = 64.dp

private fun adaptiveIcon(): Drawable {
    val glyph = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(Color.WHITE)
    }
    return AdaptiveIconDrawable(ColorDrawable(0xFF1B65C0.toInt()), InsetDrawable(glyph, 0.25f))
}

private fun appWith(icon: Drawable) = AppInfo(
    label = "App",
    packageName = "com.example.app",
    componentName = ComponentName("com.example.app", "MainActivity"),
    icon = icon
)

/** Adaptive (full bleed), legacy bitmap (never clipped) and adaptive themed (tile), at the given radius. */
@Composable
private fun IconsAtRadius(radiusDp: Int) {
    val config = AureoleThemeConfig(
        darkTheme = isSystemInDarkTheme(),
        aureoleDimens = AureoleDimens.forCornerRadius(radiusDp)
    )
    AureoleLauncherTheme(config) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(AureoleDS.dimens.medium)
        ) {
            AureoleAppIcon(app = appWith(adaptiveIcon()), size = IconSize)
            AureoleAppIcon(app = appWith(0xFF2E7D32.toInt().toDrawable()), size = IconSize)
            AureoleAppIcon(
                app = appWith(adaptiveIcon()),
                size = IconSize,
                params = AureoleAppIconParams(isThemed = true)
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun AppIconRadiusZeroScreenshotTest() = IconsAtRadius(0)

@PreviewTest
@AureolePreview
@Composable
private fun AppIconRadiusDefaultScreenshotTest() = IconsAtRadius(20)

@PreviewTest
@AureolePreview
@Composable
private fun AppIconRadius24ScreenshotTest() = IconsAtRadius(24)
