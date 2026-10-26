package dev.mnascimentos.aureole.feature.home.components.widgets

import android.graphics.drawable.Drawable
import android.view.InflateException
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupMetrics
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import timber.log.Timber

private const val TAG = "WidgetPickerAppListRow"
private const val WIDGET_PREVIEW_ASPECT_RATIO = 1.2f

@Composable
internal fun AppGroupHeader(
    group: WidgetAppGroup,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 48.dp)
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(
                start = AureoleDS.dimens.large,
                end = AureoleDS.dimens.xxLarge + AureoleDS.dimens.xSmall,
                top = AureoleDS.dimens.small,
                bottom = AureoleDS.dimens.small
            )
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (group.appIcon != null) {
            AsyncImage(
                model = group.appIcon,
                contentDescription = group.appName,
                modifier = Modifier.size(AureoleDS.dimens.xxLarge)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(AureoleDS.dimens.xxLarge)
                    .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(AureoleDS.dimens.radiusXSmall))
            )
        }

        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.appName,
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = pluralStringResource(
                    R.plurals.widget_count,
                    group.availableWidgets.size,
                    group.availableWidgets.size
                ),
                style = AureoleDS.typography.bodySmall,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    }
}

@Composable
internal fun WidgetPreviewCard(
    widget: WidgetVariant,
    fallbackAppIcon: Drawable?,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(FolderPopupMetrics.WidthMax)
            .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
            .background(
                AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f),
                RoundedCornerShape(AureoleDS.dimens.radiusLarge)
            )
            .clickable(
                role = Role.Button,
                onClick = onClick
            )
            .padding(AureoleDS.dimens.medium),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(WIDGET_PREVIEW_ASPECT_RATIO)
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .background(AureoleDS.colors.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            WidgetPreviewContent(widget = widget, fallbackAppIcon = fallbackAppIcon)
        }

        Spacer(modifier = Modifier.height(AureoleDS.dimens.small))

        Text(
            text = widget.title,
            style = AureoleDS.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            color = AureoleDS.colors.onSurfaceHigh,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = "About the widget here",
            style = AureoleDS.typography.bodyMedium,
            color = AureoleDS.colors.onSurfaceMedium,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WidgetPreviewContent(
    widget: WidgetVariant,
    fallbackAppIcon: Drawable?
) {
    var previewFailed by remember(widget.widgetId) { mutableStateOf(false) }

    if (widget.previewImage != null) {
        AsyncImage(
            model = widget.previewImage,
            contentDescription = widget.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(AureoleDS.dimens.xSmall)
                .fillMaxSize()
        )
    } else if (widget.previewLayoutRes != 0 && !previewFailed && widget.providerInfo != null) {
        val info = widget.providerInfo
        AndroidView(
            factory = { ctx ->
                try {
                    val rv = RemoteViews(
                        info.provider.packageName,
                        widget.previewLayoutRes
                    )
                    rv.apply(ctx, null)
                } catch (e: InflateException) {
                    Timber.tag(TAG).e(e, "Failed to inflate previewLayout for ${widget.title}")
                    previewFailed = true
                    ImageView(ctx).apply { if (fallbackAppIcon != null) setImageDrawable(fallbackAppIcon) }
                } catch (e: IllegalArgumentException) {
                    Timber.tag(TAG).e(e, "Invalid previewLayout for ${widget.title}")
                    previewFailed = true
                    ImageView(ctx).apply { if (fallbackAppIcon != null) setImageDrawable(fallbackAppIcon) }
                }
            },
            modifier = Modifier
                .padding(AureoleDS.dimens.xSmall)
                .fillMaxSize()
        )
    } else if (fallbackAppIcon != null) {
        AsyncImage(
            model = fallbackAppIcon,
            contentDescription = widget.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(AureoleDS.dimens.medium)
                .fillMaxSize()
        )
    } else {
        Text(
            text = "Widget",
            style = AureoleDS.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
    }
}

@AureolePreview
@Composable
private fun WidgetPickerAppListRowPreview() {
    val sampleGroup = WidgetAppGroup(
        appId = "com.example.clock",
        appName = "Relógio",
        availableWidgets = listOf(
            WidgetVariant(widgetId = "clock_1", title = "Relógio Analógico"),
            WidgetVariant(widgetId = "clock_2", title = "Relógio Digital")
        )
    )
    AureoleLauncherTheme {
        Column {
            AppGroupHeader(group = sampleGroup, onClick = {})
            Spacer(modifier = Modifier.height(AureoleDS.dimens.xSmall))
            WidgetPreviewCard(
                widget = WidgetVariant(widgetId = "clock_1", title = "Relógio Analógico"),
                fallbackAppIcon = null,
                onClick = {}
            )
        }
    }
}
