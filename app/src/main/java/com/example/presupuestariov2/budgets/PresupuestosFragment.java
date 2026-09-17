package com.example.presupuestariov2.budgets;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.adapter.BudgetAdapter;
import com.example.presupuestariov2.model.Budget;
import com.example.presupuestariov2.model.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PresupuestosFragment extends Fragment {

    private final List<Budget> allBudgets = new ArrayList<>();
    private BudgetAdapter adapter;
    private RecyclerView rvBudgets;

    public PresupuestosFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList() {
        allBudgets.clear();
        allBudgets.addAll(DataManager.getInstance().getBudgets());
        if (adapter != null) {
            adapter.updateData(allBudgets);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_presupuestos, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allBudgets.clear();
        allBudgets.addAll(DataManager.getInstance().getBudgets());

        rvBudgets = view.findViewById(R.id.rvBudgets);
        rvBudgets.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new BudgetAdapter(new ArrayList<>(allBudgets), budget -> {
            String[] opciones = {"Borrador", "Enviado", "Aprobado", "Rechazado"};
            new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Cambiar estado")
                    .setItems(opciones, (dialog, which) -> {
                        budget.setStatus(Budget.Status.values()[which]);
                        adapter.notifyDataSetChanged();
                    })
                    .show();
        });
        rvBudgets.setAdapter(adapter);

        setupSearch(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Limpieza de referencias para evitar fugas de memoria
        rvBudgets = null;
        adapter = null;
    }

    private void setupSearch(View root) {
        EditText etSearch = root.findViewById(R.id.etSearchBudget);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterBudgets(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterBudgets(String query) {
        String normalized = query.trim().toLowerCase(Locale.getDefault());
        List<Budget> filtered = new ArrayList<>();
        for (Budget budget : allBudgets) {
            boolean matchesClient = budget.getClientName().toLowerCase(Locale.getDefault()).contains(normalized);
            boolean matchesId = budget.getId().toLowerCase(Locale.getDefault()).contains(normalized);
            if (matchesClient || matchesId) {
                filtered.add(budget);
            }
        }
        adapter.updateData(filtered);
    }
}