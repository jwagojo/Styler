package com.mobileapp.styler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface OutfitDao {
    @Insert
    void insert(Outfit outfit);
    @Query("SELECT * FROM outfits")
    LiveData<List<Outfit>> getAllOutfits();
}
