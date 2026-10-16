package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientesViewModel extends BaseViewModel {

    private static final String TAG = "VM_Clientes";

    private final MutableLiveData<List<Client>> clients = new MutableLiveData<>(new ArrayList<>());
    private final List<Client> cache = new ArrayList<>();
    private String query = "";

    public LiveData<List<Client>> getClients() {
        return clients;
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
                List<Client> data = DataManager.getInstance().getClients();
                synchronized (cache) {
                    cache.clear();
                    cache.addAll(data);
                }
                applyFilter(query);
                Log.i(TAG, "load: ok n=" + data.size());
            } catch (Exception e) {
                Log.e(TAG, "load: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudieron cargar los clientes.");
            }
        });
    }

    public void setQuery(String value) {
        query = value != null ? value : "";
        applyFilter(query);
    }

    private void applyFilter(String value) {
        List<Client> source;
        synchronized (cache) {
            source = new ArrayList<>(cache);
        }
        String normalized = value.trim().toLowerCase(Locale.getDefault());
        List<Client> filtered = new ArrayList<>();
        try {
            if (normalized.isEmpty()) {
                filtered.addAll(source);
            } else {
                for (Client c : source) {
                    boolean byName = c.getName() != null
                            && c.getName().toLowerCase(Locale.getDefault()).contains(normalized);
                    boolean byEmail = c.getEmail() != null
                            && c.getEmail().toLowerCase(Locale.getDefault()).contains(normalized);
                    if (byName || byEmail) filtered.add(c);
                }
            }
            clients.postValue(filtered);
            status.postValue(filtered.isEmpty() ? UiStatus.EMPTY : UiStatus.SUCCESS);
        } catch (Exception e) {
            Log.e(TAG, "filter: error", e);
            status.postValue(UiStatus.ERROR);
            errorMessage.postValue("No se pudo filtrar la lista.");
        }
    }
}
