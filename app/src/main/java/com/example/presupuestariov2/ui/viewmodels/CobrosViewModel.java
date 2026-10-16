package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

import java.util.ArrayList;
import java.util.List;

public class CobrosViewModel extends BaseViewModel {

    private static final String TAG = "VM_Cobros";

    private final MutableLiveData<List<Receivable>> receivables = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Receivable>> getReceivables() {
        return receivables;
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
                List<Receivable> data = DataManager.getInstance().getReceivables();
                receivables.postValue(data);
                status.postValue(data.isEmpty() ? UiStatus.EMPTY : UiStatus.SUCCESS);
                Log.i(TAG, "load: ok n=" + data.size());
            } catch (Exception e) {
                Log.e(TAG, "load: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudieron cargar los cobros.");
            }
        });
    }

    public void changeStatus(Receivable item, Receivable.Status newStatus) {
        if (item == null || newStatus == null) return;
        Log.d(TAG, "changeStatus: " + item.getCode() + " -> " + newStatus);
        runInBackground(() -> {
            try {
                item.setStatus(newStatus);
                DataManager.getInstance().updateReceivableStatus(item);
                List<Receivable> data = DataManager.getInstance().getReceivables();
                receivables.postValue(data);
                status.postValue(data.isEmpty() ? UiStatus.EMPTY : UiStatus.SUCCESS);
            } catch (Exception e) {
                Log.e(TAG, "changeStatus: error", e);
                errorMessage.postValue("No se pudo actualizar el cobro.");
            }
        });
    }
}
