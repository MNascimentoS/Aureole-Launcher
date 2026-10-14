package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.model.EditContainerFormCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.EditContainerFormState
import dev.mnascimentos.aureole.feature.home.folder.FolderIconRegistry

private const val MAX_DROPDOWN_APP_RESULTS = 10

data class EditContainerItemRowParams(
    val title: String,
    val iconName: String?,
    val isFolder: Boolean,
    val canMoveUp: Boolean,
    val canMoveDown: Boolean,
)

data class EditContainerItemRowActions(
    val onMoveUp: () -> Unit,
    val onMoveDown: () -> Unit,
    val onRemove: () -> Unit,
)

@Composable
internal fun EditContainerOrientationSelector(
    orientation: String,
    onOrientationSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.xSmall)
    ) {
        AureoleText(
            text = "Orientação do Container",
            style = AureoleTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleTheme.colors.onSurfaceHigh
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
        ) {
            val isVertical = orientation.equals("Vertical", ignoreCase = true)
            val isHorizontal = orientation.equals("Horizontal", ignoreCase = true)

            listOf("Vertical" to isVertical, "Horizontal" to isHorizontal).forEach { (optText, isSelected) ->
                val onClick = { onOrientationSelected(optText) }
                val modifier = Modifier.weight(1f)
                if (isSelected) {
                    Button(
                        onClick = onClick,
                        modifier = modifier,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        AureoleText(text = optText)
                    }
                } else {
                    OutlinedButton(
                        onClick = onClick,
                        modifier = modifier,
                        border = BorderStroke(1.dp, AureoleTheme.colors.outline.copy(alpha = 0.5f))
                    ) {
                        AureoleText(text = optText, color = AureoleTheme.colors.onSurfaceHigh)
                    }
                }
            }
        }
    }
}

private val positionOptionsMap = listOf(
    "Space Between" to "Espaçamento Entre (Separados)",
    "Space Evenly" to "Espaçamento Igual (Igualitário)",
    "Top" to "Início (Topo / Esquerda)",
    "Center" to "Centro",
    "Bottom" to "Fim (Base / Direita)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditContainerAlignmentSelector(
    position: String,
    onPositionSelected: (String) -> Unit
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }
    val displayValue = positionOptionsMap.find { it.first == position }?.second ?: position

    AureoleText(
        text = "Alinhamento dos Itens",
        style = AureoleTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = AureoleTheme.colors.onSurfaceHigh,
        modifier = Modifier.padding(bottom = 4.dp)
    )
    ExposedDropdownMenuBox(
        expanded = isDropdownExpanded,
        onExpandedChange = { isDropdownExpanded = !isDropdownExpanded }
    ) {
        OutlinedTextField(
            value = displayValue,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isDropdownExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = AureoleTheme.colors.outline,
                focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }
        ) {
            positionOptionsMap.forEach { (key, label) ->
                DropdownMenuItem(
                    text = { AureoleText(label, color = AureoleTheme.colors.onSurfaceHigh) },
                    onClick = {
                        onPositionSelected(key)
                        isDropdownExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
internal fun EditContainerOptionsExpandableSection(
    formState: EditContainerFormState,
    callbacks: EditContainerFormCallbacks
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.3f))
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isExpanded = !isExpanded }
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            AureoleText(
                text = "Opções de Aparência e Comportamento",
                style = AureoleTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = AureoleTheme.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f)
            )
            IconButton(
                onClick = { isExpanded = !isExpanded },
                modifier = Modifier.size(28.dp)
            ) {
                if (isExpanded) {
                    AureoleDS.icons.ArrowUp(
                        tint = AureoleTheme.colors.onSurfaceMedium,
                        modifier = Modifier.size(16.dp)
                    )
                } else {
                    AureoleDS.icons.ArrowDown(
                        tint = AureoleTheme.colors.onSurfaceMedium,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = isExpanded,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            EditContainerSwitchesColumn(formState = formState, callbacks = callbacks)
        }
    }
}

@Composable
private fun EditContainerSwitchesColumn(
    formState: EditContainerFormState,
    callbacks: EditContainerFormCallbacks
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
        modifier = Modifier.padding(top = AureoleDS.dimens.small)
    ) {
        EditContainerOrientationSelector(
            orientation = formState.orientation,
            onOrientationSelected = callbacks.onOrientationChange
        )

        EditContainerAlignmentSelector(
            position = formState.position,
            onPositionSelected = callbacks.onPositionChange
        )

        SettingSwitchRow(
            label = "Fundo do Container",
            checked = formState.isBackgroundEnabled,
            onCheckedChange = callbacks.onBgChange
        )

        SettingSwitchRow(
            label = "Expandir Célula",
            checked = formState.isExpandCell,
            onCheckedChange = callbacks.onExpandChange
        )

        SettingSwitchRow(
            label = "Botão de Criar Pasta",
            checked = formState.showAddFolderButton,
            onCheckedChange = callbacks.onAddFolderBtnChange
        )

        SettingSwitchRow(
            label = "Exibir Rótulos das Pastas",
            checked = formState.showFolderLabels,
            onCheckedChange = callbacks.onFolderLabelsChange
        )

        SettingSwitchRow(
            label = "Modo de Pasta Expansiva (Grid)",
            checked = formState.isGridFolderEnabled,
            onCheckedChange = callbacks.onGridFolderChange
        )
    }
}

