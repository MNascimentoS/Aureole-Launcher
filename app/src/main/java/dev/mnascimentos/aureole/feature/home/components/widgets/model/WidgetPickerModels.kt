package dev.mnascimentos.aureole.feature.home.components.widgets.model

import android.appwidget.AppWidgetProviderInfo
import android.graphics.drawable.Drawable

data class WidgetAppGroup(
    val appId: String,
    val appName: String,
    val appIcon: Drawable? = null,
    val availableWidgets: List<WidgetVariant> = emptyList()
)

data class WidgetVariant(
    val widgetId: String,
    val title: String,
    val previewImage: Drawable? = null,
    val previewLayoutRes: Int = 0,
    val minSpanX: Int = 2,
    val minSpanY: Int = 2,
    val providerInfo: AppWidgetProviderInfo? = null
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
