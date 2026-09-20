package dev.mnascimentos.aureole.feature.home.components

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
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
import dev.mnascimentos.aureole.feature.home.MainUiState

private const val FAVORITE_INACTIVE_ALPHA = 0.4f

data class AppItemRowActions(
    val onToggleFavorite: ((String) -> Unit)? = null,
    val onAppInfoClick: ((AppInfo) -> Unit)? = null
)

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
    if (uiState.isLeftHandedMode) {
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(42.dp)
        )
    } else {
        Image(
            bitmap = iconBitmap,
            contentDescription = app.label,
            modifier = Modifier.size(42.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = app.label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
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
            val favTint = if (isFavorite) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = FAVORITE_INACTIVE_ALPHA)
            }
            DropdownMenuItem(
                text = {
                    Text(if (isFavorite) "Remove from Favorites" else "Add to Favorites")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = favTint
                    )
                },
                onClick = {
                    onDismiss()
                    actions.onToggleFavorite.invoke(app.packageName)
                }
            )
        }
        if (actions.onAppInfoClick != null) {
            DropdownMenuItem(
                text = { Text("App Info") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null
                    )
                },
                onClick = {
                    onDismiss()
                    actions.onAppInfoClick.invoke(app)
                }
            )
        }
    }
}

@AureolePreview
@Composable
fun AppItemRowPreview() {
    val mockApp = AppInfo(
        label = "Browser",
        packageName = "com.example.browser",
        componentName = ComponentName("com.example.browser", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            AppItemRow(
                app = mockApp,
                onClick = {},
                isFavorite = true
            )
        }
    }
}

