package dev.mnascimentos.aureole.fixtures

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.platform.LocalConfiguration
import dev.mnascimentos.aureole.feature.home.grid.extensions.landscapeFromPortrait
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/** A prévia é de paisagem (`@AureoleLandscapePreview`): mais larga que alta. */
@Composable
@ReadOnlyComposable
internal fun isPreviewLandscape(): Boolean {
    val current = LocalConfiguration.current
    return current.screenWidthDp > current.screenHeightDp
}

/**
 * Na prévia de paisagem, marca a configuração como paisagem, como o aparelho deitado (a prévia só muda o
 * tamanho). Em retrato, ou se a orientação já é paisagem, não muda nada.
 */
@Composable
internal fun OrientedCase(content: @Composable () -> Unit) {
    val current = LocalConfiguration.current
    if (isPreviewLandscape() && current.orientation != Configuration.ORIENTATION_LANDSCAPE) {
        val configuration = Configuration(current).apply { orientation = Configuration.ORIENTATION_LANDSCAPE }
        CompositionLocalProvider(LocalConfiguration provides configuration, content = content)
    } else {
        content()
    }
}

/** O estado da home deitada: `isLandscape` e, se faltar, a grade de paisagem tirada da de retrato. */
internal fun MainUiState.inLandscape(): MainUiState = copy(
    isLandscape = true,
    landscapeGridItems = landscapeGridItems.ifEmpty { landscapeFromPortrait(gridItems, isLeftHandedMode) }
)
