package com.youngsdeveloper.bus_murcia.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [PlaceEntity::class], version = 2
)
abstract class AppDatabase:RoomDatabase() {

    abstract fun placeDao(): PlaceDao


    companion object{
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context):AppDatabase{
            INSTANCE = INSTANCE ?: Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "data_bus_murcia"
            ).build()
            return INSTANCE!!
        }

        fun destroyInstance(){
            INSTANCE = null
        }
    }
}