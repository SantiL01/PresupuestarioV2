package com.example.presupuestariov2.ui.controladores.base;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

public abstract class BaseFragment extends Fragment {

    protected abstract void setupViews(View root);

    protected abstract void observeViewModel();

    protected void renderStatus(UiStatus state, ProgressBar loading, View content,
                                TextView emptyView, String emptyText) {
        if (state == null) return;
        try {
            if (loading != null) {
                loading.setVisibility(state == UiStatus.LOADING ? View.VISIBLE : View.GONE);
            }
            if (content != null) {
                content.setVisibility(state == UiStatus.LOADING ? View.GONE : View.VISIBLE);
            }
            if (emptyView != null) {
                boolean showEmpty = state == UiStatus.EMPTY;
                emptyView.setVisibility(showEmpty ? View.VISIBLE : View.GONE);
                if (showEmpty && emptyText != null) {
                    emptyView.setText(emptyText);
                }
            }
        } catch (Exception e) {
            showError("No se pudo actualizar la vista.");
        }
    }

    protected void showError(String message) {
        try {
            if (getContext() != null && message != null) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViews(view);
        observeViewModel();
    }
}
