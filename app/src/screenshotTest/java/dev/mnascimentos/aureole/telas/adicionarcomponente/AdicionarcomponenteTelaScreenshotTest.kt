package dev.mnascimentos.aureole.telas.adicionarcomponente

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase

/** "Adicionar à home": os tipos de componente que a grade aceita. */
@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = HomeCase(MainUiState()) {
    AddContainerDialog(onDismissRequest = {}, onSelectType = {})
}

/** Adicionar dentro de um grupo rolável: só os componentes que cabem num grupo. */
@PreviewTest
@AureolePreview
@Composable
private fun ComponentesScreenshotTest() = HomeCase(MainUiState()) {
    AddContainerDialog(onDismissRequest = {}, onSelectType = {}, isNested = true)
}
