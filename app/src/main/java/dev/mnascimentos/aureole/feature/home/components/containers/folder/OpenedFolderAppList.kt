package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.appitem.AureoleAppIcon
import dev.mnascimentos.aureole.composable.appitem.model.AureoleAppIconParams
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview

@Composable
fun OpenedFolderAppList(
    appsInFolder: List<AppInfo>,
    isLeftHandedMode: Boolean,
    onAppClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier,
    onAppLongClick: ((AppInfo) -> Unit)? = null
) {
    if (appsInFolder.isEmpty()) {
        Box(
            modifier = modifier
                .fillMaxWidth()
                .padding(vertical = AureoleDS.dimens.medium),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.home_folder_opened_empty),
                style = AureoleDS.typography.bodySmall,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    } else {
        LazyColumn(
            contentPadding = PaddingValues(vertical = AureoleDS.dimens.minimal),
            modifier = modifier.fillMaxWidth()
        ) {
            items(
                items = appsInFolder,
                key = { it.packageName },
                contentType = { "opened_folder_app_row" }
            ) { app ->
                OpenedFolderAppItemRow(
                    app = app,
                    isLeftHandedMode = isLeftHandedMode,
                    onClick = { onAppClick(app) },
                    onLongClick = { onAppLongClick?.invoke(app) }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OpenedFolderAppItemRow(
    app: AppInfo,
    isLeftHandedMode: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit = {}
) {
    val uiState = LocalHomeUiState.current
    val iconParams = AureoleAppIconParams(
        isThemed = uiState.isThemedAppIconsEnabled,
        showThemedBox = false,
        legacyShape = CircleShape,
        contentDescription = app.label
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minWidth = AureoleDS.dimens.minTouchTarget, minHeight = AureoleDS.dimens.minTouchTarget)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmallCompact))
            .combinedClickable(
                role = Role.Button,
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(horizontal = AureoleDS.dimens.xSmall, vertical = AureoleDS.dimens.xSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isLeftHandedMode) {
            Text(
                text = app.label,
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceHigh,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(AureoleDS.dimens.smallCompact))
            AureoleAppIcon(app = app, size = AureoleDS.dimens.iconXLargeMedium, params = iconParams)
        } else {
            AureoleAppIcon(app = app, size = AureoleDS.dimens.iconXLargeMedium, params = iconParams)
            Spacer(modifier = Modifier.width(AureoleDS.dimens.smallCompact))
            Text(
                text = app.label,
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceHigh,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@AureolePreview
@Composable
private fun OpenedFolderAppListPreview() {
    val sampleApps = listOf(
        AppInfo(
            label = "Camera",
            packageName = "com.example.camera",
            componentName = ComponentName("com.example.camera", "MainActivity"),
            icon = 0xFF2E7D32.toInt().toDrawable()
        )
    )
    AureoleLauncherTheme {
        OpenedFolderAppList(
            appsInFolder = sampleApps,
            isLeftHandedMode = false,
            onAppClick = {}
        )
    }
}
