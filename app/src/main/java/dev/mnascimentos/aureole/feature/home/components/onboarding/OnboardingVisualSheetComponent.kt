package dev.mnascimentos.aureole.feature.home.components.onboarding

import android.text.format.DateFormat
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.clock.ClockHeaderUtils
import dev.mnascimentos.aureole.composable.clock.model.formatClockSample
import dev.mnascimentos.aureole.composable.controls.AureoleSheetTabBar
import dev.mnascimentos.aureole.composable.minihome.model.MiniHomeContent
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.composable.sheets.AureoleSheetSavedHint
import dev.mnascimentos.aureole.composable.sheets.FontPickerGrid
import dev.mnascimentos.aureole.composable.sheets.PalettePickerGrid
import dev.mnascimentos.aureole.composable.sheets.ThemePreviewOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewOverlayParams
import dev.mnascimentos.aureole.composable.sheets.model.ThemePreviewState
import dev.mnascimentos.aureole.composable.sheets.model.basePaletteName
import dev.mnascimentos.aureole.composable.sheets.model.discard
import dev.mnascimentos.aureole.composable.sheets.model.fontNameOf
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.visualPreviewTime
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.VisualChoice
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.tabLabelRes

/**
 * The Visual step (onboarding-redesenho RF-C1, D-05): one sheet with the tabs Palette | Font fixed at the bottom
 * and no button. The mini-home above shows the real time and date, written as the user's clock writes them (D-06).
 * A tap repaints the mini-home and the sheet at once; closing the sheet, whichever way, saves what was tapped and
 * completes the step ([OnboardingActions.onCloseVisual]). [picker] is the tab it opens on; null shows nothing.
 */
@Composable
fun OnboardingVisualSheetComponent(
    picker: AppearancePicker?,
    selection: AppearanceSelection,
    actions: OnboardingActions,
    modifier: Modifier = Modifier,
    nowMillis: Long = remember { System.currentTimeMillis() }
) {
    if (picker == null) return
    var tab by rememberSaveable { mutableStateOf(picker) }
    var pending by remember { mutableStateOf(ThemePreviewState()) }
    val base = AureoleThemeConfig(
        darkTheme = isSystemInDarkTheme(),
        aureoleColors = AureoleDS.colors,
        aureoleFontName = fontNameOf(AureoleDS.typography.fontFamily),
        aureoleIcons = AureoleDS.icons,
        aureoleDimens = AureoleDS.dimens
    )
    ThemePreviewOverlay(
        params = ThemePreviewOverlayParams(
            title = stringResource(R.string.home_onboarding_visual_title),
            base = base,
            pending = pending,
            // Closing always completes the step, so it always "commits" (once, guarded by the overlay).
            isChanged = { true },
            // The palette is saved by its base name (the light variant shows on a light system), as the pickers do.
            onCommit = {
                actions.onCloseVisual(VisualChoice(pending.palette?.name?.basePaletteName(), pending.fontName))
            },
            onDismissRequest = { pending = pending.discard() },
            miniHome = visualMiniHome(selection, nowMillis),
            hasTabBottomBar = true
        ),
        modifier = modifier,
        bottomBar = { VisualBottomBar(selected = tab, onSelect = { tab = it }) }
    ) {
        Crossfade(targetState = tab, label = "visual_tab") { shown ->
            when (shown) {
                AppearancePicker.PALETTE -> PalettePickerGrid(
                    shownName = pending.palette?.name?.basePaletteName() ?: selection.paletteName.basePaletteName(),
                    onSelect = { pending = pending.copy(palette = it) },
                    fadesEdges = true
                )
                AppearancePicker.FONT -> FontPickerGrid(
                    shownFont = pending.fontName ?: selection.fontName,
                    onSelect = { pending = pending.copy(fontName = it) },
                    fadesEdges = true
                )
            }
        }
    }
}

/** Below the tab's content: "saved when you close" (the sheet has no button) and the tabs. */
@Composable
private fun ColumnScope.VisualBottomBar(selected: AppearancePicker, onSelect: (AppearancePicker) -> Unit) {
    AureoleSheetSavedHint()
    AureoleSheetTabBar(
        labels = AppearancePicker.entries.map { stringResource(it.tabLabelRes) },
        selectedIndex = AppearancePicker.entries.indexOf(selected),
        description = stringResource(R.string.home_onboarding_visual_tab_bar_description),
        onSelect = { onSelect(AppearancePicker.entries[it]) }
    )
}

/** The sample mini-home (clock, Phone, Messages, Camera) with the real time and date of [nowMillis]. */
@ReadOnlyComposable
@Composable
private fun visualMiniHome(selection: AppearanceSelection, nowMillis: Long): MiniHomeContent {
    val locale = LocalConfiguration.current.locales[0]
    val isSystem24Hour = DateFormat.is24HourFormat(LocalContext.current)
    val datePattern = ClockHeaderUtils.resolveClockDatePattern(selection.clockDateFormat, locale)
    return sampleMiniHomeContent().copy(
        time = visualPreviewTime(selection.clockTimeFormat, isSystem24Hour, locale, nowMillis),
        date = formatClockSample(datePattern, locale, nowMillis)
    )
}

private const val PREVIEW_NOW_MILLIS = 1_767_625_200_000L

@AureolePreview
@Composable
private fun OnboardingVisualSheetPreview() {
    AureoleLauncherTheme {
        OnboardingVisualSheetComponent(
            picker = AppearancePicker.PALETTE,
            selection = AppearanceSelection(paletteName = "Frostbite", fontName = "Istok Web"),
            actions = OnboardingActions(),
            nowMillis = PREVIEW_NOW_MILLIS
        )
    }
}
