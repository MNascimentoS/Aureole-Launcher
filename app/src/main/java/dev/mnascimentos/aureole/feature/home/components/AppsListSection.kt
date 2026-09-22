package dev.mnascimentos.aureole.feature.home.components

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureolePreview
import dev.mnascimentos.aureole.core.designsystem.theme.fadingEdges
import dev.mnascimentos.aureole.feature.home.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.HomeScreenActions
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private const val SEARCH_BAR_HAZE_ALPHA_MULTIPLIER = 0.7f
private const val SEARCH_BAR_MIN_ALPHA = 0.2f
private const val SEARCH_BAR_MAX_ALPHA = 0.95f
private const val SEARCH_BAR_DEFAULT_ALPHA = 0.9f

@Composable
fun AppsListDrawer(
    listState: LazyListState,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val uiState = LocalHomeUiState.current
    val actions = LocalHomeActions.current
    val startPadding = if (uiState.isLeftHandedMode) 72.dp else 24.dp
    val endPadding = if (uiState.isLeftHandedMode) 24.dp else 72.dp

    val hazeModifier = if (uiState.isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 30.dp,
                tint = HazeTint(MaterialTheme.colorScheme.background.copy(alpha = uiState.hazeOpacity))
            )
        ) {
            blurEnabled = uiState.isHazeEnabled
        }
    } else {
        Modifier
    }
    val drawerBgColor = if (uiState.isHazeEnabled && (hazeState != null)) {
        MaterialTheme.colorScheme.background.copy(alpha = uiState.hazeOpacity)
    } else {
        MaterialTheme.colorScheme.background
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .then(hazeModifier)
            .background(drawerBgColor)
            .statusBarsPadding()
    ) {
        LazyColumn(
            state = listState,
            contentPadding = PaddingValues(
                start = startPadding,
                top = 24.dp,
                end = endPadding,
                bottom = 100.dp
            ),
            modifier = Modifier
                .weight(1f)
                .fadingEdges(listState)
        ) {
            appsListItems(uiState, actions)
        }

        BottomSearchBar(
            query = uiState.searchQuery,
            onQueryChange = actions.onSearchQueryChanged,
            onSettingsClick = actions.onSettingsClick,
            hazeState = hazeState,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
        )
    }
}

@Composable
private fun BottomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
    hazeState: HazeState? = null,
) {
    val uiState = LocalHomeUiState.current
    val searchBarHazeModifier = if (uiState.isHazeEnabled && (hazeState != null)) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 20.dp,
                tint = HazeTint(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = uiState.hazeOpacity))
            )
        ) {
            blurEnabled = uiState.isHazeEnabled
        }
    } else {
        Modifier
    }

    val searchBarAlpha = if (uiState.isHazeEnabled) {
        (uiState.hazeOpacity * SEARCH_BAR_HAZE_ALPHA_MULTIPLIER)
            .coerceIn(SEARCH_BAR_MIN_ALPHA, SEARCH_BAR_MAX_ALPHA)
    } else {
        SEARCH_BAR_DEFAULT_ALPHA
    }

    Box(
        modifier = modifier
            .then(searchBarHazeModifier)
            .background(
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = searchBarAlpha)
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (uiState.isLeftHandedMode) {
                SettingsButton(onClick = onSettingsClick)
                Spacer(modifier = Modifier.width(16.dp))
                SearchField(query = query, onQueryChange = onQueryChange, modifier = Modifier.weight(1f))
            } else {
                SearchField(query = query, onQueryChange = onQueryChange, modifier = Modifier.weight(1f))
                Spacer(modifier = Modifier.width(16.dp))
                SettingsButton(onClick = onSettingsClick)
            }
        }
    }
}

@Composable
private fun SearchField(query: String, onQueryChange: (String) -> Unit, modifier: Modifier = Modifier) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = { Text("Search apps") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
        singleLine = true,
        shape = RoundedCornerShape(24.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        modifier = modifier.height(56.dp)
    )
}

@Composable
private fun SettingsButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .height(56.dp)
            .width(56.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.primaryContainer)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = "Settings",
            tint = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

fun LazyListScope.appsListItems(
    uiState: MainUiState,
    actions: HomeScreenActions,
) {
    itemsIndexed(
        items = uiState.filteredApps,
        key = { _, app -> app.packageName }
    ) { index, app ->
        val currentLetter = app.firstLetter

        val isFirstOfLetter = (index == 0) || (uiState.filteredApps[index - 1].firstLetter != currentLetter)

        if (isFirstOfLetter && uiState.searchQuery.isBlank()) {
            Text(
                text = currentLetter.toString(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(
                    top = 20.dp,
                    bottom = 8.dp
                )
            )
        }

        AppItemRow(
            app = app,
            onClick = { actions.onAppClick(app) },
        )
    }
}

@AureolePreview
@Composable
fun AppsListDrawerPreview() {
    val mockApp = AppInfo(
        label = "Gallery",
        packageName = "com.example.gallery",
        componentName = ComponentName("com.example.gallery", "MainActivity"),
        icon = ColorDrawable(0xFF1B65C0.toInt())
    )
    val mockUiState = MainUiState(
        apps = listOf(mockApp),
        filteredApps = listOf(mockApp),
        isHazeEnabled = false
    )
    val mockActions = HomeScreenActions(
        onWidgetRowHeightChanged = {},
        onAddWidgetClick = {},
        onRemoveWidgetClick = {},
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
