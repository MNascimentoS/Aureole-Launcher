package dev.mnascimentos.aureole.composable.appitem

import android.content.ComponentName
import android.content.Context
import android.graphics.Color
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
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
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.menu.AureoleMenuListSheet
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Favorite
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.icons.Smile
import dev.mnascimentos.aureole.core.designsystem.icons.VerticalContainer
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.util.AppShortcutUtils
import dev.mnascimentos.aureole.util.AureoleShortcutItem
import dev.mnascimentos.aureole.util.IntentUtils
import dev.mnascimentos.aureole.util.toImageBitmap

/**
 * Menu of an app (long press), a plain vertical list: the app shortcuts, a divider, App info, Favorite and the
 * container or folder settings, then (after another divider, in red) Remove from folder and Uninstall. Uninstall
 * needs a 1 s hold. The thumb side comes from the launcher setting, so the order is the same for both hands.
 */
@Composable
fun AppItemBottomSheet(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shortcuts = params.shortcuts ?: remember(params.app.packageName) {
        AppShortcutUtils.getAppShortcuts(context, params.app.packageName)
    }

    AureoleMenuListSheet(
        title = params.app.label,
        onDismiss = onDismiss,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m ->
                AureoleAppIcon(
                    app = params.app,
                    modifier = m,
                    params = AureoleAppIconParams(contentDescription = params.app.label)
                )
            },
            onSettingsClick = params.actions.onOpenSettings
        )
    ) {
        appMenuEntries(params = params, shortcuts = shortcuts, onDismiss = onDismiss, context = context)
    }
}

@Composable
private fun appMenuEntries(
    params: AppItemBottomSheetParams,
    shortcuts: List<AureoleShortcutItem>,
    onDismiss: () -> Unit,
    context: Context
): List<MenuEntry> = buildList {
    if (shortcuts.isNotEmpty()) {
        add(MenuEntry.Section(text = stringResource(R.string.home_app_menu_section_shortcuts)))
        shortcuts.forEach { shortcut ->
            add(MenuEntry.Content { ShortcutRow(shortcut = shortcut, onDismiss = onDismiss, context = context) })
        }
        add(MenuEntry.Divider)
    }
    addAll(appActionEntries(params = params, onDismiss = onDismiss, context = context))
    addAll(appNavEntries(params = params, onDismiss = onDismiss))
    addAll(appDestructiveEntries(params = params, onDismiss = onDismiss, context = context))
}

@Composable
private fun appActionEntries(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit,
    context: Context
): List<MenuEntry> = buildList {
    add(
        MenuEntry.Row(
            MenuRowParams(
                title = stringResource(R.string.home_app_menu_info),
                onClick = {
                    params.actions.onAppInfoClick?.invoke(params.app)
                        ?: IntentUtils.openAppInfo(context, params.app.packageName)
                    onDismiss()
                },
                icon = { m, c -> AureoleDS.icons.Info(modifier = m, tint = c) }
            )
        )
    )
    params.actions.onToggleFavorite?.let { toggle ->
        val titleRes = if (params.isFavorite) {
            R.string.home_app_menu_favorite_remove
        } else {
            R.string.home_app_menu_favorite_add
        }
        add(
            MenuEntry.Row(
                MenuRowParams(
                    title = stringResource(titleRes),
                    onClick = {
                        toggle(params.app.packageName)
                        onDismiss()
                    },
                    icon = { m, c -> AureoleDS.icons.Favorite(modifier = m, tint = c) }
                )
            )
        )
    }
}

@Composable
private fun appNavEntries(params: AppItemBottomSheetParams, onDismiss: () -> Unit): List<MenuEntry> {
    val containerTitle = stringResource(R.string.home_folder_container_settings)
    val navRows = listOf(
        containerTitle to params.onOpenContainerSettings,
        stringResource(R.string.home_folder_settings) to params.onOpenFolderSettings,
        stringResource(R.string.home_app_menu_quick_panel_settings) to params.actions.onEditFavoritesClick
    )
    return navRows.mapNotNull { (title, onClick) ->
        val isContainer = title == containerTitle
        onClick?.let {
            MenuEntry.Row(
                MenuRowParams(
                    title = title,
                    onClick = {
                        it()
                        onDismiss()
                    },
                    icon = { m, c ->
                        if (isContainer) {
                            AureoleDS.icons.VerticalContainer(modifier = m, tint = c)
                        } else {
                            AureoleDS.icons.Settings(modifier = m, tint = c)
                        }
                    },
                    showChevron = !isContainer
                )
            )
        }
    }
}

@Composable
private fun appDestructiveEntries(
    params: AppItemBottomSheetParams,
    onDismiss: () -> Unit,
    context: Context
): List<MenuEntry> = buildList {
    params.onRemoveFromFolder?.let { remove ->
        add(
            MenuEntry.Row(
                MenuRowParams(
                    title = stringResource(R.string.home_app_menu_remove_from_folder),
                    onClick = {
                        remove()
                        onDismiss()
                    },
                    icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                    isDestructive = true
                )
            )
        )
    }
    add(
        MenuEntry.Row(
            MenuRowParams(
                title = stringResource(R.string.home_app_menu_uninstall),
                subtitle = stringResource(R.string.home_app_menu_hold_to_confirm),
                onClick = {
                    params.actions.onUninstallClick?.invoke(params.app)
                        ?: IntentUtils.uninstallApp(context, params.app.packageName)
                    onDismiss()
                },
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true,
                requiresHold = true
            )
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
                            .clip(RoundedCornerShape(AureoleDS.dimens.radiusAppIcon))
                            .background(AureoleDS.colors.surfaceVariant)
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
