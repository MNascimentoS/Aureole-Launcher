package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.common.RenderModalBottomSheetOrInspection
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.AppShortcutUtils
import dev.mnascimentos.aureole.util.AureoleShortcutItem
import dev.mnascimentos.aureole.util.IntentUtils
import dev.mnascimentos.aureole.util.toImageBitmap

@Composable
internal fun AppItemBottomSheet(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xLarge)
        ) {
            val iconBitmap = remember(params.app.packageName) { params.app.getIconBitmap() }

            AppItemBottomSheetHeader(params.app, iconBitmap, context)

            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

            AppActionSheetContent(
                params = params,
                onDismiss = onDismiss
            )
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun AppItemBottomSheetHeader(app: AppInfo, iconBitmap: ImageBitmap, context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = AureoleDS.dimens.xSmall, top = AureoleDS.dimens.xSmall, end = AureoleDS.dimens.xLarge),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.iconXLarge)
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
                .padding(top = AureoleDS.dimens.xxSmall, end = AureoleDS.dimens.xxSmall)
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
internal fun AppActionSheetContent(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val shortcuts = remember(params.app.packageName) {
        AppShortcutUtils.getAppShortcuts(context, params.app.packageName)
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        AppShortcutsMenu(shortcuts = shortcuts, onDismiss = onDismiss, context = context)

        AureolePopupMenuItem(
            title = "App Info",
            icon = { m -> AureoleDS.icons.Info(m) },
            onClick = {
                if (params.actions.onAppInfoClick != null) {
                    params.actions.onAppInfoClick.invoke(params.app)
                } else {
                    IntentUtils.openAppInfo(context, params.app.packageName)
                }
                onDismiss()
            }
        )

        AureolePopupMenuItem(
            title = "Desinstalar",
            icon = { m -> AureoleDS.icons.Delete(m) },
            isDestructive = true,
            onClick = {
                if (params.actions.onUninstallClick != null) {
                    params.actions.onUninstallClick.invoke(params.app)
                } else {
                    IntentUtils.uninstallApp(context, params.app.packageName)
                }
                onDismiss()
            }
        )

        AppActionSheetAdditionalItems(params, onDismiss)
    }
}

@Composable
private fun AppActionSheetAdditionalItems(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    AppActionSheetContainerItems(params, onDismiss)
    AppActionSheetFolderItems(params, onDismiss)
}

@Composable
private fun AppActionSheetContainerItems(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    if (params.onRemoveFromContainer != null) {
        AureolePopupMenuItem(
            title = "Remover do Container",
            icon = { m -> AureoleDS.icons.Delete(m) },
            isDestructive = true,
            onClick = {
                params.onRemoveFromContainer.invoke()
                onDismiss()
            }
        )
    }

    if (params.onOpenContainerSettings != null) {
        AureolePopupMenuItem(
            title = "Configurações do Container",
            icon = { m -> AureoleDS.icons.Settings(m) },
            onClick = {
                params.onOpenContainerSettings.invoke()
                onDismiss()
            }
        )
    }
}

@Composable
private fun AppActionSheetFolderItems(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    if (params.onRemoveFromFolder != null) {
        AureolePopupMenuItem(
            title = "Remover da Pasta",
            icon = { m -> AureoleDS.icons.Delete(m) },
            isDestructive = true,
            onClick = {
                params.onRemoveFromFolder.invoke()
                onDismiss()
            }
        )
    }

    if (params.onOpenFolderSettings != null) {
        AureolePopupMenuItem(
            title = "Configurações da Pasta",
            customIcon = {
                AureoleDS.icons.Folder(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(20.dp)
                )
            },
            onClick = {
                params.onOpenFolderSettings.invoke()
                onDismiss()
            }
        )
    }

    if (params.actions.onToggleFavorite != null) {
        AureolePopupMenuItem(
            title = if (params.isFavorite) "Remove from favorites" else "Add to favorites",
            icon = { m -> AureoleDS.icons.Star(m) },
            onClick = {
                params.actions.onToggleFavorite.invoke(params.app.packageName)
                onDismiss()
            }
        )
    }

    if (params.actions.onEditFavoritesClick != null) {
        AureolePopupMenuItem(
            title = "Quick panel settings",
            icon = { m -> AureoleDS.icons.Settings(m) },
            onClick = {
                params.actions.onEditFavoritesClick.invoke()
                onDismiss()
            }
        )
    }
}

@Composable
private fun AppShortcutsMenu(
    shortcuts: List<AureoleShortcutItem>,
    onDismiss: () -> Unit,
    context: Context
) {
    shortcuts.forEach { shortcut ->
        val iconBitmap = remember(shortcut.id) {
            shortcut.icon?.toImageBitmap()
        }
        AureolePopupMenuItem(
            title = shortcut.label,
            customIcon = {
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = shortcut.label,
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(AureoleDS.colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        AureoleDS.icons.Smile(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            },
            onClick = {
                AppShortcutUtils.launchShortcut(context, shortcut)
                onDismiss()
            }
        )
    }
}
