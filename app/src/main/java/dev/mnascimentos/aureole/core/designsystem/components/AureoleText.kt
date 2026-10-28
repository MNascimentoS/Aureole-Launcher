package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import dev.mnascimentos.aureole.core.designsystem.model.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

typealias AureoleTextFormatParams = AureoleTextFormatParams

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
 * [AureoleText] for styled text, such as strings with bold spans built with `AnnotatedString.fromHtml`.
 *
 * @param text The styled text to be displayed.
 * @param modifier The modifier to be applied to the layout.
 * @param color Color to apply to the text.
 * @param format Grouped text formatting parameters.
 */
@NonRestartableComposable
@Composable
fun AureoleText(
    text: AnnotatedString,
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
        onTextLayout = format.onTextLayout ?: {},
        style = format.style ?: AureoleDS.typography.text,
    )
}

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
