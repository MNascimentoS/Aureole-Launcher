package dev.mnascimentos.aureole.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.feature.home.model.MainUiState

val LocalHomeUiState = staticCompositionLocalOf { MainUiState() }

/** Provides [uiState] and, with it, the hand mode that the design system and `composable/` read. */
@Composable
fun ProvideHomeUiState(uiState: MainUiState, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalHomeUiState provides uiState,
        LocalLeftHanded provides uiState.isLeftHandedMode,
        content = content
    )
}
