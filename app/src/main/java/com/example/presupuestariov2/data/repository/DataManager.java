package com.example.presupuestariov2.data.repository;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import com.example.presupuestariov2.data.local.AppDatabase;
import com.example.presupuestariov2.data.local.BudgetDao;
import com.example.presupuestariov2.data.local.BudgetEntity;
import com.example.presupuestariov2.data.local.BudgetItemEntity;
import com.example.presupuestariov2.data.local.ClientDao;
import com.example.presupuestariov2.data.local.ClientEntity;
import com.example.presupuestariov2.data.local.ReceivableDao;
import com.example.presupuestariov2.data.local.ReceivableEntity;
import com.example.presupuestariov2.data.modelos.Budget;
import com.example.presupuestariov2.data.modelos.BudgetItem;
import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.modelos.Receivable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DataManager {
    private static volatile DataManager instance;

    private AppDatabase db;
    private boolean initialized = false;

    // Fallback en memoria por si alguien llama sin init (tests)
    private final List<Client> memClients = new ArrayList<>();
    private final List<Budget> memBudgets = new ArrayList<>();
    private final List<Receivable> memReceivables = new ArrayList<>();

    private DataManager() {}

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

    public synchronized void init(Context context) {
        if (initialized) return;
        db = AppDatabase.getInstance(context);
        initialized = true;
        seedIfEmpty();
    }

    public boolean isInitialized() { return initialized; }

    // ---------- Lecturas ----------

    public List<Client> getClients() {
        if (!initialized) return memClients;
        List<ClientEntity> entities = db.clientDao().getAll();
        List<Client> out = new ArrayList<>();
        for (ClientEntity e : entities) {
            int budgetsCount = countBudgetsForClient(e.id);
            double total = sumTotalsForClient(e.id);
            Client c = new Client(e.name, e.email, e.phone != null ? e.phone : "",
                    budgetsCount, 0, total);
            c.setDbId(e.id);
            out.add(c);
        }
        return out;
    }

    public List<Budget> getBudgets() {
        if (!initialized) return memBudgets;
        List<BudgetDao.BudgetWithItems> rows = db.budgetDao().getBudgetsWithItems();
        List<ClientEntity> clients = db.clientDao().getAll();
        List<Budget> out = new ArrayList<>();
        for (BudgetDao.BudgetWithItems row : rows) {
            String clientName = resolveClientName(clients, row.budget.clientId);
            List<BudgetItem> items = new ArrayList<>();
            if (row.items != null) {
                for (BudgetItemEntity ie : row.items) {
                    items.add(new BudgetItem(ie.descripcion, ie.cantidad,
                            ie.unidadMedida, ie.precioUnitario));
                }
            }
            Budget b;
            try {
                b = new Budget(row.budget.code, clientName,
                        formatDate(row.budget.date), row.budget.totalAmount,
                        Budget.Status.valueOf(row.budget.status),
                        row.budget.descripcionServicio, items);
            } catch (Exception e) {
                b = new Budget(row.budget.code, clientName,
                        formatDate(row.budget.date), row.budget.totalAmount,
                        Budget.Status.BORRADOR,
                        row.budget.descripcionServicio, items);
            }
            b.setDbId(row.budget.id);
            b.setClientId(row.budget.clientId);
            out.add(b);
        }
        return out;
    }

    public List<Receivable> getReceivables() {
        if (!initialized) return memReceivables;
        List<ReceivableEntity> entities = db.receivableDao().getAll();
        List<ClientEntity> clients = db.clientDao().getAll();
        List<Receivable> out = new ArrayList<>();
        for (ReceivableEntity e : entities) {
            String clientName = resolveClientName(clients, e.clientId);
            Receivable r;
            try {
                r = new Receivable(clientName, e.code, "Vence " + formatDate(e.dueDate),
                        e.amount, Receivable.Status.valueOf(e.status));
            } catch (Exception ex) {
                r = new Receivable(clientName, e.code, "Vence " + formatDate(e.dueDate),
                        e.amount, Receivable.Status.PENDIENTE);
            }
            r.setDbId(e.id);
            r.setClientId(e.clientId);
            r.setBudgetId(e.budgetId);
            out.add(r);
        }
        return out;
    }

    // ---------- Escrituras ----------

    /** @return dbId o -1 si email duplicado */
    public long addClient(Client c) {
        if (c == null) return -1;
        if (!initialized) {
            memClients.add(0, c);
            return -1;
        }
        ClientEntity e = new ClientEntity();
        e.name = c.getName();
        e.email = c.getEmail();
        e.phone = c.getPhone() != null ? c.getPhone() : "";
        e.createdAt = System.currentTimeMillis();
        try {
            long id = db.clientDao().insert(e);
            c.setDbId(id);
            return id;
        } catch (Exception ex) {
            return -1;
        }
    }

    /** Guarda presupuesto + ítems + crea su cobro 1=1. @return budgetDbId o -1 */
    public long addBudget(Budget b) {
        if (b == null) return -1;
        if (!initialized) {
            memBudgets.add(0, b);
            return -1;
        }
        long clientId = resolveClientIdByName(b.getClientName());
        if (clientId == -1) return -1;

        BudgetEntity be = new BudgetEntity();
        be.code = b.getId();
        be.clientId = clientId;
        be.date = System.currentTimeMillis();
        be.descripcionServicio = b.getDescripcionServicio();
        be.totalAmount = b.getTotalAmount();
        be.status = b.getStatus() != null ? b.getStatus().name() : Budget.Status.BORRADOR.name();
        be.createdAt = System.currentTimeMillis();
        long budgetDbId;
        try {
            budgetDbId = db.budgetDao().insertBudget(be);
        } catch (Exception e) {
            return -1;
        }
        b.setDbId(budgetDbId);
        b.setClientId(clientId);

        List<BudgetItemEntity> itemEntities = new ArrayList<>();
        for (BudgetItem it : b.getItems()) {
            BudgetItemEntity ie = new BudgetItemEntity();
            ie.budgetId = budgetDbId;
            ie.descripcion = it.getDescripcion();
            ie.cantidad = it.getCantidad();
            ie.unidadMedida = it.getUnidadMedida();
            ie.precioUnitario = it.getPrecioUnitario();
            itemEntities.add(ie);
        }
        if (!itemEntities.isEmpty()) {
            db.budgetDao().insertItems(itemEntities);
        }

        // Regla 1 cobro = 1 presupuesto: cobro espejo automático
        ReceivableEntity re = new ReceivableEntity();
        re.code = be.code;
        re.clientId = clientId;
        re.budgetId = budgetDbId;
        re.amount = be.totalAmount;
        re.dueDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000;
        re.status = Receivable.Status.PENDIENTE.name();
        try {
            db.receivableDao().insert(re);
        } catch (Exception ignored) {
            // Si el código ya existía como cobro manual, se mantiene el existente
        }
        return budgetDbId;
    }

    /** Cobro manual. Si el código coincide con un presupuesto, lo vincula (respeta 1=1). */
    public long addReceivable(Receivable r) {
        if (r == null) return -1;
        if (!initialized) {
            memReceivables.add(0, r);
            return -1;
        }
        long clientId = resolveClientIdByName(r.getClientName());
        if (clientId == -1) return -1;
        BudgetEntity linked = db.budgetDao().getByCode(r.getCode());
        long budgetId = linked != null ? linked.id : -1;

        ReceivableEntity e = new ReceivableEntity();
        e.code = r.getCode();
        e.clientId = clientId;
        e.budgetId = budgetId != -1 ? budgetId : 0;
        // Si no hay presupuesto vinculado, no se puede cumplir FK -> se omite cobro huérfano
        if (budgetId == -1) return -1;
        e.amount = r.getAmount();
        e.dueDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000;
        e.status = r.getStatus() != null ? r.getStatus().name() : Receivable.Status.PENDIENTE.name();
        try {
            long id = db.receivableDao().insert(e);
            r.setDbId(id);
            r.setClientId(clientId);
            r.setBudgetId(budgetId);
            return id;
        } catch (Exception ex) {
            return -1;
        }
    }

    public void updateBudgetStatus(Budget b) {
        if (b == null || !initialized || b.getDbId() == 0) return;
        db.budgetDao().updateStatus(b.getDbId(),
                b.getStatus() != null ? b.getStatus().name() : Budget.Status.BORRADOR.name());
    }

    public void updateReceivableStatus(Receivable r) {
        if (r == null || !initialized || r.getDbId() == 0) return;
        db.receivableDao().updateStatus(r.getDbId(),
                r.getStatus() != null ? r.getStatus().name() : Receivable.Status.PENDIENTE.name());
    }

    // ---------- Helpers ----------

    private long resolveClientIdByName(String name) {
        if (name == null) return -1;
        ClientEntity e = db.clientDao().getByName(name);
        return e != null ? e.id : -1;
    }

    private String resolveClientName(List<ClientEntity> clients, long clientId) {
        for (ClientEntity c : clients) {
            if (c.id == clientId) return c.name;
        }
        return "Desconocido";
    }

    private int countBudgetsForClient(long clientId) {
        int count = 0;
        for (BudgetEntity b : db.budgetDao().getAllBudgets()) {
            if (b.clientId == clientId) count++;
        }
        return count;
    }

    private double sumTotalsForClient(long clientId) {
        double total = 0;
        for (BudgetEntity b : db.budgetDao().getAllBudgets()) {
            if (b.clientId == clientId) total += b.totalAmount;
        }
        return total;
    }

    private String formatDate(long millis) {
        try {
            return new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"))
                    .format(new Date(millis));
        } catch (Exception e) {
            return "Hoy";
        }
    }

    private void seedIfEmpty() {
        ClientDao clientDao = db.clientDao();
        if (clientDao.count() > 0) return;

        ClientEntity pedro = new ClientEntity();
        pedro.name = "Pedro García";
        pedro.email = "pedro@email.com";
        pedro.phone = "";
        pedro.createdAt = System.currentTimeMillis();
        long pedroId = clientDao.insert(pedro);

        ClientEntity maria = new ClientEntity();
        maria.name = "María López";
        maria.email = "maria@email.com";
        maria.phone = "";
        maria.createdAt = System.currentTimeMillis();
        clientDao.insert(maria);

        BudgetEntity budget = new BudgetEntity();
        budget.code = "#00125";
        budget.clientId = pedroId;
        budget.date = System.currentTimeMillis();
        budget.descripcionServicio = "Presupuesto inicial de ejemplo";
        budget.totalAmount = 450000;
        budget.status = Budget.Status.APROBADO.name();
        budget.createdAt = System.currentTimeMillis();
        long budgetId = db.budgetDao().insertBudget(budget);

        BudgetItemEntity item = new BudgetItemEntity();
        item.budgetId = budgetId;
        item.descripcion = "Servicio inicial";
        item.cantidad = 1;
        item.unidadMedida = "global";
        item.precioUnitario = 450000;
        List<BudgetItemEntity> items = new ArrayList<>();
        items.add(item);
        db.budgetDao().insertItems(items);

        ReceivableEntity receivable = new ReceivableEntity();
        receivable.code = "#00125";
        receivable.clientId = pedroId;
        receivable.budgetId = budgetId;
        receivable.amount = 450000;
        receivable.dueDate = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000;
        receivable.status = Receivable.Status.PENDIENTE.name();
        try {
            db.receivableDao().insert(receivable);
        } catch (Exception ignored) {}
    }
}
