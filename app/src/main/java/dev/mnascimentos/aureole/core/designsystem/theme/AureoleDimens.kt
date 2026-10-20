package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Design system dimension tokens for Aureole Launcher based on Figma rules.
 */
@Immutable
data class AureoleDimens(
    // 01. Principais e Composição (Spacings & Radii)
    val zero: Dp = 0.dp,
    val minimal: Dp = 2.dp,
    val xxSmall: Dp = 4.dp,
    val appRowPaddingVertical: Dp = 6.dp,
    val xSmall: Dp = 8.dp,
    val smallCompact: Dp = 10.dp,
    val small: Dp = 12.dp,
    val favoriteSectionPaddingVertical: Dp = 14.dp,
    val medium: Dp = 16.dp,
    val folderGridGap: Dp = 18.dp,
    val large: Dp = 24.dp,
    val settingsPaddingHorizontal: Dp = 28.dp,
    val xLarge: Dp = 32.dp,
    val xxLarge: Dp = 40.dp,
    val minTouchTarget: Dp = 48.dp,

    // 02. Ícones
    val iconXSmall: Dp = 12.dp,
    val iconSmallCompact: Dp = 14.dp,
    val iconSmall: Dp = 16.dp,
    val iconMediumSmall: Dp = 18.dp,
    val iconMedium: Dp = 20.dp,
    val iconLargePlus: Dp = 22.dp,
    val iconLarge: Dp = 24.dp,
    val iconXLargeSmall: Dp = 26.dp,
    val iconXLargeMedium: Dp = 28.dp,
    val iconXLarge: Dp = 36.dp,
    val iconHuge: Dp = 40.dp,

    // 03. Radius and Borders
    val borderMinimal: Dp = 1.dp,
    val borderMax: Dp = 3.dp,
    val radiusMinimal: Dp = 4.dp,
    val radiusXSmall: Dp = 8.dp,
    val radiusSmallCompact: Dp = 10.dp,
    val radiusSmall: Dp = 12.dp,
    val radiusMedium: Dp = 16.dp,
    val radiusMediumLarge: Dp = 20.dp,
    val radiusLarge: Dp = 24.dp,
    val radiusFull: Dp = 100.dp,

    // 04. Containers & Controls
    val badgeElevation: Dp = 6.dp,
    val favoriteRowPaddingVertical: Dp = 10.dp,
    val scrubberLabelOffset: Dp = 14.dp,
    val popupHazeBlurRadius: Dp = 20.dp,
    val scrubberBadgeMargin: Dp = 28.dp,
    val folderButtonSmall: Dp = 52.dp,
    val folderLabelMaxWidth: Dp = 56.dp,
    val scrubberBulgeMax: Dp = 56.dp,
    val folderButtonMedium: Dp = 60.dp,
    val scrollChildRowHeight: Dp = 60.dp,
    val folderContainerMaxWidth: Dp = 68.dp,
    val scrollChildColWidth: Dp = 70.dp,
    val scrollChildMinHeight: Dp = 70.dp,
    val scrubberSpread: Dp = 80.dp,
    val widgetSearchTopPaddingExpanded: Dp = 80.dp,
    val scrollChildMinWidth: Dp = 100.dp,
    val scrubberPaddingBottom: Dp = 112.dp,
    val previewContainerMax: Dp = 120.dp,
    val widgetListBottomPadding: Dp = 120.dp,
    val widgetPreviewCardSize: Dp = 150.dp,
    val widgetAddCardSize: Dp = 164.dp,
    val popupWidthMin: Dp = 180.dp,
    val popupWidthMax: Dp = 210.dp,
    val popupHeightMax: Dp = 400.dp,
)

val LocalAureoleDimens = staticCompositionLocalOf { AureoleDimens() }
