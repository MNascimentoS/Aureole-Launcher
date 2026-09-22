package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.ui.graphics.vector.ImageVector

data class PreferenceItemConfig(
    val title: String,
    val subtitle: String? = null,
    val leadingIcon: ImageVector? = null,
    val enabled: Boolean = true
)

data class SettingsScreenActions(
    val onBackClick: () -> Unit = {},
    val onSetDefaultLauncherClick: () -> Unit = {},
    val onOpenFavoritePickerClick: () -> Unit = {},
    val onToggleShowAllAppsOnHome: () -> Unit = {},
    val onToggleHomeButtonOpensAllApps: () -> Unit = {},
    val onToggleWidgetRow: () -> Unit = {},
    val onToggleSidePanel: () -> Unit = {},
    val onToggleSidePanelBackground: () -> Unit = {},
    val onToggleClockBackground: () -> Unit = {},
    val onToggleShowSidePanelAddFolderButton: () -> Unit = {},
    val onToggleShowFolderLabels: () -> Unit = {},
    val onToggleLeftHandedMode: () -> Unit = {},
    val onToggleDynamicWallpaper: () -> Unit = {},
    val onOpenColorPickerDialog: () -> Unit = {},
    val onSelectManualSeedColor: (Int) -> Unit = {},
    val onDismissColorPickerDialog: () -> Unit = {},
    val onChangeWallpaperClick: () -> Unit = {},
    val onRestoreDefaultWallpaperClick: () -> Unit = {},
    val onConfirmRestoreWallpaper: () -> Unit = {},
    val onDismissRestoreWallpaperDialog: () -> Unit = {},
    val onClearErrorMessage: () -> Unit = {},
    val onAddFolderClick: () -> Unit = {},
    val onDismissCreateFolderDialog: () -> Unit = {},
    val onSubmitCreateFolder: (String) -> Unit = {},
    val onToggleHaze: () -> Unit = {},
    val onOpenHazeOpacityDialog: () -> Unit = {},
    val onHazeOpacitySelected: (Float) -> Unit = {},
    val onDismissHazeOpacityDialog: () -> Unit = {},
    val onToggleShowWidgetDots: () -> Unit = {},
    val onToggleInAppUpdate: () -> Unit = {},
    val onToggleThemedAppIcons: () -> Unit = {},
    val onToggleDisableAlphabetScrubber: () -> Unit = {},
    val onOpenResetGridDialog: () -> Unit = {},
    val onConfirmResetGrid: () -> Unit = {},
    val onDismissResetGridDialog: () -> Unit = {},
    val onOpenFactoryResetDialog: () -> Unit = {},
    val onConfirmFactoryReset: () -> Unit = {},
    val onDismissFactoryResetDialog: () -> Unit = {},
)
