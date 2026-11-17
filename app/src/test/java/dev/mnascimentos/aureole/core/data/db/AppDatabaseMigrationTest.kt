package dev.mnascimentos.aureole.core.data.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemType
import dev.mnascimentos.aureole.core.data.model.FolderEntity
import dev.mnascimentos.aureole.core.data.model.FolderIconType
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRef
import dev.mnascimentos.aureole.core.designsystem.palette.ThemeColorRole
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

    @Test
    fun `when upgrading from version 3, should keep folders as icon folders without own color`() {
        seedVersion3Database()

        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        val folder = runBlocking { database.folderDao().getAllFoldersWithItems().single().toAppFolder() }
        database.close()

        assertEquals(FOLDER_ID, folder.id)
        assertEquals("Social", folder.name)
        assertEquals("chat", folder.icon)
        assertEquals(FolderIconType.ICON, folder.iconType)
        assertNull(folder.emoji)
        assertNull(folder.color)
    }

    @Test
    fun `when upgraded from version 3, should store folders with a type, an emoji and a theme color`() {
        seedVersion3Database()

        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        val stored = runBlocking {
            val dao = database.folderDao()
            dao.insertFolder(
                AppFolder(
                    id = FOLDER_ID,
                    name = "Fun",
                    iconType = FolderIconType.EMOJI,
                    emoji = "\uD83C\uDF89",
                    color = ThemeColorRef.Role(ThemeColorRole.VARIANT)
                )
            )
            dao.getAllFoldersWithItems().single().toAppFolder()
        }
        database.close()

        assertEquals(FolderIconType.EMOJI, stored.iconType)
        assertEquals("\uD83C\uDF89", stored.emoji)
        assertEquals(ThemeColorRef.Role(ThemeColorRole.VARIANT), stored.color)
    }

    @Test
    fun `when upgrading from version 2, should also add the folder style columns`() {
        seedVersion2Database()

        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        val folders = runBlocking { database.folderDao().getAllFoldersWithItems() }
        database.close()

        assertEquals(listOf(FolderIconType.ICON), folders.map { it.folder.iconType })
    }

    /** A 0.9.x install: the current schema rolled back to version 3, with one folder in it. */
    private fun seedVersion3Database() {
        val database = AppDatabase.buildDatabase(context, TEST_DATABASE)
        runBlocking {
            database.folderDao().insertFolderEntity(
                FolderEntity(id = FOLDER_ID, name = "Social", icon = "chat", displayAsGrid = true)
            )
        }
        database.close()
        rollBackToVersion3()
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
            database.folderDao().insertFolderEntity(FolderEntity(id = "folder_1", name = "Social"))
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

    // The test SQLite predates ALTER TABLE DROP COLUMN, so each table is rebuilt from Room's own DDL.
    private fun rollBackToVersion2() {
        rollBackToVersion3()
        val path = context.getDatabasePath(TEST_DATABASE).path
        SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE).use { raw ->
            raw.rebuildWithoutColumns("container_items", VERSION_3_COLUMNS, VERSION_2_COLUMNS)
            raw.version = 2
        }
    }

    /** Drops the columns version 4 added to `folders` and stamps the file as version 3. */
    private fun rollBackToVersion3() {
        val path = context.getDatabasePath(TEST_DATABASE).path
        SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READWRITE).use { raw ->
            raw.rebuildWithoutColumns("folders", VERSION_4_COLUMNS, VERSION_3_FOLDER_COLUMNS)
            raw.version = 3
        }
    }

    private fun SQLiteDatabase.rebuildWithoutColumns(table: String, removed: List<String>, kept: String) {
        val tableSql = schemaSql(table, type = "table").single()
        val indexSqls = schemaSql(table, type = "index")
        check(removed.all { it in tableSql }) { "Unexpected $table schema: $tableSql" }
        val oldTableSql = removed.fold(tableSql) { sql, column -> sql.replace(column, "") }
            .replaceFirst("`$table`", "`${table}_old`")

        execSQL(oldTableSql)
        execSQL("INSERT INTO ${table}_old ($kept) SELECT $kept FROM $table")
        execSQL("DROP TABLE $table")
        execSQL("ALTER TABLE ${table}_old RENAME TO $table")
        indexSqls.forEach { execSQL(it) }
    }

    private fun SQLiteDatabase.schemaSql(table: String, type: String): List<String> =
        rawQuery(
            "SELECT sql FROM sqlite_master WHERE type = ? AND tbl_name = ? AND sql IS NOT NULL",
            arrayOf(type, table)
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
        const val FOLDER_ID = "folder_v3"
        const val APP_ITEM_ID = "item_app"
        const val FOLDER_ITEM_ID = "item_folder"
        const val QUICK_BUTTON_ITEM_ID = "item_quick_button"
        const val VERSION_2_COLUMNS = "id, panel_id, item_type, package_name, folder_id, order_index"
        const val VERSION_3_FOLDER_COLUMNS =
            "id, panel_id, name, color, icon_fallback, icon, display_as_grid, created_at"
        val VERSION_3_COLUMNS = listOf("`quick_button_id` TEXT, ", "`quick_button_json` TEXT, ")
        val VERSION_4_COLUMNS = listOf("`icon_type` TEXT NOT NULL DEFAULT 'ICON', ", "`emoji` TEXT, ")
    }
}
