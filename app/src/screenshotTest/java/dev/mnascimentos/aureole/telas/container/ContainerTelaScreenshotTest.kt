/**
 * Personalizar grupo.
 *
 * @en Customize group
 * @area home
 * @paisagem
 * @excecao previews-same-folder D-02
 * @grupo Grupos e pastas / Groups and folders
 */
package dev.mnascimentos.aureole.telas.container

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.dialogs.FolderFormSheet
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormParams
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.EditContainerScreen
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderAppPickerDialogBody
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderPlacementLayout
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderPopup
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerCallbacks
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.sortedForPickerGrid
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupColorTarget
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupSheetPage
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTab
import dev.mnascimentos.aureole.feature.home.components.containers.model.GroupTip
import dev.mnascimentos.aureole.feature.home.components.content.ScrollViewContainerContent
import dev.mnascimentos.aureole.feature.home.components.content.model.ScrollViewContainerParams
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesListConfig
import dev.mnascimentos.aureole.feature.home.components.favorites.model.FavoritesPreviewMetrics
import dev.mnascimentos.aureole.feature.home.components.widgets.model.StackedWidgetConfig
import dev.mnascimentos.aureole.feature.home.grid.AddContainerDialog
import dev.mnascimentos.aureole.feature.home.grid.EditGridItemDialog
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.HomeScreenCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData
import dev.mnascimentos.aureole.fixtures.sampleApp

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

/**
 * Itens.
 *
 * Aba Itens: tiles Adicionar, a lista "No grupo" com apps e uma pasta.
 *
 * @en Items
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = GroupSheetCase()

/**
 * Layout.
 *
 * @en Layout
 */
@PreviewTest
@AureolePreview
@Composable
private fun LayoutScreenshotTest() = GroupSheetCase(tab = GroupTab.LAYOUT, homeSize = DpSize(HOME_WIDTH.dp, HOME_HEIGHT.dp))

/**
 * Aparência.
 *
 * @en Appearance
 */
@PreviewTest
@AureolePreview
@Composable
private fun AparenciaScreenshotTest() = GroupSheetCase(tab = GroupTab.APPEARANCE)

/**
 * Página de cor.
 *
 * A página de cor do fundo dentro da sheet: voltar, título e sem barra de abas.
 *
 * @en Color page
 */
@PreviewTest
@AureolePreview
@Composable
private fun PaginaCorScreenshotTest() =
    GroupSheetCase(tab = GroupTab.APPEARANCE, page = GroupSheetPage.Color(GroupColorTarget.BACKGROUND))

/**
 * Adicionar componente.
 *
 * "Adicionar à home": os tipos de componente que a grade aceita.
 *
 * @en Add component
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun AdicionarScreenshotTest() = HomeCase(MainUiState()) {
    AddContainerDialog(onDismissRequest = {}, onSelectType = {})
}

/**
 * Adicionar componente, dentro do grupo.
 *
 * Adicionar dentro de um grupo rolável: só os componentes que cabem num grupo.
 *
 * @en Add component, inside a group
 */
@PreviewTest
@AureolePreview
@Composable
private fun ComponentesScreenshotTest() = HomeCase(MainUiState()) {
    AddContainerDialog(onDismissRequest = {}, onSelectType = {}, isNested = true)
}

private const val LIST_HEIGHT_PX = 400f
private const val GROUP_COLS = 16
private const val GROUP_ROWS = 12

private fun group(children: List<LauncherItemState>) = LauncherItemState(
    id = "group",
    type = LauncherItemType.SCROLL_VIEW,
    col = 0,
    row = 0,
    colSpan = GROUP_COLS,
    rowSpan = GROUP_ROWS,
    scrollOrientation = ScrollOrientation.VERTICAL,
    children = children
)

/**
 * Grupo rolável.
 *
 * Sheet do grupo: relógio com "Altura" e "- Nx +", lista de apps com "Lista inteira".
 *
 * @en Scroll group
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun RolavelScreenshotTest() = HomeCase(createSampleHomeData().uiState) {
    EditGridItemDialog(
        item = group(
            listOf(
                LauncherItemState(id = "clock", type = LauncherItemType.CLOCK, col = 0, row = 0, colSpan = 2, rowSpan = 2),
                LauncherItemState(id = "apps", type = LauncherItemType.APPS_LIST, col = 0, row = 0, colSpan = 2, rowSpan = 6)
            )
        ),
        onDismissRequest = {},
        onDeleteConfirm = {}
    )
}

/**
 * Grupo rolável na home.
 *
 * Grupo na home só com a lista de apps (todos os apps): a lista aparece inteira e o grupo é que rola.
 *
 * @en Scroll group on the home
 */
