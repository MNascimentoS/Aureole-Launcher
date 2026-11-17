package dev.mnascimentos.aureole.core.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private const val VERSION_WITHOUT_QUICK_BUTTONS = 2
private const val VERSION_WITH_QUICK_BUTTONS = 3
private const val VERSION_WITH_FOLDER_STYLE = 4

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
