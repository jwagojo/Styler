package com.mobileapp.styler.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ItemDao {
    @Insert
    void insert(Item item);

    @Delete
    void delete(Item item);

    @Query("DELETE FROM items")
    void deleteAll();

    @Query("SELECT * FROM items")
    LiveData<List<Item>> getAllItems();

    @Query("SELECT * FROM items WHERE type = :itemType")
    LiveData<List<Item>> getItemsByType(String itemType);
}
