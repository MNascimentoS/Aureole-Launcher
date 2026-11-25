package dev.mnascimentos.aureole.telas.container

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData
import dev.mnascimentos.aureole.fixtures.sampleApp

private const val PANEL_APPS = 4

private val MANAGE_APPS = listOf(
    sampleApp("Telefone", "pkg.0", 0xFF2E7D32),
    sampleApp("Câmera", "pkg.1", 0xFF1B65C0)
)

/** Editor do container: prévia ao vivo, linhas de OPÇÕES e Cancelar/Salvar do lado do polegar. */
@Composable
private fun EditContainerCase(isLeftHanded: Boolean) {
    val sampleData = createSampleHomeData()
    val apps = sampleData.apps.take(PANEL_APPS)
    val panel = ContainerModel(
        id = "panel_1",
        orientation = "Vertical",
        isBackgroundEnabled = true,
        appPackageNames = apps.map { it.packageName },
        items = apps.mapIndexed { index, app ->
            ContainerItemEntity(
                id = "item_$index",
                panelId = "panel_1",
                itemType = ContainerItemType.APP,
                packageName = app.packageName,
                orderIndex = index
            )
        }
    )
    HomeCase(sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)) {
        EditContainerScreen(
            params = EditContainerScreenParams(panel = panel, allApps = sampleData.apps, onDismiss = {}, onSave = {})
        )
    }
}

@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = EditContainerCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = EditContainerCase(isLeftHanded = true)

/** Aba Itens do grupo com apps e uma pasta (a fase G troca os estados desta tela, RF-T2). */
@PreviewTest
@AureolePreview
@Composable
private fun ItensScreenshotTest() {
    val panel = ContainerModel(
        id = "p1",
        appPackageNames = MANAGE_APPS.map { it.packageName },
        folders = listOf(AppFolder(id = "f1", panelId = "p1", name = "Trabalho"))
    )
    HomeCase(MainUiState()) {
        EditContainerScreen(
            params = EditContainerScreenParams(panel = panel, allApps = MANAGE_APPS, onDismiss = {}, onSave = {})
        )
    }
}
