package com.example.presupuestariov2.model;

/**
 * Representa un cliente en la pantalla "Clientes": su contacto y un
 * resumen de su actividad (presupuestos, facturas, total facturado).
 */
public class Client {

    private final String name;
    private final String email;
    private final int budgetsCount;
    private final int invoicesCount;
    private final double totalBilled;

    public Client(String name, String email, int budgetsCount,
                  int invoicesCount, double totalBilled) {
        this.name = name;
        this.email = email;
        this.budgetsCount = budgetsCount;
        this.invoicesCount = invoicesCount;
        this.totalBilled = totalBilled;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getBudgetsCount() {
        return budgetsCount;
    }

    public int getInvoicesCount() {
        return invoicesCount;
    }

    public double getTotalBilled() {
        return totalBilled;
    }

    /** Iniciales para el avatar circular, ej: "Pedro García" -> "PG". */
    public String getInitials() {
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
}
