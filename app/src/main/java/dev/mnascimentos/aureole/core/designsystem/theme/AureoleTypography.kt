package dev.mnascimentos.aureole.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Immutable
data class AureoleTypography(
    val fontFamily: FontFamily = IstokWebFontFamily,

    // Aureole DS custom styles from Figma
    val hugeTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 20.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.4).sp
    ),
    val midTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        letterSpacing = 0.sp
    ),
    val smallTitle: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        letterSpacing = 0.15.sp
    ),
    val text: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 22.4.sp,
        letterSpacing = 0.sp
    ),
    val smallText: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.8.sp,
        letterSpacing = 0.sp
    ),

    // Fallbacks for Material3 interoperability mapped directly to Aureole DS tokens
    val displayLarge: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    val displayMedium: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    val displaySmall: TextStyle = TextStyle(
        fontFamily = fontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    val titleLarge: TextStyle = hugeTitle,
    val titleMedium: TextStyle = midTitle,
    val titleSmall: TextStyle = smallTitle,
    val bodyLarge: TextStyle = text,
    val bodyMedium: TextStyle = text,
    val bodySmall: TextStyle = smallText,
    val labelLarge: TextStyle = midTitle,
    val labelMedium: TextStyle = smallTitle,
    val labelSmall: TextStyle = smallText

)

val LocalAureoleTypography = staticCompositionLocalOf { AureoleTypography() }

// Mapping custom DS to Material3 Typography in case standard Material components are used
fun AureoleTypography.toMaterialTypography(): Typography = Typography(
    displayLarge = this.displayLarge,
    displayMedium = this.displayMedium,
    displaySmall = this.displaySmall,
    titleLarge = this.titleLarge,
    titleMedium = this.titleMedium,
    titleSmall = this.titleSmall,
    bodyLarge = this.bodyLarge,
    bodyMedium = this.bodyMedium,
    bodySmall = this.bodySmall,
    labelLarge = this.labelLarge,
    labelMedium = this.labelMedium,
    labelSmall = this.labelSmall
)
