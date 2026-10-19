package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * High level text component for Aureole Design System.
 *
 * It acts as a strict wrapper around [Text] forcing consumers
 * to use typography tokens provided by [AureoleDS.typography].
 *
 * @param text The text to be displayed.
 * @param modifier The modifier to be applied to the layout.
 * @param color Color to apply to the text.
 * @param format Grouped text formatting parameters.
 */
@NonRestartableComposable
@Composable
fun AureoleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    format: AureoleTextFormatParams = AureoleTextFormatParams(),
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = format.fontSize,
        fontStyle = format.fontStyle,
        fontWeight = format.fontWeight,
        fontFamily = format.fontFamily,
        letterSpacing = format.letterSpacing,
        textDecoration = format.textDecoration,
        textAlign = format.textAlign,
        lineHeight = format.lineHeight,
        overflow = format.overflow,
        softWrap = format.softWrap,
        maxLines = format.maxLines,
        minLines = format.minLines,
        onTextLayout = format.onTextLayout,
        style = format.style ?: AureoleDS.typography.text,
    )
}

/**
 * Grouped formatting parameters for [AureoleText].
 *
 * @property fontSize Font size to apply to the text.
 * @property fontStyle Font style (such as Italic) to apply to the text.
 * @property fontWeight Font weight (such as Bold or Medium) to apply to the text.
 * @property fontFamily Font family to use for rendering the text.
 * @property letterSpacing Amount of space to add between characters.
 * @property textDecoration Text decoration (such as Underline or LineThrough).
 * @property textAlign Alignment of text inside its layout bounds.
 * @property lineHeight Line height for multiline text layout.
 * @property overflow Visual overflow handling when text exceeds layout bounds.
 * @property softWrap Whether text should wrap soft line breaks.
 * @property maxLines Maximum number of lines to render before truncating.
 * @property minLines Minimum number of lines to reserve space for.
 * @property onTextLayout Callback invoked when text layout calculation completes.
 * @property style Base text style token from typography.
 */
data class AureoleTextFormatParams(
    val fontSize: TextUnit = TextUnit.Unspecified,
    val fontStyle: FontStyle? = null,
    val fontWeight: FontWeight? = null,
    val fontFamily: FontFamily? = null,
    val letterSpacing: TextUnit = TextUnit.Unspecified,
    val textDecoration: TextDecoration? = null,
    val textAlign: TextAlign? = null,
    val lineHeight: TextUnit = TextUnit.Unspecified,
    val overflow: TextOverflow = TextOverflow.Clip,
    val softWrap: Boolean = true,
    val maxLines: Int = Int.MAX_VALUE,
    val minLines: Int = 1,
    val onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    val style: TextStyle? = null,
)

@AureolePreview
@Composable
private fun AureoleTextPreview() {
    AureoleLauncherTheme {
        Column {
            AureoleText("Sample Text Body")
            AureoleText(
                text = "Sample Display Small",
                format = AureoleTextFormatParams(style = AureoleDS.typography.displaySmall)
            )
            AureoleText(
                text = "Sample Title Large",
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleLarge)
            )
        }
    }
}
