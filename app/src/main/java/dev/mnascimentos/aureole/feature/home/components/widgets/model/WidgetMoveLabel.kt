package dev.mnascimentos.aureole.feature.home.components.widgets.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle

/** "Move <widget>" as one string, always in reading order: the verb muted and the widget name bold. */
fun moveLabelText(label: String, name: String, labelColor: Color, nameColor: Color): AnnotatedString =
    buildAnnotatedString {
        withStyle(SpanStyle(color = labelColor)) { append(label) }
        append(' ')
        withStyle(SpanStyle(color = nameColor, fontWeight = FontWeight.Bold)) { append(name) }
    }
