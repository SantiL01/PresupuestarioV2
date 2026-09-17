package com.example.presupuestariov2.model;

public class Receivable {

    public enum Status {
        PENDIENTE,
        VENCIDO,
        PAGADO
    }

    private final String clientName;
    private final String code;
    private final String dueDateText;
    private final double amount;
    private Status status;

    public Receivable(String clientName, String code, String dueDateText,
                      double amount, Status status) {
        this.clientName = clientName != null ? clientName : "";
        this.code = code != null ? code : "";
        this.dueDateText = dueDateText != null ? dueDateText : "";
        this.amount = amount;
        this.status = status != null ? status : Status.PENDIENTE;
    }

    public String getClientName() { return clientName; }
    public String getCode() { return code; }
    public String getDueDateText() { return dueDateText; }
    public double getAmount() { return amount; }
    public Status getStatus() { return status; }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }
}