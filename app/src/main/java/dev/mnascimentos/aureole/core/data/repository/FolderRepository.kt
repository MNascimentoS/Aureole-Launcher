package dev.mnascimentos.aureole.core.data.repository

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dev.mnascimentos.aureole.core.data.db.AppDatabase
import dev.mnascimentos.aureole.core.data.db.FolderDao
import dev.mnascimentos.aureole.core.data.model.AppFolder
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
        folderDao.updateFolderDetails(folder.id, folder.name, folder.icon)
        folderDao.updateFolderItems(folder.id, folder.appPackageNames)
    }

    suspend fun updateFolderDetails(folderId: String, newName: String, icon: String?) = withContext(Dispatchers.IO) {
        folderDao.updateFolderDetails(folderId, newName, icon)
    }

    suspend fun renameFolder(folderId: String, newName: String) = withContext(Dispatchers.IO) {
        folderDao.updateFolderDetails(folderId, newName, null)
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
            val type = object : TypeToken<List<AppFolder>>() {}.type
            gson.fromJson<List<AppFolder>>(json, type) ?: emptyList()
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
