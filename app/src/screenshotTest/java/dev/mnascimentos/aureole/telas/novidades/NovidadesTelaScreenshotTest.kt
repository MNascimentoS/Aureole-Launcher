/**
 * Novidades.
 *
 * @en Release notes
 * @area home
 * @paisagem
 */
package dev.mnascimentos.aureole.telas.novidades

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.sheets.ReleaseNotesBottomSheet
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase

private val SAMPLE_VERSION = ReleaseNoteVersion(
    versionName = "0.9.13",
    releaseDate = "Outubro 2026",
    isCurrent = true,
    sections = listOf(
        ReleaseNoteSection(
            title = "Novidades",
            items = listOf(
                ReleaseNoteItem(
                    title = "Primeiros passos guiados",
                    description = "Um passeio rapido na primeira abertura mostra o essencial.",
                    type = ReleaseNoteType.FEATURE
                ),
                ReleaseNoteItem(
                    title = "Ajustes mais faceis de achar",
                    description = "O que voce mais usa fica mais perto do polegar.",
                    type = ReleaseNoteType.IMPROVEMENT
                )
            )
        )
    )
)

/**
 * Padrão.
 *
 * Novidades: a versão atual aberta e duas anteriores.
 *
 * @en Default
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(MainUiState()) {
    ReleaseNotesBottomSheet(
        onDismissRequest = {},
        allVersions = listOf(
            SAMPLE_VERSION,
            SAMPLE_VERSION.copy(versionName = "0.9.12", isCurrent = false),
            SAMPLE_VERSION.copy(versionName = "0.9.11", isCurrent = false)
        )
    )
}
