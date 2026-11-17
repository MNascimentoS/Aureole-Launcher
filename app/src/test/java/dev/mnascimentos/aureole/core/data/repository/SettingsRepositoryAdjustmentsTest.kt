package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.createBitmap
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

@RunWith(RobolectricTestRunner::class)
class SettingsRepositoryAdjustmentsTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository
    private val adjusted = WallpaperAdjustments(2f, 0.3f, 0.6f, 90, 0.5f, 10f)

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        repository.systemWallpaper = SilentSystemWallpaper()
        repository.clearCustomWallpaper()
    }

    @Test
    fun `without saved adjustments the default is read`() {
        assertEquals(WallpaperAdjustments.DEFAULT, repository.wallpaperAdjustments)
    }

    @Test
    fun `saved adjustments are read back and default clears them`() {
        repository.wallpaperAdjustments = adjusted
        assertEquals(adjusted, repository.wallpaperAdjustments)

        repository.wallpaperAdjustments = WallpaperAdjustments.DEFAULT
        assertTrue(repository.wallpaperAdjustments.isDefault)
    }

    @Test
    fun `values out of range are coerced when saved`() {
        repository.wallpaperAdjustments = WallpaperAdjustments(zoom = 50f, dim = 3f)

        assertEquals(5f, repository.wallpaperAdjustments.zoom, 0f)
        assertEquals(0.7f, repository.wallpaperAdjustments.dim, 0f)
    }

    @Test
    fun `choosing another photo resets the adjustments`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("first.png")).toString())
        repository.wallpaperAdjustments = adjusted

        repository.saveCustomWallpaper(Uri.fromFile(pngFile("second.png")).toString())

        assertEquals(WallpaperAdjustments.DEFAULT, repository.wallpaperAdjustments)
    }

    @Test
    fun `removing the photo resets the adjustments`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("remove.png")).toString())
        repository.wallpaperAdjustments = adjusted

        repository.clearCustomWallpaper()

        assertEquals(WallpaperAdjustments.DEFAULT, repository.wallpaperAdjustments)
    }

    @Test
    fun `undoing a photo swap brings the previous adjustments back`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("before.png")).toString())
        repository.wallpaperAdjustments = adjusted
        val path = requireNotNull(repository.customWallpaperPath)

        assertTrue(repository.wallpaperStash.keepCopy(path))
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("after.png")).toString())
        assertTrue(repository.wallpaperAdjustments.isDefault)
        assertTrue(repository.wallpaperStash.undo(path))

        assertEquals(adjusted, repository.wallpaperAdjustments)
    }

    @Test
    fun `undoing a restore brings the adjustments back`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("restore.png")).toString())
        repository.wallpaperAdjustments = adjusted

        val path = requireNotNull(repository.wallpaperStash.restoreDefaultKeepingUndo())
        assertTrue(repository.wallpaperAdjustments.isDefault)
        assertTrue(repository.wallpaperStash.undo(path))

        assertEquals(adjusted, repository.wallpaperAdjustments)
    }

    @Test
    fun `undo of a photo that had default adjustments leaves the default`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("plain.png")).toString())
        val path = requireNotNull(repository.customWallpaperPath)
        repository.wallpaperStash.keepCopy(path)
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("other.png")).toString())
        repository.wallpaperAdjustments = adjusted

        repository.wallpaperStash.undo(path)

        assertTrue(repository.wallpaperAdjustments.isDefault)
    }

    @Test
    fun `discarding the stash drops the kept adjustments`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("discard.png")).toString())
        repository.wallpaperAdjustments = adjusted
        val path = requireNotNull(repository.wallpaperStash.restoreDefaultKeepingUndo())

        repository.wallpaperStash.discard()

        assertFalse(repository.wallpaperStash.undo(path))
        assertTrue(repository.wallpaperAdjustments.isDefault)
        assertFalse(File(context.filesDir, "wallpaper_adjustments_undo.json").exists())
    }

    private fun pngFile(name: String): File {
        val file = File(context.cacheDir, name)
        val bitmap: Bitmap = createBitmap(4, 4)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file
    }
}

private class SilentSystemWallpaper : SystemWallpaper {
    override fun clear(): Boolean = true
    override fun save(target: File): Boolean = false
    override fun apply(source: File): Boolean = true
}
