package com.juice_studio.busmurciaapp.local

import androidx.room.*

@Dao
interface PlaceDao {

    @Query("SELECT * FROM PlaceEntity")
    suspend fun getAllPlaces():List<PlaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlace(place: PlaceEntity)


    @Query("DELETE FROM PlaceEntity WHERE id = :placeId")
    fun deletePlace(placeId: Int)
}