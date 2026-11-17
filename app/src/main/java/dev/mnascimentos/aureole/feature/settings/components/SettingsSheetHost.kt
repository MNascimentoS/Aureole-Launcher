package dev.mnascimentos.aureole.feature.settings.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.minihome.AureoleMiniHome
import dev.mnascimentos.aureole.composable.minihome.sampleMiniHomeContent
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleOverlayBackdrop
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.model.OverlayHostSpec
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDraft
import dev.mnascimentos.aureole.feature.settings.model.AppearanceDraftSaver
import dev.mnascimentos.aureole.feature.settings.model.AppearanceMenuDialogActions
import dev.mnascimentos.aureole.feature.settings.model.AreaSheetEvents
import dev.mnascimentos.aureole.feature.settings.model.AreaSheetSpec
import dev.mnascimentos.aureole.feature.settings.model.SettingsAdvancedDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsChildKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsInnerKind
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetEvent
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetHostParams
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheetSaver
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import dev.mnascimentos.aureole.feature.settings.model.area
import dev.mnascimentos.aureole.feature.settings.model.reduceSettingsSheet
import dev.mnascimentos.aureole.feature.settings.model.saveOnLeaving
import dev.mnascimentos.aureole.feature.settings.model.settingsAreaHasInner
import dev.mnascimentos.aureole.feature.settings.model.settingsSheetTitle
import dev.mnascimentos.aureole.feature.settings.sections.AdvancedSections
import dev.mnascimentos.aureole.feature.settings.sections.AppearanceSections
import dev.mnascimentos.aureole.feature.settings.sections.BehaviorSections
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withTimeoutOrNull

/** Longest the area waits for its content to be tall enough to take back the saved scroll. */
private const val SCROLL_RESTORE_WINDOW_MS = 1_000L

/** Same fade as the backdrop of a single sheet. */
private const val BACKDROP_FADE_MS = 300

/**
 * The settings root with its area sheets on top. A root card opens its area as a bottom sheet; a row that opens
 * another sheet (palette, font, background, ...) swaps it in place of the area, which comes back, at the same
 * scroll, when that one closes. Every move goes through [reduceSettingsSheet]. One blurred backdrop sits behind all
 * of them, so a swap does not flash the root; in landscape the sheets are narrow and centered.
 *
 * @param params State, actions and the first frame of the sheets.
 * @param root The root screen, given the callback that opens an area.
 */
@Composable
fun SettingsSheetHost(
    params: SettingsSheetHostParams,
    modifier: Modifier = Modifier,
    root: @Composable (onOpenArea: (SettingsArea) -> Unit) -> Unit
) {
    val uiState = params.uiState
    val actions = params.actions
    var sheet by rememberSaveable(stateSaver = SettingsSheetSaver) { mutableStateOf(params.initialSheet) }
    val dispatch: (SettingsSheetEvent) -> Unit = { event -> sheet = reduceSettingsSheet(sheet, event) }
    val area = sheet.area
    // One overlay state and one scroll per opening of an area: a child swapped in keeps both for the way back.
    val areaState = remember(area) { AureoleEditOverlayState() }
    // A plain array, not a state: saving the scroll must not recompose the host on every pixel.
    val areaScroll = rememberSaveable(area) { intArrayOf(params.initialScroll) }
    val hazeState = remember { HazeState() }
    val overlayHost = settingsOverlayHost()

    // The open sheet handles back itself; this one catches a second back while the area is sliding out, so it
    // does not leave the settings. A child sliding out is left alone: it still has to save.
    BackHandler(enabled = sheet != null) {
        if (sheet is SettingsSheet.Area) dispatch(SettingsSheetEvent.Back)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize().hazeSource(hazeState)) {
            root { opened -> dispatch(SettingsSheetEvent.OpenArea(opened)) }
        }
        CompositionLocalProvider(
            LocalHazeState provides hazeState,
            LocalLeftHanded provides uiState.isLeftHandedMode
        ) {
            AnimatedVisibility(
                visible = isBackdropVisible(sheet, areaState.internalVisible),
                enter = fadeIn(tween(BACKDROP_FADE_MS)),
                exit = fadeOut(tween(BACKDROP_FADE_MS))
            ) {
                AureoleOverlayBackdrop(hazeState = hazeState, modifier = Modifier.fillMaxSize())
            }
            if (area != null) {
                OpenArea(
                    spec = AreaSheetSpec(
                        area = area,
                        inner = (sheet as? SettingsSheet.Area)?.inner,
                        isVisible = sheet is SettingsSheet.Area,
                        overlayHost = overlayHost
                    ),
                    host = params,
                    state = areaState,
                    dispatch = dispatch,
                    list = {
                        ScrolledAreaList(scroll = areaScroll) {
                            AreaSections(area, uiState, actions, dispatch, params.onNavigateToLicenses)
                        }
                    }
                )
            }
            (sheet as? SettingsSheet.Child)?.let { child ->
                SettingsChildSheet(
                    kind = child.kind,
                    uiState = uiState,
                    actions = actions,
                    host = overlayHost,
                    onClose = { dispatch(SettingsSheetEvent.CloseChild(child.kind)) }
                )
            }
        }
    }
}

