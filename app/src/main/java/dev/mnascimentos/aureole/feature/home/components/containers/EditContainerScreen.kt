package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.composable.showsHomeSheetLogo
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerCloser
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenMode
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerSheetActions
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemsEditor
import dev.mnascimentos.aureole.feature.home.components.containers.model.handleBack
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 300L

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContainerScreen(
    params: EditContainerScreenParams,
    modifier: Modifier = Modifier
) {
    val panel = params.panel
    val state = rememberEditContainerState(panel)
    var currentMode by remember { mutableStateOf(EditContainerScreenMode.MAIN) }
    var isVisible by remember { mutableStateOf(true) }
    val itemsEditor = remember { ManageItemsEditor() }

    val currentUiState = LocalHomeUiState.current
    val effectiveUiState =
        if (currentUiState.apps.isEmpty()) currentUiState.copy(apps = params.allApps) else currentUiState
    val isLeftHanded = effectiveUiState.isLeftHandedMode
    val homeActions = LocalHomeActions.current

    ProvideHomeUiState(effectiveUiState) {
        DismissAfterAnimation(isVisible, params.onDismiss)
        val latestParams by rememberUpdatedState(params)
        val closer = remember(state) {
            EditContainerCloser(
                save = { latestParams.onSave(state.toModel()) },
                close = { isVisible = false }
            )
        }

        AureoleEditOverlay(
            onDismissRequest = closer::closeOnce,
            modifier = modifier,
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(
                    title = currentMode.title(itemsEditor),
                    onSettingsClick = { homeActions.onSettingsClick() },
                    showHeaderAction = showsHomeSheetLogo(),
                    hazeState = params.hazeState,
                    isVisible = isVisible,
                    animated = true,
                    isLeftHanded = isLeftHanded,
                    onBackPress = { currentMode.handleSheetBack(itemsEditor, closer) { currentMode = it } },
                    onDismiss = closer::closeOnce
                ),
                previewContent = {
                    EditContainerPreviewSlot(currentMode, itemsEditor, state, params.allApps)
                },
                bottomBarContent = null,
                sheetContent = {
                    EditContainerSheetContent(
                        currentMode = currentMode,
                        state = state,
                        allApps = params.allApps,
                        itemsEditor = itemsEditor,
                        actions = EditContainerSheetActions(
                            onModeChange = { currentMode = it },
                            onCustomizeHome = {
                                // Saves what is being edited (like any close), then opens Customize.
                                closer.closeOnce()
                                homeActions.onOpenCustomizeBottomSheet()
                            }
                        )
                    )
                }
            )
        )
    }
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

/** Back from an item editor returns to the list of items, in the same sheet (RF-07); else the usual back. */
private fun EditContainerScreenMode.handleSheetBack(
    itemsEditor: ManageItemsEditor,
    closer: EditContainerCloser,
    goTo: (EditContainerScreenMode) -> Unit
) {
    val wentBack = this == EditContainerScreenMode.MANAGE_ITEMS && itemsEditor.back()
    if (!wentBack) handleBack(closer, goTo)
}

/** Title of the sheet: "Manage items" follows the page of [itemsEditor] (RF-07). */
@Composable
@ReadOnlyComposable
private fun EditContainerScreenMode.title(itemsEditor: ManageItemsEditor): String =
    if (this == EditContainerScreenMode.MANAGE_ITEMS) itemsEditor.title() else baseTitle()

@Composable
@ReadOnlyComposable
private fun EditContainerScreenMode.baseTitle(): String = when (this) {
    EditContainerScreenMode.MAIN -> stringResource(R.string.home_container_edit_title)
    EditContainerScreenMode.APPEARANCE -> stringResource(R.string.home_container_edit_appearance)
    EditContainerScreenMode.BEHAVIOR -> stringResource(R.string.home_container_edit_behavior)
    EditContainerScreenMode.MANAGE_ITEMS -> stringResource(R.string.home_container_edit_manage_items)
}

