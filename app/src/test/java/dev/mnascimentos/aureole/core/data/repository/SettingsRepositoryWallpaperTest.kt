package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.exifinterface.media.ExifInterface
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
    private val system = FakeSystemWallpaper()

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        repository.systemWallpaper = system
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
    fun `when restoring keeping undo, should keep the file aside and turn the custom wallpaper off`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("stash.png")).toString())
        val path = requireNotNull(repository.customWallpaperPath)

        val stashed = repository.wallpaperStash.restoreDefaultKeepingUndo()

        assertEquals(path, stashed)
        assertFalse(File(path).exists())
        assertFalse(repository.isCustomWallpaperSet)
        assertNull(repository.customWallpaperPath)
        repository.wallpaperStash.discard()
    }

    @Test
    fun `when undoing, should put the file and the settings back`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("unstash.png")).toString())
        val path = requireNotNull(repository.wallpaperStash.restoreDefaultKeepingUndo())

        val restored = repository.wallpaperStash.undo(path)

        assertTrue(restored)
        assertTrue(File(path).exists())
        assertTrue(repository.isCustomWallpaperSet)
        assertEquals(path, repository.customWallpaperPath)
    }

    @Test
    fun `when nothing is stashed, should stash nothing and not undo`() {
        assertNull(repository.wallpaperStash.restoreDefaultKeepingUndo())
        assertFalse(repository.wallpaperStash.undo(File(context.filesDir, "nothing.jpg").absolutePath))
    }

    @Test
    fun `when the stash is discarded, should not undo anymore`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("discard.png")).toString())
        val path = requireNotNull(repository.wallpaperStash.restoreDefaultKeepingUndo())

        repository.wallpaperStash.discard()

        assertFalse(repository.wallpaperStash.undo(path))
    }

    @Test
    fun `when restoring keeping undo, should save then clear the system wallpaper`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("sys1.png")).toString())

        repository.wallpaperStash.restoreDefaultKeepingUndo()

        assertEquals(listOf("save", "clear"), system.calls)
        repository.wallpaperStash.discard()
    }

    @Test
    fun `when undoing, should reapply the saved system wallpaper`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("sys2.png")).toString())
        val path = requireNotNull(repository.wallpaperStash.restoreDefaultKeepingUndo())

        repository.wallpaperStash.undo(path)

        assertEquals(listOf("save", "clear", "apply"), system.calls)
        assertEquals("system", system.applied)
        assertFalse(File(context.filesDir, "system_wallpaper_undo.jpg").exists())
    }

    @Test
    fun `when the system wallpaper could not be saved, should still clear and not reapply on undo`() {
        system.canSave = false
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("sys3.png")).toString())
        val path = requireNotNull(repository.wallpaperStash.restoreDefaultKeepingUndo())

        assertTrue(repository.wallpaperStash.undo(path))

        assertEquals(listOf("save", "clear"), system.calls)
    }

    @Test
    fun `when the undo window expires, should not reapply and should delete the saved files`() {
        repository.saveCustomWallpaper(Uri.fromFile(pngFile("sys4.png")).toString())
        repository.wallpaperStash.restoreDefaultKeepingUndo()

        repository.wallpaperStash.discard()

        assertEquals(listOf("save", "clear"), system.calls)
        assertFalse(File(context.filesDir, "system_wallpaper_undo.jpg").exists())
        assertFalse(File(context.filesDir, "custom_wallpaper_undo.jpg").exists())
    }

    @Test
    fun `when there is no custom wallpaper, should not touch the system wallpaper`() {
        repository.wallpaperStash.restoreDefaultKeepingUndo()

        assertTrue(system.calls.isEmpty())
    }

    @Test
    fun `when the platform wallpaper is used, should not throw`() {
        val platform = PlatformSystemWallpaper(context)
        val target = File(context.cacheDir, "sys_target.jpg")

        platform.save(target)
        platform.apply(target)
        platform.apply(File(context.cacheDir, "absent.jpg"))
        platform.clear()
        target.delete()
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

    @Test
    fun `when a landscape photo has EXIF rotation 90, should save it upright`() {
        val source = jpegFile(
            "exif90.jpg",
            width = WIDE,
            height = TALL,
            orientation = ExifInterface.ORIENTATION_ROTATE_90
        )

        assertTrue(repository.saveCustomWallpaper(Uri.fromFile(source).toString()))

        assertEquals(TALL to WIDE, savedSize())
    }

    @Test
    fun `when a photo has no EXIF rotation, should keep its size`() {
        val source = jpegFile("exif0.jpg", width = WIDE, height = TALL, orientation = ExifInterface.ORIENTATION_NORMAL)

        assertTrue(repository.saveCustomWallpaper(Uri.fromFile(source).toString()))

        assertEquals(WIDE to TALL, savedSize())
    }

    @Test
    fun `when a photo has EXIF rotation 180, should keep its size`() {
        val source = jpegFile(
            "exif180.jpg",
            width = WIDE,
            height = TALL,
            orientation = ExifInterface.ORIENTATION_ROTATE_180
        )

        assertTrue(repository.saveCustomWallpaper(Uri.fromFile(source).toString()))

        assertEquals(WIDE to TALL, savedSize())
    }

    private fun savedSize(): Pair<Int, Int> {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(requireNotNull(repository.customWallpaperPath), bounds)
        return bounds.outWidth to bounds.outHeight
    }

    private fun jpegFile(name: String, width: Int, height: Int, orientation: Int): File {
        val file = File(context.cacheDir, name)
        val bitmap: Bitmap = createBitmap(width, height)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it) }
        ExifInterface(file.absolutePath).apply {
            setAttribute(ExifInterface.TAG_ORIENTATION, orientation.toString())
            saveAttributes()
        }
        return file
    }

    private fun pngFile(name: String): File {
        val file = File(context.cacheDir, name)
        val bitmap: Bitmap = createBitmap(4, 4)
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        return file
    }

    private companion object {
        const val WIDE = 40
        const val TALL = 20
    }
}

private class FakeSystemWallpaper : SystemWallpaper {
    val calls = mutableListOf<String>()
    var canSave = true
    var applied: String? = null

    override fun clear(): Boolean {
        calls += "clear"
        return true
    }

    override fun save(target: File): Boolean {
        calls += "save"
        if (canSave) target.writeText("system")
        return canSave
    }

    override fun apply(source: File): Boolean {
        calls += "apply"
        applied = source.readText()
        return true
    }
}
