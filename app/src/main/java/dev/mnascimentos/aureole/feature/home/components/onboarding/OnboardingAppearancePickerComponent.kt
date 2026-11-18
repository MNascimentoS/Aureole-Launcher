package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.composable.sheets.model.ThemePickerSheetOptions
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions

/**
 * Palette or font picker opened from the checklist, right on the home. Tapping an option only previews it;
 * "Apply" saves the choice and confirms the task, so the checklist moves on to the next one. Closing without
 * applying keeps what was previewed, or the default (Frostbite / Istok Web) when nothing was tapped, and confirms
 * the task the same way.
 */
@Composable
fun OnboardingAppearancePickerComponent(
    picker: AppearancePicker?,
    selection: AppearanceSelection,
    actions: OnboardingActions,
    modifier: Modifier = Modifier
) {
    if (picker == null) return
    // The sheet also reports a dismiss while it closes after "Apply": that one is not giving up.
    var isApplied by remember(picker) { mutableStateOf(false) }
    var hasPreview by remember(picker) { mutableStateOf(false) }
    val onDismiss = { if (!isApplied) actions.onDismissAppearancePicker(hasPreview) }
    // Only the Customize showcase has the logo during the onboarding: no settings shortcut, so no logo (RF-03).
    val options = ThemePickerSheetOptions(
        onSettingsClick = null,
        onPreview = { hasPreview = true }
    )
    when (picker) {
        AppearancePicker.PALETTE -> PickPaletteBottomSheet(
            selectedPaletteName = selection.paletteName,
            onPaletteSelected = { palette ->
                isApplied = true
                actions.onSelectPalette(palette.name)
                actions.onConfirmAppearancePicker()
            },
            onDismissRequest = onDismiss,
            modifier = modifier,
            options = options
        )
        AppearancePicker.FONT -> PickFontBottomSheet(
            selectedFontName = selection.fontName,
            onFontSelected = { fontName ->
                isApplied = true
                actions.onSelectFont(fontName)
                actions.onConfirmAppearancePicker()
            },
            onDismissRequest = onDismiss,
            modifier = modifier,
            options = options
        )
    }
}

@AureolePreview
@Composable
private fun OnboardingAppearancePickerPreview() {
    AureoleLauncherTheme {
        OnboardingAppearancePickerComponent(
            picker = AppearancePicker.PALETTE,
            selection = AppearanceSelection(paletteName = "Frostbite", fontName = "Istok Web"),
            actions = OnboardingActions()
        )
    }
}
