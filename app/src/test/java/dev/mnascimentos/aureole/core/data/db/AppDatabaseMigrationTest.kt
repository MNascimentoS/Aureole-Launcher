package dev.mnascimentos.aureole.core.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class AppDatabaseMigrationTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        context.deleteDatabase(TEST_DATABASE)
    }

    @After
    fun tearDown() {
        context.deleteDatabase(TEST_DATABASE)
    }

    @Test
    fun `when upgrading from version 2, should keep containers and their items`() {
        seedVersion2Database()

        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        val (container, items) = runBlocking {
            val dao = database.containerDao()
            dao.getContainer(PANEL_ID) to dao.getItemsForPanel(PANEL_ID)
        }
        database.close()

        assertNotNull(container)
        assertEquals(listOf(APP_ITEM_ID, FOLDER_ITEM_ID), items.map { it.id })
        assertEquals("com.example.camera", items.first().packageName)
        assertNull(items.first().quickButtonId)
        assertNull(items.first().quickButtonJson)
    }

    @Test
    fun `when upgraded from version 2, should accept quick buttons inside containers`() {
        seedVersion2Database()

        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        val stored = runBlocking {
            val dao = database.containerDao()
            dao.insertContainerItems(listOf(quickButtonItem()))
            dao.getItemsForPanel(PANEL_ID).last()
        }
        database.close()

        assertEquals(QUICK_BUTTON_ITEM_ID, stored.id)
        assertEquals("qb_1", stored.quickButtonId)
        assertEquals("{}", stored.quickButtonJson)
    }

    /**
     * Writes data with the current schema, then rolls the file back to version 2 by rebuilding
     * container_items without the columns version 3 added: what a 0.8.26 install has on disk.
     */
    private fun seedVersion2Database() {
        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        runBlocking {
            val dao = database.containerDao()
            dao.insertOrUpdateContainer(ContainerEntity(id = PANEL_ID))
            dao.insertContainerItems(
                listOf(
                    ContainerItemEntity(
                        id = APP_ITEM_ID,
                        panelId = PANEL_ID,
                        itemType = ContainerItemType.APP,
                        packageName = "com.example.camera",
                        orderIndex = 0
                    ),
                    ContainerItemEntity(
                        id = FOLDER_ITEM_ID,
                        panelId = PANEL_ID,
                        itemType = ContainerItemType.FOLDER,
                        folderId = "folder_1",
                        orderIndex = 1
                    )
                )
            )
        }
        database.close()
        rollBackToVersion2()
    }

    // The test SQLite predates ALTER TABLE DROP COLUMN, so the table is rebuilt from Room's own DDL.
    private fun rollBackToVersion2() {
        val path = context.getDatabasePath(TEST_DATABASE).path
        SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE).use { raw ->
            val tableSql = raw.schemaSql(type = "table").single()
            val indexSqls = raw.schemaSql(type = "index")
            check(VERSION_3_COLUMNS.all { it in tableSql }) { "Unexpected container_items schema: $tableSql" }
            val version2TableSql = VERSION_3_COLUMNS.fold(tableSql) { sql, column -> sql.replace(column, "") }

            raw.execSQL("ALTER TABLE container_items RENAME TO container_items_v3")
            raw.execSQL(version2TableSql)
            raw.execSQL(
                "INSERT INTO container_items ($VERSION_2_COLUMNS) SELECT $VERSION_2_COLUMNS FROM container_items_v3"
            )
            raw.execSQL("DROP TABLE container_items_v3")
            indexSqls.forEach { raw.execSQL(it) }
            raw.version = 2
        }
    }

    private fun SQLiteDatabase.schemaSql(type: String): List<String> =
        rawQuery(
            "SELECT sql FROM sqlite_master WHERE type = ? AND tbl_name = 'container_items' AND sql IS NOT NULL",
            arrayOf(type)
        ).use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(cursor.getString(0))
            }
        }

    private fun quickButtonItem() = ContainerItemEntity(
        id = QUICK_BUTTON_ITEM_ID,
        panelId = PANEL_ID,
        itemType = ContainerItemType.QUICK_BUTTON,
        quickButtonId = "qb_1",
        quickButtonJson = "{}",
        orderIndex = 2
    )

    private companion object {
        const val TEST_DATABASE = "migration_test.db"
        const val PANEL_ID = "panel_1"
        const val APP_ITEM_ID = "item_app"
        const val FOLDER_ITEM_ID = "item_folder"
        const val QUICK_BUTTON_ITEM_ID = "item_quick_button"
        const val VERSION_2_COLUMNS = "id, panel_id, item_type, package_name, folder_id, order_index"
        val VERSION_3_COLUMNS = listOf("`quick_button_id` TEXT, ", "`quick_button_json` TEXT, ")
    }
}
