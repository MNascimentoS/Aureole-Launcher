package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PreviewFitTest {

    private val gap = 40f

    @Test
    fun `when the content fits below the preview, the preview stays`() {
        assertEquals(
            PreviewFit(PreviewBehavior.STAYS, 1f, 1f),
            previewFit(previewHeight = 300f, room = 340f, gap = gap)
        )
        assertEquals(PreviewBehavior.STAYS, previewFit(previewHeight = 300f, room = 900f, gap = gap).behavior)
    }

    @Test
    fun `when the sheet would touch it, the preview shrinks and keeps the gap`() {
        val fit = previewFit(previewHeight = 300f, room = 280f, gap = gap)

        assertEquals(PreviewBehavior.SHRINKS, fit.behavior)
        assertEquals(0.8f, fit.scale, 0.001f)
        assertEquals(1f, fit.alpha, 0f)
        // Its bottom stays gap above the sheet: what it draws is room - gap tall.
        assertEquals(280f - gap, 300f * fit.scale, 0.01f)
    }

    @Test
    fun `when the scaled top does not fit, the preview has alpha 0`() {
        listOf(gap + 100f, gap + 75f, gap, 0f, -50f).forEach { room ->
            val fit = previewFit(previewHeight = 300f, room = room, gap = gap)
            assertEquals(0f, fit.alpha, 0f)
        }
        assertEquals(PreviewBehavior.HIDES, previewFit(previewHeight = 300f, room = gap + 100f, gap = gap).behavior)
        assertEquals(PREVIEW_MIN_SCALE, previewFit(300f, gap, gap).scale, 0f)
    }

    @Test
    fun `the preview fades while it shrinks to its smallest, never visible when cut`() {
        val mid = previewFit(previewHeight = 300f, room = gap + 300f * 0.625f, gap = gap)

        assertEquals(PreviewBehavior.SHRINKS, mid.behavior)
        assertEquals(0.5f, mid.alpha, 0.001f)
        // Whatever the room, visible means the scaled preview fits under the top padding.
        (0..400).forEach { room ->
            val fit = previewFit(previewHeight = 300f, room = room.toFloat(), gap = gap)
            if (fit.alpha > 0f) assertTrue(300f * fit.scale <= room - gap + 0.01f)
        }
    }

    @Test
    fun `alpha and scale are continuous at the boundaries`() {
        val h = 300f
        fun at(room: Float) = previewFit(h, room, gap)
        val fadeRoom = gap + h * PREVIEW_FADE_START_SCALE
        val minRoom = gap + h * PREVIEW_MIN_SCALE
        listOf(h + gap, fadeRoom, minRoom).forEach { edge ->
            val below = at(edge - 0.01f)
            val above = at(edge + 0.01f)
            assertEquals(above.alpha, below.alpha, 0.001f)
            assertEquals(above.scale, below.scale, 0.001f)
        }
        assertEquals(1f, at(fadeRoom).alpha, 0.001f)
        assertEquals(0f, at(minRoom).alpha, 0.001f)
    }

    @Test
    fun `a preview with no height always stays`() {
        assertEquals(PreviewBehavior.STAYS, previewFit(previewHeight = 0f, room = 0f, gap = gap).behavior)
    }
}
