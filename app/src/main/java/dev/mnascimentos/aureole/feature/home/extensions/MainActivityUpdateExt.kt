package dev.mnascimentos.aureole.feature.home.extensions

import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import dev.mnascimentos.aureole.MainActivity
import dev.mnascimentos.aureole.feature.home.HomeViewModel

fun MainActivity.checkAppUpdate(
    appUpdateManager: AppUpdateManager,
    viewModel: HomeViewModel,
    onAppUpdateInfoRetrieved: (AppUpdateInfo) -> Unit,
) {
    if (!viewModel.uiState.value.isInAppUpdateEnabled) return

    appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
        onAppUpdateInfoRetrieved(appUpdateInfo)
        if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
            viewModel.setShowUpdateDownloadedDialog(visible = true)
        } else if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
            (
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE) ||
                    appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
                )
        ) {
            viewModel.setShowUpdateAvailableDialog(visible = true)
        }
    }.addOnFailureListener { e ->
        Log.w("MainActivity", "Failed to check for app update", e)
    }
}
