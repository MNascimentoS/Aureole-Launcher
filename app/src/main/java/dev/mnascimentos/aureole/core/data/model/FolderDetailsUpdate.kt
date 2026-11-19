package dev.mnascimentos.aureole.core.data.model

import androidx.room.ColumnInfo

/** Columns of `folders` that the folder form writes when a folder is edited (a partial [FolderEntity]). */
data class FolderDetailsUpdate(
    val id: String,
    val name: String,
    val icon: String?,
    @ColumnInfo(name = "icon_type")
    val iconType: FolderIconType,
    val emoji: String?,
    /** JSON of the folder's own [dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef]. */
    val color: String?
) {
    companion object {
        fun of(folderId: String, details: FolderDetails): FolderDetailsUpdate = FolderDetailsUpdate(
            id = folderId,
            name = details.name,
            icon = details.icon,
            iconType = details.iconType,
            emoji = details.emoji,
            color = details.color?.toJson()
        )
    }
}
