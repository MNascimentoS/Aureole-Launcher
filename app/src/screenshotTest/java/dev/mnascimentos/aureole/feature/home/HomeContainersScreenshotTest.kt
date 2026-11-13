package dev.mnascimentos.aureole.feature.home

import android.appwidget.AppWidgetHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import com.android.tools.screenshot.PreviewTest
import dev.mnascimentos.aureole.composable.LocalHomeUiState
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.data.model.ScrollOrientation
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.core.designsystem.utils.AureolePreview
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions

private const val CONTAINER_COLS = 9
private const val CONTAINER_ROWS = 8
private const val GROUP_ROW = 9
private const val GROUP_ROWS = 14
private const val FULL_COLS = 20

private fun containerItem(id: String, col: Int, row: Int, colSpan: Int, rowSpan: Int) = LauncherItemState(
    id = id,
    type = LauncherItemType.SHORTCUTS_CONTAINER,
    col = col,
    row = row,
    colSpan = colSpan,
    rowSpan = rowSpan
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

/** Two loose containers and two inside a scroll group, each with its own apps: nothing is shared. */
@PreviewTest
@AureolePreview
@Composable
private fun HomeFourContainersScreenshotTest() {
    val context = LocalContext.current
    val sample = createSampleHomeData()
    val group = LauncherItemState(
        id = "group",
        type = LauncherItemType.SCROLL_VIEW,
        col = 0,
        row = GROUP_ROW,
        colSpan = FULL_COLS,
        rowSpan = GROUP_ROWS,
        scrollOrientation = ScrollOrientation.VERTICAL,
        children = listOf(
            containerItem("c3", 0, 0, CONTAINER_COLS, CONTAINER_ROWS),
            containerItem("c4", 0, 0, CONTAINER_COLS, CONTAINER_ROWS)
        )
    )
    val uiState = sample.uiState.copy(
        gridItems = listOf(
            containerItem("c1", 0, 0, CONTAINER_COLS, CONTAINER_ROWS),
            containerItem("c2", CONTAINER_COLS + 1, 0, CONTAINER_COLS, CONTAINER_ROWS),
            group
        ),
        containers = mapOf(
            "c1" to containerModel("c1", "Trabalho", "com.example.phone", "com.example.messages"),
            "c2" to containerModel("c2", "Midia", "com.example.camera"),
            "c3" to containerModel("c3", "Web", "com.example.browser"),
            "c4" to containerModel("c4", "Sistema", "com.example.settings", "com.example.phone")
        )
    )

    AureoleLauncherTheme {
        CompositionLocalProvider(
            LocalHomeUiState provides uiState,
            LocalLeftHanded provides uiState.isLeftHandedMode,
            LocalHomeActions provides sample.actions
        ) {
            HomeScreen(appWidgetHost = AppWidgetHost(context, 1024))
        }
    }
}
