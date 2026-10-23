package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.composable.ColorPickerDialog
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonColorPickerTarget
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_MS = 300L

private data class AppearanceDialogState(
    val colorPickerTarget: QuickButtonColorPickerTarget?,
    val activeModel: QuickButtonModel,
    val showIconPicker: Boolean,
    val actions: HomeScreenActions
)

@Composable
fun QuickButtonAppearanceBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    var isVisible by remember { mutableStateOf(true) }
    var colorPickerTarget by remember { mutableStateOf<QuickButtonColorPickerTarget?>(null) }
    var showIconPicker by remember { mutableStateOf(false) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }

    val activeModel = uiState.activeQuickButtonModel ?: QuickButtonModel()
    val activeAppInfo = remember(activeModel.shortcuts.click, uiState.apps) {
        if (activeModel.shortcuts.click.type == ShortcutType.OPEN_APP) {
            uiState.apps.find { it.packageName == activeModel.shortcuts.click.target }
        } else {
            null
        }
    }

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = "Quick button appearance",
                onSettingsClick = { actions.onSettingsClick() },
                hazeState = if (uiState.isHazeEnabled) hazeState else null,
                isVisible = isVisible,
                animated = true,
                maxSheetHeightFraction = 0.5f
            ),
            previewContent = {
                QuickButtonPreviewBox(activeModel = activeModel, activeAppInfo = activeAppInfo)
            },
            bottomBarContent = null,
            sheetContent = {
                QuickButtonAppearanceSheetContent(
                    activeModel = activeModel,
                    activeAppInfo = activeAppInfo,
                    actions = actions,
                    onPickColor = { target -> colorPickerTarget = target },
                    onOpenIconPicker = { showIconPicker = true }
                )
            }
        )
    )

    QuickButtonAppearanceDialogs(
        dialogState = AppearanceDialogState(
            colorPickerTarget = colorPickerTarget,
            activeModel = activeModel,
            showIconPicker = showIconPicker,
            actions = actions
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
            .height(140.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.size(72.dp)) {
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
    val actions = dialogState.actions

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
                actions.onUpdateQuickButtonAppearance(updatedAppearance)
                onDismissColorPicker()
            },
            onDismiss = onDismissColorPicker
        )
    }

    if (dialogState.showIconPicker) {
        QuickButtonIconPickerDialog(
            selectedIcon = activeModel.appearance.icon,
            onIconSelected = { selectedIcon ->
                actions.onUpdateQuickButtonAppearance(
                    activeModel.appearance.copy(icon = selectedIcon)
                )
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
