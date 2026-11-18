package dev.mnascimentos.aureole.feature.home.components.quickbutton

import androidx.compose.material3.Text
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.core.data.model.QuickButtonModel
import dev.mnascimentos.aureole.core.data.model.QuickButtonShortcuts
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.ManageItemRow
import dev.mnascimentos.aureole.feature.home.components.containers.model.ManageItemRowParams
import dev.mnascimentos.aureole.feature.home.components.quickbutton.model.displayTitle
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** One name for a quick button everywhere (RF-B1): the typed one, else the click shortcut's, else "Quick Button". */
@RunWith(RobolectricTestRunner::class)
class QuickButtonNameTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun model(label: String?, click: ShortcutAction) = QuickButtonModel(
        id = "qb",
        appearance = QuickButtonAppearance(label = label),
        shortcuts = QuickButtonShortcuts(click = click)
    )

    private fun showTitle(model: QuickButtonModel, appLabel: String? = null) {
        composeTestRule.setContent {
            AureoleLauncherTheme { Text(model.displayTitle(appLabel)) }
        }
    }

    @Test
    fun `when a name was typed, should use it`() {
        showTitle(model("  Lamp ", ShortcutAction.systemIntent("Calendar")))

        composeTestRule.onNodeWithText("Lamp").assertIsDisplayed()
    }

    @Test
    fun `when no name was typed, should use the click shortcut name`() {
        showTitle(model(null, ShortcutAction.systemIntent("Calendar")))

        composeTestRule.onNodeWithText("Calendar").assertIsDisplayed()
    }

    @Test
    fun `when the click opens an app, should use the app name`() {
        showTitle(model(null, ShortcutAction.openApp("a.b")), appLabel = "Gallery")

        composeTestRule.onNodeWithText("Gallery").assertIsDisplayed()
    }

    @Test
    fun `when there is no shortcut and no name, should use the generic quick button name`() {
        showTitle(model(null, ShortcutAction.None))

        composeTestRule.onNodeWithText("Quick Button").assertIsDisplayed()
    }

    @Test
    fun `when the manage items row shows a quick button, should show the typed name as its title`() {
        val item = ContainerItemEntity(
            id = "row",
            panelId = "panel",
            itemType = ContainerItemType.QUICK_BUTTON,
            quickButtonId = "qb"
        ).also { it.quickButtonModel = model("Lamp", ShortcutAction.systemIntent("Calendar")) }

        composeTestRule.setContent {
            AureoleLauncherTheme {
                ManageItemRow(ManageItemRowParams(item = item, folders = emptyList(), allApps = emptyList()))
            }
        }

        composeTestRule.onNodeWithText("Lamp").assertIsDisplayed()
    }
}
