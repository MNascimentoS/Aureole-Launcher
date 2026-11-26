package dev.mnascimentos.aureole.composable.color.model

import androidx.core.graphics.ColorUtils

private const val LIGHT_LUMINANCE = 0.5f

/** True when [color] as a background makes a light theme (same rule as the seed theme builder). */
fun isLightBackground(color: Int): Boolean = ColorUtils.calculateLuminance(color) >= LIGHT_LUMINANCE
