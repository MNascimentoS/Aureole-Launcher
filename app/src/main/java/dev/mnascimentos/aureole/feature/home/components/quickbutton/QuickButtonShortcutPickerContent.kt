package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.shortcutCategoryOf
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * "Select a shortcut" for the click of [model], drawn in place inside a sheet that already scrolls (the container's
 * "Manage items", after the appearance of a new quick button): the category chips and the list of shortcuts or apps.
 * [onPick] gets the chosen shortcut.
 */
@Composable
internal fun QuickButtonShortcutPickerContent(
    model: QuickButtonModel,
    apps: List<AppInfo>,
    onPick: (ShortcutAction) -> Unit,
    modifier: Modifier = Modifier
) {
    val current = model.shortcuts.click
    var category by remember { mutableStateOf(shortcutCategoryOf(current)) }
    val candidates = rememberDefaultShortcutCandidates(apps, current)
    Column(modifier = modifier.fillMaxWidth()) {
        ShortcutCategoryChips(
            selected = category,
            onSelect = { category = it },
            modifier = Modifier.padding(bottom = AureoleDS.dimens.small)
        )
        ShortcutSelectionSheetContent(
            params = ShortcutSelectionSheetParams(
                category = category,
                currentAction = current,
                defaultCandidates = candidates,
                uiState = MainUiState(apps = apps),
                editingType = "click"
            ),
            onApplyShortcut = onPick,
            isScrollable = false
        )
    }
}
