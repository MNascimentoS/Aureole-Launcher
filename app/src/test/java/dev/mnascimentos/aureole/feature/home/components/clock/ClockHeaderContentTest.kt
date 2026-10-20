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
                        dateFormat = "DEFAULT"
                    )
                )
            }
        }

        composeTestRule.onNodeWithText("Boa tarde Teste").assertIsDisplayed()
    }

    @Test
    fun `when rendering clock header content with DATE_ON_TOP style, should render without throwing`() {
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
}
