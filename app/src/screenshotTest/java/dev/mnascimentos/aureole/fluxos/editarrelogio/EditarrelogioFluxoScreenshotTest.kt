package dev.mnascimentos.aureole.fluxos.editarrelogio

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.EditClockOverlay
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

/** 2026-11-11 17:29 UTC, o mesmo instante de `telas/relogio`, para os tiles de formato saírem iguais. */
private const val FIXED_NOW_MILLIS = 1_794_418_140_000L

/**
 * A sheet "Personalizar relógio" aberta durante o onboarding: mostra o aviso "arraste para fechar, já está salvo"
 * até o "Entendi" (relogio-home-sheet RF-D2). Fora do onboarding é `telas/relogio/Padrao`.
 */
@PreviewTest
@AureolePreview
@Composable
private fun P02AvisoOnboardingScreenshotTest() {
    val sampleData = createSampleHomeData()
    val uiState = sampleData.uiState.copy(
        onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST, isChecklistExpanded = false)
    )
    HomeCase(uiState) {
        EditClockOverlay(
            uiState = uiState,
            actions = sampleData.actions,
            onDismissRequest = {},
            options = EditClockOverlayOptions(
                overlayState = AureoleEditOverlayState(initialExpanded = false),
                nowMillis = FIXED_NOW_MILLIS
            )
        )
    }
}
