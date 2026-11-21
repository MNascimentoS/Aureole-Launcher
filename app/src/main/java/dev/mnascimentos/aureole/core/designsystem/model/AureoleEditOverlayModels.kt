package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState

/**
 * State holder for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
 *
 * @property internalVisible Whether the overlay is internally visible for exit animations.
 * @property isExpanded Whether the sheet sits at its expanded height instead of its rest height.
 */
class AureoleEditOverlayState(
    initialInternalVisible: Boolean = true,
    initialExpanded: Boolean = false,
) {
    var internalVisible by mutableStateOf(initialInternalVisible)
    var isExpanded by mutableStateOf(initialExpanded)
}

/**
 * Creates and remembers an [AureoleEditOverlayState].
 */
@Composable
fun rememberAureoleEditOverlayState(
    initialInternalVisible: Boolean = true,
    initialExpanded: Boolean = false,
): AureoleEditOverlayState {
    return remember {
        AureoleEditOverlayState(
            initialInternalVisible = initialInternalVisible,
            initialExpanded = initialExpanded,
        )
    }
}

/**
 * Configuration options for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
 *
 * @property title Title text displayed at the top of the overlay sheet.
 * @property hazeState Blur effect state for background haze blurring.
 * @property isVisible Controls visibility state of the overlay.
 * @property animated Whether show/hide transition animations are enabled.
 * @property restHeightFraction Maximum fraction of the screen the sheet takes at rest; below it the sheet
 * hugs its content.
 * @property expandedHeightFraction Fraction of the screen the sheet takes when expanded.
 * @property isLeftHanded Whether the user holds the phone with the left thumb. Callers pass the launcher setting.
 * @property onBackPress Back handler: the back key and gesture. When set it decides what back does (go up a
 * screen, or close) and the overlay does not hide itself first.
 * @property onDismiss Handler for drag-down and tap-outside. Falls back to [onBackPress], then to the overlay's
 * own dismiss request.
 * @property onSettingsClick Settings icon click callback.
 * @property showHeaderAction Whether the header logo (or [headerIcon]) is drawn on the title line; false leaves it
 * out, and so does a null [onSettingsClick] without a [headerIcon].
 * @property headerIcon Optional custom icon for sheet header end action.
 * @property titleIcon Optional icon drawn before the title (menus show what the sheet is about).
 * @property edgeToEdgeContent Whether the sheet content takes the full sheet width, with no horizontal padding;
 * the content then pads itself (a pager needs this so its pages do not clip at the margin mid-swipe).
 * @property hasExternalBackdrop True when the screen around draws the dimmed, blurred backdrop itself (a screen
 * that swaps one sheet for another keeps one backdrop for both, so it does not flash between them).
 * @property host What the screen hosting several overlays decides for all of them (see [OverlayHostSpec]).
 * @property dismissOnTapOutside Whether a tap outside the sheet closes it. False lets the touches around the sheet
 * reach what is drawn behind the overlay (the photo editor moves the photo there).
 * @property isLocked A showcase: back, drag-down, tap-outside, the gesture zone and the header logo do nothing; only
 * the caller closes the overlay (through [isVisible]). Touches around the sheet still stop at the overlay.
 * @property headerAccessory Drawn in the header slot in place of the logo, not clickable (the showcase ring).
 * @property headerLeadingAction Drawn in the 48 dp slot across from the logo, at the start of the title line (the
 * end, left-handed): a shortcut of the sheet, such as the "Customize home" pencil of "Choose favorites".
 */
