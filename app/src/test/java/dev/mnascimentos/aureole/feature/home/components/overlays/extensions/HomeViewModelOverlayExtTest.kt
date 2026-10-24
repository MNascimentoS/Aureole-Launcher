package dev.mnascimentos.aureole.feature.home.components.overlays.extensions

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.model.AppFolder
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
import kotlinx.coroutines.job
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.shadows.ShadowLooper

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeViewModelOverlayExtTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        context = RuntimeEnvironment.getApplication()
        val settingsRepository = SettingsRepository(context)

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
        Dispatchers.resetMain()
    }

    private fun createApp(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "MainActivity"),
        icon = ColorDrawable(Color.BLACK)
    )

    @Test
    fun `openContainerAppBottomSheet and closeContainerAppBottomSheet update uiState correctly`() {
        val app = createApp("Test App", "com.test.app")
        val panelId = "panel_1"

        viewModel.openContainerAppBottomSheet(app, panelId)
        var state = viewModel.uiState.value
        assertNotNull(state.activeContainerAppBottomSheet)
        assertEquals(app, state.activeContainerAppBottomSheet?.app)
        assertEquals(panelId, state.activeContainerAppBottomSheet?.panelId)

        viewModel.closeContainerAppBottomSheet()
        state = viewModel.uiState.value
        assertNull(state.activeContainerAppBottomSheet)
    }

    @Test
    fun `openContainerFolderBottomSheet and closeContainerFolderBottomSheet update uiState correctly`() {
        val folder = AppFolder(id = "folder_1", name = "Folder 1")
        val panelId = "panel_1"

        viewModel.openContainerFolderBottomSheet(folder, panelId)
        var state = viewModel.uiState.value
        assertNotNull(state.activeContainerFolderBottomSheet)
        assertEquals(folder, state.activeContainerFolderBottomSheet?.folder)
        assertEquals(panelId, state.activeContainerFolderBottomSheet?.panelId)

        viewModel.closeContainerFolderBottomSheet()
        state = viewModel.uiState.value
        assertNull(state.activeContainerFolderBottomSheet)
    }

    @Test
    fun `openFolderAppBottomSheet and closeFolderAppBottomSheet update uiState correctly`() {
        val app = createApp("App In Folder", "com.test.folderapp")
        val folder = AppFolder(id = "folder_1", name = "Folder 1")

        viewModel.openFolderAppBottomSheet(app, folder)
        var state = viewModel.uiState.value
        assertNotNull(state.activeFolderAppBottomSheet)
        assertEquals(app, state.activeFolderAppBottomSheet?.app)
        assertEquals(folder, state.activeFolderAppBottomSheet?.folder)

        viewModel.closeFolderAppBottomSheet()
        state = viewModel.uiState.value
        assertNull(state.activeFolderAppBottomSheet)
    }

    @Test
    fun `openWidgetStackBottomSheet and closeWidgetStackBottomSheet update uiState correctly`() {
        val widgetId = 101
        val stackId = "stack_1"

        viewModel.openWidgetStackBottomSheet(widgetId, stackId)
        var state = viewModel.uiState.value
        assertNotNull(state.activeWidgetStackBottomSheet)
        assertEquals(widgetId, state.activeWidgetStackBottomSheet?.widgetId)
        assertEquals(stackId, state.activeWidgetStackBottomSheet?.stackId)

        viewModel.closeWidgetStackBottomSheet()
        state = viewModel.uiState.value
        assertNull(state.activeWidgetStackBottomSheet)
    }

    @Test
    fun `toggleWidgetStackDots toggles state and updates map`() {
        val stackId = "stack_123"

        // Repository default is true, so first call toggles to false
        viewModel.toggleWidgetStackDots(stackId)
        var state = viewModel.uiState.value
        assertEquals(false, state.widgetStackDots[stackId])

        // Second call toggles back to true
        viewModel.toggleWidgetStackDots(stackId)
        state = viewModel.uiState.value
        assertEquals(true, state.widgetStackDots[stackId])
    }

    @Test
    fun `openEditFolderForFolder sets active folder and isRenameFolderDialogVisible true`() {
        val folder = AppFolder(id = "f1", name = "My Folder")

        viewModel.openEditFolderForFolder(folder)
        val state = viewModel.uiState.value
        assertEquals(folder, state.activeFolder)
        assertTrue(state.isRenameFolderDialogVisible)
    }
}

private const val VIEW_MODEL_SHUTDOWN_MS = 2_000L
private const val SHUTDOWN_POLL_MS = 5L
