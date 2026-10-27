package dev.mnascimentos.aureole.feature.home.components.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.PickFontBottomSheet
import dev.mnascimentos.aureole.composable.sheets.PickPaletteBottomSheet
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearancePicker
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.AppearanceSelection
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingActions

/**
 * Palette or font picker opened from the checklist, right on the home: each tap applies at once so
 * the whole screen re-themes behind the sheet. Closing it after a pick counts as done, like "Looks good".
 */
@Composable
fun OnboardingAppearancePickerComponent(
    picker: AppearancePicker?,
    selection: AppearanceSelection,
    actions: OnboardingActions,
    modifier: Modifier = Modifier
) {
    if (picker == null) return
    var hasPicked by remember(picker) { mutableStateOf(false) }
    val onDismiss = if (hasPicked) actions.onConfirmAppearancePicker else actions.onDismissAppearancePicker
    val footer = @Composable { AppearancePickerFooter(onConfirm = actions.onConfirmAppearancePicker) }
    when (picker) {
        AppearancePicker.PALETTE -> PickPaletteBottomSheet(
            selectedPaletteName = selection.paletteName,
            onPaletteSelected = { palette ->
                hasPicked = true
                actions.onSelectPalette(palette.name)
            },
            onDismissRequest = onDismiss,
            modifier = modifier,
            footer = footer
        )
        AppearancePicker.FONT -> PickFontBottomSheet(
            selectedFontName = selection.fontName,
            onFontSelected = { fontName ->
                hasPicked = true
                actions.onSelectFont(fontName)
            },
            onDismissRequest = onDismiss,
            modifier = modifier,
            footer = footer
        )
    }
}

@Composable
private fun AppearancePickerFooter(onConfirm: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = AureoleDS.dimens.small),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AureoleText(
            text = stringResource(R.string.home_onboarding_picker_hint),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.text, textAlign = TextAlign.Center)
        )
        OnboardingPrimaryButton(text = stringResource(R.string.home_onboarding_picker_done), onClick = onConfirm)
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
