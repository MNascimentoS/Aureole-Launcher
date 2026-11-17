package dev.mnascimentos.aureole.core.image

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class StackBlurTest {

    @Test
    fun `radius zero returns the same pixels`() {
        val pixels = intArrayOf(0xFF102030.toInt(), 0xFFFFFFFF.toInt(), 0xFF000000.toInt(), 0xFF808080.toInt())
        val copy = pixels.copyOf()

        StackBlur.blur(pixels, 2, 2, 0)

        assertArrayEquals(copy, pixels)
    }

    @Test
    fun `a flat image stays flat`() {
        val flat = 0xFF336699.toInt()
        val pixels = IntArray(8 * 6) { flat }

        StackBlur.blur(pixels, 8, 6, 5)

        assertTrue(pixels.all { it == flat })
    }

    @Test
    fun `a sharp edge becomes a gradient`() {
        val width = 16
        val pixels = IntArray(width) { if (it < width / 2) 0xFF000000.toInt() else 0xFFFFFFFF.toInt() }

        StackBlur.blur(pixels, width, 1, 4)

        val before = pixels[width / 2 - 1] and 0xFF
        val after = pixels[width / 2] and 0xFF
        assertTrue(before > 0)
        assertTrue(after < 255)
        assertTrue(before <= after)
    }

    @Test
    fun `alpha is blurred like the color channels and the size is kept`() {
        val pixels = IntArray(4 * 4) { 0x80FF0000.toInt() }

        val result = StackBlur.blur(pixels, 4, 4, 2)

        assertEquals(16, result.size)
        assertTrue(result.all { it == 0x80FF0000.toInt() })
    }

    @Test
    fun `invalid sizes leave the pixels alone`() {
        val pixels = intArrayOf(1, 2, 3)

        assertArrayEquals(intArrayOf(1, 2, 3), StackBlur.blur(pixels, 0, 3, 3))
        assertArrayEquals(intArrayOf(1, 2, 3), StackBlur.blur(pixels, 2, 2, 3))
    }
}
