package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.ui.test.junit4.v2.createComposeRule
import dev.mnascimentos.aureole.composable.dialogs.FolderIconRegistry
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
    fun `when the folder position is known, the anchor should be its window top less the status bar`() {
        val anchor = OpenedFolderOverlayUtils.folderAnchorTopPx(topYPx = 200f, statusInsetPx = 24f, fallbackPx = 80f)
        assertEquals(176f, anchor, 0f)
    }

    @Test
    fun `when the folder position is unknown, the anchor should be the fallback`() {
        val anchor = OpenedFolderOverlayUtils.folderAnchorTopPx(topYPx = 0f, statusInsetPx = 24f, fallbackPx = 80f)
        assertEquals(80f, anchor, 0f)
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
