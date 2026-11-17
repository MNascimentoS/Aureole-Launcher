package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The section label sits on the same side as the rows below it: left for right-handed, right for left-handed. */
@RunWith(RobolectricTestRunner::class)
class SheetSectionLabelTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    /** Whether the first letter of "SYSTEM" starts in the right half of the label. */
    private fun startsOnTheRight(isLeftHanded: Boolean): Boolean {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalLeftHanded provides isLeftHanded) {
                    Box(modifier = Modifier.fillMaxWidth()) { SheetSectionLabel(text = "System") }
                }
            }
        }
        val node = composeTestRule.onNodeWithText("SYSTEM").fetchSemanticsNode()
        val results = mutableListOf<TextLayoutResult>()
        node.config.getOrNull(SemanticsActions.GetTextLayoutResult)?.action?.invoke(results)
        val layout = results.first()
        return layout.getBoundingBox(0).left > layout.size.width / 2f
    }

    @Test
    fun `right-handed label starts on the left`() {
        assertTrue(!startsOnTheRight(isLeftHanded = false))
    }

    @Test
    fun `left-handed label sits on the right`() {
        assertTrue(startsOnTheRight(isLeftHanded = true))
    }
}
