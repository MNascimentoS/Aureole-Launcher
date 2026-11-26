package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.NestedSwipe
import dev.mnascimentos.aureole.feature.home.model.SwipeDownConditions
import dev.mnascimentos.aureole.feature.home.model.SwipeScrollTracker
import dev.mnascimentos.aureole.feature.home.model.SwipeUpConditions
import dev.mnascimentos.aureole.feature.home.model.VERTICAL_INTENT_THRESHOLD_DP
import dev.mnascimentos.aureole.feature.home.model.VerticalSwipe
import dev.mnascimentos.aureole.feature.home.model.activeHomeGestures
import dev.mnascimentos.aureole.feature.home.model.isOverlayActive
import dev.mnascimentos.aureole.feature.home.model.opensAllApps
import dev.mnascimentos.aureole.feature.home.model.opensNotifications

@Composable
internal fun rememberSwipeDownNested(uiState: MainUiState, actions: HomeScreenActions): NestedSwipe {
    val density = LocalDensity.current
    val canOpen = SwipeDownConditions(
        isEnabled = uiState.isSwipeDownNotificationsEnabled,
        isOverlayActive = uiState.isOverlayActive,
        isEditing = uiState.isGridEditMode
    ).opensNotifications()
    val currentCanOpen by rememberUpdatedState(canOpen)
    val currentOnExpand by rememberUpdatedState(actions.onExpandNotificationShade)
    val tracker = remember(density) {
        SwipeScrollTracker(thresholdPx = with(density) { VERTICAL_INTENT_THRESHOLD_DP.dp.toPx() })
    }
    val connection = remember(tracker) {
        swipeNestedConnection(tracker, canOpen = { currentCanOpen }, onOpen = { currentOnExpand() })
    }
    val isOn = uiState.activeHomeGestures().isSwipeDownOn
    return NestedSwipe(canOpen, if (isOn) Modifier.homeSwipeNestedScroll(tracker, connection) else Modifier)
}

@Composable
internal fun rememberSwipeUpNested(uiState: MainUiState, actions: HomeScreenActions): NestedSwipe {
    val density = LocalDensity.current
    val canOpen = SwipeUpConditions(
        isEnabled = uiState.isSwipeUpAllAppsEnabled,
        isOverlayActive = uiState.isOverlayActive,
        isEditing = uiState.isGridEditMode
    ).opensAllApps()
    val currentCanOpen by rememberUpdatedState(canOpen)
    val currentOnOpen by rememberUpdatedState(actions.onAllAppsDrawerOpen)
    val tracker = remember(density) {
        SwipeScrollTracker(
            thresholdPx = with(density) { VERTICAL_INTENT_THRESHOLD_DP.dp.toPx() },
            direction = VerticalSwipe.UP
        )
    }
    val connection = remember(tracker) {
        swipeNestedConnection(tracker, canOpen = { currentCanOpen }, onOpen = { currentOnOpen() })
    }
    val isOn = uiState.activeHomeGestures().isSwipeUpOn
    return NestedSwipe(canOpen, if (isOn) Modifier.homeSwipeNestedScroll(tracker, connection) else Modifier)
}
