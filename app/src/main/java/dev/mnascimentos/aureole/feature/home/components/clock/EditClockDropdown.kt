@file:Suppress("MagicNumber")

package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockDropdownConfig

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditClockDropdown(
    config: EditClockDropdownConfig,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(config.initialExpanded) }
    val currentDisplayLabel = remember(config.selectedKey, config.options) {
        config.options.find { it.first == config.selectedKey }?.second ?: config.selectedKey
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (config.label != null) {
            AureoleText(
                text = config.label,
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            DropdownAnchorBox(
                isExpanded = isExpanded,
                displayLabel = currentDisplayLabel,
                modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
            )

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
                containerColor = AureoleDS.colors.surfaceVariant,
                border = BorderStroke(
                    width = AureoleDS.dimens.borderMinimal,
                    color = AureoleDS.colors.outline.copy(alpha = 0.3f)
                ),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall)
            ) {
                config.options.forEach { (key, optionLabel) ->
                    DropdownOptionMenuItem(
                        optionLabel = optionLabel,
                        isSelected = (key == config.selectedKey),
                        onSelect = {
                            onOptionSelected(key)
                            isExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
internal fun EditClockDropdown(
    label: String?,
    selectedKey: String,
    options: List<Pair<String, String>>,
    onOptionSelected: (String) -> Unit,
    initialExpanded: Boolean = false
) {
    EditClockDropdown(
        config = EditClockDropdownConfig(
            label = label,
            selectedKey = selectedKey,
            options = options,
            initialExpanded = initialExpanded
        ),
        onOptionSelected = onOptionSelected
    )
}

@Composable
private fun DropdownAnchorBox(
    isExpanded: Boolean,
    displayLabel: String,
    modifier: Modifier = Modifier
) {
    val arrowRotation by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        animationSpec = tween(durationMillis = 200),
        label = "arrowRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
            .border(
                BorderStroke(
                    width = AureoleDS.dimens.borderMinimal,
                    color = if (isExpanded) {
                        AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.35f)
                    } else {
                        AureoleDS.colors.outline.copy(alpha = 0.2f)
                    }
                ),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall)
            )
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AureoleText(
                text = displayLabel,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
            )
            AureoleDS.icons.ArrowDown(
                tint = if (isExpanded) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier
                    .size(AureoleDS.dimens.iconSmall)
                    .graphicsLayer { rotationZ = arrowRotation }
            )
        }
    }
}

@Composable
private fun DropdownOptionMenuItem(
    optionLabel: String,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AureoleText(
                    text = optionLabel,
                    color = if (isSelected) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.onSurfaceMedium,
                    format = AureoleTextFormatParams(
                        style = AureoleDS.typography.bodyMedium,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                )
                if (isSelected) {
                    AureoleDS.icons.Check(
                        tint = AureoleDS.colors.onSurfaceHigh,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                    )
                }
            }
        },
        onClick = onSelect,
        contentPadding = PaddingValues(
            horizontal = AureoleDS.dimens.medium,
            vertical = AureoleDS.dimens.small
        ),
        modifier = Modifier
            .padding(
                horizontal = AureoleDS.dimens.xxSmall,
                vertical = 2.dp
            )
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(
                if (isSelected) AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.12f) else Color.Transparent
            )
    )
}
