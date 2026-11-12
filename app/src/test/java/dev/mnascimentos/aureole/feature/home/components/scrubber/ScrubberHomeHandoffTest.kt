package dev.mnascimentos.aureole.feature.home.components.scrubber

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.HomeScreen
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.EDGE_HOLD_MS
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberCallbacks
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberMotion
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.ScrubberOptions
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterCenterY
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.letterIndexAt
import dev.mnascimentos.aureole.feature.home.components.scrubber.model.scrubberBoundsFor
import dev.mnascimentos.aureole.feature.home.model.LocalHomeActions
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.ProvideHomeUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private val ALPHABET = ('A'..'Z').toList()
private const val SWEEP_STEP_PX = 2
private const val SWEEP_OVERSHOOT_PX = 30
private const val HOLD_MARGIN_MS = 60L
private const val EDGE_X_PX = 4f

/** The letter the home computes for a Y is the letter the drawer scrubber selects for it (RF-20). */
@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp-xxhdpi")
class ScrubberHomeHandoffTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `scrubber selects the letter the home computes for every Y, A at the top and Z at the bottom`() {
        var parentHeightPx by mutableFloatStateOf(0f)
        var externalTouchY by mutableFloatStateOf(-1f)
        var selected: Char? = null
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ProvideHomeUiState(MainUiState(isAllAppsDrawerOpen = true, alphabet = ALPHABET)) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .onGloballyPositioned { parentHeightPx = it.size.height.toFloat() }
                    ) {
                        CurvedAlphabetScrubber(
                            alphabet = ALPHABET,
                            callbacks = ScrubberCallbacks(onLetterSelected = { selected = it }),
                            options = ScrubberOptions(
                                isAlwaysVisible = true,
                                externalTouchY = externalTouchY,
                                isProminentSelection = true
                            ),
                            modifier = Modifier.align(Alignment.BottomEnd)
                        )
                    }
                }
            }
        }
        composeTestRule.waitForIdle()
        val bounds = scrubberBoundsFor(parentHeightPx, composeTestRule.density)
        val boundaries = (0..ALPHABET.size).flatMap { k ->
            val y = bounds.topPx + k * bounds.heightPx / ALPHABET.size
            listOf(y - 1f, y + 1f)
        }
        val sweepStart = bounds.topPx.toInt() - SWEEP_OVERSHOOT_PX
        val sweepEnd = bounds.bottomPx.toInt() + SWEEP_OVERSHOOT_PX
        val sweep = (sweepStart..sweepEnd step SWEEP_STEP_PX).map { it.toFloat() }

        (sweep + boundaries + listOf(bounds.topPx, bounds.bottomPx)).forEach { y ->
            val localY = bounds.toLocalY(y)
            externalTouchY = localY
            composeTestRule.waitForIdle()
            val homeIndex = letterIndexAt(localY, bounds.heightPx, ALPHABET.size)
            assertEquals("y=$y", ALPHABET[homeIndex], selected)
        }
        externalTouchY = bounds.toLocalY(bounds.topPx)
        composeTestRule.waitForIdle()
        assertEquals('A', selected)
        externalTouchY = bounds.toLocalY(bounds.bottomPx)
        composeTestRule.waitForIdle()
        assertEquals('Z', selected)
    }

    @Test
    fun `holding on the home edge opens the drawer on P, follows the finger to Z and stays open on release`() {
        var uiState by mutableStateOf(
            MainUiState(
                isLoading = false,
                alphabet = ALPHABET,
                letterIndexMap = ALPHABET.withIndex().associate { (index, char) -> char to index }
            )
        )
        val actions = createDefaultHomeScreenActions().copy(
            onAllAppsDrawerOpen = { uiState = uiState.copy(isAllAppsDrawerOpen = true) }
        )
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(
                    LocalHomeUiState provides uiState,
                    LocalLeftHanded provides uiState.isLeftHandedMode,
                    LocalHomeActions provides actions
                ) {
                    HomeScreen(appWidgetHost = null)
                }
            }
        }
        composeTestRule.waitForIdle()
        composeTestRule.mainClock.autoAdvance = false

        composeTestRule.onRoot().performTouchInput {
            val bounds = scrubberBoundsFor(height.toFloat(), composeTestRule.density)
            down(
                Offset(
                    width - EDGE_X_PX,
                    bounds.topPx + letterCenterY(ALPHABET.indexOf('P'), bounds.heightPx, ALPHABET.size)
                )
            )
        }
        composeTestRule.mainClock.advanceTimeBy(EDGE_HOLD_MS + HOLD_MARGIN_MS + ScrubberMotion.BADGE_ENTER_MS)

        assertTrue(uiState.isAllAppsDrawerOpen)
        assertEquals("strip and badge both show P", 2, textCount("P"))

        composeTestRule.onRoot().performTouchInput {
            val bounds = scrubberBoundsFor(height.toFloat(), composeTestRule.density)
            moveTo(Offset(width - EDGE_X_PX, bounds.bottomPx))
        }
        composeTestRule.mainClock.advanceTimeBy(HOLD_MARGIN_MS + ScrubberMotion.BADGE_ENTER_MS)
        assertEquals("the scrub kept going after the drawer opened", 2, textCount("Z"))
        assertEquals(1, textCount("P"))

        composeTestRule.onRoot().performTouchInput { up() }
        composeTestRule.mainClock.advanceTimeBy(HOLD_MARGIN_MS + ScrubberMotion.BADGE_EXIT_MS * 2L)

        assertTrue(uiState.isAllAppsDrawerOpen)
        assertEquals("the badge fades out on release", 1, textCount("Z"))
    }

    private fun textCount(text: String): Int =
        composeTestRule.onAllNodesWithText(text, useUnmergedTree = true).fetchSemanticsNodes().size
}
