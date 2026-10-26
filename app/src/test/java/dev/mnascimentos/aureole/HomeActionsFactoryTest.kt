package dev.mnascimentos.aureole

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.provider.Settings
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.lifecycle.viewModelScope
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
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
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
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
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeActionsFactoryTest {

    private lateinit var activity: MainActivity
    private lateinit var viewModel: HomeViewModel
    private lateinit var appUpdateManager: FakeAppUpdateManager
    private val updateLauncher = RecordingLauncher()

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        activity = Robolectric.buildActivity(MainActivity::class.java).get()
        viewModel = createViewModel(activity)
        appUpdateManager = FakeAppUpdateManager(activity)
    }

    @After
    fun tearDown() {
        // Let the init's background loads finish before Dispatchers.Main is swapped back.
        runBlocking { viewModel.viewModelScope.coroutineContext.job.cancelAndJoin() }
        Dispatchers.resetMain()
    }

    @Test
    fun `when settings is clicked, should start SettingsActivity`() {
        actions().onSettingsClick()

        val started = shadowOf(activity).nextStartedActivity
        assertEquals(SettingsActivity::class.java.name, started.component?.className)
    }

    @Test
    fun `when an app without launch intent is clicked, should not start anything`() {
        actions().onAppClick(appInfo("com.not.installed"))

        assertNull(shadowOf(activity).nextStartedActivity)
    }

    @Test
    fun `when app info is requested, should open the system app details screen`() {
        actions().onAppInfoClick(appInfo("com.example.mail"))

        val started = shadowOf(activity).nextStartedActivity
        assertEquals(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, started.action)
        assertEquals("package:com.example.mail", started.dataString)
    }

    @Test
    fun `when uninstall is requested, should start the delete intent for the package`() {
        actions().onUninstallAppClick(appInfo("com.example.mail"))

        val started = shadowOf(activity).nextStartedActivity
        assertEquals(Intent.ACTION_DELETE, started.action)
        assertEquals("package:com.example.mail", started.dataString)
    }

    @Test
    fun `when add widget is clicked, should target the stack and open the widget picker`() {
        actions().onAddWidgetClick("stack_1")

        val state = viewModel.uiState.value
        assertEquals("stack_1", state.targetWidgetStackId)
        assertTrue(state.showWidgetPicker)
    }

    @Test
    fun `when the all apps drawer is opened and closed, should reflect it in state`() {
        val actions = actions()

        actions.onAllAppsDrawerOpen()
        assertTrue(viewModel.uiState.value.isAllAppsDrawerOpen)

        actions.onAllAppsDrawerClose()
        assertFalse(viewModel.uiState.value.isAllAppsDrawerOpen)
    }

    @Test
    fun `when customize, add container and favorite picker actions run, should toggle their sheets`() {
        val actions = actions()

        actions.onOpenCustomizeBottomSheet()
        actions.onOpenAddContainerDialog()
        actions.onOpenFavoritePicker("container_1")
        val opened = viewModel.uiState.value
        assertTrue(opened.showCustomizeBottomSheet)
        assertTrue(opened.showAddContainerDialog)
        assertTrue(opened.showFavoritePickerDialog)

        actions.onCloseCustomizeBottomSheet()
        actions.onCloseAddContainerDialog()
        val closed = viewModel.uiState.value
        assertFalse(closed.showCustomizeBottomSheet)
        assertFalse(closed.showAddContainerDialog)
    }

    @Test
    fun `when grid edit mode is entered and cancelled, should toggle edit mode`() {
        val actions = actions()

        actions.onEnterGridEditMode()
        assertTrue(viewModel.uiState.value.isGridEditMode)

        actions.onCancelGridEditMode()
        assertFalse(viewModel.uiState.value.isGridEditMode)
    }

    @Test
    fun `when the widget resize dialog is opened and closed, should toggle it`() {
        val actions = actions()

        actions.onOpenWidgetResizeDialog()
        assertTrue(viewModel.uiState.value.showWidgetResizeDialog)

        actions.onCloseWidgetResizeDialog()
        assertFalse(viewModel.uiState.value.showWidgetResizeDialog)
    }

    @Test
    fun `when the edit clock sheet is opened and closed, should toggle it`() {
        val actions = actions()

        actions.onOpenEditClockBottomSheet()
        assertTrue(viewModel.uiState.value.showEditClockBottomSheet)

        actions.onCloseEditClockBottomSheet()
        assertFalse(viewModel.uiState.value.showEditClockBottomSheet)
    }

    @Test
    fun `when the update dialog is dismissed, should hide update dialogs and stop asking`() {
        viewModel.setShowUpdateAvailableDialog(visible = true)
        viewModel.setShowUpdateDownloadedDialog(visible = true)

        actions().onDismissUpdateDialog()

        assertTrue(viewModel.isUpdateInProgressOrDismissed)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
        assertFalse(viewModel.uiState.value.showUpdateDownloadedDialog)
    }

    @Test
    fun `when starting an update without cached info, should only hide the dialog`() {
        viewModel.setShowUpdateAvailableDialog(visible = true)

        actions().onStartInAppUpdate()

        assertTrue(viewModel.isUpdateInProgressOrDismissed)
        assertFalse(viewModel.uiState.value.showUpdateAvailableDialog)
        assertFalse(appUpdateManager.isConfirmationDialogVisible)
    }

    @Test
    fun `when starting an update with cached info, should launch the update flow`() {
        appUpdateManager.setUpdateAvailable(UPDATE_VERSION_CODE, AppUpdateType.FLEXIBLE)
        val info = appUpdateManager.appUpdateInfo.result

        actions(cachedInfo = info).onStartInAppUpdate()

        assertTrue(appUpdateManager.isConfirmationDialogVisible)
        assertTrue(viewModel.isUpdateInProgressOrDismissed)
    }

    @Test
    fun `when completing a downloaded update, should hide the downloaded dialog`() {
        viewModel.setShowUpdateDownloadedDialog(visible = true)

        actions().onCompleteInAppUpdate()

        assertFalse(viewModel.uiState.value.showUpdateDownloadedDialog)
    }

    private fun actions(
        cachedInfo: com.google.android.play.core.appupdate.AppUpdateInfo? = null
    ): HomeScreenActions = HomeActionsFactory(
        context = activity,
        viewModel = viewModel,
        appUpdateManager = appUpdateManager,
        updateActivityResultLauncher = updateLauncher,
        cachedAppUpdateInfo = cachedInfo
    ).createHomeActions()

    private fun appInfo(packageName: String) = AppInfo(
        label = packageName,
        packageName = packageName,
        componentName = ComponentName(packageName, "$packageName.Main"),
        icon = ColorDrawable()
    )

    private fun createViewModel(context: Context): HomeViewModel {
        val settingsRepository = SettingsRepository(context)
        return HomeViewModel(
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
    }

    private class RecordingLauncher : ActivityResultLauncher<IntentSenderRequest>() {
        val launched = mutableListOf<IntentSenderRequest>()

        override val contract: ActivityResultContract<IntentSenderRequest, *> =
            ActivityResultContracts.StartIntentSenderForResult()

        override fun launch(input: IntentSenderRequest, options: ActivityOptionsCompat?) {
            launched += input
        }

        override fun unregister() = Unit
    }

    private companion object {
        const val UPDATE_VERSION_CODE = 999
    }
}
