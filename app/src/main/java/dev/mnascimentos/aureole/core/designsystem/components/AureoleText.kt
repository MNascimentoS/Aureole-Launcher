package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * @param style Style configuration for the text, defaults to [AureoleDS.typography.text].
 */
@Suppress("LongParameterList") // Wrapper for Material3 Text component mirroring standard Text parameters
@Composable
fun AureoleText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    style: TextStyle = AureoleDS.typography.text,
) {
    Text(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        fontStyle = fontStyle,
        fontWeight = fontWeight,
        fontFamily = fontFamily,
        letterSpacing = letterSpacing,
        textDecoration = textDecoration,
        textAlign = textAlign,
        lineHeight = lineHeight,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        onTextLayout = onTextLayout,
        style = style,
    )
}

@AureolePreview
@Composable
fun AureoleTextPreview() {
    AureoleLauncherTheme {
        Column {
            AureoleText(
                text = "Title Medium Text",
                style = AureoleDS.typography.titleMedium,
                color = AureoleDS.colors.onSurfaceHigh
            )
            AureoleText(
                text = "Body Medium Text",
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceMedium
            )
            AureoleText(
                text = "Label Small Text",
                style = AureoleDS.typography.labelSmall,
                color = AureoleDS.colors.onSurfaceLow
            )
        }
    }
}
