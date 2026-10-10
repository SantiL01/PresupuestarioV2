package com.example.presupuestariov2.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "budgets",
        foreignKeys = @ForeignKey(entity = ClientEntity.class,
                parentColumns = "id", childColumns = "clientId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("clientId"), @Index(value = "code", unique = true)})
public class BudgetEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @ColumnInfo(name = "code")
    public String code;

    @ColumnInfo(name = "clientId")
    public long clientId;

    @ColumnInfo(name = "date")
    public long date;

    @ColumnInfo(name = "descripcion_servicio")
    public String descripcionServicio;

    @ColumnInfo(name = "total_amount")
    public double totalAmount;

    @ColumnInfo(name = "status")
    public String status;

    @ColumnInfo(name = "created_at")
    public long createdAt;
}
