package dev.mnascimentos.aureole.feature.home.components.appsdrawer.extensions

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import androidx.core.graphics.drawable.toDrawable
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.repository.AppRepository
import dev.mnascimentos.aureole.core.data.repository.ContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FavoriteContainerRepository
import dev.mnascimentos.aureole.core.data.repository.FolderRepository
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.core.data.repository.SettingsRepository
import dev.mnascimentos.aureole.core.data.repository.WidgetRepository
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.model.HomeRepositories
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper

@RunWith(RobolectricTestRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelSearchExtTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val appA = AppInfo(
        label = "Alpha App",
        packageName = "com.example.alpha",
        componentName = ComponentName("com.example.alpha", "MainActivity"),
        icon = Color.RED.toDrawable()
    )
    private val appB = AppInfo(
        label = "Browser Chrome",
        packageName = "com.android.chrome",
        componentName = ComponentName("com.android.chrome", "MainActivity"),
        icon = Color.BLUE.toDrawable()
    )
    private val appC = AppInfo(
        label = "Navegador Web",
        packageName = "com.example.browser",
        componentName = ComponentName("com.example.browser", "MainActivity"),
        icon = Color.GREEN.toDrawable()
    )
    private val appSpecial = AppInfo(
        label = "123 Calculator",
        packageName = "com.example.calc",
        componentName = ComponentName("com.example.calc", "MainActivity"),
        icon = Color.YELLOW.toDrawable()
    )
    private val appAccented = AppInfo(
        label = "Música e Áudio",
        packageName = "com.example.music",
        componentName = ComponentName("com.example.music", "MainActivity"),
        icon = Color.CYAN.toDrawable()
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        val settingsRepo = SettingsRepository(context)

        viewModel = HomeViewModel(
            HomeRepositories(
                appRepository = AppRepository(context),
                widgetRepository = WidgetRepository(context),
                settingsRepository = settingsRepo,
                folderRepository = FolderRepository(context),
                favoriteContainerRepository = FavoriteContainerRepository(context, settingsRepo),
                containerRepository = ContainerRepository(context),
                gridRepository = GridRepository(context)
            )
        )
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
    fun `when updateAppsState is called, should update apps, alphabet and favoriteApps`() {
        val sampleApps = listOf(appSpecial, appA, appB, appAccented)
        viewModel.updateAppsState(sampleApps)

        val state = viewModel.uiState.value
        assertEquals(sampleApps, state.apps)
        assertEquals(sampleApps, state.filteredApps)
        assertTrue(state.alphabet.contains('#'))
        assertTrue(state.alphabet.contains('A'))
        assertTrue(state.alphabet.contains('B'))
        assertTrue(state.alphabet.contains('M'))
        assertEquals(0, state.letterIndexMap['#'])
        assertEquals(1, state.letterIndexMap['A'])
    }

    @Test
    fun `when updateAppsState is called with empty apps and isLoading true, should keep loading`() {
        viewModel.updateAppsState(emptyList())

        val state = viewModel.uiState.value
        assertTrue(state.apps.isEmpty())
        assertTrue(state.filteredApps.isEmpty())
    }

    @Test
    fun `when applySearchFilter is called with blank query, should restore all apps and alphabet`() {
        val sampleApps = listOf(appA, appB)
        viewModel.updateAppsState(sampleApps)

        viewModel.applySearchFilter("   ")

        val state = viewModel.uiState.value
        assertEquals(sampleApps, state.filteredApps)
        assertFalse(state.alphabet.isEmpty())
        assertFalse(state.letterIndexMap.isEmpty())
    }

    @Test
    fun `when applySearchFilter matches label or package name, should filter and clear alphabet`() {
        val sampleApps = listOf(appA, appB, appC)
        viewModel.updateAppsState(sampleApps)

        viewModel.applySearchFilter("chrome")

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredApps.size)
        assertEquals(appB, state.filteredApps.first())
        assertTrue(state.alphabet.isEmpty())
        assertTrue(state.letterIndexMap.isEmpty())
    }

    @Test
    fun `when applySearchFilter uses synonyms like browser or musica, should expand search terms`() {
        val sampleApps = listOf(appA, appB, appC, appAccented)
        viewModel.updateAppsState(sampleApps)

        // "browser" synonym should match Chrome and "Navegador Web"
        viewModel.applySearchFilter("browser")
        var state = viewModel.uiState.value
        assertTrue(state.filteredApps.contains(appB))
        assertTrue(state.filteredApps.contains(appC))

        // "musica" (with or without accent) should match "Música e Áudio"
        viewModel.applySearchFilter("musica")
        state = viewModel.uiState.value
        assertTrue(state.filteredApps.contains(appAccented))
    }

    @Test
    fun `when applySearchFilter query has no matches, should return empty filteredApps`() {
        val sampleApps = listOf(appA, appB)
        viewModel.updateAppsState(sampleApps)

        viewModel.applySearchFilter("non_existing_app_query_123")

        val state = viewModel.uiState.value
        assertTrue(state.filteredApps.isEmpty())
    }

    @Test
    fun `when onSearchQueryChanged is called, should update searchQuery and debounce filter`() = runTest(
        testDispatcher
    ) {
        val sampleApps = listOf(appA, appB, appC)
        viewModel.updateAppsState(sampleApps)

        viewModel.onSearchQueryChanged("Alpha")
        assertEquals("Alpha", viewModel.uiState.value.searchQuery)

        testDispatcher.scheduler.advanceTimeBy(350)
        testDispatcher.scheduler.runCurrent()
        ShadowLooper.idleMainLooper()

        val state = viewModel.uiState.value
        assertEquals(1, state.filteredApps.size)
        assertEquals(appA, state.filteredApps.first())
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
