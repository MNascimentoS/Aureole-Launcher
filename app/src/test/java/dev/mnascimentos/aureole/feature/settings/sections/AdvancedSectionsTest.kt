package dev.mnascimentos.aureole.feature.settings.sections

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTouchInput
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.composable.menu.model.MENU_HOLD_MS
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.settings.model.ENTRY_TAP_GUARD_MS
import dev.mnascimentos.aureole.feature.settings.model.SettingsAdvancedDialogActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsScreenActions
import dev.mnascimentos.aureole.feature.settings.model.SettingsUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.shadows.ShadowSystemClock
import java.time.Duration

private const val HOLD_MARGIN_MS = 100L

@RunWith(RobolectricTestRunner::class)
class AdvancedSectionsTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun render(
        uiState: SettingsUiState = SettingsUiState(),
        actions: SettingsScreenActions = SettingsScreenActions(),
        dialogActions: SettingsAdvancedDialogActions = SettingsAdvancedDialogActions({}, {}, {})
    ) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                Column(Modifier.verticalScroll(rememberScrollState())) {
                    AdvancedSections(uiState = uiState, actions = actions, dialogActions = dialogActions)
                }
            }
        }
    }

    private fun top(text: String) = composeTestRule.onNodeWithText(text).getUnclippedBoundsInRoot().top

    /** Scrolls with the clock running, then freezes it right before the finger goes down and lets the hold elapse. */
    private fun hold(title: String) {
        composeTestRule.onNodeWithText(title).performScrollTo()
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.onNodeWithText(title).performTouchInput { down(center) }
        composeTestRule.mainClock.advanceTimeBy(MENU_HOLD_MS + HOLD_MARGIN_MS)
    }

    @Test
    fun `groups come in the order System, About, Care zone`() {
        render()
        val tops = listOf("SYSTEM", "ABOUT", "CARE ZONE").map { top(it) }
        assertEquals(tops.sorted(), tops)
        assertEquals(tops.distinct(), tops)
    }

    @Test
    fun `Check for updates sits in System, before the About group`() {
        render()
        composeTestRule.onNodeWithText("Check for updates").assertExists()
        assertTrue(top("SYSTEM") < top("Check for updates"))
        assertTrue(top("Check for updates") < top("ABOUT"))
    }

    @Test
    fun `Reset and Uninstall are the last rows, after the About links`() {
        render()
        assertTrue(top("Open source licenses") < top("CARE ZONE"))
        assertTrue(top("CARE ZONE") < top("Reset launcher"))
        assertTrue(top("Reset launcher") < top("Uninstall launcher"))
    }

    @Test
    fun `holding Reset fires the factory reset, a quick tap does not`() {
        var confirmed = false
        render(actions = SettingsScreenActions(onConfirmFactoryReset = { confirmed = true }))

        composeTestRule.onNodeWithText("Reset launcher").performScrollTo().performClick()
        assertFalse(confirmed)

        hold("Reset launcher")
        assertTrue(confirmed)
    }

    @Test
    fun `holding Uninstall hands over to the store, a quick tap does not`() {
        val application = ApplicationProvider.getApplicationContext<Application>()
        render()

        composeTestRule.onNodeWithText("Uninstall launcher").performScrollTo().performClick()
        assertNull(shadowOf(application).nextStartedActivity)

        hold("Uninstall launcher")
        assertNotNull(shadowOf(application).nextStartedActivity)
    }

    @Test
    fun `About rows and the other actions call their callbacks`() {
        var contributors = false
        var licenses = false
        var releaseNotes = false
        var defaultLauncher = false
        render(
            actions = SettingsScreenActions(onSetDefaultLauncherClick = { defaultLauncher = true }),
            dialogActions = SettingsAdvancedDialogActions(
                onNavigateToContributors = { contributors = true },
                onNavigateToLicenses = { licenses = true },
                onOpenReleaseNotesDialog = { releaseNotes = true }
            )
        )

        composeTestRule.onNodeWithText("Contributors").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Open source licenses").performScrollTo().performClick()
        composeTestRule.onNodeWithText("What's new").performScrollTo().performClick()
        composeTestRule.onNodeWithText("Default launcher").performScrollTo().performClick()
        assertTrue(contributors && licenses && releaseNotes && defaultLauncher)
    }

    @Test
    fun `Check for updates toggles after the entry guard`() {
        var toggled = false
        render(actions = SettingsScreenActions(onToggleInAppUpdate = { toggled = true }))

        composeTestRule.onNodeWithText("Check for updates").performScrollTo().performClick()
        assertFalse(toggled)
        ShadowSystemClock.advanceBy(Duration.ofMillis(ENTRY_TAP_GUARD_MS))
        composeTestRule.onNodeWithText("Check for updates").performClick()
        assertTrue(toggled)
    }
}
