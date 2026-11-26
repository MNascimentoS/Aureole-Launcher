package dev.mnascimentos.aureole.composable.color.model

import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef

/**
 * What a color row needs: [title] of the row and of the picker it opens, the saved color ([selected], null follows
 * the theme and shows [themeColor]), what the color paints ([role]) and where the choice goes ([onSelect]).
 */
data class ColorFieldParams(
    val title: String,
    val selected: ThemeColorRef?,
    val themeColor: Color,
    val role: ComponentColorRole,
    val onSelect: (ThemeColorRef?) -> Unit
)
