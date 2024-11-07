package com.example.agapimenatragoudia.data.entity


import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context

@Database(entities = [SongEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songDao(): SongDao
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "song_database"
                ).fallbackToDestructiveMigration()
                   .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
