package com.example.presupuestariov2.ui.controladores;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.ui.adaptadores.ReceivableAdapter;
import com.example.presupuestariov2.ui.controladores.base.BaseFragment;
import com.example.presupuestariov2.ui.viewmodels.HomeViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;
import com.example.presupuestariov2.utils.FormatUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class HomeFragment extends BaseFragment {

    private static final String TAG = "UI_Home";

    private HomeViewModel viewModel;
    private RecyclerView rvReceivables;
    private ReceivableAdapter adapter;
    private TextView tvTotalPorCobrar;
    private TextView tvEmptyReceivables;
    private TextView tvError;
    private ProgressBar pbLoading;
    private final List<Receivable> pendientes = new ArrayList<>();

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    protected void setupViews(View root) {
        Log.d(TAG, "setupViews");
        try {
            TextView tvGreetingTime = root.findViewById(R.id.tvGreetingTime);
            TextView tvSelectedMonth = root.findViewById(R.id.tvSelectedMonth);
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            tvGreetingTime.setText(FormatUtils.greetingForHour(hour));
            tvSelectedMonth.setText(FormatUtils.currentMonthTitle());

            View row = root.findViewById(R.id.quickActionsTop);
            if (row != null) {
                row.findViewById(R.id.actionPresupuesto).setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).openPresupuestos();
                });
                row.findViewById(R.id.actionFactura).setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).openCobros();
                });
                row.findViewById(R.id.actionCliente).setOnClickListener(v -> {
                    if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).openClientes();
                });
            }

            rvReceivables = root.findViewById(R.id.rvReceivables);
            tvTotalPorCobrar = root.findViewById(R.id.tvTotalPorCobrar);
            tvEmptyReceivables = root.findViewById(R.id.tvEmptyReceivables);
            tvError = root.findViewById(R.id.tvError);
            pbLoading = root.findViewById(R.id.pbLoading);

            rvReceivables.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ReceivableAdapter(pendientes, receivable -> {
                try {
                    Toast.makeText(getContext(), "Cobro: " + receivable.getCode(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "click cobro", e);
                }
            });
            rvReceivables.setAdapter(adapter);

            root.findViewById(R.id.tvSeeAllReceivables).setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).openCobros();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "setupViews error", e);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getPending().observe(getViewLifecycleOwner(), data -> {
            try {
                pendientes.clear();
                if (data != null) pendientes.addAll(data);
                if (adapter != null) adapter.notifyDataSetChanged();
            } catch (Exception e) {
                Log.e(TAG, "update lista", e);
            }
        });

        viewModel.getTotalText().observe(getViewLifecycleOwner(), text -> {
            if (tvTotalPorCobrar != null && text != null) tvTotalPorCobrar.setText(text);
        });

        viewModel.getStatus().observe(getViewLifecycleOwner(), state -> {
            renderStatus(state, pbLoading, rvReceivables, tvEmptyReceivables,
                    "Sin cobros pendientes. Creá un presupuesto y aparecerá acá.");
            if (state == UiStatus.SUCCESS || state == UiStatus.EMPTY) {
                boolean empty = pendientes.isEmpty();
                if (tvEmptyReceivables != null) {
                    tvEmptyReceivables.setVisibility(empty ? View.VISIBLE : View.GONE);
                }
                if (rvReceivables != null) {
                    rvReceivables.setVisibility(empty ? View.GONE : View.VISIBLE);
                }
            }
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

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvReceivables = null;
        adapter = null;
        tvTotalPorCobrar = null;
        tvEmptyReceivables = null;
        tvError = null;
        pbLoading = null;
    }
}
