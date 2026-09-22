package dev.mnascimentos.aureole.feature.home.folder

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val HAZE_ALPHA_MULTIPLIER = 0.8f
private const val HAZE_MIN_ALPHA = 0.25f
private const val HAZE_MAX_ALPHA = 0.95f
private const val OPAQUE_ALPHA = 1f

@Composable
fun OpenedFolderPopup(
    folder: AppFolder,
    allApps: List<AppInfo>,
    actions: FolderPopupActions,
    modifier: Modifier = Modifier,
    isGridFolderEnabled: Boolean = false,
    hazeState: HazeState? = null
) {
    val uiState = LocalHomeUiState.current
    val isHazeEnabled = uiState.isHazeEnabled
    val hazeOpacity = uiState.hazeOpacity

    val isGridMode = isGridFolderEnabled || folder.displayAsGrid

    val appsInFolder = folder.appPackageNames.mapNotNull { pkgName ->
        allApps.find { it.packageName == pkgName }
    }.sortedBy { it.label.lowercase() }

    var showActionsByLongPress by remember { mutableStateOf(false) }
    val isActionsVisible = appsInFolder.isEmpty() || showActionsByLongPress

    val hazeModifier = if (isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 24.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceContainerHigh.copy(alpha = hazeOpacity))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val backgroundAlpha = if (isHazeEnabled) {
        (hazeOpacity * HAZE_ALPHA_MULTIPLIER).coerceIn(HAZE_MIN_ALPHA, HAZE_MAX_ALPHA)
    } else {
        OPAQUE_ALPHA
    }

    if (isGridMode) {
        GridStyleExpandedFolderPopup(
            folder = folder,
            appsInFolder = appsInFolder,
            actions = actions,
            isActionsVisible = isActionsVisible,
            onToggleActions = { showActionsByLongPress = !showActionsByLongPress },
            hazeState = hazeState,
            isHazeEnabled = isHazeEnabled,
            hazeOpacity = hazeOpacity,
            modifier = modifier
        )
    } else {
        Box(
            modifier = modifier
                .widthIn(min = 210.dp, max = 250.dp)
                .heightIn(max = 380.dp)
                .clip(RoundedCornerShape(18.dp))
                .then(hazeModifier)
                .background(
                    MaterialTheme.colorScheme.surfaceContainerHigh.copy(
                        alpha = backgroundAlpha
                    )
                )
                .padding(12.dp)
        ) {
            OpenedFolderPopupContent(
                folderName = folder.name,
                appsInFolder = appsInFolder,
                isActionsVisible = isActionsVisible,
                isGridMode = false,
                actions = actions,
                onToggleActions = { showActionsByLongPress = !showActionsByLongPress }
            )
        }
    }
}

@Suppress("UnusedParameter")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GridStyleExpandedFolderPopup(
    folder: AppFolder,
    appsInFolder: List<AppInfo>,
    actions: FolderPopupActions,
    isActionsVisible: Boolean,
    onToggleActions: () -> Unit,
    hazeState: HazeState?,
    isHazeEnabled: Boolean,
    hazeOpacity: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.88f)
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Large Header: Centered Folder Name & Add Button (Matching Image 2)
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
                        onLongClick = onToggleActions
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
            isVisible = isActionsVisible,
            onAddAppsClick = actions.onAddAppsClick,
            onEditFolderClick = actions.onEditFolderClick
        )

        // Card Container with 3-column app grid (Matching Image 2)
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

        val gridMaxHeight = when {
            appsInFolder.isEmpty() -> 100.dp
            appsInFolder.size <= 3 -> 120.dp
            appsInFolder.size <= 6 -> 230.dp
            appsInFolder.size <= 9 -> 340.dp
            else -> 440.dp
        }

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
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
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
private fun OpenedFolderPopupContent(
    folderName: String,
    appsInFolder: List<AppInfo>,
    isActionsVisible: Boolean,
    isGridMode: Boolean,
    actions: FolderPopupActions,
    onToggleActions: () -> Unit
) {
    val isLeftHandedMode = LocalHomeUiState.current.isLeftHandedMode
    Column(modifier = Modifier.fillMaxWidth()) {
        OpenedFolderHeader(
            title = folderName,
            onDismiss = actions.onDismiss,
            onToggleActions = onToggleActions
        )

        Spacer(modifier = Modifier.height(8.dp))

        OpenedFolderActions(
            isVisible = isActionsVisible,
            onAddAppsClick = actions.onAddAppsClick,
            onEditFolderClick = actions.onEditFolderClick
        )

        if (isGridMode) {
            OpenedFolderGrid(
                appsInFolder = appsInFolder,
                onAppClick = actions.onAppClick
            )
        } else {
            OpenedFolderAppList(
                appsInFolder = appsInFolder,
                isLeftHandedMode = isLeftHandedMode,
                onAppClick = actions.onAppClick
            )
        }
    }
}

@Composable
private fun OpenedFolderGrid(
    appsInFolder: List<AppInfo>,
    onAppClick: (AppInfo) -> Unit
) {
    if (appsInFolder.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Nenhum aplicativo na pasta",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    } else {
        val gridHeight = when {
            appsInFolder.size <= 3 -> 110.dp
            appsInFolder.size <= 6 -> 210.dp
            else -> 310.dp
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = gridHeight)
        ) {
            items(appsInFolder, key = { it.packageName }) { app ->
                OpenedFolderGridAppItem(
                    app = app,
                    onClick = { onAppClick(app) }
                )
            }
        }
    }
}

@Composable
private fun OpenedFolderGridAppItem(
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
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp, horizontal = 2.dp)
    ) {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderHeader(
    title: String,
    onDismiss: () -> Unit,
    onToggleActions: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .combinedClickable(
                    onClick = {},
                    onLongClick = onToggleActions
                )
                .padding(vertical = 4.dp, horizontal = 4.dp)
        )

        IconButton(
            onClick = onDismiss,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Folder",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun OpenedFolderActions(
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
private fun OpenedFolderAppList(
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

@AureolePreview
@Composable
fun OpenedFolderPopupPreview() {
    val mockApp = AppInfo(
        label = "Chat",
        packageName = "com.example.chat",
        componentName = ComponentName("com.example.chat", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
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
                folder = mockFolder,
                allApps = listOf(mockApp),
                actions = actions
            )
        }
    }
}
