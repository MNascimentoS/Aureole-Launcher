package dev.mnascimentos.aureole.feature.home

import android.content.res.Configuration
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.grid.GridEngineUtils
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `getGridLimits should return correct limits for orientation`() {
        val landscapeLimits = getGridLimits(Configuration.ORIENTATION_LANDSCAPE)
        assertEquals(GridEngineUtils.LANDSCAPE_MAX_COLS, landscapeLimits.maxCols)
        assertEquals(GridEngineUtils.LANDSCAPE_MAX_ROWS, landscapeLimits.maxRows)

        val portraitLimits = getGridLimits(Configuration.ORIENTATION_PORTRAIT)
        assertEquals(GridEngineUtils.PORTRAIT_MAX_COLS, portraitLimits.maxCols)
        assertEquals(GridEngineUtils.PORTRAIT_MAX_ROWS, portraitLimits.maxRows)
    }

    @Test
    fun `getGridItemsForOrientation should return items matching orientation`() {
        val defaultItem = LauncherItemState(id = "default_1")
        val landscapeItem = LauncherItemState(id = "landscape_1")
        val portraitItem = LauncherItemState(id = "portrait_1")

        val uiState = MainUiState(
            gridItems = listOf(defaultItem),
            landscapeGridItems = listOf(landscapeItem),
            portraitGridItems = listOf(portraitItem)
        )

        val landscapeItems = getGridItemsForOrientation(isLandscape = true, uiState = uiState)
        assertEquals(listOf(landscapeItem), landscapeItems)

        val portraitItems = getGridItemsForOrientation(isLandscape = false, uiState = uiState)
        assertEquals(listOf(portraitItem), portraitItems)

        val emptyOrientationUiState = MainUiState(
            gridItems = listOf(defaultItem),
            landscapeGridItems = emptyList(),
            portraitGridItems = emptyList()
        )

        assertEquals(
            listOf(defaultItem),
            getGridItemsForOrientation(isLandscape = true, uiState = emptyOrientationUiState)
        )
        assertEquals(
            listOf(defaultItem),
            getGridItemsForOrientation(isLandscape = false, uiState = emptyOrientationUiState)
        )
    }

    @Test
    fun `buildContainerConfig should build config for shortcuts container item`() {
        val hazeState = HazeState()
        val item = LauncherItemState(id = "c1", type = LauncherItemType.SHORTCUTS_CONTAINER)
        val containerModel = ContainerModel(
            id = "c1",
            title = "My Container",
            showFolderLabels = true,
            isBackgroundEnabled = true
        )
        val uiState = MainUiState(
            containers = mapOf("c1" to containerModel),
            openedFolderId = "f1"
        )

        val config = buildContainerConfig(item, uiState, hazeState)

        assertEquals("c1", config.panelId)
        assertEquals("My Container", config.title)
        assertEquals("f1", config.openedFolderId)
        assertEquals(true, config.showFolderLabels)
        assertEquals(hazeState, config.hazeState)
    }

    @Test
    fun `buildContainerConfig should build fallback config for non-container item`() {
        val hazeState = HazeState()
        val item = LauncherItemState(id = "clock1", type = LauncherItemType.CLOCK)
        val uiState = MainUiState(
            containerPosition = "Center",
            showFolderLabels = false,
            isContainerBackgroundEnabled = false
        )

        val config = buildContainerConfig(item, uiState, hazeState)

        assertEquals("clock1", config.panelId)
        assertEquals("Center", config.position)
        assertEquals(false, config.showFolderLabels)
        assertEquals(false, config.isBackgroundEnabled)
        assertEquals(hazeState, config.hazeState)
    }

    @Test
    fun `when rendering MainHomeLayout, should render grid container`() {
        val item = LauncherItemState(id = "clock_1", type = LauncherItemType.CLOCK)
        val uiState = MainUiState(gridItems = listOf(item))
        val hazeState = HazeState()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalHomeActions provides createDefaultHomeScreenActions()
                ) {
                    MainHomeLayout(
                        appWidgetHost = null,
                        currentHeightPx = 200f,
                        favListState = LazyListState(),
                        hazeState = hazeState
                    )
                }
            }
        }
    }
}
