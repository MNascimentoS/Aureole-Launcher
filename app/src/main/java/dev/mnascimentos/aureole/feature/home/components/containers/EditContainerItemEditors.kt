package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.FolderFormContent
import dev.mnascimentos.aureole.composable.dialogs.FolderFormPreview
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormContentActions
import dev.mnascimentos.aureole.composable.dialogs.model.folderFormTitleRes
import dev.mnascimentos.aureole.composable.dialogs.model.resolveFolderName
import dev.mnascimentos.aureole.composable.dialogs.model.toDetails
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemsEditor
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemsPage
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonAppearanceSheetContent
import dev.mnascimentos.aureole.feature.home.components.quickbutton.QuickButtonSheetPreview

/** Title of "Manage items" for its current page: the list, the folder form or the quick button editor (RF-07). */
@Composable
@ReadOnlyComposable
internal fun ManageItemsEditor.title(): String = when (page) {
    ManageItemsPage.ItemList -> stringResource(R.string.home_container_edit_manage_items)
    is ManageItemsPage.FolderEditor -> stringResource(folderFormTitleRes(folderMode, isEditing = true))
    is ManageItemsPage.QuickButtonEditor -> stringResource(R.string.home_quickbutton_appearance_title)
}

private fun EditContainerState.quickButton(id: String): QuickButtonModel? = quickButtons.find { it.id == id }

private fun QuickButtonModel.appInfoIn(allApps: List<AppInfo>): AppInfo? =
    if (shortcuts.click.type == ShortcutType.OPEN_APP) {
        allApps.find { it.packageName == shortcuts.click.target }
    } else {
        null
    }

/** True while an item is edited in place, so the preview above the sheet is the item's and not the container's. */
internal fun ManageItemsEditor.showsItemPreview(state: EditContainerState): Boolean = when (val current = page) {
    ManageItemsPage.ItemList -> false
    is ManageItemsPage.FolderEditor -> true
    is ManageItemsPage.QuickButtonEditor -> state.quickButton(current.quickButtonId) != null
}

/** The preview above the sheet while an item is edited in place: the folder or the button, with its name pill. */
@Composable
internal fun ManageItemsEditorPreview(
    editor: ManageItemsEditor,
    state: EditContainerState,
    allApps: List<AppInfo>
) {
    val page = editor.page
    val quickButton = (page as? ManageItemsPage.QuickButtonEditor)?.let { state.quickButton(it.quickButtonId) }
    when {
        page is ManageItemsPage.FolderEditor -> {
            val placeholder = stringResource(R.string.home_folder_name_placeholder)
            AureolePreviewFrame {
                FolderFormPreview(
                    draft = editor.folderDraft,
                    label = resolveFolderName(editor.folderDraft.name, placeholder),
                    onNameChange = { name ->
                        val draft = editor.folderDraft.copy(name = name)
                        editor.editFolderDraft(draft)
                        state.updateFolder(page.folderId, draft.toDetails(placeholder))
                    }
                )
            }
        }
        page is ManageItemsPage.QuickButtonEditor && quickButton != null -> {
            val model = quickButton
            AureolePreviewFrame {
                QuickButtonSheetPreview(
                    model = model,
                    appInfo = model.appInfoIn(allApps),
                    onNameChange = { name ->
                        state.updateQuickButtonAppearance(model.id, model.appearance.copy(label = name))
                    }
                )
            }
        }
        else -> Unit
    }
}

/** The editor of the item open in [editor], in place of the list, with "Back" to return to it. */
@Composable
internal fun ColumnScope.ManageItemsEditorContent(editor: ManageItemsEditor, state: EditContainerState) {
    when (val page = editor.page) {
        ManageItemsPage.ItemList -> Unit
        is ManageItemsPage.FolderEditor -> {
            val placeholder = stringResource(R.string.home_folder_name_placeholder)
            FolderFormContent(
                draft = editor.folderDraft,
                mode = editor.folderMode,
                showDelete = false,
                actions = FolderFormContentActions(
                    onDraftChange = { draft ->
                        editor.editFolderDraft(draft)
                        state.updateFolder(page.folderId, draft.toDetails(placeholder))
                    },
                    onModeChange = { editor.folderMode = it },
                    onDelete = null
                )
            )
        }
        is ManageItemsPage.QuickButtonEditor -> state.quickButton(page.quickButtonId)?.let { model ->
            QuickButtonAppearanceSheetContent(
                activeModel = model,
                onUpdateAppearance = { state.updateQuickButtonAppearance(model.id, it) },
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
    Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
    EditContainerBackButton(onClick = { editor.back() })
}

@AureolePreview
@Composable
private fun ManageItemsQuickButtonEditorPreview() {
    AureoleLauncherTheme {
        val state = rememberEditContainerState(ContainerModel(id = "preview"))
        val editor = remember { ManageItemsEditor().apply { openQuickButton(state.addQuickButton()) } }
        Column {
            ManageItemsEditorPreview(editor = editor, state = state, allApps = emptyList())
            ManageItemsEditorContent(editor = editor, state = state)
        }
    }
}
