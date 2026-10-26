package dev.mnascimentos.aureole.feature.home.components.appsdrawer.appitem

import android.content.ComponentName
import android.graphics.Color
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.core.graphics.drawable.toDrawable
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowActions
import dev.mnascimentos.aureole.composable.appitem.model.AppItemRowConfig
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.LocalHomeUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppItemRowTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val sampleApp = AppInfo(
        label = "Row App Test",
        packageName = "com.example.row",
        componentName = ComponentName("com.example.row", "MainActivity"),
        icon = Color.GREEN.toDrawable(),
    )

    @Test
    fun `when rendering AppItemRow in standard right-handed mode, should display app label`() {
        val uiState = MainUiState(isLeftHandedMode = false, isThemedAppIconsEnabled = false)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    HomeAppItemRow(
                        app = sampleApp,
                        onClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Row App Test").assertIsDisplayed()
    }

    @Test
    fun `when rendering AppItemRow in left-handed mode, should display app label`() {
        val uiState = MainUiState(isLeftHandedMode = true, isThemedAppIconsEnabled = false)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    HomeAppItemRow(
                        app = sampleApp,
                        onClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Row App Test").assertIsDisplayed()
    }

    @Test
    fun `when rendering AppItemRow with themed app icons enabled, should display app label`() {
        val uiState = MainUiState(isLeftHandedMode = false, isThemedAppIconsEnabled = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    HomeAppItemRow(
                        app = sampleApp,
                        onClick = {}
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Row App Test").assertIsDisplayed()
    }

    @Test
    fun `when clicking AppItemRow, should invoke onClick callback`() {
        var clicked = false
        val uiState = MainUiState()

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    HomeAppItemRow(
                        app = sampleApp,
                        onClick = { clicked = true }
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Row App Test").performClick()
        assertTrue(clicked)
    }

    @Test
    fun `when long clicking AppItemRow with useActionSheet true, should show AppItemBottomSheet`() {
        var appInfoClicked = false
        val uiState = MainUiState()

        val actions = AppItemRowActions(
            onAppInfoClick = { appInfoClicked = true }
        )
        val config = AppItemRowConfig(useActionSheet = true)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    HomeAppItemRow(
                        app = sampleApp,
                        onClick = {},
                        config = config,
                        actions = actions
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Row App Test").performTouchInput { longClick() }

        composeTestRule.onNodeWithText("App Info").assertIsDisplayed()
        composeTestRule.onNodeWithText("App Info").performClick()

        assertTrue(appInfoClicked)
    }

    @Test
    fun `when long clicking AppItemRow with useActionSheet false, should show AppItemPopup`() {
        var appInfoClicked = false
        val uiState = MainUiState()

        val actions = AppItemRowActions(
            onAppInfoClick = { appInfoClicked = true }
        )
        val config = AppItemRowConfig(useActionSheet = false)

        composeTestRule.setContent {
            AureoleLauncherTheme {
                CompositionLocalProvider(LocalHomeUiState provides uiState) {
                    HomeAppItemRow(
                        app = sampleApp,
                        onClick = {},
                        config = config,
                        actions = actions
                    )
                }
            }
        }

        composeTestRule.onNodeWithText("Row App Test").performTouchInput { longClick() }

        composeTestRule.onNodeWithText("App").assertIsDisplayed()
        composeTestRule.onNodeWithText("App").performClick()

        assertTrue(appInfoClicked)
    }
}
