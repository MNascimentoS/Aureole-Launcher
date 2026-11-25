package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel

/**
 * An item just removed from the group with ×, kept for its Undo (RF-C6): where it was ([index]), the item, and the
 * folder or quick button it took along. [id] tells two removals apart for the snackbar.
 */
data class RemovedGroupItem(
    val id: Long,
    val index: Int,
    val item: ContainerItemEntity,
    val name: String,
    val folder: AppFolder? = null,
    val quickButton: QuickButtonModel? = null
)

/** "1 item", "5 items": the one count of the group, the same in the list and in "Add apps" (RF-C2). */
fun groupItemCount(items: List<ContainerItemEntity>): Int = items.size

/** Whether a new folder with [selectedCount] apps can be created (D-09). */
fun canCreateFolder(selectedCount: Int): Boolean = selectedCount >= GroupSheetMetrics.MIN_FOLDER_APPS

/** [selected] with [packageName] added at the end, or taken out when it is there (RF-C4). */
fun toggledSelection(selected: List<String>, packageName: String): List<String> =
    if (packageName in selected) selected - packageName else selected + packageName
