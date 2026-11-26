package dev.mnascimentos.aureole.core.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.mnascimentos.aureole.core.data.model.migratedContainerColors

private const val VERSION_WITHOUT_QUICK_BUTTONS = 2
private const val VERSION_WITH_QUICK_BUTTONS = 3
private const val VERSION_WITH_FOLDER_STYLE = 4
private const val VERSION_WITH_GROUP_COLORS = 5

/** Version 3 lets containers hold quick buttons. */
val MIGRATION_2_3 = object : Migration(VERSION_WITHOUT_QUICK_BUTTONS, VERSION_WITH_QUICK_BUTTONS) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE container_items ADD COLUMN quick_button_id TEXT")
        db.execSQL("ALTER TABLE container_items ADD COLUMN quick_button_json TEXT")
    }
}

/** Version 4 lets a folder show an icon, its letter or an emoji (`icon_type`, `emoji`); `color` already exists. */
val MIGRATION_3_4 = object : Migration(VERSION_WITH_QUICK_BUTTONS, VERSION_WITH_FOLDER_STYLE) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE folders ADD COLUMN icon_type TEXT NOT NULL DEFAULT 'ICON'")
        db.execSQL("ALTER TABLE folders ADD COLUMN emoji TEXT")
    }
}

/**
 * Version 5 gives each app & folder group its two colors and its item spacing (grupo-apps-pastas RF-E3, RF-D3):
 * the old background switch becomes the background color ([migratedContainerColors]), so every group keeps its look.
 */
val MIGRATION_4_5 = object : Migration(VERSION_WITH_FOLDER_STYLE, VERSION_WITH_GROUP_COLORS) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE containers ADD COLUMN background_color_ref TEXT")
        db.execSQL("ALTER TABLE containers ADD COLUMN item_color_ref TEXT")
        db.execSQL("ALTER TABLE containers ADD COLUMN item_spacing TEXT NOT NULL DEFAULT 'NORMAL'")
        val rows = db.query("SELECT id, is_background_enabled, background_color FROM containers").use { cursor ->
            buildList {
                while (cursor.moveToNext()) {
                    val legacyColor = if (cursor.isNull(2)) null else cursor.getLong(2)
                    add(cursor.getString(0) to migratedContainerColors(cursor.getInt(1) != 0, legacyColor))
                }
            }
        }
        rows.forEach { (id, colors) ->
            db.execSQL(
                "UPDATE containers SET background_color_ref = ?, item_color_ref = ? WHERE id = ?",
                arrayOf(colors.background?.toJson(), colors.item?.toJson(), id)
            )
        }
    }
}
