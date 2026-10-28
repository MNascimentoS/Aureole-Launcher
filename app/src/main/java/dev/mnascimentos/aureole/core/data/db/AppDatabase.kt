package dev.mnascimentos.aureole.core.data.db

import android.content.Context
import androidx.annotation.VisibleForTesting
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
    exportSchema = true
)
@TypeConverters(RoomConverters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun appInfoDao(): AppInfoDao
    abstract fun containerDao(): ContainerDao
    abstract fun favoriteContainerDao(): FavoriteContainerDao
    abstract fun folderDao(): FolderDao

    companion object {
        private const val DATABASE_NAME = "aureole_app_database.db"

        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context, DATABASE_NAME).also { instance = it }
            }
        }

        @VisibleForTesting
        internal fun buildDatabase(context: Context, name: String): AppDatabase =
            Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, name)
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
    }
}
