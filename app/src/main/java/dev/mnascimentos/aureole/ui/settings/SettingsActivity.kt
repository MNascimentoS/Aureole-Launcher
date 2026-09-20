package dev.mnascimentos.aureole.ui.settings

import android.app.role.RoleManager
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.ui.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.ui.theme.AureoleLauncherTheme

class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    @Suppress("LongMethod")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val uiState by viewModel.uiState.collectAsState()

            AureoleLauncherTheme(
                isDynamicWallpaperEnabled = uiState.isDynamicWallpaperEnabled,
                seedColor = Color(uiState.manualSeedColor),
            ) {
                LaunchedEffect(uiState.shouldFinishActivity) {
                    if (uiState.shouldFinishActivity) {
                        viewModel.onActivityFinishedHandled()
                        finish()
                    }
                }

                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                ) { uri ->
                    uri?.let { viewModel.setCustomWallpaper(it) }
                }

                SettingsScreen(
                    uiState = uiState,
                    actions = buildSettingsScreenActions(photoPickerLauncher),
                )

                if (uiState.showFavoritePickerDialog) {
                    FavoriteAppsDialog(
                        allApps = uiState.allApps,
                        favoriteAppPackages = uiState.favoriteAppPackages,
                        onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
                        onDismiss = { viewModel.setShowFavoritePicker(show = false) },
                    )
                }
            }
        }
    }

    private fun buildSettingsScreenActions(
        photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    ): SettingsScreenActions {
        return SettingsScreenActions(
            onBackClick = { finish() },
            onSetDefaultLauncherClick = { openDefaultLauncherSettings() },
            onOpenFavoritePickerClick = { viewModel.setShowFavoritePicker(show = true) },
            onToggleShowAllAppsOnHome = { viewModel.toggleShowAllAppsOnHome() },
            onToggleHomeButtonOpensAllApps = { viewModel.toggleHomeOpensAllApps() },
            onToggleWidgetRow = { viewModel.toggleWidgetRow() },
            onToggleSidePanel = { viewModel.toggleSidePanel() },
            onToggleShowFolderLabels = { viewModel.toggleShowFolderLabels() },
            onOpenSidePanelPositionDialog = { viewModel.setShowSidePanelPositionDialog(show = true) },
            onToggleLeftHandedMode = { viewModel.toggleLeftHandedMode() },
            onSidePanelPositionSelected = { position -> viewModel.setSidePanelPosition(position) },
            onDismissSidePanelPositionDialog = { viewModel.setShowSidePanelPositionDialog(show = false) },
            onToggleDynamicWallpaper = { viewModel.toggleDynamicWallpaper() },
            onOpenColorPickerDialog = { viewModel.setShowColorPickerDialog(show = true) },
            onSelectManualSeedColor = { color -> viewModel.setManualSeedColor(color) },
            onDismissColorPickerDialog = { viewModel.setShowColorPickerDialog(show = false) },
            onChangeWallpaperClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            onRestoreDefaultWallpaperClick = { viewModel.setShowRestoreWallpaperDialog(show = true) },
            onConfirmRestoreWallpaper = { viewModel.restoreDefaultWallpaper() },
            onDismissRestoreWallpaperDialog = { viewModel.setShowRestoreWallpaperDialog(show = false) },
            onClearErrorMessage = { viewModel.clearErrorMessage() },
            onAddFolderClick = { viewModel.setShowCreateFolderDialog(show = true) },
            onDismissCreateFolderDialog = { viewModel.setShowCreateFolderDialog(show = false) },
            onSubmitCreateFolder = viewModel::createFolder,
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkDefaultLauncher()
        viewModel.loadSettings()
    }

    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    private fun openDefaultLauncherSettings() {
        try {
            val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val roleManager = getSystemService(RoleManager::class.java)
                if (roleManager != null && roleManager.isRoleAvailable(RoleManager.ROLE_HOME)) {
                    roleManager.createRequestRoleIntent(RoleManager.ROLE_HOME)
                } else {
                    Intent(Settings.ACTION_HOME_SETTINGS)
                }
            } else {
                Intent(Settings.ACTION_HOME_SETTINGS)
            }
            startActivity(intent)
        } catch (_: Exception) {
            try {
                startActivity(Intent(Settings.ACTION_HOME_SETTINGS))
            } catch (_: Exception) {}
        }
    }
}
