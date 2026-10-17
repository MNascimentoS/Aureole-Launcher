package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderIconRegistry

private const val MAX_DROPDOWN_APP_RESULTS = 8

@Composable
internal fun EditContainerItemRow(
    params: EditContainerItemRowParams,
    actions: EditContainerItemRowActions
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        EditContainerItemMoveButtons(params = params, actions = actions)

        Spacer(modifier = Modifier.width(4.dp))

        FolderIconRegistry.RenderIcon(
            name = params.iconName,
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(20.dp)
        )

        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

        AureoleText(
            text = params.title,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )

        AureoleText(
            text = if (params.isFolder) "Pasta" else "App",
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = 4.dp),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
        )

        IconButton(onClick = actions.onRemove, modifier = Modifier.size(28.dp)) {
            AureoleDS.icons.Delete(
                tint = AureoleDS.colors.error,
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
                    AureoleDS.colors.onSurfaceHigh
                } else {
                    AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.3f)
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
                    AureoleDS.colors.onSurfaceHigh
                } else {
                    AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.3f)
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
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(18.dp)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedBorderColor = AureoleDS.colors.outline,
                focusedTextColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
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
        text = { AureoleText(app.label, color = AureoleDS.colors.onSurfaceHigh) },
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

@AureolePreview
@Composable
private fun EditContainerItemRowPreview() {
    AureoleLauncherTheme {
        EditContainerItemRow(
            params = EditContainerItemRowParams(
                title = "Work Folder",
                iconName = "briefcase",
                isFolder = true,
                canMoveUp = false,
                canMoveDown = true
            ),
            actions = EditContainerItemRowActions(
                onMoveUp = {},
                onMoveDown = {},
                onRemove = {}
            )
        )
    }
}
