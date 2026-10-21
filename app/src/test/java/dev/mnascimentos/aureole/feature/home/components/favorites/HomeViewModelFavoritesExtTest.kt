package dev.mnascimentos.aureole.feature.home.components.favorites

import android.content.Context
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
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class HomeViewModelFavoritesExtTest {

    private lateinit var context: Context
    private lateinit var settingsRepository: SettingsRepository
    private lateinit var viewModel: HomeViewModel

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(StandardTestDispatcher())
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
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

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when toggleFavorite is called for non-existing package, should add it`() = runTest {
        val pkg = "com.example.app1"
        viewModel.toggleFavorite(pkg, "default_favorites")
        var retries = 0
        while (!viewModel.uiState.value.favoriteAppPackages.contains(pkg) && retries < 20) {
            Thread.sleep(50)
            advanceUntilIdle()
            retries++
        }
        assertTrue(viewModel.uiState.value.favoriteAppPackages.contains(pkg))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when toggleFavorite is called for existing package, should remove it`() = runTest {
        val pkg = "com.example.app1"
        viewModel.toggleFavorite(pkg, "default_favorites")
        var retries = 0
        while (!viewModel.uiState.value.favoriteAppPackages.contains(pkg) && retries < 20) {
            Thread.sleep(50)
            advanceUntilIdle()
            retries++
        }
        assertTrue(viewModel.uiState.value.favoriteAppPackages.contains(pkg))

        viewModel.toggleFavorite(pkg, "default_favorites")
        retries = 0
        while (viewModel.uiState.value.favoriteAppPackages.contains(pkg) && retries < 20) {
            Thread.sleep(50)
            advanceUntilIdle()
            retries++
        }
        assertFalse(viewModel.uiState.value.favoriteAppPackages.contains(pkg))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `when updateFavoritePackages is called, should update active container favorites`() = runTest {
        val newFavs = listOf("com.example.app1", "com.example.app2")
        viewModel.updateFavoritePackages(newFavs)
        var retries = 0
        while (viewModel.uiState.value.favoriteAppPackages != newFavs && retries < 20) {
            Thread.sleep(50)
            advanceUntilIdle()
            retries++
        }
        assertEquals(newFavs, viewModel.uiState.value.favoriteAppPackages)
    }
}
