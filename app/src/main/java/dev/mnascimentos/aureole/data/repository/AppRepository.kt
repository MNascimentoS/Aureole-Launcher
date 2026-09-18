package dev.mnascimentos.aureole.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dev.mnascimentos.aureole.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
        val ownPackageName = context.packageName

        resolveInfoList.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            // Don't show the launcher itself in the app drawer list
            if (packageName == ownPackageName) return@mapNotNull null

            val label = resolveInfo.loadLabel(packageManager).toString()
            val componentName = ComponentName(packageName, resolveInfo.activityInfo.name)
            val icon = resolveInfo.loadIcon(packageManager)

            AppInfo(
                label = label,
                packageName = packageName,
                componentName = componentName,
                icon = icon
            )
        }.sortedBy { it.label.lowercase() }
    }

    fun launchApp(componentName: ComponentName) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = componentName
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        context.startActivity(intent)
    }
}
