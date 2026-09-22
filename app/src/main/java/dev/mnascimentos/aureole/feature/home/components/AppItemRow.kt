package dev.mnascimentos.aureole.feature.home.components

import android.content.ComponentName
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val FAVORITE_INACTIVE_ALPHA = 0.4f
private const val COLOR_MAX_FACTOR = 255
private val ICON_OUTER_SIZE = 42.dp
private val ICON_INNER_SIZE = 26.dp
private val ICON_CORNER_RADIUS = 12.dp
private val ICON_PADDING = 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemRow(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isFavorite: Boolean = false,
    actions: AppItemRowActions = AppItemRowActions()
) {
    var showMenu by remember { mutableStateOf(false) }
    val iconBitmap: ImageBitmap = remember(app.packageName) {
        app.getIconBitmap()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppItemRowContent(
                app = app,
                iconBitmap = iconBitmap,
            )
        }

        AppItemRowDropdownMenu(
            app = app,
            showMenu = showMenu,
            isFavorite = isFavorite,
            actions = actions,
            onDismiss = { showMenu = false }
        )
    }
}

@Composable
private fun RowScope.AppItemRowContent(
    app: AppInfo,
    iconBitmap: ImageBitmap,
) {
    val uiState = LocalHomeUiState.current
    val onPrimaryContainerColor = MaterialTheme.colorScheme.onPrimaryContainer
    val displayBitmap = remember(app.packageName, uiState.isThemedAppIconsEnabled, onPrimaryContainerColor) {
        if (uiState.isThemedAppIconsEnabled) {
            val argb = Color.argb(
                (onPrimaryContainerColor.alpha * COLOR_MAX_FACTOR).toInt(),
                (onPrimaryContainerColor.red * COLOR_MAX_FACTOR).toInt(),
                (onPrimaryContainerColor.green * COLOR_MAX_FACTOR).toInt(),
                (onPrimaryContainerColor.blue * COLOR_MAX_FACTOR).toInt()
            )
            app.getThemedIconBitmap(argb)
        } else {
            iconBitmap
        }
    }

    val isLeftHandedMode = uiState.isLeftHandedMode

    val appLabel = @Composable {
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (isLeftHandedMode) TextAlign.End else TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }

    val appIcon = @Composable {
        AppItemIcon(bitmap = displayBitmap, label = app.label, isThemed = uiState.isThemedAppIconsEnabled)
    }

    if (isLeftHandedMode) {
        appLabel()
        Spacer(modifier = Modifier.width(16.dp))
        appIcon()
    } else {
        appIcon()
        Spacer(modifier = Modifier.width(16.dp))
        appLabel()
    }
}

@Composable
private fun AppItemIcon(
    bitmap: ImageBitmap,
    label: String,
    isThemed: Boolean
) {
    if (isThemed) {
        Box(
            modifier = Modifier
                .size(ICON_OUTER_SIZE)
                .clip(RoundedCornerShape(ICON_CORNER_RADIUS))
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(ICON_PADDING),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = label,
                modifier = Modifier.size(ICON_INNER_SIZE)
            )
        }
    } else {
        Image(
            bitmap = bitmap,
            contentDescription = label,
            modifier = Modifier.size(ICON_OUTER_SIZE)
        )
    }
}

@Composable
private fun AppItemRowDropdownMenu(
    app: AppInfo,
    showMenu: Boolean,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = onDismiss
    ) {
        if (actions.onToggleFavorite != null) {
            FavoriteMenuItem(
                isFavorite = isFavorite,
                onClick = {
                    actions.onToggleFavorite.invoke(app.packageName)
                    onDismiss()
                }
            )
        }

        if (actions.onEditFavoritesClick != null) {
            EditFavoritesMenuItem(
                onClick = {
                    actions.onEditFavoritesClick.invoke()
                    onDismiss()
                }
            )
        }

        if (actions.onAppInfoClick != null) {
            AppInfoMenuItem(
                onClick = {
                    actions.onAppInfoClick.invoke(app)
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun FavoriteMenuItem(
    isFavorite: Boolean,
    onClick: () -> Unit
) {
    val favTint = if (isFavorite) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = FAVORITE_INACTIVE_ALPHA)
    }
    DropdownMenuItem(
        text = { Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = favTint
            )
        },
        onClick = onClick
    )
}

@Composable
private fun EditFavoritesMenuItem(
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text("Editar Favoritos") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = onClick
    )
}

@Composable
private fun AppInfoMenuItem(
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text("App Info") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        onClick = onClick
    )
}

@AureolePreview
@Composable
fun AppItemRowPreview() {
    val mockApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockUiState = MainUiState(
        apps = listOf(mockApp),
        favoriteApps = listOf(mockApp)
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides mockUiState) {
            AppItemRow(
                app = mockApp,
                onClick = {},
                isFavorite = true
            )
        }
    }
}
