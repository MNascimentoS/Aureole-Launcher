package dev.mnascimentos.aureole.composable.reorder

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onParent
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.composable.reorder.model.LocalReorderHandle
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

/** A list with `dragHandleOnly`: only the handle of an item starts its drag. */
@OptIn(ExperimentalTestApi::class)
@RunWith(RobolectricTestRunner::class)
class ReorderableHandleTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private var order: List<String> = emptyList()
    private var moveEnds = 0

    private fun setList(dragHandleOnly: Boolean) {
        composeTestRule.setContent {
            var items by remember { mutableStateOf(listOf("A", "B", "C")) }
            AureoleLauncherTheme {
                ReorderableColumn(
                    params = ReorderableParams(
                        items = items,
                        key = { it },
                        actions = ReorderableActions(
                            onMove = { from, to ->
                                items = move(items, from, to)
                                order = items
                            },
                            onMoveEnd = { moveEnds++ }
                        ),
                        spacing = 8.dp,
                        dragHandleOnly = dragHandleOnly
                    )
                ) { item ->
                    Row {
                        Text(item, Modifier.size(80.dp))
                        Box(
                            Modifier
                                .size(80.dp)
                                .then(LocalReorderHandle.current.gestureModifier)
                                .semantics { contentDescription = "Handle" }
                        )
                    }
                }
            }
        }
    }

    private fun dragFrom(target: () -> SemanticsNodeInteraction) {
        target().performTouchInput {
            val distance = Offset(0f, height * 1.5f)
            down(center)
            advanceEventTime(LONG_PRESS_MS)
            repeat(DRAG_STEPS) { moveBy(distance / DRAG_STEPS.toFloat()) }
            up()
        }
        composeTestRule.mainClock.advanceTimeBy(LONG_PRESS_MS)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `holding and dragging the handle moves the item`() {
        setList(dragHandleOnly = true)

        dragFrom { composeTestRule.onAllNodesWithContentDescription("Handle")[0] }

        assertEquals(listOf("B", "A", "C"), order)
        assertEquals(1, moveEnds)
    }

    @Test
    fun `dragging the item outside its handle does nothing`() {
        setList(dragHandleOnly = true)

        dragFrom { composeTestRule.onNodeWithText("A") }

        assertEquals(emptyList<String>(), order)
        assertEquals(0, moveEnds)
    }

    @Test
    fun `without dragHandleOnly the whole item still drags`() {
        setList(dragHandleOnly = false)

        dragFrom { composeTestRule.onNodeWithText("A").onParent() }

        assertEquals(listOf("B", "A", "C"), order)
    }
}
