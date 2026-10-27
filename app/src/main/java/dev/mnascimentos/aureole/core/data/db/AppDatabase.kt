package dev.mnascimentos.aureole.core.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.mnascimentos.aureole.core.data.model.ContainerEntity
import dev.mnascimentos.aureole.core.data.model.ContainerItemEntity
import dev.mnascimentos.aureole.core.data.model.FolderEntity
import dev.mnascimentos.aureole.core.data.model.FolderItemEntity

@Database(
    entities = [
        AppInfoEntity::class,
        ContainerEntity::class,
        ContainerItemEntity::class,
        FavoriteContainerEntity::class,
        ContainerFavoriteItemEntity::class,
        FolderEntity::class,
        FolderItemEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao
    abstract fun containerDao(): ContainerDao
    abstract fun favoriteContainerDao(): FavoriteContainerDao
    abstract fun folderDao(): FolderDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aureole_app_database.db",
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build().also { instance = it }
            }
        }
    }
}
