package com.juice_studio.busmurciaapp.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PlaceDao {

    @Query("SELECT * FROM PlaceEntity")
    suspend fun getAllPlaces():List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlace(place: PlaceEntity)
}