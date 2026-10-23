package dev.mnascimentos.aureole.feature.home.grid

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AddContainerDialogTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when rendering dialog, should display default options`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddContainerDialog(
                    onDismissRequest = {},
                    onSelectType = {}
                )
            }
        }

        composeTestRule.onNodeWithText("Adicionar Container").assertExists()
        composeTestRule.onNodeWithText("Relógio").assertExists()
        composeTestRule.onNodeWithText("Lista de Aplicativos").assertExists()
        composeTestRule.onNodeWithText("Container").assertExists()
        composeTestRule.onNodeWithText("Grupo de Widgets do Android").assertExists()
        composeTestRule.onNodeWithText("Botão Rápido").assertExists()
        composeTestRule.onNodeWithText("Container Scroll View").assertExists()
    }

    @Test
    fun `when rendering nested dialog, should not display scroll view option`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddContainerDialog(
                    onDismissRequest = {},
                    onSelectType = {},
                    isNested = true
                )
            }
        }

        composeTestRule.onNodeWithText("Adicionar ao Scroll View").assertExists()
        composeTestRule.onNodeWithText("Container Scroll View").assertDoesNotExist()
    }

    @Test
    fun `when clicking an option, should invoke onSelectType and onDismissRequest`() {
        var selectedType: LauncherItemType? = null
        var dismissed = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddContainerDialog(
                    onDismissRequest = { dismissed = true },
                    onSelectType = { selectedType = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Lista de Aplicativos").performClick()

        assertEquals(LauncherItemType.APPS_LIST, selectedType)
        assertTrue(dismissed)
    }
}
