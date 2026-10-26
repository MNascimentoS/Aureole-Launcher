package dev.mnascimentos.aureole.feature.home.grid

import android.content.Context
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.R
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
        val context = ApplicationProvider.getApplicationContext<Context>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddContainerDialog(
                    onDismissRequest = {},
                    onSelectType = {}
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.add_container_title)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_clock)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_apps_list)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_shortcuts_container)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_widget_list)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_quick_button)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_scroll_view)).assertExists()
    }

    @Test
    fun `when rendering nested dialog, should not display scroll view option`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        composeTestRule.setContent {
            AureoleLauncherTheme {
                AddContainerDialog(
                    onDismissRequest = {},
                    onSelectType = {},
                    isNested = true
                )
            }
        }

        composeTestRule.onNodeWithText(context.getString(R.string.add_container_nested_title)).assertExists()
        composeTestRule.onNodeWithText(context.getString(R.string.add_container_scroll_view)).assertDoesNotExist()
    }

    @Test
    fun `when clicking an option, should invoke onSelectType and onDismissRequest`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
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

        composeTestRule.onNodeWithText(context.getString(R.string.add_container_apps_list)).performClick()

        assertEquals(LauncherItemType.APPS_LIST, selectedType)
        assertTrue(dismissed)
    }
}
