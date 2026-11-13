package dev.mnascimentos.aureole.core.data.image

import androidx.exifinterface.media.ExifInterface
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WallpaperImageTest {

    @Test
    fun `exifRotationDegrees maps the eight EXIF orientations to the rotation to apply`() {
        val expected = mapOf(
            ExifInterface.ORIENTATION_NORMAL to 0,
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL to 0,
            ExifInterface.ORIENTATION_ROTATE_180 to 180,
            ExifInterface.ORIENTATION_FLIP_VERTICAL to 180,
            ExifInterface.ORIENTATION_TRANSPOSE to 270,
            ExifInterface.ORIENTATION_ROTATE_90 to 90,
            ExifInterface.ORIENTATION_TRANSVERSE to 90,
            ExifInterface.ORIENTATION_ROTATE_270 to 270
        )

        expected.forEach { (orientation, degrees) ->
            assertEquals("orientation $orientation", degrees, exifRotationDegrees(orientation))
        }
    }

    @Test
    fun `exifRotationDegrees treats an unknown or missing orientation as upright`() {
        assertEquals(0, exifRotationDegrees(ExifInterface.ORIENTATION_UNDEFINED))
        assertEquals(0, exifRotationDegrees(99))
        assertEquals(0, exifRotationDegrees(-1))
    }

    @Test
    fun `exifIsMirrored is true only for the four mirrored orientations`() {
        val mirrored = setOf(
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL,
            ExifInterface.ORIENTATION_FLIP_VERTICAL,
            ExifInterface.ORIENTATION_TRANSPOSE,
            ExifInterface.ORIENTATION_TRANSVERSE
        )

        mirrored.forEach { assertTrue("orientation $it", exifIsMirrored(it)) }
        listOf(
            ExifInterface.ORIENTATION_NORMAL,
            ExifInterface.ORIENTATION_ROTATE_90,
            ExifInterface.ORIENTATION_ROTATE_180,
            ExifInterface.ORIENTATION_ROTATE_270,
            ExifInterface.ORIENTATION_UNDEFINED
        ).forEach { assertFalse("orientation $it", exifIsMirrored(it)) }
    }

    @Test
    fun `sampleSizeFor keeps an image that is not bigger than twice the target`() {
        assertEquals(1, sampleSizeFor(width = 1080, height = 2400, target = 2400))
        assertEquals(1, sampleSizeFor(width = 4000, height = 4000, target = 2400))
    }

    @Test
    fun `sampleSizeFor halves a huge photo while both sides stay at or above the target`() {
        assertEquals(2, sampleSizeFor(width = 6000, height = 4800, target = 2400))
        assertEquals(4, sampleSizeFor(width = 12000, height = 9600, target = 2400))
    }

    @Test
    fun `sampleSizeFor never goes below 1 for degenerate sizes`() {
        assertEquals(1, sampleSizeFor(width = 0, height = 0, target = 2400))
        assertEquals(1, sampleSizeFor(width = 100, height = 100, target = 0))
    }
}
