package dev.mnascimentos.aureole.feature.settings

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
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
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.FavoriteAppsDialog
import dev.mnascimentos.aureole.feature.settings.ext.checkDefaultLauncher
import dev.mnascimentos.aureole.feature.settings.ext.restoreDefaultWallpaper
import dev.mnascimentos.aureole.feature.settings.model.SettingValue
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions

class SettingsActivity : ComponentActivity() {

    private val viewModel: SettingsViewModel by viewModels()

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
                        onDismiss = {
                            viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, visible = false)
                        },
                    )
                }
            }
        }
    }

    private fun buildSettingsScreenActions(
        photoPickerLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    ): SettingsScreenActions {
        val baseActions = SettingsScreenActions(
            onBackClick = { finish() },
            onSetDefaultLauncherClick = { openDefaultLauncherSettings() },
            onChangeWallpaperClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            onSubmitCreateFolder = viewModel::createFolder,
            onConfirmRestoreWallpaper = { viewModel.restoreDefaultWallpaper() },
            onClearErrorMessage = { viewModel.clearErrorMessage() },
            onSidePanelPositionSelected = { pos ->
                viewModel.setSettingValue(SettingValue.SidePanelPosition(pos))
            },
            onSelectManualSeedColor = { color ->
                viewModel.setSettingValue(SettingValue.ManualSeedColor(color))
            },
            onHazeOpacitySelected = { opacity ->
                viewModel.setSettingValue(SettingValue.HazeOpacity(opacity))
            },
            onConfirmResetGrid = { viewModel.resetGridLayout() },
        )
        return applyTogglesAndDialogs(baseActions)
    }

    private fun applyTogglesAndDialogs(base: SettingsScreenActions): SettingsScreenActions {
        return base.copy(
            onToggleShowAllAppsOnHome = { viewModel.toggleSetting(SettingToggle.SHOW_ALL_APPS_ON_HOME) },
            onToggleHomeButtonOpensAllApps = { viewModel.toggleSetting(SettingToggle.HOME_OPENS_ALL_APPS) },
            onToggleWidgetRow = { viewModel.toggleSetting(SettingToggle.WIDGET_ROW) },
            onToggleShowWidgetDots = { viewModel.toggleSetting(SettingToggle.SHOW_WIDGET_DOTS) },
            onToggleSidePanel = { viewModel.toggleSetting(SettingToggle.SIDE_PANEL) },
            onToggleShowFolderLabels = { viewModel.toggleSetting(SettingToggle.SHOW_FOLDER_LABELS) },
            onToggleLeftHandedMode = { viewModel.toggleSetting(SettingToggle.LEFT_HANDED_MODE) },
            onToggleDynamicWallpaper = { viewModel.toggleSetting(SettingToggle.DYNAMIC_WALLPAPER) },
            onToggleHaze = { viewModel.toggleSetting(SettingToggle.HAZE) },
            onToggleInAppUpdate = { viewModel.toggleSetting(SettingToggle.IN_APP_UPDATE) },
            onToggleThemedAppIcons = { viewModel.toggleSetting(SettingToggle.THEMED_APP_ICONS) },
            onToggleDisableAlphabetScrubber = { viewModel.toggleSetting(SettingToggle.DISABLE_ALPHABET_SCRUBBER) },
            onOpenResetGridDialog = {
                viewModel.setDialogVisible(SettingsDialog.RESET_GRID, visible = true)
            },
            onDismissResetGridDialog = {
                viewModel.setDialogVisible(SettingsDialog.RESET_GRID, visible = false)
            },
            onOpenFavoritePickerClick = {
                viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, visible = true)
            },
            onOpenSidePanelPositionDialog = {
                viewModel.setDialogVisible(SettingsDialog.SIDE_PANEL_POSITION, visible = true)
            },
            onDismissSidePanelPositionDialog = {
                viewModel.setDialogVisible(SettingsDialog.SIDE_PANEL_POSITION, visible = false)
            },
            onOpenColorPickerDialog = {
                viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, visible = true)
            },
            onDismissColorPickerDialog = {
                viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, visible = false)
            },
            onRestoreDefaultWallpaperClick = {
                viewModel.setDialogVisible(SettingsDialog.RESTORE_WALLPAPER, visible = true)
            },
            onDismissRestoreWallpaperDialog = {
                viewModel.setDialogVisible(SettingsDialog.RESTORE_WALLPAPER, visible = false)
            },
            onAddFolderClick = {
                viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, visible = true)
            },
            onDismissCreateFolderDialog = {
                viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, visible = false)
            },
            onOpenHazeOpacityDialog = {
                viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, visible = true)
            },
            onDismissHazeOpacityDialog = {
                viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, visible = false)
            }
        )
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkDefaultLauncher()
        viewModel.loadSettings()
    }

    private fun openDefaultLauncherSettings() {
        if (!tryLaunchIntent(Intent(Settings.ACTION_HOME_SETTINGS)) &&
            !tryLaunchIntent(Intent("android.settings.MANAGE_DEFAULT_APPS_SETTINGS"))
        ) {
            tryLaunchIntent(Intent(Settings.ACTION_SETTINGS))
        }
    }

    private fun tryLaunchIntent(intent: Intent): Boolean {
        return try {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) {
            Log.w(TAG, "Activity not found for intent action: ${intent.action}", e)
            false
        } catch (e: SecurityException) {
            Log.w(TAG, "SecurityException launching intent action: ${intent.action}", e)
            false
        }
    }

    companion object {
        private const val TAG = "SettingsActivity"
    }
}
