package dev.mnascimentos.aureole.core.data.repository

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import dev.mnascimentos.aureole.core.data.cache.IconCache
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.db.AppInfoEntity
import dev.mnascimentos.aureole.core.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val dao = db.appInfoDao()
    private val iconCache = IconCache(context)

    val appsFlow: Flow<List<AppInfo>> = dao.getAppsFlow().map { entities ->
        entities.map { entity ->
            val componentName = ComponentName(entity.packageName, entity.activityName)
            val icon = iconCache.getIcon(entity.packageName, componentName)
            AppInfo(
                label = entity.label,
                packageName = entity.packageName,
                componentName = componentName,
                icon = icon,
            )
        }
    }.flowOn(Dispatchers.IO)

    suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        val entities = dao.getAllApps()
        if (entities.isNotEmpty()) {
            return@withContext entities.map { entity ->
                val component = ComponentName(entity.packageName, entity.activityName)
                AppInfo(
                    label = entity.label,
                    packageName = entity.packageName,
                    componentName = component,
                    icon = iconCache.getIcon(entity.packageName, component),
                )
            }
        }
        syncApps()
        dao.getAllApps().map { entity ->
            val component = ComponentName(entity.packageName, entity.activityName)
            AppInfo(
                label = entity.label,
                packageName = entity.packageName,
                componentName = component,
                icon = iconCache.getIcon(entity.packageName, component),
            )
        }
    }

    suspend fun syncApps() = withContext(Dispatchers.IO) {
        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
        val ownPackageName = context.packageName

        val entities = mutableListOf<AppInfoEntity>()
        val packageNames = mutableListOf<String>()

        for (resolveInfo in resolveInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName == ownPackageName) continue

            val label = resolveInfo.loadLabel(packageManager).toString()
            val activityName = resolveInfo.activityInfo.name

            val icon = resolveInfo.loadIcon(packageManager)
            iconCache.cacheIcon(packageName, icon)

            packageNames.add(packageName)
            entities.add(
                AppInfoEntity(
                    packageName = packageName,
                    label = label,
                    activityName = activityName,
                )
            )
        }

        dao.insertApps(entities)
        if (packageNames.isNotEmpty()) {
            dao.deleteAppsNotIn(packageNames)
        } else {
            dao.clearAll()
        }
    }

    suspend fun syncPackage(packageName: String) = withContext(Dispatchers.IO) {
        if (packageName == context.packageName) return@withContext

        val packageManager = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            `package` = packageName
        }

        val resolveInfoList = packageManager.queryIntentActivities(intent, 0)
        val resolveInfo = resolveInfoList.firstOrNull()

        if (resolveInfo != null) {
            val label = resolveInfo.loadLabel(packageManager).toString()
            val activityName = resolveInfo.activityInfo.name
            val icon = resolveInfo.loadIcon(packageManager)

            iconCache.cacheIcon(packageName, icon)
            val entity = AppInfoEntity(
                packageName = packageName,
                label = label,
                activityName = activityName,
            )
            dao.insertApp(entity)
        } else {
            removePackage(packageName)
        }
    }

    suspend fun removePackage(packageName: String) = withContext(Dispatchers.IO) {
        iconCache.removeIcon(packageName)
        dao.deleteApp(packageName)
    }

    fun launchApp(componentName: ComponentName) {
        val intent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
            component = componentName
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        }
        context.startActivity(intent)
    }

    companion object {
        @Volatile
        private var instance: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return instance ?: synchronized(this) {
                instance ?: AppRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
