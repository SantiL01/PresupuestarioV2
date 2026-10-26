package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.PreferencesManager;

public class MasViewModel extends BaseViewModel {

    private static final String TAG = "VM_Mas";

    private final MutableLiveData<Integer> dueDays =
            new MutableLiveData<>(PreferencesManager.getInstance().getDueDays());

    public LiveData<Integer> getDueDays() {
        return dueDays;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void setDueDays(int days) {
        PreferencesManager.getInstance().setDueDays(days);
        dueDays.setValue(PreferencesManager.getInstance().getDueDays());
        status.setValue(UiStatus.SUCCESS);
        Log.i(TAG, "setDueDays: " + days);
    }
}
