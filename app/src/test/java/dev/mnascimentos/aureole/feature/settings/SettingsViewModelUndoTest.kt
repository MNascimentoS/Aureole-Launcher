package dev.mnascimentos.aureole.feature.settings

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.designsystem.model.TextResource
import dev.mnascimentos.aureole.feature.settings.extensions.onUndo
import dev.mnascimentos.aureole.feature.settings.extensions.onUndoExpired
import dev.mnascimentos.aureole.feature.settings.extensions.restoreDefaultWallpaper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.io.File

private const val AWAIT_MS = 5_000L
private const val POLL_MS = 20L

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class SettingsViewModelUndoTest {

    private lateinit var context: Context
    private lateinit var repository: SettingsRepository
    private lateinit var viewModel: SettingsViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        context = RuntimeEnvironment.getApplication()
        repository = SettingsRepository(context)
        repository.clearCustomWallpaper()
        viewModel = SettingsViewModel(
            settingsRepository = repository,
            appRepository = AppRepository(context),
            folderRepository = FolderRepository(context),
            gridRepository = GridRepository(context)
        )
    }

    @After
    fun tearDown() {
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        repository.wallpaperStash.discard()
        repository.clearCustomWallpaper()
        Dispatchers.resetMain()
    }

    @Test
    fun `restoring the background offers undo that brings the custom image back`() {
        val path = saveCustomWallpaper()

        viewModel.restoreDefaultWallpaper()
        awaitUntil { viewModel.uiState.value.undoSnackbar != null }

        assertFalse(viewModel.uiState.value.isCustomWallpaperSet)
        assertFalse(File(path).exists())
        assertEquals(
            TextResource.fromResId(R.string.settings_appearance_background_restored),
            viewModel.uiState.value.undoSnackbar?.message
        )

        viewModel.onUndo()
        awaitUntil { viewModel.uiState.value.isCustomWallpaperSet }

        assertTrue(viewModel.uiState.value.isCustomWallpaperSet)
        assertEquals(path, viewModel.uiState.value.customWallpaperPath)
        assertTrue(File(path).exists())
        assertTrue(repository.isCustomWallpaperSet)
        assertNull(viewModel.uiState.value.undoSnackbar)
    }

    @Test
    fun `restoring when there is no custom image offers no undo`() {
        viewModel.restoreDefaultWallpaper()
        Thread.sleep(POLL_MS * 5)

        assertNull(viewModel.uiState.value.undoSnackbar)
        assertFalse(viewModel.uiState.value.isCustomWallpaperSet)
    }

    @Test
    fun `when the undo offer expires, the custom image kept aside is deleted`() {
        val path = saveCustomWallpaper()
        viewModel.restoreDefaultWallpaper()
        awaitUntil { viewModel.uiState.value.undoSnackbar != null }
        val offer = requireNotNull(viewModel.uiState.value.undoSnackbar)

        viewModel.onUndoExpired(offer.id)

        assertNull(viewModel.uiState.value.undoSnackbar)
        assertFalse(repository.wallpaperStash.undo(path))
        assertFalse(File(path).exists())
    }

    @Test
    fun `an expiry of an older offer keeps the current one and the stashed image`() {
        val path = saveCustomWallpaper()
        viewModel.restoreDefaultWallpaper()
        awaitUntil { viewModel.uiState.value.undoSnackbar != null }
        val offer = requireNotNull(viewModel.uiState.value.undoSnackbar)

        viewModel.onUndoExpired(offer.id - 1)

        assertNotNull(viewModel.uiState.value.undoSnackbar)
        assertTrue(repository.wallpaperStash.undo(path))
    }

    private fun saveCustomWallpaper(): String {
        val source = File(context.cacheDir, "undo_source.png")
        val bitmap: Bitmap = createBitmap(4, 4)
        source.outputStream().use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        assertTrue(repository.saveCustomWallpaper(Uri.fromFile(source).toString()))
        viewModel.loadSettings()
        return requireNotNull(repository.customWallpaperPath)
    }

    /** The repository hops to Dispatchers.IO, which the test dispatcher does not control: poll for the result. */
    private fun awaitUntil(condition: () -> Boolean) {
        val deadline = System.currentTimeMillis() + AWAIT_MS
        while (!condition() && System.currentTimeMillis() < deadline) Thread.sleep(POLL_MS)
    }
}
