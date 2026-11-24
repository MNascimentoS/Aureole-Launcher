package dev.mnascimentos.aureole.telas.pasta

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderPlacementLayout
import dev.mnascimentos.aureole.feature.home.components.containers.folder.OpenedFolderPopup
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderPopupActions
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.OpenedFolderPopupConfig
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.HomeScreenCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData
import dev.mnascimentos.aureole.fixtures.sampleApp

private const val FOLDER_TOP_PX = 250f
private const val LAST_ITEM_APPS = 6
private val PHONE_HEIGHT = 640.dp
private val ANCHOR_FROM_BOTTOM = 80.dp
private val POPUP_END_PADDING = 88.dp

/** Pasta aberta em popup sobre a home. */
@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() {
    val sampleData = createSampleHomeData()
    val folder = sampleData.folders.first().copy(displayAsGrid = false)
    HomeScreenCase(
        sampleData.uiState.copy(openedFolderId = folder.id, activeFolder = folder, activeFolderTopYPx = FOLDER_TOP_PX)
    )
}

/**
 * Pasta aberta do último item do container, no pé da tela: sobe até ficar 16 dp acima da barra de navegação
 * (prévias não têm barras: 16 dp acima do fim).
 */
@PreviewTest
@AureolePreview
@Composable
private fun UltimoItemScreenshotTest() {
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
