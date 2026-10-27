package dev.mnascimentos.aureole.core.data.db

import androidx.room.TypeConverter
import dev.mnascimentos.aureole.core.data.model.ContainerItemType

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
}
