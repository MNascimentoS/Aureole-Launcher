package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.utils.LocalHazeState
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.GridFolderPopupParams

private const val GRID_COLUMNS = 4
private val POPUP_HAZE_BLUR_RADIUS = 20.dp
private const val POPUP_HAZE_ALPHA = 0.55f

@NonRestartableComposable
@Composable
fun GridFolderPopup(
    params: GridFolderPopupParams,
    modifier: Modifier = Modifier
) {
    GridFolderContainer(
        params = params,
        modifier = modifier
    )
}

@Composable
private fun GridFolderContainer(
    params: GridFolderPopupParams,
    modifier: Modifier = Modifier
) {
    val effectiveHazeState = params.hazeState ?: LocalHazeState.current

    val hazeModifier = if (params.isHazeEnabled && effectiveHazeState != null) {
        Modifier.hazeEffect(
            state = effectiveHazeState,
            style = HazeStyle(
                blurRadius = POPUP_HAZE_BLUR_RADIUS,
                tint = HazeTint(Color.Black.copy(alpha = POPUP_HAZE_ALPHA))
            )
        ) { blurEnabled = true }
    } else {
        Modifier
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .then(hazeModifier)
            .background(
                color = AureoleDS.colors.surface,
                shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
            )
            .padding(AureoleDS.dimens.medium)
    ) {
        if (params.appsInFolder.isEmpty()) {
            EmptyFolderGridContent(maxListHeightDp = 400.dp)
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                PopulatedFolderGrid(
                    params = params,
                    appsInFolder = params.appsInFolder,
                    actions = params.actions
                )
            }
        }
    }
}

@Composable
private fun EmptyFolderGridContent(maxListHeightDp: Dp) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(maxListHeightDp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nenhum aplicativo na pasta",
            style = AureoleDS.typography.bodyMedium,
            color = AureoleDS.colors.onSurfaceMedium
        )
    }
}

@Composable
private fun PopulatedFolderGrid(
    params: GridFolderPopupParams,
    appsInFolder: List<AppInfo>,
    actions: FolderPopupActions
) {
    val homeActions = LocalHomeActions.current
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = appsInFolder,
            key = { it.packageName },
            contentType = { "opened_folder_app_grid" }
        ) { app ->
            OpenedFolderSamsungGridAppItem(
                app = app,
                onClick = { actions.onAppClick(app) },
                onLongClick = { homeActions.onOpenFolderAppBottomSheet(app, params.folder) }
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderSamsungGridAppItem(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val uiState = LocalHomeUiState.current
    val iconBitmap: ImageBitmap = app.getDisplayIconBitmap(
        isThemed = uiState.isThemedAppIconsEnabled,
        tintColor = AureoleDS.colors.onSurfaceMedium
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 6.dp)
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(52.dp)
                .clip(RoundedCornerShape(16.dp))
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label,
            style = AureoleDS.typography.labelSmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@AureolePreview
@Composable
private fun GridFolderPopupPreview() {
    AureoleLauncherTheme {
        GridFolderPopup(
            params = GridFolderPopupParams(
                folder = AppFolder(id = "1", panelId = "1", name = "Preview Folder"),
                appsInFolder = emptyList(),
                actions = FolderPopupActions(
                    onDismiss = {},
                    onAppClick = {},
                    onAddAppsClick = {},
                    onEditFolderClick = {}
                ),
                isActionsVisible = false,
                onToggleActions = {},
                hazeState = null,
                isHazeEnabled = false
            )
        )
    }
}
