package dev.mnascimentos.aureole.feature.home

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.composable.actions.AureoleActionRow
import dev.mnascimentos.aureole.composable.actions.model.ActionRowParams
import dev.mnascimentos.aureole.composable.menu.AureoleMenuListSheet
import dev.mnascimentos.aureole.composable.menu.model.MenuEntry
import dev.mnascimentos.aureole.composable.menu.model.MenuRowParams
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.designsystem.components.AureoleFloatingSearchBar
import dev.mnascimentos.aureole.core.designsystem.components.AureoleSearchBarState
import dev.mnascimentos.aureole.core.designsystem.model.GlassLevel
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleThemeConfig
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.components.containers.folder.ContainerFolderItem
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderConfig
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.ContainerFolderItemParams
import dev.mnascimentos.aureole.feature.home.components.overlays.CustomizeBottomSheet
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.settings.components.CardsGlassBottomSheet
import dev.mnascimentos.aureole.feature.settings.model.BlurOpacity
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassDraft
import dev.mnascimentos.aureole.feature.settings.model.CardsGlassParams

private const val STRAIGHT_RADIUS_DP = 0

/** Same screens as the default-radius tests, with the corner radius set to 0: everything must be straight. */
@Composable
private fun StraightTheme(content: @Composable () -> Unit) {
    val config = AureoleThemeConfig(
        darkTheme = isSystemInDarkTheme(),
        aureoleDimens = AureoleDimens.forCornerRadius(STRAIGHT_RADIUS_DP)
    )
    AureoleLauncherTheme(config, content)
}

@PreviewTest
@AureolePreview
@Composable
private fun ActionRowRadiusZeroScreenshotTest() = StraightTheme {
    AureoleActionRow(
        params = ActionRowParams("Save", {}, "Restore", {}),
        isLeftHanded = false,
        modifier = Modifier.padding(AureoleDS.dimens.medium)
    )
}

@PreviewTest
@AureolePreview
@Composable
private fun FloatingSearchBarRadiusZeroScreenshotTest() = StraightTheme {
    Column {
        AureoleFloatingSearchBar(
            state = AureoleSearchBarState(query = "", onQueryChange = {}, isExpanded = false, onExpandedChange = {})
        )
        AureoleFloatingSearchBar(
            state = AureoleSearchBarState(query = "Search", onQueryChange = {}, isExpanded = true, onExpandedChange = {})
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun MenuSheetRadiusZeroScreenshotTest() = StraightTheme {
    ProvideHomeUiState(createSampleHomeData().uiState) {
        AureoleMenuListSheet(title = "Camera", onDismiss = {}) {
            listOf(
                MenuEntry.Row(MenuRowParams(title = "App info", onClick = {}, showChevron = true)),
                MenuEntry.Row(MenuRowParams(title = "Remove", onClick = {}, isDestructive = true))
            )
        }
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun CustomizeSheetRadiusZeroScreenshotTest() = StraightTheme {
    val sampleData = createSampleHomeData()
    ProvideHomeUiState(sampleData.uiState) {
        CustomizeBottomSheet(uiState = sampleData.uiState, actions = sampleData.actions, onDismissRequest = {})
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun CardsGlassSheetRadiusZeroScreenshotTest() = StraightTheme {
    ProvideHomeUiState(createSampleHomeData().uiState) {
        CardsGlassBottomSheet(
            params = CardsGlassParams(
                initial = CardsGlassDraft(STRAIGHT_RADIUS_DP, GlassLevel.MEDIUM, BlurOpacity.MEDIUM.value),
                onChange = {},
                onDismissRequest = {},
                isLeftHanded = false
            )
        )
    }
}

@PreviewTest
@AureolePreview
@Composable
private fun ContainersHomeRadiusZeroScreenshotTest() =
    FourContainersHome(dimens = AureoleDimens.forCornerRadius(STRAIGHT_RADIUS_DP))

@PreviewTest
@AureolePreview
@Composable
private fun ContainerFolderItemRadiusZeroScreenshotTest() = StraightTheme {
    val sampleData = createSampleHomeData()
    CompositionLocalProvider(
        LocalHomeUiState provides sampleData.uiState,
        LocalLeftHanded provides sampleData.uiState.isLeftHandedMode,
        LocalHomeActions provides sampleData.actions
    ) {
        ContainerFolderItem(
            params = ContainerFolderItemParams(
                folder = AppFolder(id = "f1", name = "Media", icon = "star"),
                isOpened = false,
                config = ContainerFolderConfig(
                    showFolderLabels = true,
                    isGridFolderEnabled = false,
                    isBackgroundEnabled = true
                ),
                onFolderClick = { _, _ -> }
            )
        )
    }
}
