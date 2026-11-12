package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toDrawable
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.dialogs.CreateFolderDialog
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

private val manageApps = listOf("Telefone" to 0xFF2E7D32, "Câmera" to 0xFF1B65C0).mapIndexed { index, (label, color) ->
    AppInfo(
        label = label,
        packageName = "pkg.$index",
        componentName = ComponentName("pkg.$index", "Main"),
        icon = color.toInt().toDrawable()
    )
}

@Composable
private fun ManageItemsCase(isLeftHanded: Boolean) {
    val panel = ContainerModel(
        id = "p1",
        appPackageNames = manageApps.map { it.packageName },
        folders = listOf(AppFolder(id = "f1", panelId = "p1", name = "Trabalho"))
    )
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AureoleDS.colors.surface)
                    .padding(16.dp)
            ) {
                ManageItemsContent(state = rememberEditContainerState(panel), allApps = manageApps, onBack = {})
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ContainerManageItemsRightHandedScreenshotTest() = ManageItemsCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ContainerManageItemsLeftHandedScreenshotTest() = ManageItemsCase(isLeftHanded = true)

/**
 * The form opened from "Gerenciar itens": the real form lives in its own full screen window
 * (AureoleOverlayWindow), which the screenshot renderer does not draw, so the form is stacked here over
 * the manage items content exactly as the window shows it: full screen, above the sheet.
 */
@Composable
private fun ManageItemsFolderFormCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Box(modifier = Modifier.fillMaxSize().background(AureoleDS.colors.surface)) {
                Box(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    ManageItemsContent(
                        state = rememberEditContainerState(ContainerModel(id = "p1")),
                        allApps = manageApps,
                        onBack = {}
                    )
                }
                CreateFolderDialog(onDismiss = {}, onSubmit = { _, _ -> })
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ContainerManageItemsFolderFormRightHandedScreenshotTest() =
    ManageItemsFolderFormCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun ContainerManageItemsFolderFormLeftHandedScreenshotTest() =
    ManageItemsFolderFormCase(isLeftHanded = true)
