package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions

private object ClockSectionMetrics {
    val NegativeChipVerticalSpacing = (-8).dp
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockStyleSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    tempCustomGreeting: String,
    onGreetingChange: (String) -> Unit
) {
    EditClockSectionHeader(title = "Estilo do Layout")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(ClockSectionMetrics.NegativeChipVerticalSpacing)
    ) {
        EditClockLayoutChip(
            label = "Saudação Diária",
            selected = uiState.clockStyle == "DYNAMIC_GREETING",
            onClick = { actions.onUpdateClockStyle("DYNAMIC_GREETING") }
        )
        EditClockLayoutChip(
            label = "Data no Topo",
            selected = uiState.clockStyle == "DATE_ON_TOP",
            onClick = { actions.onUpdateClockStyle("DATE_ON_TOP") }
        )
        EditClockLayoutChip(
            label = "Saudação + Data",
            selected = uiState.clockStyle == "GREETING_AND_DATE",
            onClick = { actions.onUpdateClockStyle("GREETING_AND_DATE") }
        )
        EditClockLayoutChip(
            label = "Texto Personalizado",
            selected = uiState.clockStyle == "CUSTOM_GREETING",
            onClick = { actions.onUpdateClockStyle("CUSTOM_GREETING") }
        )
        EditClockLayoutChip(
            label = "Apenas Horário",
            selected = uiState.clockStyle == "TIME_ONLY",
            onClick = { actions.onUpdateClockStyle("TIME_ONLY") }
        )
    }

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

        AureoleText(
            text = "Formato de Data:",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodySmall)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalArrangement = Arrangement.spacedBy(ClockSectionMetrics.NegativeChipVerticalSpacing)
        ) {
            EditClockLayoutChip("Completo (sexta, 9 out)", uiState.clockDateFormat == "DEFAULT") {
                actions.onUpdateClockDateFormat("DEFAULT")
            }
            EditClockLayoutChip("Curto (Tue, 22 Sep)", uiState.clockDateFormat == "SHORT") {
                actions.onUpdateClockDateFormat("SHORT")
            }
            EditClockLayoutChip("Médio (9 de outubro)", uiState.clockDateFormat == "MEDIUM") {
                actions.onUpdateClockDateFormat("MEDIUM")
            }
            EditClockLayoutChip("Numérico (09/10/2026)", uiState.clockDateFormat == "NUMERIC") {
                actions.onUpdateClockDateFormat("NUMERIC")
            }
        }
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
