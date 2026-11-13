package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.search.AureoleSearchField
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.resolveEditingShortcut
import dev.mnascimentos.aureole.feature.home.components.quickbutton.extensions.withShortcut
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutCategory
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionCallbacks
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionOptions
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionSheetParams
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.bestShortcutAction
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.buildShortcutSearchCandidates
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.searchShortcuts
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.shortcutCategoryOf
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.shortcutGestureLabelRes
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.shortcutLabel
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
    options: ShortcutSelectionOptions = ShortcutSelectionOptions()
) {
    var isVisible by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf(options.initialSearchQuery) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            onDismissRequest()
        }
    }

    val activeModel = uiState.activeQuickButtonModel ?: QuickButtonModel()
    val editingType = uiState.editingShortcutType
    val currentAction = resolveEditingShortcut(editingType, activeModel)
    var category by remember { mutableStateOf(shortcutCategoryOf(currentAction)) }
    val candidates = remember(uiState.apps, category) {
        buildShortcutSearchCandidates(
            DEFAULT_SHORTCUT_OPTIONS,
            uiState.apps,
            onlyApps = category == ShortcutCategory.APPS
        )
    }
    val searchResults = remember(candidates, searchQuery) { searchShortcuts(candidates, searchQuery) }
    val applyShortcut = { action: ShortcutAction ->
        actions.onUpdateQuickButtonShortcuts(activeModel.shortcuts.withShortcut(editingType, action))
        isVisible = false
    }

    ShortcutSelectionOverlay(
        params = ShortcutSelectionSheetParams(
            category = category,
            currentAction = currentAction,
            editingType = editingType,
            defaultShortcutOptions = DEFAULT_SHORTCUT_OPTIONS,
            uiState = uiState,
            isVisible = isVisible,
            searchQuery = searchQuery,
            searchResults = searchResults
        ),
        callbacks = ShortcutSelectionCallbacks(
            onQueryChange = { searchQuery = it },
            onApplyShortcut = applyShortcut,
            onSelectCategory = {
                searchQuery = ""
                category = it
            },
            onSettingsClick = { actions.onSettingsClick() },
            onDismiss = { isVisible = false }
        ),
        activeModel = activeModel,
        hazeState = options.hazeState,
        modifier = modifier
    )
}

@Composable
private fun ShortcutSelectionOverlay(
    params: ShortcutSelectionSheetParams,
    callbacks: ShortcutSelectionCallbacks,
    activeModel: QuickButtonModel,
    hazeState: HazeState?,
    modifier: Modifier = Modifier
) {
    val searchPlaceholder = stringResource(R.string.home_quickbutton_search_placeholder)
    val title = if (params.category == ShortcutCategory.APPS) {
        stringResource(R.string.home_quickbutton_select_app)
    } else {
        stringResource(R.string.home_quickbutton_select_shortcut)
    }
    val pillText = stringResource(
        shortcutGestureLabelRes(params.editingType),
        shortcutLabel(params.currentAction, params.uiState.apps)
    )
    CompositionLocalProvider(LocalLeftHanded provides params.uiState.isLeftHandedMode) {
        AureoleEditOverlay(
            onDismissRequest = callbacks.onDismiss,
            modifier = modifier,
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(
                    title = title,
                    hazeState = if (params.uiState.isHazeEnabled) hazeState else null,
                    isVisible = params.isVisible,
                    animated = true,
                    isLeftHanded = params.uiState.isLeftHandedMode,
                    onSettingsClick = callbacks.onSettingsClick
                ),
                previewContent = {
                    ShortcutSelectionPreview(
                        model = activeModel,
                        pillText = pillText,
                        modifier = Modifier.align(Alignment.Center)
                    )
                },
                bottomBarContent = {
                    ShortcutCategoryChips(
                        selected = params.category,
                        onSelect = callbacks.onSelectCategory,
                        modifier = Modifier.padding(bottom = AureoleDS.dimens.xSmall)
                    )
                    AureoleSearchField(
                        query = params.searchQuery,
                        onQueryChange = callbacks.onQueryChange,
                        placeholder = searchPlaceholder,
                        onSearch = { bestShortcutAction(params.searchResults)?.let(callbacks.onApplyShortcut) }
                    )
                },
                sheetContent = {
                    ShortcutSelectionSheetContent(
                        params = params,
                        onApplyShortcut = callbacks.onApplyShortcut
                    )
                }
            )
        )
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
