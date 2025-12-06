package com.mobileapp.styler.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "items")
public class Item {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String name;
    public String type;
    public String imagePath;
}
