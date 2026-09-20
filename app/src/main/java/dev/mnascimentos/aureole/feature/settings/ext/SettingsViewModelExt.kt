package dev.mnascimentos.aureole.feature.settings.ext

import android.app.Application
import android.app.WallpaperManager
import android.app.role.RoleManager
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.viewModelScope
import dev.mnascimentos.aureole.feature.settings.SettingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

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
