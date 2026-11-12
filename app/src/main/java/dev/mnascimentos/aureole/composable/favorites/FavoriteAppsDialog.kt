package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.appitem.AppItemRow
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsScreenMode
import dev.mnascimentos.aureole.composable.favorites.model.backTarget
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_MS = 300L
private const val PREVIEW_MAX_APPS = 4

@Composable
@ReadOnlyComposable
private fun favoriteAppsTitle(mode: FavoriteAppsScreenMode): String = when (mode) {
    FavoriteAppsScreenMode.MAIN -> stringResource(R.string.home_favorites_dialog_title)
    FavoriteAppsScreenMode.MANAGE -> stringResource(R.string.home_favorites_manage_apps_order)
    FavoriteAppsScreenMode.EDIT_SHORTCUTS -> stringResource(R.string.home_favorites_edit_shortcuts)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAppsDialog(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    modifier: Modifier = Modifier,
    initialMode: FavoriteAppsScreenMode = FavoriteAppsScreenMode.MAIN
) {
    var isVisible by remember { mutableStateOf(true) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            actions.onDismiss()
        }
    }

    var currentMode by remember { mutableStateOf(initialMode) }
    var searchQuery by remember(currentMode) { mutableStateOf("") }

    val appMap = remember(config.allApps) {
        config.allApps.associateBy { it.packageName }
    }

    val previewFavoriteApps = remember(config.favoriteAppPackages, appMap) {
        config.favoriteAppPackages.mapNotNull { pkg -> appMap[pkg] }.take(PREVIEW_MAX_APPS)
    }

    val titleText = favoriteAppsTitle(currentMode)

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = titleText,
                isVisible = isVisible,
                animated = true,
                isLeftHanded = config.rowConfig.isLeftHandedMode,
                onSettingsClick = actions.onSettingsClick,
                onBackPress = { currentMode.backTarget()?.let { currentMode = it } ?: run { isVisible = false } }
            ),
            previewContent = {
                AureolePreviewFrame {
                    FavoriteAppsPreviewContent(previewFavoriteApps = previewFavoriteApps, rowConfig = config.rowConfig)
                }
            },
            bottomBarContent = if (currentMode == FavoriteAppsScreenMode.MAIN) {
                null
            } else {
                {
                    FavoriteAppsBottomBar(
                        onBack = { currentMode = FavoriteAppsScreenMode.MAIN },
                        searchQuery = searchQuery,
                        onSearchQueryChange = { searchQuery = it }
                    )
                }
            },
            sheetContent = {
                FavoriteAppsSheetContent(
                    currentMode = currentMode,
                    config = config,
                    actions = actions,
                    onModeChange = { currentMode = it },
                    searchQuery = searchQuery
                )
            }
        )
    )
}

@Composable
private fun FavoriteAppsPreviewContent(previewFavoriteApps: List<AppInfo>, rowConfig: AppItemRowConfig) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = AureoleDS.dimens.large, vertical = AureoleDS.dimens.small)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusLarge))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.35f))
            .border(
                width = AureoleDS.dimens.borderMinimal,
                color = AureoleDS.colors.outline.copy(alpha = 0.25f),
                shape = RoundedCornerShape(AureoleDS.dimens.radiusLarge)
            )
            .padding(vertical = AureoleDS.dimens.xSmall, horizontal = AureoleDS.dimens.xxSmall),
        contentAlignment = Alignment.Center
    ) {
        if (previewFavoriteApps.isEmpty()) {
            AureoleText(
                text = stringResource(R.string.home_favorites_none_selected),
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.padding(AureoleDS.dimens.medium),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        } else {
            FavoriteAppsPreviewList(previewFavoriteApps = previewFavoriteApps, rowConfig = rowConfig)
        }
    }
}

@Composable
private fun FavoriteAppsPreviewList(previewFavoriteApps: List<AppInfo>, rowConfig: AppItemRowConfig) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(AureoleDS.dimens.minimal),
        modifier = Modifier.fillMaxWidth()
    ) {
        items(
            items = previewFavoriteApps,
            key = { app -> "preview_${app.packageName}" },
            contentType = { "preview_app_item" }
        ) { app ->
            AppItemRow(
                app = app,
                onClick = {},
                config = rowConfig
            )
        }
    }
}

@AureolePreview
@Composable
private fun FavoriteAppsDialogPreview() {
    AureoleLauncherTheme {
        FavoriteAppsDialog(
            config = FavoriteAppsDialogConfig(
                allApps = emptyList(),
                favoriteAppPackages = emptyList()
            ),
            actions = FavoriteAppsDialogActions()
        )
    }
}
