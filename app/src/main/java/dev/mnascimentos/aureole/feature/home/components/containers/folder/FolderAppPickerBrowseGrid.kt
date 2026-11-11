package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.animation.core.withInfiniteAnimationFrameNanos
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalViewConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.utils.LongPressViewConfiguration
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.DragSelection
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FIRST_GRID_CELL_INDEX
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FOLDER_PICKER_GRID_TEST_TAG
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.PickerCellBounds
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.RemainingFolderAppGridParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.edgeAutoScrollDelta
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.hitTestCell
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.letterIndex
import dev.mnascimentos.aureole.feature.home.components.scrubber.CurvedAlphabetScrubber
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val GRID_COLUMNS = 4

/** Same hold as the prototype: shorter than the system long press, so the sweep starts quickly. */
private const val SELECTION_LONG_PRESS_TIMEOUT_MS = 280L

/** Band at the top and bottom of the grid where a selection drag scrolls it. */
private const val AUTO_SCROLL_EDGE_DP = 56f

/** Fastest auto scroll, per frame, with the finger at the very edge. */
private const val AUTO_SCROLL_MAX_STEP_DP = 14f

/**
 * Browse mode of the folder picker (no search): every app in a four column grid, the chosen ones with a check,
 * and the A–Z scrubber on the thumb side. The chosen apps are reordered in the footer tray.
 *
 * Holding an app and sliding marks (or, from a marked app, unmarks) every app the finger reaches, with a
 * tic for each. The grid is frozen during the sweep so nothing moves under the finger.
 */
@Composable
internal fun FolderAppPickerBrowseGrid(
    params: FolderAppPickerBodyParams,
    onToggleSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedPackages = params.selectedPackages
    val gridState = rememberLazyGridState()
    val haptics = LocalHapticFeedback.current
    val dragSelection = remember(selectedPackages) { DragSelection(selectedPackages) }
    var frozenApps by remember { mutableStateOf<List<AppInfo>?>(null) }
    var pointer by remember { mutableStateOf(Offset.Zero) }
    val shownApps = frozenApps ?: params.gridApps
    val isSelecting = frozenApps != null
    val tick = { haptics.performHapticFeedback(HapticFeedbackType.SegmentTick) }

    val onDragStart by rememberUpdatedState { position: Offset ->
        val pkg = gridState.packageAt(position)
        if (pkg != null) {
            frozenApps = shownApps
            pointer = position
            if (dragSelection.start(pkg, pkg in selectedPackages)) tick()
        }
    }
    val onDragMove: (Offset) -> Unit = { position ->
        pointer = position
        val pkg = gridState.packageAt(position)
        if (pkg != null && dragSelection.enter(pkg)) tick()
    }
    val onDragEnd = {
        dragSelection.end()
        frozenApps = null
    }
    // A tap that lands while a sweep runs is the end of that sweep, not a toggle of its own.
    val guardedToggle = { pkg: String -> if (!dragSelection.isActive) onToggleSelect(pkg) }

    EdgeAutoScroll(isSelecting = isSelecting, gridState = gridState, pointer = { pointer }, onScrolled = onDragMove)

    val letters = remember(shownApps) { letterIndex(shownApps, FIRST_GRID_CELL_INDEX) { it.label } }
    val showScrubber = letters.isNotEmpty() && !LocalHomeUiState.current.isAlphabetScrubberDisabled
    val gutter = if (showScrubber) AureoleDS.dimens.minTouchTarget + AureoleDS.dimens.xSmall else AureoleDS.dimens.zero

    Box(modifier = modifier.fillMaxWidth()) {
        PickerLazyGrid(
            gridState = gridState,
            isSelecting = isSelecting,
            gridModifier = Modifier
                .thumbSideGutter(gutter, params.isLeftHandedMode)
                .dragToSelect(onStart = { onDragStart(it) }, onMove = onDragMove, onEnd = onDragEnd)
        ) {
            remainingFolderAppSection(gridParams(params, shownApps, isSelecting, guardedToggle))
        }
        if (showScrubber) {
            PickerScrubber(
                letters = letters,
                gridState = gridState,
                onTick = tick,
                modifier = Modifier.align(if (params.isLeftHandedMode) Alignment.TopStart else Alignment.TopEnd)
            )
        }
    }
}

