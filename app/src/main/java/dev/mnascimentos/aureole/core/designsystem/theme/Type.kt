package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R

val IstokWebFontFamily = FontFamily(Font(R.font.istok_web_regular, FontWeight.Normal, FontStyle.Normal))

val AvailableFonts = mapOf(
    "Istok Web" to IstokWebFontFamily,
    "Roboto" to FontFamily(Font(R.font.roboto_regular, FontWeight.Normal, FontStyle.Normal)),
    "Roboto Mono" to FontFamily(Font(R.font.roboto_mono_regular, FontWeight.Normal, FontStyle.Normal)),
    "Montserrat" to FontFamily(Font(R.font.montserrat_regular, FontWeight.Normal, FontStyle.Normal)),
    "Andika" to FontFamily(Font(R.font.andika_regular, FontWeight.Normal, FontStyle.Normal)),
    "Rubik" to FontFamily(Font(R.font.rubik_regular, FontWeight.Normal, FontStyle.Normal)),
    "Chelsea Market" to FontFamily(Font(R.font.chelsea_market_regular, FontWeight.Normal, FontStyle.Normal)),
    "Special Elite" to FontFamily(Font(R.font.special_elite_regular, FontWeight.Normal, FontStyle.Normal)),
    "Orbitron" to FontFamily(Font(R.font.orbitron_regular, FontWeight.Normal, FontStyle.Normal)),
    "Space Grotesk" to FontFamily(Font(R.font.space_grotesk_regular, FontWeight.Normal, FontStyle.Normal)),
    "Space Mono" to FontFamily(Font(R.font.space_mono_regular, FontWeight.Normal, FontStyle.Normal)),
    "Audiowide" to FontFamily(Font(R.font.audiowide_regular, FontWeight.Normal, FontStyle.Normal))
)
