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
    private long id;

    @ColumnInfo(name = "code")
    private String code;

    @ColumnInfo(name = "clientId")
    private long clientId;

    @ColumnInfo(name = "date")
    private long date;

    @ColumnInfo(name = "descripcion_servicio")
    private String descripcionServicio;

    @ColumnInfo(name = "total_amount")
    private double totalAmount;

    @ColumnInfo(name = "status")
    private String status;

    @ColumnInfo(name = "created_at")
    private long createdAt;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
    public long getClientId() { return clientId; }
    public void setClientId(long clientId) { this.clientId = clientId; }
    public long getDate() { return date; }
    public void setDate(long date) { this.date = date; }
    public String getDescripcionServicio() { return descripcionServicio; }
    public void setDescripcionServicio(String descripcionServicio) { this.descripcionServicio = descripcionServicio; }
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
