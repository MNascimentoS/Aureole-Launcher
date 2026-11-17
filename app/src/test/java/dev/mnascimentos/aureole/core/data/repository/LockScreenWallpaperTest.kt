package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.core.graphics.createBitmap
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class LockScreenWallpaperTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository
    private val system = RecordingLockWallpaper()
    private val teal = Color.rgb(0, 128, 128)
    private val orange = Color.rgb(255, 128, 0)

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        repository.systemWallpaper = system
        repository.clearCustomWallpaper()
        repository.selectedThemeName = "Custom"
        repository.manualSeedColor = teal
    }

    private fun pickPhoto() {
        val file = File(context.cacheDir, "photo.png")
        val bitmap: Bitmap = createBitmap(40, 20).also { it.eraseColor(Color.MAGENTA) }
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue(repository.saveCustomWallpaper(Uri.fromFile(file).toString()))
    }

    @Test
    fun `when the option is off, should not touch the lock screen`() = runTest {
        repository.applyToLockScreen = false

        assertTrue(repository.lockScreen.applyCurrent())

        assertEquals(0, system.applied.size)
    }

    @Test
    fun `option is off until the user turns it on`() {
        assertFalse(repository.applyToLockScreen)
        assertFalse(repository.isLockScreenNoticeSeen)
    }

    @Test
    fun `when on with a solid color, should apply a flat bitmap of the screen size in portrait`() = runTest {
        repository.applyToLockScreen = true

        assertTrue(repository.lockScreen.applyCurrent())

        val (width, height) = portraitScreenSize(context)
        val bitmap = system.applied.single()
        assertEquals(width to height, bitmap.first to bitmap.second)
        assertEquals(teal, bitmap.third)
        assertTrue(File(context.filesDir, "lock_wallpaper.png").exists())
    }

    @Test
    fun `when on with a photo, should apply the photo dimmed by the saved adjustments`() = runTest {
        repository.applyToLockScreen = true
        pickPhoto()
        repository.wallpaperAdjustments = WallpaperAdjustments(dim = 0f)

        assertTrue(repository.lockScreen.applyCurrent())

        val pixel = system.applied.single().third
        // The saved photo is a JPEG, so a channel can be off by a little.
        assertEquals(255f, Color.red(pixel).toFloat(), 4f)
        assertEquals(0f, Color.green(pixel).toFloat(), 4f)
        assertEquals(255f, Color.blue(pixel).toFloat(), 4f)
    }

    @Test
    fun `when the photo is dimmed, should apply it darker`() = runTest {
        repository.applyToLockScreen = true
        pickPhoto()
        repository.wallpaperAdjustments = WallpaperAdjustments(dim = 0.5f)

        repository.lockScreen.applyCurrent()

        val pixel = system.applied.single().third
        assertTrue(Color.red(pixel) in 120..135)
    }

    @Test
    fun `when the system refuses, should report failure and keep no image`() = runTest {
        repository.applyToLockScreen = true
        system.applyResult = false

        assertFalse(repository.lockScreen.applyCurrent())

        assertFalse(File(context.filesDir, "lock_wallpaper.png").exists())
    }

    @Test
    fun `undo brings back the image applied before the last change`() = runTest {
        repository.applyToLockScreen = true
        repository.lockScreen.applyCurrent()
        repository.manualSeedColor = orange
        repository.lockScreen.applyCurrent()
        assertEquals(orange, system.applied.last().third)

        repository.manualSeedColor = teal
        assertTrue(repository.lockScreen.applyPrevious())

        assertEquals(3, system.applied.size)
        assertEquals(teal, system.applied.last().third)
    }

    @Test
    fun `undo without an earlier image draws the restored background again`() = runTest {
        repository.applyToLockScreen = true
        repository.manualSeedColor = orange
        repository.lockScreen.applyCurrent()

        repository.manualSeedColor = teal
        assertTrue(repository.lockScreen.applyPrevious())

        assertEquals(teal, system.applied.last().third)
    }

    @Test
    fun `undo does nothing while the option is off`() = runTest {
        repository.applyToLockScreen = false

        assertTrue(repository.lockScreen.applyPrevious())

        assertEquals(0, system.applied.size)
    }

    @Test
    fun `back to system wallpaper clears the lock screen and forgets the image`() = runTest {
        repository.applyToLockScreen = true
        repository.lockScreen.applyCurrent()

        assertTrue(repository.lockScreen.clear())

        assertEquals(1, system.clearLockCalls)
        assertFalse(File(context.filesDir, "lock_wallpaper.png").exists())
    }

    @Test
    fun `when clearing fails, should report it and keep the image`() = runTest {
        repository.applyToLockScreen = true
        repository.lockScreen.applyCurrent()
        system.clearResult = false

        assertFalse(repository.lockScreen.clear())

        assertTrue(File(context.filesDir, "lock_wallpaper.png").exists())
    }

    @Test
    fun `permission to change the wallpaper comes from the system`() {
        system.canSet = false
        assertFalse(repository.lockScreen.canSet())

        system.canSet = true
        assertTrue(repository.lockScreen.canSet())
    }

    @Test
    fun `stash gives back only an image that was kept`() {
        val stash = repository.wallpaperStash
        assertNull(stash.takeLockImageBack())

        stash.lockImageFile.writeText("a")
        stash.keepLockImage()
        stash.lockImageFile.writeText("b")

        assertEquals("a", stash.takeLockImageBack()?.readText())
        stash.discard()
        assertNull(stash.takeLockImageBack())
    }
}

/** Records what the app asks the system to do with the lock screen; the bitmap is kept as (w, h, pixel 5,5). */
private class RecordingLockWallpaper : SystemWallpaper {
    val applied = mutableListOf<Triple<Int, Int, Int>>()
    var applyResult = true
    var clearResult = true
    var canSet = true
    var clearLockCalls = 0

    override fun clear(): Boolean = true
    override fun save(target: File): Boolean = false
    override fun apply(source: File): Boolean = true

    override fun applyLock(bitmap: Bitmap): Boolean {
        if (applyResult) applied += Triple(bitmap.width, bitmap.height, bitmap.getPixel(5, 5))
        return applyResult
    }

    override fun clearLock(): Boolean {
        clearLockCalls++
        return clearResult
    }

    override fun canSetWallpaper(): Boolean = canSet
}
