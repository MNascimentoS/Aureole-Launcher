package dev.mnascimentos.aureole.feature.settings.components.wallpaper

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/** The ghost home is drawn at this opacity, so the photo shows through it (RF-03). */
private const val GHOST_ALPHA = 0.4f

/** Opacity of the app squares of the ghost home (before [GHOST_ALPHA]). */
private const val GHOST_APP_ALPHA = 0.85f

/** Apps of the ghost row, like a row of favorites. */
private const val GHOST_APPS = 4

/** Space above and below the ghost row of apps; the row sits a little above the middle. */
private const val GHOST_TOP_WEIGHT = 1f
private const val GHOST_BOTTOM_WEIGHT = 1.3f

/** Opacity of the frame line and of the strip outside it. */
private const val FRAME_LINE_ALPHA = 0.55f
private const val OUTSIDE_FRAME_ALPHA = 0.35f

/** Opacity of the pill behind the zoom tag. */
private const val ZOOM_TAG_ALPHA = 0.45f

/**
 * The home in ghost form over the photo: clock, date and a row of apps at 40%, without touch, so the user sees what
 * sits on top of which part of the photo.
 */
@Composable
internal fun WallpaperEditorGhost(modifier: Modifier = Modifier) {
    val content = sampleMiniHomeContent()
    val ink = AureoleDS.colors.onSurfaceHigh
    Column(
        modifier = modifier
            .fillMaxSize()
            .graphicsLayer { alpha = GHOST_ALPHA }
            .clearAndSetSemantics {}
            .statusBarsPadding()
            .padding(top = AureoleDS.dimens.xxLarge),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AureoleText(
            text = content.time,
            color = ink,
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.displayLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        )
        AureoleText(
            text = content.date,
            color = ink,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall, textAlign = TextAlign.Center)
        )
        Spacer(modifier = Modifier.weight(GHOST_TOP_WEIGHT))
        GhostApps(color = ink.copy(alpha = GHOST_APP_ALPHA))
        Spacer(modifier = Modifier.weight(GHOST_BOTTOM_WEIGHT))
    }
}

@Composable
private fun GhostApps(color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = AureoleDS.dimens.xLarge),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        repeat(GHOST_APPS) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.quickActionIconSize)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusAppIcon))
                    .background(color)
            )
        }
    }
}

/** A dashed line around the screen area; the thin strip outside it is dimmed (RF-03). */
@Composable
internal fun WallpaperEditorScreenFrame(modifier: Modifier = Modifier) {
    val line = AureoleDS.colors.onSurfaceHigh.copy(alpha = FRAME_LINE_ALPHA)
    val outside = Color.Black.copy(alpha = OUTSIDE_FRAME_ALPHA)
    val dimens = AureoleDS.dimens
    Canvas(modifier = modifier.fillMaxSize()) {
        val inset = dimens.xSmall.toPx()
        val radius = CornerRadius(dimens.radiusLarge.toPx())
        val rect = Rect(Offset(inset, inset), Offset(size.width - inset, size.height - inset))
        val frame = Path().apply { addRoundRect(RoundRect(rect, radius)) }
        clipPath(frame, clipOp = ClipOp.Difference) { drawRect(outside) }
        val dash = dimens.dashLength.toPx()
        val dashes = PathEffect.dashPathEffect(floatArrayOf(dash, dash))
        val stroke = Stroke(width = dimens.minimal.toPx(), pathEffect = dashes)
        drawRoundRect(color = line, topLeft = rect.topLeft, size = rect.size, cornerRadius = radius, style = stroke)
    }
}

/** The zoom over the "cover the screen" size, as a small tag in the corner ("2.0x"). */
@Composable
internal fun WallpaperEditorZoomTag(zoom: Float, modifier: Modifier = Modifier) {
    AureoleText(
        text = stringResource(R.string.settings_wallpaper_editor_zoom_value, zoom),
        color = AureoleDS.colors.onSurfaceHigh,
        modifier = modifier
            .clearAndSetSemantics {}
            .statusBarsPadding()
            .padding(AureoleDS.dimens.large)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.background.copy(alpha = ZOOM_TAG_ALPHA))
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xxSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
    )
}

@AureolePreview
@Composable
private fun WallpaperEditorGhostPreview() {
    AureoleLauncherTheme {
        Box(modifier = Modifier.fillMaxSize().background(AureoleDS.colors.surfaceVariant)) {
            WallpaperEditorGhost()
            WallpaperEditorScreenFrame()
            WallpaperEditorZoomTag(zoom = 1f, modifier = Modifier.align(Alignment.TopEnd))
        }
    }
}
