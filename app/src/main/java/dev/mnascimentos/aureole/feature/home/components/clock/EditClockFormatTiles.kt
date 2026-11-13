package dev.mnascimentos.aureole.feature.home.components.clock

import android.text.format.DateFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.composable.clock.ClockHeaderUtils
import dev.mnascimentos.aureole.composable.clock.model.ClockDateFormat
import dev.mnascimentos.aureole.composable.clock.model.ClockTimeFormat
import dev.mnascimentos.aureole.composable.clock.model.formatClockSample
import dev.mnascimentos.aureole.composable.optiontile.AureoleOptionTile
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_DATE_COLUMNS
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_TIME_COLUMNS
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockDateOption
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTimeOption

private const val TIME_SAMPLE_PATTERN_12H = "h:mm a"

/** Hour picker: System, 12 h and 24 h, each showing [nowMillis] written that way. */
@Composable
internal fun EditClockTimeTiles(
    selectedKey: String,
    nowMillis: Long,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val is24Hour = DateFormat.is24HourFormat(LocalContext.current)
    val selected = ClockTimeFormat.fromKey(selectedKey)
    TileGrid(
        modifier = modifier,
        columns = CLOCK_TIME_COLUMNS,
        items = ClockTimeOption.entries
    ) { option, tileModifier ->
        val (_, format24) = ClockHeaderUtils.resolveClockTimeFormats(option.format.key)
        val uses12Hour = option.format == ClockTimeFormat.H12 || (option.format == ClockTimeFormat.SYSTEM && !is24Hour)
        val pattern = if (uses12Hour) TIME_SAMPLE_PATTERN_12H else format24
        AureoleOptionTile(
            sample = { TileSample(formatClockSample(pattern, locale, nowMillis)) },
            label = stringResource(option.labelRes),
            selected = option.format == selected,
            onClick = { onSelect(option.format.key) },
            modifier = tileModifier
        )
    }
}

/** Date picker: Full, Short, Medium and Numeric, each showing [nowMillis] with the pattern the home clock uses. */
@Composable
internal fun EditClockDateTiles(
    selectedKey: String,
    nowMillis: Long,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val locale = LocalConfiguration.current.locales[0]
    val selected = ClockDateFormat.fromKey(selectedKey)
    TileGrid(
        modifier = modifier,
        columns = CLOCK_DATE_COLUMNS,
        items = ClockDateOption.entries
    ) { option, tileModifier ->
        val pattern = ClockHeaderUtils.resolveClockDatePattern(option.format.storageKey, locale)
        AureoleOptionTile(
            sample = { TileSample(formatClockSample(pattern, locale, nowMillis)) },
            label = stringResource(option.labelRes),
            selected = option.format == selected,
            onClick = { onSelect(option.format.storageKey) },
            modifier = tileModifier
        )
    }
}

@Composable
private fun TileSample(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceHigh,
        format = AureoleTextFormatParams(
            style = AureoleDS.typography.bodyMedium,
            textAlign = TextAlign.Start,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    )
}

@Composable
internal fun <T> TileGrid(
    columns: Int,
    items: List<T>,
    modifier: Modifier = Modifier,
    tile: @Composable (T, Modifier) -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .selectableGroup(),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        items.chunked(columns).forEach { row ->
            Row(
                modifier = Modifier.height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
            ) {
                row.forEach { item -> tile(item, Modifier.weight(1f).fillMaxHeight()) }
                repeat(columns - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@AureolePreview
@Composable
private fun EditClockFormatTilesPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier.background(AureoleDS.colors.surface).padding(AureoleDS.dimens.medium),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            EditClockTimeTiles(selectedKey = "24H", nowMillis = PREVIEW_NOW_MILLIS, onSelect = {})
            EditClockDateTiles(selectedKey = "DEFAULT", nowMillis = PREVIEW_NOW_MILLIS, onSelect = {})
        }
    }
}

internal const val PREVIEW_NOW_MILLIS = 1_794_000_000_000L