data class AureoleEditOverlayConfig(
    val title: String? = null,
    val hazeState: HazeState? = null,
    val isVisible: Boolean = true,
    val animated: Boolean = true,
    val restHeightFraction: Float = 0.6f,
    val expandedHeightFraction: Float = 0.9f,
    val isLeftHanded: Boolean = false,
    val onBackPress: (() -> Unit)? = null,
    val onDismiss: (() -> Unit)? = null,
    val onSettingsClick: (() -> Unit)? = null,
    val showHeaderAction: Boolean = true,
    val headerIcon: (@Composable (Modifier) -> Unit)? = null,
    val titleIcon: (@Composable (Modifier) -> Unit)? = null,
    /** Secondary line under the title, in the header itself (e.g. the size of the item being edited). */
    val subtitle: String? = null,
    val edgeToEdgeContent: Boolean = false,
    val hasExternalBackdrop: Boolean = false,
    val host: OverlayHostSpec = OverlayHostSpec(),
    /** The bottom bar is the tab bar: it keeps a tight margin above and below, unlike a row of buttons. */
    val hasTabBottomBar: Boolean = false,
    val dismissOnTapOutside: Boolean = true,
    val isLocked: Boolean = false,
    val headerAccessory: (@Composable () -> Unit)? = null,
    val headerLeadingAction: (@Composable () -> Unit)? = null,
)

/**
 * Parameter container for [dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay].
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
 * Sheet heights in pixels, resolved from the screen height and [AureoleEditOverlayConfig].
 *
 * @property restMaxPx Largest height the sheet takes at rest.
 * @property expandedPx Largest height of the expanded sheet; it stops at its content when that is shorter.
 * @property elasticLimitPx Most the sheet stretches above rest when its content already fits.
 * @property bouncePx How high the short bounce goes when the sheet cannot expand (double tap, fling up).
 * @property fitSlackPx How much taller than the rest height the content may be and still count as fitting.
 */
internal data class SheetBounds(
    val restMaxPx: Float,
    val expandedPx: Float,
    val elasticLimitPx: Float = 0f,
    val bouncePx: Float = 0f,
    val fitSlackPx: Float = 0f
)

/**
 * Extra slots of the sheet body: a custom header icon and the invisible gesture zone above the header.
 */
internal class OverlaySheetDecorations(
    val gestureZone: (@Composable () -> Unit)? = null,
    val edgeToEdgeContent: Boolean = false,
    val showHeaderAction: Boolean = true,
    /** Left-handed: the logo goes to the start of the title line. */
    val isLeftHanded: Boolean = false,
    val hasTabBottomBar: Boolean = false,
    val headerSlots: OverlayHeaderSlots = OverlayHeaderSlots()
)

/**
 * The optional header slots: the [headerIcon] and [titleIcon] of the config; [accessory] replaces the logo in the
 * header slot, with no click (see [AureoleEditOverlayConfig.headerAccessory]); [leadingAction] is the slot across
 * from the logo (see [AureoleEditOverlayConfig.headerLeadingAction]).
 */
internal class OverlayHeaderSlots(
    val headerIcon: (@Composable (Modifier) -> Unit)? = null,
    val titleIcon: (@Composable (Modifier) -> Unit)? = null,
    val subtitle: String? = null,
    val accessory: (@Composable () -> Unit)? = null,
    val leadingAction: (@Composable () -> Unit)? = null
)

internal class OverlayDismissState(
    val visibleState: MutableTransitionState<Boolean>,
    val internalVisible: Boolean,
    val triggerDismiss: () -> Unit
)

/** What drag-down and tap-outside run: [AureoleEditOverlayConfig.onDismiss], else back, else [fallback]. */
internal fun AureoleEditOverlayConfig.dismissHandler(fallback: () -> Unit): () -> Unit =
    onDismiss ?: onBackPress ?: fallback

/** What the back key runs: the screen's own [AureoleEditOverlayConfig.onBackPress], else [dismiss]. */
internal fun AureoleEditOverlayConfig.backHandler(dismiss: () -> Unit): () -> Unit = onBackPress ?: dismiss

/** What back, drag-down and tap-outside start: [dismiss], or nothing while [AureoleEditOverlayConfig.isLocked]. */
internal fun AureoleEditOverlayConfig.userDismiss(dismiss: () -> Unit): () -> Unit = if (isLocked) ({}) else dismiss

/** The title line of a sheet header: the [text], the optional [icon] before it and a [subtitle] under it. */
internal class OverlayHeaderTitle(
    val text: String,
    val icon: (@Composable (Modifier) -> Unit)? = null,
    val subtitle: String? = null
)
