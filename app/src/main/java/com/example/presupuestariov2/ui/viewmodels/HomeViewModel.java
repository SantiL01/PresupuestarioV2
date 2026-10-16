package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.FormatUtils;

import java.util.ArrayList;
import java.util.List;

public class HomeViewModel extends BaseViewModel {

    private static final String TAG = "VM_Home";

    private final MutableLiveData<List<Receivable>> pending = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> totalText = new MutableLiveData<>("");

    public LiveData<List<Receivable>> getPending() {
        return pending;
    }

    public LiveData<String> getTotalText() {
        return totalText;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void load() {
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "load: inicio");
        runInBackground(() -> {
            List<Receivable> result = new ArrayList<>();
            double total = 0;
            try {
                List<Receivable> all = DataManager.getInstance().getReceivables();
                for (Receivable r : all) {
                    if (r.getStatus() == Receivable.Status.PENDIENTE
                            || r.getStatus() == Receivable.Status.VENCIDO) {
                        result.add(r);
                        total += r.getAmount();
                    }
                }
                pending.postValue(result);
                totalText.postValue(FormatUtils.pendingTotal(total, result.size()));
                status.postValue(result.isEmpty() ? UiStatus.EMPTY : UiStatus.SUCCESS);
                Log.i(TAG, "load: ok n=" + result.size());
            } catch (Exception e) {
                Log.e(TAG, "load: error", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("No se pudieron cargar los cobros.");
            }
        });
    }
}
