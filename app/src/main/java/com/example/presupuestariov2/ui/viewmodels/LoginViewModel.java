package com.example.presupuestariov2.ui.viewmodels;

import android.util.Log;
import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.presupuestariov2.data.modelos.User;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.base.BaseViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.PreferencesManager;

public class LoginViewModel extends BaseViewModel {

    private static final String TAG = "VM_Login";
    private static final int MIN_PASSWORD_LENGTH = 6;

    private final MutableLiveData<Boolean> registerMode = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> loggedIn = new MutableLiveData<>(false);

    public LoginViewModel() {
        status.setValue(UiStatus.SUCCESS);
    }

    public LiveData<Boolean> getRegisterMode() {
        return registerMode;
    }

    public LiveData<Boolean> getLoggedIn() {
        return loggedIn;
    }

    @Override
    protected String getTag() {
        return TAG;
    }

    public void toggleMode() {
        Boolean current = registerMode.getValue();
        registerMode.setValue(current == null || !current);
        errorMessage.setValue(null);
    }

    public void login(String email, String password) {
        String mail = email != null ? email.trim() : "";
        if (!isValidEmail(mail)) {
            errorMessage.setValue("Ingresá un correo electrónico válido.");
            return;
        }
        if (password == null || password.isEmpty()) {
            errorMessage.setValue("Ingresá tu contraseña.");
            return;
        }
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "login: inicio");
        runInBackground(() -> {
            User user = DataManager.getInstance().login(mail, password);
            if (user == null) {
                errorMessage.postValue("Correo o contraseña incorrectos.");
                status.postValue(UiStatus.ERROR);
                return;
            }
            PreferencesManager.getInstance().startSession(user.getDbId());
            status.postValue(UiStatus.SUCCESS);
            loggedIn.postValue(true);
            Log.i(TAG, "login: ok id=" + user.getDbId());
        });
    }

    public void register(String name, String email, String password, String confirm) {
        String nombre = name != null ? name.trim() : "";
        String mail = email != null ? email.trim() : "";
        if (nombre.isEmpty()) {
            errorMessage.setValue("Ingresá tu nombre.");
            return;
        }
        if (!isValidEmail(mail)) {
            errorMessage.setValue("Ingresá un correo electrónico válido.");
            return;
        }
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            errorMessage.setValue("La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres.");
            return;
        }
        if (!password.equals(confirm)) {
            errorMessage.setValue("Las contraseñas no coinciden.");
            return;
        }
        status.setValue(UiStatus.LOADING);
        Log.d(TAG, "register: inicio");
        runInBackground(() -> {
            if (DataManager.getInstance().emailExists(mail)) {
                errorMessage.postValue("Ya existe una cuenta con ese correo.");
                status.postValue(UiStatus.ERROR);
                return;
            }
            long id = DataManager.getInstance().registerUser(nombre, mail, password);
            if (id == -1) {
                errorMessage.postValue("No se pudo crear la cuenta. Intentá nuevamente.");
                status.postValue(UiStatus.ERROR);
                return;
            }
            PreferencesManager.getInstance().startSession(id);
            status.postValue(UiStatus.SUCCESS);
            loggedIn.postValue(true);
            Log.i(TAG, "register: ok id=" + id);
        });
    }

    private boolean isValidEmail(String email) {
        return !email.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
