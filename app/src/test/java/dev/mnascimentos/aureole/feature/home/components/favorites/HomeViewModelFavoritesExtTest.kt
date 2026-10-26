package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.Context
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.setShowFavoritePicker
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.toggleFavorite
import dev.mnascimentos.aureole.feature.home.components.favorites.extensions.updateFavoritePackages
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelFavoritesExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        settingsRepository = SettingsRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepository,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepository),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
        // Give time for viewModel init
        ShadowLooper.idleMainLooper()
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back: they resume
        // on Main from IO threads, so drive the test scheduler until the cancelled scope completes.
        val scope = viewModel.viewModelScope.coroutineContext.job
        scope.cancel()
        val deadline = System.currentTimeMillis() + VIEW_MODEL_SHUTDOWN_MS
        while (!scope.isCompleted && System.currentTimeMillis() < deadline) {
            testDispatcher.scheduler.advanceUntilIdle()
            Thread.sleep(SHUTDOWN_POLL_MS)
        }
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when setShowFavoritePicker is true with containerId, should update state`() {
        viewModel.setShowFavoritePicker(true, "container_123")

        assertTrue(viewModel.uiState.value.showFavoritePickerDialog)
        assertEquals("container_123", viewModel.uiState.value.activeFavoriteContainerId)
    }

    @Test
    fun `when setShowFavoritePicker is false, should reset activeFavoriteContainerId to null`() {
        viewModel.setShowFavoritePicker(true, "container_123")
        viewModel.setShowFavoritePicker(false)

        assertFalse(viewModel.uiState.value.showFavoritePickerDialog)
        assertNull(viewModel.uiState.value.activeFavoriteContainerId)
    }

    @Test
    fun `when toggleFavorite is called for non-existing package, should add it`() = runTest(testDispatcher) {
        val pkg = "com.example.app1"
        viewModel.toggleFavorite(pkg, "default_favorites")

        val state = viewModel.uiState.first { it.favoriteAppPackages.contains(pkg) }
        assertTrue(state.favoriteAppPackages.contains(pkg))
    }

    @Test
    fun `when toggleFavorite is called for existing package, should remove it`() = runTest(testDispatcher) {
        val pkg = "com.example.app1"
        viewModel.toggleFavorite(pkg, "default_favorites")

        var state = viewModel.uiState.first { it.favoriteAppPackages.contains(pkg) }
        assertTrue(state.favoriteAppPackages.contains(pkg))

        viewModel.toggleFavorite(pkg, "default_favorites")
        state = viewModel.uiState.first { !it.favoriteAppPackages.contains(pkg) }
        assertFalse(state.favoriteAppPackages.contains(pkg))
    }

    @Test
    fun `when updateFavoritePackages is called, should update active container favorites`() = runTest(testDispatcher) {
        val newFavs = listOf("com.example.app1", "com.example.app2")
        viewModel.updateFavoritePackages(newFavs)

        val state = viewModel.uiState.first { it.favoriteAppPackages == newFavs }
        assertEquals(newFavs, state.favoriteAppPackages)
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
