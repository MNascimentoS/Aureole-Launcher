package dev.mnascimentos.aureole.feature.home.components.containers

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.QuickButtonAppearance
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import org.junit.Assert.assertEquals
import org.junit.Test

/** The in-place editors of "Manage items" write to the draft through these (RF-07); plain JVM. */
class EditContainerStateEditTest {

    private fun state(folders: List<AppFolder> = emptyList()) = EditContainerState(
        panel = ContainerModel(id = "p"),
        options = ContainerOptionsState(
            position = "Center",
            orientation = "Vertical",
            isBackgroundEnabled = true,
            isExpandCell = false,
            showAddFolderButton = true,
            showFolderLabels = true,
            isGridFolderEnabled = false,
            hideText = false,
            textColor = null,
            backgroundColor = null,
            strokeEnabled = false,
            strokeColor = null,
            folderColor = null,
            folderStrokeEnabled = false,
            scrollUpShortcut = null,
            scrollDownShortcut = null
        ),
        itemsList = emptyList(),
        folders = folders
    )

    @Test
    fun `adding a quick button should return its id for the editor`() {
        val state = state()

        val id = state.addQuickButton()

        assertEquals(id, state.quickButtons.single().id)
        assertEquals(id, state.itemsList.single { it.itemType == ContainerItemType.QUICK_BUTTON }.quickButtonId)
    }

    @Test
    fun `the quick button editor should write the look, saved with a trimmed name`() {
        val state = state()
        val id = state.addQuickButton()

        state.updateQuickButton(id) { it.copy(appearance = QuickButtonAppearance(label = " Lamp ")) }

        assertEquals(" Lamp ", state.quickButtons.single().appearance.label)
        assertEquals(" Lamp ", state.itemsList.single().quickButtonModel?.appearance?.label)
        assertEquals("Lamp", state.toModel().quickButtons.single().appearance.label)
    }

    @Test
    fun `an unknown quick button should change nothing`() {
        val state = state()
        state.addQuickButton()
        val before = state.quickButtons

        state.updateQuickButton("missing") { it.copy(appearance = QuickButtonAppearance(label = "x")) }

        assertEquals(before, state.quickButtons)
    }

    @Test
    fun `the folder editor should write the name and look to that folder only`() {
        val state = state(folders = listOf(AppFolder(id = "a", name = "A"), AppFolder(id = "b", name = "B")))

        state.updateFolder("a", FolderDetails(name = "Games", emoji = "x"))

        assertEquals(listOf("Games", "B"), state.folders.map { it.name })
        assertEquals("x", state.folders.first().emoji)
    }
}
