package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.mnascimentos.aureole.R
import dev.mnascimentos.aureole.composable.dialogs.model.FolderFormMode
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pure rules of the group sheet (grupo-apps-pastas RF-B4, RF-B7, RF-B8, RF-C4, RF-D2). */
class GroupSheetModelsTest {

    @Test
    fun `the alignment labels should follow the position of the group`() {
        assertEquals(R.string.home_container_group_align_top, alignmentLabelRes(GroupAlignment.START, isLyingDown = false))
        assertEquals(R.string.home_container_group_align_bottom, alignmentLabelRes(GroupAlignment.END, isLyingDown = false))
        assertEquals(R.string.home_container_group_align_left, alignmentLabelRes(GroupAlignment.START, isLyingDown = true))
        assertEquals(R.string.home_container_group_align_right, alignmentLabelRes(GroupAlignment.END, isLyingDown = true))
        assertEquals(R.string.home_container_group_align_center, alignmentLabelRes(GroupAlignment.CENTER, isLyingDown = true))
    }

    @Test
    fun `an old spread alignment should keep its value with no option marked`() {
        assertEquals(0, alignmentIndexOf("Top"))
        assertEquals(2, alignmentIndexOf("Bottom"))
        assertEquals(-1, alignmentIndexOf("Space Between"))
        assertEquals(-1, alignmentIndexOf("Space Evenly"))
    }

    @Test
    fun `the orientation should read and write standing and lying down`() {
        assertTrue(isLyingDown("Horizontal"))
        assertFalse(isLyingDown("Vertical"))
        assertEquals("Horizontal", orientationOf(isLyingDown = true))
        assertEquals("Vertical", orientationOf(isLyingDown = false))
    }

    @Test
    fun `the preview should show three slots in an empty group and one after the items`() {
        assertEquals(3, addSlotCountFor(0))
        assertEquals(1, addSlotCountFor(4))
    }

    @Test
    fun `a new folder should need two apps or more`() {
        assertFalse(canCreateFolder(0))
        assertFalse(canCreateFolder(1))
        assertTrue(canCreateFolder(2))
        assertEquals(listOf("a", "b"), toggledSelection(listOf("a"), "b"))
        assertEquals(listOf("b"), toggledSelection(listOf("a", "b"), "a"))
    }

    @Test
    fun `closing the sheet should pick the snackbar for what happened`() {
        val before = ContainerModel(id = "p")
        val withItem = before.copy(items = listOf(item()))

        assertEquals(GroupCloseOutcome.Unchanged, groupCloseOutcome(before, before, isNew = false))
        assertEquals(GroupCloseOutcome.Updated(before), groupCloseOutcome(before, withItem, isNew = false))
        assertEquals(GroupCloseOutcome.Added, groupCloseOutcome(before, withItem, isNew = true))
        assertEquals(GroupCloseOutcome.DiscardedEmpty, groupCloseOutcome(before, before, isNew = true))
        assertEquals(GroupCloseOutcome.Unchanged, groupCloseOutcome(null, withItem, isNew = false))
    }

    @Test
    fun `back from a page should return to the tab it came from`() {
        val nav = GroupSheetNav()
        nav.selectTab(GroupTab.APPEARANCE)
        nav.open(GroupSheetPage.Color(GroupColorTarget.ITEM))

        assertTrue(nav.isOnPage)
        assertTrue(nav.back())
        assertEquals(GroupSheetPage.Tabs, nav.page)
        assertEquals(GroupTab.APPEARANCE, nav.tab)
        assertFalse(nav.back())
    }

    @Test
    fun `opening New folder should start an empty draft`() {
        val nav = GroupSheetNav()
        nav.open(GroupSheetPage.NewFolder)
        nav.newFolder = NewFolderDraft(name = "Work", apps = listOf("a"))
        nav.back()

        nav.open(GroupSheetPage.NewFolder)

        assertEquals(NewFolderDraft(), nav.newFolder)
    }

    @Test
    fun `back from the icon grid of a folder should return to its form first`() {
        val nav = GroupSheetNav()
        nav.openFolder(AppFolder(id = "f", name = "Work"))
        nav.folderMode = FolderFormMode.ALL_ICONS

        assertTrue(nav.back())
        assertEquals(GroupSheetPage.FolderEditor("f"), nav.page)
        assertEquals(FolderFormMode.FORM, nav.folderMode)
        assertTrue(nav.back())
        assertEquals(GroupSheetPage.Tabs, nav.page)
    }

    private fun item() = ContainerItemEntity(
        id = "i",
        panelId = "p",
        itemType = ContainerItemType.APP,
        packageName = "pkg",
        orderIndex = 0
    )
}
