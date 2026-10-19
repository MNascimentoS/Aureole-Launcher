package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItemParams
import dev.mnascimentos.aureole.core.designsystem.icons.Add
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState

@Composable
fun OpenedFolderActions(
    isVisible: Boolean,
    onAddAppsClick: () -> Unit,
    onEditFolderClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            AureolePopupMenuItem(
                title = "Add Apps",
                params = AureolePopupMenuItemParams(
                    icon = { m -> AureoleDS.icons.Add(m) }
                ),
                onClick = onAddAppsClick
            )

            AureolePopupMenuItem(
                title = "Edit Folder",
                params = AureolePopupMenuItemParams(
                    icon = { m -> AureoleDS.icons.Edit(m) }
                ),
                onClick = onEditFolderClick
            )
        }
    }
}

@Composable
fun OpenedFolderAppList(
    appsInFolder: List<AppInfo>,
    isLeftHandedMode: Boolean,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    onAppLongClick: ((AppInfo) -> Unit)? = null
) {
    if (appsInFolder.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No apps in folder",
                style = AureoleDS.typography.bodySmall,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = 2.dp),
            modifier = modifier.fillMaxWidth()
        ) {
            items(
                items = appsInFolder,
                key = { it.packageName },
                contentType = { "opened_folder_app_row" }
            ) { app ->
                OpenedFolderAppItemRow(
                    app = app,
                    isLeftHandedMode = isLeftHandedMode,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick?.invoke(app) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderAppItemRow(
    app: AppInfo,
    isLeftHandedMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val uiState = LocalHomeUiState.current
    val iconBitmap: ImageBitmap = app.getDisplayIconBitmap(
        isThemed = uiState.isThemedAppIconsEnabled,
        tintColor = AureoleDS.colors.onSurfaceMedium
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLeftHandedMode) {
            Text(
                text = app.label,
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceHigh,
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
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceHigh,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@AureolePreview
@Composable
private fun OpenedFolderAppListPreview() {
    val sampleApps = listOf(
        AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        )
    )
    AureoleLauncherTheme {
        OpenedFolderAppList(
            appsInFolder = sampleApps,
            isLeftHandedMode = false,
            onAppClick = {}
        )
    }
}
