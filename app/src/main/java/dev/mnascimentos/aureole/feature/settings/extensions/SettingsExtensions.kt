package dev.mnascimentos.aureole.feature.settings.extensions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

fun getVersionName(context: Context): String {
    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        packageInfo.versionName ?: "1.0.0"
    } catch (_: Throwable) {
        "1.0.0"
    }
}
