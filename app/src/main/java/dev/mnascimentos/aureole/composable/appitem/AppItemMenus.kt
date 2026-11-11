package dev.mnascimentos.aureole.composable.appitem

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.menu.AureoleMenuQuickActions
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuQuickAction
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.util.AppShortcutUtils
import dev.mnascimentos.aureole.util.AureoleShortcutItem
import dev.mnascimentos.aureole.util.IntentUtils
import dev.mnascimentos.aureole.util.toImageBitmap

/**
 * Menu of an app (long press): the app shortcuts and container or folder rows on top, and a bar of round
 * actions at the bottom with Favorite at the thumb side and Uninstall, which needs a hold, at the far end.
 */
@Composable
fun AppItemBottomSheet(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val iconBitmap = remember(params.app.packageName) { params.app.getIconBitmap() }
    val shortcuts = remember(params.app.packageName) {
        AppShortcutUtils.getAppShortcuts(context, params.app.packageName)
    }
    val quickActions = appQuickActions(params = params, context = context, onDismiss = onDismiss)

    AureoleMenuSheet(
        title = params.app.label,
        onDismiss = onDismiss,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m -> Image(bitmap = iconBitmap, contentDescription = params.app.label, modifier = m) },
            onSettingsClick = params.actions.onOpenSettings,
            bottomBar = { AureoleMenuQuickActions(actions = quickActions) }
        )
    ) {
        AppMenuRows(params = params, shortcuts = shortcuts, onDismiss = onDismiss, context = context)
    }
}

@Composable
private fun appQuickActions(
    params: AppItemBottomSheetParams,
    context: Context,
    onDismiss: () -> Unit
): List<MenuQuickAction> {
    val favoriteLabel = stringResource(R.string.home_app_menu_favorite)
    val infoLabel = stringResource(R.string.home_app_menu_info)
    val uninstallLabel = stringResource(R.string.home_app_menu_hold_uninstall)
    return buildList {
        params.actions.onToggleFavorite?.let { toggle ->
            add(
                MenuQuickAction(
                    label = favoriteLabel,
                    icon = { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) },
                    onTrigger = {
                        toggle(params.app.packageName)
                        onDismiss()
                    },
                    isActive = params.isFavorite
                )
            )
        }
        add(
            MenuQuickAction(
                label = infoLabel,
                icon = { m, c -> AureoleDS.icons.Info(modifier = m, tint = c) },
                onTrigger = {
                    params.actions.onAppInfoClick?.invoke(params.app)
                        ?: IntentUtils.openAppInfo(context, params.app.packageName)
                    onDismiss()
                }
            )
        )
        add(
            MenuQuickAction(
                label = uninstallLabel,
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                onTrigger = {
                    params.actions.onUninstallClick?.invoke(params.app)
                        ?: IntentUtils.uninstallApp(context, params.app.packageName)
                    onDismiss()
                },
                isDestructive = true,
                requiresHold = true
            )
        )
    }
}

@Composable
private fun AppMenuRows(
    params: AppItemBottomSheetParams,
    shortcuts: List<AureoleShortcutItem>,
    onDismiss: () -> Unit,
    context: Context
) {
    val hasRows = shortcuts.isNotEmpty() || params.onOpenContainerSettings != null ||
        params.onOpenFolderSettings != null || params.actions.onEditFavoritesClick != null

    if (params.onRemoveFromFolder != null) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_app_menu_remove_from_folder),
                onClick = {
                    params.onRemoveFromFolder.invoke()
                    onDismiss()
                },
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true
            )
        )
    }
    if (hasRows) AureoleMenuSectionLabel(text = stringResource(R.string.home_app_menu_section_shortcuts))

    shortcuts.forEach { shortcut -> ShortcutRow(shortcut = shortcut, onDismiss = onDismiss, context = context) }
    AppMenuNavRow(
        title = stringResource(R.string.home_folder_container_settings),
        onClick = params.onOpenContainerSettings,
        onDismiss = onDismiss
    )
    AppMenuNavRow(
        title = stringResource(R.string.home_folder_settings),
        onClick = params.onOpenFolderSettings,
        onDismiss = onDismiss
    )
    AppMenuNavRow(
        title = stringResource(R.string.home_app_menu_quick_panel_settings),
        onClick = params.actions.onEditFavoritesClick,
        onDismiss = onDismiss
    )
}

@Composable
private fun AppMenuNavRow(title: String, onClick: (() -> Unit)?, onDismiss: () -> Unit) {
    if (onClick == null) return
    AureoleMenuRow(
        MenuRowParams(
            title = title,
            onClick = {
                onClick()
                onDismiss()
            },
            icon = { m, c -> AureoleDS.icons.Settings(modifier = m, tint = c) },
            showChevron = true
        )
    )
}

@Composable
private fun ShortcutRow(shortcut: AureoleShortcutItem, onDismiss: () -> Unit, context: Context) {
    val iconBitmap = remember(shortcut.id) { shortcut.icon?.toImageBitmap() }
    AureoleMenuRow(
        MenuRowParams(
            title = shortcut.label,
            onClick = {
                AppShortcutUtils.launchShortcut(context, shortcut)
                onDismiss()
            },
            icon = { m, c ->
                if (iconBitmap != null) {
                    Image(
                        bitmap = iconBitmap,
                        contentDescription = shortcut.label,
                        modifier = m
                            .size(AureoleDS.dimens.iconLargePlus)
                            .clip(CircleShape)
                    )
                } else {
                    AureoleDS.icons.Smile(modifier = m, tint = c)
                }
            }
        )
    )
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
        isFavorite = true,
        actions = AppItemRowActions(onToggleFavorite = {}),
        onOpenContainerSettings = {}
    )
    AureoleLauncherTheme {
        AppItemBottomSheet(
            params = params,
            onDismiss = {}
        )
    }
}
