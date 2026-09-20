package dev.mnascimentos.aureole.ui.components

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.data.model.AppFolder
import dev.mnascimentos.aureole.data.model.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OpenedFolderPopup(
    folder: AppFolder,
    allApps: List<AppInfo>,
    actions: FolderPopupActions,
    modifier: Modifier = Modifier,
    isLeftHandedMode: Boolean = false
) {
    val appsInFolder = folder.appPackageNames.mapNotNull { pkgName ->
        allApps.find { it.packageName == pkgName }
    }.sortedBy { it.label.lowercase() }

    var showActionsByLongPress by remember { mutableStateOf(false) }
    val isActionsVisible = appsInFolder.isEmpty() || showActionsByLongPress

    Box(
        modifier = modifier
            .widthIn(min = 210.dp, max = 250.dp)
            .heightIn(max = 380.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            OpenedFolderHeader(
                title = folder.name,
                onDismiss = actions.onDismiss,
                onToggleActions = { showActionsByLongPress = !showActionsByLongPress }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OpenedFolderActions(
                isVisible = isActionsVisible,
                onAddAppsClick = actions.onAddAppsClick,
                onEditFolderClick = actions.onEditFolderClick
            )

            OpenedFolderAppList(
                appsInFolder = appsInFolder,
                isLeftHandedMode = isLeftHandedMode,
                onAppClick = actions.onAppClick
            )
        }
    }
}

data class FolderPopupActions(
    val onDismiss: () -> Unit,
    val onAppClick: (AppInfo) -> Unit,
    val onAddAppsClick: () -> Unit,
    val onEditFolderClick: () -> Unit
)

// Backward compatibility overload
@Suppress("LongParameterList")
@Composable
fun OpenedFolderPopup(
    folder: AppFolder,
    allApps: List<AppInfo>,
    onDismiss: () -> Unit,
    onAppClick: (AppInfo) -> Unit,
    onAddAppsClick: () -> Unit,
    onEditFolderClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLeftHandedMode: Boolean = false
) {
    OpenedFolderPopup(
        folder = folder,
        allApps = allApps,
        actions = FolderPopupActions(
            onDismiss = onDismiss,
            onAppClick = onAppClick,
            onAddAppsClick = onAddAppsClick,
            onEditFolderClick = onEditFolderClick
        ),
        modifier = modifier,
        isLeftHandedMode = isLeftHandedMode
    )
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
