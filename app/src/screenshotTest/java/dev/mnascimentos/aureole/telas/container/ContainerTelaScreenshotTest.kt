package dev.mnascimentos.aureole.telas.container

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupColorTarget
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

private const val PANEL_APPS = 3
private const val PANEL_ID = "panel_1"
private const val HOME_WIDTH = 84
private const val HOME_HEIGHT = 330

/** Grupo com 3 apps e uma pasta, o conteúdo de todo estado desta tela. */
private fun sampleGroup(apps: List<String>): ContainerModel {
    val folder = AppFolder(id = "folder_1", panelId = PANEL_ID, name = "Trabalho", appPackageNames = apps.take(2))
    val items = apps.mapIndexed { index, pkg ->
        ContainerItemEntity(
            id = "item_$index",
            panelId = PANEL_ID,
            itemType = ContainerItemType.APP,
            packageName = pkg,
            orderIndex = index
        )
    } + ContainerItemEntity(
        id = "item_folder",
        panelId = PANEL_ID,
        itemType = ContainerItemType.FOLDER,
        folderId = folder.id,
        orderIndex = apps.size
    )
    return ContainerModel(
        id = PANEL_ID,
        orientation = "Vertical",
        isBackgroundEnabled = true,
        appPackageNames = apps,
        folders = listOf(folder),
        items = items
    )
}

/** Sheet "Personalizar grupo" na aba [tab] ou na página [page], com o grupo de exemplo. */
@Composable
private fun GroupSheetCase(
    tab: GroupTab = GroupTab.ITEMS,
    page: GroupSheetPage = GroupSheetPage.Tabs,
    homeSize: DpSize? = null
) {
    val sampleData = createSampleHomeData()
    val apps = sampleData.apps.take(PANEL_APPS)
    HomeCase(sampleData.uiState) {
        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = sampleGroup(apps.map { it.packageName }),
                allApps = sampleData.apps,
                onDismiss = {},
                onSave = {},
                initialTab = tab,
                initialPage = page,
                homeSize = homeSize
            )
        )
    }
}

/** Aba Itens: tiles Adicionar, a lista "No grupo" com apps e uma pasta. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = GroupSheetCase()

@PreviewTest
@AureolePreview
@Composable
private fun LayoutScreenshotTest() = GroupSheetCase(tab = GroupTab.LAYOUT, homeSize = DpSize(HOME_WIDTH.dp, HOME_HEIGHT.dp))

@PreviewTest
@AureolePreview
@Composable
private fun AparenciaScreenshotTest() = GroupSheetCase(tab = GroupTab.APPEARANCE)

/** A página de cor do fundo dentro da sheet: voltar, título e sem barra de abas. */
@PreviewTest
@AureolePreview
@Composable
private fun PaginaCorScreenshotTest() =
    GroupSheetCase(tab = GroupTab.APPEARANCE, page = GroupSheetPage.Color(GroupColorTarget.BACKGROUND))
