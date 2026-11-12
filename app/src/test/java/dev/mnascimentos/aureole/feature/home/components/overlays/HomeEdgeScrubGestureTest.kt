package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.EDGE_HOLD_MS
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberBounds
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterCenterY
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterIndexAt
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBoundsFor
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val TAG = "home"
private const val HOLD_MARGIN_MS = 60L
private const val EDGE_X_PX = 4f
private const val LETTER_COUNT = 26
private const val P_INDEX = 15

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class HomeEdgeScrubGestureTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var drawerOpenCount = 0
    private var shadeOpened = false
    private val touchYs = mutableListOf<Float>()
    private val scrubStates = mutableListOf<Boolean>()

    private fun setHome(isLeftHanded: Boolean = false, isDisabled: Boolean = false) {
        composeTestRule.setContent {
            val density = LocalDensity.current
            var widthPx by remember { mutableFloatStateOf(0f) }
            var heightPx by remember { mutableFloatStateOf(0f) }
            val params = HomeDragParams(
                isLeftHandedMode = isLeftHanded,
                isAlphabetScrubberDisabled = isDisabled,
                screenHeightPx = heightPx,
                screenWidthPx = widthPx,
                isAllAppsDrawerOpen = false,
                density = density,
                onAllAppsDrawerOpen = { drawerOpenCount++ },
                onExpandNotificationShade = { shadeOpened = true },
                onExternalTouchYChange = { touchYs += it },
                onEdgeScrubChange = { scrubStates += it }
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        widthPx = it.size.width.toFloat()
                        heightPx = it.size.height.toFloat()
                    }
                    .testTag(TAG)
                    .homeDragGestures(params)
            )
        }
    }

    private fun bounds(heightPx: Float): ScrubberBounds = scrubberBoundsFor(heightPx, composeTestRule.density)

    private fun letterY(heightPx: Float, index: Int): Float {
        val bounds = bounds(heightPx)
        return bounds.topPx + letterCenterY(index, bounds.heightPx, LETTER_COUNT)
    }

    private fun holdDown(position: (width: Float, height: Float) -> Offset) {
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithTag(TAG).performTouchInput { down(position(width.toFloat(), height.toFloat())) }
        composeTestRule.mainClock.advanceTimeBy(EDGE_HOLD_MS + HOLD_MARGIN_MS)
    }

    @Test
    fun `holding on the alphabet edge at P opens the drawer with the finger on P`() {
        setHome()
        var heightPx = 0f

        holdDown { width, height ->
            heightPx = height
            Offset(width - EDGE_X_PX, letterY(height, P_INDEX))
        }

        assertEquals(1, drawerOpenCount)
        assertEquals(listOf(true), scrubStates)
        val bounds = bounds(heightPx)
        assertEquals(P_INDEX, letterIndexAt(touchYs.last(), bounds.heightPx, LETTER_COUNT))
    }

    @Test
    fun `sliding after the hold follows the finger from A to Z and lifting ends the scrub`() {
        setHome()
        var heightPx = 0f
        holdDown { width, height ->
            heightPx = height
            Offset(width - EDGE_X_PX, letterY(height, P_INDEX))
        }
        val bounds = bounds(heightPx)

        composeTestRule.onNodeWithTag(TAG).performTouchInput { moveTo(Offset(width - EDGE_X_PX, bounds.topPx)) }
        assertEquals(0, letterIndexAt(touchYs.last(), bounds.heightPx, LETTER_COUNT))
        composeTestRule.onNodeWithTag(TAG).performTouchInput { moveTo(Offset(width - EDGE_X_PX, bounds.bottomPx)) }
        assertEquals(LETTER_COUNT - 1, letterIndexAt(touchYs.last(), bounds.heightPx, LETTER_COUNT))
        composeTestRule.onNodeWithTag(TAG).performTouchInput { up() }
        composeTestRule.mainClock.advanceTimeBy(HOLD_MARGIN_MS)

        assertEquals(-1f, touchYs.last(), 0f)
        assertEquals(listOf(true, false), scrubStates)
        assertEquals(1, drawerOpenCount)
        assertFalse(shadeOpened)
    }

    @Test
    fun `a quick tap on the alphabet edge does nothing`() {
        setHome()

        composeTestRule.onNodeWithTag(TAG).performTouchInput {
            val y = letterY(height.toFloat(), P_INDEX)
            down(Offset(width - EDGE_X_PX, y))
            up()
        }
        composeTestRule.mainClock.advanceTimeBy(EDGE_HOLD_MS + HOLD_MARGIN_MS)

        assertEquals(0, drawerOpenCount)
        assertTrue(touchYs.isEmpty())
        assertTrue(scrubStates.isEmpty())
    }

    @Test
    fun `holding on the edge above the alphabet does nothing`() {
        setHome()

        holdDown { width, height -> Offset(width - EDGE_X_PX, bounds(height).topPx / 2f) }

        assertEquals(0, drawerOpenCount)
        assertTrue(scrubStates.isEmpty())
    }

    @Test
    fun `holding inside the screen at the alphabet height does nothing`() {
        setHome()

        holdDown { width, height -> Offset(width / 2f, letterY(height, P_INDEX)) }

        assertEquals(0, drawerOpenCount)
        assertTrue(scrubStates.isEmpty())
    }

    @Test
    fun `holding on the right edge in left handed mode does nothing`() {
        setHome(isLeftHanded = true)

        holdDown { width, height -> Offset(width - EDGE_X_PX, letterY(height, P_INDEX)) }

        assertEquals(0, drawerOpenCount)
    }

    @Test
    fun `holding on the left edge in left handed mode opens the drawer on the letter`() {
        setHome(isLeftHanded = true)
        var heightPx = 0f

        holdDown { _, height ->
            heightPx = height
            Offset(EDGE_X_PX, letterY(height, P_INDEX))
        }

        assertEquals(1, drawerOpenCount)
        assertEquals(P_INDEX, letterIndexAt(touchYs.last(), bounds(heightPx).heightPx, LETTER_COUNT))
    }

    @Test
    fun `holding on the edge with the alphabet disabled does nothing`() {
        setHome(isDisabled = true)

        holdDown { width, height -> Offset(width - EDGE_X_PX, letterY(height, P_INDEX)) }

        assertEquals(0, drawerOpenCount)
    }

    @Test
    fun `a vertical swipe starting on the alphabet edge scrubs instead of opening the shade`() {
        setHome()

        composeTestRule.onNodeWithTag(TAG).performTouchInput {
            down(Offset(width - EDGE_X_PX, letterY(height.toFloat(), P_INDEX)))
            repeat(4) { moveBy(Offset(0f, 60f)) }
            up()
        }

        assertFalse(shadeOpened)
        assertEquals(1, drawerOpenCount)
        assertEquals(listOf(true, false), scrubStates)
    }

    @Test
    fun `a vertical swipe on the edge above the alphabet keeps the home gesture`() {
        setHome()

        composeTestRule.onNodeWithTag(TAG).performTouchInput {
            down(Offset(width - EDGE_X_PX, bounds(height.toFloat()).topPx / 4f))
            repeat(4) { moveBy(Offset(0f, 60f)) }
            up()
        }

        assertTrue(shadeOpened)
        assertTrue(scrubStates.isEmpty())
    }
}
