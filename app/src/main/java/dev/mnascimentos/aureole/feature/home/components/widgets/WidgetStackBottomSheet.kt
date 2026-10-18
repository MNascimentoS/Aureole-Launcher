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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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
import dev.mnascimentos.aureole.feature.home.components.common.RenderModalBottomSheetOrInspection
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetStackBottomSheetParams
import dev.mnascimentos.aureole.feature.settings.SettingsActivity
import dev.mnascimentos.aureole.util.IntentUtils

@Suppress("LongMethod") // Bottom sheet composable rendering widget stack menu options
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

    val sheetContent =
        @Suppress("LongMethod") // Bottom sheet composable rendering widget stack menu options
        @Composable
        {
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
                        title = "Redimensionar Grupo",
                        params = AureolePopupMenuItemParams(
                            icon = { m -> AureoleDS.icons.Edit(m) }
                        ),
                        onClick = {
                            params.onEditStack()
                            onDismiss()
                        }
                    )

                    AureolePopupMenuItem(
                        title = "Alternar Indicador de Posição",
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
                        title = "Remover",
                        params = AureolePopupMenuItemParams(
                            icon = { m -> AureoleDS.icons.Delete(m) },
                            isDestructive = true
                        ),
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

@Suppress("LongMethod") // Bottom sheet composable rendering widget stack menu options
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
