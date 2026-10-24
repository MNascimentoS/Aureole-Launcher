package dev.mnascimentos.aureole.composable

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Slider
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

private val PresetColors = listOf(
    0xFF6650A4.toInt(), // Purple
    0xFF1B65C0.toInt(), // Blue
    0xFF00796B.toInt(), // Teal
    0xFF2E7D32.toInt(), // Green
    0xFFF57F17.toInt(), // Amber
    0xFFE65100.toInt(), // Orange
    0xFFC62828.toInt(), // Red
    0xFFAD1457.toInt(), // Pink
    0xFF00838F.toInt(), // Cyan
    0xFF3F51B5.toInt(), // Slate
)

private const val CUSTOM_SATURATION = 0.65f
private const val CUSTOM_LIGHTNESS = 0.45f
private const val MAX_HUE = 360f
private const val DEFAULT_PRESET_COLOR = 0xFF6650A4.toInt()
private const val HSL_COMPONENTS_COUNT = 3

@Composable
fun ColorPickerDialog(
    initialColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedColor by remember { mutableIntStateOf(initialColor) }

    val initialHsl = remember(initialColor) {
        val hsl = FloatArray(HSL_COMPONENTS_COUNT)
        ColorUtils.colorToHSL(initialColor, hsl)
        hsl
    }
    var hue by remember { mutableFloatStateOf(initialHsl[0]) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            AureoleText(
                text = stringResource(R.string.color_picker_primary),
                format = AureoleTextFormatParams(style = AureoleDS.typography.titleMedium, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium),
            ) {
                PresetColorsSection(
                    selectedColor = selectedColor,
                    onSelectPreset = { colorInt ->
                        selectedColor = colorInt
                        val hsl = FloatArray(HSL_COMPONENTS_COUNT)
                        ColorUtils.colorToHSL(colorInt, hsl)
                        hue = hsl[0]
                    }
                )

                Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

                CustomHueSection(
                    selectedColor = selectedColor,
                    hue = hue,
                    onHueChanged = { newHue ->
                        hue = newHue
                        val customColor = ColorUtils.HSLToColor(
                            floatArrayOf(newHue, CUSTOM_SATURATION, CUSTOM_LIGHTNESS)
                        )
                        selectedColor = customColor
                    }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onColorSelected(selectedColor) }) {
                AureoleText(stringResource(R.string.common_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                AureoleText(stringResource(R.string.common_cancel))
            }
        },
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PresetColorsSection(
    selectedColor: Int,
    onSelectPreset: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)) {
        AureoleText(
            text = stringResource(R.string.colors_recommended),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium)
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        ) {
            PresetColors.forEach { colorInt ->
                PresetColorItem(
                    colorInt = colorInt,
                    isSelected = selectedColor == colorInt,
                    onSelect = { onSelectPreset(colorInt) }
                )
            }
        }
    }
}

@Composable
private fun PresetColorItem(
    colorInt: Int,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.xxLarge)
            .clip(CircleShape)
            .background(Color(colorInt))
            .border(
                width = if (isSelected) AureoleDS.dimens.borderMax else AureoleDS.dimens.borderMinimal,
                color = if (isSelected) AureoleDS.colors.onSurfaceHigh else Color.Transparent,
                shape = CircleShape,
            )
            .clickable(
                onClick = onSelect,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {},
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            AureoleDS.icons.Check(
                tint = Color.White,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium),
            )
        }
    }
}

@Composable
private fun CustomHueSection(
    selectedColor: Int,
    hue: Float,
    onHueChanged: (Float) -> Unit
) {
    Column {
        AureoleText(
            text = stringResource(R.string.color_picker_custom_hue),
            color = AureoleDS.colors.onSurfaceMedium,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelMedium)
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        ) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconXLarge)
                    .clip(CircleShape)
                    .background(Color(selectedColor))
                    .border(AureoleDS.dimens.borderMinimal, AureoleDS.colors.outline, CircleShape),
            )

            Slider(
                value = hue,
                onValueChange = onHueChanged,
                valueRange = 0f..MAX_HUE,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@AureolePreview
@Composable
private fun ColorPickerDialogPreview() {
    AureoleLauncherTheme {
        ColorPickerDialog(
            initialColor = DEFAULT_PRESET_COLOR,
            onColorSelected = {},
            onDismiss = {}
        )
    }
}
