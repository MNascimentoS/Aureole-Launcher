package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.components.SettingsBottomSheet

private val PRESET_TEXT_COLORS = listOf(
    Pair("Padrão", 0),
    Pair("Azul Gelado", 0xFF90CAF9.toInt()),
    Pair("Lavanda", 0xFFE1BEE7.toInt()),
    Pair("Branco", 0xFFFFFFFF.toInt()),
    Pair("Ouro", 0xFFFFE082.toInt()),
    Pair("Menta", 0xFFA5D6A7.toInt()),
    Pair("Coral", 0xFFFFAB91.toInt())
)

private val PRESET_BG_COLORS = listOf(
    Pair("Padrão", 0),
    Pair("Escuro Slate", 0xFF1E2124.toInt()),
    Pair("Roxo Profundo", 0xFF281C74.toInt()),
    Pair("Azul Noturno", 0xFF1A237E.toInt()),
    Pair("Vinho", 0xFF4A148C.toInt()),
    Pair("Verde Escuro", 0xFF1B5E20.toInt())
)

private val CHIP_HORIZONTAL_SPACING = 8.dp
private const val NEGATIVE_SPACING_INT = -8
private val CHIP_VERTICAL_SPACING = NEGATIVE_SPACING_INT.dp

@Composable
fun EditClockBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit
) {
    var tempCustomGreeting by remember(uiState.clockCustomGreeting) {
        mutableStateOf(uiState.clockCustomGreeting)
    }

    SettingsBottomSheet(
        title = "Personalizar Relógio",
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            EditClockPreviewCard(uiState = uiState, tempCustomGreeting = tempCustomGreeting)

            Spacer(modifier = Modifier.height(10.dp))

            EditClockStyleSection(
                uiState = uiState,
                actions = actions,
                tempCustomGreeting = tempCustomGreeting,
                onGreetingChange = {
                    tempCustomGreeting = it
                    actions.onUpdateClockCustomGreeting(it)
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            EditClockAlignmentSection(uiState = uiState, actions = actions)

            Spacer(modifier = Modifier.height(10.dp))

            EditClockTextColorSection(uiState = uiState, actions = actions)

            Spacer(modifier = Modifier.height(10.dp))

            EditClockFontSection(uiState = uiState, actions = actions)

            Spacer(modifier = Modifier.height(10.dp))

            EditClockFormatSection(uiState = uiState, actions = actions)

            Spacer(modifier = Modifier.height(10.dp))

            EditClockBackgroundSection(uiState = uiState, actions = actions)

            Spacer(modifier = Modifier.height(24.dp))

            EditClockActionButtons(
                actions = actions,
                onDismissRequest = onDismissRequest,
                onResetGreeting = { tempCustomGreeting = "" }
            )
        }
    }
}

@Composable
private fun EditClockPreviewCard(
    uiState: MainUiState,
    tempCustomGreeting: String
) {
    AureoleText(
        text = "Pré-visualização ao vivo",
        style = AureoleTheme.typography.labelLarge,
        color = AureoleTheme.colors.onSurfaceMedium,
        modifier = Modifier.padding(bottom = 8.dp)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(8.dp)
    ) {
        ClockHeader(
            hazeConfig = ClockHazeConfig(
                isHazeEnabled = false,
                isBackgroundEnabled = uiState.isClockBackgroundEnabled
            ),
            config = ClockHeaderConfig(
                style = uiState.clockStyle,
                customGreeting = tempCustomGreeting,
                alignment = uiState.clockAlignment,
                fontFamily = uiState.clockFontFamily,
                timeFormat = uiState.clockTimeFormat,
                dateFormat = uiState.clockDateFormat,
                textColor = uiState.clockTextColor,
                backgroundColor = uiState.clockBackgroundColor
            )
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditClockStyleSection(
    uiState: MainUiState,
    actions: HomeScreenActions,
    tempCustomGreeting: String,
    onGreetingChange: (String) -> Unit
) {
    EditClockSectionHeader(title = "Estilo do Layout")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CHIP_HORIZONTAL_SPACING),
        verticalArrangement = Arrangement.spacedBy(CHIP_VERTICAL_SPACING)
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
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = tempCustomGreeting,
            onValueChange = onGreetingChange,
            label = { AureoleText("Texto da Saudação") },
            placeholder = { AureoleText("Ex: Bem-vindo(a) de volta!") },
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun EditClockAlignmentSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Alinhamento")
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CHIP_HORIZONTAL_SPACING)
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
private fun EditClockTextColorSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Cor do Texto")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
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
private fun EditClockFontSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Estilo da Fonte")
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CHIP_HORIZONTAL_SPACING),
        verticalArrangement = Arrangement.spacedBy(CHIP_VERTICAL_SPACING)
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
private fun EditClockFormatSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Formato de Hora e Data")

    AureoleText(
        text = "Formato de Hora:",
        style = AureoleTheme.typography.bodySmall,
        color = AureoleTheme.colors.onSurfaceMedium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CHIP_HORIZONTAL_SPACING)
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

    Spacer(modifier = Modifier.height(10.dp))

    AureoleText(
        text = "Formato de Data:",
        style = AureoleTheme.typography.bodySmall,
        color = AureoleTheme.colors.onSurfaceMedium,
        modifier = Modifier.padding(bottom = 6.dp)
    )
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(CHIP_HORIZONTAL_SPACING),
        verticalArrangement = Arrangement.spacedBy(CHIP_VERTICAL_SPACING)
    ) {
        EditClockLayoutChip(
            label = "Completo (sexta, 9 out)",
            selected = uiState.clockDateFormat == "DEFAULT",
            onClick = { actions.onUpdateClockDateFormat("DEFAULT") }
        )
        EditClockLayoutChip(
            label = "Curto (Tue, 22 Sep)",
            selected = uiState.clockDateFormat == "SHORT",
            onClick = { actions.onUpdateClockDateFormat("SHORT") }
        )
        EditClockLayoutChip(
            label = "Médio (9 de outubro)",
            selected = uiState.clockDateFormat == "MEDIUM",
            onClick = { actions.onUpdateClockDateFormat("MEDIUM") }
        )
        EditClockLayoutChip(
            label = "Numérico (09/10/2026)",
            selected = uiState.clockDateFormat == "NUMERIC",
            onClick = { actions.onUpdateClockDateFormat("NUMERIC") }
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun EditClockBackgroundSection(
    uiState: MainUiState,
    actions: HomeScreenActions
) {
    EditClockSectionHeader(title = "Fundo do Cartão")
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        AureoleText(
            text = "Mostrar cartão de fundo",
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh
        )
        Switch(
            checked = uiState.isClockBackgroundEnabled,
            onCheckedChange = { actions.onToggleClockBackground() }
        )
    }

    if (uiState.isClockBackgroundEnabled) {
        Spacer(modifier = Modifier.height(8.dp))
        AureoleText(
            text = "Cor de Fundo Personalizada:",
            style = AureoleTheme.typography.bodySmall,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            PRESET_BG_COLORS.forEach { (name, colorValue) ->
                EditClockColorSwatchItem(
                    name = name,
                    colorValue = colorValue,
                    isSelected = uiState.clockBackgroundColor == colorValue,
                    onClick = { actions.onUpdateClockBackgroundColor(colorValue) }
                )
            }
        }
    }
}

@Composable
private fun EditClockActionButtons(
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    onResetGreeting: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {
                actions.onResetClockSettings()
                onResetGreeting()
            }
        ) {
            AureoleText(
                text = "Redefinir Padrões",
                color = MaterialTheme.colorScheme.error
            )
        }

        Button(
            onClick = onDismissRequest,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            AureoleText("Concluído", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

@AureolePreview
@Composable
fun EditClockBottomSheetPreview() {
    val uiState = MainUiState()
    val actions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {},
        onEnterGridEditMode = {},
        onCancelGridEditMode = {},
        onSaveGridEditMode = {}
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides uiState) {
            EditClockBottomSheet(
                uiState = uiState,
                actions = actions,
                onDismissRequest = {}
            )
        }
    }
}
