package dev.mnascimentos.aureole.core.data.model

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperGeometryTest {

    private val portrait = Size(1000f, 2000f)
    private val landscape = Size(2000f, 1000f)
    private val delta = 0.0001f

    @Test
    fun `cover zoom of a photo with the view aspect is the plain ratio`() {
        assertEquals(1f, WallpaperGeometry.minCoverZoom(Size(1000f, 2000f), portrait, 0), delta)
        assertEquals(0.5f, WallpaperGeometry.minCoverZoom(Size(2000f, 4000f), portrait, 0), delta)
    }

    @Test
    fun `cover zoom of a horizontal photo in a portrait view is set by the height`() {
        assertEquals(2f, WallpaperGeometry.minCoverZoom(Size(2000f, 1000f), portrait, 0), delta)
    }

    @Test
    fun `cover zoom of a vertical photo in a landscape view is set by the width`() {
        assertEquals(1f, WallpaperGeometry.minCoverZoom(Size(2000f, 4000f), landscape, 0), delta)
    }

    @Test
    fun `quarter turns swap the photo sides before covering`() {
        val horizontal = Size(2000f, 1000f)

        assertEquals(1f, WallpaperGeometry.minCoverZoom(horizontal, portrait, 90), delta)
        assertEquals(1f, WallpaperGeometry.minCoverZoom(horizontal, portrait, 270), delta)
        assertEquals(2f, WallpaperGeometry.minCoverZoom(horizontal, portrait, 180), delta)
    }

    @Test
    fun `empty photo does not divide by zero`() {
        assertEquals(1f, WallpaperGeometry.minCoverZoom(Size(0f, 0f), portrait, 0), 0f)
        val rect = WallpaperGeometry.visibleRect(Size(0f, 0f), portrait, WallpaperAdjustments.DEFAULT)
        assertEquals(1f, rect.width, 0f)
        assertEquals(Offset(0.5f, 0.5f), WallpaperGeometry.clampFocus(Size(0f, 0f), portrait, WallpaperAdjustments()))
    }

    @Test
    fun `default framing of a horizontal photo shows the center band in a portrait view`() {
        val rect = WallpaperGeometry.visibleRect(Size(2000f, 1000f), portrait, WallpaperAdjustments.DEFAULT)

        assertEquals(0.375f, rect.left, delta)
        assertEquals(0.625f, rect.right, delta)
        assertEquals(0f, rect.top, delta)
        assertEquals(1f, rect.bottom, delta)
    }

    @Test
    fun `default framing of a vertical photo shows the center band in a landscape view`() {
        val rect = WallpaperGeometry.visibleRect(Size(1000f, 2000f), landscape, WallpaperAdjustments.DEFAULT)

        assertEquals(0f, rect.left, delta)
        assertEquals(1f, rect.right, delta)
        assertEquals(0.375f, rect.top, delta)
        assertEquals(0.625f, rect.bottom, delta)
    }

    @Test
    fun `rotating a horizontal photo by 90 makes it fill a portrait view`() {
        val rect = WallpaperGeometry.visibleRect(
            Size(2000f, 1000f),
            portrait,
            WallpaperAdjustments(rotation = 90)
        )

        assertEquals(0f, rect.left, delta)
        assertEquals(1f, rect.right, delta)
        assertEquals(0f, rect.top, delta)
        assertEquals(1f, rect.bottom, delta)
    }

    @Test
    fun `zoom shrinks the visible part and the focus centers it`() {
        val adjustments = WallpaperAdjustments(zoom = 2f, focusX = 0.4f, focusY = 0.6f)

        val rect = WallpaperGeometry.visibleRect(Size(1000f, 2000f), portrait, adjustments)

        assertEquals(0.5f, rect.width, delta)
        assertEquals(0.5f, rect.height, delta)
        assertEquals(0.4f, (rect.left + rect.right) / 2f, delta)
        assertEquals(0.6f, (rect.top + rect.bottom) / 2f, delta)
    }

    @Test
    fun `maximum zoom shows a fifth of the photo`() {
        val rect = WallpaperGeometry.visibleRect(Size(1000f, 2000f), portrait, WallpaperAdjustments(zoom = 5f))

        assertEquals(0.2f, rect.width, delta)
        assertEquals(0.2f, rect.height, delta)
    }

    @Test
    fun `focus on the edges is pulled in so the rect never leaves the photo`() {
        val corner = WallpaperAdjustments(zoom = 2f, focusX = 0f, focusY = 1f)
        val rect = WallpaperGeometry.visibleRect(Size(1000f, 2000f), portrait, corner)

        assertEquals(0f, rect.left, delta)
        assertEquals(1f, rect.bottom, delta)
        assertEquals(Offset(0.25f, 0.75f), WallpaperGeometry.clampFocus(Size(1000f, 2000f), portrait, corner))
    }

    @Test
    fun `focus of an axis that cannot move stays centered`() {
        val adjustments = WallpaperAdjustments(focusX = 0f, focusY = 0f)

        val focus = WallpaperGeometry.clampFocus(Size(2000f, 1000f), portrait, adjustments)

        assertEquals(0.125f, focus.x, delta)
        assertEquals(0.5f, focus.y, delta)
    }

    @Test
    fun `saved focus is kept when the screen turns to landscape`() {
        val adjustments = WallpaperAdjustments(zoom = 1f, focusX = 0.5f, focusY = 0.3f)

        val rect = WallpaperGeometry.visibleRect(Size(1000f, 2000f), landscape, adjustments)

        assertEquals(0.3f, (rect.top + rect.bottom) / 2f, delta)
        assertTrue(rect.top >= 0f && rect.bottom <= 1f)
    }

    @Test
    fun `rect stays inside the photo for many rotations zooms and focuses`() {
        val images = listOf(Size(1000f, 2000f), Size(2000f, 1000f), Size(1500f, 1500f))
        val views = listOf(portrait, landscape)
        val focuses = listOf(0f, 0.5f, 1f)
        val cases = images.flatMap { image -> views.map { image to it } }.flatMap { (image, view) ->
            listOf(0, 90, 180, 270).flatMap { rotation ->
                listOf(1f, 1.7f, 5f).flatMap { zoom ->
                    focuses.flatMap { fx ->
                        focuses.map { fy -> Triple(image, view, WallpaperAdjustments(zoom, fx, fy, rotation)) }
                    }
                }
            }
        }
        cases.forEach { (image, view, adjustments) ->
            val rect = WallpaperGeometry.visibleRect(image, view, adjustments)
            assertTrue(rect.left >= -delta && rect.top >= -delta)
            assertTrue(rect.right <= 1f + delta && rect.bottom <= 1f + delta)
        }
    }
}
