package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerBodyParams
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FolderAppPickerCallbacks

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
            callbacks = FolderAppPickerCallbacks(onDismiss = {}, onSave = {})
        )
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
