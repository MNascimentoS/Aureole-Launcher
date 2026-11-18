package dev.mnascimentos.aureole.core.designsystem.model

import org.junit.Assert.assertEquals
import org.junit.Test

class PreviewFitTest {

    private val gap = 40f

    @Test
    fun `when the content fits below the preview, the preview stays`() {
        assertEquals(PreviewFit(PreviewBehavior.STAYS, 1f, 1f), previewFit(previewHeight = 300f, room = 340f, gap = gap))
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
    fun `when the content needs the room, the preview fades out at its smallest`() {
        val half = previewFit(previewHeight = 300f, room = gap + 75f, gap = gap)
        val gone = previewFit(previewHeight = 300f, room = gap, gap = gap)

        assertEquals(PreviewBehavior.HIDES, half.behavior)
        assertEquals(PREVIEW_MIN_SCALE, half.scale, 0f)
        assertEquals(0.5f, half.alpha, 0.001f)
        assertEquals(0f, gone.alpha, 0f)
        assertEquals(0f, previewFit(previewHeight = 300f, room = 0f, gap = gap).alpha, 0f)
    }

    @Test
    fun `the fade starts where the shrink ends`() {
        val atMin = previewFit(previewHeight = 300f, room = gap + 300f * PREVIEW_MIN_SCALE, gap = gap)

        assertEquals(PreviewBehavior.SHRINKS, atMin.behavior)
        assertEquals(1f, atMin.alpha, 0f)
    }

    @Test
    fun `a preview with no height always stays`() {
        assertEquals(PreviewBehavior.STAYS, previewFit(previewHeight = 0f, room = 0f, gap = gap).behavior)
    }
}
