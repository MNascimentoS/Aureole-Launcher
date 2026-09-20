package dev.mnascimentos.aureole.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.ui.MainUiState
import dev.mnascimentos.aureole.ui.screens.HomeScreenActions

@Composable
fun AppsListDrawer(
    uiState: MainUiState,
    actions: HomeScreenActions,
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val startPadding = if (uiState.isLeftHandedMode) 72.dp else 24.dp
    val endPadding = if (uiState.isLeftHandedMode) 24.dp else 72.dp

    Column(
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
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
            modifier = Modifier.weight(1f)
        ) {
            appsListItems(uiState, actions)
        }

        BottomSearchBar(
            query = uiState.searchQuery,
            onQueryChange = actions.onSearchQueryChanged,
            onSettingsClick = actions.onSettingsClick,
            isLeftHandedMode = uiState.isLeftHandedMode,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .imePadding()
        )
    }
}

// Convenience overload for backwards compatibility
@Suppress("UNUSED_PARAMETER", "LongParameterList")
@Composable
fun AppsListDrawer(
    uiState: MainUiState,
    actions: HomeScreenActions,
    listState: LazyListState,
    onSearchQueryChanged: (String) -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AppsListDrawer(
        uiState = uiState,
        actions = actions,
        listState = listState,
        modifier = modifier
    )
}

@Composable
private fun BottomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSettingsClick: () -> Unit,
    isLeftHandedMode: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLeftHandedMode) {
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
            isLeftHandedMode = uiState.isLeftHandedMode
        )
    }
}
