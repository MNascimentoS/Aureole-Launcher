package dev.mnascimentos.aureole.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    LazyColumn(
        state = listState,
        contentPadding = PaddingValues(start = 24.dp, top = 24.dp, end = 72.dp, bottom = 48.dp),
        modifier = modifier
            .fillMaxHeight()
            .fillMaxWidth(0.85f)
    ) {
        appsListItems(uiState, actions)
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

        if (isFirstOfLetter) {
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
            onClick = { actions.onAppClick(app) }
        )
    }
}
