@file:Suppress("MagicNumber")

package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

private object ClockSectionMetrics {
    val NegativeChipVerticalSpacing = (-8).dp
}

private val CLOCK_STYLE_OPTIONS = listOf(
    Pair("DYNAMIC_GREETING", "Saudação Diária"),
    Pair("DATE_ON_TOP", "Data no Topo"),
    Pair("GREETING_AND_DATE", "Saudação + Data"),
    Pair("CUSTOM_GREETING", "Texto Personalizado"),
    Pair("TIME_ONLY", "Apenas Horário")
)

private val DATE_FORMAT_OPTIONS = listOf(
    Pair("DEFAULT", "Completo (sexta, 9 out)"),
    Pair("SHORT", "Curto (Tue, 22 Sep)"),
    Pair("MEDIUM", "Médio (9 de outubro)"),
    Pair("NUMERIC", "Numérico (09/10/2026)")
)

@Suppress("LongMethod")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditClockDropdown(
    label: String?,
    selectedKey: String,
    options: List<Pair<String, String>>,
    onOptionSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    val currentDisplayLabel = remember(selectedKey, options) {
        options.find { it.first == selectedKey }?.second ?: selectedKey
    }

    Column(modifier = modifier.fillMaxWidth()) {
        if (label != null) {
            AureoleText(
                text = label,
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.padding(bottom = AureoleDS.dimens.xxSmall),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
            )
        }

        ExposedDropdownMenuBox(
            expanded = isExpanded,
            onExpandedChange = { isExpanded = !isExpanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.45f))
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AureoleText(
                        text = currentDisplayLabel,
                        color = AureoleDS.colors.onSurfaceHigh,
                        format = AureoleTextFormatParams(
                            style = AureoleDS.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                    )
                    AureoleDS.icons.ArrowDown(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                    )
                }
            }

            ExposedDropdownMenu(
                expanded = isExpanded,
                onDismissRequest = { isExpanded = false },
                modifier = Modifier
                    .background(AureoleDS.colors.surfaceVariant)
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            ) {
                options.forEach { (key, optionLabel) ->
                    val isSelected = (key == selectedKey)
                    DropdownMenuItem(
                        text = {
                            AureoleText(
                                text = optionLabel,
                                color = if (isSelected) {
                                    AureoleDS.colors.onSurfaceHigh
                                } else {
                                    AureoleDS.colors.onSurfaceMedium
                                },
                                format = AureoleTextFormatParams(
                                    style = AureoleDS.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            )
                        },
                        onClick = {
                            onOptionSelected(key)
                            isExpanded = false
                        },
                        modifier = Modifier.background(
                            if (isSelected) AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.1f) else Color.Transparent
                        )
                    )
                }
            }
        }
    }
}

@Composable
internal fun EditClockStyleSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    tempCustomGreeting: String,
    onGreetingChange: (String) -> Unit
) {
    EditClockSectionHeader(title = "Estilo do Layout")
    EditClockDropdown(
        label = null,
        selectedKey = uiState.clockStyle,
        options = CLOCK_STYLE_OPTIONS,
        onOptionSelected = { actions.onUpdateClockStyle(it) }
    )

    if (uiState.clockStyle == "CUSTOM_GREETING") {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
        OutlinedTextField(
            value = tempCustomGreeting,
            onValueChange = onGreetingChange,
            label = { AureoleText("Texto da Saudação") },
            placeholder = { AureoleText("Ex: Bem-vindo(a) de volta!") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
internal fun EditClockAlignmentSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Alinhamento")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
    ) {
        EditClockChoiceButton(
            text = "Esquerda",
            selected = uiState.clockAlignment == "START",
            onClick = { actions.onUpdateClockAlignment("START") },
            modifier = Modifier.weight(1f)
        )
        EditClockChoiceButton(
            text = "Centro",
            selected = uiState.clockAlignment == "CENTER",
            onClick = { actions.onUpdateClockAlignment("CENTER") },
            modifier = Modifier.weight(1f)
        )
        EditClockChoiceButton(
            text = "Direita",
            selected = uiState.clockAlignment == "END",
            onClick = { actions.onUpdateClockAlignment("END") },
            modifier = Modifier.weight(1f)
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockTextColorSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Cor do Texto")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.smallCompact)
    ) {
        PRESET_TEXT_COLORS.forEach { (name, colorValue) ->
            EditClockColorSwatchItem(
                name = name,
                colorValue = colorValue,
                isSelected = uiState.clockTextColor == colorValue,
                onClick = { actions.onUpdateClockTextColor(colorValue) }
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockFontSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Estilo da Fonte")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(ClockSectionMetrics.NegativeChipVerticalSpacing)
    ) {
        EditClockLayoutChip(
            label = "Padrão",
            selected = uiState.clockFontFamily == "SANS_SERIF",
            onClick = { actions.onUpdateClockFontFamily("SANS_SERIF") }
        )
        EditClockLayoutChip(
            label = "Negrito",
            selected = uiState.clockFontFamily == "BOLD",
            onClick = { actions.onUpdateClockFontFamily("BOLD") }
        )
        EditClockLayoutChip(
            label = "Serifada",
            selected = uiState.clockFontFamily == "SERIF",
            onClick = { actions.onUpdateClockFontFamily("SERIF") }
        )
        EditClockLayoutChip(
            label = "Monospaced",
            selected = uiState.clockFontFamily == "MONOSPACE",
            onClick = { actions.onUpdateClockFontFamily("MONOSPACE") }
        )
        EditClockLayoutChip(
            label = "Arredondada",
            selected = uiState.clockFontFamily == "ROUNDED",
            onClick = { actions.onUpdateClockFontFamily("ROUNDED") }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockFormatSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    Column {
        EditClockSectionHeader(title = "Formato de Hora e Data")

        AureoleText(
            text = "Formato de Hora:",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall)
        ) {
            EditClockChoiceButton(
                text = "Sistema",
                selected = uiState.clockTimeFormat == "SYSTEM",
                onClick = { actions.onUpdateClockTimeFormat("SYSTEM") },
                modifier = Modifier.weight(1f)
            )
            EditClockChoiceButton(
                text = "12 Horas",
                selected = uiState.clockTimeFormat == "12H",
                onClick = { actions.onUpdateClockTimeFormat("12H") },
                modifier = Modifier.weight(1f)
            )
            EditClockChoiceButton(
                text = "24 Horas",
                selected = uiState.clockTimeFormat == "24H",
                onClick = { actions.onUpdateClockTimeFormat("24H") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.smallCompact))

        EditClockDropdown(
            label = "Formato de Data:",
            selectedKey = uiState.clockDateFormat,
            options = DATE_FORMAT_OPTIONS,
            onOptionSelected = { actions.onUpdateClockDateFormat(it) }
        )
    }
}

@AureolePreview
@Composable
private fun EditClockSectionsPreview() {
    AureoleLauncherTheme {
        Column(
            modifier = Modifier
                .background(AureoleDS.colors.surface)
                .padding(AureoleDS.dimens.medium)
        ) {
            EditClockStyleSection(
                uiState = MainUiState(),
                actions = createDefaultHomeScreenActions(),
                tempCustomGreeting = "Bem-vindo",
                onGreetingChange = {}
            )
        }
    }
}
