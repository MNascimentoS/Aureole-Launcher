package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils

private const val TAG = "GridRepository"

class GridRepository(private val context: Context) {

    private val gson = Gson()
    private val prefs
        get() = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun getGridItems(): List<LauncherItemState> {
        val json = prefs.getString(KEY_GRID_ITEMS, null)
        if (json == null) {
            return GridEngineUtils.getDefaultGridItems()
        }
        return try {
            val type = object : TypeToken<List<LauncherItemState>>() {}.type
            val items: List<LauncherItemState>? = gson.fromJson(json, type)
            if (items == null) {
                emptyList()
            } else {
                items.map { item ->
                    if (item.type == null) {
                        item.copy(type = LauncherItemType.APPS_LIST)
                    } else {
                        item
                    }
                }
            }
        } catch (e: JsonSyntaxException) {
            Log.w(TAG, "Failed to parse grid items JSON", e)
            emptyList()
        } catch (e: IllegalStateException) {
            Log.w(TAG, "Illegal state while parsing grid items JSON", e)
            emptyList()
        }
    }

    fun saveGridItems(items: List<LauncherItemState>) {
        val json = gson.toJson(items)
        prefs.edit { putString(KEY_GRID_ITEMS, json) }
    }

    fun resetToDefault() {
        prefs.edit { remove(KEY_GRID_ITEMS) }
    }

    companion object {
        private const val PREFS_NAME = "aureole_grid_prefs"
        private const val KEY_GRID_ITEMS = "grid_items_json"
    }
}
