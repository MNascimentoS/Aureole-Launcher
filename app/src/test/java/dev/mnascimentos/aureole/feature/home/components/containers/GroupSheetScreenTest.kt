package dev.mnascimentos.aureole.feature.home.components.containers

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import dev.mnascimentos.aureole.composable.ProvideHomeUiState
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.components.containers.model.EditContainerScreenParams
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/** "Customize group": tabs at the foot, pages in place and the tips of the first group (grupo-apps-pastas). */
@RunWith(RobolectricTestRunner::class)
class GroupSheetScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val app = AppInfo(
        label = APP_LABEL,
        packageName = APP_PACKAGE,
        componentName = ComponentName(APP_PACKAGE, "MainActivity"),
        icon = ColorDrawable(APP_COLOR)
    )
    private var tipsSeen = 0
    private val saved = mutableListOf<ContainerModel>()

    private fun setSheet(isNew: Boolean, areTipsSeen: Boolean = false, uiState: MainUiState = MainUiState()) {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                ProvideHomeUiState(uiState) {
                    EditContainerScreen(
                        params = EditContainerScreenParams(
                            panel = ContainerModel(id = "group"),
                            allApps = listOf(app),
                            onDismiss = {},
                            onSave = { saved += it },
                            isNew = isNew,
                            areTipsSeen = areTipsSeen,
                            onTipsSeen = { tipsSeen++ }
                        )
                    )
                }
            }
        }
        composeTestRule.mainClock.advanceTimeBy(OPEN_MS)
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when a group is opened again, should show the tabs and no tip`() {
        setSheet(isNew = false)

        composeTestRule.onNodeWithText("Customize group").assertIsDisplayed()
        composeTestRule.onNodeWithText("Items").assertIsDisplayed()
        composeTestRule.onNodeWithText("Layout").assertIsDisplayed()
        composeTestRule.onNodeWithText("Appearance").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip tips").assertDoesNotExist()
        composeTestRule.onNodeWithText(SAVED_HINT).assertDoesNotExist()
    }

    @Test
    fun `when the onboarding runs, should show the saved notice on the Items tab`() {
        setSheet(
            isNew = false,
            uiState = MainUiState(onboarding = OnboardingUiState(stage = OnboardingStage.CHECKLIST))
        )

        composeTestRule.onNodeWithText(SAVED_HINT).assertExists()
    }

    @Test
    fun `when the first group opens, should show tip 1 and save the skip`() {
        setSheet(isNew = true)

        composeTestRule.onNodeWithText("This is your app & folder group").assertIsDisplayed()
        composeTestRule.onNodeWithText("Skip tips").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("This is your app & folder group").assertDoesNotExist()
        assertEquals(1, tipsSeen)
    }

    @Test
    fun `when an app is added from tip 1, should go on to tip 2`() {
        setSheet(isNew = true)

        // By semantics: in the small Robolectric screen the button may sit below the sheet's visible part.
        composeTestRule.onNodeWithText("Add app").performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Tap to put in the group").assertExists()
        composeTestRule.onNodeWithText(APP_LABEL).performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("1 item in the group").assertExists()
        composeTestRule.onNodeWithContentDescription("Back").performSemanticsAction(SemanticsActions.OnClick)
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Done, the group is already on the home").assertExists()
        composeTestRule.onNodeWithContentDescription("Remove $APP_LABEL", useUnmergedTree = true).assertExists()
    }

    @Test
    fun `when the tabs change, should show the layout and the appearance`() {
        setSheet(isNew = false)

        composeTestRule.onNodeWithText("Layout").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Upright").assertIsDisplayed()
        composeTestRule.onNodeWithText("Lying down").assertIsDisplayed()
        composeTestRule.onNodeWithText("Your changes are saved automatically when you close").assertDoesNotExist()

        composeTestRule.onNodeWithText("Appearance").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Group background").assertIsDisplayed()
        composeTestRule.onNodeWithText("Group background").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithText("Only for this group").assertIsDisplayed()
    }

    private companion object {
        const val OPEN_MS = 500L
        const val SAVED_HINT = "Your changes are saved automatically when you close"
        const val APP_LABEL = "Camera"
        const val APP_PACKAGE = "com.example.camera"
        const val APP_COLOR = 0xFF1B65C0.toInt()
    }
}
