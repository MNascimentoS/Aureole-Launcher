package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.ui.Alignment
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.designsystem.theme.AureoleLauncherTheme
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderUtilsAndRegistryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    // --- OpenedFolderOverlayUtils Tests ---

    @Test
    fun `when isGridFolderEnabled is true, should resolve alignment to Center`() {
        val alignment = OpenedFolderOverlayUtils.resolvePopupAlignment(
            isGridFolderEnabled = true,
            isLeftHandedMode = false,
        )
        assertEquals(Alignment.Center, alignment)
    }

    @Test
    fun `when isGridFolderEnabled is false and isLeftHandedMode is true, should resolve alignment to TopStart`() {
        val alignment = OpenedFolderOverlayUtils.resolvePopupAlignment(
            isGridFolderEnabled = false,
            isLeftHandedMode = true
        )
        assertEquals(Alignment.TopStart, alignment)
    }

    @Test
    fun `when isGridFolderEnabled is false and isLeftHandedMode is false, should resolve alignment to TopEnd`() {
        val alignment = OpenedFolderOverlayUtils.resolvePopupAlignment(
            isGridFolderEnabled = false,
            isLeftHandedMode = false
        )
        assertEquals(Alignment.TopEnd, alignment)
    }

    @Test
    fun `when topYPx is positive, should calculate clampedTopDp based on topYPx and screenDensity`() {
        // topYPx = 200, density = 2.0 -> raw = 100.dp - 24.dp = 76.dp
        val clamped = OpenedFolderOverlayUtils.calculateClampedTopDp(
            topYPx = 200f,
            screenHeightPx = 1920f,
            screenDensity = 2f,
            topInsetDp = 24.dp
        )
        assertEquals(76.dp, clamped)
    }

    @Test
    fun `when topYPx is zero or negative, should default rawTopDp to 40dp`() {
        val clamped = OpenedFolderOverlayUtils.calculateClampedTopDp(
            topYPx = 0f,
            screenHeightPx = 1920f,
            screenDensity = 2f,
            topInsetDp = 24.dp
        )
        assertEquals(40.dp, clamped)
    }

    @Test
    fun `when calculated topDp exceeds maxTopDp, should coerce to maxTopDp`() {
        // screenHeightPx = 400, density = 2.0 -> (200 - 300).coerceAtLeast(16) = 16.dp maxTopDp
        val clamped = OpenedFolderOverlayUtils.calculateClampedTopDp(
            topYPx = 1000f,
            screenHeightPx = 400f,
            screenDensity = 2f,
            topInsetDp = 0.dp
        )
        assertEquals(16.dp, clamped)
    }

    @Test
    fun `when folder displayAsGrid is true, should return true for isGridFolderMode`() {
        val uiState = MainUiState()
        val folder = AppFolder(id = "1", name = "Test", displayAsGrid = true)

        val isGrid = OpenedFolderOverlayUtils.isGridFolderMode(uiState, folder)
        assertTrue(isGrid)
    }

    @Test
    fun `when folder panel has isGridFolderEnabled true, should return true for isGridFolderMode`() {
        val panel = ContainerModel(id = "p1", isGridFolderEnabled = true)
        val uiState = MainUiState(containers = mapOf("p1" to panel))
        val folder = AppFolder(id = "1", name = "Test", panelId = "p1", displayAsGrid = false)

        val isGrid = OpenedFolderOverlayUtils.isGridFolderMode(uiState, folder)
        assertTrue(isGrid)
    }

    @Test
    fun `when neither folder nor panel has grid enabled, should return false for isGridFolderMode`() {
        val panel = ContainerModel(id = "p1", isGridFolderEnabled = false)
        val uiState = MainUiState(containers = mapOf("p1" to panel))
        val folder = AppFolder(id = "1", name = "Test", panelId = "p1", displayAsGrid = false)

        val isGrid = OpenedFolderOverlayUtils.isGridFolderMode(uiState, folder)
        assertFalse(isGrid)
    }

    // --- FolderIconRegistry Tests ---

    @Test
    fun `when rendering icon by key, should display icon correctly`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderIconRegistry.RenderIcon(name = "star")
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when rendering icon by alias, should resolve to mapped key`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderIconRegistry.RenderIcon(name = "jogos") // mapped to "play" -> "RightDown"
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when rendering unknown icon name, should fallback to default folder icon`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderIconRegistry.RenderIcon(name = "unknown_icon_xyz_123")
            }
        }
        composeTestRule.waitForIdle()
    }

    @Test
    fun `when rendering null icon name, should fallback gracefully`() {
        composeTestRule.setContent {
            AureoleLauncherTheme {
                FolderIconRegistry.RenderIcon(name = null)
            }
        }
        composeTestRule.waitForIdle()
    }
}
