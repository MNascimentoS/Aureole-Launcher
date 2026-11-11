package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutCategory
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSearchCandidate
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.shortcutsInCategory
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
internal fun ShortcutSelectionSheetContent(
    params: ShortcutSelectionSheetParams,
    onApplyShortcut: (ShortcutAction) -> Unit
) {
    if (params.searchQuery.isNotBlank()) {
        ShortcutSearchResultsList(
            query = params.searchQuery.trim(),
            results = params.searchResults,
            currentAction = params.currentAction,
            onApplyShortcut = onApplyShortcut
        )
        return
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
    ) {
        if (params.category == ShortcutCategory.APPS) {
            AppSelectionList(
                apps = params.uiState.apps,
                currentAction = params.currentAction,
                onApplyShortcut = onApplyShortcut
            )
        } else {
            DefaultShortcutSelectionList(params = params, onApplyShortcut = onApplyShortcut)
        }
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
    }
}

@Composable
private fun AppSelectionList(
    apps: List<AppInfo>,
    currentAction: ShortcutAction,
    onApplyShortcut: (ShortcutAction) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        apps.sortedBy { it.label.lowercase() }.forEach { app ->
            val action = ShortcutAction.openApp(app.packageName)
            ShortcutActionRow(
                candidate = ShortcutSearchCandidate(action = action, label = app.label, app = app),
                isSelected = currentAction == action,
                onClick = { onApplyShortcut(action) }
            )
        }
    }
}

@Composable
private fun DefaultShortcutSelectionList(
    params: ShortcutSelectionSheetParams,
    onApplyShortcut: (ShortcutAction) -> Unit
) {
    val actions = shortcutsInCategory(params.defaultShortcutOptions, params.category)
    Column(modifier = Modifier.fillMaxWidth()) {
        if (params.category == ShortcutCategory.AUREOLE) {
            AureoleMenuSectionLabel(text = stringResource(R.string.home_quickbutton_shortcut_section_aureole))
        }
        actions.forEach { action ->
            ShortcutActionRow(
                candidate = ShortcutSearchCandidate(action = action, label = action.displayName),
                isSelected = params.currentAction == action,
                onClick = { onApplyShortcut(action) }
            )
        }
    }
}

@AureolePreview
@Composable
private fun ShortcutSelectionSheetContentPreview() {
    AureoleLauncherTheme {
        ShortcutSelectionSheetContent(
            params = ShortcutSelectionSheetParams(
                category = ShortcutCategory.AUREOLE,
                currentAction = ShortcutAction.AllApps,
                defaultShortcutOptions = listOf(ShortcutAction.AllApps, ShortcutAction.Settings),
                uiState = MainUiState()
            ),
            onApplyShortcut = {}
        )
    }
}
