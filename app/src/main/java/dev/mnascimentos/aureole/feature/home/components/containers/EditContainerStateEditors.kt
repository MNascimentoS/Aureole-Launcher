package dev.mnascimentos.aureole.feature.home.components.containers

import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.data.model.withDetails
import java.util.UUID

/** "Quick button": adds a button whose tap runs [action] (RF-C5). */
internal fun EditContainerState.addQuickButton(action: ShortcutAction): String =
    addQuickButton(QuickButtonModel(shortcuts = QuickButtonShortcuts(click = action)))

/** Adds a quick button and returns its id, so the sheet can open its editor right away (RF-07). */
internal fun EditContainerState.addQuickButton(model: QuickButtonModel = QuickButtonModel()): String {
    val qbId = if (model.id.isNotEmpty()) model.id else UUID.randomUUID().toString()
    val newQuickButton = model.copy(id = qbId)
    quickButtons = quickButtons + newQuickButton
    itemsList = itemsList + ContainerItemEntity(
        id = UUID.randomUUID().toString(),
        panelId = panel.id,
        itemType = ContainerItemType.QUICK_BUTTON,
        quickButtonId = newQuickButton.id,
        orderIndex = itemsList.size
    ).also { it.quickButtonModel = newQuickButton }
    return qbId
}

/** Writes the name and look chosen in the in-place folder editor to the folder [folderId]. */
internal fun EditContainerState.updateFolder(folderId: String, details: FolderDetails) {
    folders = folders.map { if (it.id == folderId) it.withDetails(details) else it }
}

/**
 * Writes the change [change] made in the in-place quick button editor (the look or the shortcuts) to the button
 * [quickButtonId].
 */
internal fun EditContainerState.updateQuickButton(
    quickButtonId: String,
    change: (QuickButtonModel) -> QuickButtonModel
) {
    val updated = quickButtons.find { it.id == quickButtonId }?.let(change) ?: return
    quickButtons = quickButtons.map { if (it.id == quickButtonId) updated else it }
    // The entity keeps its model outside equals(), so a new list would compare equal and not be stored:
    // the item's model is updated where it is (the save reads it before the quick button list).
    itemsList.filter { item ->
        item.itemType == ContainerItemType.QUICK_BUTTON &&
            (item.quickButtonId == quickButtonId || item.id == quickButtonId)
    }.forEach { it.quickButtonModel = updated }
}
