@file:Suppress("MagicNumber") // Suppressed for animation, padding, and size constants in launcher apps list

package dev.mnascimentos.aureole.feature.home.components.appsdrawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBarParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarHazeConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
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

            Box(
                modifier = alignModifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                contentAlignment = when {
                    isSearchExpanded -> Alignment.TopCenter
                    uiState.isLeftHandedMode -> Alignment.BottomStart
                    else -> Alignment.BottomEnd
                }
            ) {
                AureoleFloatingSearchBar(
                    state = AureoleSearchBarState(
                        query = uiState.searchQuery,
                        onQueryChange = actions.onSearchQueryChanged,
                        isExpanded = isSearchExpanded,
                        onExpandedChange = { isSearchExpanded = it }
                    ),
                    params = AureoleFloatingSearchBarParams(
                        placeholder = "Search apps",
                        hazeConfig = AureoleSearchBarHazeConfig(
                            state = hazeState,
                            isEnabled = uiState.isHazeEnabled,
                            opacity = uiState.hazeOpacity
                        )
                    )
                )
            }
        }
    }
}

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

@AureolePreview
@Composable
private fun AppsListSectionDrawerPreview() {
    AureoleLauncherTheme {
        AppsListDrawer(
            listState = rememberLazyListState()
        )
    }
}
