package dev.mnascimentos.aureole.core.designsystem.model

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Whether the launcher is in left-handed mode. The design system and the shared `composable/` package read it
 * instead of the feature state, so they do not depend on `feature/`. The home route feeds it from the UI state.
 */
val LocalLeftHanded = staticCompositionLocalOf { false }
