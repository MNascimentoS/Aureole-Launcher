package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.composable.reorder.model.ReorderAutoScroll
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerState

/** What the Items tab needs besides the group: the sheet's navigation, the tip shown and the list's auto scroll. */
internal class GroupItemsTabParams(
    val state: EditContainerState,
    val allApps: List<AppInfo>,
    val nav: GroupSheetNav,
    val tip: GroupTip?,
    val autoScroll: ReorderAutoScroll?,
    val onRemove: (index: Int, name: String) -> Unit
)

/** What a page of apps ("Add apps", "New folder") shows: every app, the [query] typed and the apps marked. */
internal class GroupAppListParams(
    val allApps: List<AppInfo>,
    val query: String,
    val onQueryChange: (String) -> Unit,
    val isMarked: (String) -> Boolean,
    val onToggle: (String) -> Unit
)

/** The "removed" snackbar of the sheet: the item just removed ([removed]), its Undo and its time out (RF-C6). */
internal class GroupRemovalBar(
    val removed: RemovedGroupItem?,
    val isLeftHanded: Boolean,
    val onUndo: () -> Unit,
    val onTimeout: (Long) -> Unit
)

/** The removal of the sheet: its snackbar ([bar]) and what × runs ([remove], with the index and name of the item). */
internal class GroupRemoval(val bar: GroupRemovalBar, val remove: (index: Int, name: String) -> Unit)

/** What the content of the group sheet needs: the group, the apps, where the sheet is and the tips. */
internal class GroupSheetBodyParams(
    val state: EditContainerState,
    val allApps: List<AppInfo>,
    val nav: GroupSheetNav,
    val tips: GroupTipFlow,
    val onRemove: (index: Int, name: String) -> Unit,
    val onSelectTab: (GroupTab) -> Unit
)
