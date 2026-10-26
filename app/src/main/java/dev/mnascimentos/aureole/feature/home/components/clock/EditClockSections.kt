@file:Suppress("MagicNumber")

package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

private object ClockSectionMetrics {
    val NegativeChipVerticalSpacing = (-8).dp
}

@Composable
@ReadOnlyComposable
private fun getClockStyleOptions(): List<Pair<String, String>> = listOf(
    Pair("DYNAMIC_GREETING", stringResource(R.string.home_clock_daily_greeting)),
    Pair("DATE_ON_TOP", stringResource(R.string.home_clock_date_on_top)),
    Pair("GREETING_AND_DATE", stringResource(R.string.home_clock_greeting_and_date)),
    Pair("CUSTOM_GREETING", stringResource(R.string.home_clock_custom_text)),
    Pair("TIME_ONLY", stringResource(R.string.home_clock_time_only))
)

@Composable
@ReadOnlyComposable
private fun getDateFormatOptions(): List<Pair<String, String>> = listOf(
    Pair("DEFAULT", stringResource(R.string.home_clock_date_default)),
    Pair("SHORT", stringResource(R.string.home_clock_date_short)),
    Pair("MEDIUM", stringResource(R.string.home_clock_date_medium)),
    Pair("NUMERIC", stringResource(R.string.home_clock_date_numeric))
)

@Composable
internal fun EditClockStyleSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    tempCustomGreeting: String,
    onGreetingChange: (String) -> Unit
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_layout_style))
    EditClockDropdown(
        label = null,
        selectedKey = uiState.clockStyle,
        options = getClockStyleOptions(),
        onOptionSelected = { actions.onUpdateClockStyle(it) }
    )

    if (uiState.clockStyle == "CUSTOM_GREETING") {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        OutlinedTextField(
            value = tempCustomGreeting,
            onValueChange = onGreetingChange,
            label = { AureoleText(stringResource(R.string.home_clock_greeting_text)) },
            placeholder = { AureoleText(stringResource(R.string.home_clock_greeting_placeholder)) },
            singleLine = true,
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
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        EditClockChoiceButton(
            text = stringResource(R.string.home_clock_align_left),
            selected = uiState.clockAlignment == "START",
            onClick = { actions.onUpdateClockAlignment("START") },
            modifier = Modifier.weight(1f)
        )
        EditClockChoiceButton(
            text = stringResource(R.string.home_clock_align_center),
            selected = uiState.clockAlignment == "CENTER",
            onClick = { actions.onUpdateClockAlignment("CENTER") },
            modifier = Modifier.weight(1f)
        )
        EditClockChoiceButton(
            text = stringResource(R.string.home_clock_align_right),
            selected = uiState.clockAlignment == "END",
            onClick = { actions.onUpdateClockAlignment("END") },
            modifier = Modifier.weight(1f)
        )
    }
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
        PRESET_TEXT_COLORS.forEach { (name, colorValue) ->
            EditClockColorSwatchItem(
                name = name,
                colorValue = colorValue,
                isSelected = uiState.clockTextColor == colorValue,
                onClick = { actions.onUpdateClockTextColor(colorValue) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockFontSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = stringResource(R.string.home_clock_font_style))
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(ClockSectionMetrics.NegativeChipVerticalSpacing)
    ) {
        EditClockLayoutChip(
            label = stringResource(R.string.home_clock_font_default),
            selected = uiState.clockFontFamily == "SANS_SERIF",
            onClick = { actions.onUpdateClockFontFamily("SANS_SERIF") }
        )
        EditClockLayoutChip(
            label = stringResource(R.string.home_clock_font_bold),
            selected = uiState.clockFontFamily == "BOLD",
            onClick = { actions.onUpdateClockFontFamily("BOLD") }
        )
        EditClockLayoutChip(
            label = stringResource(R.string.home_clock_font_serif),
            selected = uiState.clockFontFamily == "SERIF",
            onClick = { actions.onUpdateClockFontFamily("SERIF") }
        )
        EditClockLayoutChip(
            label = stringResource(R.string.home_clock_font_monospaced),
            selected = uiState.clockFontFamily == "MONOSPACE",
            onClick = { actions.onUpdateClockFontFamily("MONOSPACE") }
        )
        EditClockLayoutChip(
            label = stringResource(R.string.home_clock_font_rounded),
            selected = uiState.clockFontFamily == "ROUNDED",
            onClick = { actions.onUpdateClockFontFamily("ROUNDED") }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockFormatSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    Column {
        EditClockSectionHeader(title = stringResource(R.string.home_clock_time_date_format))

        AureoleText(
            text = stringResource(R.string.home_clock_time_format),
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            EditClockChoiceButton(
                text = stringResource(R.string.home_clock_time_system),
                selected = uiState.clockTimeFormat == "SYSTEM",
                onClick = { actions.onUpdateClockTimeFormat("SYSTEM") },
                modifier = Modifier.weight(1f)
            )
            EditClockChoiceButton(
                text = stringResource(R.string.home_clock_time_12h),
                selected = uiState.clockTimeFormat == "12H",
                onClick = { actions.onUpdateClockTimeFormat("12H") },
                modifier = Modifier.weight(1f)
            )
            EditClockChoiceButton(
                text = stringResource(R.string.home_clock_time_24h),
                selected = uiState.clockTimeFormat == "24H",
                onClick = { actions.onUpdateClockTimeFormat("24H") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.smallCompact))

        EditClockDropdown(
            label = stringResource(R.string.home_clock_date_format_label),
            selectedKey = uiState.clockDateFormat,
            options = getDateFormatOptions(),
            onOptionSelected = { actions.onUpdateClockDateFormat(it) }
        )
    }
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

@AureolePreview
@Composable
private fun EditClockDropdownOpenPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        ) {
            EditClockDropdown(
                label = "Estilo do Layout",
                selectedKey = "DATE_ON_TOP",
                options = getClockStyleOptions(),
                onOptionSelected = {},
                initialExpanded = true
            )
        }
    }
}
