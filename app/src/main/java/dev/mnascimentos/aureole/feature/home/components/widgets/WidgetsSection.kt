package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetHost
import android.appwidget.AppWidgetHostView
import android.appwidget.AppWidgetManager
import android.view.View
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.rememberNestedScrollInteropConnection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalAnimatedVisibilityScope
import dev.mnascimentos.aureole.core.designsystem.utils.LocalSharedTransitionScope
import dev.mnascimentos.aureole.feature.home.components.widgets.model.PagerContentParams
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetPagerParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.getWidgetIdsForStack
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

private const val COLOR_ANIMATION_DURATION_MS = 150
private const val LONG_PRESS_MARGIN_MS = 50L
private const val MIN_LONG_PRESS_TIMEOUT_MS = 200L

private object IndicatorMetrics {
    val IndicatorHeight = 5.dp
}

@Composable
fun StackedWidgetSection(
    config: StackedWidgetConfig,
    modifier: Modifier = Modifier,
    appWidgetHost: AppWidgetHost? = null,
    stackId: String = "top_widgets"
) {
    val uiState = LocalHomeUiState.current
    val widgetIds = uiState.getWidgetIdsForStack(stackId)
    val showAddButton = false
    val pageCount = widgetIds.size
    val pagerState = rememberPagerState(pageCount = { pageCount })

    val showDotsForStack = uiState.widgetStackDots[stackId] ?: true

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StackedWidgetPagerContent(
            pagerParams = StackedWidgetPagerParams(
                config = config,
                widgetIds = widgetIds,
                appWidgetHost = appWidgetHost,
                stackId = stackId,
                params = PagerContentParams(
                    showAddButton = showAddButton,
                    pageCount = pageCount,
                    pagerState = pagerState,
                    hasFillMaxSize = true
                )
            ),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        )

        if (pageCount >= 1 && showDotsForStack) {
            WidgetPositionIndicatorBar(
                pageCount = pageCount,
                currentPage = pagerState.currentPage
            )
        }
    }
}

@Composable
private fun StackedWidgetPagerContent(
    pagerParams: StackedWidgetPagerParams,
    modifier: Modifier = Modifier
) {
    val config = pagerParams.config
    val widgetIds = pagerParams.widgetIds
    val appWidgetHost = pagerParams.appWidgetHost
    val stackId = pagerParams.stackId
    val params = pagerParams.params

    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val pageCount = params.pageCount
    val showAddButton = params.showAddButton

    if (pageCount > 0) {
        HorizontalPager(
            state = params.pagerState,
            modifier = modifier
        ) { page ->
            if (page < widgetIds.size) {
                val widgetId = widgetIds[page]
                WidgetHostItem(
                    widgetId = widgetId,
                    stackId = stackId,
                    appWidgetHost = appWidgetHost,
                    modifier = Modifier.fillMaxSize()
                )
            } else if (showAddButton) {
                AddWidgetButton(
                    onClick = { actions.onAddWidgetClick(stackId) },
                    modifier = Modifier.fillMaxSize(),
                    hazeState = config.hazeState,
                    isHazeEnabled = uiState.isHazeEnabled,
                    hazeOpacity = uiState.hazeOpacity
                )
            }
        }
    } else {
        Spacer(modifier = modifier.height(config.currentHeightDp))
    }
}

@Composable
fun WidgetPositionIndicatorBar(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .padding(top = AureoleDS.dimens.xxSmall, bottom = AureoleDS.dimens.xxSmall)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = (index == currentPage)
            val animatedWidth by animateDpAsState(
                targetValue = if (isSelected) AureoleDS.dimens.iconLargePlus else AureoleDS.dimens.xSmall,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "indicator_width"
            )
            val animatedColor by animateColorAsState(
                targetValue = if (isSelected) {
                    AureoleDS.colors.onSurfaceHigh
                } else {
                    AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.25f)
                },
                animationSpec = tween(COLOR_ANIMATION_DURATION_MS),
                label = "indicator_color"
            )

            Box(
                modifier = Modifier
                    .padding(horizontal = AureoleDS.dimens.minimal)
                    .height(IndicatorMetrics.IndicatorHeight)
                    .width(animatedWidth)
                    .clip(CircleShape)
                    .background(animatedColor)
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun WidgetHostItem(
    widgetId: Int,
    stackId: String,
    modifier: Modifier = Modifier,
    appWidgetHost: AppWidgetHost? = null
) {
    val actions = LocalHomeActions.current
    val coroutineScope = rememberCoroutineScope()

    val sharedTransitionScope = LocalSharedTransitionScope.current
    val animatedVisibilityScope = LocalAnimatedVisibilityScope.current

    val sharedModifier = if (sharedTransitionScope != null && animatedVisibilityScope != null) {
        with(sharedTransitionScope) {
            Modifier.sharedElement(
                sharedContentState = rememberSharedContentState(key = "widget_$widgetId"),
                animatedVisibilityScope = animatedVisibilityScope
            )
        }
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .then(sharedModifier)
            .pointerInput(widgetId, stackId) {
                val longPressTimeout = viewConfiguration.longPressTimeoutMillis
                awaitEachGesture {
                    val down = awaitFirstDown(pass = PointerEventPass.Initial)
                    var isLongPressTriggered = false

                    val job = coroutineScope.launch {
                        delay((longPressTimeout - LONG_PRESS_MARGIN_MS).coerceAtLeast(MIN_LONG_PRESS_TIMEOUT_MS))
                        isLongPressTriggered = true
                        down.consume()
                        actions.onOpenWidgetStackBottomSheet(widgetId, stackId)
                    }

                    try {
                        val up = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                        job.cancel()
                        if (isLongPressTriggered) {
                            up?.consume()
                        }
                    } catch (e: IllegalArgumentException) {
                        Timber.tag("WidgetHostItem").e(e, "Gesture error")
                        job.cancel()
                    }
                }
            }
    ) {
        AndroidView(
            factory = { context -> createWidgetHostView(context, appWidgetHost, widgetId) },
            update = { view -> updateWidgetHostView(view, widgetId) },
            modifier = Modifier.fillMaxSize().nestedScroll(rememberNestedScrollInteropConnection())
        )
    }
}

private fun updateWidgetHostView(view: View, widgetId: Int) {
    if (view is AppWidgetHostView) {
        val appWidgetManager = AppWidgetManager.getInstance(view.context)
        val appWidgetInfo = appWidgetManager.getAppWidgetInfo(widgetId)
        if (appWidgetInfo != null) {
            applyWidgetOptionsIfNeeded(view, widgetId, appWidgetManager, appWidgetInfo)
        }
    }
}

@AureolePreview
@Composable
private fun WidgetPositionIndicatorBarPreview() {
    AureoleLauncherTheme {
        WidgetPositionIndicatorBar(
            pageCount = 4,
            currentPage = 1
        )
    }
}
