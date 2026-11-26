package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import java.util.UUID

data class ContainerModel(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Container",
    val position: String = "Space Between",
    val orientation: String = "Vertical",
    val isBackgroundEnabled: Boolean = false,
    val isExpandCell: Boolean = false,
    val showAddFolderButton: Boolean = true,
    val showFolderLabels: Boolean = false,
    val isGridFolderEnabled: Boolean = true,

    val hideText: Boolean = false,
    val textColor: Long? = null,
    val backgroundColor: Long? = null,
    val strokeEnabled: Boolean = false,
    val strokeColor: Long? = null,
    val folderColor: Long? = null,
    val folderStrokeEnabled: Boolean = false,
    val scrollUpShortcut: String? = null,
    val scrollDownShortcut: String? = null,
    /** Group background; null is "None" (no background, no glass). Kept in step with [isBackgroundEnabled]. */
    val backgroundColorRef: ThemeColorRef? = null,
    /** Background of each item; null is "None". */
    val itemColorRef: ThemeColorRef? = CONTAINER_ITEM_THEME_REF,
    val itemSpacing: ContainerItemSpacing = ContainerItemSpacing.NORMAL,

    val items: List<ContainerItemEntity> = emptyList(),
    val folders: List<AppFolder> = emptyList(),
    val appPackageNames: List<String> = emptyList(),
    val quickButtons: List<QuickButtonModel> = emptyList()
)