@PreviewTest
@AureolePreview
@Composable
private fun RolavelNaHomeScreenshotTest() = HomeCase(createSampleHomeData().uiState) {
    val apps = LauncherItemState(
        id = "apps",
        type = LauncherItemType.APPS_LIST,
        col = 0,
        row = 0,
        colSpan = 2,
        rowSpan = 2,
        showAllApps = true
    )
    ScrollViewContainerContent(
        ScrollViewContainerParams(
            item = group(listOf(apps)),
            favConfig = FavoritesListConfig(
                currentHeightDp = FavoritesPreviewMetrics.DefaultHeight,
                currentHeightPx = LIST_HEIGHT_PX,
                state = rememberLazyListState()
            ),
            appWidgetHost = null,
            stackedWidgetConfig = StackedWidgetConfig(
                topWidgetIds = emptyList(),
                currentHeightDp = FavoritesPreviewMetrics.DefaultHeight,
                currentHeightPx = LIST_HEIGHT_PX
            ),
            hazeState = null
        )
    )
}

private const val FOLDER_TOP_PX = 250f
private const val LAST_ITEM_APPS = 6
private val PHONE_HEIGHT = 640.dp
private val ANCHOR_FROM_BOTTOM = 80.dp
private val POPUP_END_PADDING = 88.dp

/**
 * Pasta aberta.
 *
 * Pasta aberta em popup sobre a home.
 *
 * @en Open folder
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PastaScreenshotTest() {
    val sampleData = createSampleHomeData()
    val folder = sampleData.folders.first().copy(displayAsGrid = false)
    HomeScreenCase(
        sampleData.uiState.copy(openedFolderId = folder.id, activeFolder = folder, activeFolderTopYPx = FOLDER_TOP_PX)
    )
}

/**
 * Pasta aberta, do último item.
 *
 * Pasta aberta do último item do container, no pé da tela: sobe até ficar 16 dp acima da barra de navegação
 * (prévias não têm barras: 16 dp acima do fim).
 *
 * @en Open folder, from the last item
 */
@PreviewTest
@AureolePreview
@Composable
private fun PastaUltimoItemScreenshotTest() {
    val apps = (1..LAST_ITEM_APPS).map { index -> sampleApp("App $index", "com.example.app$index", 0xFF1B65C0) }
    val folder = AppFolder(id = "f1", name = "Work", appPackageNames = apps.map { it.packageName })
    HomeCase(MainUiState()) {
        Box(modifier = Modifier.height(PHONE_HEIGHT).fillMaxWidth()) {
            OpenedFolderPlacementLayout(
                anchorTopPx = with(LocalDensity.current) { (PHONE_HEIGHT - ANCHOR_FROM_BOTTOM).toPx() },
                isGridFolder = false,
                isLeftHanded = false
            ) {
                OpenedFolderPopup(
                    config = OpenedFolderPopupConfig(
                        folder = folder,
                        allApps = apps,
                        actions = FolderPopupActions(onDismiss = {}, onAppClick = {})
                    ),
                    modifier = Modifier.padding(end = POPUP_END_PADDING)
                )
            }
        }
    }
}

private val SAMPLE_FOLDER = AppFolder(
    id = "1",
    name = "Bank",
    icon = "account_balance",
    color = ThemeColorRef.Role(ThemeColorRole.OUTLINE)
)

@Composable
private fun FolderFormCase(folder: AppFolder?) = HomeCase(MainUiState()) {
    FolderFormSheet(
        params = FolderFormParams(folder = folder, onDelete = if (folder != null) ({}) else null),
        onDismiss = {},
        onSave = {}
    )
}

/**
 * Formulário de pasta, criar.
 *
 * Pasta nova: nome vazio sob o tile, segmento Ícone com as sugestões, "Criar e escolher apps".
 *
 * @en Folder form, create
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PastaCriarScreenshotTest() = FolderFormCase(folder = null)

/**
 * Formulário de pasta, editar.
 *
 * Editar pasta: cor própria, ícone salvo marcado, "Excluir pasta" no fim, sem botões.
 *
 * @en Folder form, edit
 */
@PreviewTest
@AureolePreview
@Composable
private fun PastaEditarScreenshotTest() = FolderFormCase(folder = SAMPLE_FOLDER)

private val GRID_COLORS = listOf(0xFF2E7D32, 0xFF1B65C0, 0xFFC62828, 0xFF6A1B9A, 0xFFEF6C00, 0xFF00838F)

private val GRID_APPS: List<AppInfo> = listOf(
    "Agenda", "Banco", "Bloco", "Câmera", "Calendário", "Chat", "Drive", "E-mail", "Fotos", "Galeria",
    "Jogos", "Mapas", "Música", "Notas", "Podcasts", "Relógio", "Spotify", "Tempo", "Uber", "Vídeos"
).mapIndexed { index, label -> sampleApp(label, "pkg.$index", GRID_COLORS[index % GRID_COLORS.size]) }

private val MARKED_LABELS = listOf("Câmera", "Fotos", "Galeria")

