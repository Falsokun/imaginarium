package com.example.olesya.boardgames.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import com.example.olesya.boardgames.entity.ImageHolder

@Database(entities = [ImageHolder::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract fun imagesDao(): ImageHolderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
                INSTANCE ?: synchronized(this) {
                    INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
                }

        private fun buildDatabase(context: Context) =
                Room.databaseBuilder(context.applicationContext,
                        AppDatabase::class.java, "cards.db")
                        .allowMainThreadQueries()
                        .build()
    }

}
