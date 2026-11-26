package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.dialogs.FolderNamePill
import dev.mnascimentos.aureole.composable.search.AureoleSearchField
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Check
import dev.mnascimentos.aureole.core.designsystem.icons.Plus
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.search.rankByQuery
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderAppPickerPanel
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupAppListParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.model.NewFolderDraft
import dev.mnascimentos.aureole.feature.home.components.containers.model.appsMissingForFolder
import dev.mnascimentos.aureole.feature.home.components.containers.model.canCreateFolder

/**
 * "Add apps" (RF-C3, D-10): the search and every app with its mark, "+" (out of the group) or a check on a white
 * circle (in it). A tap puts the app at the end of the group or takes it out, and the preview follows at once.
 */
@Composable
internal fun ColumnScope.GroupAddAppsPage(params: GroupAppListParams) {
    GroupAppList(params)
}

/**
 * "New folder" (RF-C4): FOLDER NAME, then the folder's own app picker (grid with drag-to-mark and A-Z, tray of the
 * chosen apps in order, search). The button that creates it sits at the foot of the sheet ([GroupNewFolderFooter]);
 * [onAppsChange] gets the chosen packages as they change.
 */
@Composable
internal fun ColumnScope.GroupNewFolderPage(
    draft: NewFolderDraft,
    onNameChange: (String) -> Unit,
    allApps: List<AppInfo>,
    onAppsChange: (List<String>) -> Unit
) {
    val selected = remember { mutableStateListOf<String>().apply { addAll(draft.apps) } }
    LaunchedEffect(Unit) { snapshotFlow { selected.toList() }.collect(onAppsChange) }
    GroupSectionLabel(text = stringResource(R.string.home_container_group_folder_name_label))
    FolderNamePill(value = draft.name, onValueChange = onNameChange, modifier = Modifier.fillMaxWidth())
    Spacer(modifier = Modifier.height(AureoleDS.dimens.small))
    FolderAppPickerPanel(
        allApps = allApps,
        selectedPackages = selected,
        isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode
    )
}

/**
 * "Create folder with N apps"; while fewer than 2 are picked it is off and says how many are missing ("Choose 2 apps
 * to create", D-09, onboarding-redesenho RF-C7), never "with 0 app".
 */
@Composable
internal fun ColumnScope.GroupNewFolderFooter(selectedCount: Int, onCreate: () -> Unit) {
    val isEnabled = canCreateFolder(selectedCount)
    TextButton(
        onClick = onCreate,
        enabled = isEnabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .alpha(if (isEnabled) 1f else GroupSheetMetrics.DISABLED_ALPHA)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusFull))
            .background(AureoleDS.colors.onSurfaceHigh)
    ) {
        val missing = appsMissingForFolder(selectedCount)
        AureoleText(
            text = if (isEnabled) {
                pluralStringResource(R.plurals.home_container_group_create_folder, selectedCount, selectedCount)
            } else {
                pluralStringResource(R.plurals.home_container_group_create_folder_missing, missing, missing)
            },
            color = AureoleDS.colors.surface,
            format = AureoleTextFormatParams(style = AureoleDS.typography.labelLarge, fontWeight = FontWeight.Bold)
        )
    }
}

/** The search field and the apps, A to Z or ranked by what is typed, each with its mark. */
@Composable
private fun ColumnScope.GroupAppList(params: GroupAppListParams) {
    AureoleSearchField(
        query = params.query,
        onQueryChange = params.onQueryChange,
        placeholder = stringResource(R.string.home_container_group_search),
        onSearch = {}
    )
    val sorted = remember(params.allApps) { params.allApps.sortedBy { it.label.lowercase() } }
    val shown = remember(sorted, params.query) { rankByQuery(sorted, params.query) { it.label } }
    LazyColumn(
        modifier = Modifier
            .weight(1f, fill = false)
            .padding(top = AureoleDS.dimens.xSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall)
    ) {
        items(items = shown, key = { it.packageName }) { app ->
            GroupAppToggleRow(
                app = app,
                isMarked = params.isMarked(app.packageName)
            ) { params.onToggle(app.packageName) }
        }
    }
}

/** One app of the list: icon, name and its mark ("+" or a check on a white circle); the whole row toggles it. */
@Composable
private fun GroupAppToggleRow(app: AppInfo, isMarked: Boolean, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
            .toggleable(value = isMarked, role = Role.Checkbox, onValueChange = { onToggle() })
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xxSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        AureoleAppIcon(
            app = app,
            size = AureoleDS.dimens.xLarge,
            params = AureoleAppIconParams(legacyShape = RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
        )
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium, maxLines = 1)
        )
        GroupAppMark(isMarked = isMarked)
    }
}

@Composable
private fun GroupAppMark(isMarked: Boolean) {
    Box(
        modifier = Modifier
            .size(AureoleDS.dimens.iconXLargeMedium)
            .clip(CircleShape)
            .background(if (isMarked) AureoleDS.colors.onSurfaceHigh else AureoleDS.colors.surfaceVariant),
        contentAlignment = Alignment.Center
    ) {
        val iconModifier = Modifier.size(AureoleDS.dimens.iconSmall)
        if (isMarked) {
            AureoleDS.icons.Check(tint = AureoleDS.colors.surface, modifier = iconModifier)
        } else {
            AureoleDS.icons.Plus(tint = AureoleDS.colors.onSurfaceHigh, modifier = iconModifier)
        }
    }
}
