package dev.mnascimentos.aureole.composable

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.feature.home.components.onboarding.extensions.showsSheetLogo
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

/** Whether the home's sheets show the Aureole logo in their title: not while the onboarding runs (RF-03). */
@Composable
@ReadOnlyComposable
fun showsHomeSheetLogo(): Boolean = LocalHomeUiState.current.onboarding.showsSheetLogo
