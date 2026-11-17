package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.model.SheetBounds

/** Most a sheet whose content already fits stretches above rest. */
private val SHEET_ELASTIC_LIMIT = 64.dp

/** Height of the short bounce of a sheet that cannot expand. */
private val SHEET_BOUNCE = 24.dp

/** Content up to this much taller than the rest height still counts as fitting. */
private val SHEET_FIT_SLACK = 8.dp

internal fun sheetBoundsFor(density: Density, screenHeightPx: Float, restFraction: Float, expandedFraction: Float) =
    with(density) {
        SheetBounds(
            restMaxPx = screenHeightPx * restFraction,
            expandedPx = screenHeightPx * expandedFraction,
            elasticLimitPx = SHEET_ELASTIC_LIMIT.toPx(),
            bouncePx = SHEET_BOUNCE.toPx(),
            fitSlackPx = SHEET_FIT_SLACK.toPx()
        )
    }
