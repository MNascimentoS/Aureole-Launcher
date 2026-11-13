package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.folder.model.FOLDER_PICKER_GRID_TEST_TAG
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

private const val HOLD_MS = 600L

@RunWith(RobolectricTestRunner::class)
@Config(qualifiers = "w411dp-h891dp")
class FolderAppPickerGridTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val ticks = mutableListOf<HapticFeedbackType>()
    private val fakeHaptics = object : HapticFeedback {
        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
            ticks += hapticFeedbackType
        }
    }

    private fun app(label: String) = AppInfo(
        label = label,
        packageName = "pkg.${label.lowercase()}",
        componentName = ComponentName("pkg.${label.lowercase()}", "Main"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )

    private val firstRow = listOf("Alpha", "Bravo", "Charlie", "Delta").map(::app)

    private fun setPicker(
        apps: List<AppInfo>,
        selected: List<String> = emptyList(),
        onSave: (List<String>) -> Unit = {}
    ) {
        composeTestRule.setContent {
            CompositionLocalProvider(
                LocalInspectionMode provides true,
                LocalHapticFeedback provides fakeHaptics,
                LocalHomeUiState provides MainUiState(),
                LocalLeftHanded provides (MainUiState()).isLeftHandedMode
            ) {
                AureoleLauncherTheme {
                    FolderAppPickerDialog(
                        folder = AppFolder(id = "f", name = "Media", appPackageNames = selected),
                        allApps = apps,
                        onDismiss = {},
                        onSave = onSave
                    )
                }
            }
        }
    }

    private fun grid(): SemanticsNodeInteraction = composeTestRule.onNodeWithTag(FOLDER_PICKER_GRID_TEST_TAG)

    private fun inGrid(label: String): SemanticsNodeInteraction =
        composeTestRule.onNode(hasText(label) and hasAnyAncestor(hasTestTag(FOLDER_PICKER_GRID_TEST_TAG)))

    private fun gridBounds(): Rect = grid().fetchSemanticsNode().boundsInRoot

    /** Center of [label] relative to the grid, which is where `performTouchInput` on the grid measures. */
    private fun centerInGrid(label: String): Offset =
        inGrid(label).fetchSemanticsNode().boundsInRoot.center - gridBounds().topLeft

    private fun holdAndSlide(from: String, vararg through: String) {
        val start = centerInGrid(from)
        val targets = through.map(::centerInGrid)
        grid().performTouchInput {
            down(start)
            moveTo(start, delayMillis = HOLD_MS)
        }
        composeTestRule.waitForIdle()
        grid().performTouchInput {
            targets.forEach { moveTo(it) }
            up()
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `hold and slide marks every app reached with a tic each`() {
        var saved = emptyList<String>()
        setPicker(firstRow, onSave = { saved = it })

        holdAndSlide("Alpha", "Bravo", "Charlie")
        composeTestRule.onNodeWithText("Save 3 apps").performClick()

        assertEquals(listOf("pkg.alpha", "pkg.bravo", "pkg.charlie"), saved)
        assertEquals(3, ticks.count { it == HapticFeedbackType.SegmentTick })
    }

    @Test
    fun `hold and slide from a marked app unmarks the apps reached`() {
        var saved = listOf("x")
        setPicker(firstRow, selected = listOf("pkg.alpha", "pkg.bravo", "pkg.delta"), onSave = { saved = it })

        holdAndSlide("Alpha", "Bravo")
        composeTestRule.onNodeWithText("Save 1 app").performClick()

        assertEquals(listOf("pkg.delta"), saved)
    }

    @Test
    fun `a single tap still toggles one app and the cell is a checkbox`() {
        var saved = emptyList<String>()
        setPicker(firstRow, onSave = { saved = it })

        inGrid("Charlie")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Checkbox))
            .assertIsOff()
            .performClick()
        composeTestRule.onNodeWithText("Save 1 app").performClick()

        assertEquals(listOf("pkg.charlie"), saved)
    }

    @Test
    fun `sliding the scrubber to the last letter scrolls the grid to its first app`() {
        val apps = ('A'..'Z').flatMap { letter -> (1..3).map { app("$letter$it app") } }
        setPicker(apps)
        inGrid("Z1 app").assertDoesNotExist()

        val bounds = gridBounds()
        val scrubberX = bounds.width + (composeTestRule.density.density * SCRUBBER_CENTER_DP)
        grid().performTouchInput {
            down(Offset(scrubberX, bounds.height - BOTTOM_START_PX))
            moveTo(Offset(scrubberX, bounds.height - BOTTOM_END_PX))
            up()
        }
        composeTestRule.waitForIdle()

        inGrid("Z1 app").assertIsDisplayed()
        assertTrue(ticks.isNotEmpty())
    }

    private companion object {
        /** The scrubber sits right of the grid, in a 48 dp column after an 8 dp gap from the screen edge. */
        const val SCRUBBER_CENTER_DP = 24f
        const val BOTTOM_START_PX = 80f
        const val BOTTOM_END_PX = 12f
    }
}
