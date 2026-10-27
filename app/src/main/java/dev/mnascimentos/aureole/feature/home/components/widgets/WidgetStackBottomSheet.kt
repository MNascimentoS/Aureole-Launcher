package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.sheets.RenderModalBottomSheetOrInspection
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItem
import dev.mnascimentos.aureole.core.designsystem.components.AureolePopupMenuItemParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Delete
import dev.mnascimentos.aureole.core.designsystem.icons.Dots
import dev.mnascimentos.aureole.core.designsystem.icons.Edit
import dev.mnascimentos.aureole.core.designsystem.icons.Info
import dev.mnascimentos.aureole.core.designsystem.icons.Layout
import dev.mnascimentos.aureole.core.designsystem.icons.Logo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackBottomSheetParams
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

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
            WidgetStackMenuItems(
                params = params,
                widgetAppPackage = widgetAppPackage,
                context = context,
                onDismiss = onDismiss
            )
        }
    }

    RenderModalBottomSheetOrInspection(sheetContent, onDismiss)
}

@Composable
private fun WidgetStackMenuItems(
    params: WidgetStackBottomSheetParams,
    widgetAppPackage: String?,
    context: Context,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.xxSmall)
    ) {
        if (widgetAppPackage != null) {
            AureolePopupMenuItem(
                title = stringResource(R.string.home_widget_app_settings),
                params = AureolePopupMenuItemParams(
                    icon = { m -> AureoleDS.icons.Info(m) }
                ),
                onClick = {
                    IntentUtils.openAppInfo(context, widgetAppPackage)
                    onDismiss()
                }
            )
        }

        AureolePopupMenuItem(
            title = stringResource(R.string.home_widget_resize_group),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Edit(m) }
            ),
            onClick = {
                params.onEditStack()
                onDismiss()
            }
        )

        AureolePopupMenuItem(
            title = stringResource(R.string.home_widget_toggle_indicator),
            params = AureolePopupMenuItemParams(
                customIcon = {
                    AureoleDS.icons.Dots(
                        tint = AureoleDS.colors.onSurfaceMedium,
                        modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                    )
                }
            ),
            onClick = {
                params.onToggleDots(params.stackId)
                onDismiss()
            }
        )

        AureolePopupMenuItem(
            title = stringResource(R.string.home_widget_remove),
            params = AureolePopupMenuItemParams(
                icon = { m -> AureoleDS.icons.Delete(m) },
                isDestructive = false
            ),
            onClick = {
                params.onRemoveStack()
                onDismiss()
            }
        )
    }
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
                    .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                    .background(AureoleDS.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Layout(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconMedium)
                )
            }

            Spacer(modifier = Modifier.width(AureoleDS.dimens.smallCompact))

            AureoleText(
                text = stringResource(R.string.home_widget_group),
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
                modifier = Modifier.size(AureoleDS.dimens.iconXLarge)
            ) {
                AureoleDS.icons.Logo(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconSmall)
                )
            }
        }
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
