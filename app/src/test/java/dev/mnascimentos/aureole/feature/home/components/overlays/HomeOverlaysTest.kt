package dev.mnascimentos.aureole.feature.home.components.overlays

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.unit.Density
import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.AppsDrawerOverlayConfig
import dev.mnascimentos.aureole.feature.home.model.HomeDragParams
import dev.mnascimentos.aureole.feature.home.model.HomeOverlaysConfig
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ScrubberOverlayConfig
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestScope
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class HomeOverlaysTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testScope = TestScope()

    @Test
    fun `when home overlays content is rendered and scrubber is enabled, should display scrubber letters`() {
        val uiState = MainUiState(
            isAllAppsDrawerOpen = false,
            isAlphabetScrubberDisabled = false,
            alphabet = listOf('A', 'B', 'C')
        )

        val config = HomeOverlaysConfig(
            uiState = uiState,
            actions = createDefaultHomeScreenActions(),
            listState = LazyListState(),
            hazeState = HazeState(),
            externalTouchY = -1f,
            coroutineScope = testScope,
            screenHeightPx = 1000f,
            onExternalTouchYReset = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HomeOverlaysContent(config = config)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("A").assertIsDisplayed()
        composeTestRule.onNodeWithText("B").assertIsDisplayed()
    }

    @Test
    fun `when scrubber is disabled, home overlays content should not render scrubber letters`() {
        val uiState = MainUiState(
            isAllAppsDrawerOpen = false,
            isAlphabetScrubberDisabled = true,
            alphabet = listOf('A', 'B', 'C')
        )

        val config = HomeOverlaysConfig(
            uiState = uiState,
            actions = createDefaultHomeScreenActions(),
            listState = LazyListState(),
            hazeState = HazeState(),
            externalTouchY = -1f,
            coroutineScope = testScope,
            screenHeightPx = 1000f,
            onExternalTouchYReset = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        HomeOverlaysContent(config = config)
                    }
                }
            }
        }

        composeTestRule.onNodeWithText("A").assertDoesNotExist()
    }

    @Test
    fun `when apps drawer overlay is open and tapped, should invoke onClose`() {
        var closed = false

        val config = AppsDrawerOverlayConfig(
            isAllAppsDrawerOpen = true,
            isOpenedFromBottom = false,
            isLeftHandedMode = false,
            listState = LazyListState(),
            hazeState = HazeState(),
            onClose = { closed = true }
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppsDrawerOverlay(
                    config = config,
                    modifier = Modifier.testTag("drawer_overlay")
                )
            }
        }

        composeTestRule.onNodeWithTag("drawer_overlay").performClick()

        assertTrue(closed)
    }

    @Test
    fun `when apps drawer overlay is open from bottom and left handed, should render drawer`() {
        val config = AppsDrawerOverlayConfig(
            isAllAppsDrawerOpen = true,
            isOpenedFromBottom = true,
            isLeftHandedMode = true,
            listState = LazyListState(),
            hazeState = HazeState(),
            onClose = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AppsDrawerOverlay(
                    config = config,
                    modifier = Modifier.testTag("drawer_overlay")
                )
            }
        }

        composeTestRule.onNodeWithTag("drawer_overlay").assertIsDisplayed()
    }

    @Test
    fun `when scrubber overlay is rendered with left handed mode, should render alphabet letters`() {
        val uiState = MainUiState(
            isAllAppsDrawerOpen = false,
            isAlphabetScrubberDisabled = false,
            isLeftHandedMode = true,
            alphabet = listOf('X', 'Y', 'Z')
        )

        val config = ScrubberOverlayConfig(
            uiState = uiState,
            externalTouchY = -1f,
            listState = LazyListState(),
            coroutineScope = testScope,
            actions = createDefaultHomeScreenActions(),
            onExternalTouchYReset = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    ScrubberOverlay(config = config)
                }
            }
        }

        composeTestRule.onNodeWithText("X").assertIsDisplayed()
    }

    @Test
    fun `when vertical drag down occurs, should trigger onExpandNotificationShade`() {
        var notificationShadeExpanded = false

        val density = Density(density = 1f)
        val params = HomeDragParams(
            isLeftHandedMode = false,
            isAlphabetScrubberDisabled = false,
            screenHeightPx = 1000f,
            screenWidthPx = 500f,
            isAllAppsDrawerOpen = false,
            density = density,
            onAllAppsDrawerOpen = {},
            onExpandNotificationShade = { notificationShadeExpanded = true },
            onExternalTouchYChange = {}
        )

        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("drag_box")
                    .homeDragGestures(params)
            )
        }

        composeTestRule.onNodeWithTag("drag_box").performTouchInput {
            down(Offset(250f, 200f))
            moveBy(Offset(0f, 40f))
            moveBy(Offset(0f, 40f))
            moveBy(Offset(0f, 40f))
            up()
        }

        assertTrue(notificationShadeExpanded)
    }

    @Test
    fun `when vertical drag up occurs, should trigger onAllAppsDrawerOpen`() {
        var drawerOpened = false

        val density = Density(density = 1f)
        val params = HomeDragParams(
            isLeftHandedMode = false,
            isAlphabetScrubberDisabled = false,
            screenHeightPx = 1000f,
            screenWidthPx = 500f,
            isAllAppsDrawerOpen = false,
            density = density,
            onAllAppsDrawerOpen = { drawerOpened = true },
            onExpandNotificationShade = {},
            onExternalTouchYChange = {}
        )

        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("drag_box")
                    .homeDragGestures(params)
            )
        }

        composeTestRule.onNodeWithTag("drag_box").performTouchInput {
            down(Offset(250f, 500f))
            moveBy(Offset(0f, -40f))
            moveBy(Offset(0f, -40f))
            moveBy(Offset(0f, -40f))
            up()
        }

        assertTrue(drawerOpened)
    }

    @Test
    fun `when edge drag inward from right edge occurs, should trigger drawer open and touch Y change`() {
        var drawerOpened = false
        var lastTouchY = 0f

        composeTestRule.setContent {
            val density = LocalDensity.current
            var widthPx by remember { mutableFloatStateOf(0f) }
            var heightPx by remember { mutableFloatStateOf(0f) }

            val params = HomeDragParams(
                isLeftHandedMode = false,
                isAlphabetScrubberDisabled = false,
                screenHeightPx = heightPx,
                screenWidthPx = widthPx,
                isAllAppsDrawerOpen = false,
                density = density,
                onAllAppsDrawerOpen = { drawerOpened = true },
                onExpandNotificationShade = {},
                onExternalTouchYChange = { y -> lastTouchY = y }
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        widthPx = it.size.width.toFloat()
                        heightPx = it.size.height.toFloat()
                    }
                    .testTag("drag_box")
                    .homeDragGestures(params)
            )
        }

        composeTestRule.onNodeWithTag("drag_box").performTouchInput {
            down(Offset(width - 5f, height / 2f))
            moveBy(Offset(-20f, 0f))
            moveBy(Offset(-100f, 0f))
            up()
        }

        assertTrue(drawerOpened)
        assertEquals(-1f, lastTouchY)
    }

    @Test
    fun `when edge drag inward from left edge occurs in left handed mode, should trigger drawer open`() {
        var drawerOpened = false

        composeTestRule.setContent {
            val density = LocalDensity.current
            var widthPx by remember { mutableFloatStateOf(0f) }
            var heightPx by remember { mutableFloatStateOf(0f) }

            val params = HomeDragParams(
                isLeftHandedMode = true,
                isAlphabetScrubberDisabled = false,
                screenHeightPx = heightPx,
                screenWidthPx = widthPx,
                isAllAppsDrawerOpen = false,
                density = density,
                onAllAppsDrawerOpen = { drawerOpened = true },
                onExpandNotificationShade = {},
                onExternalTouchYChange = {}
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned {
                        widthPx = it.size.width.toFloat()
                        heightPx = it.size.height.toFloat()
                    }
                    .testTag("drag_box")
                    .homeDragGestures(params)
            )
        }

        composeTestRule.onNodeWithTag("drag_box").performTouchInput {
            down(Offset(5f, height / 2f))
            moveBy(Offset(20f, 0f))
            moveBy(Offset(100f, 0f))
            up()
        }

        assertTrue(drawerOpened)
    }

    @Test
    fun `when scrubber is disabled, edge drag should not trigger drawer open`() {
        var drawerOpened = false

        val density = Density(density = 1f)
        val params = HomeDragParams(
            isLeftHandedMode = false,
            isAlphabetScrubberDisabled = true,
            screenHeightPx = 1000f,
            screenWidthPx = 500f,
            isAllAppsDrawerOpen = false,
            density = density,
            onAllAppsDrawerOpen = { drawerOpened = true },
            onExpandNotificationShade = {},
            onExternalTouchYChange = {}
        )

        composeTestRule.setContent {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("drag_box")
                    .homeDragGestures(params)
            )
        }

        composeTestRule.onNodeWithTag("drag_box").performTouchInput {
            down(Offset(480f, 500f))
            moveBy(Offset(-20f, 0f))
            moveBy(Offset(-20f, 0f))
            cancel()
        }

        assertFalse(drawerOpened)
    }
}
