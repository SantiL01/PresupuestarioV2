package com.example.presupuestariov2.ui.controladores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.data.modelos.Receivable;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.adaptadores.ReceivableAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private RecyclerView rvReceivables;
    private ReceivableAdapter adapter;
    private TextView tvTotalPorCobrar;
    private TextView tvEmptyReceivables;
    private final List<Receivable> pendientes = new ArrayList<>();

    public HomeFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupHeader(view);
        setupQuickActions(view);
        setupReceivables(view);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshReceivables();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvReceivables = null;
        adapter = null;
        tvTotalPorCobrar = null;
        tvEmptyReceivables = null;
    }

    private void setupHeader(View root) {
        TextView tvGreetingTime = root.findViewById(R.id.tvGreetingTime);
        TextView tvSelectedMonth = root.findViewById(R.id.tvSelectedMonth);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String saludo = hour < 12 ? "Buenos días" : (hour < 20 ? "Buenas tardes" : "Buenas noches");
        tvGreetingTime.setText(saludo);

        String mes = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"))
                .format(Calendar.getInstance().getTime());
        if (!mes.isEmpty()) {
            mes = mes.substring(0, 1).toUpperCase(new Locale("es", "ES")) + mes.substring(1);
        }
        tvSelectedMonth.setText(mes);
    }

    private void setupQuickActions(View root) {
        View row = root.findViewById(R.id.quickActionsTop);
        if (row == null) return;
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

    private void setupReceivables(View root) {
        rvReceivables = root.findViewById(R.id.rvReceivables);
        tvTotalPorCobrar = root.findViewById(R.id.tvTotalPorCobrar);
        tvEmptyReceivables = root.findViewById(R.id.tvEmptyReceivables);

        rvReceivables.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ReceivableAdapter(pendientes, receivable ->
                Toast.makeText(getContext(), "Cobro: " + receivable.getCode(), Toast.LENGTH_SHORT).show());
        rvReceivables.setAdapter(adapter);

        root.findViewById(R.id.tvSeeAllReceivables).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openCobros();
            }
        });

        refreshReceivables();
    }

    private void refreshReceivables() {
        pendientes.clear();
        double total = 0;
        for (Receivable r : DataManager.getInstance().getReceivables()) {
            // Solo lo no cobrado: PENDIENTE o VENCIDO. PAGADO ya no es por cobrar.
            if (r.getStatus() == Receivable.Status.PENDIENTE
                    || r.getStatus() == Receivable.Status.VENCIDO) {
                pendientes.add(r);
                total += r.getAmount();
            }
        }
        if (adapter != null) adapter.notifyDataSetChanged();
        if (tvTotalPorCobrar != null) {
            tvTotalPorCobrar.setText(String.format(Locale.getDefault(),
                    "$ %.2f pendiente (%d)", total, pendientes.size()));
        }
        if (tvEmptyReceivables != null && rvReceivables != null) {
            boolean empty = pendientes.isEmpty();
            tvEmptyReceivables.setVisibility(empty ? View.VISIBLE : View.GONE);
            rvReceivables.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    }
}