/** The grid of other apps, frozen as [shownApps] while a sweep runs. */
private fun gridParams(
    params: FolderAppPickerBodyParams,
    shownApps: List<AppInfo>,
    isSelecting: Boolean,
    onToggleSelect: (String) -> Unit
): RemainingFolderAppGridParams = RemainingFolderAppGridParams(
    apps = shownApps,
    searchQuery = params.searchQuery,
    isSelecting = isSelecting,
    isMarked = { it in params.selectedPackages },
    onToggleSelect = onToggleSelect
)

@Composable
private fun PickerLazyGrid(
    gridState: LazyGridState,
    isSelecting: Boolean,
    gridModifier: Modifier,
    content: LazyGridScope.() -> Unit
) {
    val baseConfiguration = LocalViewConfiguration.current
    val selectionConfiguration = remember(baseConfiguration) {
        LongPressViewConfiguration(baseConfiguration, SELECTION_LONG_PRESS_TIMEOUT_MS)
    }
    CompositionLocalProvider(LocalViewConfiguration provides selectionConfiguration) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLUMNS),
            state = gridState,
            modifier = Modifier
                .fillMaxSize()
                .then(gridModifier)
                .testTag(FOLDER_PICKER_GRID_TEST_TAG),
            userScrollEnabled = !isSelecting,
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xxSmall),
            content = content
        )
    }
}

/** A–Z on the thumb side: jumps to the first app of each letter, with a tic per letter. */
@Composable
private fun PickerScrubber(
    letters: Map<Char, Int>,
    gridState: LazyGridState,
    onTick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    CurvedAlphabetScrubber(
        alphabet = letters.keys.toList(),
        callbacks = ScrubberCallbacks(
            onLetterSelected = { letter ->
                onTick()
                letters[letter]?.let { index -> scope.launch { gridState.scrollToItem(index) } }
            }
        ),
        modifier = modifier,
        options = ScrubberOptions(
            isAlwaysVisible = true,
            heightFraction = 1f,
            bottomPadding = AureoleDS.dimens.xSmall
        )
    )
}

/** While a sweep runs with the finger near an edge, scrolls the grid and re-checks the app under it. */
@Composable
private fun EdgeAutoScroll(
    isSelecting: Boolean,
    gridState: LazyGridState,
    pointer: () -> Offset,
    onScrolled: (Offset) -> Unit
) {
    val density = LocalDensity.current
    val edgePx = with(density) { AUTO_SCROLL_EDGE_DP.dp.toPx() }
    val maxStepPx = with(density) { AUTO_SCROLL_MAX_STEP_DP.dp.toPx() }
    val currentOnScrolled by rememberUpdatedState(onScrolled)
    LaunchedEffect(isSelecting, edgePx, maxStepPx) {
        if (!isSelecting) return@LaunchedEffect
        while (isActive) {
            withInfiniteAnimationFrameNanos { }
            val position = pointer()
            val height = gridState.layoutInfo.viewportSize.height.toFloat()
            val delta = edgeAutoScrollDelta(position.y, height, edgePx, maxStepPx)
            if (delta != 0f && gridState.scrollBy(delta) != 0f) currentOnScrolled(position)
        }
    }
}

private fun Modifier.thumbSideGutter(gutter: Dp, isLeftHandedMode: Boolean): Modifier =
    if (isLeftHandedMode) padding(start = gutter) else padding(end = gutter)

private fun Modifier.dragToSelect(
    onStart: (Offset) -> Unit,
    onMove: (Offset) -> Unit,
    onEnd: () -> Unit
): Modifier = pointerInput(Unit) {
    detectDragGesturesAfterLongPress(
        onDragStart = onStart,
        onDrag = { change, _ ->
            change.consume()
            onMove(change.position)
        },
        onDragEnd = onEnd,
        onDragCancel = onEnd
    )
}

private fun LazyGridState.packageAt(position: Offset): String? =
    hitTestCell(position.x, position.y, layoutInfo.visibleItemsInfo.map { it.toCellBounds() })

private fun LazyGridItemInfo.toCellBounds(): PickerCellBounds =
    PickerCellBounds(key = key, x = offset.x, y = offset.y, width = size.width, height = size.height)
