package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.createSampleHomeData
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState

@Composable
private fun EditContainerCase(isLeftHanded: Boolean) {
    val sampleData = createSampleHomeData()
    val panel = ContainerModel(
        id = "panel_1",
        orientation = "Vertical",
        isBackgroundEnabled = true,
        appPackageNames = sampleData.apps.take(4).map { it.packageName },
        items = sampleData.apps.take(4).mapIndexed { index, app ->
            ContainerItemEntity(
                id = "item_$index",
                panelId = "panel_1",
                itemType = ContainerItemType.APP,
                packageName = app.packageName,
                orderIndex = index
            )
        }
    )
    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides sampleData.uiState.copy(isLeftHandedMode = isLeftHanded),
            LocalLeftHanded provides (sampleData.uiState.copy(isLeftHandedMode = isLeftHanded)).isLeftHandedMode,
            LocalHomeActions provides sampleData.actions
        ) {
            EditContainerScreen(
                params = EditContainerScreenParams(
                    panel = panel,
                    allApps = sampleData.apps,
                    onDismiss = {},
                    onSave = {}
                )
            )
        }
    }
}

/** Container editor main screen: live preview label, OPTIONS rows and Cancel/Save on the thumb side. */
@PreviewTest
@AureolePreview
@Composable
private fun EditContainerMainRightHandedScreenshotTest() = EditContainerCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerMainLeftHandedScreenshotTest() = EditContainerCase(isLeftHanded = true)

@Composable
private fun MainContentCase(isLeftHanded: Boolean) {
    ProvideHomeUiState(MainUiState(isLeftHandedMode = isLeftHanded)) {
        AureoleLauncherTheme {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AureoleDS.colors.surface)
                    .padding(AureoleDS.dimens.medium)
            ) {
                MainSettingsContent(
                    componentCount = 4,
                    onAppearanceClick = {},
                    onBehaviorClick = {},
                    onManageItemsClick = {}
                )
            }
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerOptionsRightHandedScreenshotTest() = MainContentCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun EditContainerOptionsLeftHandedScreenshotTest() = MainContentCase(isLeftHanded = true)
