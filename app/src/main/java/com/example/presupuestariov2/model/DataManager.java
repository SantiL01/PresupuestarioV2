package com.example.presupuestariov2.model;

import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static volatile DataManager instance;

    private final List<Client> clients;
    private final List<Budget> budgets;
    private final List<Receivable> receivables;

    private DataManager() {
        clients = new ArrayList<>();
        budgets = new ArrayList<>();
        receivables = new ArrayList<>();

        // Datos iniciales de prueba
        clients.add(new Client("Pedro García", "pedro@email.com", 8, 5, 1450000));
        clients.add(new Client("María López", "maria@email.com", 3, 2, 620000));

        budgets.add(new Budget("#00125", "Pedro García", "17 Sep 2026", 450000, Budget.Status.APROBADO));
        receivables.add(new Receivable("Pedro García", "#00087", "Vence 30 Ago 2026", 450000, Receivable.Status.PENDIENTE));
    }

    public static DataManager getInstance() {
        if (instance == null) {
            synchronized (DataManager.class) {
                if (instance == null) {
                    instance = new DataManager();
                }
            }
        }
        return instance;
    }

    public List<Client> getClients() { return clients; }
    public List<Budget> getBudgets() { return budgets; }
    public List<Receivable> getReceivables() { return receivables; }

    public void addClient(Client c) {
        if (c != null) clients.add(0, c);
    }
    public void addBudget(Budget b) {
        if (b != null) budgets.add(0, b);
    }
    public void addReceivable(Receivable r) {
        if (r != null) receivables.add(0, r);
    }
}