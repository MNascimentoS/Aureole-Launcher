package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.runtime.saveable.SaverScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.data.model.WallpaperGeometry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

private const val EPS = 1e-4f

/**
 * A landscape photo (2000 x 1000) on a portrait screen (1000 x 2000): at 1x it is scaled by 2, so the screen shows a
 * quarter of its width (0.25) and all of its height.
 */
private val FRAME = WallpaperEditorFrame(imageSize = Size(2000f, 1000f), viewSize = Size(1000f, 2000f))

class WallpaperEditorStateTest {

    private val start = WallpaperEditorState(initial = WallpaperAdjustments.DEFAULT)

    /** Fraction of the rotated photo under [point] of the view. */
    private fun photoPointUnder(adjustments: WallpaperAdjustments, point: Offset): Offset {
        val rect = WallpaperGeometry.visibleRect(FRAME.imageSize, FRAME.viewSize, adjustments)
        return Offset(
            rect.left + rect.width * point.x / FRAME.viewSize.width,
            rect.top + rect.height * point.y / FRAME.viewSize.height
        )
    }

    @Test
    fun `pinch zooms around the fingers and keeps the photo point under them`() {
        val point = Offset(800f, 600f)
        val before = photoPointUnder(start.current, point)

        val zoomed = start.pinched(FRAME, zoomBy = 2f, centroid = point)

        assertEquals(2f, zoomed.current.zoom, EPS)
        val after = photoPointUnder(zoomed.current, point)
        assertEquals(before.x, after.x, EPS)
        assertEquals(before.y, after.y, EPS)
    }

    @Test
    fun `pinch stops at 1x and at 5x, and past 5x only stretches up to a limit`() {
        assertEquals(1f, start.pinched(FRAME, 0.2f, FRAME.viewCenter).current.zoom, EPS)

        val past = start.pinched(FRAME, 7f, FRAME.viewCenter)
        assertEquals(5f, past.current.zoom, EPS)
        assertEquals(1.4f, past.overshoot, EPS)
        assertEquals(1.5f, past.pinched(FRAME, 4f, FRAME.viewCenter).overshoot, EPS)

        // Pinching back in first takes the stretch away, then the zoom.
        val back = past.pinched(FRAME, 0.5f, FRAME.viewCenter)
        assertEquals(3.5f, back.current.zoom, EPS)
        assertEquals(1f, back.overshoot, EPS)
        assertEquals(1f, past.settled().overshoot, EPS)
    }

    @Test
    fun `overshoot scale shows only part of the stretch`() {
        assertEquals(1f, overshootScale(1f), EPS)
        assertEquals(1.15f, overshootScale(1.5f), EPS)
        assertEquals(1.15f, overshootScale(3f), EPS)
    }

    @Test
    fun `drag moves the photo and stops at the edges`() {
        // The finger goes left: the view moves right on the photo (2 px of view per px of photo, 2000 px wide).
        val moved = start.dragged(FRAME, Offset(-400f, 0f))
        assertEquals(0.6f, moved.current.focusX, EPS)

        val farRight = start.dragged(FRAME, Offset(-100_000f, 0f))
        assertEquals(0.875f, farRight.current.focusX, EPS)
        val farLeft = start.dragged(FRAME, Offset(100_000f, 0f))
        assertEquals(0.125f, farLeft.current.focusX, EPS)
        // The whole height is in view at 1x: nothing to move vertically.
        assertEquals(0.5f, start.dragged(FRAME, Offset(0f, 5_000f)).current.focusY, EPS)
    }

    @Test
    fun `gestures do nothing before the photo size is known`() {
        val unknown = WallpaperEditorFrame(imageSize = Size.Unspecified, viewSize = FRAME.viewSize)
        assertFalse(unknown.isReady)
        assertEquals(start, start.pinched(unknown, 2f, Offset.Zero))
        assertEquals(start, start.dragged(unknown, Offset(10f, 10f)))
    }

    @Test
    fun `double tap switches between 1x and 2x at the point`() {
        val point = Offset(200f, 1000f)
        val inTarget = start.doubleTapTarget(FRAME, point)
        assertEquals(2f, inTarget.zoom, EPS)
        assertEquals(photoPointUnder(start.current, point).x, photoPointUnder(inTarget, point).x, EPS)

        val zoomedIn = start.withAdjustments(inTarget)
        assertEquals(1f, zoomedIn.doubleTapTarget(FRAME, point).zoom, EPS)
    }

