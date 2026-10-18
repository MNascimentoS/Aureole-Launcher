package dev.mnascimentos.aureole.core.designsystem.components

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AureolePopupTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering popup box, should display inner content`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupBox {
                    Text("Popup Box Content")
                }
            }
        }

        composeTestRule.onNodeWithText("Popup Box Content").assertIsDisplayed()
    }

    @Test
    fun `when rendering header banner, should display title`() {
        var logoClicked = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleHeaderBanner(
                    title = "Banner Title",
                    onLogoClick = { logoClicked = true }
                )
            }
        }

        composeTestRule.onNodeWithText("Banner Title").assertIsDisplayed()
    }

    @Test
    fun `when rendering popup header, should display main title and session title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupHeader(
                    title = "Popup Title",
                    params = AureolePopupHeaderParams(
                        sessionTitle = "Session Subtitle",
                        showAureoleLogo = true
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Popup Title").assertIsDisplayed()
        composeTestRule.onNodeWithText("Session Subtitle").assertIsDisplayed()
    }

    @Test
    fun `when rendering popup menu section, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupMenuSection(title = "Section Header")
            }
        }

        composeTestRule.onNodeWithText("Section Header").assertIsDisplayed()
    }

    @Test
    fun `when rendering preview, should display complete popup box`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupBox {
                    AureolePopupHeader(
                        title = "Folder Name",
                        params = AureolePopupHeaderParams(
                            sessionTitle = "Session Subtitle"
                        )
                    )
                    AureolePopupMenuSection(title = "Section Header")
                    Text("Popup Body Content")
                }
            }
        }

        composeTestRule.onNodeWithText("Folder Name").assertIsDisplayed()
        composeTestRule.onNodeWithText("Session Subtitle").assertIsDisplayed()
        composeTestRule.onNodeWithText("Section Header").assertIsDisplayed()
        composeTestRule.onNodeWithText("Popup Body Content").assertIsDisplayed()
    }

    @Test
    fun `when rendering header banner with no logo click, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureoleHeaderBanner(
                    title = "Banner Default"
                )
            }
        }
        composeTestRule.onNodeWithText("Banner Default").assertIsDisplayed()
    }

    @Test
    fun `when rendering popup header with custom icons and settings click, should display icon`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupHeader(
                    title = "Custom Icon Header",
                    params = AureolePopupHeaderParams(
                        showAureoleLogo = false,
                        onSettingsClick = {},
                        customIcon = { Text("CustomIcon") }
                    )
                )
            }
        }
        composeTestRule.onNodeWithText("Custom Icon Header").assertIsDisplayed()
        composeTestRule.onNodeWithText("CustomIcon").assertIsDisplayed()
    }

    @Test
    fun `when rendering popup header with icon vector and close click, should display icon`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupHeader(
                    title = "Vector Icon Header",
                    params = AureolePopupHeaderParams(
                        showAureoleLogo = false,
                        onCloseClick = {},
                        customIcon = { Text("VectorIcon") }
                    )
                )
            }
        }
        composeTestRule.onNodeWithText("Vector Icon Header").assertIsDisplayed()
        composeTestRule.onNodeWithText("VectorIcon").assertIsDisplayed()
    }

    @Test
    fun `when rendering popup header with no clicks, should display title`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupHeader(
                    title = "No Clicks Header",
                    params = AureolePopupHeaderParams(
                        showAureoleLogo = false
                    )
                )
            }
        }
        composeTestRule.onNodeWithText("No Clicks Header").assertIsDisplayed()
    }

    @Test
    fun `when rendering popup header with icon vector and settings click, should display icon`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AureolePopupHeader(
                    title = "Settings Vector Header",
                    params = AureolePopupHeaderParams(
                        showAureoleLogo = false,
                        onSettingsClick = {},
                        customIcon = { Text("VectorIcon2") }
                    )
                )
            }
        }
        composeTestRule.onNodeWithText("Settings Vector Header").assertIsDisplayed()
        composeTestRule.onNodeWithText("VectorIcon2").assertIsDisplayed()
    }
}
