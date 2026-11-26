package dev.mnascimentos.aureole.feature.home.model

import androidx.compose.ui.Modifier

/**
 * A vertical swipe the home takes from a scrollable that sits at its end: whether it may open now ([canOpen]) and the
 * [modifier] that listens for it (empty while the swipe is off, RF-02).
 */
class NestedSwipe(val canOpen: Boolean, val modifier: Modifier)
