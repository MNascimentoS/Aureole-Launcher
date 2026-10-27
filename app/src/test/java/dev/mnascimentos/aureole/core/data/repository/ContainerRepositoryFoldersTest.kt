package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class ContainerRepositoryFoldersTest {

    private lateinit var repository: ContainerRepository

    @Before
    fun setUp() {
        val context: Context = RuntimeEnvironment.getApplication()
        repository = ContainerRepository(context)
    }

    @After
    fun tearDown() = runBlocking {
        // The Room database is shared by every test class in the JVM.
        repository.deleteContainer(PANEL)
    }

    @Test
    fun `when a container does not exist, should return null`() = runBlocking {
        assertNull(repository.getContainer("does_not_exist"))
    }

    @Test
    fun `when ensuring an existing container, should return it unchanged`() = runBlocking {
        repository.ensureContainerExists(PANEL, defaultTitle = "Primeiro")

        val again = repository.ensureContainerExists(PANEL, defaultTitle = "Outro")

        assertEquals("Primeiro", again.title)
    }

    @Test
    fun `when adding a folder to a panel that does not exist yet, should create both`() = runBlocking {
        repository.addFolderToPanel(PANEL, AppFolder(id = FOLDER, name = "Trabalho"))

        val panel = requireNotNull(repository.getContainer(PANEL))
        assertEquals(listOf(FOLDER), panel.folders.map { it.id })
        assertEquals(PANEL, panel.folders.single().panelId)
        assertEquals(1, panel.items.count { it.itemType == ContainerItemType.FOLDER && it.folderId == FOLDER })
    }

    @Test
    fun `when the same folder is added twice, should not duplicate its item`() = runBlocking {
        repository.addFolderToPanel(PANEL, AppFolder(id = FOLDER, name = "Trabalho"))

        repository.addFolderToPanel(PANEL, AppFolder(id = FOLDER, name = "Trabalho renomeada"))

        val panel = requireNotNull(repository.getContainer(PANEL))
        assertEquals(1, panel.items.count { it.folderId == FOLDER })
        assertEquals("Trabalho renomeada", panel.folders.single().name)
    }

    @Test
    fun `when a folder is removed from a panel, should drop it`() = runBlocking {
        repository.addFolderToPanel(PANEL, AppFolder(id = FOLDER, name = "Trabalho"))

        repository.deleteFolderFromPanel(PANEL, FOLDER)

        assertTrue(requireNotNull(repository.getContainer(PANEL)).folders.isEmpty())
    }

    @Test
    fun `when removing a folder from a missing panel, should do nothing`() = runBlocking {
        repository.deleteFolderFromPanel("missing_panel", "missing_folder")

        assertNull(repository.getContainer("missing_panel"))
    }

    @Test
    fun `when a container is deleted, should delete its folders too`() = runBlocking {
        repository.addFolderToPanel(PANEL, AppFolder(id = FOLDER, name = "Trabalho"))

        repository.deleteContainer(PANEL)

        assertNull(repository.getContainer(PANEL))
        assertTrue(repository.getAllContainers().flatMap { it.folders }.none { it.id == FOLDER })
    }

    private companion object {
        const val PANEL = "panel_repo_test"
        const val FOLDER = "folder_repo_test"
    }
}
