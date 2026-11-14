package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.AdaptiveIconDrawable
import android.graphics.drawable.ColorDrawable
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class FullBleedIconTest {

    private val adaptive = AdaptiveIconDrawable(ColorDrawable(Color.BLUE), ColorDrawable(Color.TRANSPARENT))

    @Test
    fun `full bleed bitmap fills every corner with the background, no mask`() {
        val bitmap = AppIconHelper.fullBleedBitmap(adaptive, sizePx = 48)

        assertEquals(48, bitmap.width)
        listOf(0 to 0, 47 to 0, 0 to 47, 47 to 47, 24 to 24).forEach { (x, y) ->
            assertEquals(Color.BLUE, bitmap.getPixel(x, y))
        }
    }

    @Test
    fun `adaptive and cached full bleed icons are full bleed, legacy bitmaps are not`() {
        val resources = RuntimeEnvironment.getApplication().resources
        assertTrue(app(adaptive).isFullBleedIcon)
        assertTrue(app(FullBleedIconDrawable(resources, createBitmap(4, 4))).isFullBleedIcon)
        assertFalse(app(ColorDrawable(Color.RED)).isFullBleedIcon)
    }

    @Test
    fun `an adaptive icon is turned into a square bitmap without the mask`() {
        val bitmap = app(adaptive).getIconBitmap()

        assertEquals(FULL_BLEED_SIZE_PX, bitmap.width)
        assertEquals(FULL_BLEED_SIZE_PX, bitmap.height)
    }

    private fun app(icon: android.graphics.drawable.Drawable) = AppInfo(
        label = "App",
        packageName = "com.example.app",
        componentName = ComponentName("com.example.app", "Main"),
        icon = icon
    )
}
