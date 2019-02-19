package com.example.olesya.boardgames.database

import androidx.room.*
import com.example.olesya.boardgames.entity.ImageHolder

@Dao
interface ImageHolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(imgs: List<ImageHolder>)

    @Delete
    fun delete(img: ImageHolder)

    @Query("SELECT * FROM imageholder")
    fun getAllImages(): List<ImageHolder>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(holder: ImageHolder)
}
