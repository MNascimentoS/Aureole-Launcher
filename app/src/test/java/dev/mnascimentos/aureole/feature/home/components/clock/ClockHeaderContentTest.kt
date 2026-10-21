package dev.mnascimentos.aureole.feature.home.components.clock

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.clock.model.ClockHeaderContentParams
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ClockHeaderContentTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering clock header content with greeting, should display greeting`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ClockHeaderContent(
                    params = ClockHeaderContentParams(
                        maxHeight = 120.dp,
                        colorPrimary = android.graphics.Color.WHITE,
                        composeTextColor = Color.White,
                        greeting = "Boa tarde Teste",
                        clockStyle = "GREETING_AND_DATE",
                        alignment = "CENTER",
                        fontFamily = "DEFAULT",
                        timeFormat = "12H",
                        dateFormat = "DEFAULT",
                    )
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Boa tarde Teste").assertIsDisplayed()
    }

    @Test
    fun `when rendering clock header content with DATE_ON_TOP style, should render without throwing`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ClockHeaderContent(
                    params = ClockHeaderContentParams(
                        maxHeight = 120.dp,
                        colorPrimary = android.graphics.Color.WHITE,
                        composeTextColor = Color.White,
                        greeting = "Bom dia Teste",
                        clockStyle = "DATE_ON_TOP",
                        alignment = "START",
                        fontFamily = "DEFAULT",
                        timeFormat = "24H",
                        dateFormat = "DEFAULT"
                    )
                )
            }
        }
    }

    @Test
    fun `when rendering clock header content with TIME_ONLY style, should not display greeting`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ClockHeaderContent(
                    params = ClockHeaderContentParams(
                        maxHeight = 120.dp,
                        colorPrimary = android.graphics.Color.WHITE,
                        composeTextColor = Color.White,
                        greeting = "Minha Saudacao Oculta",
                        clockStyle = "TIME_ONLY",
                        alignment = "CENTER",
                        fontFamily = "DEFAULT",
                        timeFormat = "12H",
                        dateFormat = "DEFAULT"
                    )
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Minha Saudacao Oculta").assertDoesNotExist()
    }

    @Test
    fun `when rendering clock header content with CUSTOM_GREETING style, should display greeting`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ClockHeaderContent(
                    params = ClockHeaderContentParams(
                        maxHeight = 120.dp,
                        colorPrimary = android.graphics.Color.WHITE,
                        composeTextColor = Color.White,
                        greeting = "Bem-vindo de volta!",
                        clockStyle = "CUSTOM_GREETING",
                        alignment = "CENTER",
                        fontFamily = "DEFAULT",
                        timeFormat = "12H",
                        dateFormat = "DEFAULT"
                    )
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Bem-vindo de volta!").assertIsDisplayed()
    }

    @Test
    fun `when rendering clock header content with compact height, should render without throwing`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ClockHeaderContent(
                    params = ClockHeaderContentParams(
                        maxHeight = 80.dp,
                        colorPrimary = android.graphics.Color.WHITE,
                        composeTextColor = Color.White,
                        greeting = "Saudacao Compacta",
                        clockStyle = "GREETING_AND_DATE",
                        alignment = "CENTER",
                        fontFamily = "DEFAULT",
                        timeFormat = "12H",
                        dateFormat = "SHORT"
                    )
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Saudacao Compacta").assertIsDisplayed()
    }

    @Test
    fun `when rendering clock header content with large height and END alignment, should render without throwing`() {
        // Arrange & Act
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ClockHeaderContent(
                    params = ClockHeaderContentParams(
                        maxHeight = 160.dp,
                        colorPrimary = android.graphics.Color.WHITE,
                        composeTextColor = Color.White,
                        greeting = "Saudacao Direita",
                        clockStyle = "GREETING_AND_DATE",
                        alignment = "END",
                        fontFamily = "BOLD",
                        timeFormat = "24H",
                        dateFormat = "ISO"
                    )
                )
            }
        }

        // Assert
        composeTestRule.onNodeWithText("Saudacao Direita").assertIsDisplayed()
    }
}
