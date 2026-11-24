package dev.mnascimentos.aureole.telas.home

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalConfiguration
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleDimens
import dev.mnascimentos.aureole.core.designsystem.utils.AureoleLandscapePreview
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.grid.GridDefaults
import dev.mnascimentos.aureole.fixtures.HomeScreenCase
import dev.mnascimentos.aureole.fixtures.createSampleHomeData

private const val CONTAINER_COLS = 9
private const val CONTAINER_ROWS = 8
private const val GROUP_ROW = 9
private const val GROUP_ROWS = 14
private const val FULL_COLS = 20
private const val STRAIGHT_RADIUS_DP = 0

/** Home padrão; na prévia de paisagem, a mesma home deitada (grade de paisagem). */
@Composable
private fun DefaultHomeCase(isLeftHanded: Boolean) {
    val current = LocalConfiguration.current
    val isLandscape = current.screenWidthDp > current.screenHeightDp
    val items = GridDefaults.getDefaultGridItems(isLandscape = isLandscape, isLeftHanded = isLeftHanded)
    val base = createSampleHomeData().uiState
    val uiState = base.copy(
        isLandscape = isLandscape,
        isLeftHandedMode = isLeftHanded,
        gridItems = items,
        landscapeGridItems = if (isLandscape) items else base.landscapeGridItems
    )
    val configuration = Configuration(current).apply {
        if (isLandscape) orientation = Configuration.ORIENTATION_LANDSCAPE
    }
    CompositionLocalProvider(LocalConfiguration provides configuration) {
        HomeScreenCase(uiState)
    }
}

private fun containerItem(id: String, col: Int) = LauncherItemState(
    id = id,
    type = LauncherItemType.SHORTCUTS_CONTAINER,
    col = col,
    row = 0,
    colSpan = CONTAINER_COLS,
    rowSpan = CONTAINER_ROWS
)

private fun containerModel(id: String, title: String, vararg packages: String) = ContainerModel(
    id = id,
    title = title,
    isBackgroundEnabled = true,
    items = packages.mapIndexed { index, pkg ->
        ContainerItemEntity(
            id = "$id-$pkg",
            panelId = id,
            itemType = ContainerItemType.APP,
            packageName = pkg,
            orderIndex = index
        )
    },
    appPackageNames = packages.toList()
)

/** Dois containers soltos e dois num grupo rolável, com raio 0: tudo reto. */
@Composable
private fun StraightContainersCase() {
    val group = LauncherItemState(
        id = "group",
        type = LauncherItemType.SCROLL_VIEW,
        col = 0,
        row = GROUP_ROW,
        colSpan = FULL_COLS,
        rowSpan = GROUP_ROWS,
        scrollOrientation = ScrollOrientation.VERTICAL,
        children = listOf(containerItem("c3", 0), containerItem("c4", 0))
    )
    val uiState = createSampleHomeData().uiState.copy(
        gridItems = listOf(containerItem("c1", 0), containerItem("c2", CONTAINER_COLS + 1), group),
        containers = mapOf(
            "c1" to containerModel("c1", "Trabalho", "com.example.phone", "com.example.messages"),
            "c2" to containerModel("c2", "Midia", "com.example.camera"),
            "c3" to containerModel("c3", "Web", "com.example.browser"),
            "c4" to containerModel("c4", "Sistema", "com.example.settings", "com.example.phone")
        )
    )
    HomeScreenCase(uiState, AureoleDimens.forCornerRadius(STRAIGHT_RADIUS_DP))
}

@PreviewTest
@AureolePreview
@AureoleLandscapePreview
@Composable
private fun PadraoScreenshotTest() = DefaultHomeCase(isLeftHanded = false)

@PreviewTest
@AureolePreview
@Composable
private fun CanhotoScreenshotTest() = DefaultHomeCase(isLeftHanded = true)

@PreviewTest
@AureolePreview
@Composable
private fun RaioZeroScreenshotTest() = StraightContainersCase()
