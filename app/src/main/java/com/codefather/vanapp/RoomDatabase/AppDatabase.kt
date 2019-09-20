package com.codefather.vanapp.RoomDatabase.RoomDatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import android.content.Context
import com.codefather.vanapp.RoomDatabase.DAOs.ClientDao
import com.codefather.vanapp.RoomDatabase.DAOs.HistoryDao
import com.codefather.vanapp.RoomDatabase.Entities.ClientEntity
import com.codefather.vanapp.RoomDatabase.Entities.HistoryEntity
import com.codefather.vanapp.Utils.Constants

/**
 *
 * Created by Georges Jamous on 05, March, 2019.
 *
 */
@Database(
    entities = [
        ClientEntity::class,
        HistoryEntity::class
    ], version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, Constants.FileConstants.DATABASE_NAME)
                .build()
    }
}