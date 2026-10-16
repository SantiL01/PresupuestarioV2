package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.Budget;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PresupuestosViewModel extends BaseViewModel {

    private static final String TAG = "VM_Presupuestos";

    private final MutableLiveData<List<Budget>> budgets = new MutableLiveData<>(new ArrayList<>());
    private final List<Budget> cache = new ArrayList<>();
    private String query = "";

    public LiveData<List<Budget>> getBudgets() {
        return budgets;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void load() {
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "load: inicio");
        runInBackground(() -> {
            try {
                List<Budget> data = DataManager.getInstance().getBudgets();
                synchronized (cache) {
                    cache.clear();
                    cache.addAll(data);
                }
                applyFilter(query);
                Log.i(TAG, "load: ok n=" + data.size());
            } catch (Exception e) {
                Log.e(TAG, "load: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudieron cargar los presupuestos.");
            }
        });
    }

    public void setQuery(String value) {
        query = value != null ? value : "";
        applyFilter(query);
    }

    private void applyFilter(String value) {
        List<Budget> source;
        synchronized (cache) {
            source = new ArrayList<>(cache);
        }
        String normalized = value.trim().toLowerCase(Locale.getDefault());
        List<Budget> filtered = new ArrayList<>();
        try {
            if (normalized.isEmpty()) {
                filtered.addAll(source);
            } else {
                for (Budget b : source) {
                    boolean byClient = b.getClientName() != null
                            && b.getClientName().toLowerCase(Locale.getDefault()).contains(normalized);
                    boolean byCode = b.getId() != null
                            && b.getId().toLowerCase(Locale.getDefault()).contains(normalized);
                    if (byClient || byCode) filtered.add(b);
                }
            }
            budgets.postValue(filtered);
            status.postValue(filtered.isEmpty() ? UiStatus.EMPTY : UiStatus.SUCCESS);
        } catch (Exception e) {
            Log.e(TAG, "filter: error", e);
            status.postValue(UiStatus.ERROR);
            errorMessage.postValue("No se pudo filtrar la lista.");
        }
    }

    public void changeStatus(Budget budget, Budget.Status newStatus) {
        if (budget == null || newStatus == null) return;
        Log.d(TAG, "changeStatus: " + budget.getId() + " -> " + newStatus);
        runInBackground(() -> {
            try {
                budget.setStatus(newStatus);
                DataManager.getInstance().updateBudgetStatus(budget);
                applyFilter(query);
            } catch (Exception e) {
                Log.e(TAG, "changeStatus: error", e);
                errorMessage.postValue("No se pudo actualizar el estado.");
            }
        });
    }
}
