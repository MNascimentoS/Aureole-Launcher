package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.menu.AureoleMenuRow
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSectionLabel
import dev.mnascimentos.aureole.composable.menu.AureoleMenuSheet
import dev.mnascimentos.aureole.composable.menu.AureoleSwitchRow
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.composable.menu.model.MenuSheetParams
import dev.mnascimentos.aureole.composable.menu.model.SwitchRowParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.MultipleView
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackBottomSheetParams
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

/**
 * Menu of a widget group (long press on the group): Remove at the top, away from the thumb, then the group
 * actions and the position indicator switch, on the shared menu base.
 */
@Composable
internal fun WidgetStackBottomSheet(
    params: WidgetStackBottomSheetParams,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val widgetAppPackage = remember(params.widgetId, params.appPackage) {
        params.appPackage ?: resolveWidgetPackage(context, params.widgetId)
    }

    AureoleMenuSheet(
        title = stringResource(R.string.home_widget_group),
        onDismiss = onDismiss,
        modifier = modifier,
        params = MenuSheetParams(
            titleIcon = { m -> AureoleDS.icons.MultipleView(modifier = m, tint = AureoleDS.colors.onSurfaceMedium) },
            onSettingsClick = { context.startActivity(Intent(context, SettingsActivity::class.java)) }
        )
    ) { dismiss ->
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_widget_remove),
                onClick = {
                    params.onRemoveStack()
                    dismiss()
                },
                icon = { m, c -> AureoleDS.icons.Delete(modifier = m, tint = c) },
                isDestructive = true
            )
        )
        AureoleMenuSectionLabel(text = stringResource(R.string.home_widget_menu_section))
        WidgetStackMenuRows(params, widgetAppPackage, context, dismiss)
    }
}

@Composable
private fun WidgetStackMenuRows(
    params: WidgetStackBottomSheetParams,
    widgetAppPackage: String?,
    context: Context,
    dismiss: () -> Unit
) {
    if (widgetAppPackage != null) {
        AureoleMenuRow(
            MenuRowParams(
                title = stringResource(R.string.home_widget_app_settings),
                onClick = {
                    IntentUtils.openAppInfo(context, widgetAppPackage)
                    dismiss()
                },
                icon = { m, c -> AureoleDS.icons.Settings(modifier = m, tint = c) },
                showChevron = true
            )
        )
    }
    AureoleMenuRow(
        MenuRowParams(
            title = stringResource(R.string.home_widget_resize_group),
            onClick = {
                params.onEditStack()
                dismiss()
            },
            icon = { m, c -> AureoleDS.icons.MultipleView(modifier = m, tint = c) },
            showChevron = true
        )
    )
    AureoleSwitchRow(
        SwitchRowParams(
            title = stringResource(R.string.home_widget_toggle_indicator),
            checked = params.showIndicator,
            onCheckedChange = {
                params.onToggleDots(params.stackId)
                dismiss()
            },
            icon = { m, c -> AureoleDS.icons.Dots(modifier = m.size(AureoleDS.dimens.iconMedium), tint = c) }
        )
    )
}

private fun resolveWidgetPackage(context: Context, widgetId: Int?): String? {
    if (widgetId == null || widgetId == -1) return null
    return try {
        AppWidgetManager.getInstance(context).getAppWidgetInfo(widgetId)?.provider?.packageName
    } catch (_: Throwable) {
        null
    }
}

@AureolePreview
@Composable
private fun WidgetStackBottomSheetPreview() {
    AureoleLauncherTheme {
        WidgetStackBottomSheet(
            params = WidgetStackBottomSheetParams(
                stackId = "stack_1",
                widgetId = 1,
                onEditStack = {},
                onToggleDots = {},
                onRemoveStack = {}
            ),
            onDismiss = {}
        )
    }
}
