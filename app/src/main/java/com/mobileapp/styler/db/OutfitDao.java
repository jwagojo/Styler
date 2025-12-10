package com.mobileapp.styler.db;

import androidx.room.Dao;
import androidx.room.Insert;

@Dao
public interface OutfitDao {
    @Insert
    void insert(Outfit outfit);
}
