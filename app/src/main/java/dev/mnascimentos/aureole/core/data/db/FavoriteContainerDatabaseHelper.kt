package dev.mnascimentos.aureole.core.data.db

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class FavoriteContainerDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS favorite_containers (
                id TEXT PRIMARY KEY NOT NULL,
                created_at INTEGER NOT NULL
            )
            """.trimIndent()
        )

        db.execSQL(
            """
            CREATE TABLE IF NOT EXISTS container_favorite_items (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                container_id TEXT NOT NULL,
                package_name TEXT NOT NULL,
                item_order INTEGER NOT NULL DEFAULT 0,
                FOREIGN KEY(container_id) REFERENCES favorite_containers(id) ON DELETE CASCADE
            )
            """.trimIndent()
        )

        db.execSQL(
            "CREATE INDEX IF NOT EXISTS idx_favorite_items_container_id ON container_favorite_items(container_id)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS container_favorite_items")
        db.execSQL("DROP TABLE IF EXISTS favorite_containers")
        onCreate(db)
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true)
    }

    fun getFavoriteItemsForContainer(containerId: String): List<String> {
        val db = readableDatabase
        val items = mutableListOf<String>()
        val cursor = db.query(
            "container_favorite_items",
            arrayOf("package_name"),
            "container_id = ?",
            arrayOf(containerId),
            null,
            null,
            "item_order ASC"
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val pkgName = c.getString(c.getColumnIndexOrThrow("package_name"))
                items.add(pkgName)
            }
        }
        return items
    }

    fun getAllContainersWithItems(): Map<String, List<String>> {
        val db = readableDatabase
        val result = mutableMapOf<String, List<String>>()

        val cursor = db.query(
            "favorite_containers",
            arrayOf("id"),
            null,
            null,
            null,
            null,
            "created_at ASC"
        )

        cursor.use { c ->
            while (c.moveToNext()) {
                val containerId = c.getString(c.getColumnIndexOrThrow("id"))
                val items = getFavoriteItemsForContainer(containerId)
                result[containerId] = items
            }
        }
        return result
    }

    fun updateContainerItems(containerId: String, packageNames: List<String>) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val containerValues = ContentValues().apply {
                put("id", containerId)
                put("created_at", System.currentTimeMillis())
            }
            db.insertWithOnConflict("favorite_containers", null, containerValues, SQLiteDatabase.CONFLICT_IGNORE)

            db.delete("container_favorite_items", "container_id = ?", arrayOf(containerId))

            packageNames.forEachIndexed { index, pkg ->
                val itemValues = ContentValues().apply {
                    put("container_id", containerId)
                    put("package_name", pkg)
                    put("item_order", index)
                }
                db.insert("container_favorite_items", null, itemValues)
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun deleteContainer(containerId: String) {
        val db = writableDatabase
        db.delete("favorite_containers", "id = ?", arrayOf(containerId))
    }

    companion object {
        private const val DATABASE_NAME = "aureole_launcher_favorite_containers.db"
        private const val DATABASE_VERSION = 1
    }
}
