package com.example.presupuestariov2.data.modelos;

public class Client {

    private long dbId;
    private final String name;
    private final String email;
    private String phone;
    private final int budgetsCount;
    private final int invoicesCount;
    private final double totalBilled;

    public Client(String name, String email, int budgetsCount,
                  int invoicesCount, double totalBilled) {
        this(name, email, "", budgetsCount, invoicesCount, totalBilled);
    }

    public Client(String name, String email, String phone, int budgetsCount,
                  int invoicesCount, double totalBilled) {
        this.name = name != null ? name : "";
        this.email = email != null ? email : "";
        this.phone = phone != null ? phone : "";
        this.budgetsCount = budgetsCount;
        this.invoicesCount = invoicesCount;
        this.totalBilled = totalBilled;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone != null ? phone : ""; }
    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }
    public int getBudgetsCount() { return budgetsCount; }
    public int getInvoicesCount() { return invoicesCount; }
    public double getTotalBilled() { return totalBilled; }

    public String getInitials() {
        if (name.isEmpty()) return "";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
            if (sb.length() >= 2) break;
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return name;
    }
}