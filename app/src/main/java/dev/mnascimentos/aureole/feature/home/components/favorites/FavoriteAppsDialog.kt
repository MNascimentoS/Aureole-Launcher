package dev.mnascimentos.aureole.feature.home.components.favorites

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppItemRow
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoriteAppsScreenMode
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_MS = 300L
private const val PREVIEW_WIDTH_FRACTION = 0.88f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoriteAppsDialog(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    modifier: Modifier = Modifier
) {
    var isVisible by remember { mutableStateOf(true) }

    if (!isVisible) {
        LaunchedEffect(isVisible) {
            delay(DISMISS_DELAY_MS)
            actions.onDismiss()
        }
    }

    var currentMode by remember { mutableStateOf(FavoriteAppsScreenMode.MAIN) }

    val appMap = remember(config.allApps) {
        config.allApps.associateBy { it.packageName }
    }

    val previewFavoriteApps = remember(config.favoriteAppPackages, appMap) {
        config.favoriteAppPackages.mapNotNull { pkg -> appMap[pkg] }
    }

    val titleText = when (currentMode) {
        FavoriteAppsScreenMode.MAIN -> "Lista de Aplicativos"
        FavoriteAppsScreenMode.EDIT_SHORTCUTS -> "Edit shortcuts"
        FavoriteAppsScreenMode.APPEARANCE -> "Aparência"
    }

    AureoleEditOverlay(
        onDismissRequest = { isVisible = false },
        modifier = modifier,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = titleText,
                isVisible = isVisible,
                animated = true,
                maxSheetHeightFraction = 0.65f,
                onBackPress = {
                    if (currentMode != FavoriteAppsScreenMode.MAIN) {
                        currentMode = FavoriteAppsScreenMode.MAIN
                    } else {
                        isVisible = false
                    }
                }
            ),
            previewContent = {
                FavoriteAppsPreviewContent(previewFavoriteApps = previewFavoriteApps)
            },
            bottomBarContent = {
                FavoriteAppsBottomBar(
                    currentMode = currentMode,
                    onBack = { currentMode = FavoriteAppsScreenMode.MAIN },
                    onClose = { isVisible = false }
                )
            },
            sheetContent = {
                FavoriteAppsSheetContent(
                    currentMode = currentMode,
                    config = config,
                    actions = actions,
                    onEditShortcutsClick = { currentMode = FavoriteAppsScreenMode.EDIT_SHORTCUTS },
                    onAppearanceClick = { currentMode = FavoriteAppsScreenMode.APPEARANCE }
                )
            }
        )
    )
}

@Composable
private fun FavoriteAppsPreviewContent(previewFavoriteApps: List<AppInfo>) {
    Box(
        modifier = Modifier
            .fillMaxWidth(PREVIEW_WIDTH_FRACTION)
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
                text = "Nenhum favorito selecionado",
                color = AureoleDS.colors.onSurfaceMedium,
                modifier = Modifier.padding(AureoleDS.dimens.medium),
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyMedium)
            )
        } else {
            FavoriteAppsPreviewList(previewFavoriteApps = previewFavoriteApps)
        }
    }
}

@Composable
private fun FavoriteAppsPreviewList(previewFavoriteApps: List<AppInfo>) {
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
                onClick = {}
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
