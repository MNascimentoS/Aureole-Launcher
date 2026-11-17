package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.mnascimentos.aureole.composable.sheets.FontPickerGrid
import dev.mnascimentos.aureole.composable.sheets.PalettePickerGrid
import dev.mnascimentos.aureole.composable.sheets.model.basePaletteName
import dev.mnascimentos.aureole.composable.sheets.model.fontNameOf
import dev.mnascimentos.aureole.composable.sheets.model.resolvePreviewConfig
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDraft
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.toPreviewState

/** Slide of the content of the sheet when a picker comes in or goes back to the list. */
private const val INNER_SLIDE_MS = 280

/**
 * Draws [content] (the Appearance sheet and its preview) in the theme of the current settings with the [draft] on
 * top, so a palette or font tapped in a picker shows at once without being saved.
 */
@Composable
internal fun AppearanceDraftTheme(draft: AppearanceDraft, content: @Composable () -> Unit) {
    val isDarkTheme = isSystemInDarkTheme()
    val base = AureoleThemeConfig(
        darkTheme = isDarkTheme,
        aureoleColors = AureoleDS.colors,
        aureoleFontName = fontNameOf(AureoleDS.typography.fontFamily),
        aureoleIcons = AureoleDS.icons,
        aureoleDimens = AureoleDS.dimens
    )
    AureoleLauncherTheme(config = resolvePreviewConfig(base, draft.toPreviewState(isDarkTheme)), content = content)
}

/**
 * The content of an area sheet: its list or, with [inner], a picker in its place. The picker slides in from the
 * side and the list slides back when it goes.
 */
@Composable
internal fun AreaSheetContentSwitch(
    inner: SettingsInnerKind?,
    list: @Composable () -> Unit,
    picker: @Composable (SettingsInnerKind) -> Unit
) {
    AnimatedContent(
        targetState = inner,
        transitionSpec = { innerSlide() },
        // Fills the sheet: the list and each picker give it the same height, so the preview above does not jump.
        modifier = Modifier.fillMaxSize(),
        label = "areaInner"
    ) { shown ->
        if (shown == null) list() else picker(shown)
    }
}

private fun AnimatedContentTransitionScope<SettingsInnerKind?>.innerSlide(): ContentTransform {
    // Into a picker the content moves to the left; back to the list, to the right.
    val forward = targetState != null
    val enter = slideInHorizontally(tween(INNER_SLIDE_MS)) { width -> if (forward) width else -width } +
        fadeIn(tween(INNER_SLIDE_MS))
    val exit = slideOutHorizontally(tween(INNER_SLIDE_MS)) { width -> if (forward) -width else width } +
        fadeOut(tween(INNER_SLIDE_MS))
    return enter togetherWith exit
}

/**
 * The palette, font or [background] picker inside the Appearance sheet. A tap only changes the [draft]
 * ([onDraftChange]); the sheet saves it when the picker goes back to the list or the sheet closes.
 */
@Composable
internal fun AppearancePicker(
    kind: SettingsInnerKind,
    uiState: SettingsUiState,
    draft: AppearanceDraft,
    onDraftChange: (AppearanceDraft) -> Unit,
    background: @Composable () -> Unit = {}
) {
    when (kind) {
        SettingsInnerKind.PALETTE -> PalettePickerGrid(
            shownName = draft.paletteName ?: uiState.selectedThemeName,
            onSelect = { palette -> onDraftChange(draft.copy(paletteName = palette.name.basePaletteName())) }
        )
        SettingsInnerKind.FONT -> FontPickerGrid(
            shownFont = draft.fontName ?: uiState.selectedFontName,
            onSelect = { font -> onDraftChange(draft.copy(fontName = font)) }
        )
        SettingsInnerKind.BACKGROUND -> background()
    }
}

@AureolePreview
@Composable
private fun AppearancePickerPreview() {
    AureoleLauncherTheme {
        AppearanceDraftTheme(draft = AppearanceDraft()) {
            AreaSheetContentSwitch(
                inner = SettingsInnerKind.PALETTE,
                list = {},
                picker = { kind ->
                    AppearancePicker(
                        kind = kind,
                        uiState = SettingsUiState(),
                        draft = AppearanceDraft(),
                        onDraftChange = {}
                    )
                }
            )
        }
    }
}
