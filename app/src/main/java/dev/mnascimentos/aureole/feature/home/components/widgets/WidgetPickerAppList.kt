package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetProviderInfo
import android.graphics.drawable.Drawable
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderButtonMetrics
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBarParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarSizes
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarState
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSectionHeader
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.scrubber.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetPickerMetrics
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import kotlinx.coroutines.launch

@Suppress("LongMethod") // Composable rendering list of app widgets and expand controls
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
    val topPadding = if (isSearchExpanded) WidgetPickerMetrics.SearchTopPaddingExpanded else AureoleDS.dimens.medium

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
                .padding(start = AureoleDS.dimens.large, end = AureoleDS.dimens.large, top = AureoleDS.dimens.medium)
        } else {
            Modifier
                .padding(end = AureoleDS.dimens.large, bottom = AureoleDS.dimens.large)
        }

        Box(modifier = Modifier.align(searchBarAlign)) {
            AureoleFloatingSearchBar(
                state = AureoleSearchBarState(
                    query = state.searchQuery,
                    onQueryChange = { onIntent(WidgetSelectorIntent.SearchQueryChanged(it)) },
                    isExpanded = isSearchExpanded,
                    onExpandedChange = { isSearchExpanded = it }
                ),
                modifier = searchBarModifier,
                params = AureoleFloatingSearchBarParams(
                    placeholder = stringResource(R.string.home_widget_picker_search_placeholder),
                    focusRequester = focusRequester,
                    sizes = AureoleSearchBarSizes(
                        collapsedSize = FolderButtonMetrics.MediumSize,
                        barHeight = FolderButtonMetrics.SmallSize
                    )
                )
            )
        }
    }
}

@Suppress("LongMethod") // Composable rendering list of app widgets and expand controls
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

@Suppress("LongMethod") // Composable rendering list of app widgets and expand controls
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

@Suppress("LongMethod") // Composable rendering list of app widgets and expand controls
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
        contentPadding = PaddingValues(bottom = WidgetPickerMetrics.ListBottomPadding, top = topPadding)
    ) {
        groupedApps.forEach { (letter, groups) ->
            item(key = "header_$letter") {
                AureoleSectionHeader(
                    title = letter.toString(),
                    modifier = Modifier.padding(
                        start = AureoleDS.dimens.large,
                        top = AureoleDS.dimens.medium,
                        bottom = AureoleDS.dimens.xSmall
                    )
                )
            }

            items(
                items = groups,
                key = { it.appId },
                contentType = { "widget_app_group" }
            ) { group ->
                WidgetAppGroupItem(
                    group = group,
                    isExpanded = expandedAppIds.contains(group.appId),
                    onToggleExpand = { onIntent(WidgetSelectorIntent.ToggleAppGroup(group.appId)) },
                    onWidgetSelected = onWidgetSelected
                )
            }
        }
    }
}

@Composable
private fun WidgetAppGroupItem(
    group: WidgetAppGroup,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    AppGroupHeader(
        group = group,
        onClick = onToggleExpand
    )

    if (isExpanded) {
        WidgetPreviewRow(
            widgets = group.availableWidgets,
            fallbackAppIcon = group.appIcon,
            onWidgetSelected = onWidgetSelected
        )
    }
}

@Composable
private fun WidgetPreviewRow(
    widgets: List<WidgetVariant>,
    fallbackAppIcon: Drawable?,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = AureoleDS.dimens.small),
        contentPadding = PaddingValues(start = AureoleDS.dimens.large, end = AureoleDS.dimens.minTouchTarget),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)
    ) {
        items(
            items = widgets,
            key = { it.widgetId },
            contentType = { "widget_preview_card" }
        ) { widget ->
            WidgetPreviewCard(
                widget = widget,
                fallbackAppIcon = fallbackAppIcon,
                onClick = { widget.providerInfo?.let(onWidgetSelected) }
            )
        }
    }
}

@AureolePreview
@Composable
private fun WidgetGroupListPreview() {
    val sampleState = WidgetSelectorState(
        widgetGroups = listOf(
            WidgetAppGroup(
                appId = "com.example.clock",
                appName = "Relógio",
                availableWidgets = listOf(
                    WidgetVariant(widgetId = "clock_1", title = "Relógio Analógico"),
                    WidgetVariant(widgetId = "clock_2", title = "Relógio Digital")
                )
            ),
            WidgetAppGroup(
                appId = "com.example.weather",
                appName = "Tempo",
                availableWidgets = listOf(
                    WidgetVariant(widgetId = "weather_1", title = "Previsão Diária")
                )
            )
        )
    )
    AureoleLauncherTheme {
        WidgetGroupList(
            state = sampleState,
            onIntent = {},
            onWidgetSelected = {},
            onDismiss = {}
        )
    }
}
