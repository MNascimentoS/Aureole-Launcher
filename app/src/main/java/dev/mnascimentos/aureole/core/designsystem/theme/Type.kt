package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.annotation.FontRes
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R

val IstokWebFontFamily = FontFamily(Font(R.font.istok_web_regular, FontWeight.Normal, FontStyle.Normal))

/** Fonte variável do google/fonts fixada em wght 400, pois o eixo padrão de algumas (Rubik, Space Grotesk) é Light. */
private fun variableRegular(@FontRes resId: Int): Font = Font(
    resId = resId,
    weight = FontWeight.Normal,
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(REGULAR_WEIGHT))
)

private const val REGULAR_WEIGHT = 400

val AvailableFonts = mapOf(
    "Istok Web" to IstokWebFontFamily,
    "Roboto" to FontFamily(variableRegular(R.font.roboto_regular)),
    "Roboto Mono" to FontFamily(variableRegular(R.font.roboto_mono_regular)),
    "Montserrat" to FontFamily(variableRegular(R.font.montserrat_regular)),
    "Andika" to FontFamily(Font(R.font.andika_regular, FontWeight.Normal, FontStyle.Normal)),
    "Rubik" to FontFamily(variableRegular(R.font.rubik_regular)),
    "Chelsea Market" to FontFamily(Font(R.font.chelsea_market_regular, FontWeight.Normal, FontStyle.Normal)),
    "Special Elite" to FontFamily(Font(R.font.special_elite_regular, FontWeight.Normal, FontStyle.Normal)),
    "Orbitron" to FontFamily(variableRegular(R.font.orbitron_regular)),
    "Space Grotesk" to FontFamily(variableRegular(R.font.space_grotesk_regular)),
    "Space Mono" to FontFamily(Font(R.font.space_mono_regular, FontWeight.Normal, FontStyle.Normal)),
    "Audiowide" to FontFamily(Font(R.font.audiowide_regular, FontWeight.Normal, FontStyle.Normal))
)
