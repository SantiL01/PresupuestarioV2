package com.example.presupuestariov2.data.modelos;

public class Receivable {

    public enum Status {
        PENDIENTE,
        VENCIDO,
        PAGADO
    }

    private long dbId;
    private long clientId;
    private long budgetId;
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
    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }
    public long getClientId() { return clientId; }
    public void setClientId(long clientId) { this.clientId = clientId; }
    public long getBudgetId() { return budgetId; }
    public void setBudgetId(long budgetId) { this.budgetId = budgetId; }

    public void setStatus(Status status) {
        if (status != null) {
            this.status = status;
        }
    }
}