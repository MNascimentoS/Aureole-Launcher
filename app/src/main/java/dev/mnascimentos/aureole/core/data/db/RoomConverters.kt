package dev.mnascimentos.aureole.core.data.db

import androidx.room.TypeConverter
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.FolderIconType

class RoomConverters {
    @TypeConverter
    fun fromContainerItemType(value: ContainerItemType?): String? {
        return value?.name
    }

    @TypeConverter
    fun toContainerItemType(value: String?): ContainerItemType? {
        return value?.let {
            try {
                ContainerItemType.valueOf(it)
            } catch (_: IllegalArgumentException) {
                ContainerItemType.APP
            }
        }
    }

    @TypeConverter
    fun fromFolderIconType(value: FolderIconType?): String? = value?.name

    @TypeConverter
    fun toFolderIconType(value: String?): FolderIconType =
        FolderIconType.entries.firstOrNull { it.name == value } ?: FolderIconType.ICON
}
