package dev.mnascimentos.aureole.feature.home.components.containers.model

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.graphics.Color
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel

internal data class ContainerColumnConfig(
    val backgroundColor: Color,
    val verticalArrangement: Arrangement.Vertical,
    val onFolderClick: (AppFolder, Float) -> Unit,
    val shouldExpand: Boolean = false
)

internal data class ContainerRowConfig(
    val backgroundColor: Color,
    val horizontalArrangement: Arrangement.Horizontal,
    val onFolderClick: (AppFolder, Float) -> Unit,
    val shouldExpand: Boolean = false
)

internal sealed class ContainerRenderItem {
    data class Folder(val folder: AppFolder) : ContainerRenderItem()
    data class App(val appInfo: AppInfo) : ContainerRenderItem()
    data class QuickButton(val quickButtonModel: QuickButtonModel) : ContainerRenderItem()
}
