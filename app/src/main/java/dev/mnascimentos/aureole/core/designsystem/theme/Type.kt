package dev.mnascimentos.aureole.core.designsystem.theme

import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_MONOSPACED
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_ROUNDED
import dev.mnascimentos.aureole.core.designsystem.theme.model.FONT_KEY_SERIF

val IstokWebFontFamily = FontFamily(Font(R.font.istok_web_regular, FontWeight.Normal, FontStyle.Normal))

/** Fonte variável do google/fonts fixada em wght 400, pois o eixo padrão de algumas (Rubik, Space Grotesk) é Light. */
private fun variableRegular(@FontRes resId: Int): Font = Font(
    resId = resId,
    weight = FontWeight.Normal,
    style = FontStyle.Normal,
    variationSettings = FontVariation.Settings(FontVariation.weight(REGULAR_WEIGHT))
)

private const val REGULAR_WEIGHT = 400

private fun staticRegular(@FontRes resId: Int): FontFamily =
    FontFamily(Font(resId, FontWeight.Normal, FontStyle.Normal))

private fun variableFamily(@FontRes resId: Int): FontFamily = FontFamily(variableRegular(resId))

/** The system rounded sans; shared by the font catalog and the clock. */
val RoundedFontFamily: FontFamily by lazy { FontFamily(Typeface.create("sans-serif-rounded", Typeface.NORMAL)) }

/** Family of every key of `AureoleFontCatalog`, in the same order. */
val AvailableFonts: Map<String, FontFamily> = linkedMapOf(
    "Istok Web" to IstokWebFontFamily,
    "Roboto" to variableFamily(R.font.roboto_regular),
    "Roboto Mono" to variableFamily(R.font.roboto_mono_regular),
    "Montserrat" to variableFamily(R.font.montserrat_regular),
    "Andika" to staticRegular(R.font.andika_regular),
    "Rubik" to variableFamily(R.font.rubik_regular),
    "Chelsea Market" to staticRegular(R.font.chelsea_market_regular),
    "Special Elite" to staticRegular(R.font.special_elite_regular),
    "Orbitron" to variableFamily(R.font.orbitron_regular),
    "Space Grotesk" to variableFamily(R.font.space_grotesk_regular),
    "Space Mono" to staticRegular(R.font.space_mono_regular),
    "Audiowide" to staticRegular(R.font.audiowide_regular),
    FONT_KEY_SERIF to FontFamily.Serif,
    FONT_KEY_MONOSPACED to FontFamily.Monospace,
    FONT_KEY_ROUNDED to RoundedFontFamily,
    "Poppins" to staticRegular(R.font.poppins_regular),
    "Outfit" to variableFamily(R.font.outfit_regular),
    "Manrope" to variableFamily(R.font.manrope_regular),
    "Lexend" to variableFamily(R.font.lexend_regular),
    "Quicksand" to variableFamily(R.font.quicksand_regular),
    "Nunito" to variableFamily(R.font.nunito_regular),
    "Josefin Sans" to variableFamily(R.font.josefin_sans_regular),
    "Bebas Neue" to staticRegular(R.font.bebas_neue_regular),
    "Righteous" to staticRegular(R.font.righteous_regular),
    "Michroma" to staticRegular(R.font.michroma_regular),
    "Playfair Display" to variableFamily(R.font.playfair_display_regular),
    "DM Serif Display" to staticRegular(R.font.dm_serif_display_regular),
    "Lora" to variableFamily(R.font.lora_regular),
    "Abril Fatface" to staticRegular(R.font.abril_fatface_regular),
    "JetBrains Mono" to variableFamily(R.font.jetbrains_mono_regular),
    "Share Tech Mono" to staticRegular(R.font.share_tech_mono_regular),
    "VT323" to staticRegular(R.font.vt323_regular)
)
