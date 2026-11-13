package dev.mnascimentos.aureole.feature.home.components.clock.model

/**
 * Minimum height of the clock pager: the tallest page at rest, so switching tabs does not move the footer,
 * and none when expanded, so the content follows the tab and the divider stays right below it.
 */
fun pagerMinHeight(isExpanded: Boolean, tallestPx: Int): Int =
    if (isExpanded) 0 else tallestPx.coerceAtLeast(0)
