package com.example.presupuestariov2.data.modelos;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
public class Budget {

    public enum Status {
        BORRADOR,
        ENVIADO,
        APROBADO,
        RECHAZADO
    }

    private final String id;           // Código visible Ej: "#00125"
    private long dbId;                 // PK Room
    private long clientId;             // FK Room
    private final String clientName;   // Nombre del cliente asociado (denormalizado para UI)
    private final String date;         // Fecha de creación/emisión (texto visible)
    private final double totalAmount;  // Monto total

    private Status status;             // Estado del presupuesto

    private final String descripcionServicio; // Descripción breve del servicio (texto libre)
    private final List<BudgetItem> items;     // Ítems del presupuesto

    public Budget(String id, String clientName, String date, double totalAmount, Status status) {
        this(id, clientName, date, totalAmount, status, "", new ArrayList<>());
    }

    public Budget(String id, String clientName, String date, double totalAmount, Status status,
                  String descripcionServicio, List<BudgetItem> items) {
        this.id = id;
        this.clientName = clientName;
        this.date = date;
        this.totalAmount = totalAmount;
        this.status = status;
        this.descripcionServicio = descripcionServicio != null ? descripcionServicio : "";
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getClientName() {
        return clientName;
    }

    public String getDate() {
        return date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getDescripcionServicio() {
        return descripcionServicio;
    }

    public List<BudgetItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public List<BudgetItem> getItemsCopy() {
        return new ArrayList<>(items);
    }

    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }
    public long getClientId() { return clientId; }
    public void setClientId(long clientId) { this.clientId = clientId; }
}