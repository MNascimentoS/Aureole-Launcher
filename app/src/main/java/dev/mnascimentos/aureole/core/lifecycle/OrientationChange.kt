package dev.mnascimentos.aureole.core.lifecycle

import android.content.res.Configuration

fun isLandscapeOrientation(orientation: Int): Boolean = orientation == Configuration.ORIENTATION_LANDSCAPE

/** The new landscape flag when the orientation really changed, or null when the grid needs no reload. */
fun landscapeChange(currentIsLandscape: Boolean, newOrientation: Int): Boolean? {
    if (newOrientation == Configuration.ORIENTATION_UNDEFINED) return null
    val target = isLandscapeOrientation(newOrientation)
    return target.takeIf { it != currentIsLandscape }
}
