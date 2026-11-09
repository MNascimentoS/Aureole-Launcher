package dev.mnascimentos.aureole.composable.appitem.model

// Label widths step through LABEL_WIDTH_STEPS sizes out of order, so the rows do not look stamped.
private const val LABEL_MIN_WIDTH = 0.4f
private const val LABEL_WIDTH_STEP = 0.05f
private const val LABEL_WIDTH_STEPS = 7

object AppListSkeletonMetrics {
    /** Width fraction of the label bar on skeleton row [index]. */
    fun labelWidth(index: Int): Float = LABEL_MIN_WIDTH + ((index * 2 + 1) % LABEL_WIDTH_STEPS) * LABEL_WIDTH_STEP
}
