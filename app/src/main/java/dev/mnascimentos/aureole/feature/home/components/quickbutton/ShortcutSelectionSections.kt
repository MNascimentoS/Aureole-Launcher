package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.shortcutCategoryOf
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
internal fun ShortcutSelectionSheetContent(
    params: ShortcutSelectionSheetParams,
    onApplyShortcut: (ShortcutAction) -> Unit,
    isScrollable: Boolean = true
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
    // Inside a sheet that already scrolls (the container's "Manage items") the list does not scroll on its own.
    val listModifier = if (isScrollable) {
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
    } else {
        Modifier.fillMaxWidth()
    }
    Column(modifier = listModifier) {
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
    val candidates = params.defaultCandidates.filter { shortcutCategoryOf(it.action) == params.category }
    Column(modifier = Modifier.fillMaxWidth()) {
        if (params.category == ShortcutCategory.AUREOLE) {
            AureoleMenuSectionLabel(text = stringResource(R.string.home_quickbutton_shortcut_section_aureole))
        }
        candidates.forEach { candidate ->
            ShortcutActionRow(
                candidate = candidate,
                isSelected = params.currentAction == candidate.action,
                onClick = { onApplyShortcut(candidate.action) }
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
                defaultCandidates = listOf(
                    ShortcutSearchCandidate(ShortcutAction.AllApps, "All apps"),
                    ShortcutSearchCandidate(ShortcutAction.Settings, "Aureole Settings")
                ),
                uiState = MainUiState()
            ),
            onApplyShortcut = {}
        )
    }
}
