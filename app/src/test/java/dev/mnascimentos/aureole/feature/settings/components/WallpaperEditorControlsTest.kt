package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextDirection
import dev.mnascimentos.aureole.core.data.model.WallpaperAdjustments
import dev.mnascimentos.aureole.core.designsystem.model.LocalLeftHanded
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.components.wallpaper.WallpaperEditorControls
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorFrame
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorHandlers
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorState
import dev.mnascimentos.aureole.feature.settings.model.WallpaperEditorTab
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** The value beside the slider keeps "8 dp" / "30%" reading left to right in the mirrored (left-handed) sheet. */
@RunWith(RobolectricTestRunner::class)
class WallpaperEditorControlsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun valueDirection(isLeftHanded: Boolean, tab: WallpaperEditorTab, text: String): TextDirection {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalLeftHanded provides isLeftHanded) {
                    WallpaperEditorControls(
                        state = WallpaperEditorState(
                            initial = WallpaperAdjustments.DEFAULT.copy(blurDp = BLUR, dim = DIM),
                            tab = tab
                        ),
                        frame = WallpaperEditorFrame(imageSize = Size.Unspecified, viewSize = Size.Unspecified),
                        handlers = WallpaperEditorHandlers(onChange = {}, onAnimateTo = {})
                    )
                }
            }
        }
        val results = mutableListOf<TextLayoutResult>()
        composeTestRule.onNodeWithText(text).fetchSemanticsNode().config[SemanticsActions.GetTextLayoutResult]
            .action?.invoke(results)
        return results.single().layoutInput.style.textDirection
    }

    @Test
    fun `left-handed blur value is forced left to right`() {
        assertEquals(TextDirection.Ltr, valueDirection(true, WallpaperEditorTab.BLUR, "8 dp"))
    }

    @Test
    fun `left-handed dim value is forced left to right`() {
        assertEquals(TextDirection.Ltr, valueDirection(true, WallpaperEditorTab.DIM, "30%"))
    }

    @Test
    fun `right-handed value keeps the same direction`() {
        assertEquals(TextDirection.Ltr, valueDirection(false, WallpaperEditorTab.BLUR, "8 dp"))
    }

    private companion object {
        const val BLUR = 8f
        const val DIM = 0.3f
    }
}
