package com.example.presupuestariov2.ui.controladores;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.ui.adaptadores.ReceivableAdapter;
import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.ui.controladores.base.BaseFragment;
import com.example.presupuestariov2.ui.viewmodels.CobrosViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

import java.util.ArrayList;

public class CobrosFragment extends BaseFragment {

    private static final String TAG = "UI_Cobros";

    private CobrosViewModel viewModel;
    private RecyclerView rvCobros;
    private ReceivableAdapter adapter;
    private ProgressBar pbLoading;
    private TextView tvError;
    private TextView tvEmpty;

    public CobrosFragment() {}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_cobros, container, false);
    }

    @Override
    protected void setupViews(View root) {
        Log.d(TAG, "setupViews");
        try {
            rvCobros = root.findViewById(R.id.rvCobros);
            pbLoading = root.findViewById(R.id.pbLoading);
            tvError = root.findViewById(R.id.tvError);
            tvEmpty = root.findViewById(R.id.tvEmpty);
            rvCobros.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ReceivableAdapter(new ArrayList<>(), receivable -> {
                try {
                    String[] opciones = {"Pendiente", "Vencido", "Pagado"};
                    new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                            .setTitle("Actualizar Cobro")
                            .setItems(opciones, (dialog, which) -> {
                                try {
                                    viewModel.changeStatus(receivable, Receivable.Status.values()[which]);
                                } catch (Exception e) {
                                    Log.e(TAG, "cambio estado", e);
                                }
                            })
                            .show();
                } catch (Exception e) {
                    Log.e(TAG, "dialog estado", e);
                }
            });
            rvCobros.setAdapter(adapter);
        } catch (Exception e) {
            Log.e(TAG, "setupViews error", e);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel = new ViewModelProvider(this).get(CobrosViewModel.class);

        viewModel.getReceivables().observe(getViewLifecycleOwner(), data -> {
            try {
                if (adapter != null && data != null) {
                    adapter.updateData(data);
                }
            } catch (Exception e) {
                Log.e(TAG, "update lista", e);
            }
        });

        viewModel.getStatus().observe(getViewLifecycleOwner(), state -> {
            renderStatus(state, pbLoading, rvCobros, tvEmpty, "Sin cobros registrados.");
            if (tvError != null && state != UiStatus.ERROR) tvError.setVisibility(View.GONE);
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

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (viewModel != null) viewModel.load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView");
        rvCobros = null;
        adapter = null;
        pbLoading = null;
        tvError = null;
        tvEmpty = null;
    }
}
