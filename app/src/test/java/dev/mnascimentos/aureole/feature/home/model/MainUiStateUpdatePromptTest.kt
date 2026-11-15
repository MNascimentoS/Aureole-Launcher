package dev.mnascimentos.aureole.feature.home.model

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainUiStateUpdatePromptTest {

    private val app = AppInfo("Mail", "com.mail", ComponentName("com.mail", "com.mail.Main"), ColorDrawable())

    @Test
    fun `when the home has nothing in front, should allow the update prompt`() {
        assertTrue(MainUiState().canShowUpdatePrompt)
    }

    @Test
    fun `when the drawer or any sheet, dialog or folder is in front, should hold the update prompt`() {
        val inFront = mapOf<String, (MainUiState) -> MainUiState>(
            "all apps drawer" to { it.copy(isAllAppsDrawerOpen = true) },
            "grid edit mode" to { it.copy(isGridEditMode = true) },
            "widget picker" to { it.copy(showWidgetPicker = true) },
            "customize sheet" to { it.copy(showCustomizeBottomSheet = true) },
            "release notes sheet" to { it.copy(showReleaseNotesBottomSheet = true) },
            "edit clock sheet" to { it.copy(showEditClockBottomSheet = true) },
            "search query" to { it.copy(searchQuery = "ma") },
            "opened folder" to { it.copy(activeFolder = AppFolder(id = "f", name = "Trabalho")) },
            "favorite picker" to { it.copy(showFavoritePickerDialog = true) },
            "settings dialog" to { it.copy(showSettingsDialog = true) },
            "container app sheet" to {
                it.copy(activeContainerAppBottomSheet = ContainerAppBottomSheetState(app, "p"))
            },
        )
        inFront.forEach { (name, open) ->
            assertFalse("$name in front", open(MainUiState()).canShowUpdatePrompt)
        }
    }

    @Test
    fun `when the onboarding blocks the home, should hold the update prompt`() {
        val onboarding = OnboardingUiState(stage = OnboardingStage.WELCOME)
        assertFalse(MainUiState(onboarding = onboarding).canShowUpdatePrompt)
    }

    @Test
    fun `when the prompt is held, should keep the pending flag so it shows back on the home`() {
        val pending = MainUiState(showUpdateAvailableDialog = true, isAllAppsDrawerOpen = true)
        assertFalse(pending.canShowUpdatePrompt)
        assertTrue(
            pending.copy(isAllAppsDrawerOpen = false).let { it.showUpdateAvailableDialog && it.canShowUpdatePrompt }
        )
    }
}
