package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.sheets.AureoleConfirmOverlay
import dev.mnascimentos.aureole.composable.sheets.model.ConfirmOverlayParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.AppearanceDialogState
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonAppearanceMetrics
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorPickerTarget
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Quick button appearance editor in an edit overlay. The edits go to a draft that the live preview anchored above
 * the sheet follows; Save writes it to the button and Cancel (or swiping down) drops it. The filled button is on
 * the thumb side.
 */
@Composable
fun QuickButtonAppearanceBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    val activeModel = uiState.activeQuickButtonModel ?: QuickButtonModel()
    var draft by remember { mutableStateOf(activeModel.appearance) }
    var colorPickerTarget by remember { mutableStateOf<QuickButtonColorPickerTarget?>(null) }
    var showIconPicker by remember { mutableStateOf(false) }

    val draftModel = activeModel.copy(appearance = draft)
    val activeAppInfo = remember(activeModel.shortcuts.click, uiState.apps) {
        if (activeModel.shortcuts.click.type == ShortcutType.OPEN_APP) {
            uiState.apps.find { it.packageName == activeModel.shortcuts.click.target }
        } else {
            null
        }
    }

    AureoleConfirmOverlay(
        params = ConfirmOverlayParams(
            title = stringResource(R.string.home_quickbutton_appearance_title),
            primaryLabel = stringResource(R.string.core_common_save),
            onPrimary = { actions.onUpdateQuickButtonAppearance(draft) },
            onDismissRequest = onDismissRequest,
            isLeftHanded = uiState.isLeftHandedMode,
            onSettingsClick = { actions.onSettingsClick() },
            hazeState = if (uiState.isHazeEnabled) hazeState else null
        ),
        modifier = modifier,
        preview = {
            AureolePreviewFrame(
                modifier = Modifier.align(Alignment.Center)
            ) {
                QuickButtonPreviewBox(activeModel = draftModel, activeAppInfo = activeAppInfo)
            }
        }
    ) {
        QuickButtonAppearanceSheetContent(
            activeModel = draftModel,
            activeAppInfo = activeAppInfo,
            onUpdateAppearance = { draft = it },
            onPickColor = { target -> colorPickerTarget = target },
            onOpenIconPicker = { showIconPicker = true }
        )
    }

    QuickButtonAppearanceDialogs(
        dialogState = AppearanceDialogState(
            colorPickerTarget = colorPickerTarget,
            activeModel = draftModel,
            showIconPicker = showIconPicker,
            onUpdateAppearance = { draft = it },
            isLeftHanded = uiState.isLeftHandedMode
        ),
        onDismissColorPicker = { colorPickerTarget = null },
        onDismissIconPicker = { showIconPicker = false }
    )
}

@Composable
private fun QuickButtonPreviewBox(
    activeModel: QuickButtonModel,
    activeAppInfo: AppInfo?
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(QuickButtonAppearanceMetrics.PreviewBoxHeight),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(QuickButtonAppearanceMetrics.PreviewItemSize)) {
            QuickButtonGridItem(
                model = activeModel,
                appInfo = activeAppInfo,
                onAction = {},
                onLongPress = {}
            )
        }
    }
}

@Composable
private fun QuickButtonAppearanceDialogs(
    dialogState: AppearanceDialogState,
    onDismissColorPicker: () -> Unit,
    onDismissIconPicker: () -> Unit
) {
    val activeModel = dialogState.activeModel

    dialogState.colorPickerTarget?.let { target ->
        val initialColor = when (target) {
            QuickButtonColorPickerTarget.BACKGROUND -> activeModel.appearance.backgroundColor
            QuickButtonColorPickerTarget.STROKE -> activeModel.appearance.strokeColor
            QuickButtonColorPickerTarget.ICON -> activeModel.appearance.iconColor
        }
        ColorPickerDialog(
            initialColor = initialColor,
            onColorSelected = { selectedColor ->
                val updatedAppearance = when (target) {
                    QuickButtonColorPickerTarget.BACKGROUND ->
                        activeModel.appearance.copy(backgroundColor = selectedColor)
                    QuickButtonColorPickerTarget.STROKE ->
                        activeModel.appearance.copy(strokeColor = selectedColor)
                    QuickButtonColorPickerTarget.ICON ->
                        activeModel.appearance.copy(iconColor = selectedColor)
                }
                dialogState.onUpdateAppearance(updatedAppearance)
                onDismissColorPicker()
            },
            onDismiss = onDismissColorPicker,
            isLeftHanded = dialogState.isLeftHanded
        )
    }

    if (dialogState.showIconPicker) {
        QuickButtonIconPickerDialog(
            selectedIcon = activeModel.appearance.icon,
            onIconSelected = { selectedIcon ->
                dialogState.onUpdateAppearance(activeModel.appearance.copy(icon = selectedIcon))
                onDismissIconPicker()
            },
            onDismiss = onDismissIconPicker
        )
    }
}

@AureolePreview
@Composable
private fun QuickButtonAppearanceBottomSheetPreview() {
    val dummyActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )
    AureoleLauncherTheme {
        QuickButtonAppearanceBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}

@AureolePreview
@Composable
private fun QuickButtonAppearanceBottomSheetHazeDisabledPreview() {
    val dummyActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )
    AureoleLauncherTheme {
        QuickButtonAppearanceBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel(),
                isHazeEnabled = false
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
