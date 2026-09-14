package com.example.presupuestariov2.model;

/**
 * Representa una fila de la sección "Actividad reciente" del Home.
 * Ejemplos: "Pedro aprobó Presupuesto #00124", "Recibiste $350.000", etc.
 */
public class ActivityItem {

    // Tipos de actividad -> definen qué ícono se muestra en la lista
    public enum Type {
        APROBADO,   // check verde: presupuesto aprobado
        PAGO,       // check verde: pago recibido
        VISTO,      // ojo: alguien visualizó un documento
        ALERTA      // triángulo de advertencia: vencimientos, etc.
    }

    private final Type type;
    private final String description; // "Pedro aprobó Presupuesto #00124"
    private final String timeAgo;      // "Hace 1h", "Ayer"

    public ActivityItem(Type type, String description, String timeAgo) {
        this.type = type;
        this.description = description;
        this.timeAgo = timeAgo;
    }

    public Type getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeAgo() {
        return timeAgo;
    }
}
