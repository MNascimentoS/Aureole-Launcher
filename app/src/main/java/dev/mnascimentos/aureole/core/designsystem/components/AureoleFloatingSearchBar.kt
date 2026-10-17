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
import androidx.compose.runtime.remember
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

/**
 * Reusable floating search bar / bubble design system component for Aureole Launcher.
 *
 * Expands from a floating action bubble into a search input bar.
 *
 * @param query The current search query.
 * @param onQueryChange Callback invoked when the search query changes.
 * @param isExpanded Whether the search bar is currently expanded.
 * @param onExpandedChange Callback invoked when the expanded state changes.
 * @param modifier The modifier to be applied to this composable.
 * @param placeholder The text to display when the query is empty.
 * @param focusRequester The focus requester for the search input field.
 * @param hazeConfig The configuration for the haze effect on the search bar.
 * @param sizes The sizing configuration for the search bar.
 */
@Suppress("LongMethod", "MagicNumber", "LongParameterList")
@Composable
fun AureoleFloatingSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isExpanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    focusRequester: FocusRequester = remember { FocusRequester() },
    hazeConfig: AureoleSearchBarHazeConfig = AureoleSearchBarHazeConfig(),
    sizes: AureoleSearchBarSizes = AureoleSearchBarSizes(),
) {
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
                backgroundColor = bubbleBgColor,
                barHeight = sizes.barHeight,
                modifier = hazeModifier,
                placeholder = placeholder,
                focusRequester = focusRequester,
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

@Suppress("LongParameterList")
@Composable
private fun ExpandedSearchRow(
    query: String,
    onQueryChange: (String) -> Unit,
    onCloseClick: () -> Unit,
    backgroundColor: Color,
    barHeight: Dp,
    modifier: Modifier = Modifier,
    placeholder: String = "Search",
    focusRequester: FocusRequester = remember { FocusRequester() },
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(barHeight)
            .clip(CircleShape)
            .then(modifier)
            .background(backgroundColor)
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            AureoleDS.icons.Search(
                tint = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))

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
                            style = AureoleDS.typography.bodyLarge
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
                modifier = Modifier.size(32.dp)
            ) {
                AureoleDS.icons.Close(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(20.dp)
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
            .clickable { onExpandedChange(true) },
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(24.dp)
        )
    }
}

@AureolePreview
@Composable
private fun AureoleFloatingSearchBarPreview() {
    AureoleLauncherTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AureoleFloatingSearchBar(
                query = "",
                onQueryChange = {},
                isExpanded = false,
                onExpandedChange = {}
            )
            AureoleFloatingSearchBar(
                query = "Search query",
                onQueryChange = {},
                isExpanded = true,
                onExpandedChange = {}
            )
        }
    }
}
