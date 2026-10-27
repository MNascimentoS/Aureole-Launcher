package dev.mnascimentos.aureole.core.data.model

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppInfoModelTest {

    @Test
    fun testAppInfoPropertiesAndMethods() {
        val app = AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "MainActivity"),
            icon = ColorDrawable(Color.RED)
        )

        assertEquals('C', app.firstLetter)

        val iconBitmap = app.getIconBitmap()
        assertNotNull(iconBitmap)

        // Cached hit
        assertEquals(iconBitmap, app.getIconBitmap())

        val themedBitmap = app.getThemedIconBitmap(0xFF00FF00.toInt())
        assertNotNull(themedBitmap)

        // Cached hit for themed bitmap
        assertEquals(themedBitmap, app.getThemedIconBitmap(0xFF00FF00.toInt()))
    }

    @Test
    fun testAppInfoFirstLetterWithSpecialChars() {
        val appWithSpace = AppInfo(
            label = "  Gallery",
            packageName = "com.example.gallery",
            componentName = ComponentName("com.example.gallery", "MainActivity"),
            icon = ColorDrawable(Color.BLUE)
        )
        assertEquals('G', appWithSpace.firstLetter)

        val appEmpty = AppInfo(
            label = "  ",
            packageName = "com.example.empty",
            componentName = ComponentName("com.example.empty", "MainActivity"),
            icon = ColorDrawable(Color.BLUE)
        )
        assertEquals('#', appEmpty.firstLetter)
    }
}
