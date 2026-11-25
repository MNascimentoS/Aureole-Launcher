package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.showsHomeSheetLogo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerCloser
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupRemoval
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupRemovalBar
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetNav
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTip
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTipFlow
import dev.mnascimentos.aureole.feature.home.components.containers.model.RemovedGroupItem
import dev.mnascimentos.aureole.feature.home.components.containers.model.firstGroupTip
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 300L

/**
 * "Customize group" (grupo-apps-pastas Part B): one sheet like the clock's, with the group drawn above it as on the
 * home, the tabs Items / Layout / Appearance at its foot and pages in their place (D-03, D-04). No save button: it
 * closes by dragging, back or a tap outside, and saves the group as it closes (RF-B5, D-13). The first group shows
 * three tips (Part F).
 */
@Composable
fun EditContainerScreen(
    params: EditContainerScreenParams,
    modifier: Modifier = Modifier
) {
    val state = rememberEditContainerState(params.panel)
    val nav = rememberGroupSheetNav(params)
    var isVisible by remember { mutableStateOf(true) }
    val latestParams by rememberUpdatedState(params)
    val tips = rememberGroupTips(params)

    val currentUiState = LocalHomeUiState.current
    val effectiveUiState =
        if (currentUiState.apps.isEmpty()) currentUiState.copy(apps = params.allApps) else currentUiState

    ProvideHomeUiState(effectiveUiState) {
        DismissAfterAnimation(isVisible, params.onDismiss)
        val closer = remember(state) {
            EditContainerCloser(save = { latestParams.onSave(state.toModel()) }, close = { isVisible = false })
        }
        val goBack = groupGoBack(nav, state, tips, closer)
        val selectTab: (GroupTab) -> Unit = { tab ->
            nav.selectTab(tab)
            tips.onTabSelected(tab)
        }
        val removal = rememberGroupRemoval(state, effectiveUiState.isLeftHandedMode)
        val baseConfig = groupBaseConfig(
            params = params,
            isVisible = isVisible,
            isLeftHanded = effectiveUiState.isLeftHandedMode,
            onBackPress = goBack,
            onDismiss = closer::closeOnce
        )
        AureoleEditOverlay(
            onDismissRequest = closer::closeOnce,
            modifier = modifier,
            params = AureoleEditOverlayParams(
                config = baseConfig.forGroupPage(nav, state, onBack = goBack, onClose = closer::closeOnce),
                previewContent = { GroupPreviewSlot(params, nav, state) },
                bottomBarContent = groupBottomBar(nav, state, removal.bar, tips, selectTab),
                sheetContent = {
                    GroupSheetBody(
                        GroupSheetBodyParams(
                            state = state,
                            allApps = params.allApps,
                            nav = nav,
                            tips = tips,
                            onRemove = removal.remove,
                            onSelectTab = selectTab
                        )
                    )
                }
            )
        )
    }
}

/** What sits under the content: the tab bar with its notices on the tabs, the "Create folder" button on its page. */
private fun groupBottomBar(
    nav: GroupSheetNav,
    state: EditContainerState,
    removal: GroupRemovalBar,
    tips: GroupTipFlow,
    onTabSelected: (GroupTab) -> Unit
): (@Composable ColumnScope.() -> Unit)? = when (nav.page) {
    GroupSheetPage.Tabs -> {
        {
            GroupSheetBottomBar(
                nav = nav,
                removal = removal,
                isLayoutPulsing = tips.tip == GroupTip.CHOOSE_POSITION,
                onTabSelected = onTabSelected
            )
        }
    }
    GroupSheetPage.NewFolder -> {
        {
            val defaultName = stringResource(R.string.home_container_group_folder_default_name)
            GroupNewFolderFooter(selectedCount = nav.newFolder.apps.size) {
                val draft = nav.newFolder
                state.addFolder(FolderDetails(name = draft.name.ifBlank { defaultName }.trim()), draft.apps)
                nav.back()
            }
        }
    }
    else -> null
}

