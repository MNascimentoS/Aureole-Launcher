package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import dev.mnascimentos.aureole.composable.sheets.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.components.wallpaper.WallpaperEditorOverlay
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams
import dev.mnascimentos.aureole.feature.settings.model.ChildSheetLinks
import dev.mnascimentos.aureole.feature.settings.model.SetBackgroundParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetEvent
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetHostParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorParams
import dev.mnascimentos.aureole.feature.settings.model.toCardsGlassDraft

/**
 * The sheet an area opens in its place (the palette and font pickers open inside the Appearance sheet instead).
 * Each one saves on close as it always did (with its Undo) and calls [onClose] once it has slid out, which brings
 * the area back. The photo editor opens from the background sheet ([ChildSheetLinks.onOpenEditor]) and gives it
 * back the same way.
 */
@Composable
internal fun SettingsChildSheet(
    kind: SettingsChildKind,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    links: ChildSheetLinks = ChildSheetLinks(),
    onClose: () -> Unit
) {
    val host = links.host
    when (kind) {
        SettingsChildKind.BACKGROUND -> SetBackgroundBottomSheet(
            params = SetBackgroundParams(
                currentColor = uiState.manualSeedColor,
                isCustomImageSet = uiState.isCustomWallpaperSet,
                onSolidColorSelected = actions.onSelectBackgroundColor,
                onCustomImageClick = actions.onChangeWallpaperClick,
                onDismissRequest = onClose,
                isLeftHanded = uiState.isLeftHandedMode,
                host = host,
                customImagePath = uiState.customWallpaperPath,
                adjustments = uiState.wallpaperAdjustments,
                onEditPhotoClick = links.onOpenEditor
            )
        )
        SettingsChildKind.WALLPAPER_EDITOR -> WallpaperEditorOverlay(
            params = WallpaperEditorParams(
                photoPath = uiState.customWallpaperPath?.takeIf { uiState.isCustomWallpaperSet && it.isNotEmpty() },
                initial = uiState.wallpaperAdjustments,
                onFinish = actions.onWallpaperEditorClosed,
                onDismissRequest = onClose,
                isLeftHanded = uiState.isLeftHandedMode,
                host = host
            )
        )
        SettingsChildKind.CARDS_GLASS -> CardsGlassBottomSheet(
            params = CardsGlassParams(
                initial = uiState.toCardsGlassDraft(),
                onChange = actions.onApplyCardsGlass,
                onCommit = actions.onCardsGlassClosed,
                onDismissRequest = onClose,
                isLeftHanded = uiState.isLeftHandedMode,
                host = host
            )
        )
        SettingsChildKind.RELEASE_NOTES -> ReleaseNotesBottomSheet(onDismissRequest = onClose, host = host)
        SettingsChildKind.CONTRIBUTORS -> ContributorsBottomSheet(onDismissRequest = onClose, host = host)
    }
}

/** The child sheet on top; the background one can swap itself for the photo editor. */
@Composable
internal fun OpenChild(
    child: SettingsSheet.Child,
    host: SettingsSheetHostParams,
    overlayHost: OverlayHostSpec,
    dispatch: (SettingsSheetEvent) -> Unit
) {
    SettingsChildSheet(
        kind = child.kind,
        uiState = host.uiState,
        actions = host.actions,
        links = ChildSheetLinks(
            host = overlayHost,
            onOpenEditor = { dispatch(SettingsSheetEvent.OpenChild(SettingsChildKind.WALLPAPER_EDITOR)) }
        ),
        onClose = { dispatch(SettingsSheetEvent.CloseChild(child.kind)) }
    )
}

/** A photo was just picked: the editor opens on it in place of the background sheet (RF-01), once. */
@Composable
internal fun OpenEditorOnRequest(isRequested: Boolean, onOpen: () -> Unit, onOpened: () -> Unit) {
    val currentOnOpen by rememberUpdatedState(onOpen)
    val currentOnOpened by rememberUpdatedState(onOpened)
    LaunchedEffect(isRequested) {
        if (isRequested) {
            currentOnOpen()
            currentOnOpened()
        }
    }
}

@AureolePreview
@Composable
private fun SettingsChildSheetPreview() {
    AureoleLauncherTheme {
        SettingsChildSheet(
            kind = SettingsChildKind.CONTRIBUTORS,
            uiState = SettingsUiState(),
            actions = SettingsScreenActions(),
            links = ChildSheetLinks(host = OverlayHostSpec()),
            onClose = {}
        )
    }
}
