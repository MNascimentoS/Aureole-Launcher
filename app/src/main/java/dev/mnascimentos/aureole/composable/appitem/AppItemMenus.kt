package dev.mnascimentos.aureole.composable.appitem

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItemParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Folder
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.components.common.RenderModalBottomSheetOrInspection
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.AppShortcutUtils
import dev.mnascimentos.aureole.util.AureoleShortcutItem
import dev.mnascimentos.aureole.util.IntentUtils
import dev.mnascimentos.aureole.util.toImageBitmap

@Composable
fun AppItemBottomSheet(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
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

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss, modifier = modifier)
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
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmallCompact))
                    .background(AureoleDS.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = app.label,
                    modifier = Modifier.size(AureoleDS.dimens.iconLargePlus)
                )
            }

            Spacer(modifier = Modifier.width(AureoleDS.dimens.smallCompact))

            AureoleText(
                text = app.label,
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.weight(1f),
                format = AureoleTextFormatParams(
                    style = AureoleDS.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
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
                modifier = Modifier.size(AureoleDS.dimens.iconXLargeSmall)
            ) {
                AureoleDS.icons.Logo(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconMediumSmall)
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
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xSmall)
    ) {
        AppShortcutsMenu(shortcuts = shortcuts, onDismiss = onDismiss, context = context)

        AureolePopupMenuItem(
            title = stringResource(R.string.home_app_menu_info),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Info(m) }
            ),
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
            title = stringResource(R.string.home_app_menu_uninstall),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Delete(m) },
                isDestructive = false
            ),
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
    if (params.onOpenContainerSettings != null) {
        AureolePopupMenuItem(
            title = stringResource(R.string.home_folder_container_settings),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Settings(m) }
            ),
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
            title = stringResource(R.string.home_app_menu_remove_from_folder),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Delete(m) },
                isDestructive = false
            ),
            onClick = {
                params.onRemoveFromFolder.invoke()
                onDismiss()
            }
        )
    }

    if (params.onOpenFolderSettings != null) {
        AureolePopupMenuItem(
            title = stringResource(R.string.home_folder_settings),
            params = AureolePopupMenuItemParams(
                customIcon = {
                    AureoleDS.icons.Folder(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                }
            ),
            onClick = {
                params.onOpenFolderSettings.invoke()
                onDismiss()
            }
        )
    }

    ToggleFavoriteMenuItem(params = params, onDismiss = onDismiss)

    if (params.actions.onEditFavoritesClick != null) {
        AureolePopupMenuItem(
            title = stringResource(R.string.home_app_menu_quick_panel_settings),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Settings(m) }
            ),
            onClick = {
                params.actions.onEditFavoritesClick.invoke()
                onDismiss()
            }
        )
    }
}

@Composable
private fun ToggleFavoriteMenuItem(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit
) {
    if (params.actions.onToggleFavorite != null) {
        AureolePopupMenuItem(
            title = if (params.isFavorite) {
                stringResource(R.string.home_app_menu_remove_from_favorites)
            } else {
                stringResource(R.string.home_app_menu_add_to_favorites)
            },
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Star(m) }
            ),
            onClick = {
                params.actions.onToggleFavorite.invoke(params.app.packageName)
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
            params = AureolePopupMenuItemParams(
                customIcon = {
                    if (iconBitmap != null) {
                        Image(
                            bitmap = iconBitmap,
                            contentDescription = shortcut.label,
                            modifier = Modifier
                                .size(AureoleDS.dimens.iconLargePlus)
                                .clip(CircleShape)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(AureoleDS.dimens.iconLargePlus)
                                .clip(CircleShape)
                                .background(AureoleDS.colors.surfaceVariant),
                            contentAlignment = Alignment.Center
                        ) {
                            AureoleDS.icons.Smile(
                                tint = AureoleDS.colors.onSurfaceMedium,
                                modifier = Modifier.size(AureoleDS.dimens.iconSmallCompact)
                            )
                        }
                    }
                }
            ),
            onClick = {
                AppShortcutUtils.launchShortcut(context, shortcut)
                onDismiss()
            }
        )
    }
}

@AureolePreview
@Composable
private fun AppItemBottomSheetPreview() {
    val sampleApp = AppInfo(
        label = "Browser",
        packageName = "com.example.browser",
        componentName = ComponentName("com.example.browser", "MainActivity"),
        icon = Color.BLUE.toDrawable()
    )
    val params = AppItemBottomSheetParams(
        app = sampleApp,
        isFavorite = false,
        actions = AppItemRowActions()
    )
    AureoleLauncherTheme {
        AppItemBottomSheet(
            params = params,
            onDismiss = {}
        )
    }
}
