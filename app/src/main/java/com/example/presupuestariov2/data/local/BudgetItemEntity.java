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
    public long id;

    @ColumnInfo(name = "budgetId")
    public long budgetId;

    @ColumnInfo(name = "descripcion")
    public String descripcion;

    @ColumnInfo(name = "cantidad")
    public double cantidad;

    @ColumnInfo(name = "unidad_medida")
    public String unidadMedida;

    @ColumnInfo(name = "precio_unitario")
    public double precioUnitario;
}
