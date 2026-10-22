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
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.icons.Close
import dev.mnascimentos.aureole.core.designsystem.icons.Search
import dev.mnascimentos.aureole.core.designsystem.model.AureoleFloatingSearchBarParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleSearchBarHazeConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleSearchBarSizes
import dev.mnascimentos.aureole.core.designsystem.model.AureoleSearchBarState
import dev.mnascimentos.aureole.core.designsystem.model.ExpandedSearchRowParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

typealias AureoleSearchBarHazeConfig = AureoleSearchBarHazeConfig
typealias AureoleSearchBarSizes = AureoleSearchBarSizes
typealias AureoleSearchBarState = AureoleSearchBarState
typealias AureoleFloatingSearchBarParams = AureoleFloatingSearchBarParams
typealias ExpandedSearchRowParams = ExpandedSearchRowParams

private val SEARCH_BLUR_RADIUS = 24.dp
private const val BUBBLE_ALPHA_SCALE = 0.85f
private const val MIN_BUBBLE_ALPHA = 0.3f
private const val MAX_BUBBLE_ALPHA = 0.95f

/**
 * Floating Action Search Bar for Aureole Launcher.
 *
 * It transitions smoothly between a collapsed floating action bubble and an
 * expanded floating search input bar.
 *
 * @param state State holder managing query and expansion state.
 * @param modifier The modifier to be applied to the layout.
 * @param params Additional visual and layout configuration parameters.
 */
@Composable
fun AureoleFloatingSearchBar(
    state: AureoleSearchBarState,
    modifier: Modifier = Modifier,
    params: AureoleFloatingSearchBarParams = AureoleFloatingSearchBarParams(),
) {
    val isExpanded = state.isExpanded

    LaunchedEffect(isExpanded) {
        if (isExpanded) {
            params.focusRequester.requestFocus()
        }
    }

    val hazeStyle = HazeStyle(
        blurRadius = SEARCH_BLUR_RADIUS,
        tint = HazeTint(AureoleDS.colors.surfaceVariant.copy(alpha = params.hazeConfig.opacity)),
    )

    AnimatedContent(
        targetState = isExpanded,
        transitionSpec = {
            (fadeIn() + scaleIn()).togetherWith(fadeOut() + scaleOut())
        },
        label = "AureoleFloatingSearchBarContent",
        modifier = modifier
    ) { expanded ->
        if (expanded) {
            ExpandedSearchRow(
                state = state,
                params = ExpandedSearchRowParams(
                    backgroundColor = getSearchBarBackgroundColor(params.hazeConfig),
                    barHeight = params.sizes.barHeight,
                    placeholder = params.placeholder,
                    focusRequester = params.focusRequester
                ),
                hazeConfig = params.hazeConfig,
                hazeStyle = hazeStyle
            )
        } else {
            CollapsedSearchBubble(
                onExpand = { state.onExpandedChange(true) },
                params = params,
                hazeStyle = hazeStyle
            )
        }
    }
}

@Composable
private fun ExpandedSearchRow(
    state: AureoleSearchBarState,
    params: ExpandedSearchRowParams,
    hazeConfig: AureoleSearchBarHazeConfig,
    hazeStyle: HazeStyle
) {
    val searchBarModifier = Modifier
        .fillMaxWidth()
        .height(params.barHeight)
        .clip(CircleShape)
        .then(
            if (hazeConfig.isEnabled && hazeConfig.state != null) {
                Modifier.hazeEffect(state = hazeConfig.state, style = hazeStyle) { blurEnabled = true }
            } else {
                Modifier
            }
        )
        .background(params.backgroundColor)
        .padding(horizontal = AureoleDS.dimens.medium)

    Row(
        modifier = searchBarModifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )

        Spacer(modifier = Modifier.width(AureoleDS.dimens.small))

        ExpandedSearchInputField(
            query = state.query,
            onQueryChange = state.onQueryChange,
            placeholder = params.placeholder,
            focusRequester = params.focusRequester,
            modifier = Modifier.weight(1f)
        )

        Spacer(modifier = Modifier.width(AureoleDS.dimens.xSmall))

        IconButton(
            onClick = {
                state.onQueryChange("")
                state.onExpandedChange(false)
            },
            modifier = Modifier.size(AureoleDS.dimens.iconXLarge)
        ) {
            AureoleDS.icons.Close(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMedium)
            )
        }
    }
}

@Composable
private fun ExpandedSearchInputField(
    query: String,
    onQueryChange: (String) -> Unit,
    placeholder: String,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.CenterStart
    ) {
        if (query.isEmpty()) {
            AureoleText(
                text = placeholder,
                color = AureoleDS.colors.onSurfaceLow,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        }

        BasicTextField(
            value = query,
            onValueChange = onQueryChange,
            singleLine = true,
            textStyle = AureoleDS.typography.bodyMedium.copy(color = AureoleDS.colors.onSurfaceHigh),
            cursorBrush = SolidColor(AureoleDS.colors.onSurfaceHigh),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester)
        )
    }
}

@Composable
private fun CollapsedSearchBubble(
    onExpand: () -> Unit,
    params: AureoleFloatingSearchBarParams,
    hazeStyle: HazeStyle
) {
    val bubbleModifier = Modifier
        .size(params.sizes.collapsedSize)
        .clip(CircleShape)
        .then(
            if (params.hazeConfig.isEnabled && params.hazeConfig.state != null) {
                Modifier.hazeEffect(state = params.hazeConfig.state, style = hazeStyle) { blurEnabled = true }
            } else {
                Modifier
            }
        )
        .background(getSearchBarBackgroundColor(params.hazeConfig))
        .clickable(onClick = onExpand, role = Role.Button)

    Box(
        modifier = bubbleModifier,
        contentAlignment = Alignment.Center
    ) {
        AureoleDS.icons.Search(
            tint = AureoleDS.colors.onSurfaceHigh,
            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
        )
    }
}

@Composable
@ReadOnlyComposable
private fun getSearchBarBackgroundColor(hazeConfig: AureoleSearchBarHazeConfig): Color {
    val surfaceColor = AureoleDS.colors.surfaceVariant
    return if (hazeConfig.isEnabled && hazeConfig.state != null) {
        val calculatedAlpha = (hazeConfig.opacity * BUBBLE_ALPHA_SCALE).coerceIn(
            MIN_BUBBLE_ALPHA,
            MAX_BUBBLE_ALPHA
        )
        surfaceColor.copy(alpha = calculatedAlpha)
    } else {
        surfaceColor
    }
}

@AureolePreview
@Composable
private fun AureoleFloatingSearchBarCollapsedPreview() {
    AureoleLauncherTheme {
        AureoleFloatingSearchBar(
            state = AureoleSearchBarState(
                query = "",
                onQueryChange = {},
                isExpanded = false,
                onExpandedChange = {}
            )
        )
    }
}

@AureolePreview
@Composable
private fun AureoleFloatingSearchBarExpandedPreview() {
    AureoleLauncherTheme {
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
