package dev.mnascimentos.aureole.feature.home.model

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.blocksHome
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainUiStateScrubberEdgeTest {

    private val folder = AppFolder(id = "folder_1", name = "Trabalho")
    private val app = AppInfo("Mail", "com.mail", ComponentName("com.mail", "com.mail.Main"), ColorDrawable())
    private val drawerOpen = MainUiState(isAllAppsDrawerOpen = true)

    @Test
    fun `when the drawer is closed, should not keep the scrubber edge active`() {
        assertFalse(MainUiState().isScrubberEdgeActive)
    }

    @Test
    fun `when only the drawer is open, should keep the scrubber edge active`() {
        assertTrue(drawerOpen.isScrubberEdgeActive)
        assertTrue(drawerOpen.isOverlayActive)
    }

    @Test
    fun `when something other than the drawer is open, should not keep the scrubber edge active`() {
        val overlays = mapOf<String, (MainUiState) -> MainUiState>(
            "grid edit mode" to { it.copy(isGridEditMode = true) },
            "widget picker" to { it.copy(showWidgetPicker = true) },
            "customize" to { it.copy(showCustomizeBottomSheet = true) },
            "search query" to { it.copy(searchQuery = "ma") },
            "opened folder" to { it.copy(activeFolder = folder) },
            "create folder" to { it.copy(isCreateFolderDialogVisible = true) },
            "favorite picker" to { it.copy(showFavoritePickerDialog = true) },
            "container app sheet" to {
                it.copy(activeContainerAppBottomSheet = ContainerAppBottomSheetState(app, "p"))
            },
        )
        overlays.forEach { (name, open) ->
            assertFalse("$name over the drawer", open(drawerOpen).isScrubberEdgeActive)
        }
    }

    @Test
    fun `when the onboarding blocks the home, should not keep the scrubber edge active`() {
        val onboarding = OnboardingUiState(stage = OnboardingStage.WELCOME)
        assertEquals(true, onboarding.blocksHome)
        assertFalse(drawerOpen.copy(onboarding = onboarding).isScrubberEdgeActive)
    }

    @Test
    fun `when the drawer is open, should still count as a home overlay for the back handler`() {
        assertTrue(drawerOpen.isHomeOverlayActive)
    }
}
