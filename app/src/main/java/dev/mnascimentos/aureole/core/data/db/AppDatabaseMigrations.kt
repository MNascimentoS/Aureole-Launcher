package dev.mnascimentos.aureole.core.data.db

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private const val VERSION_WITHOUT_QUICK_BUTTONS = 2
private const val VERSION_WITH_QUICK_BUTTONS = 3

/** Version 3 lets containers hold quick buttons. */
val MIGRATION_2_3 = object : Migration(VERSION_WITHOUT_QUICK_BUTTONS, VERSION_WITH_QUICK_BUTTONS) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE container_items ADD COLUMN quick_button_id TEXT")
        db.execSQL("ALTER TABLE container_items ADD COLUMN quick_button_json TEXT")
    }
}