/** The item removed with × and its Undo for 4 s (RF-C6); a new removal replaces the one still offered. */
@Composable
private fun rememberGroupRemoval(state: EditContainerState, isLeftHanded: Boolean): GroupRemoval {
    var removed by remember { mutableStateOf<RemovedGroupItem?>(null) }
    var nextId by remember { mutableLongStateOf(0L) }
    return GroupRemoval(
        bar = GroupRemovalBar(
            removed = removed,
            isLeftHanded = isLeftHanded,
            onUndo = {
                removed?.let(state::restore)
                removed = null
            },
            onTimeout = { id -> if (removed?.id == id) removed = null }
        ),
        remove = { index, name ->
            nextId += 1
            removed = state.removeItemAt(index, nextId, name) ?: removed
        }
    )
}

/** Waits for the close animation, then dismisses; does nothing while the sheet is visible. */
@Composable
private fun DismissAfterAnimation(isVisible: Boolean, onDismiss: () -> Unit) {
    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(ANIMATION_DURATION_MS)
            onDismiss()
        }
    }
}

@AureolePreview
@Composable
private fun EditContainerScreenPreview() {
    AureoleLauncherTheme {
        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = ContainerModel(id = "preview_container"),
                allApps = emptyList(),
                onDismiss = {},
                onSave = {},
                isNew = true,
                areTipsSeen = false
            )
        )
    }
}

@Composable
private fun rememberGroupSheetNav(params: EditContainerScreenParams): GroupSheetNav = remember {
    GroupSheetNav(params.initialTab).also { start ->
        if (params.initialPage != GroupSheetPage.Tabs) start.open(params.initialPage)
    }
}

@Composable
private fun rememberGroupTips(params: EditContainerScreenParams): GroupTipFlow {
    val latestParams by rememberUpdatedState(params)
    return remember {
        val first = params.startTip ?: firstGroupTip(params.isNew, params.areTipsSeen)
        GroupTipFlow(first) { latestParams.onTipsSeen() }
    }
}

@Composable
private fun groupBaseConfig(
    params: EditContainerScreenParams,
    isVisible: Boolean,
    isLeftHanded: Boolean,
    onBackPress: () -> Unit,
    onDismiss: () -> Unit
): AureoleEditOverlayConfig {
    val homeActions = LocalHomeActions.current
    return AureoleEditOverlayConfig(
        title = stringResource(R.string.home_container_edit_title),
        onSettingsClick = { homeActions.onSettingsClick() },
        showHeaderAction = showsHomeSheetLogo(),
        hazeState = params.hazeState,
        isVisible = isVisible,
        animated = true,
        isLeftHanded = isLeftHanded,
        onBackPress = onBackPress,
        onDismiss = onDismiss,
        hasTabBottomBar = true,
        showsDismissFeedback = true
    )
}

/** Back: out of a page to its tab, or (on the tabs) closes the sheet; leaving "Add apps" with items moves the tips. */
private fun groupGoBack(
    nav: GroupSheetNav,
    state: EditContainerState,
    tips: GroupTipFlow,
    closer: EditContainerCloser
): () -> Unit = {
    val wasAddingApps = nav.page == GroupSheetPage.AddApps
    if (nav.back()) {
        if (wasAddingApps) tips.onAppsPageClosed(state.itemsList.size)
    } else {
        closer.closeOnce()
    }
}

/** The group above the sheet: the editor of the open folder or quick button, or the group as on the home. */
@Composable
private fun GroupPreviewSlot(params: EditContainerScreenParams, nav: GroupSheetNav, state: EditContainerState) {
    if (nav.showsItemPreview(state)) {
        GroupItemEditorPreview(nav = nav, state = state, allApps = params.allApps)
    } else {
        GroupSheetPreview(state = state, homeSize = params.homeSize) { nav.open(GroupSheetPage.AddApps) }
    }
}
