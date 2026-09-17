package com.example.presupuestariov2.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.MainActivity;
import com.example.presupuestariov2.R;
import com.example.presupuestariov2.adapter.ActivityAdapter;
import com.example.presupuestariov2.adapter.ReceivableAdapter;
import com.example.presupuestariov2.model.ActivityItem;
import com.example.presupuestariov2.model.Receivable;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvRecentActivity;
    private RecyclerView rvReceivables;

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

        setupRecentActivity(view);
        setupReceivables(view);
        setupMonthSelector(view);
        setupQuickActions(view);
        setupChart(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Liberación de referencias clave para evitar fugas en Home
        rvRecentActivity = null;
        rvReceivables = null;
    }

    private void setupRecentActivity(View root) {
        rvRecentActivity = root.findViewById(R.id.rvRecentActivity);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentActivity.setAdapter(new ActivityAdapter(loadRecentActivity()));

        root.findViewById(R.id.tvSeeAllActivity).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: ir a pantalla de actividad completa", Toast.LENGTH_SHORT).show());
    }

    private List<ActivityItem> loadRecentActivity() {
        List<ActivityItem> list = new ArrayList<>();
        list.add(new ActivityItem(ActivityItem.Type.APROBADO, "Pedro aprobó Presupuesto #00124", "Hace 1h"));
        list.add(new ActivityItem(ActivityItem.Type.PAGO, "Recibiste $350.000", "Hace 3h"));
        list.add(new ActivityItem(ActivityItem.Type.VISTO, "María visualizó Factura #00087", "Ayer"));
        list.add(new ActivityItem(ActivityItem.Type.ALERTA, "Factura #00082 vence mañana", "Ayer"));
        return list;
    }

    private void setupReceivables(View root) {
        rvReceivables = root.findViewById(R.id.rvReceivables);
        rvReceivables.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReceivables.setAdapter(new ReceivableAdapter(loadReceivables(), receivable ->
                Toast.makeText(getContext(), "Cobro: " + receivable.getCode(), Toast.LENGTH_SHORT).show()));

        root.findViewById(R.id.tvSeeAllReceivables).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openCobros();
            }
        });
    }

    private List<Receivable> loadReceivables() {
        List<Receivable> list = new ArrayList<>();
        list.add(new Receivable("Pedro García", "#00087", "Vence 30 Ago 2026", 450000, Receivable.Status.PENDIENTE));
        list.add(new Receivable("Carlos Ruiz", "#00086", "Vence 25 Ago 2026", 320000, Receivable.Status.VENCIDO));
        list.add(new Receivable("Sofía Torres", "#00082", "Vence 22 Ago 2026", 300000, Receivable.Status.PENDIENTE));
        return list;
    }

    private void setupMonthSelector(View root) {
        root.findViewById(R.id.monthSelector).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: abrir selector de mes/año", Toast.LENGTH_SHORT).show());
    }

    private void setupQuickActions(View root) {
        setupQuickActionsRow(root.findViewById(R.id.quickActionsTop));
        setupQuickActionsRow(root.findViewById(R.id.quickActionsBottom));
    }

    private void setupQuickActionsRow(View row) {
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

    private void setupChart(View root) {
        // Contenedor reservado para gráficos (MPAndroidChart)
    }
}