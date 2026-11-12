package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.composable.clock.fontFamily
import dev.mnascimentos.aureole.composable.clock.model.ClockFontSpec
import dev.mnascimentos.aureole.composable.clock.model.formatClockSample
import dev.mnascimentos.aureole.composable.optiontile.AureoleOptionTile
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_FONT_COLUMNS
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockFontOption

private const val FONT_SAMPLE_PATTERN = "HH:mm"

/** Font picker: each tile shows [nowMillis] written in its own font, with the name below. */
@Composable
internal fun EditClockFontTiles(
    selectedKey: String,
    nowMillis: Long,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val selected = ClockFontSpec.fromKey(selectedKey)
    val sample = formatClockSample(FONT_SAMPLE_PATTERN, locale, nowMillis)
    TileGrid(
        modifier = modifier,
        columns = CLOCK_FONT_COLUMNS,
        items = ClockFontOption.entries
    ) { option, tileModifier ->
        val family = option.font.fontFamily()
        AureoleOptionTile(
            sample = {
                AureoleText(
                    text = sample,
                    color = AureoleDS.colors.onSurfaceHigh,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyLarge,
                        fontFamily = family,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )
            },
            label = stringResource(option.labelRes),
            selected = option.font == selected,
            onClick = { onSelect(option.font.storageKey) },
            modifier = tileModifier
        )
    }
}

@AureolePreview
@Composable
private fun EditClockFontTilesPreview() {
    AureoleLauncherTheme {
        EditClockFontTiles(
            selectedKey = "SANS_SERIF",
            nowMillis = PREVIEW_NOW_MILLIS,
            onSelect = {},
            modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium)
        )
    }
}
