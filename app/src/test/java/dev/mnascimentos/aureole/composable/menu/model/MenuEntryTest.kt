package dev.mnascimentos.aureole.composable.menu.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MenuEntryTest {

    private fun row(title: String, destructive: Boolean = false) =
        MenuEntry.Row(MenuRowParams(title = title, onClick = {}, isDestructive = destructive))

    private fun titles(entries: List<MenuEntry>) = entries.map {
        when (it) {
            is MenuEntry.Row -> it.params.title
            is MenuEntry.Section -> "section:${it.text}"
            is MenuEntry.Note -> "note:${it.text}"
            is MenuEntry.Content -> "content"
            MenuEntry.Divider -> "divider"
        }
    }

    @Test
    fun `destructive entries go to the end`() {
        val ordered = destructiveLast(listOf(row("Remove", true), row("Rename"), row("Icon")))

        assertEquals(listOf("Rename", "Icon", "Remove"), titles(ordered))
    }

    @Test
    fun `destructiveLast keeps the order inside each group`() {
        val ordered = destructiveLast(
            listOf(row("Uninstall", true), row("A"), row("Remove from folder", true), row("B"), MenuEntry.Section("s"))
        )

        assertEquals(listOf("A", "B", "section:s", "Uninstall", "Remove from folder"), titles(ordered))
    }

    @Test
    fun `destructiveLast leaves a list without destructive entries as it is`() {
        val items = listOf(row("A"), MenuEntry.Note("n"), row("B"))

        assertEquals(items, destructiveLast(items))
        assertEquals(emptyList<MenuEntry>(), destructiveLast(emptyList()))
    }

    @Test
    fun `only rows flagged as destructive are destructive`() {
        assertTrue(row("Remove", true).isDestructive)
        assertFalse(row("Rename").isDestructive)
        assertFalse(MenuEntry.Section("s").isDestructive)
        assertFalse(MenuEntry.Content {}.isDestructive)
        assertFalse(MenuEntry.Divider.isDestructive)
    }

    @Test
    fun `a divider goes right before the destructive group`() {
        val ordered = withDestructiveDivider(listOf(row("Remove", true), row("Rename")))

        assertEquals(listOf("Rename", "divider", "Remove"), titles(ordered))
    }

    @Test
    fun `no divider when nothing is destructive or everything is`() {
        assertEquals(listOf("A", "B"), titles(withDestructiveDivider(listOf(row("A"), row("B")))))
        assertEquals(
            listOf("Remove", "Uninstall"),
            titles(withDestructiveDivider(listOf(row("Remove", true), row("Uninstall", true))))
        )
    }

    @Test
    fun `an existing divider before the destructive group is not doubled`() {
        val ordered = withDestructiveDivider(listOf(row("A"), MenuEntry.Divider, row("Remove", true)))

        assertEquals(listOf("A", "divider", "Remove"), titles(ordered))
    }
}
