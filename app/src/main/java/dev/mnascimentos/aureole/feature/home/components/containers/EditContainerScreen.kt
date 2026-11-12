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
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.composable.preview.aureoleDashedBorder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenMode
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.backTarget
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
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

    val currentUiState = LocalHomeUiState.current
    val effectiveUiState =
        if (currentUiState.apps.isEmpty()) currentUiState.copy(apps = params.allApps) else currentUiState
    val isLeftHanded = effectiveUiState.isLeftHandedMode
    val homeActions = LocalHomeActions.current

    ProvideHomeUiState(effectiveUiState) {
        if (!isVisible) {
            LaunchedEffect(isVisible) {
                delay(ANIMATION_DURATION_MS)
                params.onDismiss()
            }
        }
        val saveAndClose = {
            params.onSave(state.toModel())
            isVisible = false
        }

        AureoleEditOverlay(
            onDismissRequest = saveAndClose,
            modifier = modifier,
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(
                    title = currentMode.title(),
                    onSettingsClick = { homeActions.onSettingsClick() },
                    hazeState = params.hazeState,
                    isVisible = isVisible,
                    animated = true,
                    isLeftHanded = isLeftHanded,
                    onBackPress = { currentMode.backTarget()?.let { currentMode = it } ?: saveAndClose() }
                ),
                previewContent = { EditContainerPreview(state = state) },
                bottomBarContent = null,
                sheetContent = {
                    EditContainerSheetContent(
                        currentMode = currentMode,
                        state = state,
                        allApps = params.allApps,
                        onModeChange = { currentMode = it }
                    )
                }
            )
        )
    }
}

@Composable
@ReadOnlyComposable
private fun EditContainerScreenMode.title(): String = when (this) {
    EditContainerScreenMode.MAIN -> stringResource(R.string.home_container_edit_title)
    EditContainerScreenMode.APPEARANCE -> stringResource(R.string.home_container_edit_appearance)
    EditContainerScreenMode.BEHAVIOR -> stringResource(R.string.home_container_edit_behavior)
    EditContainerScreenMode.MANAGE_ITEMS -> stringResource(R.string.home_container_edit_manage_items)
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
    onModeChange: (EditContainerScreenMode) -> Unit
) {
    when (currentMode) {
        EditContainerScreenMode.MAIN -> {
            MainSettingsContent(
                componentCount = state.itemsList.size,
                onAppearanceClick = { onModeChange(EditContainerScreenMode.APPEARANCE) },
                onBehaviorClick = { onModeChange(EditContainerScreenMode.BEHAVIOR) },
                onManageItemsClick = { onModeChange(EditContainerScreenMode.MANAGE_ITEMS) }
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
                onBack = { onModeChange(EditContainerScreenMode.MAIN) }
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
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text(stringResource(R.string.home_container_edit_back))
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
                onSave = {}
            )
        )
    }
}
