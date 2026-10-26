package com.example.presupuestariov2.data.modelos;

public class User {

    private long dbId;
    private final String name;
    private final String email;

    public User(String name, String email) {
        this.name = name != null ? name : "";
        this.email = email != null ? email : "";
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public long getDbId() { return dbId; }
    public void setDbId(long dbId) { this.dbId = dbId; }

    public String getInitials() {
        if (name.isEmpty()) return "";
        String[] parts = name.trim().split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length && sb.length() < 2; i++) {
            if (!parts[i].isEmpty()) sb.append(Character.toUpperCase(parts[i].charAt(0)));
        }
        return sb.toString();
    }
}
