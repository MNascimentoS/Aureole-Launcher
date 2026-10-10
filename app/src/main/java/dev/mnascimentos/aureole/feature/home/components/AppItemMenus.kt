package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.AppShortcutUtils
import dev.mnascimentos.aureole.util.AureoleShortcutItem
import dev.mnascimentos.aureole.util.IntentUtils
import dev.mnascimentos.aureole.util.toImageBitmap

data class AppItemBottomSheetParams(
    val app: AppInfo,
    val isFavorite: Boolean = false,
    val actions: AppItemRowActions = AppItemRowActions(),
    val onRemoveFromContainer: (() -> Unit)? = null,
    val onOpenContainerSettings: (() -> Unit)? = null,
    val onRemoveFromFolder: (() -> Unit)? = null,
    val onOpenFolderSettings: (() -> Unit)? = null,
)

data class ContainerFolderBottomSheetParams(
    val onEditShortcuts: () -> Unit,
    val onRename: () -> Unit,
    val onIcon: () -> Unit,
    val onRemoveFolder: () -> Unit,
    val onOpenContainerSettings: () -> Unit,
)

data class WidgetStackBottomSheetParams(
    val widgetId: Int?,
    val stackId: String,
    val onEditStack: () -> Unit,
    val onToggleDots: (String) -> Unit,
    val onRemoveStack: () -> Unit,
)

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
private fun RenderModalBottomSheetOrInspection(
    sheetContent: @Composable () -> Unit,
    onDismiss: () -> Unit
) {
    if (LocalInspectionMode.current) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            Surface(
                shape = RoundedCornerShape(
                    topStart = AureoleTheme.dimens.cornerRadius,
                    topEnd = AureoleTheme.dimens.cornerRadius
                ),
                color = AureoleTheme.colors.surface,
                contentColor = AureoleTheme.colors.onSurfaceMedium,
                modifier = Modifier.fillMaxWidth()
            ) {
                sheetContent()
            }
        }
    } else {
        @OptIn(ExperimentalMaterial3Api::class)
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        @OptIn(ExperimentalMaterial3Api::class)
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            shape = RoundedCornerShape(
                topStart = AureoleTheme.dimens.cornerRadius,
                topEnd = AureoleTheme.dimens.cornerRadius
            ),
            containerColor = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceMedium,
            dragHandle = null
        ) {
            sheetContent()
        }
    }
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
internal fun ContainerFolderBottomSheet(
    params: ContainerFolderBottomSheetParams,
    onDismiss: () -> Unit
) {
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xLarge)
        ) {
            ContainerFolderBottomSheetHeader()

            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = 6.dp)
            ) {
                AureolePopupMenuItem(
                    title = "Editar atalhos",
                    customIcon = {
                        AureoleDS.icons.Star(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                        )
                    },
                    onClick = params.onEditShortcuts
                )

                AureolePopupMenuItem(
                    title = "Renomear",
                    customIcon = {
                        AureoleDS.icons.Edit(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                        )
                    },
                    onClick = params.onRename
                )

                AureolePopupMenuItem(
                    title = "Ícone",
                    customIcon = {
                        AureoleDS.icons.Smile(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                        )
                    },
                    onClick = params.onIcon
                )

                AureolePopupMenuItem(
                    title = "Remover pasta",
                    customIcon = {
                        AureoleDS.icons.Delete(
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                        )
                    },
                    isDestructive = true,
                    onClick = params.onRemoveFolder
                )

                AureolePopupMenuItem(
                    title = "Configurações do Container",
                    customIcon = {
                        AureoleDS.icons.Settings(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                        )
                    },
                    onClick = params.onOpenContainerSettings
                )
            }
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun ContainerFolderBottomSheetHeader() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium)
    ) {
        AureoleText(
            text = "Folder settings",
            style = AureoleDS.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = AureoleDS.colors.onSurfaceHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = AureoleDS.dimens.medium, bottom = AureoleDS.dimens.small)
        )

        IconButton(
            onClick = {
                context.startActivity(Intent(context, SettingsActivity::class.java))
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 6.dp)
                .size(26.dp)
        ) {
            AureoleDS.icons.Logo(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
internal fun WidgetStackBottomSheet(
    params: WidgetStackBottomSheetParams,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val widgetAppPackage = remember(params.widgetId) {
        if (params.widgetId != null && params.widgetId != -1) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager.getAppWidgetInfo(params.widgetId)?.provider?.packageName
            } catch (_: Throwable) {
                null
            }
        } else {
            null
        }
    }

    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xLarge)
        ) {
            WidgetStackBottomSheetHeader(context)

            Spacer(modifier = Modifier.height(AureoleDS.dimens.xxSmall))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AureoleDS.dimens.medium, vertical = 6.dp)
            ) {
                if (widgetAppPackage != null) {
                    AureolePopupMenuItem(
                        title = "Configurações do App",
                        icon = { m -> AureoleDS.icons.Info(m) },
                        onClick = {
                            IntentUtils.openAppInfo(context, widgetAppPackage)
                            onDismiss()
                        }
                    )
                }

                AureolePopupMenuItem(
                    title = "Redimensionar Grupo",
                    icon = { m -> AureoleDS.icons.Edit(m) },
                    onClick = {
                        params.onEditStack()
                        onDismiss()
                    }
                )

                AureolePopupMenuItem(
                    title = "Alternar Indicador de Posição",
                    customIcon = {
                        AureoleDS.icons.Dots(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                        )
                    },
                    onClick = {
                        params.onToggleDots(params.stackId)
                        onDismiss()
                    }
                )

                AureolePopupMenuItem(
                    title = "Remover",
                    icon = { m -> AureoleDS.icons.Delete(m) },
                    isDestructive = true,
                    onClick = {
                        params.onRemoveStack()
                        onDismiss()
                    }
                )
            }
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun WidgetStackBottomSheetHeader(context: Context) {
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
                AureoleDS.icons.Layout(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            AureoleText(
                text = "Grupo de Widgets",
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
                                RoundedCornerShape(AureoleDS.dimens.borderMax)
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
