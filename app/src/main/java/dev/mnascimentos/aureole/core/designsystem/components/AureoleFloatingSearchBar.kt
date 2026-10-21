package dev.mnascimentos.aureole.core.designsystem.components

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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

/**
 * Configuration for the haze effect applied to the Aureole floating search bar.
 *
 * @property state The state of the haze effect.
 * @property isEnabled Whether the haze effect is enabled.
 * @property opacity The opacity of the haze effect.
 */
data class AureoleSearchBarHazeConfig(
    val state: HazeState? = null,
    val isEnabled: Boolean = false,
    val opacity: Float = 0.5f,
)

/**
 * Sizing configuration for the Aureole floating search bar.
 *
 * @property barHeight The height of the search bar when expanded.
 * @property collapsedSize The size of the floating action bubble when collapsed.
 */
data class AureoleSearchBarSizes(
    val barHeight: Dp = 56.dp,
    val collapsedSize: Dp = 56.dp,
)

/**
 * State holder for [AureoleFloatingSearchBar].
 *
 * @param query The current search text query.
 * @param onQueryChange Callback invoked when the search text query changes.
 * @param isExpanded Whether the search bar is expanded or collapsed into a bubble.
 * @param onExpandedChange Callback invoked when the expanded state changes.
 */
data class AureoleSearchBarState(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val isExpanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
)

/**
 * Parameters for configuring [AureoleFloatingSearchBar].
 *
 * @param placeholder The placeholder text displayed when the query is empty.
 * @param focusRequester Focus requester used to control input focus when expanded.
 * @param hazeConfig Configuration for the background haze/blur effect.
 * @param sizes Sizing parameters for collapsed and expanded states.
 */
class AureoleFloatingSearchBarParams(
    val placeholder: String = "Search",
    val focusRequester: FocusRequester = FocusRequester(),
    val hazeConfig: AureoleSearchBarHazeConfig = AureoleSearchBarHazeConfig(),
    val sizes: AureoleSearchBarSizes = AureoleSearchBarSizes()
)

/**
 * Parameters for configuring internal expanded search row layout.
 *
 * @param backgroundColor Background color of the expanded search bar.
 * @param barHeight Height of the expanded search bar.
 * @param placeholder Placeholder text for search input.
 * @param focusRequester Focus requester for search text input.
 */
data class ExpandedSearchRowParams(
    val backgroundColor: Color,
    val barHeight: Dp,
    val placeholder: String = "Search",
    val focusRequester: FocusRequester = FocusRequester()
)

private val SEARCH_BLUR_RADIUS = 24.dp
private const val BUBBLE_ALPHA_SCALE = 0.85f
private const val MIN_BUBBLE_ALPHA = 0.3f
private const val MAX_BUBBLE_ALPHA = 0.95f

/**
 * Reusable floating search bar / bubble design system component for Aureole Launcher.
 *
 * Expands from a floating action bubble into a search input bar.
 *
 * @param state The search bar state (query, expansion, callbacks).
 * @param modifier The modifier to be applied to this composable.
 * @param params Additional parameters for the search bar.
 */
@Composable
fun AureoleFloatingSearchBar(
    state: AureoleSearchBarState,
    modifier: Modifier = Modifier,
    params: AureoleFloatingSearchBarParams = AureoleFloatingSearchBarParams(),
) {
    val query = state.query
    val onQueryChange = state.onQueryChange
    val isExpanded = state.isExpanded
    val onExpandedChange = state.onExpandedChange

    LaunchedEffect(query) {
        if (query.isNotEmpty() && !isExpanded) {
            onExpandedChange(true)
        }
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            params.focusRequester.requestFocus()
        }
    }

    val hazeModifier = Modifier.applyHazeConfig(params.hazeConfig)
    val bubbleBgColor = computeBubbleBgColor(params.hazeConfig)

    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = {
            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
        },
        label = "aureole_floating_search_anim",
        modifier = modifier,
    ) { expanded ->
        if (expanded) {
            ExpandedSearchRow(
                query = query,
                onQueryChange = onQueryChange,
                onCloseClick = {
                    if (query.isNotEmpty()) {
                        onQueryChange("")
                    }
                    onExpandedChange(false)
                },
                params = ExpandedSearchRowParams(
                    backgroundColor = bubbleBgColor,
                    barHeight = params.sizes.barHeight,
                    placeholder = params.placeholder,
                    focusRequester = params.focusRequester
                ),
                modifier = hazeModifier,
            )
        } else {
            CollapsedSearchIcon(
                onExpandedChange = onExpandedChange,
                backgroundColor = bubbleBgColor,
                collapsedSize = params.sizes.collapsedSize,
                modifier = hazeModifier,
            )
        }
    }
}

