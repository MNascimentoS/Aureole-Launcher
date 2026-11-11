package dev.mnascimentos.aureole.feature.home.components.quickbutton

import android.content.ComponentName
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.ShortcutSelectionOptions
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import dev.mnascimentos.aureole.feature.home.model.createDefaultHomeScreenActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ShortcutSelectionSearchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val maps = AppInfo(
        label = "Maps",
        packageName = "com.maps",
        componentName = ComponentName("com.maps", "Main"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )

    private var updated: QuickButtonShortcuts? = null

    private fun setSheet(initialSearchQuery: String = "") {
        val actions = createDefaultHomeScreenActions().copy(onUpdateQuickButtonShortcuts = { updated = it })
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ShortcutSelectionBottomSheet(
                    uiState = MainUiState(
                        activeQuickButtonModel = QuickButtonModel(),
                        editingShortcutType = "click",
                        apps = listOf(maps)
                    ),
                    actions = actions,
                    onDismissRequest = {},
                    options = ShortcutSelectionOptions(initialSearchQuery = initialSearchQuery)
                )
            }
        }
    }

    private fun type(text: String) {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(text)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `typing shows matching results and the hint`() {
        setSheet()
        type("calc")

        composeTestRule.onNodeWithText("Calculator").assertIsDisplayed()
        composeTestRule.onNodeWithText("best result below · ↵ picks").assertIsDisplayed()
    }

    @Test
    fun `enter applies the best result`() {
        setSheet()
        type("cal")

        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()

        assertEquals(ShortcutAction.systemIntent("Calculator"), updated?.click)
    }

    @Test
    fun `enter can pick an app`() {
        setSheet()
        type("map")

        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()

        assertEquals(ShortcutAction.openApp("com.maps"), updated?.click)
    }

    @Test
    fun `no match shows the empty message and enter does nothing`() {
        setSheet()
        type("zzz")

        composeTestRule.onNodeWithText("Nothing with “zzz”.").assertIsDisplayed()
        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()

        assertNull(updated)
    }

    @Test
    fun `tapping a result applies it`() {
        setSheet(initialSearchQuery = "calc")

        composeTestRule.onNodeWithText("Calculator").performClick()

        assertEquals(ShortcutAction.systemIntent("Calculator"), updated?.click)
    }

    @Test
    fun `apps chip switches to apps only search`() {
        setSheet()
        composeTestRule.onNodeWithText("Apps").performClick()
        type("cal")

        composeTestRule.onNodeWithText("Nothing with “cal”.").assertIsDisplayed()
    }
}
