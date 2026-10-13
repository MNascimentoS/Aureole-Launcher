package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.InflateException
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetVariant
import kotlinx.coroutines.launch

private const val TAG = "WidgetPickerAppList"

@Composable
fun WidgetGroupList(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
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

    // Group apps by starting letter
    val groupedApps = remember(state.widgetGroups) {
        state.widgetGroups
            .groupBy { it.appName.firstOrNull()?.uppercaseChar() ?: '#' }
            .toSortedMap()
    }

    // Alphabet ONLY contains letters that actually have widgets
    val alphabet = remember(groupedApps) {
        groupedApps.keys.toList()
    }

    val letterIndexMap = remember(groupedApps) {
        val map = mutableMapOf<Char, Int>()
        var currentIndex = 0
        groupedApps.forEach { (letter, apps) ->
            map[letter] = currentIndex
            currentIndex += apps.size + 1 // +1 for the header
        }
        map
    }

    val topPadding = if (isSearchExpanded) 80.dp else 16.dp

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize().padding(end = 40.dp), // space for scrubber
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
                                .padding(vertical = 12.dp),
                            contentPadding = PaddingValues(horizontal = 24.dp),
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

        // Alphabet Scrubber (hidden when search is active)
        if (!isSearchExpanded && state.searchQuery.isEmpty()) {
            CurvedAlphabetScrubber(
                alphabet = alphabet,
                options = ScrubberOptions(
                    isAlwaysVisible = true,
                    isGestureEnabled = true,
                    externalTouchY = -1f
                ),
                callbacks = ScrubberCallbacks(
                    onLetterSelected = { letter ->
                        letterIndexMap[letter]?.let { targetIndex ->
                            if (targetIndex != lastScrolledIndex) {
                                lastScrolledIndex = targetIndex
                                coroutineScope.launch {
                                    listState.scrollToItem(targetIndex)
                                }
                            }
                        }
                    }
                ),
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }

        // Search Bar or Search FAB
        AnimatedContent(
            targetState = isSearchExpanded,
            transitionSpec = {
                (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
            },
            label = "search_widget_anim",
            modifier = if (isSearchExpanded) {
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, top = 16.dp)
            } else {
                Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 24.dp)
            }
        ) { expanded ->
            if (expanded) {
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
                        value = state.searchQuery,
                        onValueChange = { onIntent(WidgetSelectorIntent.SearchQueryChanged(it)) },
                        modifier = Modifier
                            .weight(1f)
                            .focusRequester(focusRequester),
                        singleLine = true,
                        textStyle = AureoleTheme.typography.bodyLarge.copy(color = Color.White),
                        decorationBox = { innerTextField ->
                            if (state.searchQuery.isEmpty()) {
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
                                if (state.searchQuery.isNotEmpty()) {
                                    onIntent(WidgetSelectorIntent.SearchQueryChanged(""))
                                } else {
                                    isSearchExpanded = false
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
            } else {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(AureoleTheme.colors.surfaceVariant)
                        .clickable { isSearchExpanded = true },
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
            .padding(horizontal = 24.dp, vertical = 12.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (group.appIcon != null) {
            AsyncImage(
                model = group.appIcon,
                contentDescription = group.appName,
                modifier = Modifier.size(42.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.appName,
                style = AureoleTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = "${group.availableWidgets.size} Widget${if (group.availableWidgets.size > 1) "s" else ""}",
                style = AureoleTheme.typography.bodySmall,
                color = AureoleTheme.colors.onSurfaceMedium
            )
        }
    }
}

@Composable
fun WidgetPreviewCard(
    widget: WidgetVariant,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val appIcon = remember(widget.providerInfo) {
        val info = widget.providerInfo
        if (info != null) {
            try {
                info.loadIcon(context, 0)
                    ?: context.packageManager.getApplicationIcon(info.provider.packageName)
            } catch (e: PackageManager.NameNotFoundException) {
                Log.e(TAG, "App icon not found for ${info.provider.packageName}", e)
                null
            }
        } else {
            null
        }
    }

    Column(
        modifier = Modifier
            .width(220.dp)
            .background(AureoleTheme.colors.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(16.dp))
                .background(AureoleTheme.colors.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            WidgetPreviewContent(widget = widget, appIcon = appIcon)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = widget.title,
            style = AureoleTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = AureoleTheme.colors.onSurfaceHigh,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "About the widget here",
            style = AureoleTheme.typography.bodyMedium,
            color = AureoleTheme.colors.onSurfaceMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WidgetPreviewContent(
    widget: WidgetVariant,
    appIcon: Drawable?
) {
    var previewFailed by remember(widget.widgetId) { mutableStateOf(false) }

    if (widget.previewImage != null) {
        AsyncImage(
            model = widget.previewImage,
            contentDescription = widget.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        )
    } else if (widget.previewLayoutRes != 0 && !previewFailed && widget.providerInfo != null) {
        val info = widget.providerInfo
        AndroidView(
            factory = { ctx ->
                try {
                    val rv = RemoteViews(
                        info.provider.packageName,
                        widget.previewLayoutRes
                    )
                    rv.apply(ctx, null)
                } catch (e: InflateException) {
                    Log.e(TAG, "Failed to inflate previewLayout for ${widget.title}", e)
                    previewFailed = true
                    ImageView(ctx).apply { if (appIcon != null) setImageDrawable(appIcon) }
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Invalid previewLayout for ${widget.title}", e)
                    previewFailed = true
                    ImageView(ctx).apply { if (appIcon != null) setImageDrawable(appIcon) }
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        )
    } else if (appIcon != null) {
        AsyncImage(
            model = appIcon,
            contentDescription = widget.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    } else {
        Text(
            text = "Widget",
            style = AureoleTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}
