@file:Suppress("MagicNumber") // Suppressed for animation, padding, and size constants in launcher apps list

package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState

private const val DRAWER_HAZE_TINT_FACTOR = 0.5f
private const val DRAWER_HAZE_MIN_ALPHA = 0.15f
private const val DRAWER_HAZE_MAX_ALPHA = 0.65f

@Composable
fun AppsListDrawer(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val startPadding = if (uiState.isLeftHandedMode) 72.dp else 24.dp
    val endPadding = if (uiState.isLeftHandedMode) 24.dp else 72.dp

    val baseBgColor = AureoleDS.colors.background
    val hasHaze = uiState.isHazeEnabled && (hazeState != null)
    val hazeModifier =
        Modifier.buildDrawerHaze(hasHaze, hazeState, baseBgColor, uiState.hazeOpacity)
    val drawerBgColor = if (hasHaze) Color.Transparent else baseBgColor

    var isSearchExpanded by remember { mutableStateOf(uiState.searchQuery.isNotEmpty()) }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .then(hazeModifier)
            .background(drawerBgColor)
    ) {
        val topOffsetDp = (maxHeight * (uiState.headerOffsetPercent / 100f)).coerceAtLeast(16.dp)
        val extraTopPadding = if (uiState.showSearchBarInAllApps && isSearchExpanded) 88.dp else 0.dp

        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = startPadding,
                top = topOffsetDp + extraTopPadding,
                end = endPadding,
                bottom = 120.dp
            ),
            modifier = Modifier
                .fillMaxSize()
                .fadingEdges(listState)
        ) {
            appsListItems(uiState, actions)
        }

        if (uiState.showSearchBarInAllApps) {
            val alignModifier = if (isSearchExpanded) {
                Modifier
                    .align(Alignment.TopCenter)
                    .statusBarsPadding()
            } else {
                val align = if (uiState.isLeftHandedMode) Alignment.BottomStart else Alignment.BottomEnd
                Modifier
                    .align(align)
                    .navigationBarsPadding()
                    .imePadding()
            }

            FloatingSearchBubble(
                state = SearchBubbleState(
                    query = uiState.searchQuery,
                    onQueryChange = actions.onSearchQueryChanged,
                    isExpanded = isSearchExpanded,
                    onExpandedChange = { isSearchExpanded = it },
                ),
                hazeState = hazeState,
                modifier = alignModifier
            )
        }
    }
}

@Composable
private fun Modifier.buildDrawerHaze(
    hasHaze: Boolean,
    hazeState: HazeState?,
    baseBgColor: Color,
    hazeOpacity: Float
): Modifier {
    if (!hasHaze || hazeState == null) return this
    val tintAlpha = (hazeOpacity * DRAWER_HAZE_TINT_FACTOR).coerceIn(
        DRAWER_HAZE_MIN_ALPHA,
        DRAWER_HAZE_MAX_ALPHA
    )
    return this.then(
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(baseBgColor.copy(alpha = tintAlpha))
            )
        ) {
            blurEnabled = true
        }
    )
}

data class SearchBubbleState(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val isExpanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
)

@Composable
private fun FloatingSearchBubble(
    state: SearchBubbleState,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val uiState = LocalHomeUiState.current
    if (!uiState.showSearchBarInAllApps) return

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(state.query) {
        if (state.query.isNotEmpty() && !state.isExpanded) {
            state.onExpandedChange(true)
        }
    }

    LaunchedEffect(state.isExpanded) {
        if (state.isExpanded) {
            focusRequester.requestFocus()
        }
    }

    val searchBarHazeModifier = if (uiState.isHazeEnabled && hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = uiState.hazeOpacity))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val bubbleBgColor = if (!uiState.isHazeEnabled) {
        AureoleDS.colors.surfaceVariant
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = (uiState.hazeOpacity * 0.85f).coerceIn(0.3f, 0.95f))
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        contentAlignment = when {
            state.isExpanded -> Alignment.TopCenter
            uiState.isLeftHandedMode -> Alignment.BottomStart
            else -> Alignment.BottomEnd
        }
    ) {
        SearchBubbleAnimatedContent(
            state = state,
            focusRequester = focusRequester,
            hazeModifier = searchBarHazeModifier,
            bubbleBgColor = bubbleBgColor
        )
    }
}

@Composable
private fun SearchBubbleAnimatedContent(
    state: SearchBubbleState,
    focusRequester: FocusRequester,
    hazeModifier: Modifier,
    bubbleBgColor: Color
) {
    AnimatedContent(
        targetState = state.isExpanded,
        transitionSpec = {
            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
        },
        label = "search_bubble_anim"
    ) { expanded ->
        if (expanded) {
            ExpandedSearchBubbleBar(
                state = state,
                focusRequester = focusRequester,
                hazeModifier = hazeModifier,
                bubbleBgColor = bubbleBgColor
            )
        } else {
            CollapsedSearchBubbleIcon(
                onExpandedChange = state.onExpandedChange,
                hazeModifier = hazeModifier,
                bubbleBgColor = bubbleBgColor
            )
        }
    }
}

@Composable
private fun ExpandedSearchBubbleBar(
    state: SearchBubbleState,
    focusRequester: FocusRequester,
    hazeModifier: Modifier,
    bubbleBgColor: Color
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(CircleShape)
            .then(hazeModifier)
            .background(bubbleBgColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        SearchInputRow(state = state, focusRequester = focusRequester)
    }
}

@Composable
private fun SearchInputRow(
    state: SearchBubbleState,
    focusRequester: FocusRequester
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        TextField(
            value = state.query,
            onValueChange = state.onQueryChange,
            placeholder = {
                AureoleText(
                    text = "Search apps",
                    color = AureoleDS.colors.onSurfaceLow,
                    style = AureoleDS.typography.bodyLarge
                )
            },
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = AureoleDS.colors.onSurfaceHigh,
                unfocusedTextColor = AureoleDS.colors.onSurfaceHigh
            ),
            modifier = Modifier
                .weight(1f)
                .focusRequester(focusRequester)
        )

        IconButton(
            onClick = {
                state.onQueryChange("")
                state.onExpandedChange(false)
            },
            modifier = Modifier.size(32.dp)
        ) {
            AureoleDS.icons.Close(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun CollapsedSearchBubbleIcon(
    onExpandedChange: (Boolean) -> Unit,
    hazeModifier: Modifier,
    bubbleBgColor: Color
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .clip(CircleShape)
            .then(hazeModifier)
            .background(bubbleBgColor)
            .clickable { onExpandedChange(true) },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(24.dp)
        )
    }
}
