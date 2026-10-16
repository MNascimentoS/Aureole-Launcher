package dev.mnascimentos.aureole.feature.home.components.favorites

import android.appwidget.AppWidgetHost
import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.components.AureoleText
import dev.mnascimentos.aureole.core.designsystem.icons.Star
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val PREVIEW_APPWIDGET_HOST_ID = 1024

@Composable
internal fun FavoritesEmptyHint(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(RoundedCornerShape(AureoleDS.dimens.radiusMedium))
            .background(AureoleDS.colors.surfaceVariant.copy(alpha = 0.3f))
            .clickable(onClick = onClick)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AureoleDS.icons.Star(
                tint = AureoleDS.colors.onSurfaceHigh,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            AureoleText(
                text = "Tap to select Favorites",
                style = AureoleDS.typography.bodyMedium,
                color = AureoleDS.colors.onSurfaceMedium
            )
        }
    }
}

@Composable
internal fun AllAppsDivider() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.12f)
        )
        AureoleText(
            text = " ALL APPS ",
            style = AureoleDS.typography.labelSmall,
            color = AureoleDS.colors.onSurfaceMedium.copy(alpha = 0.6f),
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = AureoleDS.colors.onSurfaceHigh.copy(alpha = 0.12f)
        )
    }
}

@AureolePreview
@Composable
fun FavoritesListPreview() {
    val context = LocalContext.current
    val mockApp = AppInfo(
        label = "Phone",
        packageName = "com.example.phone",
        componentName = ComponentName("com.example.phone", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockUiState = MainUiState(
        favoriteApps = listOf(mockApp),
        favoriteAppPackages = listOf(mockApp.packageName),
        isWidgetRowEnabled = false
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
            FavoritesList(
                config = FavoritesListConfig(
                    currentHeightDp = 160.dp,
                    currentHeightPx = 400f,
                    state = rememberLazyListState()
                ),
                appWidgetHost = remember { AppWidgetHost(context, PREVIEW_APPWIDGET_HOST_ID) }
            )
        }
    }
}