/** The item being edited in place, or else the live preview of the container. */
@Composable
private fun EditContainerPreviewSlot(
    currentMode: EditContainerScreenMode,
    itemsEditor: ManageItemsEditor,
    state: EditContainerState,
    allApps: List<AppInfo>
) {
    val isEditorPreview = currentMode == EditContainerScreenMode.MANAGE_ITEMS && itemsEditor.showsItemPreview(state)
    if (isEditorPreview) {
        ManageItemsEditorPreview(editor = itemsEditor, state = state, allApps = allApps)
    } else {
        EditContainerPreview(state = state)
    }
}

/** Live preview of the draft with its label, anchored above the sheet. */
@Composable
private fun EditContainerPreview(state: EditContainerState) {
    val isHorizontal = state.orientation.equals("Horizontal", ignoreCase = true)
    val previewModifier = if (isHorizontal) {
        Modifier
            .wrapContentWidth(Alignment.CenterHorizontally)
            .heightIn(min = ContainerPreviewMetrics.MinSize, max = ContainerPreviewMetrics.MaxSize)
    } else {
        Modifier
            .wrapContentHeight(Alignment.Bottom)
            .widthIn(min = ContainerPreviewMetrics.MinSize, max = ContainerPreviewMetrics.MaxSize)
    }
    AureolePreviewFrame {
        Container(
            config = state.toConfig(),
            onFolderClick = { _, _ -> },
            modifier = Modifier
                .padding(horizontal = AureoleDS.dimens.large)
                .aureoleDashedBorder()
                .padding(AureoleDS.dimens.xSmall)
                .then(previewModifier)
        )
    }
}

@Composable
private fun EditContainerSheetContent(
    currentMode: EditContainerScreenMode,
    state: EditContainerState,
    allApps: List<AppInfo>,
    itemsEditor: ManageItemsEditor,
    actions: EditContainerSheetActions
) {
    val onModeChange = actions.onModeChange
    when (currentMode) {
        EditContainerScreenMode.MAIN -> {
            MainSettingsContent(
                componentCount = state.itemsList.size,
                onOpenMode = onModeChange,
                onCustomizeHome = actions.onCustomizeHome
            )
        }
        EditContainerScreenMode.APPEARANCE, EditContainerScreenMode.BEHAVIOR -> {
            AppearanceSettingsContent(
                state = state,
                isBehaviorMode = currentMode == EditContainerScreenMode.BEHAVIOR,
                onBack = { onModeChange(EditContainerScreenMode.MAIN) }
            )
        }
        EditContainerScreenMode.MANAGE_ITEMS -> {
            ManageItemsContent(
                state = state,
                allApps = allApps,
                onBack = { onModeChange(EditContainerScreenMode.MAIN) },
                editor = itemsEditor
            )
        }
    }
}

@Composable
internal fun AppearanceSettingsContent(
    state: EditContainerState,
    isBehaviorMode: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (isBehaviorMode) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.home_container_edit_expand_cells),
                    color = AureoleDS.colors.onSurfaceMedium
                )
                Switch(checked = state.isExpandCell, onCheckedChange = { state.isExpandCell = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.home_container_edit_grid_folders),
                    color = AureoleDS.colors.onSurfaceMedium
                )
                Switch(checked = state.isGridFolderEnabled, onCheckedChange = { state.isGridFolderEnabled = it })
            }
        } else {
            EditContainerOrientationSelector(
                orientation = state.orientation,
                onOrientationSelected = { state.orientation = it }
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(stringResource(R.string.home_container_edit_bg_enabled), color = AureoleDS.colors.onSurfaceMedium)
                Switch(checked = state.isBackgroundEnabled, onCheckedChange = { state.isBackgroundEnabled = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    stringResource(R.string.home_container_edit_show_folder_labels),
                    color = AureoleDS.colors.onSurfaceMedium
                )
                Switch(checked = state.showFolderLabels, onCheckedChange = { state.showFolderLabels = it })
            }
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
        EditContainerBackButton(onClick = onBack)
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
                onSave = {}
            )
        )
    }
}
