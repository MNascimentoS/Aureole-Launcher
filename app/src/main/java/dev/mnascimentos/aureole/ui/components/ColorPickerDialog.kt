@file:Suppress("MagicNumber", "LongMethod")

package dev.mnascimentos.aureole.ui.components

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
import androidx.compose.ui.unit.dp
import androidx.core.graphics.ColorUtils

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

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorPickerDialog(
    initialColor: Int,
    onColorSelected: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    var selectedColor by remember { mutableIntStateOf(initialColor) }

    val initialHsl = remember(initialColor) {
        val hsl = FloatArray(3)
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
                                .clickable {
                                    selectedColor = colorInt
                                    val hsl = FloatArray(3)
                                    ColorUtils.colorToHSL(colorInt, hsl)
                                    hue = hsl[0]
                                },
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

                Spacer(modifier = Modifier.height(4.dp))

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
                        onValueChange = { newHue ->
                            hue = newHue
                            val customColor = ColorUtils.HSLToColor(floatArrayOf(newHue, 0.65f, 0.45f))
                            selectedColor = customColor
                        },
                        valueRange = 0f..360f,
                        modifier = Modifier.weight(1f),
                    )
                }
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
