package com.example.olesya.boardgames.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.example.olesya.boardgames.models.ImageHolder;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface  ImageHolderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ImageHolder... imgs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(ArrayList<ImageHolder> imgs);

    // Удаление Person из бд
    @Delete
    void delete(ImageHolder img);

    // Получение всех Person из бд
    @Query("SELECT * FROM imageholder")
    List<ImageHolder> getAllImages();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ImageHolder holder);
}
