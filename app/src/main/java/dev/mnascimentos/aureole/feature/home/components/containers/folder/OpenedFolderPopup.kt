package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import android.content.Intent
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleHeaderBanner
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.GridFolderPopupParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.settings.SettingsActivity

private const val GRID_MAX_3 = 3
private const val GRID_MAX_6 = 6
private const val GRID_COLS = 3

@Composable
fun OpenedFolderPopup(
    config: OpenedFolderPopupConfig,
    modifier: Modifier = Modifier
) {
    val folder = config.folder
    val isGridMode = config.isGridFolderEnabled || folder.displayAsGrid

    val appsInFolder = remember(folder, config.allApps) {
        folder.appPackageNames.mapNotNull { pkgName ->
            config.allApps.find { it.packageName == pkgName }
        }
    }

    var showActionsByLongPress by remember { mutableStateOf(false) }
    val isActionsVisible = appsInFolder.isEmpty() || showActionsByLongPress

    if (isGridMode) {
        GridFolderPopup(
            params = GridFolderPopupParams(
                folder = folder,
                appsInFolder = appsInFolder,
                actions = config.actions,
                isActionsVisible = isActionsVisible,
                onToggleActions = { showActionsByLongPress = !showActionsByLongPress },
                hazeState = config.hazeState,
                isHazeEnabled = true
            ),
            modifier = modifier
        )
    } else {
        StandardFolderPopupBox(
            config = config,
            appsInFolder = appsInFolder,
            isActionsVisible = isActionsVisible,
            modifier = modifier
        )
    }
}

@Composable
private fun StandardFolderPopupBox(
    config: OpenedFolderPopupConfig,
    appsInFolder: List<AppInfo>,
    isActionsVisible: Boolean,
    modifier: Modifier = Modifier
) {
    val uiState = LocalHomeUiState.current
    val homeActions = LocalHomeActions.current
    val context = LocalContext.current

    AureolePopupBox(
        hazeState = null,
        modifier = modifier
            .widthIn(min = AureoleDS.dimens.popupWidthMin, max = AureoleDS.dimens.popupWidthMax)
            .heightIn(max = AureoleDS.dimens.popupHeightMax)
    ) {
        AureoleHeaderBanner(
            title = config.folder.name,
            onLogoClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            }
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AureoleDS.dimens.small, vertical = AureoleDS.dimens.xSmall)
        ) {
            OpenedFolderActions(
                isVisible = isActionsVisible,
                onAddAppsClick = config.actions.onAddAppsClick,
                onEditFolderClick = config.actions.onEditFolderClick
            )

            if (config.folder.displayAsGrid) {
                OpenedFolderGrid(
                    folder = config.folder,
                    appsInFolder = appsInFolder,
                    onAppClick = config.actions.onAppClick
                )
            } else {
                OpenedFolderAppList(
                    appsInFolder = appsInFolder,
                    isLeftHandedMode = uiState.isLeftHandedMode,
                    onAppClick = config.actions.onAppClick,
                    onAppLongClick = { app -> homeActions.onOpenFolderAppBottomSheet(app, config.folder) }
                )
            }
        }
    }
}

@Composable
private fun OpenedFolderGrid(
    folder: AppFolder,
    appsInFolder: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    val homeActions = LocalHomeActions.current
    if (appsInFolder.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AureoleDS.dimens.large),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum aplicativo na pasta",
                style = AureoleDS.typography.bodySmall,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    } else {
        val gridHeight = when {
            appsInFolder.size <= GRID_MAX_3 -> AureoleDS.dimens.popupWidthMin / 2 + AureoleDS.dimens.xxLarge
            appsInFolder.size <= GRID_MAX_6 -> AureoleDS.dimens.popupWidthMax
            else -> AureoleDS.dimens.popupWidthMax + AureoleDS.dimens.radiusFull
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(GRID_COLS),
            contentPadding = PaddingValues(horizontal = AureoleDS.dimens.xxSmall, vertical = AureoleDS.dimens.xSmall),
            horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.xSmall),
            verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = gridHeight)
        ) {
            items(
                items = appsInFolder,
                key = { it.packageName },
                contentType = { "opened_folder_popup_app_grid" }
            ) { app ->
                OpenedFolderGridAppItem(
                    app = app,
                    onClick = { onAppClick(app) },
                    onLongClick = { homeActions.onOpenFolderAppBottomSheet(app, folder) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderGridAppItem(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = AureoleDS.dimens.xxSmall, horizontal = AureoleDS.dimens.minimal)
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(AureoleDS.dimens.xxLarge)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))
        Text(
            text = app.label,
            style = AureoleDS.typography.labelSmall,
            color = AureoleDS.colors.onSurfaceHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@AureolePreview
@Composable
private fun OpenedFolderPopupPreview() {
    val mockApp = AppInfo(
        label = "Chat",
        packageName = "com.example.chat",
        componentName = ComponentName("com.example.chat", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val mockFolder = AppFolder(
        id = "1",
        name = "Social",
        appPackageNames = listOf(mockApp.packageName)
    )
    val actions = FolderPopupActions(
        onDismiss = {},
        onAppClick = {},
        onAddAppsClick = {},
        onEditFolderClick = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            OpenedFolderPopup(
                config = OpenedFolderPopupConfig(
                    folder = mockFolder,
                    allApps = listOf(mockApp),
                    actions = actions
                )
            )
        }
    }
}
