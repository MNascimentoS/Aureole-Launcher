package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.db.FolderDao
import dev.mnascimentos.aureole.core.data.model.AppFolder
import dev.mnascimentos.aureole.core.data.model.FolderDetails
import dev.mnascimentos.aureole.core.data.model.FolderDetailsUpdate
import dev.mnascimentos.aureole.core.data.model.LegacyFolderJson
import dev.mnascimentos.aureole.core.data.model.details
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class FolderRepository(
    private val folderDao: FolderDao,
    private val context: Context? = null
) {
    constructor(context: Context) : this(
        AppDatabase.getInstance(context).folderDao(),
        context
    )

    private val gson = Gson()

    suspend fun getFolders(): List<AppFolder> = withContext(Dispatchers.IO) {
        val dbFolders = folderDao.getAllFoldersWithItems().map { it.toAppFolder() }
        if (dbFolders.isEmpty() && context != null) {
            val legacyFolders = getLegacyFolders(context)
            if (legacyFolders.isNotEmpty()) {
                legacyFolders.forEach { folder ->
                    folderDao.insertFolder(folder)
                }
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                    remove(KEY_FOLDERS)
                }
                return@withContext folderDao.getAllFoldersWithItems().map { it.toAppFolder() }
            }
        }
        dbFolders
    }

    suspend fun addFolder(folder: AppFolder): AppFolder = withContext(Dispatchers.IO) {
        folderDao.insertFolder(folder)
        folder
    }

    suspend fun updateFolder(folder: AppFolder) = withContext(Dispatchers.IO) {
        writeDetails(folder.id, folder.details())
        folderDao.updateFolderItems(folder.id, folder.appPackageNames)
    }

    /** Saves the name, icon, kind (icon, letter or emoji), emoji and own color of the folder. */
    suspend fun updateFolderDetails(folderId: String, details: FolderDetails) = withContext(Dispatchers.IO) {
        writeDetails(folderId, details)
    }

    suspend fun renameFolder(folderId: String, newName: String) = withContext(Dispatchers.IO) {
        folderDao.renameFolder(folderId, newName)
    }

    private suspend fun writeDetails(folderId: String, details: FolderDetails) {
        folderDao.updateFolderDetails(FolderDetailsUpdate.of(folderId, details))
    }

    suspend fun updateFolderApps(folderId: String, packageNames: List<String>) = withContext(Dispatchers.IO) {
        folderDao.updateFolderItems(folderId, packageNames)
    }

    suspend fun deleteFolder(folderId: String) = withContext(Dispatchers.IO) {
        folderDao.deleteFolder(folderId)
    }

    private fun getLegacyFolders(ctx: Context): List<AppFolder> {
        val prefs = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FOLDERS, null)
        if (json.isNullOrBlank()) return emptyList()

        return try {
            // Read through a nullable mirror: Gson skips Kotlin defaults, so fields the old JSON lacks (the folder
            // look) would arrive as null in non-null properties.
            val type = object : TypeToken<List<LegacyFolderJson>>() {}.type
            gson.fromJson<List<LegacyFolderJson>>(json, type)?.map { it.toAppFolder() }.orEmpty()
        } catch (e: JsonSyntaxException) {
            Timber.tag("FolderRepository").e(e, "Failed to parse legacy folders JSON")
            emptyList()
        }
    }

    companion object {
        private const val PREFS_NAME = "aureole_folders_prefs"
        private const val KEY_FOLDERS = "folders_list"
    }
}
