package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.data.model.details
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
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

        repository.updateFolderDetails("f1", FolderDetails(name = "New Name", icon = "icon1"))
        repository.updateFolderApps("f1", listOf("pkg1", "pkg2"))

        val updated = repository.getFolders().first()
        assertEquals("New Name", updated.name)
        assertEquals("icon1", updated.icon)
        assertEquals(listOf("pkg1", "pkg2"), updated.appPackageNames)

        repository.renameFolder("f1", "Renamed")
        assertEquals("Renamed", repository.getFolders().first().name)
        assertEquals("icon1", repository.getFolders().first().icon)
        repository.updateFolder(folder.copy(name = "Updated"))
        assertEquals("Updated", repository.getFolders().first().name)

        repository.deleteFolder("f1")
        assertTrue(repository.getFolders().isEmpty())
    }

    @Test
    fun `editing saves the kind, emoji and own color, and reads them back`() = runBlocking {
        repository.addFolder(AppFolder(id = "f2", name = "Fun", icon = "star"))
        val details = FolderDetails(
            name = "Games",
            icon = "sports_esports",
            iconType = FolderIconType.EMOJI,
            emoji = "\uD83C\uDFAE",
            color = ThemeColorRef.Role(ThemeColorRole.VARIANT)
        )

        repository.updateFolderDetails("f2", details)
        assertEquals(details, repository.getFolders().first().details())

        val back = FolderDetails(name = "Fun", icon = "star", color = ThemeColorRef.Fixed(0xFF336699.toInt()))
        repository.updateFolderDetails("f2", back)
        assertEquals(back, repository.getFolders().first().details())
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
