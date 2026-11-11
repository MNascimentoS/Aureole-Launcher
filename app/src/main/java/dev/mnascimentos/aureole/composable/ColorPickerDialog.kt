package dev.mnascimentos.aureole.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.color.AureoleColorSwatches
import dev.mnascimentos.aureole.composable.color.AureoleHueSlider
import dev.mnascimentos.aureole.composable.color.model.DefaultPickerColor
import dev.mnascimentos.aureole.composable.color.model.RecommendedColors
import dev.mnascimentos.aureole.composable.color.model.colorForHue
import dev.mnascimentos.aureole.composable.color.model.formatHex
import dev.mnascimentos.aureole.composable.color.model.hueOf
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Color picker in an edit overlay: the chosen color and its hex sit anchored above the sheet, the recommended
 * colors and the hue slider inside it, and Cancel / Confirm below, the filled button on the thumb side.
 */
@Composable
fun ColorPickerDialog(
    initialColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    var selectedColor by remember { mutableIntStateOf(initialColor) }
    var hue by remember { mutableFloatStateOf(hueOf(initialColor)) }

    AureoleConfirmOverlay(
        params = ConfirmOverlayParams(
            title = stringResource(R.string.settings_appearance_color_picker_primary),
            primaryLabel = stringResource(R.string.core_common_confirm),
            onPrimary = { onColorSelected(selectedColor) },
            onDismissRequest = onDismiss,
            isLeftHanded = isLeftHanded
        ),
        modifier = modifier,
        preview = { ColorPreview(color = selectedColor) }
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            SectionLabel(
                text = stringResource(R.string.settings_appearance_colors_recommended)
            )
            LeftHandMirror(isLeftHanded = isLeftHanded) {
                AureoleColorSwatches(
                    colors = RecommendedColors,
                    selected = selectedColor,
                    onSelect = { color ->
                        selectedColor = color
                        hue = hueOf(color)
                    }
                )
            }
            SectionLabel(
                text = stringResource(R.string.settings_appearance_color_picker_custom_hue)
            )
            AureoleHueSlider(
                hue = hue,
                onHueChange = { newHue ->
                    hue = newHue
                    selectedColor = colorForHue(newHue)
                }
            )
        }
    }
}

@Composable
private fun BoxScope.ColorPreview(color: Int) {
    AureolePreviewFrame(
        label = stringResource(R.string.settings_appearance_preview_label),
        modifier = Modifier.align(Alignment.Center)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconXLarge * 2)
                    .clip(CircleShape)
                    .background(Color(color))
                    .border(AureoleDS.dimens.borderMax, AureoleDS.colors.onSurfaceHigh, CircleShape)
            )
            AureoleText(
                text = formatHex(color),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    AureoleText(
        text = text.uppercase(),
        color = AureoleDS.colors.onSurfaceMedium,
        modifier = modifier.fillMaxWidth(),
        format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
    )
}

@AureolePreview
@Composable
private fun ColorPickerDialogPreview() {
    AureoleLauncherTheme {
        ColorPickerDialog(
            initialColor = DefaultPickerColor,
            onColorSelected = {},
            onDismiss = {}
        )
    }
}
