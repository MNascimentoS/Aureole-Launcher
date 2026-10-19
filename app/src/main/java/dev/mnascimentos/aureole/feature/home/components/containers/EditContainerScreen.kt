package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.icons.Click
import dev.mnascimentos.aureole.core.designsystem.icons.ColorFill
import dev.mnascimentos.aureole.core.designsystem.icons.DragMenu
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerConfig
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenMode
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import kotlinx.coroutines.delay

private const val ANIMATION_DURATION_MS = 300

@Suppress("LongMethod") // Screen composable managing container editor state and options
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditContainerScreen(
    params: EditContainerScreenParams,
    modifier: Modifier = Modifier
) {
    val panel = params.panel
    val allApps = params.allApps
    val onDismiss = params.onDismiss
    val onSave = params.onSave
    val hazeState = params.hazeState
    val state = rememberEditContainerState(panel)
    var currentMode by remember { mutableStateOf(EditContainerScreenMode.MAIN) }
    var isVisible by remember { mutableStateOf(true) }

    val config = ContainerConfig(
        panelId = panel.id,
        items = state.itemsList,
        folders = state.folders.map { it.copy(displayAsGrid = state.isGridFolderEnabled) },
        appPackageNames = state.itemsList.mapNotNull { it.packageName },
        position = state.position,
        orientation = state.orientation,
        isBackgroundEnabled = state.isBackgroundEnabled,
        isExpandCell = state.isExpandCell,
        showAddFolderButton = state.showAddFolderButton,
        showFolderLabels = state.showFolderLabels,
        isGridFolderEnabled = state.isGridFolderEnabled,
        hideText = state.hideText,
        textColor = state.textColor,
        backgroundColor = state.backgroundColor,
        strokeEnabled = state.strokeEnabled,
        strokeColor = state.strokeColor,
        folderColor = state.folderColor,
        folderStrokeEnabled = state.folderStrokeEnabled,
        scrollUpShortcut = state.scrollUpShortcut,
        scrollDownShortcut = state.scrollDownShortcut
    )

    val currentUiState = LocalHomeUiState.current
    val effectiveUiState = if (currentUiState.apps.isEmpty()) currentUiState.copy(apps = allApps) else currentUiState

    CompositionLocalProvider(LocalHomeUiState provides effectiveUiState) {
        if (!isVisible) {
            LaunchedEffect(isVisible) {
                delay(ANIMATION_DURATION_MS.toLong())
                onDismiss()
            }
        }

        val isHorizontal = state.orientation.equals("Horizontal", ignoreCase = true)
        val previewModifier = if (isHorizontal) {
            Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
                .heightIn(min = AureoleDS.dimens.folderButtonMedium, max = AureoleDS.dimens.previewContainerMax)
        } else {
            Modifier
                .wrapContentHeight(Alignment.Bottom)
                .widthIn(min = AureoleDS.dimens.folderButtonMedium, max = AureoleDS.dimens.previewContainerMax)
        }

        val titleText = when (currentMode) {
            EditContainerScreenMode.MAIN -> "Container settings"
            EditContainerScreenMode.APPEARANCE -> "Aparência"
            EditContainerScreenMode.BEHAVIOR -> "Comportamento"
            EditContainerScreenMode.MANAGE_ITEMS -> "Gerenciar Itens"
        }

        val homeActions = LocalHomeActions.current

        AureoleEditOverlay(
            onDismissRequest = {
                onSave(state.toModel())
                isVisible = false
            },
            modifier = modifier,
            params = AureoleEditOverlayParams(
                config = AureoleEditOverlayConfig(
                    title = titleText,
                    onSettingsClick = { homeActions.onSettingsClick() },
                    hazeState = hazeState,
                    isVisible = isVisible,
                    animated = true,
                    onBackPress = {
                        if (currentMode != EditContainerScreenMode.MAIN) {
                            currentMode = EditContainerScreenMode.MAIN
                        } else {
                            onSave(state.toModel())
                            isVisible = false
                        }
                    }
                ),
                previewContent = {
                    Container(config = config, onFolderClick = { _, _ -> }, modifier = previewModifier)
                },
                sheetContent = {
                    EditContainerSheetContent(
                        currentMode = currentMode,
                        state = state,
                        allApps = allApps,
                        onModeChange = { currentMode = it },
                        onSaveAndClose = {
                            onSave(state.toModel())
                            isVisible = false
                        }
                    )
                }
            )
        )
    }
}

@Composable
private fun EditContainerSheetContent(
    currentMode: EditContainerScreenMode,
    state: EditContainerState,
    allApps: List<AppInfo>,
    onModeChange: (EditContainerScreenMode) -> Unit,
    onSaveAndClose: () -> Unit
) {
    when (currentMode) {
        EditContainerScreenMode.MAIN -> {
            MainSettingsContent(
                onAppearanceClick = { onModeChange(EditContainerScreenMode.APPEARANCE) },
                onBehaviorClick = { onModeChange(EditContainerScreenMode.BEHAVIOR) },
                onManageItemsClick = { onModeChange(EditContainerScreenMode.MANAGE_ITEMS) },
                onSave = onSaveAndClose
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
private fun MainSettingsContent(
    onAppearanceClick: () -> Unit,
    onBehaviorClick: () -> Unit,
    onManageItemsClick: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        MainSettingsNavigationRow(
            title = "Appearance",
            onClick = onAppearanceClick,
            icon = {
                AureoleDS.icons.ColorFill(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }
        )

        MainSettingsNavigationRow(
            title = "Behavior",
            onClick = onBehaviorClick,
            icon = {
                AureoleDS.icons.Click(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }
        )

        MainSettingsNavigationRow(
            title = "Manage Items",
            onClick = onManageItemsClick,
            icon = {
                AureoleDS.icons.DragMenu(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }
        )

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
        Button(
            onClick = onSave,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }
    }
}

@Composable
private fun MainSettingsNavigationRow(
    title: String,
    onClick: () -> Unit,
    icon: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick,
                role = Role.Button
            )
            .semantics(mergeDescendants = true) {}
            .padding(vertical = AureoleDS.dimens.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon()
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        Text(text = title, color = AureoleDS.colors.onSurfaceHigh)
    }
}

@Composable
private fun AppearanceSettingsContent(
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
                Text("Expand cells", color = AureoleDS.colors.onSurfaceMedium)
                Switch(checked = state.isExpandCell, onCheckedChange = { state.isExpandCell = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Grid folders", color = AureoleDS.colors.onSurfaceMedium)
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
                Text("Background enabled", color = AureoleDS.colors.onSurfaceMedium)
                Switch(checked = state.isBackgroundEnabled, onCheckedChange = { state.isBackgroundEnabled = it })
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = AureoleDS.dimens.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Show folder labels", color = AureoleDS.colors.onSurfaceMedium)
                Switch(checked = state.showFolderLabels, onCheckedChange = { state.showFolderLabels = it })
            }
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.large))
        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Voltar")
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
