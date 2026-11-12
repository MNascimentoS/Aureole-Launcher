package dev.mnascimentos.aureole.feature.home.model

import android.content.ComponentName
import android.graphics.drawable.ColorDrawable
import androidx.compose.ui.unit.IntRect
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.AppInfo
import dev.mnascimentos.aureole.core.data.model.OnboardingStage
import dev.mnascimentos.aureole.feature.home.components.onboarding.model.OnboardingUiState
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class GestureExclusionTest {

    private val folder = AppFolder(id = "folder_1", name = "Trabalho")
    private val app = AppInfo("Mail", "com.mail", ComponentName("com.mail", "com.mail.Main"), ColorDrawable())
    private val drawerOpen = MainUiState(isAllAppsDrawerOpen = true)

    private val overlaysOverDrawer = mapOf<String, (MainUiState) -> MainUiState>(
        "grid edit mode" to { it.copy(isGridEditMode = true) },
        "customize sheet" to { it.copy(showCustomizeBottomSheet = true) },
        "search" to { it.copy(searchQuery = "ma") },
        "opened folder" to { it.copy(activeFolder = folder) },
        "create folder dialog" to { it.copy(isCreateFolderDialogVisible = true) },
        "favorite picker dialog" to { it.copy(showFavoritePickerDialog = true) },
        "widget picker" to { it.copy(showWidgetPicker = true) },
        "container app sheet" to { it.copy(activeContainerAppBottomSheet = ContainerAppBottomSheetState(app, "p")) },
        "onboarding" to { it.copy(onboarding = OnboardingUiState(stage = OnboardingStage.WELCOME)) },
    )

    @Test
    fun `when on the plain home, should exclude the full screen`() {
        assertEquals(GestureExclusion.FULL_SCREEN, MainUiState().gestureExclusion())
        assertEquals(GestureExclusion.FULL_SCREEN, MainUiState(isLeftHandedMode = true).gestureExclusion())
    }

    @Test
    fun `when only the drawer is open right-handed, should exclude only the right edge`() {
        assertEquals(GestureExclusion.RIGHT_EDGE, drawerOpen.gestureExclusion())
    }

    @Test
    fun `when only the drawer is open left-handed, should exclude only the left edge`() {
        assertEquals(GestureExclusion.LEFT_EDGE, drawerOpen.copy(isLeftHandedMode = true).gestureExclusion())
    }

    @Test
    fun `when something is over the drawer, should exclude nothing in both hands`() {
        overlaysOverDrawer.forEach { (name, open) ->
            assertEquals("$name right", GestureExclusion.NONE, open(drawerOpen).gestureExclusion())
            val left = open(drawerOpen.copy(isLeftHandedMode = true))
            assertEquals("$name left", GestureExclusion.NONE, left.gestureExclusion())
        }
    }

    @Test
    fun `when an overlay is open without the drawer, should exclude nothing`() {
        overlaysOverDrawer.forEach { (name, open) ->
            assertEquals(name, GestureExclusion.NONE, open(MainUiState()).gestureExclusion())
        }
    }

    @Test
    fun `when the exclusion is the full screen, should return one rect over the whole area`() {
        assertEquals(
            listOf(IntRect(0, 0, 1080, 2400)),
            gestureExclusionRects(GestureExclusion.FULL_SCREEN, 1080, 2400, 0)
        )
    }

    @Test
    fun `when the exclusion is the right edge, should return a full-height strip on the right`() {
        assertEquals(
            listOf(IntRect(1000, 0, 1080, 2400)),
            gestureExclusionRects(GestureExclusion.RIGHT_EDGE, 1080, 2400, 80)
        )
    }

    @Test
    fun `when the exclusion is the left edge, should return a full-height strip on the left`() {
        assertEquals(
            listOf(IntRect(0, 0, 80, 2400)),
            gestureExclusionRects(GestureExclusion.LEFT_EDGE, 1080, 2400, 80)
        )
    }

    @Test
    fun `when the gesture inset is zero, should have nothing to exclude on an edge`() {
        assertEquals(emptyList<IntRect>(), gestureExclusionRects(GestureExclusion.RIGHT_EDGE, 1080, 2400, 0))
        assertEquals(emptyList<IntRect>(), gestureExclusionRects(GestureExclusion.LEFT_EDGE, 1080, 2400, 0))
        assertEquals(emptyList<IntRect>(), gestureExclusionRects(GestureExclusion.RIGHT_EDGE, 1080, 2400, -5))
    }

    @Test
    fun `when the inset is wider than the screen, should clamp the strip to the screen`() {
        assertEquals(
            listOf(IntRect(0, 0, 1080, 2400)),
            gestureExclusionRects(GestureExclusion.RIGHT_EDGE, 1080, 2400, 5000)
        )
    }

    @Test
    fun `when there is no exclusion, should return no rects`() {
        assertEquals(emptyList<IntRect>(), gestureExclusionRects(GestureExclusion.NONE, 1080, 2400, 80))
    }

    @Test
    fun `when the area is empty, should return no rects`() {
        assertEquals(emptyList<IntRect>(), gestureExclusionRects(GestureExclusion.FULL_SCREEN, 0, 2400, 80))
        assertEquals(emptyList<IntRect>(), gestureExclusionRects(GestureExclusion.FULL_SCREEN, 1080, 0, 80))
    }
}
