package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.controls.AureoleSegmentedControl
import dev.mnascimentos.aureole.core.data.model.ClockMessage
import dev.mnascimentos.aureole.core.data.model.resolveClockLayout
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockAlignmentOption
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockMessageOption
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockMetrics
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
    val choice = resolveClockLayout(uiState.clockStyle, uiState.clockMessage)
    EditClockSectionHeader(title = stringResource(R.string.home_clock_layout))
    EditClockStyleCards(
        selectedKey = choice.layout.key,
        onSelect = { actions.onUpdateClockStyle(it.key) }
    )
    if (!choice.layout.showsMessage) return

    Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    EditClockSectionHeader(title = stringResource(R.string.home_clock_message))
    AureoleSegmentedControl(
        options = ClockMessageOption.entries.map { stringResource(it.labelRes) },
        selectedIndex = ClockMessageOption.indexOfKey(choice.message.key),
        onSelect = { actions.onUpdateClockMessage(ClockMessageOption.entries[it].message.key) }
    )
    if (choice.message == ClockMessage.CUSTOM) {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small + AureoleDS.dimens.xSmall))
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
        onSelect = { actions.onUpdateClockAlignment(ClockAlignmentOption.entries[it].key) }
    )
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
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
        EditClockDateSubsection(
            showsDate = resolveClockLayout(uiState.clockStyle, uiState.clockMessage).layout.showsDate,
            selectedKey = uiState.clockDateFormat,
            nowMillis = nowMillis,
            onSelect = { actions.onUpdateClockDateFormat(it) }
        )
    }
}

/**
 * Format › Date. With a layout that has no date (D-14) it stays on screen but disabled: dimmed, not tappable and
 * exposed as disabled, with a notice; the saved date format is kept.
 */
@Composable
private fun EditClockDateSubsection(
    showsDate: Boolean,
    selectedKey: String,
    nowMillis: Long,
    onSelect: (String) -> Unit
) {
    val dimmed = Modifier.alpha(if (showsDate) 1f else EditClockMetrics.DISABLED_ALPHA)
    Column {
        FormatSubtitle(text = stringResource(R.string.home_clock_date_format_label), modifier = dimmed)
        if (!showsDate) EditClockNoDateNotice()
        EditClockDateTiles(
            selectedKey = selectedKey,
            nowMillis = nowMillis,
            onSelect = onSelect,
            enabled = showsDate,
            modifier = dimmed
        )
    }
}

@Composable
private fun EditClockNoDateNotice() {
    Row(
        modifier = Modifier.padding(bottom = AureoleDS.dimens.small),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        AureoleDS.icons.Info(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(AureoleDS.dimens.iconSmall)
        )
        AureoleText(
            text = stringResource(R.string.home_clock_format_no_date),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
    }
}

/**
 * Sub-label of the format options ("Time", "Date"): shares the section title's start inset,
 * so both sit on one line.
 */
@Composable
private fun FormatSubtitle(text: String, modifier: Modifier = Modifier) {
    AureoleText(
        text = text,
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = modifier.padding(
            bottom = AureoleDS.dimens.small
        ),
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
