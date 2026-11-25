package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.FolderFormContent
import dev.mnascimentos.aureole.composable.dialogs.FolderFormGlyphCard
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormContentActions
import dev.mnascimentos.aureole.composable.dialogs.model.resolveFolderName
import dev.mnascimentos.aureole.composable.dialogs.model.toDetails
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetNav
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonAppearanceSheetContent
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonSheetPreview

internal fun EditContainerState.quickButton(id: String): QuickButtonModel? = quickButtons.find { it.id == id }

private fun QuickButtonModel.appInfoIn(allApps: List<AppInfo>): AppInfo? =
    if (shortcuts.click.type == ShortcutType.OPEN_APP) allApps.find { it.packageName == shortcuts.click.target } else null

/** True while an item is edited in place, so the preview above the sheet is the item's and not the group's. */
internal fun GroupSheetNav.showsItemPreview(state: EditContainerState): Boolean = when (val current = page) {
    is GroupSheetPage.FolderEditor -> true
    is GroupSheetPage.QuickButtonEditor -> state.quickButton(current.quickButtonId) != null
    else -> false
}

/** The preview above the sheet while an item is edited in place: the folder or the button, with its name pill. */
@Composable
internal fun GroupItemEditorPreview(nav: GroupSheetNav, state: EditContainerState, allApps: List<AppInfo>) {
    when (val page = nav.page) {
        is GroupSheetPage.FolderEditor -> {
            val placeholder = stringResource(R.string.home_folder_name_placeholder)
            AureolePreviewFrame {
                FolderFormGlyphCard(
                    draft = nav.folderDraft,
                    label = resolveFolderName(nav.folderDraft.name, placeholder),
                    onNameChange = { name ->
                        val draft = nav.folderDraft.copy(name = name)
                        nav.editFolderDraft(draft)
                        state.updateFolder(page.folderId, draft.toDetails(placeholder))
                    }
                )
            }
        }
        is GroupSheetPage.QuickButtonEditor -> state.quickButton(page.quickButtonId)?.let { model ->
            AureolePreviewFrame {
                QuickButtonSheetPreview(
                    model = model,
                    appInfo = model.appInfoIn(allApps),
                    onNameChange = { name ->
                        state.updateQuickButton(model.id) { it.copy(appearance = model.appearance.copy(label = name)) }
                    }
                )
            }
        }
        else -> Unit
    }
}

/** The folder form of the folder being edited, in place of the tabs; every change goes to the group at once. */
@Composable
internal fun ColumnScope.GroupFolderEditorPage(nav: GroupSheetNav, state: EditContainerState, folderId: String) {
    val placeholder = stringResource(R.string.home_folder_name_placeholder)
    FolderFormContent(
        draft = nav.folderDraft,
        mode = nav.folderMode,
        showDelete = false,
        actions = FolderFormContentActions(
            onDraftChange = { draft ->
                nav.editFolderDraft(draft)
                state.updateFolder(folderId, draft.toDetails(placeholder))
            },
            onModeChange = { nav.folderMode = it },
            onDelete = null
        )
    )
}

/** The look of the quick button being edited, in place of the tabs. */
@Composable
internal fun ColumnScope.GroupQuickButtonEditorPage(state: EditContainerState, quickButtonId: String) {
    state.quickButton(quickButtonId)?.let { model ->
        QuickButtonAppearanceSheetContent(
            activeModel = model,
            onUpdateAppearance = { appearance -> state.updateQuickButton(model.id) { it.copy(appearance = appearance) } },
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}
