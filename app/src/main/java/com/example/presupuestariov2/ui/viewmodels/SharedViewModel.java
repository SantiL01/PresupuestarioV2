package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

public class SharedViewModel extends BaseViewModel {

    private static final String TAG = "VM_Shared";

    private final MutableLiveData<String> notice = new MutableLiveData<>();
    private final MutableLiveData<Boolean> refreshClients = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> refreshReceivables = new MutableLiveData<>(false);

    public LiveData<String> getNotice() {
        return notice;
    }

    public LiveData<Boolean> getRefreshClients() {
        return refreshClients;
    }

    public LiveData<Boolean> getRefreshReceivables() {
        return refreshReceivables;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void createClient(String name, String email, String phone) {
        if (name == null || name.trim().isEmpty()) {
            notice.setValue("El nombre es obligatorio.");
            return;
        }
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "createClient: inicio");
        runInBackground(() -> {
            try {
                Client client = new Client(name.trim(),
                        email != null ? email.trim() : "",
                        phone != null ? phone.trim() : "", 0, 0, 0.0);
                long id = DataManager.getInstance().addClient(client);
                if (id == -1 && DataManager.getInstance().isInitialized()) {
                    errorMessage.postValue("No se pudo guardar (email duplicado?).");
                    status.postValue(UiStatus.ERROR);
                    return;
                }
                notice.postValue("Cliente agregado con éxito.");
                refreshClients.postValue(true);
                status.postValue(UiStatus.SUCCESS);
                Log.i(TAG, "createClient: ok");
            } catch (Exception e) {
                Log.e(TAG, "createClient: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudo crear el cliente.");
            }
        });
    }

    public void createReceivable(String clientName, String code, double amount) {
        if (clientName == null || clientName.isEmpty() || code == null || code.isEmpty()) {
            notice.setValue("Debe completar todos los datos.");
            return;
        }
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "createReceivable: inicio code=" + code);
        runInBackground(() -> {
            try {
                Receivable item = new Receivable(clientName, code.trim(),
                        "Reciente", amount, Receivable.Status.PENDIENTE);
                long id = DataManager.getInstance().addReceivable(item);
                if (id == -1 && DataManager.getInstance().isInitialized()) {
                    errorMessage.postValue("El código debe coincidir con un presupuesto existente.");
                    status.postValue(UiStatus.ERROR);
                    return;
                }
                notice.postValue("Cobro registrado.");
                refreshReceivables.postValue(true);
                status.postValue(UiStatus.SUCCESS);
                Log.i(TAG, "createReceivable: ok");
            } catch (Exception e) {
                Log.e(TAG, "createReceivable: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudo registrar el cobro.");
            }
        });
    }

    public void consumeRefreshClients() {
        refreshClients.setValue(false);
    }

    public void consumeRefreshReceivables() {
        refreshReceivables.setValue(false);
    }
}
