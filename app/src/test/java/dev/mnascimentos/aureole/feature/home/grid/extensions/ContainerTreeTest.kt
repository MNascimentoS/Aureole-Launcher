package dev.mnascimentos.aureole.feature.home.grid.extensions

import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import org.junit.Assert.assertEquals
import org.junit.Test

class ContainerTreeTest {

    private fun container(id: String) = LauncherItemState(id = id, type = LauncherItemType.SHORTCUTS_CONTAINER)

    private fun group(id: String, vararg children: LauncherItemState) = LauncherItemState(
        id = id,
        type = LauncherItemType.SCROLL_VIEW,
        children = children.toList()
    )

    @Test
    fun `flattenWithChildren keeps top level items and adds the children of scroll groups`() {
        val items = listOf(container("top"), group("g", container("c1"), group("inner", container("c2"))))

        assertEquals(listOf("top", "g", "c1", "inner", "c2"), items.flattenWithChildren().map { it.id })
    }

    @Test
    fun `flattenWithChildren of an empty list or of items without children is the list itself`() {
        assertEquals(emptyList<LauncherItemState>(), emptyList<LauncherItemState>().flattenWithChildren())
        val items = listOf(container("a"), container("b"))
        assertEquals(items, items.flattenWithChildren())
    }

    @Test
    fun `flattenWithChildren treats null children as none`() {
        val item = LauncherItemState(id = "g", type = LauncherItemType.SCROLL_VIEW, children = null)

        assertEquals(listOf("g"), listOf(item).flattenWithChildren().map { it.id })
    }

    @Test
    fun `containerIds lists only the containers, at any depth`() {
        val items = listOf(
            container("top"),
            LauncherItemState(id = "clock", type = LauncherItemType.CLOCK),
            group("g", container("c1"), LauncherItemState(id = "list", type = LauncherItemType.APPS_LIST))
        )

        assertEquals(listOf("top", "c1"), items.containerIds())
    }

    @Test
    fun `missingContainerIds ignores containers that already exist and repeated ids`() {
        val items = listOf(container("a"), group("g", container("b"), container("a")))

        assertEquals(listOf("b"), items.missingContainerIds(existing = setOf("a")))
        assertEquals(listOf("a", "b"), items.missingContainerIds(existing = emptySet()))
    }
}
