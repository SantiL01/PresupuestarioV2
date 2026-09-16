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

/**
 * Pantalla de inicio (Home) del presupuestario.
 * Junta lo que se ve en las dos capturas: saludo + selector de mes + accesos
 * rápidos + actividad reciente + por cobrar + resumen de ingresos/gastos + gráfico.
 *
 * TODO: por ahora los datos son de ejemplo (hardcodeados). Cuando tengas el
 * repositorio/base de datos, reemplazá loadRecentActivity(), loadReceivables()
 * y los setText() de los totales por datos reales (Room, API, etc.).
 */
public class HomeFragment extends Fragment {

    private RecyclerView rvRecentActivity;
    private RecyclerView rvReceivables;

    public HomeFragment() {
        // Constructor vacío requerido
    }

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
        // El bottom nav + FAB ahora los maneja MainActivity (ver layout_bottom_nav.xml
        // en activity_main.xml), porque son compartidos entre todas las pantallas.
    }

    // ---------------------------------------------------------------------
    // Actividad reciente
    // ---------------------------------------------------------------------
    private void setupRecentActivity(View root) {
        rvRecentActivity = root.findViewById(R.id.rvRecentActivity);
        rvRecentActivity.setLayoutManager(new LinearLayoutManager(getContext()));
        rvRecentActivity.setAdapter(new ActivityAdapter(loadRecentActivity()));

        root.findViewById(R.id.tvSeeAllActivity).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: ir a pantalla de actividad completa", Toast.LENGTH_SHORT).show());
    }

    private List<ActivityItem> loadRecentActivity() {
        List<ActivityItem> list = new ArrayList<>();
        list.add(new ActivityItem(ActivityItem.Type.APROBADO,
                "Pedro aprobó Presupuesto #00124", "Hace 1h"));
        list.add(new ActivityItem(ActivityItem.Type.PAGO,
                "Recibiste $350.000", "Hace 3h"));
        list.add(new ActivityItem(ActivityItem.Type.VISTO,
                "María visualizó Factura #00087", "Ayer"));
        list.add(new ActivityItem(ActivityItem.Type.ALERTA,
                "Factura #00082 vence mañana", "Ayer"));
        return list;
    }

    // ---------------------------------------------------------------------
    // Por cobrar
    // ---------------------------------------------------------------------
    private void setupReceivables(View root) {
        rvReceivables = root.findViewById(R.id.rvReceivables);
        rvReceivables.setLayoutManager(new LinearLayoutManager(getContext()));
        rvReceivables.setAdapter(new ReceivableAdapter(loadReceivables(), receivable ->
                Toast.makeText(getContext(),
                        "Abrir " + receivable.getCode() + " de " + receivable.getClientName(),
                        Toast.LENGTH_SHORT).show()));

        root.findViewById(R.id.tvSeeAllReceivables).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: ir a pantalla de cobros completa", Toast.LENGTH_SHORT).show());
    }

    private List<Receivable> loadReceivables() {
        List<Receivable> list = new ArrayList<>();
        list.add(new Receivable("Pedro García", "#00087", "Vence 30 Ago 2026",
                450000, Receivable.Status.PENDIENTE));
        list.add(new Receivable("Carlos Ruiz", "#00086", "Vence 25 Ago 2026",
                320000, Receivable.Status.VENCIDO));
        list.add(new Receivable("Sofía Torres", "#00082", "Vence 22 Ago 2026",
                300000, Receivable.Status.PENDIENTE));
        return list;
    }

    // ---------------------------------------------------------------------
    // Selector de mes
    // ---------------------------------------------------------------------
    private void setupMonthSelector(View root) {
        root.findViewById(R.id.monthSelector).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: abrir selector de mes/año", Toast.LENGTH_SHORT).show());
    }

    // ---------------------------------------------------------------------
    // Accesos rápidos (se usan en las dos secciones: arriba y abajo)
    // ---------------------------------------------------------------------
    private void setupQuickActions(View root) {
        setupQuickActionsRow(root.findViewById(R.id.quickActionsTop));
        setupQuickActionsRow(root.findViewById(R.id.quickActionsBottom));
    }

    private void setupQuickActionsRow(View row) {
        if (row == null) return;
        row.findViewById(R.id.actionPresupuesto).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: ir a Nuevo Presupuesto", Toast.LENGTH_SHORT).show());
        row.findViewById(R.id.actionFactura).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: ir a Nueva Factura", Toast.LENGTH_SHORT).show());
        row.findViewById(R.id.actionGasto).setOnClickListener(v ->
                Toast.makeText(getContext(), "TODO: ir a Nuevo Gasto", Toast.LENGTH_SHORT).show());

        // Este acceso rápido sí lo dejamos ya conectado: lleva a la pantalla Clientes
        // reutilizando la navegación central de MainActivity.
        row.findViewById(R.id.actionCliente).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).openClientes();
            }
        });
    }

    // ---------------------------------------------------------------------
    // Gráfico de barras (Ingresos vs Gastos)
    // ---------------------------------------------------------------------
    private void setupChart(View root) {
        // Este método arma el gráfico de barras con MPAndroidChart.
        // 1) Agregá la dependencia en app/build.gradle:
        //      implementation 'com.github.PhilJay:MPAndroidChart:v3.1.0'
        //    y en settings.gradle el repositorio jitpack.io.
        // 2) Descomentá el código de abajo (se dejó comentado para que el
        //    proyecto compile aunque todavía no hayas agregado la librería).

        /*
        FrameLayout chartContainer = root.findViewById(R.id.chartContainer);
        BarChart barChart = new BarChart(getContext());
        chartContainer.addView(barChart);

        String[] months = {"Mar", "Abr", "May", "Jun", "Jul", "Ago"};
        float[] ingresos = {900000, 1400000, 1050000, 1750000, 1500000, 2450000};

        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < ingresos.length; i++) {
            entries.add(new BarEntry(i, ingresos[i]));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Ingresos");
        dataSet.setColor(getResources().getColor(R.color.primary_blue));

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.getAxisRight().setEnabled(false);
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(months));
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getXAxis().setGranularity(1f);
        barChart.getLegend().setEnabled(false);
        barChart.invalidate();
        */
    }
}
