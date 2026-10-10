package com.example.presupuestariov2.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "clients",
        indices = {@Index(value = "email", unique = true)})
public class ClientEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "email")
    public String email;

    @ColumnInfo(name = "phone", defaultValue = "")
    public String phone;

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
