package com.example.presupuestariov2.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "receivables",
        foreignKeys = {
                @ForeignKey(entity = ClientEntity.class,
                        parentColumns = "id", childColumns = "clientId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = BudgetEntity.class,
                        parentColumns = "id", childColumns = "budgetId",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("clientId"),
                @Index(value = "budgetId", unique = true),
                @Index(value = "code", unique = true)})
public class ReceivableEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "code")
    public String code;

    @ColumnInfo(name = "clientId")
    public long clientId;

    // 1 cobro = 1 presupuesto -> FK única
    @ColumnInfo(name = "budgetId")
    public long budgetId;

    @ColumnInfo(name = "amount")
    public double amount;

    @ColumnInfo(name = "due_date")
    public long dueDate;

    @ColumnInfo(name = "status")
    public String status;
}
