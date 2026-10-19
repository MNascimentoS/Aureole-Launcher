package dev.mnascimentos.aureole.core.designsystem.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope

private const val ANIMATION_DURATION_MS = 300

/**
 * Configuration options for [AureoleEditOverlay].
 *
 * @property title Title text displayed at the top of the overlay sheet.
 * @property hazeState Blur effect state for background haze blurring.
 * @property isVisible Controls visibility state of the overlay.
 * @property animated Whether show/hide transition animations are enabled.
 * @property maxSheetHeightFraction Maximum fraction of screen height occupied by the sheet.
 * @property onBackPress Custom back key handler callback.
 * @property onSettingsClick Settings icon click callback.
 */
data class AureoleEditOverlayConfig(
    val title: String? = null,
    val hazeState: HazeState? = null,
    val isVisible: Boolean = true,
    val animated: Boolean = true,
    val maxSheetHeightFraction: Float = 0.70f,
    val onBackPress: (() -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
)

/**
 * Content slots and configuration parameters for [AureoleEditOverlay].
 *
 * @param config Configuration options provided via [AureoleEditOverlayConfig].
 * @param previewContent Optional preview slot displayed in the main screen area above the bottom sheet.
 * @param bottomBarContent Optional action bottom bar slot anchored below the sheet.
 * @param sheetContent Main sheet content slot.
 */
class AureoleEditOverlayParams(
    val config: AureoleEditOverlayConfig = AureoleEditOverlayConfig(),
    val previewContent: (@Composable BoxScope.() -> Unit)? = null,
    val bottomBarContent: (@Composable ColumnScope.() -> Unit)? = null,
    val sheetContent: (@Composable ColumnScope.() -> Unit)? = null,
)

/**
 * Fullscreen modal overlay sheet for edit mode features (such as container/clock/widget group editing).
 *
 * Supports background blur via Haze, swipe-down dismiss gestures, preview content area, bottom bar actions,
 * and custom sheet layout.
 *
 * @param modifier The [Modifier] applied to the root overlay layout. First optional parameter.
 * @param params Configuration and content slots provided via [AureoleEditOverlayParams].
 * @param onDismissRequest Callback executed when overlay is dismissed via drag gesture or back press.
 */
@Composable
fun AureoleEditOverlay(
    modifier: Modifier = Modifier,
    params: AureoleEditOverlayParams = AureoleEditOverlayParams(),
    onDismissRequest: () -> Unit = {},
) {
    val config = params.config
    val isInspection = LocalInspectionMode.current
    val effectiveAnimated = config.animated && !isInspection

    val overlayState = rememberOverlayDismissState(
        effectiveAnimated = effectiveAnimated,
        isVisible = config.isVisible,
        onBackPress = config.onBackPress,
        onDismissRequest = onDismissRequest
    )

    if (!isInspection) {
        BackHandler(enabled = config.isVisible && overlayState.internalVisible) {
            overlayState.triggerDismiss()
        }
    }

    AnimatedVisibility(
        visibleState = overlayState.visibleState,
        enter = fadeIn(animationSpec = tween(ANIMATION_DURATION_MS)),
        exit = fadeOut(animationSpec = tween(ANIMATION_DURATION_MS)),
        modifier = modifier.fillMaxSize()
    ) {
        CompositionLocalProvider(LocalAnimatedVisibilityScope provides this) {
            OverlayContentBox(
                params = params,
                hazeState = config.hazeState,
                effectiveAnimated = effectiveAnimated,
                triggerDismiss = overlayState.triggerDismiss,
                animScope = this
            )
        }
    }
}

private class OverlayDismissState(
    val visibleState: MutableTransitionState<Boolean>,
    val internalVisible: Boolean,
    val triggerDismiss: () -> Unit
)

@Composable
private fun rememberOverlayDismissState(
    effectiveAnimated: Boolean,
    isVisible: Boolean,
    onBackPress: (() -> Unit)?,
    onDismissRequest: () -> Unit
): OverlayDismissState {
    var internalVisible by remember { mutableStateOf(true) }

    val visibleState = remember(effectiveAnimated) {
        MutableTransitionState(if (effectiveAnimated) false else isVisible)
    }.apply {
        targetState = if (effectiveAnimated) isVisible && internalVisible else isVisible
    }

    val triggerDismiss = remember(effectiveAnimated, onBackPress, onDismissRequest) {
        {
            if (effectiveAnimated) {
                internalVisible = false
            } else {
                onBackPress?.invoke() ?: onDismissRequest()
            }
        }
    }

    LaunchedEffect(visibleState.currentState, visibleState.isIdle, internalVisible) {
        val isHidden = !visibleState.currentState && !internalVisible
        if (effectiveAnimated && visibleState.isIdle && isHidden) {
            onBackPress?.invoke() ?: onDismissRequest()
        }
    }

    return remember(visibleState, internalVisible, triggerDismiss) {
        OverlayDismissState(visibleState, internalVisible, triggerDismiss)
    }
}

@AureolePreview
@Composable
private fun AureoleEditOverlayPreview() {
    AureoleLauncherTheme {
        AureoleEditOverlay(
            onDismissRequest = {},
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(title = "Edit Section"),
                previewContent = {
                    Text(
                        text = "Preview Slot",
                        color = Color.White,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                sheetContent = {
                    Text(
                        text = "Sheet Content",
                        color = AureoleDS.colors.onSurfaceHigh
                    )
                }
            )
        )
    }
}
