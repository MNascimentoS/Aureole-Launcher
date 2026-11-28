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

/** Open foldable (inner screen, e.g. Pixel Fold) lying down: counts as landscape in the screenshot contract. */
@Preview(
    name = "Dark Mode Fold Landscape",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF12181D,
    widthDp = FOLD_PREVIEW_LONG_DP,
    heightDp = FOLD_PREVIEW_SHORT_DP
)
annotation class AureoleGeneratedFoldLandscapePreview

typealias AureoleFoldLandscapePreview = AureoleGeneratedFoldLandscapePreview

/** Open foldable (inner screen) standing up: wider than a phone, so not landscape. */
@Preview(
    name = "Dark Mode Fold",
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    showBackground = true,
    backgroundColor = 0xFF12181D,
    widthDp = FOLD_PREVIEW_SHORT_DP,
    heightDp = FOLD_PREVIEW_LONG_DP
)
annotation class AureoleGeneratedFoldPreview

typealias AureoleFoldPreview = AureoleGeneratedFoldPreview

private const val LANDSCAPE_PREVIEW_WIDTH_DP = 891
private const val LANDSCAPE_PREVIEW_HEIGHT_DP = 411
private const val CATALOG_PREVIEW_WIDTH_DP = 411
private const val CATALOG_PREVIEW_HEIGHT_DP = 2200
private const val FOLD_PREVIEW_LONG_DP = 841
private const val FOLD_PREVIEW_SHORT_DP = 701
