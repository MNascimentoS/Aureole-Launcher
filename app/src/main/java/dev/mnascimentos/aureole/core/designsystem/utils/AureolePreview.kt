package dev.mnascimentos.aureole.core.designsystem.utils

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF12181D
)
annotation class AureoleGeneratedPreview

typealias AureolePreview = AureoleGeneratedPreview

@Preview(
    name = "Dark Mode Landscape",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF12181D,
    widthDp = LANDSCAPE_PREVIEW_WIDTH_DP,
    heightDp = LANDSCAPE_PREVIEW_HEIGHT_DP
)
annotation class AureoleGeneratedLandscapePreview

typealias AureoleLandscapePreview = AureoleGeneratedLandscapePreview

/** Tall preview of a design system catalog group: all variants of the group in one column. */
@Preview(
    name = "Dark Mode",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF12181D,
    widthDp = CATALOG_PREVIEW_WIDTH_DP,
    heightDp = CATALOG_PREVIEW_HEIGHT_DP
)
annotation class AureoleGeneratedCatalogPreview

typealias AureoleCatalogPreview = AureoleGeneratedCatalogPreview

private const val LANDSCAPE_PREVIEW_WIDTH_DP = 891
private const val LANDSCAPE_PREVIEW_HEIGHT_DP = 411
private const val CATALOG_PREVIEW_WIDTH_DP = 411
private const val CATALOG_PREVIEW_HEIGHT_DP = 2200
