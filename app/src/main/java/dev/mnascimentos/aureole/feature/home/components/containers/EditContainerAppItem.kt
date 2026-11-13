package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.ExposedDropdownMenuBoxScope
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.dialogs.FolderIconRegistry
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerItemRowParams
import kotlinx.coroutines.delay

private const val MAX_DROPDOWN_APP_RESULTS = 8

private const val DEBOUNCE_DELAY_MS = 300L

@Composable
internal fun EditContainerItemRow(
    params: EditContainerItemRowParams,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.5f))
            .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        FolderIconRegistry.RenderIcon(
            name = params.iconName,
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )

        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

        AureoleText(
            text = params.title,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
        )

        AureoleText(
            text = if (params.isFolder) {
                stringResource(R.string.home_container_edit_folder_label)
            } else {
                stringResource(R.string.home_container_edit_app_label)
            },
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.padding(end = AureoleDS.dimens.xxSmall),
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall)
        )

        IconButton(onClick = onRemove, modifier = Modifier.size(AureoleDS.dimens.large)) {
            AureoleDS.icons.Delete(
                tint = AureoleDS.colors.error,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun EditContainerAddAppDropdown(
    allApps: List<AppInfo>,
    onAppSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.home_container_edit_add_app)
) {
    var isExpanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var debouncedQuery by remember { mutableStateOf("") }

    LaunchedEffect(searchQuery) {
        delay(DEBOUNCE_DELAY_MS)
        debouncedQuery = searchQuery
    }

    val filteredApps = remember(debouncedQuery, allApps) {
        if (debouncedQuery.isBlank()) {
            allApps.take(MAX_DROPDOWN_APP_RESULTS)
        } else {
            allApps.filter { it.label.contains(debouncedQuery, ignoreCase = true) }.take(MAX_DROPDOWN_APP_RESULTS)
        }
    }

    ExposedDropdownMenuBox(
        expanded = isExpanded,
        onExpandedChange = { isExpanded = !isExpanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = {
                searchQuery = it
                isExpanded = true
            },
            singleLine = true,
            placeholder = { AureoleText(label) },
            leadingIcon = {
                AureoleDS.icons.Search(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
            shape = RoundedCornerShape(AureoleDS.dimens.radiusSmall),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedBorderColor = AureoleDS.colors.outline,
                focusedTextColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
            ),
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable).fillMaxWidth()
        )

        AddAppDropdownMenu(
            isExpanded = isExpanded && filteredApps.isNotEmpty(),
            filteredApps = filteredApps,
            onDismiss = { isExpanded = false },
            onAppSelected = {
                onAppSelected(it)
                searchQuery = ""
                isExpanded = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExposedDropdownMenuBoxScope.AddAppDropdownMenu(
    isExpanded: Boolean,
    filteredApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppSelected: (String) -> Unit
) {
    ExposedDropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(AureoleDS.dimens.radiusMedium),
        containerColor = AureoleDS.colors.surfaceVariant,
        border = BorderStroke(
            width = AureoleDS.dimens.borderMinimal,
            color = AureoleDS.colors.outline.copy(alpha = 0.3f)
        ),
        shadowElevation = 8.dp,
        modifier = Modifier.padding(vertical = AureoleDS.dimens.xxSmall)
    ) {
        filteredApps.forEach { app ->
            DropdownAppItem(app = app, onAppSelected = onAppSelected)
        }
    }
}

@Composable
private fun DropdownAppItem(
    app: AppInfo,
    onAppSelected: (String) -> Unit
) {
    DropdownMenuItem(
        text = {
            AureoleText(
                text = app.label,
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        },
        leadingIcon = {
            AureoleAppIcon(
                app = app,
                size = AureoleDS.dimens.iconMedium,
                params = AureoleAppIconParams(contentDescription = app.label)
            )
        },
        onClick = { onAppSelected(app.packageName) },
        modifier = Modifier
            .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.minimal)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
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
                isFolder = true
            ),
            onRemove = {}
        )
    }
}
