package dev.mnascimentos.aureole.core.lifecycle

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.InstallStatus
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.extensions.checkAppUpdate

class UpdateLifecycleObserver(
    private val appUpdateManager: AppUpdateManager,
    private val viewModel: HomeViewModel,
    private val onAppUpdateInfoRetrieved: (AppUpdateInfo) -> Unit
) : DefaultLifecycleObserver {

    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING,
            InstallStatus.PENDING,
            InstallStatus.INSTALLING -> {
                viewModel.isUpdateInProgressOrDismissed = true
                viewModel.setShowUpdateAvailableDialog(visible = false)
            }
            InstallStatus.DOWNLOADED -> {
                viewModel.setShowUpdateAvailableDialog(visible = false)
                viewModel.setShowUpdateDownloadedDialog(visible = true)
            }
            else -> {}
        }
    }

    override fun onCreate(owner: LifecycleOwner) {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    override fun onResume(owner: LifecycleOwner) {
        checkAppUpdate(appUpdateManager, viewModel, onAppUpdateInfoRetrieved)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        appUpdateManager.unregisterListener(installStateUpdatedListener)
    }
}
