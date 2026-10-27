package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.model.AppFolder
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ContainerRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: ContainerRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = ContainerRepository(db.containerDao(), db.folderDao())
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testContainerRepositoryOperations() = runBlocking {
        val container = repository.ensureContainerExists("panel_1", "Title 1")
        assertNotNull(container)
        assertEquals("panel_1", container.id)

        val fetched = repository.getContainer("panel_1")
        assertNotNull(fetched)

        val folder = AppFolder(id = "folder_1", name = "Folder 1")
        repository.addFolderToPanel("panel_1", folder)
        val withFolder = repository.getContainer("panel_1")
        assertTrue(withFolder?.folders?.any { it.id == "folder_1" } == true)

        repository.deleteFolderFromPanel("panel_1", "folder_1")
        val withoutFolder = repository.getContainer("panel_1")
        assertTrue(withoutFolder?.folders?.none { it.id == "folder_1" } == true)

        val all = repository.getAllContainers()
        assertTrue(all.isNotEmpty())

        repository.deleteContainer("panel_1")
        assertNull(repository.getContainer("panel_1"))
    }
}
