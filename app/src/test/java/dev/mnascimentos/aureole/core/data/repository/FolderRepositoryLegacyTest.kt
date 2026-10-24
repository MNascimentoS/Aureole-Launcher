package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.room.Room
import dev.mnascimentos.aureole.core.data.db.AppDatabase
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
class FolderRepositoryLegacyTest {

    private lateinit var context: Context
    private lateinit var database: AppDatabase
    private lateinit var repository: FolderRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        database = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java).allowMainThreadQueries().build()
        repository = FolderRepository(database.folderDao(), context)
    }

    @After
    fun tearDown() {
        legacyPrefs().edit().clear().commit()
        database.close()
    }

    private fun legacyPrefs() = context.getSharedPreferences("aureole_folders_prefs", Context.MODE_PRIVATE)

    @Test
    fun `when there are no folders anywhere, should return an empty list`() = runBlocking {
        assertTrue(repository.getFolders().isEmpty())
    }

    @Test
    fun `when only legacy folders exist, should migrate them and clear the old storage`() = runBlocking {
        legacyPrefs().edit().putString(
            "folders_list",
            """
            [
              {"id":"f1","name":"Trabalho","appPackageNames":["com.mail"]},
              {"id":"f2","name":"Jogos","appPackageNames":[]}
            ]
            """.trimIndent()
        ).commit()

        val folders = repository.getFolders()

        assertEquals(setOf("Trabalho", "Jogos"), folders.map { it.name }.toSet())
        assertNull(legacyPrefs().getString("folders_list", null))
    }

    @Test
    fun `when the legacy data is broken, should ignore it`() = runBlocking {
        legacyPrefs().edit().putString("folders_list", "{not json").commit()

        assertTrue(repository.getFolders().isEmpty())
    }

    @Test
    fun `when the legacy data is an empty list, should keep nothing`() = runBlocking {
        legacyPrefs().edit().putString("folders_list", "[]").commit()

        assertTrue(repository.getFolders().isEmpty())
    }

    @Test
    fun `when there is no context for legacy data, should only read the database`() = runBlocking {
        val withoutContext = FolderRepository(database.folderDao(), null)

        assertTrue(withoutContext.getFolders().isEmpty())
    }
}
