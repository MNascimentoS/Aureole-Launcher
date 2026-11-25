package dev.mnascimentos.aureole.core.data.repository

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.containerItemSpacingOf
import dev.mnascimentos.aureole.core.designsystem.palette.parseThemeColorRef

/** The group saved as [this] row, with its [items] (quick buttons already read) and its [folders]. */
internal fun ContainerEntity.toContainerModel(items: List<ContainerItemEntity>, folders: List<AppFolder>) =
    ContainerModel(
        id = id,
        title = title,
        position = position,
        orientation = orientation,
        isBackgroundEnabled = isBackgroundEnabled,
        isExpandCell = isExpandCell,
        showAddFolderButton = showAddFolderButton,
        showFolderLabels = showFolderLabels,
        isGridFolderEnabled = isGridFolderEnabled,
        hideText = hideText,
        textColor = textColor,
        backgroundColor = backgroundColor,
        strokeEnabled = strokeEnabled,
        strokeColor = strokeColor,
        folderColor = folderColor,
        folderStrokeEnabled = folderStrokeEnabled,
        scrollUpShortcut = scrollUpShortcut,
        scrollDownShortcut = scrollDownShortcut,
        backgroundColorRef = parseThemeColorRef(backgroundColorRef),
        itemColorRef = parseThemeColorRef(itemColorRef),
        itemSpacing = containerItemSpacingOf(itemSpacing),
        items = items,
        folders = folders,
        appPackageNames = items.filter { it.itemType == ContainerItemType.APP }.mapNotNull { it.packageName },
        quickButtons = items.filter { it.itemType == ContainerItemType.QUICK_BUTTON }.mapNotNull { it.quickButtonModel }
    )

/** The row that saves [this] group's own settings (its items and folders are saved apart). */
internal fun ContainerModel.toContainerEntity() = ContainerEntity(
    id = id,
    title = title,
    position = position,
    orientation = orientation,
    isBackgroundEnabled = isBackgroundEnabled,
    isExpandCell = isExpandCell,
    showAddFolderButton = showAddFolderButton,
    showFolderLabels = showFolderLabels,
    isGridFolderEnabled = isGridFolderEnabled,
    hideText = hideText,
    textColor = textColor,
    backgroundColor = backgroundColor,
    strokeEnabled = strokeEnabled,
    strokeColor = strokeColor,
    folderColor = folderColor,
    folderStrokeEnabled = folderStrokeEnabled,
    scrollUpShortcut = scrollUpShortcut,
    scrollDownShortcut = scrollDownShortcut,
    backgroundColorRef = backgroundColorRef?.toJson(),
    itemColorRef = itemColorRef?.toJson(),
    itemSpacing = itemSpacing.name
)