    @Test
    fun `rotate turns a quarter clockwise keeping the middle and four turns come back`() {
        val offCenter = start.pinched(FRAME, 2f, FRAME.viewCenter).dragged(FRAME, Offset(-200f, 0f))
        val turned = offCenter.rotated(FRAME)
        assertEquals(90, turned.current.rotation)
        // The point that was in the middle, (x, 0.5), is now at (0.5, x) of the turned photo.
        assertEquals(0.5f, turned.current.focusX, EPS)
        assertEquals(offCenter.current.focusX, turned.current.focusY, EPS)

        val fullTurn = start.rotated(FRAME).rotated(FRAME).rotated(FRAME).rotated(FRAME)
        assertEquals(start.current, fullTurn.current)
    }

    @Test
    fun `center and reset`() {
        val moved = start.pinched(FRAME, 3f, Offset(100f, 100f)).withDim(0.5f)
        val centered = moved.centeredAdjustments()
        assertEquals(0.5f, centered.focusX, EPS)
        assertEquals(0.5f, centered.focusY, EPS)
        assertEquals(moved.current.zoom, centered.zoom, EPS)

        assertEquals(WallpaperAdjustments.DEFAULT, moved.resetAdjustments())
    }

    @Test
    fun `isChanged compares with the opening adjustments, not the tab`() {
        assertFalse(start.isChanged)
        assertFalse(start.copy(tab = WallpaperEditorTab.BLUR).isChanged)
        assertTrue(start.withDim(0.5f).isChanged)
        assertTrue(start.withBlur(4f).isChanged)
        assertFalse(start.withDim(0.5f).withAdjustments(start.initial).isChanged)
    }

    @Test
    fun `sliders round to whole steps and stay in range`() {
        assertEquals(0.33f, start.withDim(0.333f).current.dim, EPS)
        assertEquals(0.7f, start.withDim(0.95f).current.dim, EPS)
        assertEquals(0f, start.withDim(-1f).current.dim, EPS)
        assertEquals(33, start.withDim(0.333f).dimPercent)
        assertEquals(13f, start.withBlur(12.6f).current.blurDp, EPS)
        assertEquals(25f, start.withBlur(40f).current.blurDp, EPS)
        assertEquals(13, start.withBlur(12.6f).blurDpValue)
        assertEquals(69, editorSliderSteps(0.7f, 0.01f))
        assertEquals(24, editorSliderSteps(25f, 1f))
    }

    @Test
    fun `TalkBack zoom and moves`() {
        val zoomedIn = start.zoomedBy(FRAME, EDITOR_ZOOM_STEP)
        assertEquals(EDITOR_ZOOM_STEP, zoomedIn.current.zoom, EPS)
        assertEquals(1f, zoomedIn.zoomedBy(FRAME, 1f / EDITOR_ZOOM_STEP).current.zoom, EPS)

        // Moving the photo right shows more of its left side.
        assertTrue(start.moved(FRAME, EditorMove.RIGHT).current.focusX < 0.5f)
        assertTrue(start.moved(FRAME, EditorMove.LEFT).current.focusX > 0.5f)
        val tall = zoomedIn
        assertTrue(tall.moved(FRAME, EditorMove.DOWN).current.focusY < tall.current.focusY)
        assertTrue(tall.moved(FRAME, EditorMove.UP).current.focusY > tall.current.focusY)
    }

    @Test
    fun `spring frames go from one adjustment to the other`() {
        val to = WallpaperAdjustments(zoom = 3f, focusX = 0.2f, focusY = 0.4f, rotation = 90, dim = 0.5f, blurDp = 10f)
        val half = lerpAdjustments(WallpaperAdjustments.DEFAULT, to, 0.5f)
        assertEquals(2f, half.zoom, EPS)
        assertEquals(0.35f, half.focusX, EPS)
        assertEquals(90, half.rotation)
        assertEquals(5f, half.blurDp, EPS)
        assertEquals(to, lerpAdjustments(WallpaperAdjustments.DEFAULT, to, 1f))
    }

    @Test
    fun `the editor survives a rotation of the screen`() {
        val state = start.copy(tab = WallpaperEditorTab.DIM).withDim(0.5f).pinched(FRAME, 2f, FRAME.viewCenter)
        val scope = SaverScope { true }
        val saved = with(WallpaperEditorStateSaver) { scope.save(state) }
        val restored = WallpaperEditorStateSaver.restore(requireNotNull(saved))

        assertEquals(state.copy(overshoot = 1f), restored)
        assertEquals(WallpaperEditorTab.FRAME, WallpaperEditorStateSaver.restore("broken")?.tab)
    }

    @Test
    fun `tab labels`() {
        val labels = WallpaperEditorTab.entries.map { wallpaperEditorTabLabel(it) }
        assertEquals(3, labels.toSet().size)
    }
}
