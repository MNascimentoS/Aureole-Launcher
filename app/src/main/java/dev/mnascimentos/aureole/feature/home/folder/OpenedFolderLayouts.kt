package dev.mnascimentos.aureole.feature.home.folder

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.feature.home.folder.model.FolderPopupActions

private const val GRID_MAX_3 = 3
private const val GRID_MAX_6 = 6
private const val GRID_MAX_9 = 9
private const val GRID_COLUMNS = 3
private const val GRID_WIDTH_FRACTION = 0.88f

data class GridFolderPopupParams(
    val folder: AppFolder,
    val appsInFolder: List<AppInfo>,
    val actions: FolderPopupActions,
    val isActionsVisible: Boolean,
    val onToggleActions: () -> Unit,
    val hazeState: HazeState?,
    val isHazeEnabled: Boolean
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GridStyleExpandedFolderPopup(
    params: GridFolderPopupParams,
    modifier: Modifier = Modifier
) {
    val folder = params.folder
    val appsInFolder = params.appsInFolder
    val actions = params.actions

    Column(
        modifier = modifier
            .fillMaxWidth(GRID_WIDTH_FRACTION)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Text(
                text = folder.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.Center)
                    .combinedClickable(
                        onClick = {},
                        onLongClick = params.onToggleActions
                    )
            )

            IconButton(
                onClick = actions.onAddAppsClick,
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .size(38.dp)
                    .background(Color.White.copy(alpha = 0.2f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Apps",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        OpenedFolderActions(
            isVisible = params.isActionsVisible,
            onAddAppsClick = actions.onAddAppsClick,
            onEditFolderClick = actions.onEditFolderClick
        )

        GridFolderContainer(
            appsInFolder = appsInFolder,
            actions = actions,
            hazeState = params.hazeState,
            isHazeEnabled = params.isHazeEnabled
        )
    }
}

@Composable
private fun GridFolderContainer(
    appsInFolder: List<AppInfo>,
    actions: FolderPopupActions,
    hazeState: HazeState?,
    isHazeEnabled: Boolean
) {
    val hazeModifier = if (isHazeEnabled && hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 32.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.85f))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val gridMaxHeight = calculateGridMaxHeight(appsInFolder.size)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 120.dp, max = gridMaxHeight)
            .clip(RoundedCornerShape(32.dp))
            .then(hazeModifier)
            .background(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = 0.88f))
            .padding(16.dp)
    ) {
        if (appsInFolder.isEmpty()) {
            EmptyFolderGridHint()
        } else {
            PopulatedFolderGrid(appsInFolder = appsInFolder, actions = actions)
        }
    }
}

private fun calculateGridMaxHeight(appCount: Int): Dp {
    return when {
        appCount == 0 -> 100.dp
        appCount <= GRID_MAX_3 -> 120.dp
        appCount <= GRID_MAX_6 -> 230.dp
        appCount <= GRID_MAX_9 -> 340.dp
        else -> 440.dp
    }
}

@Composable
private fun EmptyFolderGridHint() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Nenhum aplicativo na pasta",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PopulatedFolderGrid(
    appsInFolder: List<AppInfo>,
    actions: FolderPopupActions
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(GRID_COLUMNS),
        contentPadding = PaddingValues(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(appsInFolder, key = { it.packageName }) { app ->
            OpenedFolderSamsungGridAppItem(
                app = app,
                onClick = { actions.onAppClick(app) }
            )
        }
    }
}

@Composable
private fun OpenedFolderSamsungGridAppItem(
    app: AppInfo,
    onClick: () -> Unit
) {
    val iconBitmap: ImageBitmap = remember(app.packageName) {
        app.getIconBitmap()
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
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
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun OpenedFolderActions(
    isVisible: Boolean,
    onAddAppsClick: () -> Unit,
    onEditFolderClick: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEditFolderClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit Folder",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }

            Button(
                onClick = onAddAppsClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Add Apps",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun OpenedFolderAppList(
    appsInFolder: List<AppInfo>,
    isLeftHandedMode: Boolean,
    onAppClick: (AppInfo) -> Unit
) {
    if (appsInFolder.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No apps in folder",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(appsInFolder, key = { it.packageName }) { app ->
                OpenedFolderAppItemRow(
                    app = app,
                    isLeftHandedMode = isLeftHandedMode,
                    onClick = { onAppClick(app) }
                )
            }
        }
    }
}

@Composable
private fun OpenedFolderAppItemRow(
    app: AppInfo,
    isLeftHandedMode: Boolean,
    onClick: () -> Unit
) {
    val iconBitmap: ImageBitmap = remember(app.packageName) {
        app.getIconBitmap()
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLeftHandedMode) {
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
        } else {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = app.label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