@Composable
@ReadOnlyComposable
private fun computeBubbleBgColor(hazeConfig: AureoleSearchBarHazeConfig): Color {
    return if (!hazeConfig.isEnabled) {
        AureoleDS.colors.surfaceVariant
    } else {
        AureoleDS.colors.surfaceVariant.copy(
            alpha = (hazeConfig.opacity * BUBBLE_ALPHA_SCALE).coerceIn(MIN_BUBBLE_ALPHA, MAX_BUBBLE_ALPHA)
        )
    }
}

@Composable
@ReadOnlyComposable
private fun Modifier.applyHazeConfig(hazeConfig: AureoleSearchBarHazeConfig): Modifier {
    return if (hazeConfig.isEnabled && (hazeConfig.state != null)) {
        this.hazeEffect(
            state = hazeConfig.state,
            style = HazeStyle(
                blurRadius = SEARCH_BLUR_RADIUS,
                tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = hazeConfig.opacity)),
            ),
        ) {
            blurEnabled = true
        }
    } else {
        this
    }
}

@Composable
private fun ExpandedSearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    params: ExpandedSearchRowParams,
    modifier: Modifier = Modifier,
) {
    val backgroundColor = params.backgroundColor
    val barHeight = params.barHeight
    val placeholder = params.placeholder
    val focusRequester = params.focusRequester

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(CircleShape)
            .then(modifier)
            .background(backgroundColor)
            .padding(horizontal = AureoleDS.dimens.medium),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AureoleDS.icons.Search(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))

            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = AureoleDS.typography.bodyLarge.copy(color = AureoleDS.colors.onSurfaceHigh),
                cursorBrush = SolidColor(AureoleDS.colors.onSurfaceHigh),
                decorationBox = { innerTextField ->
                    if (query.isEmpty()) {
                        AureoleText(
                            text = placeholder,
                            color = AureoleDS.colors.onSurfaceLow,
                            format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
                        )
                    }
                    innerTextField()
                },
                modifier = Modifier
                    .weight(1f)
                    .focusRequester(focusRequester)
            )

            IconButton(
                onClick = onCloseClick,
                modifier = Modifier.size(AureoleDS.dimens.xLarge)
            ) {
                AureoleDS.icons.Close(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }
        }
    }
}

@Composable
private fun CollapsedSearchIcon(
    onExpandedChange: (Boolean) -> Unit,
    backgroundColor: Color,
    collapsedSize: Dp,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = Modifier
            .size(collapsedSize)
            .clip(CircleShape)
            .then(modifier)
            .background(backgroundColor)
            .clickable(role = Role.Button) { onExpandedChange(true) },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleFloatingSearchBarPreview() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.medium)
        ) {
            AureoleFloatingSearchBar(
                state = AureoleSearchBarState(
                    query = "",
                    onQueryChange = {},
                    isExpanded = false,
                    onExpandedChange = {}
                )
            )
            AureoleFloatingSearchBar(
                state = AureoleSearchBarState(
                    query = "Search query",
                    onQueryChange = {},
                    isExpanded = true,
                    onExpandedChange = {}
                )
            )
        }
    }
}
