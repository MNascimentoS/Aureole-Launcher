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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
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
 * @property collapsedSize The size of the floating action button when collapsed.
 */
data class AureoleSearchBarSizes(
    val barHeight: Dp = 56.dp,
    val collapsedSize: Dp = 56.dp,
)

data class AureoleSearchBarState(
    val query: String,
    val onQueryChange: (String) -> Unit,
    val isExpanded: Boolean,
    val onExpandedChange: (Boolean) -> Unit,
)

class AureoleFloatingSearchBarParams(
    val placeholder: String = "Search",
    val focusRequester: FocusRequester = FocusRequester(),
    val hazeConfig: AureoleSearchBarHazeConfig = AureoleSearchBarHazeConfig(),
    val sizes: AureoleSearchBarSizes = AureoleSearchBarSizes()
)

data class ExpandedSearchRowParams(
    val backgroundColor: Color,
    val barHeight: Dp,
    val placeholder: String = "Search",
    val focusRequester: FocusRequester = FocusRequester()
)

/**
 * Reusable floating search bar / bubble design system component for Aureole Launcher.
 *
 * Expands from a floating action bubble into a search input bar.
 *
 * @param state The search bar state (query, expansion, callbacks).
 * @param modifier The modifier to be applied to this composable.
 * @param params Additional parameters for the search bar.
 */
@Suppress(
    "LongMethod",
    "MagicNumber"
) // Component needs many states and handles custom UI dimensions
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

    val placeholder = params.placeholder
    val focusRequester = params.focusRequester
    val hazeConfig = params.hazeConfig
    val sizes = params.sizes

    LaunchedEffect(query) {
        if (query.isNotEmpty() && !isExpanded) {
            onExpandedChange(true)
        }
    }

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            focusRequester.requestFocus()
        }
    }

    val hazeModifier = if (hazeConfig.isEnabled && (hazeConfig.state != null)) {
        Modifier.hazeEffect(
            state = hazeConfig.state,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = hazeConfig.opacity)),
            ),
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val bubbleBgColor = if (!hazeConfig.isEnabled) {
        AureoleDS.colors.surfaceVariant
    } else {
        AureoleDS.colors.surfaceVariant.copy(alpha = (hazeConfig.opacity * 0.85f).coerceIn(0.3f, 0.95f))
    }

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
                    barHeight = sizes.barHeight,
                    placeholder = placeholder,
                    focusRequester = focusRequester
                ),
                modifier = hazeModifier,
            )
        } else {
            CollapsedSearchIcon(
                onExpandedChange = onExpandedChange,
                backgroundColor = bubbleBgColor,
                collapsedSize = sizes.collapsedSize,
                modifier = hazeModifier,
            )
        }
    }
}

@Suppress(
    "LongMethod",
    "MagicNumber"
) // Component needs many states and handles custom UI dimensions
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

@Suppress(
    "LongMethod",
    "MagicNumber"
) // Component needs many states and handles custom UI dimensions
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
            .clickable { onExpandedChange(true) },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconLarge)
        )
    }
}

@AureolePreview
@Suppress(
    "LongMethod",
    "MagicNumber"
) // Component needs many states and handles custom UI dimensions
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
