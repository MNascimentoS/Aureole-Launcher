package dev.mnascimentos.aureole.composable.sheets.model

import androidx.compose.ui.graphics.Color

/** What a palette chip writes: its [name], and, in the picker's color mode, the one [strip] color it shows. */
data class PaletteChipLabel(val name: String, val strip: Color? = null)
