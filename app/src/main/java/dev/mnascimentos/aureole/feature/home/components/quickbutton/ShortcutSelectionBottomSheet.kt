package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_MS = 300L

private val DEFAULT_SHORTCUT_OPTIONS = listOf(
    ShortcutAction.AllApps,
    ShortcutAction.Assistant,
    ShortcutAction.Settings,
    ShortcutAction.systemIntent("Browser"),
    ShortcutAction.systemIntent("Calculator"),
    ShortcutAction.systemIntent("Calendar"),
    ShortcutAction.systemIntent("Camera"),
    ShortcutAction.systemIntent("Clock"),
    ShortcutAction.systemIntent("Contacts"),
    ShortcutAction.systemIntent("Dialer"),
    ShortcutAction.systemIntent("Email"),
    ShortcutAction.systemIntent("Search"),
    ShortcutAction.systemIntent("Settings"),
    ShortcutAction.systemIntent("Files"),
    ShortcutAction.systemIntent("Messages"),
    ShortcutAction.systemIntent("Battery Settings"),
    ShortcutAction.systemIntent("Bluetooth Settings"),
    ShortcutAction.systemIntent("Device Settings"),
    ShortcutAction.systemIntent("Display Settings"),
    ShortcutAction.systemIntent("Music Player"),
    ShortcutAction.systemIntent("Location Settings"),
    ShortcutAction.systemIntent("Sound Settings"),
    ShortcutAction.systemIntent("Wi-Fi Settings"),
    ShortcutAction.None
)

@Composable
fun ShortcutSelectionBottomSheet(
    uiState: MainUiState,
    actions: HomeScreenActions,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null
) {
    var isVisible by remember { mutableStateOf(true) }
    var isShowingAppPicker by remember { mutableStateOf(false) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }

    val activeModel = uiState.activeQuickButtonModel ?: QuickButtonModel()
    val editingType = uiState.editingShortcutType
    val currentAction = resolveEditingShortcut(editingType, activeModel)

    fun applyShortcut(action: ShortcutAction) {
        val newShortcuts = when (editingType) {
            "click" -> activeModel.shortcuts.copy(click = action)
            "press" -> activeModel.shortcuts.copy(press = action)
            "swipeUp" -> activeModel.shortcuts.copy(swipeUp = action)
            "swipeLeft" -> activeModel.shortcuts.copy(swipeLeft = action)
            "swipeRight" -> activeModel.shortcuts.copy(swipeRight = action)
            else -> activeModel.shortcuts
        }
        actions.onUpdateQuickButtonShortcuts(newShortcuts)
        isVisible = false
    }

    val params = ShortcutSelectionSheetParams(
        isShowingAppPicker = isShowingAppPicker,
        currentAction = currentAction,
        defaultShortcutOptions = DEFAULT_SHORTCUT_OPTIONS,
        uiState = uiState
    )

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = if (isShowingAppPicker) "Select an app" else "Select a shortcut",
                hazeState = if (uiState.isHazeEnabled) hazeState else null,
                isVisible = isVisible,
                animated = true,
                maxSheetHeightFraction = 0.8f
            ),
            previewContent = null,
            bottomBarContent = null,
            sheetContent = {
                ShortcutSelectionSheetContent(
                    params = params,
                    onApplyShortcut = ::applyShortcut,
                    onShowAppPicker = { isShowingAppPicker = true }
                )
            }
        )
    )
}

private fun resolveEditingShortcut(type: String?, model: QuickButtonModel): ShortcutAction {
    return when (type) {
        "click" -> model.shortcuts.click
        "press" -> model.shortcuts.press
        "swipeUp" -> model.shortcuts.swipeUp
        "swipeLeft" -> model.shortcuts.swipeLeft
        "swipeRight" -> model.shortcuts.swipeRight
        else -> ShortcutAction.None
    }
}

@AureolePreview
@Composable
private fun ShortcutSelectionBottomSheetPreview() {
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
        ShortcutSelectionBottomSheet(
            uiState = MainUiState(
                activeQuickButtonModel = QuickButtonModel()
            ),
            actions = dummyActions,
            onDismissRequest = {}
        )
    }
}
