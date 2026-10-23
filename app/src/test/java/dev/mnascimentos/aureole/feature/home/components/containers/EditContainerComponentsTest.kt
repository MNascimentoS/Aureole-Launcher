package dev.mnascimentos.aureole.feature.home.components.containers

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerFormCallbacks
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerFormState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EditContainerComponentsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `when EditContainerOrientationSelector renders, should show options and trigger callback on click`() {
        var selectedOrientation: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerOrientationSelector(
                    orientation = "Vertical",
                    onOrientationSelected = { selectedOrientation = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Orientação do Container").assertIsDisplayed()
        composeTestRule.onNodeWithText("Vertical").assertIsDisplayed()
        composeTestRule.onNodeWithText("Horizontal").assertIsDisplayed().performClick()

        assertEquals("Horizontal", selectedOrientation)
    }

    @Test
    fun `when EditContainerAlignmentSelector is clicked, should open dropdown and select option`() {
        var selectedPosition: String? = null

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerAlignmentSelector(
                    position = "Space Between",
                    onPositionSelected = { selectedPosition = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Alinhamento dos Itens").assertIsDisplayed()
        composeTestRule.onNodeWithText("Espaçamento Entre (Separados)").assertIsDisplayed().performClick()

        composeTestRule.onNodeWithText("Centro").assertIsDisplayed().performClick()
        assertEquals("Center", selectedPosition)
    }

    @Test
    fun `when EditContainerOptionsExpandableSection header is clicked, should expand switches column`() {
        val formState = EditContainerFormState(
            position = "Center",
            orientation = "Vertical",
            isBackgroundEnabled = false,
            isExpandCell = false,
            showAddFolderButton = true,
            showFolderLabels = true,
            isGridFolderEnabled = false
        )

        val callbacks = EditContainerFormCallbacks(
            onPositionChange = {},
            onOrientationChange = {},
            onBgChange = {},
            onExpandChange = {},
            onAddFolderBtnChange = {},
            onFolderLabelsChange = {},
            onGridFolderChange = {}
        )

        composeTestRule.setContent {
            AureoleLauncherTheme {
                EditContainerOptionsExpandableSection(
                    formState = formState,
                    callbacks = callbacks
                )
            }
        }

        val headerNode = composeTestRule.onNodeWithText("Opções de Aparência e Comportamento")
        headerNode.assertIsDisplayed()

        headerNode.performClick()

        composeTestRule.onNodeWithText("Fundo do Container").assertIsDisplayed()
    }

    @Test
    fun `when SettingSwitchRow renders, should show label and switch`() {
        var checkedState = false

        composeTestRule.setContent {
            AureoleLauncherTheme {
                SettingSwitchRow(
                    label = "Test Setting",
                    checked = checkedState,
                    onCheckedChange = { checkedState = it }
                )
            }
        }

        composeTestRule.onNodeWithText("Test Setting").assertIsDisplayed()
    }
}