/** One backdrop for every sheet, and sheets no wider than the settings content (narrow and centered in landscape). */
@Composable
private fun settingsOverlayHost() = OverlayHostSpec(
    maxSheetWidth = AureoleDS.dimens.settingsContentMaxWidth,
    hasExternalBackdrop = true
)

/** The backdrop stays while any sheet is up, through a swap, and fades as soon as the last one starts to leave. */
private fun isBackdropVisible(sheet: SettingsSheet?, isAreaShown: Boolean): Boolean = when (sheet) {
    is SettingsSheet.Child -> true
    is SettingsSheet.Area -> isAreaShown
    null -> false
}

/**
 * The open area sheet. Appearance keeps the palette or font tapped in its pickers as a draft, drawn live in the
 * sheet and its preview: going back from a picker saves that choice and keeps the sheet; closing the sheet saves
 * what is left, with the usual Undo.
 */
@Composable
private fun OpenArea(
    spec: AreaSheetSpec,
    host: SettingsSheetHostParams,
    state: AureoleEditOverlayState,
    dispatch: (SettingsSheetEvent) -> Unit,
    list: @Composable () -> Unit
) {
    val uiState = host.uiState
    val actions = host.actions
    val area = spec.area
    if (!settingsAreaHasInner(area)) {
        AreaSheet(
            spec = spec,
            state = state,
            events = AreaSheetEvents(onClosed = { dispatch(SettingsSheetEvent.CloseArea(area)) }, onBackFromInner = {}),
            content = { list() }
        )
        return
    }
    var draft by rememberSaveable(area, stateSaver = AppearanceDraftSaver) { mutableStateOf(AppearanceDraft()) }
    val save: (SettingsInnerKind?) -> Unit = { leaving ->
        val saved = draft.saveOnLeaving(leaving, uiState.selectedThemeName, uiState.selectedFontName)
        saved.paletteName?.let(actions.onSelectTheme)
        saved.fontName?.let(actions.onSelectFont)
        draft = saved.rest
    }
    AppearanceDraftTheme(draft = draft) {
        AreaSheet(
            spec = spec,
            state = state,
            events = AreaSheetEvents(
                onClosed = {
                    save(null)
                    dispatch(SettingsSheetEvent.CloseArea(area))
                },
                onBackFromInner = {
                    save(spec.inner)
                    dispatch(SettingsSheetEvent.Back)
                }
            ),
            preview = {
                AureoleMiniHome(
                    content = sampleMiniHomeContent(),
                    isLeftHanded = LocalLeftHanded.current,
                    themedIcons = uiState.isThemedAppIconsEnabled,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        ) { inner ->
            AreaSheetContentSwitch(
                inner = inner,
                list = list,
                picker = { kind ->
                    AppearancePicker(kind = kind, uiState = uiState, draft = draft, onDraftChange = { draft = it })
                }
            )
        }
    }
}

@Composable
private fun AreaSheet(
    spec: AreaSheetSpec,
    state: AureoleEditOverlayState,
    events: AreaSheetEvents,
    preview: (@Composable BoxScope.() -> Unit)? = null,
    content: @Composable (SettingsInnerKind?) -> Unit
) {
    val isLeftHanded = LocalLeftHanded.current
    AureoleEditOverlay(
        state = state,
        onDismissRequest = events.onClosed,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(settingsSheetTitle(spec.area, spec.inner)),
                isVisible = spec.isVisible,
                isLeftHanded = isLeftHanded,
                host = spec.overlayHost,
                // With a picker inside, back brings the list back; tap outside and drag still close the sheet.
                onBackPress = if (spec.inner != null) events.onBackFromInner else null,
                onDismiss = events.onClosed,
                showHeaderAction = false,
                hasExternalBackdrop = true
            ),
            previewContent = preview,
            sheetContent = { content(spec.inner) }
        )
    )
}

