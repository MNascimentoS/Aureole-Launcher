package dev.mnascimentos.aureole.feature.home.components.widgets

import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.toArgb
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleColors
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetPalette

/**
 * Returns the function that keeps one widget host view in step with the "Theme colors in widgets" setting and the
 * current palette. Call it from the host view's `update` block; each call site keeps its own override state.
 */
@Composable
fun rememberWidgetThemeColorsApplier(): (View) -> Unit {
    val enabled = LocalHomeUiState.current.isWidgetThemeColorsEnabled
    val palette = AureoleDS.colors.toWidgetPalette()
    val hadOverride = remember { booleanArrayOf(false) }
    return { view -> hadOverride[0] = applyWidgetThemeColors(view, enabled, palette, hadOverride[0]) }
}

private fun AureoleColors.toWidgetPalette() = WidgetPalette(
    accent1 = onSurfaceMedium.toArgb(),
    accent2 = onSurfaceLow.toArgb(),
    accent3 = outline.toArgb(),
    neutral1 = surface.toArgb(),
    neutral2 = surfaceVariant.toArgb()
)
