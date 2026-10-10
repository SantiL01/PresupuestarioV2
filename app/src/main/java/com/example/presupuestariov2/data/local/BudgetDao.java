package com.example.presupuestariov2.data.local;

import androidx.room.Dao;
import androidx.room.Embedded;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Relation;
import androidx.room.Transaction;
import androidx.room.Update;

import java.util.List;

@Dao
public interface BudgetDao {

    class BudgetWithItems {
        @Embedded
        public BudgetEntity budget;

        @Relation(parentColumn = "id", entityColumn = "budgetId")
        public List<BudgetItemEntity> items;
    }

    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insertBudget(BudgetEntity budget);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(List<BudgetItemEntity> items);

    @Update
    void updateBudget(BudgetEntity budget);

    @Query("UPDATE budgets SET status = :status WHERE id = :budgetDbId")
    void updateStatus(long budgetDbId, String status);

    @Query("SELECT * FROM budgets ORDER BY id DESC")
    List<BudgetEntity> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE code = :code LIMIT 1")
    BudgetEntity getByCode(String code);

    @Transaction
    @Query("SELECT * FROM budgets ORDER BY id DESC")
    List<BudgetWithItems> getBudgetsWithItems();

    @Query("SELECT * FROM budget_items WHERE budgetId = :budgetDbId")
    List<BudgetItemEntity> getItemsForBudget(long budgetDbId);

    @Query("SELECT COUNT(*) FROM budgets")
    int count();
}
