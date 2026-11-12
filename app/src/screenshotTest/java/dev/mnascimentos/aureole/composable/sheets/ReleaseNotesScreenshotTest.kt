package dev.mnascimentos.aureole.composable.sheets

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteItem
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteSection
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteType
import dev.mnascimentos.aureole.core.data.model.ReleaseNoteVersion
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private val sampleVersion = ReleaseNoteVersion(
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

@Composable
private fun ReleaseNotesCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            ReleaseNotesBottomSheet(
                onDismissRequest = {},
                allVersions = listOf(
                    sampleVersion,
                    sampleVersion.copy(versionName = "0.9.12", isCurrent = false),
                    sampleVersion.copy(versionName = "0.9.11", isCurrent = false)
                )
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ReleaseNotesRightHandedScreenshotTest() = ReleaseNotesCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ReleaseNotesLeftHandedScreenshotTest() = ReleaseNotesCase(isLeftHanded = true)
