package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDraft
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDraftSaver
import dev.mnascimentos.aureole.feature.settings.model.AreaSheetEvents
import dev.mnascimentos.aureole.feature.settings.model.AreaSheetSpec
import dev.mnascimentos.aureole.feature.settings.model.BackgroundDraft
import dev.mnascimentos.aureole.feature.settings.model.BackgroundDraftSaver
import dev.mnascimentos.aureole.feature.settings.model.LockScreenDialog
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetEvent
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetHostParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.backgroundColorOnLeaving
import dev.mnascimentos.aureole.feature.settings.model.leavingSavesBackground
import dev.mnascimentos.aureole.feature.settings.model.saveOnLeaving
import dev.mnascimentos.aureole.feature.settings.model.shownBackgroundDraft
import dev.mnascimentos.aureole.feature.settings.model.toBackgroundParams

/**
 * The Appearance sheet. The palette, font and background tapped in its pickers stay a draft, drawn live in the
 * sheet and in the preview above it (the background picker shows its color or photo there): going back from a
 * picker saves that choice and keeps the sheet; closing the sheet saves what is left, with the usual Undo. The photo
 * editor opens full screen from the background picker and gives it back when it closes.
 */
@Composable
internal fun AppearanceAreaSheet(
    spec: AreaSheetSpec,
    host: SettingsSheetHostParams,
    state: AureoleEditOverlayState,
    dispatch: (SettingsSheetEvent) -> Unit,
    list: @Composable () -> Unit
) {
    val uiState = host.uiState
    var draft by rememberSaveable(spec.area, stateSaver = AppearanceDraftSaver) { mutableStateOf(AppearanceDraft()) }
    var background by rememberSaveable(spec.area, stateSaver = BackgroundDraftSaver) {
        mutableStateOf<BackgroundDraft?>(null)
    }
    var lockDialog by remember { mutableStateOf<LockScreenDialog?>(null) }
    val save: (SettingsInnerKind?) -> Unit = { leaving ->
        draft = applyAppearanceSave(leaving, draft, background, host)
        if (leavingSavesBackground(leaving)) background = null
    }
    val backgroundParams = uiState.toBackgroundParams(host.actions) {
        dispatch(SettingsSheetEvent.OpenChild(SettingsChildKind.WALLPAPER_EDITOR))
    }
    val shownBackground = shownBackgroundDraft(background, uiState.isCustomWallpaperSet, uiState.manualSeedColor)
    val events = AreaSheetEvents(
        onClosed = {
            save(null)
            dispatch(SettingsSheetEvent.CloseArea(spec.area))
        },
        onBackFromInner = {
            save(spec.inner)
            dispatch(SettingsSheetEvent.Back)
        }
    )
    AppearanceDraftTheme(draft = draft) {
        Box {
            AreaSheet(
                spec = spec,
                state = state,
                events = events,
                preview = {
                    BackgroundMiniHome(
                        draft = shownBackground.takeIf { spec.inner == SettingsInnerKind.BACKGROUND },
                        params = backgroundParams,
                        themedIcons = uiState.isThemedAppIconsEnabled,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            ) { inner ->
                AreaSheetContentSwitch(inner = inner, list = list) { kind ->
                    AppearancePicker(kind = kind, uiState = uiState, draft = draft, onDraftChange = { draft = it }) {
                        BackgroundPickerContent(
                            params = backgroundParams,
                            draft = shownBackground,
                            onDraftChange = { background = it },
                            onLockDialog = { lockDialog = it }
                        )
                    }
                }
            }
            LockScreenDialogs(
                dialog = lockDialog,
                option = backgroundParams.lockScreen,
                onDismiss = { lockDialog = null }
            )
        }
    }
}

/**
 * Saves what leaving [leaving] keeps: the palette and font of [draft] and the background of [background], through
 * the settings actions of [host]. Returns what is left of the draft.
 */
private fun applyAppearanceSave(
    leaving: SettingsInnerKind?,
    draft: AppearanceDraft,
    background: BackgroundDraft?,
    host: SettingsSheetHostParams
): AppearanceDraft {
    val uiState = host.uiState
    val saved = draft.saveOnLeaving(leaving, uiState.selectedThemeName, uiState.selectedFontName)
    saved.paletteName?.let(host.actions.onSelectTheme)
    saved.fontName?.let(host.actions.onSelectFont)
    backgroundColorOnLeaving(leaving, background, uiState.isCustomWallpaperSet, uiState.manualSeedColor)
        ?.let(host.actions.onSelectBackgroundColor)
    return saved.rest
}

@AureolePreview
@Composable
private fun AppearanceAreaSheetBackgroundPreview() {
    AureoleLauncherTheme {
        AppearanceAreaSheet(
            spec = AreaSheetSpec(
                area = SettingsArea.APPEARANCE,
                inner = SettingsInnerKind.BACKGROUND,
                isVisible = true
            ),
            host = SettingsSheetHostParams(
                uiState = SettingsUiState(),
                actions = SettingsScreenActions(),
                onNavigateToLicenses = {}
            ),
            state = remember { AureoleEditOverlayState() },
            dispatch = {},
            list = {}
        )
    }
}
