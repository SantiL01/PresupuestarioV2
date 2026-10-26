package com.example.presupuestariov2.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email COLLATE NOCASE LIMIT 1")
    UserEntity getByEmail(String email);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    UserEntity getById(long id);
}
