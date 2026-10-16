package com.example.presupuestariov2.ui.viewmodels.base;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.AppExecutors;

public abstract class BaseViewModel extends ViewModel {

    protected final MutableLiveData<UiStatus> status = new MutableLiveData<>(UiStatus.LOADING);
    protected final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<UiStatus> getStatus() {
        return status;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    protected void runInBackground(Runnable task) {
        AppExecutors.getInstance().diskIO().execute(() -> {
            try {
                task.run();
            } catch (Exception e) {
                Log.e(getTag(), "Error en tarea de fondo", e);
                status.postValue(UiStatus.ERROR);
                errorMessage.postValue("Ocurrió un error. Intentá nuevamente.");
            }
        });
    }

    protected abstract String getTag();
}
