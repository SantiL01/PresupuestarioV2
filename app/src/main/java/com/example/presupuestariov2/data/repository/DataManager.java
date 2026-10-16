package com.example.presupuestariov2.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.presupuestariov2.data.local.AppDatabase;
import com.example.presupuestariov2.data.local.BudgetDao;
import com.example.presupuestariov2.data.local.BudgetEntity;
import com.example.presupuestariov2.data.local.BudgetItemEntity;
import com.example.presupuestariov2.data.local.ClientDao;
import com.example.presupuestariov2.data.local.ClientEntity;
import com.example.presupuestariov2.data.local.ReceivableEntity;
import com.example.presupuestariov2.data.modelos.Budget;
import com.example.presupuestariov2.data.modelos.BudgetItem;
import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class DataManager {

    private static final String TAG = "Repo_DataManager";
    private static volatile DataManager instance;

    private AppDatabase db;
    private boolean initialized = false;

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
        try {
            db = AppDatabase.getInstance(context);
            initialized = true;
            seedIfEmpty();
            Log.d(TAG, "init ok");
        } catch (Exception e) {
            Log.e(TAG, "init error", e);
        }
    }

    public boolean isInitialized() { return initialized; }

    public List<Client> getClients() {
        if (!initialized) return new ArrayList<>(memClients);
        try {
            List<ClientEntity> entities = db.clientDao().getAll();
            List<Client> out = new ArrayList<>();
            for (ClientEntity e : entities) {
                int budgetsCount = countBudgetsForClient(e.getId());
                double total = sumTotalsForClient(e.getId());
                String phone = e.getPhone() != null ? e.getPhone() : "";
                Client c = new Client(e.getName(), e.getEmail(), phone,
                        budgetsCount, 0, total);
                c.setDbId(e.getId());
                out.add(c);
            }
            return out;
        } catch (Exception e) {
            Log.e(TAG, "getClients", e);
            throw e;
        }
    }

    public List<Budget> getBudgets() {
        if (!initialized) return new ArrayList<>(memBudgets);
        try {
            List<BudgetDao.BudgetWithItems> rows = db.budgetDao().getBudgetsWithItems();
            List<ClientEntity> clients = db.clientDao().getAll();
            List<Budget> out = new ArrayList<>();
            for (BudgetDao.BudgetWithItems row : rows) {
                if (row == null || row.budget == null) continue;
                String clientName = resolveClientName(clients, row.budget.getClientId());
                List<BudgetItem> items = new ArrayList<>();
                if (row.items != null) {
                    for (BudgetItemEntity ie : row.items) {
                        items.add(new BudgetItem(ie.getDescripcion(), ie.getCantidad(),
                                ie.getUnidadMedida(), ie.getPrecioUnitario()));
                    }
                }
                Budget b;
                try {
                    b = new Budget(row.budget.getCode(), clientName,
                            FormatUtils.dateText(row.budget.getDate()), row.budget.getTotalAmount(),
                            Budget.Status.valueOf(row.budget.getStatus()),
                            row.budget.getDescripcionServicio(), items);
                } catch (Exception ex) {
                    b = new Budget(row.budget.getCode(), clientName,
                            FormatUtils.dateText(row.budget.getDate()), row.budget.getTotalAmount(),
                            Budget.Status.BORRADOR,
                            row.budget.getDescripcionServicio(), items);
                }
                b.setDbId(row.budget.getId());
                b.setClientId(row.budget.getClientId());
                out.add(b);
            }
            return out;
        } catch (Exception e) {
            Log.e(TAG, "getBudgets", e);
            throw e;
        }
    }

    public List<Receivable> getReceivables() {
        if (!initialized) return new ArrayList<>(memReceivables);
        try {
            List<ReceivableEntity> entities = db.receivableDao().getAll();
            List<ClientEntity> clients = db.clientDao().getAll();
            List<Receivable> out = new ArrayList<>();
            for (ReceivableEntity e : entities) {
                String clientName = resolveClientName(clients, e.getClientId());
                Receivable r;
                try {
                    r = new Receivable(clientName, e.getCode(), "Vence " + FormatUtils.dateText(e.getDueDate()),
                            e.getAmount(), Receivable.Status.valueOf(e.getStatus()));
                } catch (Exception ex) {
                    r = new Receivable(clientName, e.getCode(), "Vence " + FormatUtils.dateText(e.getDueDate()),
                            e.getAmount(), Receivable.Status.PENDIENTE);
                }
                r.setDbId(e.getId());
                r.setClientId(e.getClientId());
                r.setBudgetId(e.getBudgetId());
                out.add(r);
            }
            return out;
        } catch (Exception e) {
            Log.e(TAG, "getReceivables", e);
            throw e;
        }
    }

    public long addClient(Client c) {
        if (c == null) return -1;
        if (!initialized) {
            memClients.add(0, c);
            return -1;
        }
        ClientEntity e = new ClientEntity();
        e.setName(c.getName());
        e.setEmail(c.getEmail());
        e.setPhone(c.getPhone() != null ? c.getPhone() : "");
        e.setCreatedAt(System.currentTimeMillis());
        try {
            long id = db.clientDao().insert(e);
            c.setDbId(id);
            Log.d(TAG, "addClient ok id=" + id);
            return id;
        } catch (Exception ex) {
            Log.e(TAG, "addClient error", ex);
            return -1;
        }
    }

    public long addBudget(Budget b) {
        if (b == null) return -1;
        if (!initialized) {
            memBudgets.add(0, b);
            return -1;
        }
        long clientId = resolveClientIdByName(b.getClientName());
        if (clientId == -1) return -1;

        BudgetEntity be = new BudgetEntity();
        be.setCode(b.getId());
        be.setClientId(clientId);
        be.setDate(System.currentTimeMillis());
        be.setDescripcionServicio(b.getDescripcionServicio());
        be.setTotalAmount(b.getTotalAmount());
        be.setStatus(b.getStatus() != null ? b.getStatus().name() : Budget.Status.BORRADOR.name());
        be.setCreatedAt(System.currentTimeMillis());
        long budgetDbId;
        try {
            budgetDbId = db.budgetDao().insertBudget(be);
        } catch (Exception e) {
            Log.e(TAG, "addBudget insert", e);
            return -1;
        }
        b.setDbId(budgetDbId);
        b.setClientId(clientId);

        try {
            List<BudgetItemEntity> itemEntities = new ArrayList<>();
            for (BudgetItem it : b.getItems()) {
                BudgetItemEntity ie = new BudgetItemEntity();
                ie.setBudgetId(budgetDbId);
                ie.setDescripcion(it.getDescripcion());
                ie.setCantidad(it.getCantidad());
                ie.setUnidadMedida(it.getUnidadMedida());
                ie.setPrecioUnitario(it.getPrecioUnitario());
                itemEntities.add(ie);
            }
            if (!itemEntities.isEmpty()) {
                db.budgetDao().insertItems(itemEntities);
            }
        } catch (Exception e) {
            Log.e(TAG, "addBudget items", e);
        }

        ReceivableEntity re = new ReceivableEntity();
        re.setCode(be.getCode());
        re.setClientId(clientId);
        re.setBudgetId(budgetDbId);
        re.setAmount(be.getTotalAmount());
        re.setDueDate(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        re.setStatus(Receivable.Status.PENDIENTE.name());
        try {
            db.receivableDao().insert(re);
        } catch (Exception ignored) {
        }
        Log.d(TAG, "addBudget ok id=" + budgetDbId);
        return budgetDbId;
    }

    public long addReceivable(Receivable r) {
        if (r == null) return -1;
        if (!initialized) {
            memReceivables.add(0, r);
            return -1;
        }
        long clientId = resolveClientIdByName(r.getClientName());
        if (clientId == -1) return -1;
        BudgetEntity linked;
        try {
            linked = db.budgetDao().getByCode(r.getCode());
        } catch (Exception e) {
            Log.e(TAG, "addReceivable lookup", e);
            return -1;
        }
        long budgetId = linked != null ? linked.getId() : -1;
        if (budgetId == -1) return -1;

        ReceivableEntity e = new ReceivableEntity();
        e.setCode(r.getCode());
        e.setClientId(clientId);
        e.setBudgetId(budgetId);
        e.setAmount(r.getAmount());
        e.setDueDate(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
        e.setStatus(r.getStatus() != null ? r.getStatus().name() : Receivable.Status.PENDIENTE.name());
        try {
            long id = db.receivableDao().insert(e);
            r.setDbId(id);
            r.setClientId(clientId);
            r.setBudgetId(budgetId);
            Log.d(TAG, "addReceivable ok id=" + id);
            return id;
        } catch (Exception ex) {
            Log.e(TAG, "addReceivable insert", ex);
            return -1;
        }
    }

    public void updateBudgetStatus(Budget b) {
        if (b == null || !initialized || b.getDbId() == 0) return;
        try {
            db.budgetDao().updateStatus(b.getDbId(),
                    b.getStatus() != null ? b.getStatus().name() : Budget.Status.BORRADOR.name());
        } catch (Exception e) {
            Log.e(TAG, "updateBudgetStatus", e);
            throw e;
        }
    }

    public void updateReceivableStatus(Receivable r) {
        if (r == null || !initialized || r.getDbId() == 0) return;
        try {
            db.receivableDao().updateStatus(r.getDbId(),
                    r.getStatus() != null ? r.getStatus().name() : Receivable.Status.PENDIENTE.name());
        } catch (Exception e) {
            Log.e(TAG, "updateReceivableStatus", e);
            throw e;
        }
    }

    private long resolveClientIdByName(String name) {
        if (name == null) return -1;
        try {
            ClientEntity e = db.clientDao().getByName(name);
            return e != null ? e.getId() : -1;
        } catch (Exception e) {
            Log.e(TAG, "resolveClient", e);
            return -1;
        }
    }

    private String resolveClientName(List<ClientEntity> clients, long clientId) {
        try {
            for (ClientEntity c : clients) {
                if (c.getId() == clientId) return c.getName();
            }
        } catch (Exception e) {
            Log.e(TAG, "resolveClientName", e);
        }
        return "Desconocido";
    }

    private int countBudgetsForClient(long clientId) {
        int count = 0;
        try {
            for (BudgetEntity b : db.budgetDao().getAllBudgets()) {
                if (b.getClientId() == clientId) count++;
            }
        } catch (Exception e) {
            Log.e(TAG, "countBudgets", e);
        }
        return count;
    }

    private double sumTotalsForClient(long clientId) {
        double total = 0;
        try {
            for (BudgetEntity b : db.budgetDao().getAllBudgets()) {
                if (b.getClientId() == clientId) total += b.getTotalAmount();
            }
        } catch (Exception e) {
            Log.e(TAG, "sumTotals", e);
        }
        return total;
    }

    private void seedIfEmpty() {
        try {
            ClientDao clientDao = db.clientDao();
            if (clientDao.count() > 0) return;

            ClientEntity pedro = new ClientEntity();
            pedro.setName("Pedro García");
            pedro.setEmail("pedro@email.com");
            pedro.setPhone("");
            pedro.setCreatedAt(System.currentTimeMillis());
            long pedroId = clientDao.insert(pedro);

            ClientEntity maria = new ClientEntity();
            maria.setName("María López");
            maria.setEmail("maria@email.com");
            maria.setPhone("");
            maria.setCreatedAt(System.currentTimeMillis());
            clientDao.insert(maria);

            BudgetEntity budget = new BudgetEntity();
            budget.setCode("#00125");
            budget.setClientId(pedroId);
            budget.setDate(System.currentTimeMillis());
            budget.setDescripcionServicio("Presupuesto inicial de ejemplo");
            budget.setTotalAmount(450000);
            budget.setStatus(Budget.Status.APROBADO.name());
            budget.setCreatedAt(System.currentTimeMillis());
            long budgetId = db.budgetDao().insertBudget(budget);

            BudgetItemEntity item = new BudgetItemEntity();
            item.setBudgetId(budgetId);
            item.setDescripcion("Servicio inicial");
            item.setCantidad(1);
            item.setUnidadMedida("global");
            item.setPrecioUnitario(450000);
            List<BudgetItemEntity> items = new ArrayList<>();
            items.add(item);
            db.budgetDao().insertItems(items);

            ReceivableEntity receivable = new ReceivableEntity();
            receivable.setCode("#00125");
            receivable.setClientId(pedroId);
            receivable.setBudgetId(budgetId);
            receivable.setAmount(450000);
            receivable.setDueDate(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000);
            receivable.setStatus(Receivable.Status.PENDIENTE.name());
            try {
                db.receivableDao().insert(receivable);
            } catch (Exception ignored) {}
            Log.d(TAG, "seed ok");
        } catch (Exception e) {
            Log.e(TAG, "seed", e);
        }
    }
}
