package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetProviderInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarSizes
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSectionHeader
import dev.mnascimentos.aureole.feature.home.components.scrubber.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import kotlinx.coroutines.launch

// Note: break down long method
@Suppress("LongMethod")
@Composable
fun WidgetGroupList(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var lastScrolledIndex by remember { mutableIntStateOf(-1) }
    var isSearchExpanded by remember { mutableStateOf(state.searchQuery.isNotEmpty()) }
    val focusRequester = remember { FocusRequester() }

    BackHandler(enabled = true) {
        if (state.searchQuery.isNotEmpty()) {
            onIntent(WidgetSelectorIntent.SearchQueryChanged(""))
        } else if (isSearchExpanded) {
            isSearchExpanded = false
        } else {
            onDismiss()
        }
    }

    LaunchedEffect(state.searchQuery) {
        if (state.searchQuery.isNotEmpty() && !isSearchExpanded) {
            isSearchExpanded = true
        }
    }

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            focusRequester.requestFocus()
        }
    }

    val groupedData = rememberGroupedAppsData(state.widgetGroups)
    val topPadding = if (isSearchExpanded) 80.dp else 16.dp

    Box(modifier = modifier.fillMaxSize()) {
        WidgetGroupListColumn(
            params = WidgetGroupListColumnParams(
                groupedApps = groupedData.groupedApps,
                expandedAppIds = state.expandedAppIds,
                listState = listState,
                topPadding = topPadding
            ),
            onIntent = onIntent,
            onWidgetSelected = onWidgetSelected
        )

        if (!isSearchExpanded && state.searchQuery.isEmpty()) {
            WidgetScrubberOverlay(
                groupedData = groupedData,
                listState = listState,
                lastScrolledIndex = lastScrolledIndex,
                onLastScrolledIndexChange = { lastScrolledIndex = it },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        val searchBarAlign = if (isSearchExpanded) Alignment.TopCenter else Alignment.BottomEnd
        val searchBarModifier = if (isSearchExpanded) {
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
        } else {
            Modifier
                .padding(end = 24.dp, bottom = 24.dp)
        }

        Box(modifier = Modifier.align(searchBarAlign)) {
            AureoleFloatingSearchBar(
                query = state.searchQuery,
                onQueryChange = { onIntent(WidgetSelectorIntent.SearchQueryChanged(it)) },
                isExpanded = isSearchExpanded,
                onExpandedChange = { isSearchExpanded = it },
                modifier = searchBarModifier,
                placeholder = "Pesquisar widgets...",
                focusRequester = focusRequester,
                sizes = AureoleSearchBarSizes(
                    collapsedSize = 60.dp,
                    barHeight = 52.dp
                )
            )
        }
    }
}

@Composable
private fun WidgetScrubberOverlay(
    groupedData: GroupedAppsData,
    listState: LazyListState,
    lastScrolledIndex: Int,
    onLastScrolledIndexChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    CurvedAlphabetScrubber(
        alphabet = groupedData.alphabet,
        options = ScrubberOptions(
            isAlwaysVisible = true,
            isGestureEnabled = true,
            externalTouchY = -1f
        ),
        callbacks = ScrubberCallbacks(
            onLetterSelected = { letter ->
                groupedData.letterIndexMap[letter]?.let { targetIndex ->
                    if (targetIndex != lastScrolledIndex) {
                        onLastScrolledIndexChange(targetIndex)
                        coroutineScope.launch {
                            listState.scrollToItem(targetIndex)
                        }
                    }
                }
            }
        ),
        modifier = modifier
    )
}

private data class GroupedAppsData(
    val groupedApps: Map<Char, List<WidgetAppGroup>>,
    val alphabet: List<Char>,
    val letterIndexMap: Map<Char, Int>
)

@Composable
private fun rememberGroupedAppsData(widgetGroups: List<WidgetAppGroup>): GroupedAppsData {
    return remember(widgetGroups) {
        val grouped = widgetGroups
            .groupBy { it.appName.firstOrNull()?.uppercaseChar() ?: '#' }
            .toSortedMap()
        val alphabet = grouped.keys.toList()
        val map = mutableMapOf<Char, Int>()
        var currentIndex = 0
        grouped.forEach { (letter, apps) ->
            map[letter] = currentIndex
            currentIndex += apps.size + 1
        }
        GroupedAppsData(grouped, alphabet, map)
    }
}

private data class WidgetGroupListColumnParams(
    val groupedApps: Map<Char, List<WidgetAppGroup>>,
    val expandedAppIds: Set<String>,
    val listState: LazyListState,
    val topPadding: Dp
)

@Composable
private fun WidgetGroupListColumn(
    params: WidgetGroupListColumnParams,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    val groupedApps = params.groupedApps
    val expandedAppIds = params.expandedAppIds
    val listState = params.listState
    val topPadding = params.topPadding
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 120.dp, top = topPadding)
    ) {
        groupedApps.forEach { (letter, groups) ->
            item(key = "header_$letter") {
                AureoleSectionHeader(
                    title = letter.toString(),
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            items(
                items = groups,
                key = { it.appId },
                contentType = { "widget_app_group" }
            ) { group ->
                val isExpanded = expandedAppIds.contains(group.appId)

                AppGroupHeader(
                    group = group,
                    onClick = { onIntent(WidgetSelectorIntent.ToggleAppGroup(group.appId)) }
                )

                if (isExpanded) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        contentPadding = PaddingValues(start = 24.dp, end = 48.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(
                            items = group.availableWidgets,
                            key = { it.widgetId },
                            contentType = { "widget_preview_card" }
                        ) { widget ->
                            WidgetPreviewCard(
                                widget = widget,
                                fallbackAppIcon = group.appIcon,
                                onClick = { widget.providerInfo?.let { onWidgetSelected(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}
