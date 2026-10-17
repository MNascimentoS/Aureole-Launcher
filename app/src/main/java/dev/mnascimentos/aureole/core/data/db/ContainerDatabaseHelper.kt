package dev.mnascimentos.aureole.core.data.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.core.database.sqlite.transaction
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType

class ContainerDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS containers (
                id TEXT PRIMARY KEY NOT NULL,
                title TEXT NOT NULL,
                position TEXT NOT NULL DEFAULT 'Space Between',
                orientation TEXT NOT NULL DEFAULT 'Vertical',
                is_background_enabled INTEGER NOT NULL DEFAULT 1,
                is_expand_cell INTEGER NOT NULL DEFAULT 0,
                show_add_folder_button INTEGER NOT NULL DEFAULT 1,
                show_folder_labels INTEGER NOT NULL DEFAULT 0,
                is_grid_folder_enabled INTEGER NOT NULL DEFAULT 0,
                hide_text INTEGER NOT NULL DEFAULT 0,
                text_color INTEGER,
                background_color INTEGER,
                stroke_enabled INTEGER NOT NULL DEFAULT 0,
                stroke_color INTEGER,
                folder_color INTEGER,
                folder_stroke_enabled INTEGER NOT NULL DEFAULT 0,
                scroll_up_shortcut TEXT,
                scroll_down_shortcut TEXT,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS container_items (
                id TEXT PRIMARY KEY NOT NULL,
                panel_id TEXT NOT NULL,
                item_type TEXT NOT NULL,
                package_name TEXT,
                folder_id TEXT,
                order_index INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(panel_id) REFERENCES containers(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_container_items_panel_id ON container_items(panel_id)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            try {
                db.execSQL("ALTER TABLE containers ADD COLUMN orientation TEXT NOT NULL DEFAULT 'Vertical'")
            } catch (_: Exception) {
                db.execSQL("DROP TABLE IF EXISTS container_items")
                db.execSQL("DROP TABLE IF EXISTS containers")
                onCreate(db)
                return
            }
        }
        if (oldVersion < DATABASE_VERSION) {
            try {
                db.execSQL("ALTER TABLE containers ADD COLUMN hide_text INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE containers ADD COLUMN text_color INTEGER")
                db.execSQL("ALTER TABLE containers ADD COLUMN background_color INTEGER")
                db.execSQL("ALTER TABLE containers ADD COLUMN stroke_enabled INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE containers ADD COLUMN stroke_color INTEGER")
                db.execSQL("ALTER TABLE containers ADD COLUMN folder_color INTEGER")
                db.execSQL("ALTER TABLE containers ADD COLUMN folder_stroke_enabled INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE containers ADD COLUMN scroll_up_shortcut TEXT")
                db.execSQL("ALTER TABLE containers ADD COLUMN scroll_down_shortcut TEXT")
            } catch (_: Exception) {
                db.execSQL("DROP TABLE IF EXISTS container_items")
                db.execSQL("DROP TABLE IF EXISTS containers")
                onCreate(db)
                return
            }
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun getAllContainers(): List<ContainerEntity> {
        val result = mutableListOf<ContainerEntity>()
        val db = readableDatabase
        db.query("containers", null, null, null, null, null, "created_at ASC").use { c ->
            while (c.moveToNext()) {
                result.add(c.readContainerEntity())
            }
        }
        return result
    }

    fun getContainer(panelId: String): ContainerEntity? {
        val db = readableDatabase
        db.query("containers", null, "id = ?", arrayOf(panelId), null, null, null).use { c ->
            if (c.moveToFirst()) {
                return c.readContainerEntity()
            }
        }
        return null
    }

    fun insertOrUpdateContainer(panel: ContainerEntity) {
        val db = writableDatabase
        db.insertWithOnConflict("containers", null, panel.toContentValues(), SQLiteDatabase.CONFLICT_REPLACE)
    }

    fun deleteContainer(panelId: String) {
        val db = writableDatabase
        db.delete("containers", "id = ?", arrayOf(panelId))
    }

    fun getItemsForPanel(panelId: String): List<ContainerItemEntity> {
        val db = readableDatabase
        val result = mutableListOf<ContainerItemEntity>()

        val cursor = db.query(
            "container_items",
            null,
            "panel_id = ?",
            arrayOf(panelId),
            null,
            null,
            "order_index ASC"
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndexOrThrow("id"))
                val pId = c.getString(c.getColumnIndexOrThrow("panel_id"))
                val itemTypeStr = c.getString(c.getColumnIndexOrThrow("item_type"))
                val packageName = c.getString(c.getColumnIndexOrThrow("package_name"))
                val folderId = c.getString(c.getColumnIndexOrThrow("folder_id"))
                val orderIndex = c.getInt(c.getColumnIndexOrThrow("order_index"))

                val itemType = ContainerItemType.entries.find { it.name == itemTypeStr } ?: ContainerItemType.APP

                result.add(
                    ContainerItemEntity(
                        id = id,
                        panelId = pId,
                        itemType = itemType,
                        packageName = packageName,
                        folderId = folderId,
                        orderIndex = orderIndex
                    )
                )
            }
        }
        return result
    }

    fun updatePanelItems(panelId: String, items: List<ContainerItemEntity>) {
        writableDatabase.transaction {
            delete("container_items", "panel_id = ?", arrayOf(panelId))
            items.forEachIndexed { index, item ->
                val values = ContentValues().apply {
                    put("id", item.id)
                    put("panel_id", panelId)
                    put("item_type", item.itemType.name)
                    put("package_name", item.packageName)
                    put("folder_id", item.folderId)
                    put("order_index", index)
                }
                insert("container_items", null, values)
            }
        }
    }

    companion object {
        private const val DATABASE_NAME = "aureole_launcher_containers.db"
        private const val DATABASE_VERSION = 3
    }
}

