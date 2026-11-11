package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performCustomAccessibilityActionWithLabel
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.mirror.LeftHandMirror
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableActions
import dev.mnascimentos.aureole.composable.reorder.model.ReorderableParams
import dev.mnascimentos.aureole.composable.reorder.model.move
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

private const val LONG_PRESS_MS = 1000L
private const val DRAG_STEPS = 10

@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class ReorderableListsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val moves = mutableListOf<Pair<Int, Int>>()
    private val removed = mutableListOf<Int>()
    private var moveEnds = 0
    private var order: List<String> = emptyList()

    private fun setList(isRow: Boolean, isLeftHanded: Boolean = false, withRemove: Boolean = true) {
        composeTestRule.setContent {
            var items by remember { mutableStateOf(listOf("A", "B", "C")) }
            val params = ReorderableParams(
                items = items,
                key = { it },
                actions = ReorderableActions(
                    onMove = { from, to ->
                        moves += from to to
                        items = move(items, from, to)
                        order = items
                    },
                    onMoveEnd = { moveEnds++ },
                    onRemove = if (withRemove) ({ index: Int -> removed += index }) else null
                ),
                spacing = 8.dp
            )
            AureoleLauncherTheme {
                LeftHandMirror(isLeftHanded = isLeftHanded) {
                    if (isRow) {
                        ReorderableRow(params = params, trailingContent = { Text("+") }) { item ->
                            Text(item, Modifier.size(80.dp))
                        }
                    } else {
                        ReorderableColumn(params = params) { item -> Text(item, Modifier.size(80.dp)) }
                    }
                }
            }
        }
    }

    private fun actionLabels(text: String): List<String> = composeTestRule.onNodeWithText(text).onParent()
        .fetchSemanticsNode().config.getOrNull(SemanticsActions.CustomActions).orEmpty().map { it.label }

    @Test
    fun `column items offer up and down only towards existing neighbours, plus remove`() {
        setList(isRow = false)

        assertEquals(listOf("Move down", "Remove"), actionLabels("A"))
        assertEquals(listOf("Move up", "Move down", "Remove"), actionLabels("B"))
        assertEquals(listOf("Move up", "Remove"), actionLabels("C"))
    }

    @Test
    fun `move down moves the item one place and ends the move`() {
        setList(isRow = false)

        composeTestRule.onNodeWithText("A").onParent().performCustomAccessibilityActionWithLabel("Move down")
        composeTestRule.waitForIdle()

        assertEquals(listOf(0 to 1), moves)
        assertEquals(listOf("B", "A", "C"), order)
        assertEquals(1, moveEnds)
    }

    @Test
    fun `row items move right towards the end in a left-to-right layout`() {
        setList(isRow = true)

        assertEquals(listOf("Move right", "Remove"), actionLabels("A"))
        composeTestRule.onNodeWithText("A").onParent().performCustomAccessibilityActionWithLabel("Move right")

        assertEquals(listOf(0 to 1), moves)
    }

    @Test
    fun `mirrored rows move right towards the start`() {
        setList(isRow = true, isLeftHanded = true)

        assertEquals(listOf("Move left", "Remove"), actionLabels("A"))
        composeTestRule.onNodeWithText("B").onParent().performCustomAccessibilityActionWithLabel("Move right")

        assertEquals(listOf(1 to 0), moves)
    }

    @Test
    fun `the remove badge and action report the item index`() {
        setList(isRow = true)

        composeTestRule.onAllNodesWithContentDescription("Remove")[1].performClick()
        composeTestRule.onNodeWithText("C").onParent().performCustomAccessibilityActionWithLabel("Remove")

        assertEquals(listOf(1, 2), removed)
    }

    @Test
    fun `without onRemove there is no badge nor remove action`() {
        setList(isRow = false, withRemove = false)

        assertEquals(0, composeTestRule.onAllNodesWithContentDescription("Remove").fetchSemanticsNodes().size)
        assertEquals(listOf("Move down"), actionLabels("A"))
    }

    @Test
    fun `holding and dragging an item down swaps it and ends the move once`() {
        setList(isRow = false)

        dragItem("A", Offset(0f, 1f))

        assertEquals(listOf("B", "A", "C"), order)
        assertEquals(1, moveEnds)
    }

    @Test
    fun `holding and dragging an item sideways in a row swaps it`() {
        setList(isRow = true)

        dragItem("A", Offset(1f, 0f))

        assertEquals(listOf("B", "A", "C"), order)
        assertEquals(1, moveEnds)
    }

    @Test
    fun `a hold without moving far enough changes nothing`() {
        setList(isRow = false)

        composeTestRule.onNodeWithText("A").onParent().performTouchInput {
            down(center)
            advanceEventTime(LONG_PRESS_MS)
            moveBy(Offset(0f, 5f))
            up()
        }
        composeTestRule.mainClock.advanceTimeBy(LONG_PRESS_MS)
        composeTestRule.waitForIdle()

        assertEquals(emptyList<Pair<Int, Int>>(), moves)
        assertEquals(0, moveEnds)
    }

    /** Long-presses [text] and drags it [direction] by one and a half items, in small steps. */
    private fun dragItem(text: String, direction: Offset) {
        composeTestRule.onNodeWithText(text).onParent().performTouchInput {
            val distance = Offset(width * direction.x, height * direction.y) * 1.5f
            down(center)
            advanceEventTime(LONG_PRESS_MS)
            repeat(DRAG_STEPS) { moveBy(distance / DRAG_STEPS.toFloat()) }
            up()
        }
        composeTestRule.mainClock.advanceTimeBy(LONG_PRESS_MS)
        composeTestRule.waitForIdle()
    }
}
