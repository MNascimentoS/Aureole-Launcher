/**
 * Personalizar relógio.
 *
 * @en Customize clock
 * @area home
 * @paisagem
 * @grupo Relógio / Clock
 */
package dev.mnascimentos.aureole.telas.relogio

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.sheets.model.ComponentColorRole
import dev.mnascimentos.aureole.core.designsystem.model.AureoleEditOverlayState
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.clock.EditClockOverlay
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockTab
import dev.mnascimentos.aureole.feature.home.components.clock.model.EditClockOverlayOptions
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

/** 2026-11-11 17:29 UTC, para os tiles de formato saírem iguais em toda execução. */
private const val FIXED_NOW_MILLIS = 1_794_418_140_000L

/** Sheet "Personalizar relógio" em repouso (60%), na aba [tab] ou na página de cor [colorRole]. */
@Composable
private fun EditClockCase(tab: ClockTab = ClockTab.STYLE, colorRole: ComponentColorRole? = null) {
    val sampleData = createSampleHomeData()
    HomeCase(sampleData.uiState) {
        EditClockOverlay(
            uiState = sampleData.uiState,
            actions = sampleData.actions,
            onDismissRequest = {},
            options = EditClockOverlayOptions(
                overlayState = AureoleEditOverlayState(initialExpanded = false),
                initialTab = tab,
                nowMillis = FIXED_NOW_MILLIS,
                initialColorRole = colorRole
            )
        )
    }
}

/**
 * Estilo.
 *
 * @en Style
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = EditClockCase()

/**
 * Cores.
 *
 * @en Colors
 */
@PreviewTest
@AureolePreview
@Composable
private fun CoresScreenshotTest() = EditClockCase(tab = ClockTab.COLORS)

/**
 * Página de cor.
 *
 * A página de cor do texto dentro da sheet: voltar, título e sem barra de abas.
 *
 * @en Color page
 */
@PreviewTest
@AureolePreview
@Composable
private fun PaginaCorScreenshotTest() = EditClockCase(colorRole = ComponentColorRole.TEXT)
