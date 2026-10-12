package dev.mnascimentos.aureole.feature.home.extensions

import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun checkAppUpdate(
    appUpdateManager: AppUpdateManager,
    viewModel: HomeViewModel,
    onAppUpdateInfoRetrieved: (AppUpdateInfo) -> Unit,
) {
    if (!viewModel.uiState.value.isInAppUpdateEnabled) return

    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        onAppUpdateInfoRetrieved(appUpdateInfo)

        val installStatus = appUpdateInfo.installStatus()
        val isUpdateInProgress = installStatus == InstallStatus.DOWNLOADING ||
            installStatus == InstallStatus.PENDING ||
            installStatus == InstallStatus.INSTALLING ||
            appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS

        val isUpdateAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
        val isAllowedType = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) ||
            appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

        if (installStatus == InstallStatus.DOWNLOADED) {
            viewModel.setShowUpdateAvailableDialog(visible = false)
            viewModel.setShowUpdateDownloadedDialog(visible = true)
        } else if (isUpdateInProgress) {
            viewModel.isUpdateInProgressOrDismissed = true
            viewModel.setShowUpdateAvailableDialog(visible = false)
        } else if (isUpdateAvailable && isAllowedType) {
            if (!viewModel.isUpdateInProgressOrDismissed) {
                viewModel.setShowUpdateAvailableDialog(visible = true)
            }
        }
    }.addOnFailureListener { e ->
        Log.w("MainActivity", "Failed to check for app update", e)
    }
}
