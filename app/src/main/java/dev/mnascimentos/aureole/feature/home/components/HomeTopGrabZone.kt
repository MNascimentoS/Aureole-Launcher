package dev.mnascimentos.aureole.feature.home.components

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS

const val HOME_TOP_GRAB_ZONE_TAG = "home_top_grab_zone"

/**
 * Empty strip over the status bar and the top margin of the home: holding it calls [onLongPress] (opens Customize).
 * Only long presses are handled, so swipes (notifications) pass through. Inactive in edit mode.
 */
@Composable
fun HomeTopGrabZone(enabled: Boolean, onLongPress: () -> Unit, modifier: Modifier = Modifier) {
    if (!enabled) return
    val statusBarHeight = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val currentOnLongPress by rememberUpdatedState(onLongPress)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(statusBarHeight + AureoleDS.dimens.homeTopGrabZone)
            .testTag(HOME_TOP_GRAB_ZONE_TAG)
            .pointerInput(Unit) { detectTapGestures(onLongPress = { currentOnLongPress() }) }
    )
}
