package dev.mnascimentos.aureole.feature.home.components.model

import dev.mnascimentos.aureole.core.data.model.AppInfo

data class AppItemBottomSheetParams(
    val app: AppInfo,
    val isFavorite: Boolean = false,
    val actions: AppItemRowActions = AppItemRowActions(),
    val onRemoveFromContainer: (() -> Unit)? = null,
    val onOpenContainerSettings: (() -> Unit)? = null,
    val onRemoveFromFolder: (() -> Unit)? = null,
    val onOpenFolderSettings: (() -> Unit)? = null,
)

data class WidgetStackBottomSheetParams(
    val widgetId: Int?,
    val stackId: String,
    val onEditStack: () -> Unit,
    val onToggleDots: (String) -> Unit,
    val onRemoveStack: () -> Unit,
)
