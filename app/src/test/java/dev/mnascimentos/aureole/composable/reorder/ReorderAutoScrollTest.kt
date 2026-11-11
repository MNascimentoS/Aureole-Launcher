package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val LONG_PRESS_MS = 1000L
private const val HOLD_MS = 3000L
private const val DRAG_STEPS = 10
private const val VIEWPORT_DP = 200
private const val ITEM_DP = 80

/** Holding a dragged item near an edge of the viewport scrolls the list and keeps reordering it. */
@RunWith(RobolectricTestRunner::class)
class ReorderAutoScrollTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val items = listOf("A", "B", "C", "D", "E", "F", "G", "H")
    private var order: List<String> = items
    private var moveEnds = 0
    private lateinit var scrollState: ScrollState

    private fun params(onItems: (List<String>) -> Unit, current: List<String>) = ReorderableParams(
        items = current,
        key = { it },
        actions = ReorderableActions(
            onMove = { from, to ->
                order = move(order, from, to)
                onItems(order)
            },
            onMoveEnd = { moveEnds++ }
        ),
        spacing = 8.dp
    )

    private fun setRow(isLeftHanded: Boolean = false) {
        composeTestRule.setContent {
            var current by remember { mutableStateOf(items) }
            scrollState = rememberScrollState()
            AureoleLauncherTheme {
                LeftHandMirror(isLeftHanded = isLeftHanded) {
                    ReorderableRow(
                        params = params({ current = it }, current),
                        modifier = Modifier.width(VIEWPORT_DP.dp),
                        scrollState = scrollState
                    ) { item -> Text(item, Modifier.size(ITEM_DP.dp)) }
                }
            }
        }
    }

    private fun setColumn() {
        composeTestRule.setContent {
            var current by remember { mutableStateOf(items) }
            scrollState = rememberScrollState()
            val autoScroll = rememberReorderAutoScroll(scrollState)
            AureoleLauncherTheme {
                Column(
                    Modifier
                        .height(VIEWPORT_DP.dp)
                        .reorderAutoScrollViewport(autoScroll)
                        .verticalScroll(scrollState)
                ) {
                    ReorderableColumn(params = params({ current = it }, current), autoScroll = autoScroll) { item ->
                        Text(item, Modifier.size(ITEM_DP.dp))
                    }
                }
            }
        }
    }

    /** Long-presses "A" and drags it to [target] (dp, relative to the top left corner of "A"), then holds it. */
    private fun holdAt(target: (Float) -> Offset) {
        composeTestRule.onNodeWithText("A").onParent().performTouchInput {
            down(center)
            advanceEventTime(LONG_PRESS_MS)
            val step = (target(density) - center) / DRAG_STEPS.toFloat()
            repeat(DRAG_STEPS) { moveBy(step) }
        }
        composeTestRule.mainClock.advanceTimeBy(HOLD_MS)
    }

    private fun lift() {
        composeTestRule.onNodeWithText("A").onParent().performTouchInput { up() }
        composeTestRule.mainClock.advanceTimeBy(LONG_PRESS_MS)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `holding an item at the end edge of a row scrolls it and moves the item past the viewport`() {
        setRow()

        holdAt { density -> Offset((VIEWPORT_DP - 4) * density, ITEM_DP / 2 * density) }
        lift()

        assertTrue(scrollState.value > 0)
        assertEquals(scrollState.maxValue, scrollState.value)
        assertTrue(order.indexOf("A") > 2)
        assertEquals(1, moveEnds)
    }

    @Test
    fun `a mirrored row scrolls towards its end on the left`() {
        setRow(isLeftHanded = true)

        holdAt { density -> Offset((ITEM_DP - VIEWPORT_DP + 4) * density, ITEM_DP / 2 * density) }
        lift()

        assertTrue(scrollState.value > 0)
        assertTrue(order.indexOf("A") > 2)
    }

    @Test
    fun `the row stops scrolling once the finger leaves the edge band`() {
        setRow()

        holdAt { density -> Offset((VIEWPORT_DP - 30) * density, ITEM_DP / 2 * density) }
        composeTestRule.onNodeWithText("A").onParent().performTouchInput {
            repeat(DRAG_STEPS) { moveBy(Offset(-VIEWPORT_DP / 2 * density / DRAG_STEPS, 0f)) }
        }
        composeTestRule.mainClock.advanceTimeBy(LONG_PRESS_MS)
        val stopped = scrollState.value
        composeTestRule.mainClock.advanceTimeBy(HOLD_MS)
        val after = scrollState.value
        lift()

        assertTrue(stopped > 0)
        assertEquals(stopped, after)
    }

    @Test
    fun `a dragged item in the middle of a row does not scroll it`() {
        setRow()

        holdAt { density -> Offset(VIEWPORT_DP / 2 * density, ITEM_DP / 2 * density) }
        lift()

        assertEquals(0, scrollState.value)
    }

    @Test
    fun `a column with an auto scroll scrolls its container at the bottom edge`() {
        setColumn()

        holdAt { density -> Offset(ITEM_DP / 2 * density, (VIEWPORT_DP - 4) * density) }
        lift()

        assertTrue(scrollState.value > 0)
        assertTrue(order.indexOf("A") > 2)
    }
}
