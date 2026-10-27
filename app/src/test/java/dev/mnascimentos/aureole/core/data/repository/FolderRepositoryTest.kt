package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.model.AppFolder
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class FolderRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var repository: FolderRepository
    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = FolderRepository(db.folderDao(), context)
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testFolderRepositoryOperations() = runBlocking {
        val folder = AppFolder(id = "f1", name = "Folder 1", appPackageNames = listOf("pkg1"))
        repository.addFolder(folder)

        val folders = repository.getFolders()
        assertEquals(1, folders.size)
        assertEquals("Folder 1", folders.first().name)

        repository.updateFolderDetails("f1", "New Name", "icon1")
        repository.updateFolderApps("f1", listOf("pkg1", "pkg2"))

        val updated = repository.getFolders().first()
        assertEquals("New Name", updated.name)

        repository.renameFolder("f1", "Renamed")
        repository.updateFolder(folder.copy(name = "Updated"))

        repository.deleteFolder("f1")
        assertTrue(repository.getFolders().isEmpty())
    }

    @Test
    fun testLegacyFoldersMigrationAndInvalidJson() = runBlocking {
        val prefs = context.getSharedPreferences("aureole_folders_prefs", Context.MODE_PRIVATE)

        // Invalid JSON
        prefs.edit { putString("folders_list", "{invalid_json") }
        assertTrue(repository.getFolders().isEmpty())

        // Valid Legacy JSON
        val validJson = "[{\"id\":\"legacy1\",\"name\":\"Legacy Folder\",\"appPackageNames\":[\"pkgA\"]}]"
        prefs.edit { putString("folders_list", validJson) }

        val migrated = repository.getFolders()
        assertEquals(1, migrated.size)
        assertEquals("Legacy Folder", migrated.first().name)
    }
}
