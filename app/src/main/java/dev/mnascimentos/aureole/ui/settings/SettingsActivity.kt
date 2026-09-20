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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.mnascimentos.aureole.ui.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.ui.theme.AureoleLauncherTheme

class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AureoleLauncherTheme {
                val uiState by viewModel.uiState.collectAsState()

                LaunchedEffect(uiState.shouldFinishActivity) {
                    if (uiState.shouldFinishActivity) {
                        viewModel.onActivityFinishedHandled()
                        finish()
                    }
                }

                val photoPickerLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia()
                ) { uri ->
                    if (uri != null) {
                        viewModel.setCustomWallpaper(uri)
                    }
                }

                SettingsScreen(
                    uiState = uiState,
                    actions = SettingsScreenActions(
                        onBackClick = { finish() },
                        onSetDefaultLauncherClick = { openDefaultLauncherSettings() },
                        onOpenFavoritePickerClick = { viewModel.setShowFavoritePicker(true) },
                        onToggleShowAllAppsOnHome = { viewModel.toggleShowAllAppsOnHome() },
                        onToggleHomeButtonOpensAllApps = { viewModel.toggleHomeOpensAllApps() },
                        onToggleSidePanel = { viewModel.toggleSidePanel() },
                        onOpenSidePanelPositionDialog = { viewModel.setShowSidePanelPositionDialog(true) },
                        onToggleLeftHandedMode = { viewModel.toggleLeftHandedMode() },
                        onSidePanelPositionSelected = { position -> viewModel.setSidePanelPosition(position) },
                        onDismissSidePanelPositionDialog = { viewModel.setShowSidePanelPositionDialog(false) },
                        onChangeWallpaperClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        onRestoreDefaultWallpaperClick = { viewModel.setShowRestoreWallpaperDialog(true) },
                        onConfirmRestoreWallpaper = { viewModel.restoreDefaultWallpaper() },
                        onDismissRestoreWallpaperDialog = { viewModel.setShowRestoreWallpaperDialog(false) },
                        onClearErrorMessage = { viewModel.clearErrorMessage() },
                        onAddFolderClick = { viewModel.setShowCreateFolderDialog(true) },
                        onDismissCreateFolderDialog = { viewModel.setShowCreateFolderDialog(false) },
                        onSubmitCreateFolder = { folderName -> viewModel.createFolder(folderName) }
                    )
                )

                if (uiState.showFavoritePickerDialog) {
                    FavoriteAppsDialog(
                        allApps = uiState.allApps,
                        favoriteAppPackages = uiState.favoriteAppPackages,
                        onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
                        onDismiss = { viewModel.setShowFavoritePicker(false) }
                    )
                }
            }
        }
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
