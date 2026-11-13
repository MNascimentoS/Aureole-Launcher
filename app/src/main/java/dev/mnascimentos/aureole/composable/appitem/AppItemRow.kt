package dev.mnascimentos.aureole.composable.appitem

import android.content.ComponentName
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.model.AppItemBottomSheetParams
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowMetrics
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.composable.overlay.AureoleOverlayWindow
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItemRow(
    app: AppInfo,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    config: AppItemRowConfig = AppItemRowConfig(),
    actions: AppItemRowActions = AppItemRowActions()
) {
    var showMenu by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = { showMenu = true },
                    role = Role.Button
                )
                .semantics(mergeDescendants = true) {}
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AppItemRowContent(
                app = app,
                isThemedAppIconsEnabled = config.isThemedAppIconsEnabled,
                isLeftHandedMode = config.isLeftHandedMode
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
    isThemedAppIconsEnabled: Boolean,
    isLeftHandedMode: Boolean
) {
    val appLabel = @Composable {
        AureoleText(
            text = app.label,
            color = AureoleDS.colors.onSurfaceMedium,
            modifier = Modifier.weight(1f),
            format = AureoleTextFormatParams(
                style = AureoleDS.typography.bodyLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = if (isLeftHandedMode) TextAlign.End else TextAlign.Start
            )
        )
    }

    val appIcon = @Composable {
        AureoleAppIcon(
            app = app,
            size = AppItemRowMetrics.IconOuterSize,
            params = AureoleAppIconParams(isThemed = isThemedAppIconsEnabled, contentDescription = app.label)
        )
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
            AureoleOverlayWindow(onDismissRequest = onDismiss) {
                AppItemBottomSheet(
                    params = AppItemBottomSheetParams(
                        app = app,
                        isFavorite = config.isFavorite,
                        actions = actions
                    ),
                    onDismiss = onDismiss
                )
            }
        } else {
            AppItemPopup(app = app, isFavorite = config.isFavorite, actions = actions, onDismiss = onDismiss)
        }
    }
}

@AureolePreview
@Composable
private fun AppItemRowPreview() {
    val mockApp = AppInfo(
        label = "Camera",
        packageName = "com.example.camera",
        componentName = ComponentName("com.example.camera", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )

    AureoleLauncherTheme {
        AppItemRow(
            app = mockApp,
            onClick = {},
            config = AppItemRowConfig(isFavorite = true)
        )
    }
}
