package dev.mnascimentos.aureole.feature.home.components.content

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollChildDimensions
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.fadingEdges
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ScrollViewChildItemParams
import dev.mnascimentos.aureole.feature.home.model.ScrollViewContentParams
import dev.mnascimentos.aureole.feature.home.model.VerticalScrollViewContentParams

@Composable
fun ScrollViewContainerContent(
    params: ScrollViewContainerParams,
    modifier: Modifier = Modifier
) {
    val item = params.item
    val favConfig = params.favConfig
    val appWidgetHost = params.appWidgetHost
    val stackedWidgetConfig = params.stackedWidgetConfig
    val containerConfig = params.containerConfig
    val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
    val scrollState = rememberScrollState()
    val nestedScrollInterop = rememberNestedScrollInteropConnection()
    val parentNestedScrollConnection = rememberScrollViewNestedConnection(scrollState, isVertical)

    Box(
        modifier = modifier
            .fillMaxSize()
            .fadingEdges(scrollState, isHorizontal = !isVertical)
            .nestedScroll(parentNestedScrollConnection)
            .nestedScroll(nestedScrollInterop)
    ) {
        if (isVertical) {
            VerticalScrollViewContent(
                VerticalScrollViewContentParams(
                    item = item,
                    favConfig = favConfig,
                    appWidgetHost = appWidgetHost,
                    stackedWidgetConfig = stackedWidgetConfig,
                    containerConfig = containerConfig,
                    scrollState = scrollState
                )
            )
        } else {
            HorizontalScrollViewContent(
                ScrollViewContentParams(
                    item = item,
                    favConfig = favConfig,
                    appWidgetHost = appWidgetHost,
                    stackedWidgetConfig = stackedWidgetConfig,
                    containerConfig = containerConfig
                )
            )
        }
    }
}

@Composable
private fun rememberScrollViewNestedConnection(
    scrollState: ScrollState,
    isVertical: Boolean
): NestedScrollConnection {
    return remember(scrollState, isVertical) {
        object : NestedScrollConnection {
            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource
            ): Offset {
                return dispatchParentScroll(scrollState, available, isVertical)
            }
        }
    }
}

private fun dispatchParentScroll(
    scrollState: ScrollState,
    available: Offset,
    isVertical: Boolean
): Offset {
    val delta = if (isVertical) available.y else available.x
    val canScroll = scrollState.canScrollForward || scrollState.canScrollBackward
    if (delta == 0f || !canScroll) {
        return Offset.Zero
    }

    val consumedByParent = scrollState.dispatchRawDelta(-delta)
    return if (isVertical) Offset(0f, -consumedByParent) else Offset(-consumedByParent, 0f)
}

@Composable
private fun VerticalScrollViewContent(params: VerticalScrollViewContentParams) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val item = params.item
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(params.scrollState)
            .padding(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xxSmall),
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item.safeChildren.forEach { childItem ->
            ScrollViewChildItem(
                ScrollViewChildItemParams(
                    parentId = item.id,
                    childItem = childItem,
                    favConfig = params.favConfig,
                    appWidgetHost = params.appWidgetHost,
                    stackedWidgetConfig = params.stackedWidgetConfig,
                    containerConfig = params.containerConfig,
                    isVertical = true
                )
            )
        }
        if (uiState.isGridEditMode) {
            ScrollViewAddComponentButton(parentId = item.id, actions = actions)
        }
    }
}

@Composable
private fun HorizontalScrollViewContent(params: ScrollViewContentParams) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val item = params.item
    Row(
        modifier = Modifier
            .fillMaxSize()
            .horizontalScroll(rememberScrollState())
            .padding(AureoleDS.dimens.xSmall),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        item.safeChildren.forEach { childItem ->
            ScrollViewChildItem(
                ScrollViewChildItemParams(
                    parentId = item.id,
                    childItem = childItem,
                    favConfig = params.favConfig,
                    appWidgetHost = params.appWidgetHost,
                    stackedWidgetConfig = params.stackedWidgetConfig,
                    containerConfig = params.containerConfig,
                    isVertical = false
                )
            )
        }
        if (uiState.isGridEditMode) {
            ScrollViewAddComponentButton(parentId = item.id, actions = actions)
        }
    }
}

@Composable
private fun ScrollViewChildItem(params: ScrollViewChildItemParams) {
    val childItem = params.childItem
    val isVertical = params.isVertical
    val isAppsList = childItem.safeType == LauncherItemType.APPS_LIST
    val childModifier = when {
        isAppsList && isVertical -> Modifier.fillMaxWidth().wrapContentHeight()
        isVertical -> {
            val childHeightDp = (ScrollChildDimensions.RowHeight * childItem.rowSpan)
                .coerceAtLeast(ScrollChildDimensions.MinHeight)
            Modifier.fillMaxWidth().height(childHeightDp)
        }
        else -> {
            val childWidthDp = (ScrollChildDimensions.ColWidth * childItem.colSpan)
                .coerceAtLeast(ScrollChildDimensions.MinWidth)
            Modifier.width(childWidthDp).fillMaxHeight()
        }
    }

    Box(modifier = childModifier) {
        GridItemContent(
            GridItemContentParams(
                item = childItem,
                favConfig = params.favConfig,
                appWidgetHost = params.appWidgetHost,
                stackedWidgetConfig = params.stackedWidgetConfig,
                containerConfig = params.containerConfig,
                isInScrollView = true
            )
        )
    }
}

@Composable
private fun ScrollViewAddComponentButton(parentId: String, actions: HomeScreenActions) {
    Box(
        modifier = Modifier
            .padding(vertical = AureoleDS.dimens.small)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMediumLarge))
            .background(AureoleDS.colors.surfaceVariant)
            .pointerInput("add_btn_" + parentId) {
                detectTapGestures(
                    onTap = {
                        actions.onOpenAddContainerForParent(parentId)
                    }
                )
            }
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AureoleDS.icons.Add(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(AureoleDS.dimens.iconMediumSmall)
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.xxSmall))
            AureoleText(
                text = "Componente",
                color = AureoleDS.colors.onSurfaceHigh,
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
    }
}

@AureolePreview
@Composable
private fun ScrollViewAddComponentButtonPreview() {
    val mockActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )
    AureoleLauncherTheme {
        ScrollViewAddComponentButton(parentId = "p1", actions = mockActions)
    }
}
