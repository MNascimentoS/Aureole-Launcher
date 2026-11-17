package dev.mnascimentos.aureole.feature.settings.components.wallpaper

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.common.drawFramedWallpaper
import dev.mnascimentos.aureole.feature.settings.model.EDITOR_ZOOM_STEP
import dev.mnascimentos.aureole.feature.settings.model.EditorA11yLabels
import dev.mnascimentos.aureole.feature.settings.model.EditorGestureCallbacks
import dev.mnascimentos.aureole.feature.settings.model.EditorMove
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorFrame
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorHandlers
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorState
import dev.mnascimentos.aureole.feature.settings.model.doubleTapTarget
import dev.mnascimentos.aureole.feature.settings.model.moved
import dev.mnascimentos.aureole.feature.settings.model.overshootScale
import dev.mnascimentos.aureole.feature.settings.model.pinched
import dev.mnascimentos.aureole.feature.settings.model.settled
import dev.mnascimentos.aureole.feature.settings.model.zoomedBy
import kotlinx.coroutines.launch

/**
 * The photo in full screen as the home will draw it (framing, blur and dim), the ghost home over it and the screen
 * frame. Drag moves it, pinch zooms it (1x-5x, a pinch past 5x stretches and springs back), a double tap switches
 * between 1x and 2x at the point (RF-04); TalkBack gets zoom and move actions instead.
 *
 * @param painter The photo to draw: the original, or the small blurred copy on Android 9-11; null draws only the
 *   background color.
 */
@Composable
internal fun WallpaperEditorStage(
    state: WallpaperEditorState,
    frame: WallpaperEditorFrame,
    painter: Painter?,
    handlers: WallpaperEditorHandlers,
    modifier: Modifier = Modifier
) {
    val latest by rememberUpdatedState(state)
    val latestFrame by rememberUpdatedState(frame)
    val scope = rememberCoroutineScope()
    val bounce = remember { Animatable(1f) }
    var pivot by remember { mutableStateOf(Offset.Unspecified) }
    val callbacks = EditorGestureCallbacks(
        onTransform = { zoomBy, centroid, pan ->
            val next = latest.pinched(latestFrame, zoomBy, centroid, pan)
            handlers.onChange(next)
            pivot = centroid
            scope.launch { bounce.snapTo(overshootScale(next.overshoot)) }
        },
        onEnd = {
            handlers.onChange(latest.settled())
            scope.launch { bounce.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow)) }
        },
        onDoubleTap = { point -> handlers.onAnimateTo(latest.doubleTapTarget(latestFrame, point)) }
    )
    val currentCallbacks by rememberUpdatedState(callbacks)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AureoleDS.colors.background)
            .editorGestures { currentCallbacks }
            .editorPhotoSemantics(editorA11yLabels(state.current.zoom), state, frame, handlers)
    ) {
        EditorPhoto(
            painter = painter,
            adjustments = state.current,
            modifier = Modifier.graphicsLayer {
                val scale = bounce.value
                scaleX = scale
                scaleY = scale
                transformOrigin = transformOriginOf(pivot, size.width, size.height)
            }
        )
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = state.current.dim)))
        WallpaperEditorGhost()
        WallpaperEditorScreenFrame()
        WallpaperEditorZoomTag(zoom = state.current.zoom, modifier = Modifier.align(Alignment.TopEnd))
    }
}

private fun transformOriginOf(pivot: Offset, width: Float, height: Float): TransformOrigin =
    if (pivot == Offset.Unspecified || width <= 0f || height <= 0f) {
        TransformOrigin.Center
    } else {
        TransformOrigin(pivot.x / width, pivot.y / height)
    }

/** The photo through the framing, blurred live on Android 12+ (below that, [painter] is already blurred). */
@Composable
private fun EditorPhoto(painter: Painter?, adjustments: WallpaperAdjustments, modifier: Modifier = Modifier) {
    val blurPx = with(LocalDensity.current) { adjustments.blurDp.dp.toPx() }
    val blur = if (hasLiveBlur && blurPx > 0f) {
        Modifier.clipToBounds().graphicsLayer { renderEffect = BlurEffect(blurPx, blurPx, TileMode.Clamp) }
    } else {
        Modifier
    }
    Canvas(modifier = modifier.fillMaxSize().then(blur)) {
        painter?.let { drawFramedWallpaper(it, adjustments) }
    }
}

/** What TalkBack says: the photo, its zoom and the names of the actions. */
@Composable
@ReadOnlyComposable
private fun editorA11yLabels(zoom: Float): EditorA11yLabels = EditorA11yLabels(
    photo = stringResource(R.string.settings_wallpaper_editor_photo),
    zoom = stringResource(R.string.settings_wallpaper_editor_zoom_value, zoom),
    zoomIn = stringResource(R.string.settings_wallpaper_editor_zoom_in),
    zoomOut = stringResource(R.string.settings_wallpaper_editor_zoom_out),
    moves = listOf(
        EditorMove.UP to stringResource(R.string.settings_wallpaper_editor_move_up),
        EditorMove.DOWN to stringResource(R.string.settings_wallpaper_editor_move_down),
        EditorMove.LEFT to stringResource(R.string.settings_wallpaper_editor_move_left),
        EditorMove.RIGHT to stringResource(R.string.settings_wallpaper_editor_move_right)
    )
)

/** TalkBack reads the zoom and offers zoom in/out and moves, since pinch and drag are out of its reach. */
private fun Modifier.editorPhotoSemantics(
    labels: EditorA11yLabels,
    state: WallpaperEditorState,
    frame: WallpaperEditorFrame,
    handlers: WallpaperEditorHandlers
): Modifier {
    val zooms = listOf(
        CustomAccessibilityAction(labels.zoomIn) {
            handlers.onChange(state.zoomedBy(frame, EDITOR_ZOOM_STEP))
            true
        },
        CustomAccessibilityAction(labels.zoomOut) {
            handlers.onChange(state.zoomedBy(frame, 1f / EDITOR_ZOOM_STEP))
            true
        }
    )
    val moves = labels.moves.map { (direction, name) ->
        CustomAccessibilityAction(name) {
            handlers.onChange(state.moved(frame, direction))
            true
        }
    }
    return semantics {
        contentDescription = labels.photo
        stateDescription = labels.zoom
        customActions = zooms + moves
    }
}

@AureolePreview
@Composable
private fun WallpaperEditorStagePreview() {
    AureoleLauncherTheme {
        WallpaperEditorStage(
            state = WallpaperEditorState(initial = WallpaperAdjustments.DEFAULT),
            frame = WallpaperEditorFrame(imageSize = Size.Unspecified, viewSize = Size.Unspecified),
            painter = null,
            handlers = WallpaperEditorHandlers(onChange = {}, onAnimateTo = {})
        )
    }
}
