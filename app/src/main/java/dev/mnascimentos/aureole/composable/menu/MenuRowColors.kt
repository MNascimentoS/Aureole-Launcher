package dev.mnascimentos.aureole.composable.menu

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

/** The one title color for every menu row: medium emphasis, or the error color for a destructive row. */
@Composable
@ReadOnlyComposable
fun menuRowTitleColor(isDestructive: Boolean): Color =
    if (isDestructive) AureoleDS.colors.error else AureoleDS.colors.onSurfaceMedium
