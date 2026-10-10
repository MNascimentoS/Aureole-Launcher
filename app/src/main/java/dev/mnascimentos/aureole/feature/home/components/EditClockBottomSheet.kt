package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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

@OptIn(ExperimentalLayoutApi::class)
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
            // Live Preview Card
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
                    isHazeEnabled = false,
                    isBackgroundEnabled = uiState.isClockBackgroundEnabled,
                    clockStyle = uiState.clockStyle,
                    clockCustomGreeting = tempCustomGreeting,
                    clockAlignment = uiState.clockAlignment,
                    clockFontFamily = uiState.clockFontFamily,
                    clockTimeFormat = uiState.clockTimeFormat,
                    clockDateFormat = uiState.clockDateFormat,
                    clockTextColor = uiState.clockTextColor,
                    clockBackgroundColor = uiState.clockBackgroundColor
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Clock Layout / Style
            SectionHeader(title = "Estilo do Layout")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutChip(
                    label = "Saudação Diária",
                    selected = uiState.clockStyle == "DYNAMIC_GREETING",
                    onClick = { actions.onUpdateClockStyle("DYNAMIC_GREETING") }
                )
                LayoutChip(
                    label = "Data no Topo",
                    selected = uiState.clockStyle == "DATE_ON_TOP",
                    onClick = { actions.onUpdateClockStyle("DATE_ON_TOP") }
                )
                LayoutChip(
                    label = "Saudação + Data",
                    selected = uiState.clockStyle == "GREETING_AND_DATE",
                    onClick = { actions.onUpdateClockStyle("GREETING_AND_DATE") }
                )
                LayoutChip(
                    label = "Texto Personalizado",
                    selected = uiState.clockStyle == "CUSTOM_GREETING",
                    onClick = { actions.onUpdateClockStyle("CUSTOM_GREETING") }
                )
                LayoutChip(
                    label = "Apenas Horário",
                    selected = uiState.clockStyle == "TIME_ONLY",
                    onClick = { actions.onUpdateClockStyle("TIME_ONLY") }
                )
            }

            // Custom Greeting Input Field
            if (uiState.clockStyle == "CUSTOM_GREETING") {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = tempCustomGreeting,
                    onValueChange = {
                        tempCustomGreeting = it
                        actions.onUpdateClockCustomGreeting(it)
                    },
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

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Alignment
            SectionHeader(title = "Alinhamento")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChoiceButton(
                    text = "Esquerda",
                    selected = uiState.clockAlignment == "START",
                    onClick = { actions.onUpdateClockAlignment("START") },
                    modifier = Modifier.weight(1f)
                )
                ChoiceButton(
                    text = "Centro",
                    selected = uiState.clockAlignment == "CENTER",
                    onClick = { actions.onUpdateClockAlignment("CENTER") },
                    modifier = Modifier.weight(1f)
                )
                ChoiceButton(
                    text = "Direita",
                    selected = uiState.clockAlignment == "END",
                    onClick = { actions.onUpdateClockAlignment("END") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Text Color
            SectionHeader(title = "Cor do Texto")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PRESET_TEXT_COLORS.forEach { (name, colorValue) ->
                    ColorSwatchItem(
                        name = name,
                        colorValue = colorValue,
                        isSelected = uiState.clockTextColor == colorValue,
                        onClick = { actions.onUpdateClockTextColor(colorValue) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Font Style
            SectionHeader(title = "Estilo da Fonte")
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutChip(
                    label = "Padrão",
                    selected = uiState.clockFontFamily == "SANS_SERIF",
                    onClick = { actions.onUpdateClockFontFamily("SANS_SERIF") }
                )
                LayoutChip(
                    label = "Negrito",
                    selected = uiState.clockFontFamily == "BOLD",
                    onClick = { actions.onUpdateClockFontFamily("BOLD") }
                )
                LayoutChip(
                    label = "Serifada",
                    selected = uiState.clockFontFamily == "SERIF",
                    onClick = { actions.onUpdateClockFontFamily("SERIF") }
                )
                LayoutChip(
                    label = "Monospaced",
                    selected = uiState.clockFontFamily == "MONOSPACE",
                    onClick = { actions.onUpdateClockFontFamily("MONOSPACE") }
                )
                LayoutChip(
                    label = "Arredondada",
                    selected = uiState.clockFontFamily == "ROUNDED",
                    onClick = { actions.onUpdateClockFontFamily("ROUNDED") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 5. Time & Date Format
            SectionHeader(title = "Formato de Hora e Data")
            AureoleText(
                text = "Formato de Hora:",
                style = AureoleTheme.typography.bodySmall,
                color = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChoiceButton(
                    text = "Sistema",
                    selected = uiState.clockTimeFormat == "SYSTEM",
                    onClick = { actions.onUpdateClockTimeFormat("SYSTEM") },
                    modifier = Modifier.weight(1f)
                )
                ChoiceButton(
                    text = "12 Horas",
                    selected = uiState.clockTimeFormat == "12H",
                    onClick = { actions.onUpdateClockTimeFormat("12H") },
                    modifier = Modifier.weight(1f)
                )
                ChoiceButton(
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
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                LayoutChip(
                    label = "Completo (sexta, 9 out)",
                    selected = uiState.clockDateFormat == "DEFAULT",
                    onClick = { actions.onUpdateClockDateFormat("DEFAULT") }
                )
                LayoutChip(
                    label = "Curto (Tue, 22 Sep)",
                    selected = uiState.clockDateFormat == "SHORT",
                    onClick = { actions.onUpdateClockDateFormat("SHORT") }
                )
                LayoutChip(
                    label = "Médio (9 de outubro)",
                    selected = uiState.clockDateFormat == "MEDIUM",
                    onClick = { actions.onUpdateClockDateFormat("MEDIUM") }
                )
                LayoutChip(
                    label = "Numérico (09/10/2026)",
                    selected = uiState.clockDateFormat == "NUMERIC",
                    onClick = { actions.onUpdateClockDateFormat("NUMERIC") }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 6. Background Options
            SectionHeader(title = "Fundo do Cartão")
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
                        ColorSwatchItem(
                            name = name,
                            colorValue = colorValue,
                            isSelected = uiState.clockBackgroundColor == colorValue,
                            onClick = { actions.onUpdateClockBackgroundColor(colorValue) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = {
                        actions.onResetClockSettings()
                        tempCustomGreeting = ""
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
    }
}

@Composable
private fun SectionHeader(title: String) {
    AureoleText(
        text = title,
        style = AureoleTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun LayoutChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { AureoleText(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = AureoleTheme.colors.surfaceVariant,
            labelColor = AureoleTheme.colors.onSurfaceMedium
        )
    )
}

@Composable
private fun ChoiceButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (selected) {
        Button(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            AureoleText(
                text = text,
                color = MaterialTheme.colorScheme.onPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            modifier = modifier,
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
        ) {
            AureoleText(
                text = text,
                color = AureoleTheme.colors.onSurfaceMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun ColorSwatchItem(
    name: String,
    colorValue: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val displayColor = if (colorValue == 0) MaterialTheme.colorScheme.surfaceVariant else Color(colorValue)
    val borderColor = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent

    Row(
        modifier = Modifier
            .clip(CircleShape)
            .border(2.dp, borderColor, CircleShape)
            .clickable(onClick = onClick)
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.6f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(displayColor)
                .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        AureoleText(
            text = name,
            style = AureoleTheme.typography.bodySmall,
            color = if (isSelected) MaterialTheme.colorScheme.primary else AureoleTheme.colors.onSurfaceMedium
        )
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
