package dev.mnascimentos.aureole.feature.settings.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SettingsDialogComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering SettingsButtonPositionDialog, should select option and handle cancel`() {
        var selectedPosition: String? = null
        var dismissCalled = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingsButtonPositionDialog(
                    currentPosition = "Right",
                    onPositionSelected = { selectedPosition = it },
                    onDismiss = { dismissCalled = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Posição do Botão de Configurações").assertIsDisplayed()
        composeTestRule.onNodeWithText("Esquerda (Left)").performClick()
        assertEquals("Left", selectedPosition)

        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(dismissCalled)
    }

    @Test
    fun `when rendering SearchIconPositionDialog, should select option and handle cancel`() {
        var selectedPosition: String? = null
        var dismissCalled = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SearchIconPositionDialog(
                    currentPosition = "Left",
                    onPositionSelected = { selectedPosition = it },
                    onDismiss = { dismissCalled = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Posição do Ícone de Busca").assertIsDisplayed()
        composeTestRule.onNodeWithText("Direita (Right)").performClick()
        assertEquals("Right", selectedPosition)

        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(dismissCalled)
    }

    @Test
    fun `when rendering ContainerPositionDialog, should select position option`() {
        var selectedPosition: String? = null
        var dismissCalled = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ContainerPositionDialog(
                    currentPosition = "Center",
                    onPositionSelected = { selectedPosition = it },
                    onDismiss = { dismissCalled = true },
                )
            }
        }

        composeTestRule.onNodeWithText("Alinhamento do Painel Lateral").assertIsDisplayed()
        composeTestRule.onNodeWithText("Superior (Top)").performClick()
        assertEquals("Top", selectedPosition)

        composeTestRule.onNodeWithText("Cancelar").performClick()
        assertTrue(dismissCalled)
    }
}
