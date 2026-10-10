package dev.mnascimentos.aureole.core.data.repository

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

    fun getWidgetDotsState(stackId: String): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean("${KEY_WIDGET_DOTS_PREFIX}_$stackId", true)
    }

    fun setWidgetDotsState(stackId: String, showDots: Boolean) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putBoolean("${KEY_WIDGET_DOTS_PREFIX}_$stackId", showDots)
        }
    }

    companion object {
        private const val PREFS_NAME = "aureole_prefs"
        private const val KEY_TOP_WIDGET_IDS = "top_widget_ids_list"
        private const val KEY_WIDGET_ROW_HEIGHT = "widget_row_height"
        private const val KEY_WIDGET_DOTS_PREFIX = "widget_dots_stack"
        private const val DEFAULT_WIDGET_ROW_HEIGHT = 160f
    }
}
