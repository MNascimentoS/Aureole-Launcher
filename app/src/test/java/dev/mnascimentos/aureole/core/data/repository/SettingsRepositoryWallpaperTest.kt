package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.createBitmap
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryWallpaperTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        repository.clearCustomWallpaper()
    }

    @Test
    fun `when a valid image is saved, should store it and mark the custom wallpaper as set`() {
        val source = pngFile("source.png")

        val saved = repository.saveCustomWallpaper(Uri.fromFile(source).toString())

        assertTrue(saved)
        assertTrue(repository.isCustomWallpaperSet)
        assertTrue(File(requireNotNull(repository.customWallpaperPath)).exists())
    }

    @Test
    fun `when the uri cannot be read, should report failure and keep no wallpaper`() {
        val saved = repository.saveCustomWallpaper(Uri.fromFile(File(context.cacheDir, "missing.png")).toString())

        assertFalse(saved)
        assertFalse(repository.isCustomWallpaperSet)
    }

    @Test
    fun `when the flag is set but the file is gone, should not report a custom wallpaper`() {
        repository.customWallpaperPath = File(context.filesDir, "gone.jpg").absolutePath
        repository.isCustomWallpaperSet = true

        assertFalse(repository.isCustomWallpaperSet)
    }

    @Test
    fun `when the flag is set without a path, should not report a custom wallpaper`() {
        repository.isCustomWallpaperSet = true

        assertFalse(repository.isCustomWallpaperSet)
    }

    @Test
    fun `when clearing the custom wallpaper, should delete the file and the saved path`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("to_clear.png")).toString())
        val path = requireNotNull(repository.customWallpaperPath)

        repository.clearCustomWallpaper()

        assertFalse(File(path).exists())
        assertNull(repository.customWallpaperPath)
        assertFalse(repository.isCustomWallpaperSet)
    }

    @Test
    fun `when clearing with a path whose file is already gone, should still clear the settings`() {
        repository.customWallpaperPath = File(context.filesDir, "already_gone.jpg").absolutePath

        repository.clearCustomWallpaper()

        assertNull(repository.customWallpaperPath)
    }

    @Test
    fun `when restoring the default wallpaper, should clear the custom one`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("restore.png")).toString())

        repository.restoreDefaultWallpaper()

        assertFalse(repository.isCustomWallpaperSet)
    }

    @Test
    fun `when the app does not hold the home role, should not be the default launcher`() {
        assertFalse(repository.checkDefaultLauncher())
    }

    @Test
    @Config(sdk = [28])
    fun `when on Android 9 and this app is the only home app, should be the default launcher`() {
        assertTrue(repository.checkDefaultLauncher())
    }

    @Test
    fun `when the last seen version is saved, should read it back`() {
        repository.lastSeenVersionCode = 826

        assertEquals(826, repository.lastSeenVersionCode)
    }

    private fun pngFile(name: String): File {
        val file = File(context.cacheDir, name)
        val bitmap: Bitmap = createBitmap(4, 4)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file
    }
}
