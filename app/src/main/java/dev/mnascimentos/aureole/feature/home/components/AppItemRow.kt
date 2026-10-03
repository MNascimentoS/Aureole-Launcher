package dev.mnascimentos.aureole.feature.home.components

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.components.model.AppItemRowConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
private val ICON_OUTER_SIZE = 42.dp
private val ICON_INNER_SIZE = 26.dp
private val ICON_CORNER_RADIUS = 12.dp
private val ICON_PADDING = 8.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemRow(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: AppItemRowConfig = AppItemRowConfig(),
    actions: AppItemRowActions = AppItemRowActions()
) {
    val uiState = LocalHomeUiState.current
    val isThemedAppIconsEnabled = uiState.isThemedAppIconsEnabled
    val isLeftHandedMode = uiState.isLeftHandedMode
    var showMenu by remember { mutableStateOf(false) }
    val iconBitmap: ImageBitmap = remember(app.packageName) {
        app.getIconBitmap()
    }

    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(MaterialTheme.shapes.medium)
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true }
                )
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppItemRowContent(
                app = app,
                iconBitmap = iconBitmap,
                isThemedAppIconsEnabled = isThemedAppIconsEnabled,
                isLeftHandedMode = isLeftHandedMode
            )
        }

        AppItemRowDropdownMenu(
            app = app,
            showMenu = showMenu,
            config = config,
            actions = actions,
            onDismiss = { showMenu = false }
        )
    }
}

@Composable
private fun RowScope.AppItemRowContent(
    app: AppInfo,
    iconBitmap: ImageBitmap,
    isThemedAppIconsEnabled: Boolean,
    isLeftHandedMode: Boolean
) {
    val iconColor = AureoleDS.colors.onSurfaceMedium
    val displayBitmap = remember(app.packageName, isThemedAppIconsEnabled, iconColor) {
        if (isThemedAppIconsEnabled) {
            val argb = iconColor.toArgb()
            app.getThemedIconBitmap(argb)
        } else {
            iconBitmap
        }
    }

    val appLabel = @Composable {
        AureoleText(
            text = app.label,
            style = AureoleDS.typography.bodyLarge,
            color = AureoleDS.colors.onSurfaceMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = if (isLeftHandedMode) TextAlign.End else TextAlign.Start,
            modifier = Modifier.weight(1f)
        )
    }

    val appIcon = @Composable {
        AppItemIcon(bitmap = displayBitmap, label = app.label, isThemed = isThemedAppIconsEnabled)
    }

    if (isLeftHandedMode) {
        appLabel()
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        appIcon()
    } else {
        appIcon()
        Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))
        appLabel()
    }
}

@Composable
private fun AppItemIcon(
    bitmap: ImageBitmap,
    label: String,
    isThemed: Boolean
) {
    if (isThemed) {
        Box(
            modifier = Modifier
                .size(ICON_OUTER_SIZE)
                .clip(RoundedCornerShape(ICON_CORNER_RADIUS))
                .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.85f))
                .padding(ICON_PADDING),
            contentAlignment = Alignment.Center
        ) {
            Image(
                bitmap = bitmap,
                contentDescription = label,
                modifier = Modifier.size(ICON_INNER_SIZE)
            )
        }
    } else {
        Image(
            bitmap = bitmap,
            contentDescription = label,
            modifier = Modifier.size(ICON_OUTER_SIZE)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppItemRowDropdownMenu(
    app: AppInfo,
    showMenu: Boolean,
    config: AppItemRowConfig,
    actions: AppItemRowActions,
    onDismiss: () -> Unit
) {
    if (showMenu) {
        if (config.useActionSheet) {
            AppItemBottomSheet(app = app, isFavorite = config.isFavorite, actions = actions, onDismiss = onDismiss)
        } else {
            AppItemPopup(app = app, isFavorite = config.isFavorite, actions = actions, onDismiss = onDismiss)
        }
    }
}

@AureolePreview
@Composable
fun AppItemRowPreview() {
    val mockApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockUiState = MainUiState(
        apps = listOf(mockApp),
        favoriteApps = listOf(mockApp)
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides mockUiState) {
            AppItemRow(
                app = mockApp,
                onClick = {},
                config = AppItemRowConfig(isFavorite = true)
            )
        }
    }
}
