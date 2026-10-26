package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.User;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.PreferencesManager;

public class MasViewModel extends BaseViewModel {

    private static final String TAG = "VM_Mas";

    private final MutableLiveData<User> user = new MutableLiveData<>();
    private final MutableLiveData<Integer> dueDays =
            new MutableLiveData<>(PreferencesManager.getInstance().getDueDays());
    private final MutableLiveData<Boolean> loggedOut = new MutableLiveData<>(false);

    public LiveData<User> getUser() {
        return user;
    }

    public LiveData<Integer> getDueDays() {
        return dueDays;
    }

    public LiveData<Boolean> getLoggedOut() {
        return loggedOut;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void load() {
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "load: inicio");
        runInBackground(() -> {
            long id = PreferencesManager.getInstance().getSessionUserId();
            User u = DataManager.getInstance().getUserById(id);
            if (u == null) {
                // La sesión apunta a un usuario que ya no existe
                Log.e(TAG, "load: usuario no encontrado id=" + id, null);
                PreferencesManager.getInstance().clearSession();
                loggedOut.postValue(true);
                return;
            }
            user.postValue(u);
            status.postValue(UiStatus.SUCCESS);
            Log.i(TAG, "load: ok id=" + id);
        });
    }

    public void setDueDays(int days) {
        PreferencesManager.getInstance().setDueDays(days);
        dueDays.setValue(PreferencesManager.getInstance().getDueDays());
        Log.i(TAG, "setDueDays: " + days);
    }

    public void logout() {
        PreferencesManager.getInstance().clearSession();
        loggedOut.setValue(true);
        Log.i(TAG, "logout");
    }
}
