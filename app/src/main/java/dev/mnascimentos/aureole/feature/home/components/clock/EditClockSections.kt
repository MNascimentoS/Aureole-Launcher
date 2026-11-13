package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockAlignmentOption
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

@Composable
internal fun EditClockStyleSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    tempCustomGreeting: String,
    onGreetingChange: (String) -> Unit
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_layout_style))
    EditClockStyleCards(
        selectedKey = uiState.clockStyle,
        onSelect = { actions.onUpdateClockStyle(it.key) }
    )

    if (uiState.clockStyle == "CUSTOM_GREETING") {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        OutlinedTextField(
            value = tempCustomGreeting,
            onValueChange = onGreetingChange,
            label = { AureoleText(stringResource(R.string.home_clock_greeting_text)) },
            placeholder = { AureoleText(stringResource(R.string.home_clock_greeting_placeholder)) },
            singleLine = true,
            shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun EditClockAlignmentSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_alignment))
    AureoleSegmentedControl(
        options = ClockAlignmentOption.entries.map { stringResource(it.labelRes) },
        selectedIndex = ClockAlignmentOption.indexOfKey(uiState.clockAlignment),
        onSelect = { actions.onUpdateClockAlignment(ClockAlignmentOption.entries[it].key) },
        itemHeight = AureoleDS.dimens.minTouchTarget
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockTextColorSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_text_color))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact)
    ) {
        PRESET_TEXT_COLORS.forEach { (nameRes, colorValue) ->
            EditClockColorSwatchItem(
                name = stringResource(nameRes),
                colorValue = colorValue,
                isSelected = uiState.clockTextColor == colorValue,
                onClick = { actions.onUpdateClockTextColor(colorValue) }
            )
        }
    }
}

@Composable
internal fun EditClockFontSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    nowMillis: Long = System.currentTimeMillis()
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_font_style))
    EditClockFontTiles(
        selectedKey = uiState.clockFontFamily,
        nowMillis = nowMillis,
        onSelect = { actions.onUpdateClockFontFamily(it) }
    )
}

@Composable
internal fun EditClockFormatSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    nowMillis: Long = System.currentTimeMillis()
) {
    Column {
        EditClockSectionHeader(title = stringResource(R.string.home_clock_time_date_format))
        FormatSubtitle(text = stringResource(R.string.home_clock_time_format))
        EditClockTimeTiles(
            selectedKey = uiState.clockTimeFormat,
            nowMillis = nowMillis,
            onSelect = { actions.onUpdateClockTimeFormat(it) }
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.smallCompact))
        FormatSubtitle(text = stringResource(R.string.home_clock_date_format_label))
        EditClockDateTiles(
            selectedKey = uiState.clockDateFormat,
            nowMillis = nowMillis,
            onSelect = { actions.onUpdateClockDateFormat(it) }
        )
    }
}

@Composable
private fun FormatSubtitle(text: String) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
        format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
    )
}

@AureolePreview
@Composable
private fun EditClockSectionsPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        ) {
            EditClockStyleSection(
                uiState = MainUiState(),
                actions = createDefaultHomeScreenActions(),
                tempCustomGreeting = "Bem-vindo",
                onGreetingChange = {}
            )
        }
    }
}
