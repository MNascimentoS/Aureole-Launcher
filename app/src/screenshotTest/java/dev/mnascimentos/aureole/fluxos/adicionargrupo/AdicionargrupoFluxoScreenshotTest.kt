package dev.mnascimentos.aureole.fluxos.adicionargrupo

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTip
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

private const val PANEL_ID = "panel_new"

/** O grupo recém-adicionado; com [withApp] já tem o primeiro app (depois de "Adicionar apps"). */
@Composable
private fun NewGroupCase(
    tip: GroupTip?,
    tab: GroupTab = GroupTab.ITEMS,
    page: GroupSheetPage = GroupSheetPage.Tabs,
    withApp: Boolean = false
) {
    val sampleData = createSampleHomeData()
    val first = sampleData.apps.first().packageName
    val panel = ContainerModel(
        id = PANEL_ID,
        appPackageNames = if (withApp) listOf(first) else emptyList(),
        items = if (withApp) {
            listOf(
                ContainerItemEntity(id = "i0", panelId = PANEL_ID, itemType = ContainerItemType.APP, packageName = first)
            )
        } else {
            emptyList()
        }
    )
    HomeCase(sampleData.uiState) {
        EditContainerScreen(
            params = EditContainerScreenParams(
                panel = panel,
                allApps = sampleData.apps,
                onDismiss = {},
                onSave = {},
                isNew = true,
                areTipsSeen = false,
                initialTab = tab,
                initialPage = page,
                startTip = tip
            )
        )
    }
}

/** "Adicionar componente" com a pílula "Comece aqui" no grupo (primeira vez). */
@PreviewTest
@AureolePreview
@Composable
private fun P01AdicionarComponenteScreenshotTest() = HomeCase(MainUiState()) {
    AddContainerDialog(onDismissRequest = {}, onSelectType = {}, showsGroupStartHere = true)
}

/** O grupo novo, vazio, com a dica 1 ("Adicionar app"). */
@PreviewTest
@AureolePreview
@Composable
private fun P02GrupoVazioDica1ScreenshotTest() = NewGroupCase(tip = GroupTip.FIRST_APP)

/** A página "Adicionar apps". */
@PreviewTest
@AureolePreview
@Composable
private fun P03AdicionarAppsScreenshotTest() = NewGroupCase(tip = GroupTip.FIRST_APP, page = GroupSheetPage.AddApps)

/** De volta na aba Itens, com um app e a dica 2 ("Escolher posição"). */
@PreviewTest
@AureolePreview
@Composable
private fun P04ItensDica2ScreenshotTest() = NewGroupCase(tip = GroupTip.CHOOSE_POSITION, withApp = true)

/** A aba Layout com a dica 3 ("Em pé ou deitado?"). */
@PreviewTest
@AureolePreview
@Composable
private fun P05LayoutDica3ScreenshotTest() =
    NewGroupCase(tip = GroupTip.PICK_POSITION, tab = GroupTab.LAYOUT, withApp = true)

/** A página "Nova pasta". */
@PreviewTest
@AureolePreview
@Composable
private fun P06NovaPastaScreenshotTest() = NewGroupCase(tip = null, page = GroupSheetPage.NewFolder, withApp = true)
