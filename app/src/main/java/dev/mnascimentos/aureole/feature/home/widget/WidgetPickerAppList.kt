package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetProviderInfo
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetSelectorState
import kotlinx.coroutines.launch

@Composable
fun WidgetGroupList(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
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

    Box(modifier = Modifier.fillMaxSize()) {
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
        Box(modifier = Modifier.align(searchBarAlign)) {
            WidgetGroupListSearchBar(
                isSearchExpanded = isSearchExpanded,
                searchQuery = state.searchQuery,
                focusRequester = focusRequester,
                onIntent = onIntent,
                onSearchExpandedChange = { isSearchExpanded = it }
            )
        }
    }
}

@Composable
private fun WidgetGroupListSearchBar(
    isSearchExpanded: Boolean,
    searchQuery: String,
    focusRequester: FocusRequester,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    AnimatedContent(
        targetState = isSearchExpanded,
        transitionSpec = {
            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
        },
        label = "search_widget_anim",
        modifier = if (isSearchExpanded) {
            Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, top = 16.dp)
        } else {
            Modifier
                .padding(end = 24.dp, bottom = 24.dp)
        }
    ) { expanded ->
        if (expanded) {
            WidgetExpandedSearchRow(
                searchQuery = searchQuery,
                focusRequester = focusRequester,
                onIntent = onIntent,
                onSearchExpandedChange = onSearchExpandedChange
            )
        } else {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(AureoleTheme.colors.surfaceVariant)
                    .clickable { onSearchExpandedChange(true) },
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Search(
                    tint = AureoleTheme.colors.onSurfaceHigh,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}

@Composable
private fun WidgetExpandedSearchRow(
    searchQuery: String,
    focusRequester: FocusRequester,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onSearchExpandedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .clip(RoundedCornerShape(26.dp))
            .background(AureoleTheme.colors.surfaceVariant)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AureoleDS.icons.Search(
            tint = AureoleTheme.colors.onSurfaceMedium,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        BasicTextField(
            value = searchQuery,
            onValueChange = { onIntent(WidgetSelectorIntent.SearchQueryChanged(it)) },
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester),
            singleLine = true,
            textStyle = AureoleTheme.typography.bodyLarge.copy(color = Color.White),
            decorationBox = { innerTextField ->
                if (searchQuery.isEmpty()) {
                    Text(
                        text = "Pesquisar widgets...",
                        style = AureoleTheme.typography.bodyLarge,
                        color = AureoleTheme.colors.onSurfaceMedium
                    )
                }
                innerTextField()
            }
        )
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable {
                    if (searchQuery.isNotEmpty()) {
                        onIntent(WidgetSelectorIntent.SearchQueryChanged(""))
                    } else {
                        onSearchExpandedChange(false)
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "✕",
                color = AureoleTheme.colors.onSurfaceMedium,
                fontSize = 14.sp
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
                Text(
                    text = letter.toString(),
                    style = AureoleTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = AureoleTheme.colors.onSurfaceMedium,
                    modifier = Modifier.padding(start = 24.dp, top = 16.dp, bottom = 8.dp)
                )
            }

            items(
                items = groups,
                key = { it.appId }
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
                        items(group.availableWidgets, key = { it.widgetId }) { widget ->
                            WidgetPreviewCard(
                                widget = widget,
                                onClick = { widget.providerInfo?.let { onWidgetSelected(it) } }
                            )
                        }
                    }
                }
            }
        }
    }
}
