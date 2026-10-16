package dev.mnascimentos.aureole.feature.home.components.widgets

import android.appwidget.AppWidgetProviderInfo
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.HazeTint
import dev.chrisbanes.haze.hazeEffect
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetAppGroup
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorIntent
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetSelectorState
import dev.mnascimentos.aureole.feature.home.components.widgets.model.WidgetVariant
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetPickerScreen(
    isBlurEnabled: Boolean = true,
    hazeState: HazeState? = null,
    viewModel: WidgetPickerViewModel = koinViewModel(),
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.processIntent(WidgetSelectorIntent.LoadWidgets)
    }

    WidgetSelectorScreen(
        state = state,
        hazeState = if (isBlurEnabled) hazeState else null,
        onIntent = { viewModel.processIntent(it) },
        onWidgetSelected = onWidgetSelected,
        onDismiss = onDismiss
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WidgetSelectorScreen(
    state: WidgetSelectorState,
    hazeState: HazeState? = null,
    onIntent: (WidgetSelectorIntent) -> Unit,
    onWidgetSelected: (AppWidgetProviderInfo) -> Unit,
    onDismiss: () -> Unit
) {
    val hazeModifier = if (hazeState != null) {
        Modifier.hazeEffect(
            state = hazeState,
            style = HazeStyle(
                blurRadius = 32.dp,
                tint = HazeTint(Color.Black.copy(alpha = 0.45f))
            )
        ) {
            blurEnabled = true
        }
    } else {
        Modifier
    }

    val backgroundColor = if (hazeState != null) {
        Color.Transparent
    } else {
        AureoleDS.colors.background
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .then(hazeModifier)
            .background(backgroundColor)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 16.dp)
                .clickable(enabled = false) {}
        ) {
            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.widgetGroups.isEmpty() && state.searchQuery.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Nenhum widget encontrado.", color = Color.White)
                }
            } else {
                Box(modifier = Modifier.weight(1f)) {
                    WidgetGroupList(
                        state = state,
                        onIntent = onIntent,
                        onWidgetSelected = onWidgetSelected,
                        onDismiss = onDismiss
                    )
                }
            }
        }
    }
}

@AureolePreview
@Composable
fun WidgetSelectorScreenPreview() {
    val sampleWidgetGroup = WidgetAppGroup(
        appId = "com.example.calendar",
        appName = "Calendar",
        availableWidgets = listOf(
            WidgetVariant(
                widgetId = "calendar_1",
                title = "Month View",
                minSpanX = 2,
                minSpanY = 2
            ),
            WidgetVariant(
                widgetId = "calendar_2",
                title = "Agenda View",
                minSpanX = 4,
                minSpanY = 2
            )
        )
    )
    val sampleState = WidgetSelectorState(
        isLoading = false,
        searchQuery = "",
        widgetGroups = listOf(sampleWidgetGroup),
        expandedAppIds = setOf("com.example.calendar")
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(LocalHomeUiState provides MainUiState()) {
            WidgetSelectorScreen(
                state = sampleState,
                onIntent = {},
                onWidgetSelected = {},
                onDismiss = {}
            )
        }
    }
}
