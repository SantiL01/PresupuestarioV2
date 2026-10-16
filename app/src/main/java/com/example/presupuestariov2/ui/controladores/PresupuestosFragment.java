package com.example.presupuestariov2.ui.controladores;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.ui.adaptadores.BudgetAdapter;
import com.example.presupuestariov2.data.modelos.Budget;
import com.example.presupuestariov2.ui.controladores.base.BaseFragment;
import com.example.presupuestariov2.ui.viewmodels.PresupuestosViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

import java.util.ArrayList;

public class PresupuestosFragment extends BaseFragment {

    private static final String TAG = "UI_Presupuestos";

    private PresupuestosViewModel viewModel;
    private BudgetAdapter adapter;
    private RecyclerView rvBudgets;
    private ProgressBar pbLoading;
    private TextView tvError;
    private TextView tvEmpty;

    public PresupuestosFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_presupuestos, container, false);
    }

    @Override
    protected void setupViews(View root) {
        Log.d(TAG, "setupViews");
        try {
            rvBudgets = root.findViewById(R.id.rvBudgets);
            pbLoading = root.findViewById(R.id.pbLoading);
            tvError = root.findViewById(R.id.tvError);
            tvEmpty = root.findViewById(R.id.tvEmpty);
            rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new BudgetAdapter(new ArrayList<>(), budget -> {
                try {
                    String[] opciones = {"Borrador", "Enviado", "Aprobado", "Rechazado"};
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Cambiar estado")
                            .setItems(opciones, (dialog, which) -> {
                                try {
                                    viewModel.changeStatus(budget, Budget.Status.values()[which]);
                                } catch (Exception e) {
                                    Log.e(TAG, "cambio estado", e);
                                }
                            })
                            .show();
                } catch (Exception e) {
                    Log.e(TAG, "dialog estado", e);
                }
            });
            rvBudgets.setAdapter(adapter);

            EditText etSearch = root.findViewById(R.id.etSearchBudget);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (viewModel != null) viewModel.setQuery(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        } catch (Exception e) {
            Log.e(TAG, "setupViews error", e);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel = new ViewModelProvider(this).get(PresupuestosViewModel.class);

        viewModel.getBudgets().observe(getViewLifecycleOwner(), data -> {
            try {
                if (adapter != null && data != null) adapter.updateData(data);
            } catch (Exception e) {
                Log.e(TAG, "update lista", e);
            }
        });

        viewModel.getStatus().observe(getViewLifecycleOwner(), state -> {
            renderStatus(state, pbLoading, rvBudgets, tvEmpty, "Sin presupuestos.");
            if (tvError != null) tvError.setVisibility(View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                showError(msg);
                if (tvError != null) {
                    tvError.setText(msg);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.load();
    }

    public void refreshList() {
        if (viewModel != null) viewModel.load();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvBudgets = null;
        adapter = null;
        pbLoading = null;
        tvError = null;
        tvEmpty = null;
    }
}
