package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Note::class, Category::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract val noteDao: NoteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "noteman_database"
                )
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed default categories for instant user-friendliness
                        CoroutineScope(Dispatchers.IO).launch {
                            val database = getDatabase(context)
                            database.noteDao.insertCategory(Category(name = "Work"))
                            database.noteDao.insertCategory(Category(name = "Personal"))
                            database.noteDao.insertCategory(Category(name = "Study"))
                            database.noteDao.insertCategory(Category(name = "Ideas"))
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
