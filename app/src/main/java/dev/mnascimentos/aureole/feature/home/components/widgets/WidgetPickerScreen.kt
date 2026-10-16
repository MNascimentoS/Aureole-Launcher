package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "WidgetPickerBottomSheet"
private const val CELL_MARGIN_ESTIMATE_DP = 30
private const val CELL_SIZE_ESTIMATE_DP = 70.0

class WidgetPickerViewModel : ViewModel() {
    private val _state = MutableStateFlow(WidgetSelectorState(isLoading = true))
    val state: StateFlow<WidgetSelectorState> = _state.asStateFlow()

    private var allGroups: List<WidgetAppGroup> = emptyList()

    fun processIntent(
        intent: WidgetSelectorIntent,
        context: Context? = null,
        appWidgetManager: AppWidgetManager? = null
    ) {
        when (intent) {
            is WidgetSelectorIntent.LoadWidgets -> {
                if (context != null && appWidgetManager != null) {
                    loadWidgets(context, appWidgetManager)
                }
            }

            is WidgetSelectorIntent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = intent.query) }
                filterWidgets(intent.query)
            }

            is WidgetSelectorIntent.ToggleAppGroup -> {
                _state.update { currentState ->
                    val expanded = currentState.expandedAppIds.toMutableSet()
                    if (expanded.contains(intent.appId)) {
                        expanded.remove(intent.appId)
                    } else {
                        expanded.add(intent.appId)
                    }
                    currentState.copy(expandedAppIds = expanded)
                }
            }
        }
    }

    private fun loadWidgets(context: Context, appWidgetManager: AppWidgetManager) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val groups = withContext(Dispatchers.IO) {
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
                        Log.e(TAG, "Failed to load app info for $packageName", e)
                        null
                    }
                }.sortedBy { it.appName }
            }
            allGroups = groups
            _state.update { it.copy(isLoading = false, widgetGroups = groups) }
        }
    }

    private fun filterWidgets(query: String) {
        if (query.isBlank()) {
            _state.update { it.copy(widgetGroups = allGroups) }
        } else {
            val lowerQuery = query.lowercase()
            val filtered = allGroups.mapNotNull { group ->
                if (group.appName.lowercase().contains(lowerQuery)) {
                    group
                } else {
                    val matchingWidgets = group.availableWidgets.filter {
                        it.title.lowercase().contains(lowerQuery)
                    }
                    if (matchingWidgets.isNotEmpty()) {
                        group.copy(availableWidgets = matchingWidgets)
                    } else {
                        null
                    }
                }
            }
            _state.update { it.copy(widgetGroups = filtered) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerScreen(
    appWidgetManager: AppWidgetManager,
    isBlurEnabled: Boolean = true,
    hazeState: HazeState? = null,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: WidgetPickerViewModel = remember { WidgetPickerViewModel() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(WidgetSelectorIntent.LoadWidgets, context, appWidgetManager)
    }

    WidgetSelectorScreen(
        state = state,
        hazeState = if (isBlurEnabled) hazeState else null,
        onIntent = { viewModel.processIntent(it) },
        onWidgetSelected = onWidgetSelected,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSelectorScreen(
    state: WidgetSelectorState,
    hazeState: HazeState? = null,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 32.dp,
                tint = HazeTint(Color.Black.copy(alpha = 0.45f))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val backgroundColor = if (hazeState != null) {
        Color.Transparent
    } else {
        AureoleTheme.colors.background
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(hazeModifier)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .clickable(enabled = false) {}
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.widgetGroups.isEmpty() && state.searchQuery.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum widget encontrado.", color = Color.White)
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    WidgetGroupList(
                        state = state,
                        onIntent = onIntent,
                        onWidgetSelected = onWidgetSelected,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@AureolePreview
@Composable
fun WidgetSelectorScreenPreview() {
    val sampleWidgetGroup = WidgetAppGroup(
        appId = "com.example.calendar",
        appName = "Calendar",
        availableWidgets = listOf(
            WidgetVariant(
                widgetId = "calendar_1",
                title = "Month View",
                minSpanX = 2,
                minSpanY = 2
            ),
            WidgetVariant(
                widgetId = "calendar_2",
                title = "Agenda View",
                minSpanX = 4,
                minSpanY = 2
            )
        )
    )
    val sampleState = WidgetSelectorState(
        isLoading = false,
        searchQuery = "",
        widgetGroups = listOf(sampleWidgetGroup),
        expandedAppIds = setOf("com.example.calendar")
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            WidgetSelectorScreen(
                state = sampleState,
                onIntent = {},
                onWidgetSelected = {},
                onDismiss = {}
            )
        }
    }
}