@Composable
internal fun SettingSwitchRow(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleText(
            text = label,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

@Composable
internal fun EditContainerItemRow(
    params: EditContainerItemRowParams,
    actions: EditContainerItemRowActions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditContainerItemMoveButtons(params = params, actions = actions)

        Spacer(modifier = Modifier.width(4.dp))

        FolderIconRegistry.RenderIcon(
            name = params.iconName,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

        AureoleText(
            text = params.title,
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f)
        )

        AureoleText(
            text = if (params.isFolder) "Pasta" else "App",
            style = AureoleTheme.typography.labelSmall,
            color = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 4.dp)
        )

        IconButton(onClick = actions.onRemove, modifier = Modifier.size(28.dp)) {
            AureoleDS.icons.Delete(
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun EditContainerItemMoveButtons(
    params: EditContainerItemRowParams,
    actions: EditContainerItemRowActions
) {
    Row {
        IconButton(
            onClick = actions.onMoveUp,
            enabled = params.canMoveUp,
            modifier = Modifier.size(28.dp)
        ) {
            AureoleDS.icons.ArrowUp(
                tint = if (params.canMoveUp) {
                    AureoleTheme.colors.onSurfaceHigh
                } else {
                    AureoleTheme.colors.onSurfaceMedium.copy(alpha = 0.3f)
                },
                modifier = Modifier.size(16.dp)
            )
        }
        IconButton(
            onClick = actions.onMoveDown,
            enabled = params.canMoveDown,
            modifier = Modifier.size(28.dp)
        ) {
            AureoleDS.icons.ArrowDown(
                tint = if (params.canMoveDown) {
                    AureoleTheme.colors.onSurfaceHigh
                } else {
                    AureoleTheme.colors.onSurfaceMedium.copy(alpha = 0.3f)
                },
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditContainerAddAppDropdown(
    allApps: List<AppInfo>,
    onAppSelected: (String) -> Unit,
    label: String = "Adicionar App..."
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredApps = remember(searchQuery, allApps) {
        if (searchQuery.isBlank()) {
            allApps.take(MAX_DROPDOWN_APP_RESULTS)
        } else {
            allApps.filter { it.label.contains(searchQuery, ignoreCase = true) }.take(MAX_DROPDOWN_APP_RESULTS)
        }
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isExpanded = true
            },
            placeholder = { AureoleText(label) },
            leadingIcon = {
                AureoleDS.icons.Search(
                    tint = AureoleTheme.colors.onSurfaceMedium,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = AureoleTheme.colors.outline,
                focusedTextColor = AureoleTheme.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleTheme.colors.onSurfaceHigh
            ),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = isExpanded && filteredApps.isNotEmpty(),
            onDismissRequest = { isExpanded = false }
        ) {
            filteredApps.forEach { app ->
                DropdownAppItem(
                    app = app,
                    onAppSelected = {
                        onAppSelected(it)
                        searchQuery = ""
                        isExpanded = false
                    }
                )
            }
        }
    }
}

@Composable
private fun DropdownAppItem(
    app: AppInfo,
    onAppSelected: (String) -> Unit
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }
    DropdownMenuItem(
        text = { AureoleText(app.label, color = AureoleTheme.colors.onSurfaceHigh) },
        leadingIcon = {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(20.dp)
            )
        },
        onClick = { onAppSelected(app.packageName) }
    )
}
