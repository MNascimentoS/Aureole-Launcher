package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
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
                        
                        val variants = providers.map { provider ->
                            val label = provider.loadLabel(pm)
                            val preview = provider.loadPreviewImage(context, 0) ?: provider.loadIcon(context, 0)
                            
                            // Rough estimate of spans. Widget sizing can be complex,
                            // but for preview display we estimate.
                            val minWidthAdjusted = provider.minWidth + CELL_MARGIN_ESTIMATE_DP
                            val spanX = Math.max(1, Math.ceil(minWidthAdjusted / CELL_SIZE_ESTIMATE_DP).toInt())
                            
                            val minHeightAdjusted = provider.minHeight + CELL_MARGIN_ESTIMATE_DP
                            val spanY = Math.max(1, Math.ceil(minHeightAdjusted / CELL_SIZE_ESTIMATE_DP).toInt())
                            
                            WidgetVariant(
                                widgetId = provider.provider.flattenToString(),
                                title = label,
                                previewImage = preview,
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
fun WidgetPickerBottomSheet(
    appWidgetManager: AppWidgetManager,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val viewModel: WidgetPickerViewModel = remember { WidgetPickerViewModel() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(WidgetSelectorIntent.LoadWidgets, context, appWidgetManager)
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        WidgetSelectorScreen(
            state = state,
            onIntent = { viewModel.processIntent(it) },
            onWidgetSelected = onWidgetSelected
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSelectorScreen(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        WidgetSearchBar(state = state, onIntent = onIntent)

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.widgetGroups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Nenhum widget encontrado.")
            }
        } else {
            WidgetGroupList(state = state, onIntent = onIntent, onWidgetSelected = onWidgetSelected)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WidgetSearchBar(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit
) {
    OutlinedTextField(
        value = state.searchQuery,
        onValueChange = { onIntent(WidgetSelectorIntent.SearchQueryChanged(it)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        placeholder = { Text("Pesquisar widgets...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        shape = RoundedCornerShape(24.dp),
        singleLine = true,
        colors = TextFieldDefaults.colors(
            unfocusedIndicatorColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    )
}

@Composable
private fun WidgetGroupList(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(
            items = state.widgetGroups,
            key = { it.appId }
        ) { group ->
            val isExpanded = state.expandedAppIds.contains(group.appId)

            AppGroupHeader(
                group = group,
                isExpanded = isExpanded,
                onClick = { onIntent(WidgetSelectorIntent.ToggleAppGroup(group.appId)) }
            )

            if (isExpanded) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(group.availableWidgets, key = { it.widgetId }) { widget ->
                        WidgetPreviewCard(
                            widget = widget,
                            onClick = { onWidgetSelected(widget.providerInfo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppGroupHeader(
    group: WidgetAppGroup,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (group.appIcon != null) {
            AsyncImage(
                model = group.appIcon,
                contentDescription = group.appName,
                modifier = Modifier.size(32.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = group.appName,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        val countText = group.availableWidgets.size.toString()
        Text(
            text = countText,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(end = 8.dp)
        )

        Icon(
            imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun WidgetPreviewCard(
    widget: WidgetVariant,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            if (widget.previewImage != null) {
                AsyncImage(
                    model = widget.previewImage,
                    contentDescription = widget.title,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.padding(8.dp).fillMaxSize()
                )
            } else {
                Text(
                    text = "Sem Preview",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Grid Span indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.8f),
                        RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 4.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "${widget.minSpanX}x${widget.minSpanY}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = widget.title,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}
