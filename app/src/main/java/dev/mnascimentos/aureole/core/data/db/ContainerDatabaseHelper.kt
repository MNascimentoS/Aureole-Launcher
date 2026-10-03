package dev.mnascimentos.aureole.core.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
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
                is_background_enabled INTEGER NOT NULL DEFAULT 1,
                is_expand_cell INTEGER NOT NULL DEFAULT 0,
                show_add_folder_button INTEGER NOT NULL DEFAULT 1,
                show_folder_labels INTEGER NOT NULL DEFAULT 0,
                is_grid_folder_enabled INTEGER NOT NULL DEFAULT 0,
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
        db.execSQL("DROP TABLE IF EXISTS container_items")
        db.execSQL("DROP TABLE IF EXISTS containers")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun getAllContainers(): List<ContainerEntity> {
        val result = mutableListOf<ContainerEntity>()
        val db = readableDatabase

        val cursor = db.query(
            "containers",
            null,
            null,
            null,
            null,
            null,
            "created_at ASC"
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val id = c.getString(c.getColumnIndexOrThrow("id"))
                val title = c.getString(c.getColumnIndexOrThrow("title"))
                val position = c.getString(c.getColumnIndexOrThrow("position"))
                val isBackgroundEnabled = c.getInt(c.getColumnIndexOrThrow("is_background_enabled")) == 1
                val isExpandCell = c.getInt(c.getColumnIndexOrThrow("is_expand_cell")) == 1
                val showAddFolderButton = c.getInt(c.getColumnIndexOrThrow("show_add_folder_button")) == 1
                val showFolderLabels = c.getInt(c.getColumnIndexOrThrow("show_folder_labels")) == 1
                val isGridFolderEnabled = c.getInt(c.getColumnIndexOrThrow("is_grid_folder_enabled")) == 1
                val createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))

                result.add(
                    ContainerEntity(
                        id = id,
                        title = title,
                        position = position,
                        isBackgroundEnabled = isBackgroundEnabled,
                        isExpandCell = isExpandCell,
                        showAddFolderButton = showAddFolderButton,
                        showFolderLabels = showFolderLabels,
                        isGridFolderEnabled = isGridFolderEnabled,
                        createdAt = createdAt
                    )
                )
            }
        }
        return result
    }

    fun getContainer(panelId: String): ContainerEntity? {
        val db = readableDatabase
        val cursor = db.query(
            "containers",
            null,
            "id = ?",
            arrayOf(panelId),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst()) {
                val id = c.getString(c.getColumnIndexOrThrow("id"))
                val title = c.getString(c.getColumnIndexOrThrow("title"))
                val position = c.getString(c.getColumnIndexOrThrow("position"))
                val isBackgroundEnabled = c.getInt(c.getColumnIndexOrThrow("is_background_enabled")) == 1
                val isExpandCell = c.getInt(c.getColumnIndexOrThrow("is_expand_cell")) == 1
                val showAddFolderButton = c.getInt(c.getColumnIndexOrThrow("show_add_folder_button")) == 1
                val showFolderLabels = c.getInt(c.getColumnIndexOrThrow("show_folder_labels")) == 1
                val isGridFolderEnabled = c.getInt(c.getColumnIndexOrThrow("is_grid_folder_enabled")) == 1
                val createdAt = c.getLong(c.getColumnIndexOrThrow("created_at"))

                return ContainerEntity(
                    id = id,
                    title = title,
                    position = position,
                    isBackgroundEnabled = isBackgroundEnabled,
                    isExpandCell = isExpandCell,
                    showAddFolderButton = showAddFolderButton,
                    showFolderLabels = showFolderLabels,
                    isGridFolderEnabled = isGridFolderEnabled,
                    createdAt = createdAt
                )
            }
        }
        return null
    }

    fun insertOrUpdateContainer(panel: ContainerEntity) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("id", panel.id)
            put("title", panel.title)
            put("position", panel.position)
            put("is_background_enabled", if (panel.isBackgroundEnabled) 1 else 0)
            put("is_expand_cell", if (panel.isExpandCell) 1 else 0)
            put("show_add_folder_button", if (panel.showAddFolderButton) 1 else 0)
            put("show_folder_labels", if (panel.showFolderLabels) 1 else 0)
            put("is_grid_folder_enabled", if (panel.isGridFolderEnabled) 1 else 0)
            put("created_at", panel.createdAt)
        }
        db.insertWithOnConflict("containers", null, values, SQLiteDatabase.CONFLICT_REPLACE)
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
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("container_items", "panel_id = ?", arrayOf(panelId))
            items.forEachIndexed { index, item ->
                val values = ContentValues().apply {
                    put("id", item.id)
                    put("panel_id", panelId)
                    put("item_type", item.itemType.name)
                    put("package_name", item.packageName)
                    put("folder_id", item.folderId)
                    put("order_index", index)
                }
                db.insert("container_items", null, values)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    companion object {
        private const val DATABASE_NAME = "aureole_launcher_containers.db"
        private const val DATABASE_VERSION = 1
    }
}
