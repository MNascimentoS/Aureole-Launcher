package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.runtime.MonotonicFrameClock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

private const val FRAME_NANOS = 16_000_000L
private val BOUNDS = SheetBounds(restMaxPx = 600f, expandedPx = 900f)

@OptIn(ExperimentalCoroutinesApi::class)
class OverlaySheetMotionTest {

    private val expandedChanges = mutableListOf<Boolean>()
    private var dismissCount = 0
    private var tickCount = 0
    private val events = OverlaySheetEvents(
        onExpandedChanged = { expandedChanges += it },
        onDismiss = { dismissCount++ },
        onTick = { tickCount++ }
    )

    private fun controller(motion: OverlaySheetMotion, isExpanded: Boolean = false, scope: TestScope = TestScope()) =
        OverlaySheetController(motion, BOUNDS, isExpanded, events, scope, density = 1f)

    @Test
    fun `when rest is not measured yet, should fall back to the rest maximum`() {
        val motion = OverlaySheetMotion()
        assertEquals(600f, motion.restOrFallback(BOUNDS))
        motion.restHeight = 400f
        assertEquals(400f, motion.restOrFallback(BOUNDS))
        motion.restHeight = 700f
        assertEquals(600f, motion.restOrFallback(BOUNDS))
    }

    @Test
    fun `when settled, should hug at rest and fix the height when expanded`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        assertNull(motion.fixedHeight(isExpanded = false, bounds = BOUNDS))
        assertEquals(900f, motion.fixedHeight(isExpanded = true, bounds = BOUNDS))
        assertEquals(400f, motion.currentHeight(isExpanded = false, bounds = BOUNDS))
        assertEquals(0f, motion.translation(BOUNDS))
    }

    @Test
    fun `when dragged below rest, should keep rest height and slide down`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        motion.dragBy(delta = 100f, isExpanded = false, bounds = BOUNDS)
        assertEquals(300f, motion.pinnedHeight)
        assertEquals(400f, motion.fixedHeight(isExpanded = false, bounds = BOUNDS))
        assertEquals(100f, motion.translation(BOUNDS))
    }

    @Test
    fun `when dragged past the edges, should clamp between zero and expanded`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        motion.dragBy(delta = -2000f, isExpanded = false, bounds = BOUNDS)
        assertEquals(900f, motion.pinnedHeight)
        motion.dragBy(delta = 5000f, isExpanded = false, bounds = BOUNDS)
        assertEquals(0f, motion.pinnedHeight)
    }

    @Test
    fun `when dragging crosses rest or expanded, should tick once per detent`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion)
        controller.onDrag(-100f)
        assertEquals(0, tickCount)
        controller.onDrag(-450f)
        assertEquals(1, tickCount)
        controller.onDrag(500f)
        assertEquals(2, tickCount)
    }

    @Test
    fun `when released low, should dismiss without changing height state`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion)
        controller.onDrag(200f)
        assertTrue(controller.isHeld)
        controller.onRelease(velocityPx = 0f)
        assertEquals(1, dismissCount)
        assertTrue(expandedChanges.isEmpty())
    }

    @Test
    fun `when released near expanded, should expand and tick`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion)
        controller.onDrag(-450f)
        tickCount = 0
        controller.onRelease(velocityPx = 0f)
        assertEquals(listOf(true), expandedChanges)
        assertTrue(controller.isExpanded)
        assertEquals(1, tickCount)
        assertFalse(controller.isHeld)
    }

    @Test
    fun `when flung down from expanded, should collapse to rest`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion, isExpanded = true)
        controller.onDrag(20f)
        controller.onRelease(velocityPx = 2000f)
        assertEquals(listOf(false), expandedChanges)
    }

    @Test
    fun `when density is converted, should compare velocity in dp per second`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = OverlaySheetController(motion, BOUNDS, false, events, TestScope(), density = 4f)
        controller.onDrag(-10f)
        controller.onRelease(velocityPx = -1000f)
        assertEquals(listOf(false), expandedChanges)
    }

    @Test
    fun `when toggled, should switch between rest and expanded`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion)
        controller.toggle()
        controller.toggle()
        assertEquals(listOf(true, false), expandedChanges)
        assertEquals(2, tickCount)
    }

    @Test
    fun `when nested scroll moves the sheet, should report the consumed part`() {
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion)
        assertEquals(-100f, controller.scrollBy(-100f))
        assertEquals(500f, controller.currentHeight())
        assertEquals(-400f, controller.scrollBy(-1000f))
        assertEquals(900f, controller.expandedHeight())
        assertEquals(900f, controller.scrollBy(2000f))
    }

    @Test
    fun `when drag starts during a settle, should cancel the animation`() {
        val scope = TestScope(StandardTestDispatcher())
        val motion = OverlaySheetMotion().apply { restHeight = 400f }
        val controller = controller(motion, scope = scope)
        controller.toggle()
        assertTrue(motion.settleJob?.isActive == true)
        controller.onDragStarted()
        assertTrue(motion.settleJob?.isCancelled == true)
    }

    @Test
    fun `when height animation ends, should unpin the sheet`() = runTest {
        val motion = OverlaySheetMotion()
        var frame = 0L
        val clock = object : MonotonicFrameClock {
            override suspend fun <R> withFrameNanos(onFrame: (Long) -> R): R {
                frame += FRAME_NANOS
                return onFrame(frame)
            }
        }
        withContext(clock) { motion.animateHeight(from = 400f, to = 900f) }
        assertNull(motion.pinnedHeight)
    }
}
