package dev.mnascimentos.aureole.feature.home.components.appsdrawer

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSectionHeader
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.components.AureoleTextFormatParams
import dev.mnascimentos.aureole.core.designsystem.icons.Settings
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.AppItemRow
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private object AppsListMetrics {
    val SectionHeaderTopPadding = 20.dp
    val SettingsIconContainerSize = 42.dp
    val SettingsBottomSpacerHeight = 80.dp
}

fun LazyListScope.appsListItems(
    uiState: MainUiState,
    actions: HomeScreenActions,
) {
    val filteredApps = uiState.filteredApps
    val isSearchQueryBlank = uiState.searchQuery.isBlank()

    itemsIndexed(
        items = filteredApps,
        key = { _, app -> app.packageName },
        contentType = { _, _ -> "app_item" }
    ) { index, app ->
        val currentLetter = app.firstLetter
        val isFirstOfLetter = (index == 0) || (filteredApps[index - 1].firstLetter != currentLetter)

        if (isFirstOfLetter && isSearchQueryBlank) {
            AureoleSectionHeader(
                title = currentLetter.toString(),
                color = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.padding(
                    top = AppsListMetrics.SectionHeaderTopPadding,
                    bottom = AureoleDS.dimens.xSmall
                )
            )
        }

        val isFav = uiState.favoriteAppPackages.contains(app.packageName)
        AppItemRow(
            app = app,
            onClick = { actions.onAppClick(app) },
            config = AppItemRowConfig(isFavorite = isFav),
            actions = AppItemRowActions(
                onToggleFavorite = { actions.onToggleFavorite(it) },
                onEditFavoritesClick = { actions.onOpenFavoritePicker(null) },
                onAppInfoClick = { actions.onAppInfoClick(it) },
                onUninstallClick = { actions.onUninstallAppClick(it) }
            )
        )
    }

    settingsListItem(actions)
}

private fun LazyListScope.settingsListItem(actions: HomeScreenActions) {
    item(key = "aureole_settings_item", contentType = "settings_item") {
        Spacer(modifier = Modifier.height(AureoleDS.dimens.medium))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AureoleDS.dimens.radiusSmall))
                .clickable { actions.onSettingsClick() }
                .padding(horizontal = AureoleDS.dimens.medium, vertical = AureoleDS.dimens.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(AppsListMetrics.SettingsIconContainerSize)
                    .clip(CircleShape)
                    .background(AureoleDS.colors.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                AureoleDS.icons.Settings(
                    tint = AureoleDS.colors.onSurfaceHigh,
                    modifier = Modifier.size(AureoleDS.dimens.iconLarge)
                )
            }

            Spacer(modifier = Modifier.width(AureoleDS.dimens.medium))

            AureoleText(
                text = "Aureole Settings",
                color = AureoleDS.colors.onSurfaceMedium,
                format = AureoleTextFormatParams(style = AureoleDS.typography.bodyLarge)
            )
        }
        Spacer(modifier = Modifier.height(AppsListMetrics.SettingsBottomSpacerHeight))
    }
}

@AureolePreview
@Composable
private fun AppsListDrawerPreview() {
    val mockApp = AppInfo(
        label = "Gallery",
        packageName = "com.example.gallery",
        componentName = ComponentName("com.example.gallery", "MainActivity"),
        icon = 0xFF1B65C0.toInt().toDrawable()
    )
    val mockUiState = MainUiState(
        apps = listOf(mockApp),
        filteredApps = listOf(mockApp),
        isHazeEnabled = false
    )
    val mockActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = { _, _ -> },
        onAppClick = {},
        onExpandNotificationShade = {},
        onFolderIntent = {},
        onSetAddAppToFolderDialogVisible = {},
        onSetRenameFolderDialogVisible = {},
        onSearchQueryChanged = {},
        onSettingsClick = {},
        onAllAppsDrawerClose = {},
        onAllAppsDrawerOpen = {}
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides mockUiState,
            LocalHomeActions provides mockActions
        ) {
            AppsListDrawer(
                listState = rememberLazyListState()
            )
        }
    }
}
