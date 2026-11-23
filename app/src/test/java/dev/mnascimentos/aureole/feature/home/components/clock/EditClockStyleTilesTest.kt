package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.clock.model.CLOCK_LAYOUT_TIME_BAR_TAG
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** Style tab: the layout tiles follow the alignment (RF-D5) and both are radio choices for TalkBack (RF-D6). */
@RunWith(RobolectricTestRunner::class)
class EditClockStyleTilesTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val isRadio = SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.RadioButton)

    @Test
    fun `when the alignment changes, should move the time bar of every tile to that side`() {
        var alignment by mutableStateOf("START")
        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditClockStyleCards(
                    selectedKey = "DATE_ON_TOP",
                    onSelect = {},
                    modifier = Modifier.width(360.dp),
                    alignment = alignment
                )
            }
        }
        val start = timeBarLefts()
        alignment = "CENTER"
        val center = timeBarLefts()
        alignment = "END"
        val end = timeBarLefts()

        assertEquals(start.size, end.size)
        start.indices.forEach { i ->
            assertTrue("tile $i", start[i] < center[i] && center[i] < end[i])
        }
    }

    @Test
    fun `when shown, should expose the layout tiles and the alignment as radio buttons`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Column {
                    EditClockAlignmentSection(
                        uiState = MainUiState(clockAlignment = "END"),
                        actions = createDefaultHomeScreenActions()
                    )
                    EditClockStyleCards(selectedKey = "DATE_ON_TOP", onSelect = {})
                }
            }
        }

        composeTestRule.onNode(hasText("Right") and isRadio).assertIsSelected()
        composeTestRule.onNodeWithText("Left").assertExists()
        // 3 alignments + 7 layouts.
        assertEquals(10, composeTestRule.onAllNodes(isRadio).fetchSemanticsNodes().size)
    }

    private fun timeBarLefts(): List<Dp> {
        composeTestRule.waitForIdle()
        val nodes = composeTestRule.onAllNodesWithTag(CLOCK_LAYOUT_TIME_BAR_TAG, useUnmergedTree = true)
        return nodes.fetchSemanticsNodes().indices.map { nodes[it].getBoundsInRoot().left }
    }
}
