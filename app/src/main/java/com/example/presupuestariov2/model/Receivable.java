package com.example.presupuestariov2.model;

/**
 * Representa una fila de la sección "Por cobrar": un presupuesto/factura
 * pendiente o vencido asociado a un cliente.
 */
public class Receivable {

    public enum Status {
        PENDIENTE,
        VENCIDO,
        PAGADO
    }

    private final String clientName;   // "Pedro García"
    private final String code;         // "#00087"
    private final String dueDateText;  // "Vence 30 Ago 2026"
    private final double amount;       // 450000
    private final Status status;

    public Receivable(String clientName, String code, String dueDateText,
                       double amount, Status status) {
        this.clientName = clientName;
        this.code = code;
        this.dueDateText = dueDateText;
        this.amount = amount;
        this.status = status;
    }

    public String getClientName() {
        return clientName;
    }

    public String getCode() {
        return code;
    }

    public String getDueDateText() {
        return dueDateText;
    }

    public double getAmount() {
        return amount;
    }

    public Status getStatus() {
        return status;
    }
}
