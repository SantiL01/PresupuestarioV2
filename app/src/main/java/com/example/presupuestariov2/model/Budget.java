package com.example.presupuestariov2.model;

/**
 * Representa un Presupuesto en la pantalla "Presupuestos".
 */
public class Budget {

    public enum Status {
        BORRADOR,
        ENVIADO,
        APROBADO,
        RECHAZADO
    }

    private final String id;           // Ej: "#00125"
    private final String clientName;   // Nombre del cliente asociado
    private final String date;         // Fecha de creación/emisión
    private final double totalAmount;  // Monto total

    private Status status;             // Estado del presupuesto

    public Budget(String id, String clientName, String date, double totalAmount, Status status) {
        this.id = id;
        this.clientName = clientName;
        this.date = date;
        this.totalAmount = totalAmount;
        this.status = status;
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
}