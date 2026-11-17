package dev.mnascimentos.aureole.core.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperAdjustmentsTest {

    @Test
    fun `default is the classic look`() {
        val default = WallpaperAdjustments.DEFAULT

        assertEquals(1f, default.zoom, 0f)
        assertEquals(0.5f, default.focusX, 0f)
        assertEquals(0.5f, default.focusY, 0f)
        assertEquals(0, default.rotation)
        assertEquals(0.3f, default.dim, 0f)
        assertEquals(0f, default.blurDp, 0f)
        assertTrue(default.isDefault)
        assertFalse(default.hasFraming)
    }

    @Test
    fun `coerced forces every value into its range`() {
        val wild = WallpaperAdjustments(zoom = 9f, focusX = -1f, focusY = 3f, rotation = 450, dim = 2f, blurDp = 99f)

        val coerced = wild.coerced()

        assertEquals(WallpaperAdjustments(5f, 0f, 1f, 90, 0.7f, 25f), coerced)
    }

    @Test
    fun `coerced raises values below the minimum and snaps rotation to a quarter turn`() {
        val coerced = WallpaperAdjustments(zoom = 0.2f, rotation = -90, dim = -1f, blurDp = -3f).coerced()

        assertEquals(1f, coerced.zoom, 0f)
        assertEquals(270, coerced.rotation)
        assertEquals(0f, coerced.dim, 0f)
        assertEquals(0f, coerced.blurDp, 0f)
        assertEquals(180, WallpaperAdjustments(rotation = 170).coerced().rotation)
    }

    @Test
    fun `json round trip keeps every value`() {
        val adjustments = WallpaperAdjustments(2.5f, 0.25f, 0.75f, 270, 0.55f, 12.5f)

        assertEquals(adjustments, WallpaperAdjustments.fromJson(adjustments.toJson()))
    }

    @Test
    fun `absent or broken json is the default`() {
        assertEquals(WallpaperAdjustments.DEFAULT, WallpaperAdjustments.fromJson(null))
        assertEquals(WallpaperAdjustments.DEFAULT, WallpaperAdjustments.fromJson(""))
        assertEquals(WallpaperAdjustments.DEFAULT, WallpaperAdjustments.fromJson("not json"))
    }

    @Test
    fun `missing keys fall back to their default and out of range values are coerced`() {
        val read = WallpaperAdjustments.fromJson("{\"zoom\":8,\"dim\":0.1}")

        assertEquals(5f, read.zoom, 0f)
        assertEquals(0.1f, read.dim, 0f)
        assertEquals(0.5f, read.focusX, 0f)
        assertEquals(0, read.rotation)
    }

    @Test
    fun `framing is detected apart from dim and blur`() {
        assertFalse(WallpaperAdjustments(dim = 0.6f, blurDp = 10f).hasFraming)
        assertFalse(WallpaperAdjustments(dim = 0.6f).isDefault)
        assertTrue(WallpaperAdjustments(zoom = 2f).hasFraming)
        assertTrue(WallpaperAdjustments(focusX = 0.2f).hasFraming)
        assertTrue(WallpaperAdjustments(focusY = 0.9f).hasFraming)
        assertTrue(WallpaperAdjustments(rotation = 90).hasFraming)
    }

    @Test
    fun `without framing keeps dim and blur`() {
        val adjustments = WallpaperAdjustments(3f, 0.1f, 0.9f, 180, 0.5f, 8f)

        assertEquals(WallpaperAdjustments(dim = 0.5f, blurDp = 8f), adjustments.withoutFraming())
    }
}
