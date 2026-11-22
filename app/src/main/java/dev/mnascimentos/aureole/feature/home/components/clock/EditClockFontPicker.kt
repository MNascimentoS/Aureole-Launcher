package dev.mnascimentos.aureole.feature.home.components.clock

import android.text.format.DateFormat
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.ClockHeaderUtils
import dev.mnascimentos.aureole.composable.clock.model.formatClockSample
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.model.ComponentFontOptions
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerSheetOptions
import dev.mnascimentos.aureole.core.data.model.resolveClockFont
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

/**
 * The launcher font picker opened by the clock editor (RF-B2 to B5): "Clock font", "Launcher font" first, the
 * time in [uiState]'s format written in each font and the bold switch. Every choice goes straight to [actions]
 * (auto-save, like the rest of the editor), so [preview] (the edited clock) shows it at once.
 */
@Composable
internal fun EditClockFontPicker(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    preview: (@Composable BoxScope.() -> Unit)? = null,
    nowMillis: Long = System.currentTimeMillis()
) {
    val font = resolveClockFont(uiState.clockFontFamily, uiState.clockFontBold)
    val (format12, format24) = ClockHeaderUtils.resolveClockTimeFormats(uiState.clockTimeFormat)
    val pattern = if (DateFormat.is24HourFormat(LocalContext.current)) format24 else format12
    val title = stringResource(R.string.home_clock_font_title)
    PickFontBottomSheet(
        selectedFontName = font.fontKey,
        onFontSelected = actions.onUpdateClockFontFamily,
        onDismissRequest = onDismissRequest,
        options = ThemePickerSheetOptions(
            componentFont = ComponentFontOptions(
                title = title,
                subtitle = stringResource(R.string.home_clock_font_subtitle),
                sectionLabel = title,
                sample = formatClockSample(pattern, LocalConfiguration.current.locales[0], nowMillis),
                isBold = font.isBold,
                onBoldChange = actions.onUpdateClockFontBold,
                preview = preview
            )
        )
    )
}

@AureolePreview
@Composable
private fun EditClockFontPickerPreview() {
    AureoleLauncherTheme {
        EditClockFontPicker(
            uiState = MainUiState(clockFontFamily = "Montserrat"),
            actions = createDefaultHomeScreenActions(),
            onDismissRequest = {},
            nowMillis = PREVIEW_NOW_MILLIS
        )
    }
}
