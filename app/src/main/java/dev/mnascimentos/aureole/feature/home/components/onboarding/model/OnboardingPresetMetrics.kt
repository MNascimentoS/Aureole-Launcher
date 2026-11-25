package dev.mnascimentos.aureole.feature.home.components.onboarding.model

import androidx.compose.ui.unit.dp

/** Sizes of the presets screen (RF-F1) that no layout token covers. */
object OnboardingPresetMetrics {
    /** Width of the mini home on each card; the height follows the portrait grid (20 x 30). */
    val THUMBNAIL_WIDTH = 64.dp

    /** Gap between two items of the mini home, and its inner margin. */
    val THUMBNAIL_GAP = 2.dp

    const val THUMBNAIL_ITEM_ALPHA = 0.22f
    const val THUMBNAIL_DETAIL_ALPHA = 0.5f
    const val CARD_SURFACE_ALPHA = 0.6f
    const val BACKDROP_ALPHA = 0.92f
}
