package dev.mnascimentos.aureole.feature.home.components.containers.model

import dev.chrisbanes.haze.HazeState
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.ContainerModel
import dev.mnascimentos.aureole.core.data.model.LauncherItemState
import dev.mnascimentos.aureole.core.data.model.LauncherItemType
import dev.mnascimentos.aureole.feature.home.model.MainUiState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class ContainerConfigForTest {

    private fun container(id: String) = LauncherItemState(id = id, type = LauncherItemType.SHORTCUTS_CONTAINER)

    private fun model(id: String, vararg packages: String) = ContainerModel(
        id = id,
        title = "Title $id",
        items = packages.mapIndexed { index, pkg ->
            ContainerItemEntity(
                id = "$id-$pkg",
                panelId = id,
                itemType = ContainerItemType.APP,
                packageName = pkg,
                orderIndex = index
            )
        },
        appPackageNames = packages.toList()
    )

    @Test
    fun `loaded container resolves its own model`() {
        val hazeState = HazeState()
        val uiState = MainUiState(
            containers = mapOf("c1" to model("c1", "app.one").copy(position = "Center", showFolderLabels = true)),
            openedFolderId = "f1"
        )

        val config = uiState.containerConfigFor(container("c1"), hazeState)

        assertEquals("c1", config.panelId)
        assertEquals("Title c1", config.title)
        assertEquals(listOf("app.one"), config.appPackageNames)
        assertEquals("Center", config.position)
        assertEquals(true, config.showFolderLabels)
        assertEquals("f1", config.openedFolderId)
        assertEquals(hazeState, config.hazeState)
    }

    @Test
    fun `container without a loaded model starts empty and ignores the global settings`() {
        val uiState = MainUiState(
            containerPosition = "Center",
            isContainerBackgroundEnabled = true,
            isContainerExpandCell = true,
            showFolderLabels = true,
            showContainerAddFolderButton = false
        )
        val initial = ContainerModel(id = "c1")

        val config = uiState.containerConfigFor(container("c1"), hazeState = null)

        assertEquals("c1", config.panelId)
        assertEquals(initial.position, config.position)
        assertEquals(initial.isBackgroundEnabled, config.isBackgroundEnabled)
        assertEquals(initial.isExpandCell, config.isExpandCell)
        assertEquals(initial.showFolderLabels, config.showFolderLabels)
        assertEquals(initial.showAddFolderButton, config.showAddFolderButton)
        assertEquals(emptyList<String>(), config.appPackageNames)
        assertNull(config.hazeState)
    }

    @Test
    fun `two containers inside a scroll group never share panel id, items or config`() {
        val group = LauncherItemState(
            id = "group",
            type = LauncherItemType.SCROLL_VIEW,
            children = listOf(container("c1"), container("c2"))
        )
        val uiState = MainUiState(
            gridItems = listOf(group),
            containers = mapOf(
                "c1" to model("c1", "app.one", "app.two"),
                "c2" to model("c2", "app.three").copy(orientation = "Horizontal")
            )
        )

        val configs = group.safeChildren.map { uiState.containerConfigFor(it, hazeState = null) }

        assertEquals(listOf("c1", "c2"), configs.map { it.panelId })
        assertEquals(listOf("app.one", "app.two"), configs[0].appPackageNames)
        assertEquals(listOf("app.three"), configs[1].appPackageNames)
        assertEquals(listOf("Vertical", "Horizontal"), configs.map { it.orientation })
    }
}
