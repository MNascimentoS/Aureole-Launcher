package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EditClockBackgroundSection(
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
internal fun EditClockActionButtons(
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
            AureoleText("Salvar", color = MaterialTheme.colorScheme.onPrimary)
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
        onRemoveWidgetClick = { _, _ -> },
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
