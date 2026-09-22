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
import dev.mnascimentos.aureole.feature.home.components.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.settings.ext.checkDefaultLauncher
import dev.mnascimentos.aureole.feature.settings.ext.performFactoryReset
import dev.mnascimentos.aureole.feature.settings.ext.resetGridLayout
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
                        config = FavoriteAppsDialogConfig(
                            allApps = uiState.allApps,
                            favoriteAppPackages = uiState.favoriteAppPackages
                        ),
                        actions = FavoriteAppsDialogActions(
                            onToggleFavorite = { pkg -> viewModel.toggleFavorite(pkg) },
                            onDismiss = {
                                viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, visible = false)
                            }
                        )
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
        val withToggles = applyToggleActions(base)
        return applyDialogActions(withToggles)
    }

    private fun applyToggleActions(base: SettingsScreenActions): SettingsScreenActions {
        return base.copy(
            onToggleShowAllAppsOnHome = { viewModel.toggleSetting(SettingToggle.SHOW_ALL_APPS_ON_HOME) },
            onToggleHomeButtonOpensAllApps = { viewModel.toggleSetting(SettingToggle.HOME_OPENS_ALL_APPS) },
            onToggleWidgetRow = { viewModel.toggleSetting(SettingToggle.WIDGET_ROW) },
            onToggleShowWidgetDots = { viewModel.toggleSetting(SettingToggle.SHOW_WIDGET_DOTS) },
            onToggleSidePanelBackground = { viewModel.toggleSetting(SettingToggle.SIDE_PANEL_BACKGROUND) },
            onToggleSidePanelExpandCell = { viewModel.toggleSetting(SettingToggle.SIDE_PANEL_EXPAND_CELL) },
            onToggleClockBackground = { viewModel.toggleSetting(SettingToggle.CLOCK_BACKGROUND) },
            onToggleShowSidePanelAddFolderButton = {
                viewModel.toggleSetting(SettingToggle.SHOW_SIDE_PANEL_ADD_FOLDER_BUTTON)
            },
            onToggleShowFolderLabels = { viewModel.toggleSetting(SettingToggle.SHOW_FOLDER_LABELS) },
            onToggleLeftHandedMode = { viewModel.toggleSetting(SettingToggle.LEFT_HANDED_MODE) },
            onToggleDynamicWallpaper = { viewModel.toggleSetting(SettingToggle.DYNAMIC_WALLPAPER) },
            onToggleHaze = { viewModel.toggleSetting(SettingToggle.HAZE) },
            onToggleInAppUpdate = { viewModel.toggleSetting(SettingToggle.IN_APP_UPDATE) },
            onToggleThemedAppIcons = { viewModel.toggleSetting(SettingToggle.THEMED_APP_ICONS) },
            onToggleDisableAlphabetScrubber = { viewModel.toggleSetting(SettingToggle.DISABLE_ALPHABET_SCRUBBER) },
            onToggleShowSettingsButtonInAllApps = {
                viewModel.toggleSetting(
                    SettingToggle.SHOW_SETTINGS_BUTTON_IN_ALL_APPS
                )
            },
            onToggleShowSearchBarInAllApps = { viewModel.toggleSetting(SettingToggle.SHOW_SEARCH_BAR_IN_ALL_APPS) }
        )
    }

    private fun applyDialogActions(base: SettingsScreenActions): SettingsScreenActions {
        return base.copy(
            onOpenSidePanelPositionDialog = { viewModel.setDialogVisible(SettingsDialog.SIDE_PANEL_POSITION, true) },
            onDismissSidePanelPositionDialog = {
                viewModel.setDialogVisible(
                    SettingsDialog.SIDE_PANEL_POSITION,
                    false
                )
            },
            onSidePanelPositionSelected = { pos -> viewModel.setSettingValue(SettingValue.SidePanelPosition(pos)) },
            onOpenResetGridDialog = { viewModel.setDialogVisible(SettingsDialog.RESET_GRID, true) },
            onDismissResetGridDialog = { viewModel.setDialogVisible(SettingsDialog.RESET_GRID, false) },
            onOpenFactoryResetDialog = { viewModel.setDialogVisible(SettingsDialog.FACTORY_RESET, true) },
            onDismissFactoryResetDialog = { viewModel.setDialogVisible(SettingsDialog.FACTORY_RESET, false) },
            onConfirmFactoryReset = { viewModel.performFactoryReset() },
            onOpenFavoritePickerClick = { viewModel.setDialogVisible(SettingsDialog.FAVORITE_PICKER, true) },
            onOpenColorPickerDialog = { viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, true) },
            onDismissColorPickerDialog = { viewModel.setDialogVisible(SettingsDialog.COLOR_PICKER, false) },
            onRestoreDefaultWallpaperClick = { viewModel.setDialogVisible(SettingsDialog.RESTORE_WALLPAPER, true) },
            onDismissRestoreWallpaperDialog = { viewModel.setDialogVisible(SettingsDialog.RESTORE_WALLPAPER, false) },
            onAddFolderClick = { viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, true) },
            onDismissCreateFolderDialog = { viewModel.setDialogVisible(SettingsDialog.CREATE_FOLDER, false) },
            onOpenHazeOpacityDialog = { viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, true) },
            onDismissHazeOpacityDialog = { viewModel.setDialogVisible(SettingsDialog.HAZE_OPACITY, false) },
            onOpenSettingsButtonPositionDialog = {
                viewModel.setDialogVisible(
                    SettingsDialog.SETTINGS_BUTTON_POSITION,
                    true
                )
            },
            onDismissSettingsButtonPositionDialog = {
                viewModel.setDialogVisible(
                    SettingsDialog.SETTINGS_BUTTON_POSITION,
                    false
                )
            },
            onSettingsButtonPositionSelected = { pos ->
                viewModel.setSettingValue(
                    SettingValue.SettingsButtonPosition(pos)
                )
            },
            onOpenSearchIconPositionDialog = { viewModel.setDialogVisible(SettingsDialog.SEARCH_ICON_POSITION, true) },
            onDismissSearchIconPositionDialog = {
                viewModel.setDialogVisible(
                    SettingsDialog.SEARCH_ICON_POSITION,
                    false
                )
            },
            onSearchIconPositionSelected = { pos -> viewModel.setSettingValue(SettingValue.SearchIconPosition(pos)) }
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
