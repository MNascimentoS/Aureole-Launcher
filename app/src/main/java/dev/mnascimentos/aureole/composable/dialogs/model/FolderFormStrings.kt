package dev.mnascimentos.aureole.composable.dialogs.model

import dev.mnascimentos.aureole.R

/** Title of the sheet: the grid of icons, editing a folder or a new one. */
fun folderFormTitleRes(mode: FolderFormMode, isEditing: Boolean): Int = when {
    mode == FolderFormMode.ALL_ICONS -> R.string.home_folder_all_icons
    isEditing -> R.string.home_folder_edit_title
    else -> R.string.home_folder_new_title
}

/** Label of the only button of a new folder: it also opens the app picker, or just creates. */
fun folderFormPrimaryRes(pickAppsOnCreate: Boolean): Int =
    if (pickAppsOnCreate) R.string.home_folder_create_pick_apps else R.string.core_common_create
