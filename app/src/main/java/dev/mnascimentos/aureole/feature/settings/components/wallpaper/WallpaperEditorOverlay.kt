package dev.mnascimentos.aureole.feature.settings.components.wallpaper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.AureoleAutoSaveOverlay
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.model.rememberAureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorFrame
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorHandlers
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorParams
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorState
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorStateSaver
import dev.mnascimentos.aureole.feature.settings.model.lerpAdjustments
import dev.mnascimentos.aureole.feature.settings.model.withAdjustments
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/** Same fade as the backdrop of the sheets. */
private const val STAGE_FADE_MS = 300

/**
 * The photo editor, in full screen over the settings (RF-03 to RF-06): the photo with the ghost home and the frame,
 * which gestures move, and the sheet with the tabs below. A tap around the sheet does not close it (the photo takes
 * the screen); back or a swipe down on the sheet does, and then [WallpaperEditorParams.onFinish] gets the opening and
 * the final adjustments, once. Saving happens there, with the undo, because a photo picked right before the editor
 * changes which undo is offered.
 */
@Composable
fun WallpaperEditorOverlay(params: WallpaperEditorParams, modifier: Modifier = Modifier) {
    var state by rememberSaveable(stateSaver = WallpaperEditorStateSaver) {
        mutableStateOf(WallpaperEditorState(initial = params.initial, tab = params.initialTab))
    }
    var viewSize by remember { mutableStateOf(Size.Unspecified) }
    val photo = rememberEditorPhotoPainter(params.photoPath)
    val frame = WallpaperEditorFrame(imageSize = photo?.intrinsicSize ?: Size.Unspecified, viewSize = viewSize)
    val blurPx = with(LocalDensity.current) { state.current.blurDp.dp.toPx() }.toInt()
    val shown = rememberPreviewBlurPainter(params.photoPath, blurPx) ?: photo

    var target by remember { mutableStateOf<WallpaperAdjustments?>(null) }
    val handlers = rememberEditorHandlers(read = { state }, write = { state = it }, onTarget = { target = it })

    val overlayState = rememberAureoleEditOverlayState()
    var finished by remember { mutableStateOf(false) }
    val current by rememberUpdatedState(params)
    CompositionLocalProvider(LocalLeftHanded provides params.isLeftHanded) {
        Box(modifier = modifier.fillMaxSize().onSizeChanged { viewSize = it.toSize() }) {
            StageVisibility(overlayState) { WallpaperEditorStage(state, frame, shown, handlers) }
            AureoleAutoSaveOverlay(
                params = AutoSaveOverlayParams(
                    title = stringResource(R.string.settings_wallpaper_editor_title),
                    isChanged = { state.isChanged },
                    onCommit = {},
                    onDismissRequest = {
                        if (!finished) {
                            finished = true
                            current.onFinish(state.initial, target ?: state.current)
                        }
                        current.onDismissRequest()
                    },
                    isLeftHanded = params.isLeftHanded,
                    hasExternalBackdrop = true,
                    host = params.host,
                    dismissOnTapOutside = false,
                    overlayState = overlayState
                )
            ) {
                WallpaperEditorControls(state = state, frame = frame, handlers = handlers)
            }
        }
    }
}

/**
 * Changes go straight to the state; animated ones (double tap, "Center", "Reset") spring there frame by frame, and a
 * gesture or another change stops them. [onTarget] knows where a running spring is going, so closing mid-way saves
 * its end.
 */
@Composable
private fun rememberEditorHandlers(
    read: () -> WallpaperEditorState,
    write: (WallpaperEditorState) -> Unit,
    onTarget: (WallpaperAdjustments?) -> Unit
): WallpaperEditorHandlers {
    val scope = rememberCoroutineScope()
    val progress = remember { Animatable(0f) }
    var running by remember { mutableStateOf<Job?>(null) }
    return WallpaperEditorHandlers(
        onChange = { next ->
            running?.cancel()
            onTarget(null)
            write(next)
        },
        onAnimateTo = { to ->
            running?.cancel()
            val from = read().current
            onTarget(to)
            running = scope.launch {
                progress.snapTo(0f)
                progress.animateTo(1f, spring(stiffness = Spring.StiffnessMediumLow)) {
                    write(read().withAdjustments(lerpAdjustments(from, to, value)))
                }
                write(read().withAdjustments(to))
                onTarget(null)
            }
        }
    )
}

/** The photo fades in with the sheet and out as soon as the sheet starts to leave. */
@Composable
private fun StageVisibility(overlayState: AureoleEditOverlayState, content: @Composable () -> Unit) {
    val inspection = LocalInspectionMode.current
    val visible = remember { MutableTransitionState(inspection) }
    visible.targetState = overlayState.internalVisible
    AnimatedVisibility(
        visibleState = visible,
        enter = fadeIn(tween(STAGE_FADE_MS)),
        exit = fadeOut(tween(STAGE_FADE_MS))
    ) {
        content()
    }
}

@AureolePreview
@Composable
private fun WallpaperEditorOverlayPreview() {
    AureoleLauncherTheme {
        WallpaperEditorOverlay(
            params = WallpaperEditorParams(
                photoPath = null,
                initial = WallpaperAdjustments.DEFAULT,
                onFinish = { _, _ -> },
                onDismissRequest = {}
            )
        )
    }
}
