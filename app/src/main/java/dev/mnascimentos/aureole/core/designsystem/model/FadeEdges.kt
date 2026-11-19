package dev.mnascimentos.aureole.core.designsystem.model

/** Which edges of a scrollable area fade out: only an edge with content hidden behind it (RF-E3). */
data class FadeEdges(val top: Boolean, val bottom: Boolean)

/** Fade on top only when it can scroll back up, and at the bottom only when it can scroll further down. */
fun fadeEdgesFor(canScrollBackward: Boolean, canScrollForward: Boolean): FadeEdges =
    FadeEdges(top = canScrollBackward, bottom = canScrollForward)
