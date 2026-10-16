package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.Budget;
import com.example.presupuestariov2.data.modelos.BudgetItem;
import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class NuevoPresupuestoViewModel extends BaseViewModel {

    private static final String TAG = "VM_NuevoPresupuesto";

    private final MutableLiveData<List<Client>> clients = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<BudgetItem>> items = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> totalText = new MutableLiveData<>(FormatUtils.money(0));
    private final MutableLiveData<Boolean> saved = new MutableLiveData<>(false);

    public LiveData<List<Client>> getClients() {
        return clients;
    }

    public LiveData<List<BudgetItem>> getItems() {
        return items;
    }

    public LiveData<String> getTotalText() {
        return totalText;
    }

    public LiveData<Boolean> getSaved() {
        return saved;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void loadClients() {
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "loadClients: inicio");
        runInBackground(() -> {
            try {
                List<Client> data = DataManager.getInstance().getClients();
                clients.postValue(data);
                status.postValue(UiStatus.SUCCESS);
                Log.i(TAG, "loadClients: ok n=" + data.size());
            } catch (Exception e) {
                Log.e(TAG, "loadClients: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudieron cargar los clientes.");
            }
        });
    }

    public void addItem(String descripcion, double cantidad, String unidad, double precio) {
        try {
            if (descripcion == null || descripcion.trim().isEmpty()) {
                errorMessage.postValue("Complete la descripción del ítem.");
                return;
            }
            List<BudgetItem> current = items.getValue() != null
                    ? new ArrayList<>(items.getValue()) : new ArrayList<>();
            current.add(new BudgetItem(descripcion.trim(), cantidad, unidad, precio));
            items.setValue(current);
            updateTotal(current);
        } catch (Exception e) {
            Log.e(TAG, "addItem: error", e);
            errorMessage.postValue("No se pudo agregar el ítem.");
        }
    }

    public void removeItem(int position) {
        try {
            List<BudgetItem> current = items.getValue() != null
                    ? new ArrayList<>(items.getValue()) : new ArrayList<>();
            if (position >= 0 && position < current.size()) {
                current.remove(position);
                items.setValue(current);
                updateTotal(current);
            }
        } catch (Exception e) {
            Log.e(TAG, "removeItem: error", e);
        }
    }

    private void updateTotal(List<BudgetItem> list) {
        double total = 0;
        for (BudgetItem it : list) {
            try {
                total += it.getSubtotal();
            } catch (Exception e) {
                Log.e(TAG, "updateTotal: item inválido", e);
            }
        }
        totalText.setValue(FormatUtils.money(total));
    }

    public void save(String clientName, String descripcionServicio) {
        List<BudgetItem> current = items.getValue() != null
                ? new ArrayList<>(items.getValue()) : new ArrayList<>();
        if (current.isEmpty()) {
            errorMessage.setValue("Debe agregar al menos un ítem.");
            return;
        }
        if (clientName == null || clientName.isEmpty()) {
            errorMessage.setValue("Seleccione un cliente.");
            return;
        }
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "save: inicio cliente=" + clientName);
        runInBackground(() -> {
            try {
                double total = 0;
                for (BudgetItem it : current) total += it.getSubtotal();
                String code = "#00" + (int) (Math.random() * 900 + 100);
                Budget budget = new Budget(code, clientName, "Hoy", total,
                        Budget.Status.BORRADOR,
                        descripcionServicio != null ? descripcionServicio.trim() : "",
                        new ArrayList<>(current));
                long id = DataManager.getInstance().addBudget(budget);
                if (id == -1 && DataManager.getInstance().isInitialized()) {
                    errorMessage.postValue("No se pudo guardar (verifique cliente/código).");
                    status.postValue(UiStatus.ERROR);
                    Log.e(TAG, "save: rechazado por repositorio", null);
                    return;
                }
                saved.postValue(true);
                status.postValue(UiStatus.SUCCESS);
                Log.i(TAG, "save: ok id=" + id);
            } catch (Exception e) {
                Log.e(TAG, "save: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudo guardar el presupuesto.");
            }
        });
    }

    public void consumeSaved() {
        saved.setValue(false);
    }
}
