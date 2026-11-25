package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.compose.ui.unit.DpSize
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.CONTAINER_ITEM_THEME_REF
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemSpacing
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef

/**
 * Parameters for [dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen].
 *
 * @property isNew The group was just added from "Add component" (RF-A4): closing it empty removes it.
 * @property areTipsSeen The first group's tips were already seen (RF-F1).
 * @property onTipsSeen Saves that the tips were seen (skipped or finished).
 * @property homeSize Size the group has on the home, for the preview (RF-B3); null when never measured.
 * @property initialTab Tab the sheet opens on.
 * @property initialPage Page the sheet opens on, in place of the tabs.
 * @property startTip Tip the sheet opens with, in place of the one [isNew] and [areTipsSeen] give.
 */
data class EditContainerScreenParams(
    val panel: ContainerModel,
    val allApps: List<AppInfo>,
    val onDismiss: () -> Unit,
    val onSave: (ContainerModel) -> Unit,
    val hazeState: HazeState? = null,
    val isNew: Boolean = false,
    val areTipsSeen: Boolean = true,
    val onTipsSeen: () -> Unit = {},
    val homeSize: DpSize? = null,
    val initialTab: GroupTab = GroupTab.ITEMS,
    val initialPage: GroupSheetPage = GroupSheetPage.Tabs,
    val startTip: GroupTip? = null
)

data class ContainerOptionsState(
    val position: String,
    val orientation: String,
    val isBackgroundEnabled: Boolean,
    val isExpandCell: Boolean,
    val showAddFolderButton: Boolean,
    val showFolderLabels: Boolean,
    val isGridFolderEnabled: Boolean,
    val hideText: Boolean,
    val textColor: Long?,
    val backgroundColor: Long?,
    val strokeEnabled: Boolean,
    val strokeColor: Long?,
    val folderColor: Long?,
    val folderStrokeEnabled: Boolean,
    val scrollUpShortcut: String?,
    val scrollDownShortcut: String?,
    val backgroundColorRef: ThemeColorRef? = null,
    val itemColorRef: ThemeColorRef? = CONTAINER_ITEM_THEME_REF,
    val itemSpacing: ContainerItemSpacing = ContainerItemSpacing.NORMAL
)

/**
 * One row of "In the group" (RF-C2): the [item], what it needs to show its icon and name, [onOpen] for the editor
 * of a folder or a quick button (null for apps) and [onRemove] for its ×.
 */
data class GroupItemRowParams(
    val item: ContainerItemEntity,
    val folders: List<AppFolder>,
    val allApps: List<AppInfo>,
    val onRemove: () -> Unit,
    val onOpen: (() -> Unit)? = null
)
