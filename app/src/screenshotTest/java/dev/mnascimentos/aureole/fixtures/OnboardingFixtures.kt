package dev.mnascimentos.aureole.fixtures

import android.appwidget.AppWidgetHost
import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import dev.mnascimentos.aureole.feature.home.MainScaffold
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.feature.home.model.MainUiState

/**
 * A home inteira (`MainScaffold`, que desenha a camada do onboarding por cima) com [onboarding]; na prévia de
 * paisagem, a mesma home deitada. [edit] ajusta o resto do estado (grade vazia, modo de edição) e [overlay] desenha
 * por cima da home o que o scaffold não desenha sozinho (a sheet Visual, o chip do coach).
 */
@Composable
internal fun OnboardingCase(
    onboarding: OnboardingUiState,
    edit: (MainUiState) -> MainUiState = { it },
    overlay: @Composable () -> Unit = {}
) {
    val current = LocalConfiguration.current
    val isLandscape = current.screenWidthDp > current.screenHeightDp
    val base = createSampleHomeData().uiState
    val items = if (isLandscape) GridDefaults.getDefaultGridItems(isLandscape = true) else base.gridItems
    val uiState = edit(
        base.copy(
            isLandscape = isLandscape,
            gridItems = items,
            landscapeGridItems = if (isLandscape) items else base.landscapeGridItems,
            onboarding = onboarding
        )
    )
    val configuration = Configuration(current).apply {
        if (isLandscape) orientation = Configuration.ORIENTATION_LANDSCAPE
    }
    val context = LocalContext.current
    CompositionLocalProvider(LocalConfiguration provides configuration) {
        HomeCase(uiState) {
            Box(modifier = Modifier.fillMaxSize()) {
                MainScaffold(appWidgetHost = AppWidgetHost(context, ONBOARDING_HOST_ID), onWidgetSelected = {})
                overlay()
            }
        }
    }
}

private const val ONBOARDING_HOST_ID = 1024
