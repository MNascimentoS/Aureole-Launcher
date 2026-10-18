package dev.mnascimentos.aureole.feature.home.components.widgets

import android.graphics.drawable.Drawable
import android.util.Log
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant

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
            .clickable(onClick = onClick)
            .padding(start = 24.dp, end = 48.dp, top = 12.dp, bottom = 12.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (group.appIcon != null) {
            AsyncImage(
                model = group.appIcon,
                contentDescription = group.appName,
                modifier = Modifier.size(42.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(AureoleDS.colors.surfaceVariant, RoundedCornerShape(10.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = group.appName,
                style = AureoleDS.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
            Text(
                text = "${group.availableWidgets.size} Widget${if (group.availableWidgets.size > 1) "s" else ""}",
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
            .width(220.dp)
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.6f), RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(WIDGET_PREVIEW_ASPECT_RATIO)
                .clip(RoundedCornerShape(16.dp))
                .background(AureoleDS.colors.surface.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            WidgetPreviewContent(widget = widget, fallbackAppIcon = fallbackAppIcon)
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                .padding(8.dp)
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
                    Log.e(TAG, "Failed to inflate previewLayout for ${widget.title}", e)
                    previewFailed = true
                    ImageView(ctx).apply { if (fallbackAppIcon != null) setImageDrawable(fallbackAppIcon) }
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Invalid previewLayout for ${widget.title}", e)
                    previewFailed = true
                    ImageView(ctx).apply { if (fallbackAppIcon != null) setImageDrawable(fallbackAppIcon) }
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        )
    } else if (fallbackAppIcon != null) {
        AsyncImage(
            model = fallbackAppIcon,
            contentDescription = widget.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(16.dp)
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
            Spacer(modifier = Modifier.height(8.dp))
            WidgetPreviewCard(
                widget = WidgetVariant(widgetId = "clock_1", title = "Relógio Analógico"),
                fallbackAppIcon = null,
                onClick = {}
            )
        }
    }
}
