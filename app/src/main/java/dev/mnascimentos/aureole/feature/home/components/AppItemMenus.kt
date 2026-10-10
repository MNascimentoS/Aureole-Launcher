package dev.mnascimentos.aureole.feature.home.components

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleTheme
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

data class AppItemBottomSheetParams(
    val app: AppInfo,
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
    val sheetContent = @Composable {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = AureoleDS.dimens.xLarge)
        ) {
            AppActionSheetHeader(app = params.app)

            Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

            AppActionSheetTopHorizontalBar(
                app = params.app,
                onDismiss = onDismiss
            )

            Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

            AppActionSheetAdditionalItems(
                params = params,
                onDismiss = onDismiss
            )
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@OptIn(ExperimentalMaterial3Api::class)
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
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            dragHandle = null,
            shape = RoundedCornerShape(
                topStart = AureoleTheme.dimens.cornerRadius,
                topEnd = AureoleTheme.dimens.cornerRadius
            ),
            containerColor = AureoleTheme.colors.surface,
            contentColor = AureoleTheme.colors.onSurfaceMedium
        ) {
            sheetContent()
        }
    }
}

@Composable
private fun AppActionSheetHeader(app: AppInfo) {
    val context = LocalContext.current
    val iconBitmap = remember(app.packageName) { app.getIconBitmap() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium)
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = AureoleDS.dimens.medium, bottom = AureoleDS.dimens.xSmall),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                bitmap = iconBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(AureoleDS.dimens.small))

            AureoleText(
                text = app.label,
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AureoleDS.colors.onSurfaceHigh,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

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
private fun AppActionSheetTopHorizontalBar(
    app: AppInfo,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = AureoleDS.dimens.medium),
        horizontalArrangement = Arrangement.spacedBy(AureoleDS.dimens.small)
    ) {
        TopBarSquareButton(
            title = "Info",
            icon = {
                AureoleDS.icons.Info(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            onClick = {
                onDismiss()
                IntentUtils.openAppInfo(context, app.packageName)
            }
        )

        TopBarSquareButton(
            title = "Desinstalar",
            icon = {
                AureoleDS.icons.Delete(
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            onClick = {
                onDismiss()
                IntentUtils.uninstallApp(context, app.packageName)
            }
        )
    }
}

@Composable
private fun TopBarSquareButton(
    title: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(76.dp)
            .clip(RoundedCornerShape(AureoleTheme.dimens.cornerRadius))
            .background(AureoleTheme.colors.surfaceVariant)
            .clickable { onClick() }
            .padding(vertical = AureoleDS.dimens.small, horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        icon()

        Spacer(modifier = Modifier.height(4.dp))

        AureoleText(
            text = title,
            style = AureoleDS.typography.labelSmall,
            color = AureoleDS.colors.onSurfaceHigh,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
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

            ContainerFolderMenuItems(params = params)
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun ContainerFolderMenuItems(params: ContainerFolderBottomSheetParams) {
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

            WidgetStackMenuItems(params = params, widgetAppPackage = widgetAppPackage, context = context)
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun WidgetStackMenuItems(
    params: WidgetStackBottomSheetParams,
    widgetAppPackage: String?,
    context: Context
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium, vertical = 6.dp)
    ) {
        if (widgetAppPackage != null) {
            AureolePopupMenuItem(
                title = "App Info",
                customIcon = {
                    AureoleDS.icons.Info(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                },
                onClick = { IntentUtils.openAppInfo(context, widgetAppPackage) }
            )
        }

        AureolePopupMenuItem(
            title = "Editar Stack",
            customIcon = {
                AureoleDS.icons.Edit(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            onClick = params.onEditStack
        )

        AureolePopupMenuItem(
            title = "Alternar Indicadores",
            customIcon = {
                AureoleDS.icons.Settings(
                    tint = AureoleDS.colors.onSurfaceMedium,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            onClick = { params.onToggleDots(params.stackId) }
        )

        AureolePopupMenuItem(
            title = "Remover Stack",
            customIcon = {
                AureoleDS.icons.Delete(
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            },
            isDestructive = true,
            onClick = params.onRemoveStack
        )
    }
}

@Composable
private fun WidgetStackBottomSheetHeader(context: Context) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium)
    ) {
        AureoleText(
            text = "Stack Settings",
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
private fun AppActionSheetAdditionalItems(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium, vertical = 4.dp)
    ) {
        if (params.onRemoveFromContainer != null) {
            AureolePopupMenuItem(
                title = "Remover do Container",
                customIcon = {
                    AureoleDS.icons.Delete(
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                },
                isDestructive = true,
                onClick = {
                    onDismiss()
                    params.onRemoveFromContainer.invoke()
                }
            )
        }

        if (params.onOpenContainerSettings != null) {
            AureolePopupMenuItem(
                title = "Configurações do Container",
                customIcon = {
                    AureoleDS.icons.Settings(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                },
                onClick = {
                    onDismiss()
                    params.onOpenContainerSettings.invoke()
                }
            )
        }

        if (params.onRemoveFromFolder != null) {
            AureolePopupMenuItem(
                title = "Remover da Pasta",
                customIcon = {
                    AureoleDS.icons.Delete(
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                },
                isDestructive = true,
                onClick = {
                    onDismiss()
                    params.onRemoveFromFolder.invoke()
                }
            )
        }

        if (params.onOpenFolderSettings != null) {
            AureolePopupMenuItem(
                title = "Configurações da Pasta",
                customIcon = {
                    AureoleDS.icons.Settings(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                },
                onClick = {
                    onDismiss()
                    params.onOpenFolderSettings.invoke()
                }
            )
        }
    }
}
