package dev.mnascimentos.aureole.feature.home.widget.model

import android.appwidget.AppWidgetProviderInfo
import android.graphics.drawable.Drawable

data class WidgetAppGroup(
    val appId: String,
    val appName: String,
    val appIcon: Drawable?,
    val availableWidgets: List<WidgetVariant>
)

data class WidgetVariant(
    val widgetId: String,
    val title: String,
    val previewImage: Drawable?,
    val previewLayoutRes: Int = 0,
    val minSpanX: Int,
    val minSpanY: Int,
    val providerInfo: AppWidgetProviderInfo
)

data class WidgetSelectorState(
    val isLoading: Boolean = false,
    val searchQuery: String = "",
    val widgetGroups: List<WidgetAppGroup> = emptyList(),
    val expandedAppIds: Set<String> = emptySet(),
    val error: String? = null
)

sealed class WidgetSelectorIntent {
    object LoadWidgets : WidgetSelectorIntent()
    data class SearchQueryChanged(val query: String) : WidgetSelectorIntent()
    data class ToggleAppGroup(val appId: String) : WidgetSelectorIntent()
}
