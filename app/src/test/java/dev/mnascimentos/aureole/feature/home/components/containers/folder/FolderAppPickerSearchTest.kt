package dev.mnascimentos.aureole.feature.home.components.containers.folder

import android.content.ComponentName
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.performTextInput
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderAppPickerSearchTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun app(label: String, pkg: String) = AppInfo(
        label = label,
        packageName = pkg,
        componentName = ComponentName(pkg, "Main"),
        icon = 0xFF2E7D32.toInt().toDrawable()
    )

    private val camera = app("Camera", "com.camera")
    private val camcorder = app("Camcorder", "com.camcorder")
    private val gallery = app("Gallery", "com.gallery")

    private fun setPicker(selected: List<String>, onSave: (List<String>) -> Unit, isLeftHanded: Boolean = false) {
        composeTestRule.setContent {
            CompositionLocalProvider(LocalHomeUiState provides MainUiState(isLeftHandedMode = isLeftHanded)) {
                AureoleLauncherTheme {
                    FolderAppPickerDialog(
                        folder = AppFolder(id = "f", name = "Media", appPackageNames = selected),
                        allApps = listOf(gallery, camera, camcorder),
                        onDismiss = {},
                        onSave = onSave
                    )
                }
            }
        }
    }

    private fun search(text: String) {
        composeTestRule.onNode(hasSetTextAction()).performTextInput(text)
        composeTestRule.mainClock.advanceTimeBy(400L)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `search shows ranked results with the hint`() {
        setPicker(emptyList(), onSave = {})
        search("cam")

        composeTestRule.onNodeWithText("Camera").assertIsDisplayed()
        composeTestRule.onNodeWithText("Camcorder").assertIsDisplayed()
        composeTestRule.onNodeWithText("best result below · ↵ picks").assertIsDisplayed()
    }

    @Test
    fun `enter marks the best result and save reports it`() {
        var saved = emptyList<String>()
        setPicker(emptyList(), onSave = { saved = it })
        search("cam")

        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save 1 app").performClick()

        assertEquals(listOf("com.camcorder"), saved)
    }

    @Test
    fun `enter unmarks the best result when it is already in the folder`() {
        var saved = listOf("x")
        setPicker(listOf("com.camcorder"), onSave = { saved = it })
        search("cam")

        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save 0 apps").performClick()

        assertEquals(emptyList<String>(), saved)
    }

    @Test
    fun `enter uses the best of the current query before the debounce`() {
        var saved = emptyList<String>()
        setPicker(emptyList(), onSave = { saved = it })

        composeTestRule.onNode(hasSetTextAction()).performTextInput("gal")
        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save", substring = true).performClick()

        assertEquals(listOf("com.gallery"), saved)
    }

    @Test
    fun `enter without results changes nothing`() {
        var saved = listOf("x")
        setPicker(listOf("com.gallery"), onSave = { saved = it })
        search("zzz")

        composeTestRule.onNode(hasSetTextAction()).performImeAction()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Save 1 app").performClick()

        assertEquals(listOf("com.gallery"), saved)
    }

    @Test
    fun `tray shows chips while searching and the remove button unmarks the app`() {
        var saved = listOf("x")
        setPicker(listOf("com.gallery"), onSave = { saved = it })
        search("cam")

        composeTestRule.onNodeWithContentDescription("Remove Gallery").assertIsDisplayed().performClick()
        composeTestRule.onNodeWithText("Save 0 apps").performClick()

        assertEquals(emptyList<String>(), saved)
    }

    @Test
    fun `left handed mode still renders the save button`() {
        setPicker(listOf("com.gallery"), onSave = {}, isLeftHanded = true)

        composeTestRule.onNodeWithText("Save 1 app").assertIsDisplayed()
    }
}
