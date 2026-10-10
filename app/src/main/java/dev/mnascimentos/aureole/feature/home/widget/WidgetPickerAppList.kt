package dev.mnascimentos.aureole.feature.home.widget

import android.appwidget.AppWidgetProviderInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.InflateException
import android.widget.ImageView
import android.widget.RemoteViews
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import coil.compose.AsyncImage
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowDown
import dev.mnascimentos.aureole.core.designsystem.icons.ArrowUp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.widget.model.WidgetVariant

private const val TAG = "WidgetPickerAppList"

@Composable
fun WidgetGroupList(
    state: WidgetSelectorState,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(
            items = state.widgetGroups,
            key = { it.appId }
        ) { group ->
            val isExpanded = state.expandedAppIds.contains(group.appId)

            AppGroupHeader(
                group = group,
                isExpanded = isExpanded,
                onClick = { onIntent(WidgetSelectorIntent.ToggleAppGroup(group.appId)) }
            )

            if (isExpanded) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(group.availableWidgets, key = { it.widgetId }) { widget ->
                        WidgetPreviewCard(
                            widget = widget,
                            onClick = { onWidgetSelected(widget.providerInfo) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppGroupHeader(
    group: WidgetAppGroup,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .animateContentSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (group.appIcon != null) {
            AsyncImage(
                model = group.appIcon,
                contentDescription = group.appName,
                modifier = Modifier.size(36.dp)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp))
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = group.appName,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.weight(1f)
        )

        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                    RoundedCornerShape(12.dp)
                )
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text(
                text = "${group.availableWidgets.size} widget${if (group.availableWidgets.size > 1) "s" else ""}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isExpanded) {
            AureoleDS.icons.ArrowUp(
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            AureoleDS.icons.ArrowDown(
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun WidgetPreviewCard(
    widget: WidgetVariant,
    onClick: () -> Unit
) {
    val context = LocalContext.current

    val appIcon = remember(widget.providerInfo) {
        try {
            widget.providerInfo.loadIcon(context, 0)
                ?: context.packageManager.getApplicationIcon(widget.providerInfo.provider.packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e(TAG, "App icon not found for ${widget.providerInfo.provider.packageName}", e)
            null
        }
    }

    Column(
        modifier = Modifier
            .width(160.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.2f)
                .clip(RoundedCornerShape(18.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            WidgetPreviewContent(widget = widget, appIcon = appIcon)

            WidgetSpanBadge(spanX = widget.minSpanX, spanY = widget.minSpanY)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = widget.title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun WidgetPreviewContent(
    widget: WidgetVariant,
    appIcon: Drawable?
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
    } else if (widget.previewLayoutRes != 0 && !previewFailed) {
        AndroidView(
            factory = { ctx ->
                try {
                    val rv = RemoteViews(
                        widget.providerInfo.provider.packageName,
                        widget.previewLayoutRes
                    )
                    rv.apply(ctx, null)
                } catch (e: InflateException) {
                    Log.e(TAG, "Failed to inflate previewLayout for ${widget.title}", e)
                    previewFailed = true
                    ImageView(ctx).apply { if (appIcon != null) setImageDrawable(appIcon) }
                } catch (e: IllegalArgumentException) {
                    Log.e(TAG, "Invalid previewLayout for ${widget.title}", e)
                    previewFailed = true
                    ImageView(ctx).apply { if (appIcon != null) setImageDrawable(appIcon) }
                }
            },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxSize()
        )
    } else if (appIcon != null) {
        AsyncImage(
            model = appIcon,
            contentDescription = widget.title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
        )
    } else {
        Text(
            text = widget.title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Composable
private fun WidgetSpanBadge(spanX: Int, spanY: Int) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f),
                    RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "${spanX}x$spanY",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}
