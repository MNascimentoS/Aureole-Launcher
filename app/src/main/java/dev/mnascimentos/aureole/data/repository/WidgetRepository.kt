package dev.mnascimentos.aureole.data.repository

import android.content.Context
import androidx.core.content.edit

class WidgetRepository(private val context: Context) {

    fun getSavedWidgetIds(): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedIds = prefs.getString(KEY_TOP_WIDGET_IDS, "") ?: ""
        return savedIds.split(",").mapNotNull { it.toIntOrNull() }
    }

    fun saveWidgetIds(ids: List<Int>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString(KEY_TOP_WIDGET_IDS, ids.joinToString(","))
        }
    }

    fun getSavedWidgetRowHeight(): Float {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(KEY_WIDGET_ROW_HEIGHT, DEFAULT_WIDGET_ROW_HEIGHT)
    }

    fun saveWidgetRowHeight(heightDp: Float) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putFloat(KEY_WIDGET_ROW_HEIGHT, heightDp)
        }
    }

    companion object {
        private const val PREFS_NAME = "aureole_prefs"
        private const val KEY_TOP_WIDGET_IDS = "top_widget_ids_list"
        private const val KEY_WIDGET_ROW_HEIGHT = "widget_row_height"
        private const val DEFAULT_WIDGET_ROW_HEIGHT = 160f
    }
}
