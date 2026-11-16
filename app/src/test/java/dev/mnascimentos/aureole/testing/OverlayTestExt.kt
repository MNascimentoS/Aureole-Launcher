package dev.mnascimentos.aureole.testing

import android.view.View
import androidx.activity.findViewTreeOnBackPressedDispatcherOwner
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.ComposeTestRule

private const val OVERLAY_CLOSE_ANIMATION_MS = 500L

/** Enough time on a manual clock for an edit overlay to slide in to rest. */
const val SHEET_SLIDE_IN_MS = 600L

/**
 * Closes the open edit overlay the way a user does without a button: the back key. The sheet saves what changed
 * while it closes, so the callbacks of the sheet have run when this returns.
 */
fun ComposeTestRule.closeOverlayWithBack() {
    waitForIdle()
    // The Android root of a composition is the compose view itself. A screen with a dialog window has more
    // than one, the topmost last.
    val owner = onAllNodes(isRoot()).fetchSemanticsNodes().asReversed()
        .firstNotNullOfOrNull { (it.root as View).findViewTreeOnBackPressedDispatcherOwner() }
    val dispatcher = checkNotNull(owner).onBackPressedDispatcher
    runOnUiThread { dispatcher.onBackPressed() }
    waitForIdle()
    mainClock.advanceTimeBy(OVERLAY_CLOSE_ANIMATION_MS)
    waitForIdle()
}
