package dev.mnascimentos.aureole.core.designsystem.components

import android.content.res.Configuration
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme

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
            Text(
                text = "Escolher Cor Primária",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
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

                Spacer(modifier = Modifier.height(4.dp))

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
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
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
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Cores Recomendadas",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PresetColors.forEach { colorInt ->
                val isSelected = selectedColor == colorInt
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(colorInt))
                        .border(
                            width = if (isSelected) 3.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                            shape = CircleShape,
                        )
                        .clickable { onSelectPreset(colorInt) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selecionado",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
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
        Text(
            text = "Ajuste Personalizado (Matiz)",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(selectedColor))
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
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

@Preview(name = "Light Mode", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun ColorPickerDialogPreview() {
    AureoleLauncherTheme {
        ColorPickerDialog(
            initialColor = DEFAULT_PRESET_COLOR,
            onColorSelected = {},
            onDismiss = {}
        )
    }
}
