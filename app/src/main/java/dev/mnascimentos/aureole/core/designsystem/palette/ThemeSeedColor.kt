package dev.mnascimentos.aureole.core.designsystem.palette

import androidx.compose.ui.graphics.toArgb

/** Seed color stored alongside a named palette, shared by every screen that lets the user pick one. */
fun resolveThemeSeedColor(themeName: String): Int = getPaletteByName(themeName).colors.surfaceVariant.toArgb()
