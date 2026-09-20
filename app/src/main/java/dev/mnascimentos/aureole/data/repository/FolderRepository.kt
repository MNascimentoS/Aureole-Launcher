package dev.mnascimentos.aureole.data.repository

import android.content.Context
import android.util.Log
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import dev.mnascimentos.aureole.data.db.FolderDatabaseHelper
import dev.mnascimentos.aureole.data.model.AppFolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class FolderRepository(private val context: Context) {
    private val dbHelper = FolderDatabaseHelper(context)
    private val gson = Gson()

    suspend fun getFolders(): List<AppFolder> = withContext(Dispatchers.IO) {
        val dbFolders = dbHelper.getAllFoldersWithItems().map { it.toAppFolder() }
        if (dbFolders.isEmpty()) {
            val legacyFolders = getLegacyFolders()
            if (legacyFolders.isNotEmpty()) {
                legacyFolders.forEach { folder ->
                    dbHelper.insertFolder(folder)
                }
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit {
                    remove(KEY_FOLDERS)
                }
                return@withContext dbHelper.getAllFoldersWithItems().map { it.toAppFolder() }
            }
        }
        dbFolders
    }

    suspend fun addFolder(folder: AppFolder): AppFolder = withContext(Dispatchers.IO) {
        dbHelper.insertFolder(folder)
        folder
    }

    suspend fun updateFolder(folder: AppFolder) = withContext(Dispatchers.IO) {
        dbHelper.renameFolder(folder.id, folder.name)
        dbHelper.updateFolderItems(folder.id, folder.appPackageNames)
    }

    suspend fun renameFolder(folderId: String, newName: String) = withContext(Dispatchers.IO) {
        dbHelper.renameFolder(folderId, newName)
    }

    suspend fun updateFolderApps(folderId: String, packageNames: List<String>) = withContext(Dispatchers.IO) {
        dbHelper.updateFolderItems(folderId, packageNames)
    }

    suspend fun deleteFolder(folderId: String) = withContext(Dispatchers.IO) {
        dbHelper.deleteFolder(folderId)
    }

    private fun getLegacyFolders(): List<AppFolder> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_FOLDERS, null)
        if (json.isNullOrBlank()) return emptyList()

        return try {
            val type = object : TypeToken<List<AppFolder>>() {}.type
            gson.fromJson<List<AppFolder>>(json, type) ?: emptyList()
        } catch (e: JsonSyntaxException) {
            Log.e("FolderRepository", "Failed to parse legacy folders JSON", e)
            emptyList()
        }
    }

    companion object {
        private const val PREFS_NAME = "aureole_folders_prefs"
        private const val KEY_FOLDERS = "folders_list"
    }
}
