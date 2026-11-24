package dev.mnascimentos.aureole.telas.pastaapps

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.FolderAppPickerDialogBody
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerCallbacks
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.sortedForPickerGrid
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.fixtures.HomeCase
import dev.mnascimentos.aureole.fixtures.sampleApp

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

@PreviewTest
@AureolePreview
@Composable
private fun PadraoScreenshotTest() = PickerGridCase(isLeftHanded = false)

/** Buscando "cam": resultados em cima da grade, a bandeja continua. */
@PreviewTest
@AureolePreview
@Composable
private fun BuscaScreenshotTest() {
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

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = PickerGridCase(isLeftHanded = true)
