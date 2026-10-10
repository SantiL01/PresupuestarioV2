package com.example.presupuestariov2.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ClientDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ClientEntity client);

    @Update
    void update(ClientEntity client);

    @Query("SELECT * FROM clients ORDER BY name COLLATE NOCASE ASC")
    List<ClientEntity> getAll();

    @Query("SELECT * FROM clients WHERE id = :id LIMIT 1")
    ClientEntity getById(long id);

    @Query("SELECT * FROM clients WHERE name = :name LIMIT 1")
    ClientEntity getByName(String name);

    @Query("SELECT COUNT(*) FROM clients")
    int count();
}
