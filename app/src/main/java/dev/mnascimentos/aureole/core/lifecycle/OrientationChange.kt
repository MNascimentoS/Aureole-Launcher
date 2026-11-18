package dev.mnascimentos.aureole.core.lifecycle

import android.content.res.Configuration

fun isLandscapeOrientation(orientation: Int): Boolean = orientation == Configuration.ORIENTATION_LANDSCAPE

/** The new landscape flag when the orientation really changed, or null when the grid needs no reload. */
fun landscapeChange(currentIsLandscape: Boolean, newOrientation: Int): Boolean? {
    if (newOrientation == Configuration.ORIENTATION_UNDEFINED) return null
    val target = isLandscapeOrientation(newOrientation)
    return target.takeIf { it != currentIsLandscape }
}

/**
 * The one source of the home orientation (RF-12): the current `Configuration`, checked when it changes and again when
 * the home comes back ([onStart], [onResume]), since a rotation done while the home was stopped (inside a video in
 * landscape, say) may never reach it as a change. It compares with the orientation it last asked the grid to
 * load, not with the UI state, which lags behind a load still in flight: comparing with the state is what let a
 * late landscape load win over the portrait the device was back in.
 *
 * @param load Loads the grid of that orientation (true = landscape).
 */
class OrientationSync(private val load: (isLandscape: Boolean) -> Unit) {
    var requested: Boolean? = null
        private set

    /** Loads the grid for [orientation] every time (the resume reloads it); an undefined one keeps the last. */
    fun reload(orientation: Int, current: Boolean) {
        val target = if (orientation == Configuration.ORIENTATION_UNDEFINED) {
            requested ?: current
        } else {
            isLandscapeOrientation(orientation)
        }
        requested = target
        load(target)
    }

    /** Loads only when [orientation] differs from the one last asked for ([current], the state, before any). */
    fun check(orientation: Int, current: Boolean) {
        val target = landscapeChange(requested ?: current, orientation) ?: return
        requested = target
        load(target)
    }
}
