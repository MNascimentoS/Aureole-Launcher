package dev.mnascimentos.aureole.core.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "containers")
data class ContainerEntity(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val title: String = "Painel Lateral",
    val position: String = "Space Between",
    val orientation: String = "Vertical",
    @ColumnInfo(name = "is_background_enabled")
    val isBackgroundEnabled: Boolean = true,
    @ColumnInfo(name = "is_expand_cell")
    val isExpandCell: Boolean = false,
    @ColumnInfo(name = "show_add_folder_button")
    val showAddFolderButton: Boolean = true,
    @ColumnInfo(name = "show_folder_labels")
    val showFolderLabels: Boolean = false,
    @ColumnInfo(name = "is_grid_folder_enabled")
    val isGridFolderEnabled: Boolean = false,

    @ColumnInfo(name = "hide_text")
    val hideText: Boolean = false,
    @ColumnInfo(name = "text_color")
    val textColor: Long? = null,
    @ColumnInfo(name = "background_color")
    val backgroundColor: Long? = null,
    @ColumnInfo(name = "stroke_enabled")
    val strokeEnabled: Boolean = false,
    @ColumnInfo(name = "stroke_color")
    val strokeColor: Long? = null,
    @ColumnInfo(name = "folder_color")
    val folderColor: Long? = null,
    @ColumnInfo(name = "folder_stroke_enabled")
    val folderStrokeEnabled: Boolean = false,
    @ColumnInfo(name = "scroll_up_shortcut")
    val scrollUpShortcut: String? = null,
    @ColumnInfo(name = "scroll_down_shortcut")
    val scrollDownShortcut: String? = null,

    @ColumnInfo(name = "created_at")
    val createdAt: Long = System.currentTimeMillis()
)
