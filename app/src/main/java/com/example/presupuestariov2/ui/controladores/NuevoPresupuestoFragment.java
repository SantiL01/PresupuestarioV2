package com.example.presupuestariov2.ui.controladores;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.data.modelos.Budget;
import com.example.presupuestariov2.data.modelos.BudgetItem;
import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.adaptadores.NuevoItemAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NuevoPresupuestoFragment extends Fragment implements NuevoItemAdapter.OnItemChangeListener {

    private Spinner spinnerClienteNuevo;
    private EditText etDescripcionServicio;
    private RecyclerView rvItemsPresupuesto;
    private TextView tvSubtotal, tvTotal;
    private Button btnAgregarItem, btnGuardarPresupuesto, btnExportarPdf, btnNuevoCliente;
    private ImageView btnVolver;

    private NuevoItemAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_nuevo_presupuesto, container, false);

        // Inicializar vistas
        spinnerClienteNuevo = view.findViewById(R.id.spinnerClienteNuevo);
        etDescripcionServicio = view.findViewById(R.id.etDescripcionServicio);
        rvItemsPresupuesto = view.findViewById(R.id.rvItemsPresupuesto);
        tvSubtotal = view.findViewById(R.id.tvSubtotal);
        tvTotal = view.findViewById(R.id.tvTotal);
        btnAgregarItem = view.findViewById(R.id.btnAgregarItem);
        btnGuardarPresupuesto = view.findViewById(R.id.btnGuardarPresupuesto);
        btnExportarPdf = view.findViewById(R.id.btnExportarPdf);
        btnNuevoCliente = view.findViewById(R.id.btnNuevoCliente);
        btnVolver = view.findViewById(R.id.btnVolver);

        configurarRecyclerView();
        cargarClientes();
        configurarBotones();

        return view;
    }

    private void configurarRecyclerView() {
        adapter = new NuevoItemAdapter(this);
        rvItemsPresupuesto.setLayoutManager(new LinearLayoutManager(getContext()));
        rvItemsPresupuesto.setAdapter(adapter);
    }

    private void cargarClientes() {
        List<Client> clientes = DataManager.getInstance().getClients();
        List<String> nombresClientes = new ArrayList<>();
        for (Client c : clientes) {
            nombresClientes.add(c.getName()); // Asumiendo que Client tiene getName()
        }

        if (nombresClientes.isEmpty()) {
            nombresClientes.add("Sin clientes - Crear uno nuevo");
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, nombresClientes);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClienteNuevo.setAdapter(spinnerAdapter);
    }

    public void recargarClientes() {
        cargarClientes();
    }

    private void configurarBotones() {
        btnVolver.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnNuevoCliente.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).mostrarFormularioClientePublic();
            }
        });

        btnExportarPdf.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Exportando a PDF...", Toast.LENGTH_SHORT).show();
        });

        btnAgregarItem.setOnClickListener(v -> mostrarDialogoAgregarItem());

        btnGuardarPresupuesto.setOnClickListener(v -> guardarPresupuesto());
    }

    private void mostrarDialogoAgregarItem() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_item, null);
        builder.setView(dialogView);

        EditText etDescItem = dialogView.findViewById(R.id.etDescItem);
        EditText etCantidadItem = dialogView.findViewById(R.id.etCantidadItem);
        Spinner spinnerUnidadItem = dialogView.findViewById(R.id.spinnerUnidadItem);
        EditText etPrecioItem = dialogView.findViewById(R.id.etPrecioItem);
        TextView tvDialogSubtotalLive = dialogView.findViewById(R.id.tvDialogSubtotalLive);

        // Configurar Spinner Unidades
        String[] unidades = {"unid", "hs", "m", "cm", "m2", "ml", "kg", "g", "l", "caja", "global", "servicio"};
        ArrayAdapter<String> unAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, unidades);
        unAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnidadItem.setAdapter(unAdapter);

        // TextWatcher para calcular subtotal en vivo
        TextWatcher tw = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double cant = Double.parseDouble(etCantidadItem.getText().toString());
                    double prec = Double.parseDouble(etPrecioItem.getText().toString());
                    tvDialogSubtotalLive.setText(String.format(Locale.getDefault(), "$ %.2f", cant * prec));
                } catch (NumberFormatException e) {
                    tvDialogSubtotalLive.setText("$ 0.00");
                }
            }
        };
        etCantidadItem.addTextChangedListener(tw);
        etPrecioItem.addTextChangedListener(tw);

        builder.setPositiveButton("Agregar", null); // Override después para evitar que cierre al fallar validación
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
            String desc = etDescItem.getText().toString().trim();
            String cantStr = etCantidadItem.getText().toString().trim();
            String precStr = etPrecioItem.getText().toString().trim();

            if (desc.isEmpty() || cantStr.isEmpty() || precStr.isEmpty()) {
                Toast.makeText(getContext(), "Complete los campos obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            final double cantidad;
            final double precio;
            try {
                cantidad = Double.parseDouble(cantStr);
                precio = Double.parseDouble(precStr);
            } catch (NumberFormatException e) {
                Toast.makeText(getContext(), "Cantidad o precio inválido", Toast.LENGTH_SHORT).show();
                return;
            }
            String unidad = spinnerUnidadItem.getSelectedItem().toString();

            adapter.addItem(new BudgetItem(desc, cantidad, unidad, precio));
            dialog.dismiss();
        });
    }

    private void guardarPresupuesto() {
        if (adapter.getItems().isEmpty()) {
            Toast.makeText(getContext(), "Debe agregar al menos un ítem", Toast.LENGTH_SHORT).show();
            return;
        }

        String cliente = spinnerClienteNuevo.getSelectedItem() != null ? spinnerClienteNuevo.getSelectedItem().toString() : "Desconocido";
        String descripcionGral = etDescripcionServicio.getText().toString().trim();
        double total = adapter.calcularTotal();

        String id = "#00" + (int) (Math.random() * 900 + 100);
        List<BudgetItem> itemsCopia = new ArrayList<>(adapter.getItems());
        Budget nuevoBudget = new Budget(id, cliente, "Hoy", total, Budget.Status.BORRADOR,
                descripcionGral, itemsCopia);
        long budgetDbId = DataManager.getInstance().addBudget(nuevoBudget);
        if (budgetDbId == -1 && DataManager.getInstance().isInitialized()) {
            Toast.makeText(getContext(), "No se pudo guardar (verifique cliente/código)", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(getContext(), "Presupuesto guardado + cobro generado", Toast.LENGTH_SHORT).show();
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).openPresupuestos();
        } else {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    @Override
    public void onItemsChanged() {
        double total = adapter.calcularTotal();
        String totalFormat = String.format(Locale.getDefault(), "$ %.2f", total);
        tvSubtotal.setText(totalFormat); // Subtotal y Total son iguales al no haber IVA/desc.
        tvTotal.setText(totalFormat);
    }
}