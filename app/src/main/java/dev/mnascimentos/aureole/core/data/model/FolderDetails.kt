package dev.mnascimentos.aureole.core.data.model

import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef

/**
 * What the folder form edits: the name and how the folder looks on the home (pasta-form RF-08 to RF-10). The
 * apps of the folder are not part of it.
 */
data class FolderDetails(
    val name: String,
    val icon: String? = null,
    val iconType: FolderIconType = FolderIconType.ICON,
    val emoji: String? = null,
    val color: ThemeColorRef? = null,
)

/** The name and look of this folder. */
fun AppFolder.details(): FolderDetails = FolderDetails(
    name = name,
    icon = icon,
    iconType = iconType,
    emoji = emoji,
    color = color
)

/** This folder with the name and look of [details]; id, container and apps are kept. */
fun AppFolder.withDetails(details: FolderDetails): AppFolder = copy(
    name = details.name,
    icon = details.icon,
    iconType = details.iconType,
    emoji = details.emoji,
    color = details.color
)
