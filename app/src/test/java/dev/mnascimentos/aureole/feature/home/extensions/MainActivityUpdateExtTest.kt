package dev.mnascimentos.aureole.feature.home.extensions

import android.app.Activity
import android.content.Context
import android.os.Looper
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
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
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.job
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.Shadows.shadowOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class MainActivityUpdateExtTest {

    private lateinit var context: Context
    private lateinit var viewModel: HomeViewModel
    private lateinit var manager: FakeAppUpdateManager
    private var retrieved: AppUpdateInfo? = null

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
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
        viewModel.updateUiState { it.copy(isInAppUpdateEnabled = true) }
        manager = FakeAppUpdateManager(context)
        retrieved = null
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    private fun check() {
        checkAppUpdate(manager, viewModel) { retrieved = it }
        shadowOf(Looper.getMainLooper()).idle()
    }

    private fun startFlexibleUpdate() {
        manager.setUpdateAvailable(NEW_VERSION, AppUpdateType.FLEXIBLE)
        val info = manager.appUpdateInfo.result
        val activity: Activity = Robolectric.buildActivity(Activity::class.java).setup().get()
        manager.startUpdateFlowForResult(info, activity, AppUpdateOptions.defaultOptions(AppUpdateType.FLEXIBLE), 1)
        manager.userAcceptsUpdate()
        manager.downloadStarts()
    }

    @Test
    fun `when in-app updates are disabled, should not ask the store`() {
        viewModel.updateUiState { it.copy(isInAppUpdateEnabled = false) }
        manager.setUpdateAvailable(NEW_VERSION, AppUpdateType.FLEXIBLE)

        check()

        assertNull(retrieved)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    @Test
    fun `when no update is available, should not show any dialog`() {
        manager.setUpdateNotAvailable()

        check()

        assertNotNull(retrieved)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
        assertFalse(viewModel.uiState.value.showUpdateDownloadedDialog)
    }

    @Test
    fun `when an allowed update is available, should offer it`() {
        manager.setUpdateAvailable(NEW_VERSION, AppUpdateType.FLEXIBLE)

        check()

        assertTrue(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    @Test
    fun `when an immediate-only update is available, should still offer it`() {
        manager.setUpdateAvailable(NEW_VERSION, AppUpdateType.IMMEDIATE)

        check()

        assertTrue(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    @Test
    fun `when the user already dismissed the update, should not offer it again`() {
        viewModel.isUpdateInProgressOrDismissed = true
        manager.setUpdateAvailable(NEW_VERSION, AppUpdateType.FLEXIBLE)

        check()

        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    @Test
    fun `when an update is downloading, should mark it in progress and hide the offer`() {
        startFlexibleUpdate()
        viewModel.setShowUpdateAvailableDialog(visible = true)

        check()

        assertTrue(viewModel.isUpdateInProgressOrDismissed)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    @Test
    fun `when an update finished downloading, should ask to restart`() {
        startFlexibleUpdate()
        manager.downloadCompletes()

        check()

        assertTrue(viewModel.uiState.value.showUpdateDownloadedDialog)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
    }

    private companion object {
        const val NEW_VERSION = 999
    }
}