private fun Cursor.readContainerEntity(): ContainerEntity {
    return ContainerEntity(
        id = getString(getColumnIndexOrThrow("id")),
        title = getString(getColumnIndexOrThrow("title")),
        position = getString(getColumnIndexOrThrow("position")),
        orientation = optString("orientation") ?: "Vertical",
        isBackgroundEnabled = optBoolean("is_background_enabled", true),
        isExpandCell = optBoolean("is_expand_cell"),
        showAddFolderButton = optBoolean("show_add_folder_button", true),
        showFolderLabels = optBoolean("show_folder_labels"),
        isGridFolderEnabled = optBoolean("is_grid_folder_enabled"),
        hideText = optBoolean("hide_text"),
        textColor = optLong("text_color"),
        backgroundColor = optLong("background_color"),
        strokeEnabled = optBoolean("stroke_enabled"),
        strokeColor = optLong("stroke_color"),
        folderColor = optLong("folder_color"),
        folderStrokeEnabled = optBoolean("folder_stroke_enabled"),
        scrollUpShortcut = optString("scroll_up_shortcut"),
        scrollDownShortcut = optString("scroll_down_shortcut"),
        createdAt = getLong(getColumnIndexOrThrow("created_at"))
    )
}

private fun Cursor.optBoolean(column: String, default: Boolean = false): Boolean {
    val idx = getColumnIndex(column)
    return if (idx != -1) getInt(idx) == 1 else default
}

private fun Cursor.optLong(column: String): Long? {
    val idx = getColumnIndex(column)
    return if (idx != -1 && !isNull(idx)) getLong(idx) else null
}

private fun Cursor.optString(column: String): String? {
    val idx = getColumnIndex(column)
    return if (idx != -1 && !isNull(idx)) getString(idx) else null
}

private fun ContainerEntity.toContentValues(): ContentValues {
    return ContentValues().apply {
        put("id", id)
        put("title", title)
        put("position", position)
        put("orientation", orientation)
        put("is_background_enabled", isBackgroundEnabled.toBit())
        put("is_expand_cell", isExpandCell.toBit())
        put("show_add_folder_button", showAddFolderButton.toBit())
        put("show_folder_labels", showFolderLabels.toBit())
        put("is_grid_folder_enabled", isGridFolderEnabled.toBit())
        put("hide_text", hideText.toBit())
        put("stroke_enabled", strokeEnabled.toBit())
        put("folder_stroke_enabled", folderStrokeEnabled.toBit())
        put("created_at", createdAt)
        textColor?.let { put("text_color", it) } ?: putNull("text_color")
        backgroundColor?.let { put("background_color", it) } ?: putNull("background_color")
        strokeColor?.let { put("stroke_color", it) } ?: putNull("stroke_color")
        folderColor?.let { put("folder_color", it) } ?: putNull("folder_color")
        scrollUpShortcut?.let { put("scroll_up_shortcut", it) } ?: putNull("scroll_up_shortcut")
        scrollDownShortcut?.let { put("scroll_down_shortcut", it) } ?: putNull("scroll_down_shortcut")
    }
}

private fun Boolean.toBit(): Int = if (this) 1 else 0
