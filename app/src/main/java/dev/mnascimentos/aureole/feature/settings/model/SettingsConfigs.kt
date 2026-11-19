package dev.mnascimentos.aureole.feature.settings.model

import androidx.compose.ui.graphics.vector.ImageVector
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments

data class PreferenceItemConfig(
    val title: String,
    val subtitle: String? = null,
    val leadingIcon: ImageVector? = null,
    val enabled: Boolean = true
)

/**
 * Navigation and sheet actions of the Advanced sections.
 */
data class SettingsAdvancedDialogActions(
    val onNavigateToContributors: () -> Unit,
    val onNavigateToLicenses: () -> Unit,
    val onOpenReleaseNotesDialog: () -> Unit
)

data class SettingsScreenActions(
    val onBackClick: () -> Unit = {},
    val onSecretGameOver: (Int) -> Unit = {},
    val onSetDefaultLauncherClick: () -> Unit = {},
    val onOpenFavoritePickerClick: () -> Unit = {},
    val onToggleFavorite: (String) -> Unit = {},
    val onDismissFavoritePickerDialog: () -> Unit = {},
    val onToggleHomeButtonOpensAllApps: () -> Unit = {},
    val onToggleWidgetRow: () -> Unit = {},
    val onToggleContainer: () -> Unit = {},
    val onToggleContainerBackground: () -> Unit = {},
    val onToggleContainerExpandCell: () -> Unit = {},
    val onToggleClockBackground: () -> Unit = {},
    val onToggleShowContainerAddFolderButton: () -> Unit = {},
    val onToggleShowFolderLabels: () -> Unit = {},
    val onOpenContainerPositionDialog: () -> Unit = {},
    val onDismissContainerPositionDialog: () -> Unit = {},
    val onContainerPositionSelected: (String) -> Unit = {},
    val onToggleLeftHandedMode: () -> Unit = {},
    val onToggleDynamicWallpaper: () -> Unit = {},
    val onSelectBackgroundColor: (Int) -> Unit = {},
    val onSelectTheme: (String) -> Unit = {},
    val onSelectFont: (String) -> Unit = {},
    val onChangeWallpaperClick: () -> Unit = {},
    val onRestoreDefaultWallpaperClick: () -> Unit = {},
    val onUndo: () -> Unit = {},
    val onUndoExpired: (Long) -> Unit = {},
    val onClearErrorMessage: () -> Unit = {},
    val onAddFolderClick: () -> Unit = {},
    val onDismissCreateFolderDialog: () -> Unit = {},
    val onSubmitCreateFolder: (FolderDetails) -> Unit = {},
    val onToggleHaze: () -> Unit = {},
    val onToggleInAppUpdate: () -> Unit = {},
    val onHeaderOffsetChanged: (Int) -> Unit = {},
    val onToggleThemedAppIcons: () -> Unit = {},
    val onToggleDisableAlphabetScrubber: () -> Unit = {},
    val onOpenResetGridDialog: () -> Unit = {},
    val onConfirmResetGrid: () -> Unit = {},
    val onDismissResetGridDialog: () -> Unit = {},
    val onConfirmFactoryReset: () -> Unit = {},
    val onToggleShowSettingsButtonInAllApps: () -> Unit = {},
    val onOpenSettingsButtonPositionDialog: () -> Unit = {},
    val onDismissSettingsButtonPositionDialog: () -> Unit = {},
    val onSettingsButtonPositionSelected: (String) -> Unit = {},
    val onToggleShowSearchBarInAllApps: () -> Unit = {},
    val onOpenSearchIconPositionDialog: () -> Unit = {},
    val onDismissSearchIconPositionDialog: () -> Unit = {},
    val onSearchIconPositionSelected: (String) -> Unit = {},
    val onSelectWallpaperScaleType: (String) -> Unit = {},
    val onApplyCardsGlass: (CardsGlassDraft) -> Unit = {},
    val onCardsGlassClosed: (CardsGlassDraft) -> Unit = {},
    val onWallpaperEditorOpened: () -> Unit = {},
    val onLockScreenTurnOn: () -> Unit = {},
    val onLockScreenTurnOff: (restoreSystem: Boolean) -> Unit = {},
    val onWallpaperEditorClosed: (before: WallpaperAdjustments, after: WallpaperAdjustments) -> Unit = { _, _ -> },
)
