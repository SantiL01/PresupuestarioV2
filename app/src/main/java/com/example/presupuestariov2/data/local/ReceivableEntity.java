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
    private long id;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "clientId")
    private long clientId;

    @ColumnInfo(name = "budgetId")
    private long budgetId;

    @ColumnInfo(name = "amount")
    private double amount;

    @ColumnInfo(name = "due_date")
    private long dueDate;

    @ColumnInfo(name = "status")
    private String status;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public long getClientId() { return clientId; }
    public void setClientId(long clientId) { this.clientId = clientId; }
    public long getBudgetId() { return budgetId; }
    public void setBudgetId(long budgetId) { this.budgetId = budgetId; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public long getDueDate() { return dueDate; }
    public void setDueDate(long dueDate) { this.dueDate = dueDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
