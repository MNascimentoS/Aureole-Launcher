package dev.mnascimentos.aureole.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import dev.mnascimentos.aureole.feature.settings.model.SettingsUpdateRing
import dev.mnascimentos.aureole.feature.settings.model.settingsUpdateRingFor
import timber.log.Timber

/**
 * Feeds the ring around the logo of the Settings root (RF-13): it spins while the update check runs and follows the
 * real download of the in-app update. Nothing is shown with "Check for updates" off.
 */
class SettingsUpdateRingObserver(
    private val appUpdateManager: AppUpdateManager,
    private val viewModel: SettingsViewModel
) : DefaultLifecycleObserver {

    private val isEnabled: Boolean
        get() = viewModel.uiState.value.isInAppUpdateEnabled

    private val installStateListener = InstallStateUpdatedListener { state ->
        if (isEnabled) {
            viewModel.setUpdateRing(
                settingsUpdateRingFor(state.installStatus(), state.bytesDownloaded(), state.totalBytesToDownload())
            )
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        appUpdateManager.registerListener(installStateListener)
    }

    override fun onResume(owner: LifecycleOwner) {
        if (!isEnabled) {
            viewModel.setUpdateRing(SettingsUpdateRing.Hidden)
            return
        }
        viewModel.setUpdateRing(SettingsUpdateRing.Checking)
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            viewModel.setUpdateRing(
                settingsUpdateRingFor(info.installStatus(), info.bytesDownloaded(), info.totalBytesToDownload())
            )
        }.addOnFailureListener { e ->
            Timber.tag("Settings").w(e, "Failed to check for app update")
            viewModel.setUpdateRing(SettingsUpdateRing.Hidden)
        }
    }

    override fun onDestroy(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(installStateListener)
    }
}
