package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import timber.log.Timber
import java.util.UUID

private const val TAG = "GridRepository"

class GridRepository(private val context: Context) {

    private val gson = Gson()
    private val prefs
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    @Suppress("TooGenericExceptionCaught") // Catching generic exceptions for SharedPreferences parsing
    fun getGridItems(isLandscape: Boolean = false, isLeftHanded: Boolean = false): List<LauncherItemState> {
        val key = if (isLandscape) KEY_LANDSCAPE_GRID_ITEMS else KEY_PORTRAIT_GRID_ITEMS
        var json = prefs.getString(key, null)
        if (json == null && !isLandscape) {
            json = prefs.getString(KEY_GRID_ITEMS, null)
        }
        if (json.isNullOrBlank()) {
            return GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded)
        }
        return try {
            val type = object : TypeToken<List<LauncherItemState>>() {}.type
            val items: List<LauncherItemState>? = gson.fromJson(json, type)
            // A saved empty list is a home the user (or the first run) left empty on purpose.
            when {
                items == null -> GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded)
                items.isEmpty() -> emptyList()
                else -> items.filterNotNull().map { item -> sanitizeItem(item) }
                    .ifEmpty { GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded) }
            }
        } catch (e: Exception) {
            Timber.tag(TAG).w(e, "Failed to parse or sanitize grid items JSON")
            GridDefaults.getDefaultGridItems(isLandscape, isLeftHanded)
        }
    }

    private fun sanitizeItem(item: LauncherItemState): LauncherItemState {
        val sanitizedChildren = item.safeChildren.map { child -> sanitizeItem(child) }
        val rawId = item.id as String?
        val finalId = if (rawId.isNullOrBlank()) "item_${UUID.randomUUID()}" else rawId
        return item.copy(
            id = finalId,
            type = item.safeType,
            scrollOrientation = item.safeScrollOrientation,
            children = sanitizedChildren
        )
    }

    fun saveGridItems(items: List<LauncherItemState>, isLandscape: Boolean = false) {
        val key = if (isLandscape) KEY_LANDSCAPE_GRID_ITEMS else KEY_PORTRAIT_GRID_ITEMS
        val json = gson.toJson(items)
        prefs.edit { putString(key, json) }
    }

    fun hasSavedLayout(): Boolean =
        listOf(KEY_GRID_ITEMS, KEY_PORTRAIT_GRID_ITEMS, KEY_LANDSCAPE_GRID_ITEMS).any { prefs.contains(it) }

    fun resetToDefault() {
        prefs.edit {
            remove(KEY_GRID_ITEMS)
            remove(KEY_PORTRAIT_GRID_ITEMS)
            remove(KEY_LANDSCAPE_GRID_ITEMS)
        }
    }

    companion object {
        private const val PREFS_NAME = "aureole_grid_prefs"
        private const val KEY_GRID_ITEMS = "grid_items_json"
        private const val KEY_PORTRAIT_GRID_ITEMS = "grid_items_portrait_json"
        private const val KEY_LANDSCAPE_GRID_ITEMS = "grid_items_landscape_json"
    }
}
