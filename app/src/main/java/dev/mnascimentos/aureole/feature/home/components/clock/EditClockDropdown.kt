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
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockDropdownConfig

private object Constants {
    const val ROTATION_COLLAPSED = 0f
    const val ROTATION_EXPANDED = 180f
    const val ROTATION_ANIM_DURATION = 200

    const val BG_ALPHA = 0.45f
    const val BORDER_ALPHA_EXPANDED = 0.35f
    const val BORDER_ALPHA_COLLAPSED = 0.2f
    const val MENU_BORDER_ALPHA = 0.3f

    val SHADOW_ELEVATION = 8.dp
    val MENU_ITEM_VERTICAL_PADDING = 2.dp

    const val SELECTED_BG_ALPHA = 0.12f
}

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
                    color = AureoleDS.colors.outline.copy(alpha = Constants.MENU_BORDER_ALPHA)
                ),
                shadowElevation = Constants.SHADOW_ELEVATION,
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
        targetValue = if (isExpanded) Constants.ROTATION_EXPANDED else Constants.ROTATION_COLLAPSED,
        animationSpec = tween(durationMillis = Constants.ROTATION_ANIM_DURATION),
        label = "arrowRotation"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = Constants.BG_ALPHA))
            .border(
                BorderStroke(
                    width = AureoleDS.dimens.borderMinimal,
                    color = if (isExpanded) {
                        AureoleDS.colors.onSurfaceHigh.copy(alpha = Constants.BORDER_ALPHA_EXPANDED)
                    } else {
                        AureoleDS.colors.outline.copy(alpha = Constants.BORDER_ALPHA_COLLAPSED)
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
                vertical = Constants.MENU_ITEM_VERTICAL_PADDING
            )
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(
                if (isSelected) {
                    AureoleDS.colors.onSurfaceHigh.copy(
                        alpha = Constants.SELECTED_BG_ALPHA
                    )
                } else {
                    Color.Transparent
                }
            )
    )
}

@AureolePreview
@Composable
private fun EditClockDropdownDefaultPreview() {
    AureoleLauncherTheme {
        EditClockDropdown(
            label = "Preview Label",
            selectedKey = "key1",
            options = listOf(
                "key1" to "Option 1",
                "key2" to "Option 2"
            ),
            onOptionSelected = {},
            initialExpanded = false
        )
    }
}

@AureolePreview
@Composable
private fun EditClockDropdownExpandedPreview() {
    AureoleLauncherTheme {
        EditClockDropdown(
            label = "Preview Label",
            selectedKey = "key1",
            options = listOf(
                "key1" to "Option 1",
                "key2" to "Option 2"
            ),
            onOptionSelected = {},
            initialExpanded = true
        )
    }
}
