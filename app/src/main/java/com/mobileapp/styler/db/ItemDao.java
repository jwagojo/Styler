package com.mobileapp.styler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    void insert(Item item);

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getAllItems();
}
