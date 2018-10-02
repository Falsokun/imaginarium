package com.example.olesya.boardgames.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.olesya.boardgames.models.ImageHolder;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface  ImageHolderDao {

    @Insert
    void insertAll(ImageHolder... imgs);

    @Insert
    void insertAll(ArrayList<ImageHolder> imgs);

    // Удаление Person из бд
    @Delete
    void delete(ImageHolder img);

    // Получение всех Person из бд
    @Query("SELECT * FROM imageholder")
    List<ImageHolder> getAllImages();

    @Insert
    void insert(ImageHolder holder);
}
