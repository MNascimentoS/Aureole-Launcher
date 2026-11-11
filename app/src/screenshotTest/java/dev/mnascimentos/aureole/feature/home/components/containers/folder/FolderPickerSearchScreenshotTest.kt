package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
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

private fun pickerApp(label: String, pkg: String, color: Int) = AppInfo(
    label = label,
    packageName = pkg,
    componentName = ComponentName(pkg, "Main"),
    icon = color.toDrawable()
)

@Composable
private fun PickerSearchCase(isLeftHanded: Boolean) {
    val camcorder = pickerApp("Camcorder", "com.camcorder", 0xFF2E7D32.toInt())
    val camera = pickerApp("Camera Pro", "com.pro", 0xFF1B65C0.toInt())
    val gallery = pickerApp("Gallery", "com.gallery", 0xFFC62828.toInt())
    val selected = remember { mutableStateListOf(camera.packageName, gallery.packageName) }
    AureoleLauncherTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(560.dp)
                .background(AureoleDS.colors.surface)
                .padding(horizontal = 20.dp)
        ) {
            FolderAppPickerDialogBody(
                params = FolderAppPickerBodyParams(
                    folderName = "Media",
                    searchQuery = "cam",
                    gridApps = listOf(camcorder),
                    selectedPackages = selected,
                    isSearching = true,
                    searchResults = listOf(camcorder, camera),
                    trayApps = listOf(camera, gallery),
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

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerSearchRightHandedScreenshotTest() = PickerSearchCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun FolderPickerSearchLeftHandedScreenshotTest() = PickerSearchCase(isLeftHanded = true)
