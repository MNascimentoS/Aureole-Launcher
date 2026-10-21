package dev.mnascimentos.aureole.core.data.repository

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.edit
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

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

    fun getWidgetIdsForStack(stackId: String): List<Int> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val savedIds = prefs.getString("${KEY_WIDGET_IDS_PREFIX}_$stackId", null)
        return when {
            savedIds != null -> savedIds.split(",").mapNotNull { it.toIntOrNull() }
            stackId == "top_widgets" -> getSavedWidgetIds()
            else -> emptyList()
        }
    }

    fun saveWidgetIdsForStack(stackId: String, ids: List<Int>) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
            putString("${KEY_WIDGET_IDS_PREFIX}_$stackId", ids.joinToString(","))
            if (stackId == "top_widgets") {
                putString(KEY_TOP_WIDGET_IDS, ids.joinToString(","))
            }
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

    suspend fun getAvailableWidgets(): List<WidgetAppGroup> = withContext(Dispatchers.IO) {
        val appWidgetManager = AppWidgetManager.getInstance(context) ?: return@withContext emptyList()
        val installed = appWidgetManager.installedProviders
        val pm = context.packageManager

        val grouped = installed.groupBy { it.provider.packageName }
        grouped.mapNotNull { (packageName, providers) ->
            try {
                val appInfo = pm.getApplicationInfo(packageName, 0)
                val appName = pm.getApplicationLabel(appInfo).toString()
                val appIcon = pm.getApplicationIcon(appInfo)

                val densityDpi = context.resources.displayMetrics.densityDpi
                val variants = providers.map { provider ->
                    val label = provider.loadLabel(pm)
                    val preview = provider.loadPreviewImage(context, densityDpi)
                        ?: provider.loadPreviewImage(context, 0)
                    val previewLayout =
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                            provider.previewLayout
                        } else {
                            0
                        }

                    val minWidthAdjusted = provider.minWidth + CELL_MARGIN_ESTIMATE_DP
                    val spanX = Math.max(
                        1,
                        Math.ceil(minWidthAdjusted / CELL_SIZE_ESTIMATE_DP).toInt()
                    )

                    val minHeightAdjusted = provider.minHeight + CELL_MARGIN_ESTIMATE_DP
                    val spanY = Math.max(
                        1,
                        Math.ceil(minHeightAdjusted / CELL_SIZE_ESTIMATE_DP).toInt()
                    )

                    WidgetVariant(
                        widgetId = provider.provider.flattenToString(),
                        title = label,
                        previewImage = preview,
                        previewLayoutRes = previewLayout,
                        minSpanX = spanX,
                        minSpanY = spanY,
                        providerInfo = provider
                    )
                }
                WidgetAppGroup(
                    appId = packageName,
                    appName = appName,
                    appIcon = appIcon,
                    availableWidgets = variants.sortedBy { it.title }
                )
            } catch (e: PackageManager.NameNotFoundException) {
                Timber.tag(TAG).e(e, "Failed to load app info for $packageName")
                null
            }
        }.sortedBy { it.appName }
    }

    companion object {
        private const val TAG = "WidgetRepository"
        private const val CELL_MARGIN_ESTIMATE_DP = 30
        private const val CELL_SIZE_ESTIMATE_DP = 70.0
        private const val PREFS_NAME = "aureole_prefs"
        private const val KEY_TOP_WIDGET_IDS = "top_widget_ids_list"
        private const val KEY_WIDGET_IDS_PREFIX = "widget_ids_stack"
        private const val KEY_WIDGET_ROW_HEIGHT = "widget_row_height"
        private const val KEY_WIDGET_DOTS_PREFIX = "widget_dots_stack"
        private const val DEFAULT_WIDGET_ROW_HEIGHT = 160f
    }
}
