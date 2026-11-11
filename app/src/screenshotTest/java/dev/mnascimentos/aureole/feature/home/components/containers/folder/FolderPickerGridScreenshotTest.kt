package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.sortedForPickerGrid
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private val gridColors = listOf(0xFF2E7D32, 0xFF1B65C0, 0xFFC62828, 0xFF6A1B9A, 0xFFEF6C00, 0xFF00838F)

private val gridApps: List<AppInfo> = listOf(
    "Agenda", "Banco", "Bloco", "Câmera", "Calendário", "Chat", "Drive", "E-mail", "Fotos", "Galeria",
    "Jogos", "Mapas", "Música", "Notas", "Podcasts", "Relógio", "Spotify", "Tempo", "Uber", "Vídeos"
).mapIndexed { index, label ->
    AppInfo(
        label = label,
        packageName = "pkg.$index",
        componentName = ComponentName("pkg.$index", "Main"),
        icon = gridColors[index % gridColors.size].toInt().toDrawable()
    )
}

@Composable
private fun PickerGridCase(isLeftHanded: Boolean, selectedLabels: List<String> = emptyList()) {
    val selectedApps = gridApps.filter { it.label in selectedLabels }
    val selected = remember { mutableStateListOf<String>().apply { addAll(selectedApps.map { it.packageName }) } }
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(720.dp)
                    .background(AureoleDS.colors.surface)
                    .padding(horizontal = 20.dp)
            ) {
                FolderAppPickerDialogBody(
                    params = FolderAppPickerBodyParams(
                        folderName = "Media",
                        searchQuery = "",
                        gridApps = sortedForPickerGrid(gridApps) { it.label },
                        selectedPackages = selected,
                        trayApps = selectedApps,
                        isLeftHandedMode = isLeftHanded
                    ),
                    onSearchQueryChange = {},
                    onSearch = {},
                    onDismiss = {},
                    onSave = {}
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerGridRightHandedScreenshotTest() = PickerGridCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerGridLeftHandedScreenshotTest() = PickerGridCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerGridWithMarkedAppsScreenshotTest() =
    PickerGridCase(isLeftHanded = false, selectedLabels = listOf("Câmera", "Fotos", "Galeria"))

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerGridWithMarkedAppsLeftHandedScreenshotTest() =
    PickerGridCase(isLeftHanded = true, selectedLabels = listOf("Câmera", "Fotos", "Galeria"))

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerGridCellStatesScreenshotTest() {
    AureoleLauncherTheme {
        Row(modifier = Modifier.background(AureoleDS.colors.surface)) {
            listOf(false to false, true to false, true to true, false to true).forEachIndexed { index, (isMarked, isSelecting) ->
                FolderAppGridCell(
                    app = gridApps[index],
                    isMarked = isMarked,
                    isSelecting = isSelecting,
                    onToggle = {},
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}
