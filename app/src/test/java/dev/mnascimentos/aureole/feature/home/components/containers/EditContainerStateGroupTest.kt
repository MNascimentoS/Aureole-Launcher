package dev.mnascimentos.aureole.feature.home.components.containers

import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.CONTAINER_BACKGROUND_THEME_REF
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemSpacing
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.ShortcutAction
import dev.mnascimentos.aureole.feature.home.components.containers.model.ContainerOptionsState
import dev.mnascimentos.aureole.feature.home.components.containers.model.groupItemCount
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** The group sheet changes the draft through these (grupo-apps-pastas RF-C3 to RF-C6, RF-E3); plain JVM. */
class EditContainerStateGroupTest {

    private fun state(items: List<ContainerItemEntity> = emptyList(), folders: List<AppFolder> = emptyList()) =
        EditContainerState(
            panel = ContainerModel(id = "p"),
            options = ContainerOptionsState(
                position = "Center",
                orientation = "Vertical",
                isBackgroundEnabled = false,
                isExpandCell = false,
                showAddFolderButton = true,
                showFolderLabels = false,
                isGridFolderEnabled = true,
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
            itemsList = items,
            folders = folders
        )

    private fun app(id: String, pkg: String) =
        ContainerItemEntity(id = id, panelId = "p", itemType = ContainerItemType.APP, packageName = pkg, orderIndex = 0)

    @Test
    fun `when an app is tapped in Add apps, should add it at the end and take it out on the next tap`() {
        val state = state(items = listOf(app("a", "pkg.a")))

        state.toggleApp("pkg.b")

        assertEquals(listOf("pkg.a", "pkg.b"), state.itemsList.map { it.packageName })
        assertTrue(state.hasApp("pkg.b"))

        state.toggleApp("pkg.b")

        assertEquals(listOf("pkg.a"), state.itemsList.map { it.packageName })
        assertFalse(state.hasApp("pkg.b"))
    }

    @Test
    fun `when an item is removed and undone, should come back at the same place`() {
        val state = state(items = listOf(app("a", "pkg.a"), app("b", "pkg.b"), app("c", "pkg.c")))

        val removed = state.removeItemAt(index = 1, id = 7L, name = "B")

        assertEquals(listOf("a", "c"), state.itemsList.map { it.id })
        assertEquals(7L, removed?.id)
        assertEquals("B", removed?.name)

        state.restore(requireNotNull(removed))

        assertEquals(listOf("a", "b", "c"), state.itemsList.map { it.id })
    }

    @Test
    fun `when a folder is removed and undone, should bring its folder back too`() {
        val folder = AppFolder(id = "f", name = "Work", appPackageNames = listOf("x", "y"))
        val item = ContainerItemEntity(
            id = "i",
            panelId = "p",
            itemType = ContainerItemType.FOLDER,
            folderId = "f",
            orderIndex = 0
        )
        val state = state(items = listOf(item), folders = listOf(folder))

        val removed = requireNotNull(state.removeItemAt(index = 0, id = 1L, name = "Work"))

        assertTrue(state.folders.isEmpty())

        state.restore(removed)
        state.restore(removed)

        assertEquals(listOf("i"), state.itemsList.map { it.id })
        assertEquals(listOf(folder), state.folders)
    }

    @Test
    fun `when removing out of range, should change nothing`() {
        val state = state(items = listOf(app("a", "pkg.a")))

        assertNull(state.removeItemAt(index = 3, id = 1L, name = "?"))
        assertEquals(1, state.itemsList.size)
    }

    @Test
    fun `when a folder is created with its apps, should add it at the end holding them`() {
        val state = state(items = listOf(app("a", "pkg.a")))

        state.addFolder(FolderDetails(name = "Social"), apps = listOf("pkg.x", "pkg.y"))

        val folderItem = state.itemsList.last()
        assertEquals(ContainerItemType.FOLDER, folderItem.itemType)
        val folder = state.folders.single { it.id == folderItem.folderId }
        assertEquals("Social", folder.name)
        assertEquals(listOf("pkg.x", "pkg.y"), folder.appPackageNames)
    }

    @Test
    fun `when a quick button is added for an action, should run that action on tap`() {
        val state = state()

        val id = state.addQuickButton(ShortcutAction.Notifications)

        assertEquals(ShortcutAction.Notifications, state.quickButtons.single { it.id == id }.shortcuts.click)
        assertEquals(ContainerItemType.QUICK_BUTTON, state.itemsList.single().itemType)
    }

    @Test
    fun `the count should be the same one for every kind of item`() {
        val state = state(items = listOf(app("a", "pkg.a")))
        state.addFolder(FolderDetails(name = "F"), apps = listOf("x", "y", "z"))
        state.addQuickButton(ShortcutAction.Notifications)

        assertEquals(3, groupItemCount(state.itemsList))
        assertEquals(3, state.toModel().items.size)
    }

    @Test
    fun `when saved, should keep the old switch in step with the background color`() {
        val state = state()

        assertFalse(state.toModel().isBackgroundEnabled)

        state.backgroundColorRef = CONTAINER_BACKGROUND_THEME_REF
        state.itemColorRef = null
        state.itemSpacing = ContainerItemSpacing.WIDE
        val saved = state.toModel()

        assertTrue(saved.isBackgroundEnabled)
        assertEquals(CONTAINER_BACKGROUND_THEME_REF, saved.backgroundColorRef)
        assertNull(saved.itemColorRef)
        assertEquals(ContainerItemSpacing.WIDE, saved.itemSpacing)
        assertTrue(state.toConfig().isBackgroundEnabled)
    }
}
