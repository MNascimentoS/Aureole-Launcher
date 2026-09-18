package dev.mnascimentos.aureole.ui.components

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerBottomSheet(
    appWidgetManager: AppWidgetManager,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var providers by remember { mutableStateOf<List<AppWidgetProviderInfo>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            val installed = appWidgetManager.installedProviders
            providers = installed.sortedBy { it.loadLabel(context.packageManager) }
        }
    }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        if (providers.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(providers, key = { it.provider.flattenToString() }) { provider ->
                    WidgetProviderItem(
                        provider = provider,
                        onClick = { onWidgetSelected(provider) }
                    )
                }
            }
        }
    }
}

@Composable
fun WidgetProviderItem(provider: AppWidgetProviderInfo, onClick: () -> Unit) {
    val context = LocalContext.current
    val pm = context.packageManager
    val label = provider.loadLabel(pm)
    val previewDrawable = provider.loadPreviewImage(context, 0) ?: provider.loadIcon(context, 0)
    @Suppress("TooGenericExceptionCaught", "SwallowedException")
    val previewBitmap = remember(previewDrawable) {
        try {
            previewDrawable?.toBitmap()?.asImageBitmap()
        } catch (e: Exception) {
            null
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (previewBitmap != null) {
            Image(
                bitmap = previewBitmap,
                contentDescription = label,
                modifier = Modifier.size(80.dp),
                contentScale = ContentScale.Fit
            )
        } else {
            Box(modifier = Modifier.size(80.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
