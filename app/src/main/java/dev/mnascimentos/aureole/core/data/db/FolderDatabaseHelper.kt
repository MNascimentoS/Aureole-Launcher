package dev.mnascimentos.aureole.core.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderEntity
import dev.mnascimentos.aureole.core.data.model.FolderItemEntity
import dev.mnascimentos.aureole.core.data.model.FolderWithItems

class FolderDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS folders (
                id TEXT PRIMARY KEY NOT NULL,
                panel_id TEXT,
                name TEXT NOT NULL,
                color TEXT,
                icon_fallback TEXT,
                icon TEXT,
                display_as_grid INTEGER NOT NULL DEFAULT 0,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS folder_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                folder_id TEXT NOT NULL,
                package_name TEXT NOT NULL,
                item_order INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(folder_id) REFERENCES folders(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL("CREATE INDEX IF NOT EXISTS idx_folder_items_folder_id ON folder_items(folder_id)")
        db.execSQL("CREATE INDEX IF NOT EXISTS idx_folders_panel_id ON folders(panel_id)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < VERSION_2) {
            db.execSQL("ALTER TABLE folders ADD COLUMN icon TEXT")
        }
        if (oldVersion < VERSION_3) {
            try {
                db.execSQL("ALTER TABLE folders ADD COLUMN panel_id TEXT")
            } catch (e: SQLiteException) {
                Log.w("FolderDatabaseHelper", "Column panel_id may already exist", e)
            }
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun getAllFoldersWithItems(): List<FolderWithItems> {
        val result = mutableListOf<FolderWithItems>()
        val db = readableDatabase

        val cursor = db.query(
            "folders",
            null,
            null,
            null,
            null,
            null,
            "created_at ASC"
        )

        cursor.use { folderCursor ->
            while (folderCursor.moveToNext()) {
                val id = folderCursor.getString(folderCursor.getColumnIndexOrThrow("id"))
                val panelIdIndex = folderCursor.getColumnIndex("panel_id")
                val panelId = if (panelIdIndex != -1 && !folderCursor.isNull(panelIdIndex)) {
                    folderCursor.getString(panelIdIndex)
                } else {
                    null
                }
                val name = folderCursor.getString(folderCursor.getColumnIndexOrThrow("name"))
                val color = folderCursor.getString(folderCursor.getColumnIndexOrThrow("color"))
                val iconFallback = folderCursor.getString(folderCursor.getColumnIndexOrThrow("icon_fallback"))
                val iconIndex = folderCursor.getColumnIndex("icon")
                val icon = if (iconIndex != -1 && !folderCursor.isNull(iconIndex)) {
                    folderCursor.getString(iconIndex)
                } else {
                    null
                }
                val displayAsGrid = folderCursor.getInt(folderCursor.getColumnIndexOrThrow("display_as_grid")) == 1
                val createdAt = folderCursor.getLong(folderCursor.getColumnIndexOrThrow("created_at"))

                val folderEntity = FolderEntity(
                    id = id,
                    panelId = panelId,
                    name = name,
                    color = color,
                    iconFallback = iconFallback,
                    icon = icon,
                    displayAsGrid = displayAsGrid,
                    createdAt = createdAt
                )

                val items = getFolderItems(db, id)
                result.add(FolderWithItems(folderEntity, items))
            }
        }

        return result
    }

    private fun getFolderItems(db: SQLiteDatabase, folderId: String): List<FolderItemEntity> {
        val items = mutableListOf<FolderItemEntity>()
        val itemCursor = db.query(
            "folder_items",
            null,
            "folder_id = ?",
            arrayOf(folderId),
            null,
            null,
            "item_order ASC"
        )

        itemCursor.use { cursor ->
            while (cursor.moveToNext()) {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val pkgName = cursor.getString(cursor.getColumnIndexOrThrow("package_name"))
                val itemOrder = cursor.getInt(cursor.getColumnIndexOrThrow("item_order"))

                items.add(
                    FolderItemEntity(
                        id = id,
                        folderId = folderId,
                        packageName = pkgName,
                        itemOrder = itemOrder
                    )
                )
            }
        }

        return items
    }

    fun insertFolder(folder: AppFolder) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val folderValues = ContentValues().apply {
                put("id", folder.id)
                put("panel_id", folder.panelId)
                put("name", folder.name)
                put("icon_fallback", folder.iconPackage)
                put("icon", folder.icon)
                put("display_as_grid", if (folder.displayAsGrid) 1 else 0)
                put("created_at", System.currentTimeMillis())
            }
            db.insertWithOnConflict("folders", null, folderValues, SQLiteDatabase.CONFLICT_REPLACE)

            folder.appPackageNames.forEachIndexed { index, pkg ->
                val itemValues = ContentValues().apply {
                    put("folder_id", folder.id)
                    put("package_name", pkg)
                    put("item_order", index)
                }
                db.insert("folder_items", null, itemValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun updateFolderDetails(folderId: String, newName: String, icon: String? = null) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("name", newName)
            put("icon", icon)
        }
        db.update("folders", values, "id = ?", arrayOf(folderId))
    }

    fun updateFolderItems(folderId: String, packageNames: List<String>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            db.delete("folder_items", "folder_id = ?", arrayOf(folderId))
            packageNames.forEachIndexed { index, pkg ->
                val itemValues = ContentValues().apply {
                    put("folder_id", folderId)
                    put("package_name", pkg)
                    put("item_order", index)
                }
                db.insert("folder_items", null, itemValues)
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteFolder(folderId: String) {
        val db = writableDatabase
        db.delete("folders", "id = ?", arrayOf(folderId))
    }

    companion object {
        private const val DATABASE_NAME = "aureole_launcher_folders.db"
        private const val DATABASE_VERSION = 3
        private const val VERSION_2 = 2
        private const val VERSION_3 = 3
    }
}
