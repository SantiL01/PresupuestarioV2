package com.example.presupuestariov2.data.local;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ReceivableDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(ReceivableEntity receivable);

    @Update
    void update(ReceivableEntity receivable);

    @Query("UPDATE receivables SET status = :status WHERE id = :receivableDbId")
    void updateStatus(long receivableDbId, String status);

    @Query("SELECT * FROM receivables ORDER BY id DESC")
    List<ReceivableEntity> getAll();

    @Query("SELECT * FROM receivables WHERE budgetId = :budgetDbId LIMIT 1")
    ReceivableEntity getByBudgetId(long budgetDbId);

    @Query("SELECT COUNT(*) FROM receivables")
    int count();
}
