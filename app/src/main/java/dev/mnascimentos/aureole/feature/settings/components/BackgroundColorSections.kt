package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorField
import dev.mnascimentos.aureole.composable.color.AureoleColorSwatch
import dev.mnascimentos.aureole.composable.color.AureoleHueSlider
import dev.mnascimentos.aureole.composable.color.model.ColorFieldParams
import dev.mnascimentos.aureole.composable.color.model.hueOf
import dev.mnascimentos.aureole.composable.color.model.isLightBackground
import dev.mnascimentos.aureole.composable.color.model.toneRamp
import dev.mnascimentos.aureole.composable.sheets.edgeToEdgeRow
import dev.mnascimentos.aureole.composable.sheets.edgeToEdgeRowPadding
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.composable.sheets.sheetContentInset
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Solid color choices of the background sheet, in order: the shared color row (card-grid picker, RF-07 of
 * telas-ajustesfundo) and a ramp of seven tones of one hue with a slider to move it. Each label has the space of
 * [SheetSectionHeading] above it. A short note shows when the chosen color makes a light theme.
 */
@Composable
internal fun BackgroundColorSections(
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val themeBackground = AureoleDS.colors.background.toArgb()
    Column(modifier = modifier.fillMaxWidth()) {
        AureoleColorField(
            params = ColorFieldParams(
                title = stringResource(R.string.settings_background_color),
                selected = ThemeColorRef.Fixed(selected),
                themeColor = AureoleDS.colors.background,
                role = ComponentColorRole.CARD,
                onSelect = { onSelect((it as? ThemeColorRef.Fixed)?.argb ?: themeBackground) }
            )
        )
        SheetSectionHeading(text = stringResource(R.string.settings_background_section_tones))
        ToneRamp(selected = selected, onSelect = onSelect)
        if (isLightBackground(selected)) {
            AureoleText(
                text = stringResource(R.string.settings_background_light_warning),
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall),
                modifier = Modifier.padding(top = AureoleDS.dimens.small)
            )
        }
    }
}

@Composable
private fun ToneRamp(selected: Int, onSelect: (Int) -> Unit) {
    var hue by remember { mutableFloatStateOf(hueOf(selected)) }
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)) {
        val inset = sheetContentInset()
        LazyRow(
            modifier = Modifier.fillMaxWidth().edgeToEdgeRow(inset),
            contentPadding = edgeToEdgeRowPadding(inset),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            items(items = toneRamp(hue)) { tone ->
                AureoleColorSwatch(colorInt = tone, isSelected = selected == tone, onSelect = { onSelect(tone) })
            }
        }
        AureoleHueSlider(hue = hue, onHueChange = { hue = it })
    }
}

private const val PREVIEW_COLOR = 0xFF2B373E.toInt()

@AureolePreview
@Composable
private fun BackgroundColorSectionsPreview() {
    AureoleLauncherTheme {
        BackgroundColorSections(selected = PREVIEW_COLOR, onSelect = {})
    }
}
