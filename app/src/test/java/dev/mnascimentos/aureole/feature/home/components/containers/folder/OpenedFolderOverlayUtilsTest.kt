package dev.mnascimentos.aureole.feature.home.components.containers.folder

import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OpenedFolderOverlayUtilsTest {

    @Test
    fun testResolvePopupAlignment() {
        assertEquals(
            Alignment.Center,
            OpenedFolderOverlayUtils.resolvePopupAlignment(isGridFolderEnabled = true, isLeftHandedMode = false)
        )
        assertEquals(
            Alignment.TopStart,
            OpenedFolderOverlayUtils.resolvePopupAlignment(isGridFolderEnabled = false, isLeftHandedMode = true)
        )
        assertEquals(
            Alignment.TopEnd,
            OpenedFolderOverlayUtils.resolvePopupAlignment(isGridFolderEnabled = false, isLeftHandedMode = false)
        )
    }

    @Test
    fun testCalculateClampedTopDp() {
        val dp1 = OpenedFolderOverlayUtils.calculateClampedTopDp(100f, 1000f, 2f, 10.dp)
        assertTrue(dp1.value > 0)

        val dp2 = OpenedFolderOverlayUtils.calculateClampedTopDp(0f, 0f, 2f, 0.dp)
        assertTrue(dp2.value > 0)
    }

    @Test
    fun testIsGridFolderMode() {
        val folder1 = AppFolder(id = "f1", name = "F1", displayAsGrid = true)
        val uiState1 = MainUiState()
        assertTrue(OpenedFolderOverlayUtils.isGridFolderMode(uiState1, folder1))

        val folder2 = AppFolder(id = "f2", name = "F2", panelId = "p1", displayAsGrid = false)
        val container = ContainerModel(id = "p1", isGridFolderEnabled = true)
        val uiState2 = MainUiState(containers = mapOf("p1" to container))
        assertTrue(OpenedFolderOverlayUtils.isGridFolderMode(uiState2, folder2))

        val folder3 = AppFolder(id = "f3", name = "F3", displayAsGrid = false)
        assertFalse(OpenedFolderOverlayUtils.isGridFolderMode(MainUiState(), folder3))
    }
}
