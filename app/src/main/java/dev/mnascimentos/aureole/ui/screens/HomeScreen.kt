package dev.mnascimentos.aureole.ui.screens

import android.appwidget.AppWidgetHost
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import dev.mnascimentos.aureole.data.model.AppInfo
import dev.mnascimentos.aureole.ui.MainUiState
import dev.mnascimentos.aureole.ui.components.AppsListDrawer
import dev.mnascimentos.aureole.ui.components.ClockHeader
import dev.mnascimentos.aureole.ui.components.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.ui.components.WidgetsSection
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private val AUTO_HIDE_DRAWER_DELAY = 4000.milliseconds

data class HomeScreenActions(
    val onWidgetRowHeightChanged: (Dp) -> Unit,
    val onAddWidgetClick: () -> Unit,
    val onRemoveWidgetClick: (Int) -> Unit,
    val onAppClick: (AppInfo) -> Unit,
)

@Composable
fun HomeScreen(
    uiState: MainUiState,
    topWidgetIds: List<Int>,
    appWidgetHost: AppWidgetHost,
    widgetRowHeight: Dp,
    actions: HomeScreenActions,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current
    var currentHeightPx by remember(widgetRowHeight) {
        mutableFloatStateOf(with(density) { widgetRowHeight.toPx() })
    }

    var isDrawerOpen by remember { mutableStateOf(value = false) }
    var autoHideJob by remember { mutableStateOf<Job?>(null) }

    fun scheduleAutoHide() {
        autoHideJob?.cancel()
        autoHideJob = coroutineScope.launch {
            delay(AUTO_HIDE_DRAWER_DELAY)
            isDrawerOpen = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else {
            // Home Screen (Default view: Clock + Widgets only)
            Column(modifier = Modifier.fillMaxSize()) {
                ClockHeader()

                WidgetsSection(
                    topWidgetIds = topWidgetIds,
                    appWidgetHost = appWidgetHost,
                    currentHeightDp = with(density) { currentHeightPx.toDp() },
                    onHeightChange = { newHeightPx -> currentHeightPx = newHeightPx },
                    actions = actions,
                    currentHeightPx = currentHeightPx
                )
            }

            // App Drawer Overlay (When user interacts on the right screen edge)
            AnimatedVisibility(
                visible = isDrawerOpen,
                enter = fadeIn() + slideInHorizontally { -it / 2 },
                exit = fadeOut() + slideOutHorizontally { -it / 2 },
                modifier = Modifier.fillMaxSize()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background.copy(alpha = 0.95f))
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onTap = {
                                    isDrawerOpen = false
                                    autoHideJob?.cancel()
                                }
                            )
                        }
                ) {
                    AppsListDrawer(
                        uiState = uiState,
                        actions = actions,
                        listState = listState,
                        modifier = Modifier.align(Alignment.CenterStart)
                    )
                }
            }

            // Right Screen Curved Alphabet Scrubber Interaction Zone
            CurvedAlphabetScrubber(
                alphabet = uiState.alphabet,
                onLetterSelected = { letter ->
                    uiState.letterIndexMap[letter]?.let { targetIndex ->
                        coroutineScope.launch {
                            listState.scrollToItem(targetIndex)
                        }
                    }
                },
                onInteractionStarted = {
                    autoHideJob?.cancel()
                    isDrawerOpen = true
                },
                onInteractionEnded = {
                    scheduleAutoHide()
                },
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}
