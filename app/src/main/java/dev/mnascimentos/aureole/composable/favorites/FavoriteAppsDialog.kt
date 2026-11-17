package dev.mnascimentos.aureole.composable.favorites

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogActions
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsDialogConfig
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsOverlayState
import dev.mnascimentos.aureole.composable.favorites.model.FavoriteAppsPreviewParams
import dev.mnascimentos.aureole.composable.favorites.model.appsListSections
import dev.mnascimentos.aureole.composable.preview.AureolePreviewFrame
import dev.mnascimentos.aureole.core.designsystem.components.AureoleEditOverlay
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayConfig
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayParams
import dev.mnascimentos.aureole.core.designsystem.model.OverlayLayout
import dev.mnascimentos.aureole.core.designsystem.model.overlayLayoutFor
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import kotlinx.coroutines.delay

private const val DISMISS_DELAY_MS = 300L
private const val PREVIEW_MAX_APPS = 4

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

    var searchQuery by remember { mutableStateOf("") }

    val previewParams = rememberPreviewParams(config)

    // The overlay fills the screen: side by side (landscape), the options chips move next to the search (RF-05).
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val isSideBySide = overlayLayoutFor(
            width = maxWidth.value,
            height = maxHeight.value,
            hasPreview = true
        ) == OverlayLayout.SIDE_BY_SIDE
        FavoriteAppsOverlay(
            config = config,
            actions = actions,
            state = FavoriteAppsOverlayState(
                isVisible = isVisible,
                onHide = { isVisible = false },
                searchQuery = searchQuery,
                onSearchQueryChange = { searchQuery = it },
                isSideBySide = isSideBySide
            ),
            previewParams = previewParams
        )
    }
}

@Composable
private fun FavoriteAppsOverlay(
    config: FavoriteAppsDialogConfig,
    actions: FavoriteAppsDialogActions,
    state: FavoriteAppsOverlayState,
    previewParams: FavoriteAppsPreviewParams
) {
    val isVisible = state.isVisible
    val searchQuery = state.searchQuery
    AureoleEditOverlay(
        onDismissRequest = state.onHide,
        params = AureoleEditOverlayParams(
            config = AureoleEditOverlayConfig(
                title = stringResource(R.string.home_favorites_choose),
                isVisible = isVisible,
                animated = true,
                isLeftHanded = config.rowConfig.isLeftHandedMode,
                onSettingsClick = actions.onSettingsClick,
                onBackPress = state.onHide,
                headerLeadingAction = actions.onCustomizeHome?.let { onCustomizeHome ->
                    {
                        FavoriteAppsCustomizeHomeButton(onClick = onCustomizeHome)
                    }
                }
            ),
            previewContent = {
                AureolePreviewFrame {
                    FavoriteAppsPreviewContent(previewParams)
                }
            },
            bottomBarContent = {
                FavoriteAppsBottomBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = state.onSearchQueryChange,
                    options = config.takeIf { state.isSideBySide }
                )
            },
            sheetContent = {
                FavoriteAppsSheetContent(
                    config = config,
                    actions = actions,
                    searchQuery = searchQuery,
                    showOptions = !state.isSideBySide
                )
            }
        )
    )
}

/** The list the Home would show for the current choices; the Settings sheet (no switches) shows only favorites. */
@Composable
private fun rememberPreviewParams(config: FavoriteAppsDialogConfig): FavoriteAppsPreviewParams {
    val showAll = config.showAllApps?.checked ?: false
    val hasLabels = showAll && (config.showLabels?.checked ?: true)
    val sections = remember(config.allApps, config.favoriteAppPackages, showAll) {
        appsListSections(config.allApps, config.favoriteAppPackages, showAll, PREVIEW_MAX_APPS)
    }
    return FavoriteAppsPreviewParams(sections = sections, hasLabels = hasLabels, rowConfig = config.rowConfig)
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
