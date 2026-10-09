package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetManager
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupBox
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.*
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
    isFavorite: Boolean = false,
    actions: AppItemRowActions = AppItemRowActions(),
    onRemoveFromContainer: (() -> Unit)? = null,
    onOpenContainerSettings: (() -> Unit)? = null,
    onRemoveFromFolder: (() -> Unit)? = null,
    onOpenFolderSettings: (() -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetContent = @Composable {
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
                onRemoveFromContainer = onRemoveFromContainer,
                onOpenContainerSettings = onOpenContainerSettings,
                onRemoveFromFolder = onRemoveFromFolder,
                onOpenFolderSettings = onOpenFolderSettings,
                onDismiss = onDismiss
            )
        }
    }

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
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ContainerFolderBottomSheet(
    folder: AppFolder,
    onEditShortcuts: () -> Unit,
    onRename: () -> Unit,
    onIcon: () -> Unit,
    onRemoveFolder: () -> Unit,
    onOpenContainerSettings: () -> Unit,
    onDismiss: () -> Unit
) {
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp)
        ) {
            ContainerFolderBottomSheetHeader()

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            ) {
                AureolePopupMenuItem(
                    title = "Editar atalhos",
                    customIcon = {
                        AureoleDS.icons.Star(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = onEditShortcuts
                )

                AureolePopupMenuItem(
                    title = "Renomear",
                    customIcon = {
                        AureoleDS.icons.Edit(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = onRename
                )

                AureolePopupMenuItem(
                    title = "Ícone",
                    customIcon = {
                        AureoleDS.icons.Smile(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = onIcon
                )

                AureolePopupMenuItem(
                    title = "Remover pasta",
                    customIcon = {
                        AureoleDS.icons.Delete(
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    isDestructive = true,
                    onClick = onRemoveFolder
                )

                AureolePopupMenuItem(
                    title = "Configurações do Container",
                    customIcon = {
                        AureoleDS.icons.Settings(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = onOpenContainerSettings
                )
            }
        }
    }

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
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
private fun ContainerFolderBottomSheetHeader() {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
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
                .padding(top = 16.dp, bottom = 12.dp)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WidgetStackBottomSheet(
    widgetId: Int?,
    stackId: String,
    onEditStack: () -> Unit,
    onToggleDots: (String) -> Unit,
    onRemoveStack: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val widgetAppPackage = remember(widgetId) {
        if (widgetId != null && widgetId != -1) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                appWidgetManager.getAppWidgetInfo(widgetId)?.provider?.packageName
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
                .padding(bottom = 32.dp)
        ) {
            WidgetStackBottomSheetHeader(context)

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
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
                    title = "Editar Pilha",
                    icon = { m -> AureoleDS.icons.Edit(m) },
                    onClick = {
                        onEditStack()
                        onDismiss()
                    }
                )

                AureolePopupMenuItem(
                    title = "Alternar Indicador",
                    customIcon = {
                        AureoleDS.icons.Dots(
                            tint = AureoleDS.colors.onSurfaceMedium,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    onClick = {
                        onToggleDots(stackId)
                        onDismiss()
                    }
                )

                AureolePopupMenuItem(
                    title = "Remover",
                    icon = { m -> AureoleDS.icons.Delete(m) },
                    isDestructive = true,
                    onClick = {
                        onRemoveStack()
                        onDismiss()
                    }
                )
            }
        }
    }

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
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
private fun WidgetStackBottomSheetHeader(context: Context) {
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
                AureoleDS.icons.Layout(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            AureoleText(
                text = "Pilha de Widgets",
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
    isFavorite: Boolean = false,
    actions: AppItemRowActions = AppItemRowActions(),
    onRemoveFromContainer: (() -> Unit)? = null,
    onOpenContainerSettings: (() -> Unit)? = null,
    onRemoveFromFolder: (() -> Unit)? = null,
    onOpenFolderSettings: (() -> Unit)? = null,
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

        AureolePopupMenuItem(
            title = "App Info",
            icon = { m -> AureoleDS.icons.Info(m) },
            onClick = {
                if (actions.onAppInfoClick != null) {
                    actions.onAppInfoClick.invoke(app)
                } else {
                    IntentUtils.openAppInfo(context, app.packageName)
                }
                onDismiss()
            }
        )

        AureolePopupMenuItem(
            title = "Desinstalar",
            icon = { m -> AureoleDS.icons.Delete(m) },
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

        if (onRemoveFromContainer != null) {
            AureolePopupMenuItem(
                title = "Remover do Container",
                icon = { m -> AureoleDS.icons.Delete(m) },
                isDestructive = true,
                onClick = {
                    onRemoveFromContainer.invoke()
                    onDismiss()
                }
            )
        }

        if (onOpenContainerSettings != null) {
            AureolePopupMenuItem(
                title = "Configurações do Container",
                icon = { m -> AureoleDS.icons.Settings(m) },
                onClick = {
                    onOpenContainerSettings.invoke()
                    onDismiss()
                }
            )
        }

        if (onRemoveFromFolder != null) {
            AureolePopupMenuItem(
                title = "Remover da Pasta",
                icon = { m -> AureoleDS.icons.Delete(m) },
                isDestructive = true,
                onClick = {
                    onRemoveFromFolder.invoke()
                    onDismiss()
                }
            )
        }

        if (onOpenFolderSettings != null) {
            AureolePopupMenuItem(
                title = "Configurações da Pasta",
                customIcon = {
                    AureoleDS.icons.Folder(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(20.dp)
                    )
                },
                onClick = {
                    onOpenFolderSettings.invoke()
                    onDismiss()
                }
            )
        }

        if (actions.onToggleFavorite != null) {
            AureolePopupMenuItem(
                title = if (isFavorite) "Remove from favorites" else "Add to favorites",
                icon = { m -> AureoleDS.icons.Star(m) },
                onClick = {
                    actions.onToggleFavorite.invoke(app.packageName)
                    onDismiss()
                }
            )
        }

        if (actions.onEditFavoritesClick != null) {
            AureolePopupMenuItem(
                title = "Quick panel settings",
                icon = { m -> AureoleDS.icons.Settings(m) },
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
                icon = { m, t -> AureoleDS.icons.Info(m, t) },
                label = "App",
                onClick = {
                    actions.onAppInfoClick.invoke(app)
                    onDismiss()
                }
            )
        }

        if (actions.onToggleFavorite != null) {
            AppPopupButton(
                icon = { m, t -> AureoleDS.icons.Delete(m, t) },
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
                icon = { m, t -> AureoleDS.icons.Settings(m, t) },
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
    icon: @Composable (Modifier, Color) -> Unit,
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
        icon(Modifier.size(22.dp), contentColor)
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
