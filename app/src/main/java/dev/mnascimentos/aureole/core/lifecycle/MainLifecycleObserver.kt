package dev.mnascimentos.aureole.core.lifecycle

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.os.Build
import androidx.core.content.pm.PackageInfoCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import dev.mnascimentos.aureole.feature.home.HomeViewModel
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onHomeHidden
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.onReturnedToHome
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.syncOnboarding
import dev.mnascimentos.aureole.feature.home.components.overlays.extensions.checkReleaseNotes
import dev.mnascimentos.aureole.feature.home.grid.extensions.loadGridItems

class MainLifecycleObserver(
    private val context: Context,
    private val viewModel: HomeViewModel,
    private val onResumed: () -> Unit,
) : DefaultLifecycleObserver {

    override fun onResume(owner: LifecycleOwner) {
        viewModel.loadSettings()
        viewModel.loadApps()
        viewModel.loadGridItems(isLandscape = isLandscapeOrientation(context.resources.configuration.orientation))
        val versionCode = getAppVersionCode(context)
        val versionName = getAppVersionName(context)
        // Before release notes: they stay hidden while the onboarding runs.
        viewModel.syncOnboarding()
        viewModel.onReturnedToHome()
        viewModel.checkReleaseNotes(versionCode, versionName)
        onResumed()
    }

    /** Rotation only reloads the grid, and only when the orientation really changed; settings and apps stay. */
    fun onConfigurationChanged(newConfig: Configuration) {
        val target = landscapeChange(viewModel.uiState.value.isLandscape, newConfig.orientation) ?: return
        viewModel.loadGridItems(isLandscape = target)
    }

    override fun onPause(owner: LifecycleOwner) {
        viewModel.onHomeHidden()
    }
}

private fun getAppVersionCode(context: Context): Int {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        PackageInfoCompat.getLongVersionCode(packageInfo).toInt()
    } catch (_: Exception) {
        1
    }
}

private fun getAppVersionName(context: Context): String {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName ?: "0.3.13"
    } catch (_: Exception) {
        "0.3.13"
    }
}
