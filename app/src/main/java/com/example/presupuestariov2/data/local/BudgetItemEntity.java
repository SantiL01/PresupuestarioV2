package com.example.presupuestariov2.data.local;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "budget_items",
        foreignKeys = @ForeignKey(entity = BudgetEntity.class,
                parentColumns = "id", childColumns = "budgetId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("budgetId")})
public class BudgetItemEntity {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "budgetId")
    private long budgetId;

    @ColumnInfo(name = "descripcion")
    private String descripcion;

    @ColumnInfo(name = "cantidad")
    private double cantidad;

    @ColumnInfo(name = "unidad_medida")
    private String unidadMedida;

    @ColumnInfo(name = "precio_unitario")
    private double precioUnitario;

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getBudgetId() { return budgetId; }
    public void setBudgetId(long budgetId) { this.budgetId = budgetId; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public double getCantidad() { return cantidad; }
    public void setCantidad(double cantidad) { this.cantidad = cantidad; }
    public String getUnidadMedida() { return unidadMedida; }
    public void setUnidadMedida(String unidadMedida) { this.unidadMedida = unidadMedida; }
    public double getPrecioUnitario() { return precioUnitario; }
    public void setPrecioUnitario(double precioUnitario) { this.precioUnitario = precioUnitario; }
}
