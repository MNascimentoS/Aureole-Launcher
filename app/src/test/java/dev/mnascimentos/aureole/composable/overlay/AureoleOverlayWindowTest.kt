package dev.mnascimentos.aureole.composable.overlay

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.AppItemRow
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureoleOverlayWindowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app = AppInfo(
        label = "Scroll App",
        packageName = "com.example.scroll",
        componentName = ComponentName("com.example.scroll", "MainActivity"),
        icon = Color.GREEN.toDrawable(),
    )

    @Composable
    private fun Row() {
        AppItemRow(app = app, onClick = {}, config = AppItemRowConfig(useActionSheet = true))
    }

    @Test
    fun `long click on a row inside a LazyColumn opens the menu without crashing`() {
        composeTestRule.setContent { AureoleLauncherTheme { LazyColumn { item { Row() } } } }

        composeTestRule.onNodeWithText("Scroll App").performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("App Info").assertExists()
    }

    @Test
    fun `long click on a row inside a verticalScroll Column opens the menu without crashing`() {
        composeTestRule.setContent {
            AureoleLauncherTheme { Column(Modifier.verticalScroll(rememberScrollState())) { Row() } }
        }

        composeTestRule.onNodeWithText("Scroll App").performTouchInput { longClick() }
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("App Info").assertExists()
    }

    @Test
    fun `window content leaves composition when the host is removed`() {
        val shown = mutableStateOf(true)
        composeTestRule.setContent {
            AureoleLauncherTheme {
                if (shown.value) {
                    AureoleOverlayWindow(
                        onDismissRequest = { shown.value = false }
                    ) { Text("Overlay body") }
                }
            }
        }
        composeTestRule.onNodeWithText("Overlay body").assertExists()

        shown.value = false
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Overlay body").assertDoesNotExist()
    }
}