/** Escolher apps da pasta: grade com três apps marcados e a bandeja dos escolhidos. */
@Composable
private fun PickerGridCase(isLeftHanded: Boolean) {
    val selectedApps = GRID_APPS.filter { it.label in MARKED_LABELS }
    val selected = remember { mutableStateListOf<String>().apply { addAll(selectedApps.map { it.packageName }) } }
    HomeCase(MainUiState(isLeftHandedMode = isLeftHanded)) {
        FolderAppPickerDialogBody(
            params = FolderAppPickerBodyParams(
                folderName = "Media",
                searchQuery = "",
                gridApps = sortedForPickerGrid(GRID_APPS) { it.label },
                selectedPackages = selected,
                trayApps = selectedApps,
                isLeftHandedMode = isLeftHanded
            ),
            onSearchQueryChange = {},
            onSearch = {},
            callbacks = FolderAppPickerCallbacks(onDismiss = {}, onSave = {})
        )
    }
}

/**
 * Apps da pasta.
 *
 * @en Folder apps
 */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PastaAppsScreenshotTest() = PickerGridCase(isLeftHanded = false)

/**
 * Apps da pasta, busca.
 *
 * Buscando "cam": resultados em cima da grade, a bandeja continua.
 *
 * @en Folder apps, search
 */
@PreviewTest
@AureolePreview
@Composable
private fun PastaAppsBuscaScreenshotTest() {
    val camcorder = sampleApp("Camcorder", "com.camcorder", 0xFF2E7D32)
    val camera = sampleApp("Camera Pro", "com.pro", 0xFF1B65C0)
    val gallery = sampleApp("Gallery", "com.gallery", 0xFFC62828)
    val selected = remember { mutableStateListOf(camera.packageName, gallery.packageName) }
    HomeCase(MainUiState()) {
        FolderAppPickerDialogBody(
            params = FolderAppPickerBodyParams(
                folderName = "Media",
                searchQuery = "cam",
                gridApps = listOf(camcorder),
                selectedPackages = selected,
                isSearching = true,
                searchResults = listOf(camcorder, camera),
                trayApps = listOf(camera, gallery),
                isLeftHandedMode = false
            ),
            onSearchQueryChange = {},
            onSearch = {},
            callbacks = FolderAppPickerCallbacks(onDismiss = {}, onSave = {})
        )
    }
}

/**
 * Apps da pasta, canhoto.
 *
 * @en Folder apps, left-handed
 */
@PreviewTest
@AureolePreview
@Composable
private fun PastaAppsCanhotoScreenshotTest() = PickerGridCase(isLeftHanded = true)

private const val NEW_PANEL_ID = "panel_new"

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
        id = NEW_PANEL_ID,
        appPackageNames = if (withApp) listOf(first) else emptyList(),
        items = if (withApp) {
            listOf(
                ContainerItemEntity(id = "i0", panelId = NEW_PANEL_ID, itemType = ContainerItemType.APP, packageName = first)
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

/**
 * Novo grupo · Adicionar componente.
 *
 * "Adicionar componente" com a pílula "Comece aqui" no grupo (primeira vez).
 *
 * @en New group · Add component
 */
@PreviewTest
@AureolePreview
@Composable
private fun NovoAdicionarComponenteScreenshotTest() = HomeCase(MainUiState()) {
    AddContainerDialog(onDismissRequest = {}, onSelectType = {}, showsGroupStartHere = true)
}

/**
 * Novo grupo · Grupo vazio, dica 1.
 *
 * O grupo novo, vazio, com a dica 1 ("Adicionar app").
 *
 * @en New group · Empty group, tip 1
 */
@PreviewTest
@AureolePreview
@Composable
private fun NovoGrupoVazioDica1ScreenshotTest() = NewGroupCase(tip = GroupTip.FIRST_APP)

/**
 * Novo grupo · Adicionar apps.
 *
 * A página "Adicionar apps".
 *
 * @en New group · Add apps
 */
@PreviewTest
@AureolePreview
@Composable
private fun NovoAdicionarAppsScreenshotTest() = NewGroupCase(tip = GroupTip.FIRST_APP, page = GroupSheetPage.AddApps)

/**
 * Novo grupo · Itens, dica 2.
 *
 * De volta na aba Itens, com um app e a dica 2 ("Escolher posição").
 *
 * @en New group · Items, tip 2
 */
@PreviewTest
@AureolePreview
@Composable
private fun NovoItensDica2ScreenshotTest() = NewGroupCase(tip = GroupTip.CHOOSE_POSITION, withApp = true)

/**
 * Novo grupo · Layout, dica 3.
 *
 * A aba Layout com a dica 3 ("Em pé ou deitado?").
 *
 * @en New group · Layout, tip 3
 */
@PreviewTest
@AureolePreview
@Composable
private fun NovoLayoutDica3ScreenshotTest() =
    NewGroupCase(tip = GroupTip.PICK_POSITION, tab = GroupTab.LAYOUT, withApp = true)

/**
 * Novo grupo · Nova pasta.
 *
 * A página "Nova pasta".
 *
 * @en New group · New folder
 */
@PreviewTest
@AureolePreview
@Composable
private fun NovoNovaPastaScreenshotTest() = NewGroupCase(tip = null, page = GroupSheetPage.NewFolder, withApp = true)
