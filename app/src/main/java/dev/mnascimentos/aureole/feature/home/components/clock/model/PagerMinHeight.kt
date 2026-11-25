package dev.mnascimentos.aureole.feature.home.components.clock.model

/**
 * Minimum height of the clock pager: the tallest page at rest, so switching tabs does not move the footer,
 * and none when expanded, so the content follows the tab and the divider stays right below it.
 */
fun pagerMinHeight(isExpanded: Boolean, tallestPx: Int): Int =
    if (isExpanded) 0 else tallestPx.coerceAtLeast(0)

/**
 * The tallest page seen since the editor opened: it only grows, so a page that gets shorter (a section that
 * collapses, the color page coming back) never shrinks the sheet and the preview above it stays put for the whole
 * session (onboarding-redesenho RF-C2).
 */
fun sessionTallestPage(previousPx: Int, pageHeights: List<Int>): Int =
    maxOf(previousPx, pageHeights.maxOrNull() ?: 0, 0)
