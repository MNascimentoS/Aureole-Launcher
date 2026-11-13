package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
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

/** Removing a container (and the undo that writes its snapshot back), as `dropContainers` does it. */
@RunWith(RobolectricTestRunner::class)
class ContainerRepositoryDropRestoreTest {

    private lateinit var repository: ContainerRepository

    @Before
    fun setUp() {
        val context: Context = RuntimeEnvironment.getApplication()
        repository = ContainerRepository(context)
    }

    @After
    fun tearDown() = runBlocking {
        // The Room database is shared by every test class in the JVM.
        repository.deleteContainer(FIRST)
        repository.deleteContainer(SECOND)
    }

    private suspend fun filledContainer(id: String, packageName: String) {
        repository.ensureContainerExists(id)
        repository.addFolderToPanel(id, AppFolder(id = "folder-$id", name = "Pasta $id"))
        val current = requireNotNull(repository.getContainer(id))
        val app = ContainerItemEntity(
            id = "app-$id",
            panelId = id,
            itemType = ContainerItemType.APP,
            packageName = packageName,
            orderIndex = current.items.size
        )
        repository.saveContainer(current.copy(title = "Titulo $id", items = current.items + app))
    }

    @Test
    fun `dropping a container leaves nothing of it in the database`() = runBlocking {
        filledContainer(FIRST, "app.one")

        repository.deleteContainer(FIRST)

        assertNull(repository.getContainer(FIRST))
        assertTrue(repository.getAllContainers().none { it.id == FIRST })
    }

    @Test
    fun `restoring the snapshot brings back the same items, folders and config`() = runBlocking {
        filledContainer(FIRST, "app.one")
        val snapshot = requireNotNull(repository.getContainer(FIRST))

        repository.deleteContainer(FIRST)
        repository.saveContainer(snapshot)

        assertEquals(snapshot, repository.getContainer(FIRST))
        assertEquals(listOf("folder-$FIRST"), repository.getContainer(FIRST)?.folders?.map { it.id })
        assertEquals(listOf("app.one"), repository.getContainer(FIRST)?.items?.mapNotNull { it.packageName })
    }

    @Test
    fun `dropping one container does not touch another`() = runBlocking {
        filledContainer(FIRST, "app.one")
        filledContainer(SECOND, "app.two")
        val untouched = requireNotNull(repository.getContainer(SECOND))

        repository.deleteContainer(FIRST)

        assertEquals(untouched, repository.getContainer(SECOND))
    }

    @Test
    fun `a new container starts with the given position and expand cell`() = runBlocking {
        val created = repository.ensureContainerExists(FIRST, position = "Center", isExpandCell = true)

        assertEquals("Center", created.position)
        assertTrue(created.isExpandCell)
        assertTrue(requireNotNull(repository.getContainer(FIRST)).items.isEmpty())
    }

    private companion object {
        const val FIRST = "drop_test_first"
        const val SECOND = "drop_test_second"
    }
}
