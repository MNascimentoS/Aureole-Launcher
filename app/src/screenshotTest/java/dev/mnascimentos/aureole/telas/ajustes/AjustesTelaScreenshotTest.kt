package dev.mnascimentos.aureole.telas.ajustes

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.settings.model.SettingsArea
import dev.mnascimentos.aureole.feature.settings.model.SettingsSheet
import dev.mnascimentos.aureole.fixtures.SettingsRootCase
import dev.mnascimentos.aureole.fixtures.SettingsSheetCase

/** Raiz dos ajustes; na prévia de paisagem, os dois painéis. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = SettingsRootCase()

/** Sheet Aparência: a prévia da home em cima e a lista embaixo. */
@PreviewTest
@AureolePreview
@Composable
private fun AparenciaScreenshotTest() = SettingsSheetCase(sheet = SettingsSheet.Area(SettingsArea.APPEARANCE))

/** Sheet Avançado (o mesmo layout de lista do Comportamento). */
@PreviewTest
@AureolePreview
@Composable
private fun AvancadoScreenshotTest() = SettingsSheetCase(sheet = SettingsSheet.Area(SettingsArea.ADVANCED))