/** The list of an area, scrolling inside the sheet; its scroll is kept in [scroll] for the way back. */
@Composable
private fun ScrolledAreaList(scroll: IntArray, content: @Composable () -> Unit) {
    val saved = scroll[0]
    val scrollState = rememberScrollState(initial = saved)
    RestoreAreaScroll(scrollState = scrollState, saved = saved, onScrolled = { scroll[0] = it })
    Column(modifier = Modifier.verticalScroll(scrollState)) {
        content()
        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
    }
}

/**
 * The area list leaves the composition while a child sheet or a picker is up. When it comes back, the saved scroll
 * is put back once the content is laid out far enough to hold it (a scroll set before that is clamped), and from then
 * on every scroll is saved.
 */
@Composable
private fun RestoreAreaScroll(scrollState: ScrollState, saved: Int, onScrolled: (Int) -> Unit) {
    val currentOnScrolled by rememberUpdatedState(onScrolled)
    LaunchedEffect(scrollState) {
        // The first measures, while the sheet slides back in, can allow less scroll than the content has.
        withTimeoutOrNull(SCROLL_RESTORE_WINDOW_MS) {
            snapshotFlow { scrollState.maxValue }.first { it != Int.MAX_VALUE && it >= saved }
        }
        scrollState.scrollTo(saved)
        snapshotFlow { scrollState.value }.collect { currentOnScrolled(it) }
    }
}

@Composable
private fun AreaSections(
    area: SettingsArea,
    uiState: SettingsUiState,
    actions: SettingsScreenActions,
    dispatch: (SettingsSheetEvent) -> Unit,
    onNavigateToLicenses: () -> Unit
) {
    val open: (SettingsChildKind) -> () -> Unit = { kind -> { dispatch(SettingsSheetEvent.OpenChild(kind)) } }
    when (area) {
        SettingsArea.APPEARANCE -> AppearanceSections(
            uiState = uiState,
            actions = actions,
            dialogActions = AppearanceMenuDialogActions(
                onShowSetBackgroundDialog = open(SettingsChildKind.BACKGROUND),
                onShowCardsGlassDialog = open(SettingsChildKind.CARDS_GLASS),
                onShowPaletteDialog = { dispatch(SettingsSheetEvent.OpenInner(SettingsInnerKind.PALETTE)) },
                onShowFontDialog = { dispatch(SettingsSheetEvent.OpenInner(SettingsInnerKind.FONT)) }
            )
        )
        SettingsArea.BEHAVIOR -> BehaviorSections(uiState = uiState, actions = actions)
        SettingsArea.ADVANCED -> AdvancedSections(
            uiState = uiState,
            actions = actions,
            dialogActions = SettingsAdvancedDialogActions(
                onNavigateToContributors = open(SettingsChildKind.CONTRIBUTORS),
                onNavigateToLicenses = onNavigateToLicenses,
                onOpenReleaseNotesDialog = open(SettingsChildKind.RELEASE_NOTES)
            )
        )
    }
}

@AureolePreview
@Composable
private fun SettingsSheetHostPreview() {
    AureoleLauncherTheme {
        SettingsSheetHost(
            params = SettingsSheetHostParams(
                uiState = SettingsUiState(),
                actions = SettingsScreenActions(),
                onNavigateToLicenses = {},
                initialSheet = SettingsSheet.Area(SettingsArea.APPEARANCE)
            )
        ) {
            AureoleText(text = stringResource(R.string.settings_root_title), color = AureoleDS.colors.onSurfaceHigh)
        }
    }
}
