package com.mobileapp.styler.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "outfits")
public class Outfit {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public String topImageUri;
    public String bottomImageUri;
    public String shoeImageUri;
}
