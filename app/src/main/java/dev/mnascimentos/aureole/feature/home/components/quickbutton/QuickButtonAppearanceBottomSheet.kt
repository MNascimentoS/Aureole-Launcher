package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.FolderNamePill
import dev.mnascimentos.aureole.composable.sheets.AureoleAutoSaveOverlay
import dev.mnascimentos.aureole.composable.sheets.model.AutoSaveOverlayParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.ShortcutType
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderLabel
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.NoQuickButtonCallbacks
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonAppearanceMetrics
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.QuickButtonGridItemStyle
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.defaultName
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.isLowContrast
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.normalized
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.resolvedName
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * Quick button sheet in an edit overlay (botao-rapido). The button is anchored above the sheet with its name pill
 * under it (the name is typed there, RF-01), and the sheet has Icon | Colors (RF-03). The edits go to a draft that
 * the live preview follows; closing the sheet (tap outside, back, swipe down) writes it to the button when it
 * changed, and the caller offers an "Undo". No logo in the title (RF-02b): `onSettingsClick` stays null. In
 * landscape the overlay puts the preview on one side and the sheet on the other (RF-08).
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

    val draftModel = activeModel.copy(appearance = draft)
    val activeAppInfo = remember(activeModel.shortcuts.click, uiState.apps) {
        if (activeModel.shortcuts.click.type == ShortcutType.OPEN_APP) {
            uiState.apps.find { it.packageName == activeModel.shortcuts.click.target }
        } else {
            null
        }
    }

    AureoleAutoSaveOverlay(
        params = AutoSaveOverlayParams(
            title = stringResource(R.string.home_quickbutton_appearance_title),
            isChanged = { draft.normalized() != activeModel.appearance },
            onCommit = { actions.onUpdateQuickButtonAppearance(draft.normalized()) },
            onDismissRequest = onDismissRequest,
            isLeftHanded = uiState.isLeftHandedMode,
            // Item editors have no logo on the title line (RF-02b).
            hazeState = if (uiState.isHazeEnabled) hazeState else null
        ),
        modifier = modifier,
        preview = {
            QuickButtonSheetPreview(
                model = draftModel,
                appInfo = activeAppInfo,
                onNameChange = { draft = draft.copy(label = it) },
                modifier = Modifier.align(Alignment.Center)
            )
        }
    ) {
        QuickButtonAppearanceSheetContent(
            activeModel = draftModel,
            onUpdateAppearance = { draft = it },
            modifier = Modifier.weight(1f, fill = false)
        )
    }
}

/**
 * The button as it will look on the Home, with its name under it when "Show name" is on, a short warning when the
 * icon barely contrasts with the background (RF-06; the icon gets an outline) and the name pill (RF-01).
 */
@Composable
internal fun QuickButtonSheetPreview(
    model: QuickButtonModel,
    appInfo: AppInfo?,
    onNameChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val appearance = model.appearance
    val showsAppIcon = model.shortcuts.click.type == ShortcutType.OPEN_APP && appInfo != null
    val lowContrast = !showsAppIcon && isLowContrast(
        icon = appearance.iconColor.resolve(AureoleDS.colors),
        background = appearance.backgroundColor.resolve(AureoleDS.colors)
    )
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        Box(modifier = Modifier.size(QuickButtonAppearanceMetrics.PreviewItemSize)) {
            QuickButtonGridItem(
                model = model,
                callbacks = NoQuickButtonCallbacks,
                // ~1/3 of the tile, as the folder preview (the 72 dp tile with a 24 dp icon).
                style = QuickButtonGridItemStyle(
                    appInfo = appInfo,
                    outlineIcon = lowContrast,
                    iconSize = AureoleDS.dimens.iconLarge
                )
            )
        }
        if (appearance.showLabel) {
            ContainerFolderLabel(name = appearance.resolvedName(model.defaultName(appInfo?.label)))
        }
        if (lowContrast) {
            AureoleText(
                text = stringResource(R.string.home_quickbutton_low_contrast),
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.labelSmall, textAlign = TextAlign.Center)
            )
        }
        FolderNamePill(value = appearance.label.orEmpty(), onValueChange = onNameChange)
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
