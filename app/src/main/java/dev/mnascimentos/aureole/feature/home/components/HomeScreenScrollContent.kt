package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetHost
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.theme.fadingEdges
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.SidePanelConfig
import dev.mnascimentos.aureole.feature.home.model.GridItemContentParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.ScrollViewChildItemParams
import dev.mnascimentos.aureole.feature.home.model.ScrollViewContentParams
import dev.mnascimentos.aureole.feature.home.model.VerticalScrollViewContentParams
import dev.mnascimentos.aureole.feature.home.widget.model.StackedWidgetConfig

private const val SCROLL_CHILD_MIN_HEIGHT = 70
private const val SCROLL_CHILD_MIN_WIDTH = 100
private const val SCROLL_CHILD_ROW_HEIGHT = 60
private const val SCROLL_CHILD_COL_WIDTH = 70
private const val REMOVE_BTN_SIZE = 28
private val REMOVE_BTN_PADDING = 4.dp
private val CLOSE_ICON_SIZE = 16.dp

@Composable
fun ScrollViewContainerContent(
    item: LauncherItemState,
    favConfig: FavoritesListConfig,
    appWidgetHost: AppWidgetHost,
    stackedWidgetConfig: StackedWidgetConfig,
    sidePanelConfig: SidePanelConfig
) {
    val isVertical = item.safeScrollOrientation == ScrollOrientation.VERTICAL
    val scrollState = rememberScrollState()
    val nestedScrollInterop = rememberNestedScrollInteropConnection()
    val parentNestedScrollConnection = rememberScrollViewNestedConnection(scrollState, isVertical)

    Box(
        modifier = Modifier
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
                    sidePanelConfig = sidePanelConfig,
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
                    sidePanelConfig = sidePanelConfig
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
                val delta = if (isVertical) {
                    available.y
                } else {
                    available.x
                }
                if (delta != 0f && (scrollState.canScrollForward || scrollState.canScrollBackward)) {
                    val consumedByParent = scrollState.dispatchRawDelta(-delta)
                    return if (isVertical) {
                        Offset(0f, -consumedByParent)
                    } else {
                        Offset(-consumedByParent, 0f)
                    }
                }
                return Offset.Zero
            }
        }
    }
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
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
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
                    sidePanelConfig = params.sidePanelConfig,
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
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
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
                    sidePanelConfig = params.sidePanelConfig,
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
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val parentId = params.parentId
    val childItem = params.childItem
    val isVertical = params.isVertical
    val isAppsList = childItem.safeType == LauncherItemType.APPS_LIST
    val childModifier = when {
        isAppsList && isVertical -> {
            Modifier.fillMaxWidth().wrapContentHeight()
        }
        isVertical -> {
            val childHeightDp = (childItem.rowSpan * SCROLL_CHILD_ROW_HEIGHT).dp
                .coerceAtLeast(SCROLL_CHILD_MIN_HEIGHT.dp)
            Modifier.fillMaxWidth().height(childHeightDp)
        }
        else -> {
            val childWidthDp = (childItem.colSpan * SCROLL_CHILD_COL_WIDTH).dp
                .coerceAtLeast(SCROLL_CHILD_MIN_WIDTH.dp)
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
                sidePanelConfig = params.sidePanelConfig,
                isInScrollView = true
            )
        )
        if (uiState.isGridEditMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput("block_child_" + childItem.id) {
                        detectTapGestures(onTap = {})
                    }
            )
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(REMOVE_BTN_PADDING)
                    .size(REMOVE_BTN_SIZE.dp)
                    .zIndex(100f)
                    .background(MaterialTheme.colorScheme.error, CircleShape)
                    .pointerInput("remove_btn_" + childItem.id) {
                        detectTapGestures(
                            onTap = {
                                actions.onRemoveChildFromScrollView(parentId, childItem.id)
                            }
                        )
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remover",
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(CLOSE_ICON_SIZE)
                )
            }
        }
    }
}

@Composable
private fun ScrollViewAddComponentButton(parentId: String, actions: HomeScreenActions) {
    Box(
        modifier = Modifier
            .padding(vertical = 12.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .pointerInput("add_btn_" + parentId) {
                detectTapGestures(
                    onTap = {
                        actions.onOpenAddContainerForParent(parentId)
                    }
                )
            }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Adicionar ao Scroll View",
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Componente",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
