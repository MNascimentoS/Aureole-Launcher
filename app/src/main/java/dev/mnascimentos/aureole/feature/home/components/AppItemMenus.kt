package dev.mnascimentos.aureole.feature.home.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.AppShortcutUtils
import dev.mnascimentos.aureole.util.AureoleShortcutItem
import dev.mnascimentos.aureole.util.IntentUtils
import dev.mnascimentos.aureole.util.toImageBitmap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun AppItemBottomSheet(
    app: AppInfo,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AureoleTheme.colors.surface,
        contentColor = AureoleTheme.colors.onSurfaceMedium,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

            AppItemBottomSheetHeader(app, iconBitmap, context)

            Spacer(modifier = Modifier.height(4.dp))

            AppActionSheetContent(
                app = app,
                isFavorite = isFavorite,
                actions = actions,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
private fun AppItemBottomSheetHeader(app: AppInfo, iconBitmap: ImageBitmap, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, top = 8.dp, end = 32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AureoleDS.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = app.label,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            AureoleText(
                text = app.label,
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 4.dp, end = 4.dp)
        ) {
            IconButton(
                onClick = {
                    context.startActivity(Intent(context, SettingsActivity::class.java))
                },
                modifier = Modifier.size(26.dp)
            ) {
                AureoleDS.icons.Logo(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
internal fun AppItemPopup(
    app: AppInfo,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    Popup(
        onDismissRequest = onDismiss,
        properties = PopupProperties(focusable = true)
    ) {
        AureolePopupBox(
            contentPadding = PaddingValues(0.dp),
            modifier = Modifier.width(230.dp)
        ) {
            val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

            AureolePopupHeader(
                title = app.label,
                iconBitmap = iconBitmap,
                showAureoleLogo = true
            )

            AppPopupHorizontalActionsRow(
                app = app,
                isFavorite = isFavorite,
                actions = actions,
                onDismiss = onDismiss
            )
        }
    }
}

@Composable
internal fun AppActionSheetContent(
    app: AppInfo,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shortcuts = remember(app.packageName) {
        AppShortcutUtils.getAppShortcuts(context, app.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        AppShortcutsMenu(shortcuts = shortcuts, onDismiss = onDismiss, context = context)

        if (actions.onAppInfoClick != null) {
            AureolePopupMenuItem(
                title = "App info",
                icon = Icons.Default.Info,
                onClick = {
                    actions.onAppInfoClick.invoke(app)
                    onDismiss()
                }
            )
        }

        if (actions.onToggleFavorite != null) {
            AureolePopupMenuItem(
                title = if (isFavorite) "Remove from favorites" else "Add to favorites",
                icon = Icons.Default.Star,
                onClick = {
                    actions.onToggleFavorite.invoke(app.packageName)
                    onDismiss()
                }
            )
        }

        AureolePopupMenuItem(
            title = "Uninstall",
            icon = Icons.Default.Delete,
            isDestructive = true,
            onClick = {
                if (actions.onUninstallClick != null) {
                    actions.onUninstallClick.invoke(app)
                } else {
                    IntentUtils.uninstallApp(context, app.packageName)
                }
                onDismiss()
            }
        )

        if (actions.onEditFavoritesClick != null) {
            AureolePopupMenuItem(
                title = "Quick panel settings",
                icon = Icons.Default.Settings,
                onClick = {
                    actions.onEditFavoritesClick.invoke()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun AppShortcutsMenu(
    shortcuts: List<AureoleShortcutItem>,
    onDismiss: () -> Unit,
    context: Context
) {
    shortcuts.forEach { shortcut ->
        AureolePopupMenuItem(
            title = shortcut.label,
            customIcon = {
                val iconBitmap = remember(shortcut.id) {
                    shortcut.icon?.toImageBitmap()
                }
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = shortcut.label,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .border(
                                1.5.dp,
                                AureoleDS.colors.onSurfaceMedium,
                                RoundedCornerShape(3.dp)
                            )
                            .background(AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.15f))
                    )
                }
            },
            onClick = {
                AppShortcutUtils.launchShortcut(context, shortcut)
                onDismiss()
            }
        )
    }
}

@Composable
internal fun AppPopupHorizontalActionsRow(
    app: AppInfo,
    isFavorite: Boolean,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (actions.onAppInfoClick != null) {
            AppPopupButton(
                icon = Icons.Default.Info,
                label = "App",
                onClick = {
                    actions.onAppInfoClick.invoke(app)
                    onDismiss()
                }
            )
        }

        if (actions.onToggleFavorite != null) {
            AppPopupButton(
                icon = Icons.Default.Delete,
                label = if (isFavorite) "Remove" else "Add",
                isDestructive = isFavorite,
                onClick = {
                    actions.onToggleFavorite.invoke(app.packageName)
                    onDismiss()
                }
            )
        }

        if (actions.onEditFavoritesClick != null) {
            AppPopupButton(
                icon = Icons.Default.Settings,
                label = "Settings",
                onClick = {
                    actions.onEditFavoritesClick.invoke()
                    onDismiss()
                }
            )
        }
    }
}

@Composable
private fun AppPopupButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) MaterialTheme.colorScheme.error else AureoleDS.colors.onSurfaceHigh

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .width(54.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = contentColor,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        AureoleText(
            text = label,
            style = AureoleDS.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = contentColor,
            textAlign = TextAlign.Center
        )
    }
}
