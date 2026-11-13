package dev.mnascimentos.aureole.composable.appitem.model

import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.util.AureoleShortcutItem

data class AppItemBottomSheetParams(
    val app: AppInfo,
    val isFavorite: Boolean = false,
    val actions: AppItemRowActions = AppItemRowActions(),
    val onRemoveFromContainer: (() -> Unit)? = null,
    val onOpenContainerSettings: (() -> Unit)? = null,
    val onRemoveFromFolder: (() -> Unit)? = null,
    val onOpenFolderSettings: (() -> Unit)? = null,
    /** App shortcuts to list; null asks the system for them. */
    val shortcuts: List<AureoleShortcutItem>? = null,
)
