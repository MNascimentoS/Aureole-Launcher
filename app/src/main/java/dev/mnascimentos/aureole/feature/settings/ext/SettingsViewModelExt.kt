package dev.mnascimentos.aureole.feature.settings.ext

import android.app.Application
import android.app.WallpaperManager
import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteException
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.repository.GridRepository
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException

private const val TAG = "SettingsViewModelExt"

fun SettingsViewModel.checkDefaultLauncher() {
    val context = getApplication<Application>()
    val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val roleManager = context.getSystemService(RoleManager::class.java)
        roleManager?.isRoleHeld(RoleManager.ROLE_HOME) ?: false
    } else {
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
        val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
        resolveInfo?.activityInfo?.packageName == context.packageName
    }
    updateUiState { it.copy(isDefaultLauncher = isDefault) }
}

fun SettingsViewModel.restoreDefaultWallpaper() {
    viewModelScope.launch {
        val app = getApplication<Application>()
        withContext(Dispatchers.IO) {
            settingsRepository.clearCustomWallpaper()
            try {
                val wallpaperManager = WallpaperManager.getInstance(app)
                wallpaperManager.clear()
            } catch (e: IOException) {
                Log.w("SettingsViewModel", "IOException clearing wallpaper", e)
            } catch (e: SecurityException) {
                Log.w("SettingsViewModel", "SecurityException clearing wallpaper", e)
            }
        }
        updateUiState {
            it.copy(
                isCustomWallpaperSet = false,
                customWallpaperPath = null,
                showRestoreWallpaperDialog = false
            )
        }
    }
}

fun SettingsViewModel.resetGridLayout() {
    viewModelScope.launch {
        GridRepository(getApplication()).resetToDefault()
        updateUiState {
            it.copy(
                showResetGridDialog = false,
                shouldFinishActivity = true,
                errorMessage = "Layout da tela inicial resetado para o padrão"
            )
        }
    }
}

fun SettingsViewModel.performFactoryReset() {
    viewModelScope.launch {
        val app = getApplication<Application>()
        updateUiState { it.copy(showFactoryResetDialog = false) }
        withContext(Dispatchers.IO) {
            try {
                try {
                    AppDatabase.getInstance(app).clearAllTables()
                } catch (e: SQLiteException) {
                    Log.w(TAG, "Error clearing Room database", e)
                } catch (e: IllegalStateException) {
                    Log.w(TAG, "Error clearing Room database state", e)
                }
                app.deleteDatabase("aureole_app_database.db")
                app.deleteDatabase("aureole_folders.db")

                listOf(
                    "aureole_grid_prefs",
                    "aureole_settings_prefs",
                    "aureole_widget_prefs",
                    "${app.packageName}_preferences"
                ).forEach { prefName ->
                    app.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().commit()
                }

                app.cacheDir.deleteRecursively()
                File(app.filesDir, "custom_wallpaper.jpg").delete()
            } catch (e: IOException) {
                Log.w(TAG, "Error performing factory reset IO", e)
            } catch (e: SecurityException) {
                Log.w(TAG, "SecurityException performing factory reset", e)
            }
        }

        val intent = app.packageManager.getLaunchIntentForPackage(app.packageName)?.apply {
            addFlags(
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            )
        }
        if (intent != null) {
            app.startActivity(intent)
        }
        Runtime.getRuntime().exit(0)
    }
}
