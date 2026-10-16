package com.example.presupuestariov2.ui.controladores;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.data.modelos.BudgetItem;
import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.ui.adaptadores.NuevoItemAdapter;
import com.example.presupuestariov2.ui.viewmodels.NuevoPresupuestoViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NuevoPresupuestoFragment extends Fragment {

    private static final String TAG = "UI_NuevoPresupuesto";

    private NuevoPresupuestoViewModel viewModel;
    private Spinner spinnerClienteNuevo;
    private EditText etDescripcionServicio;
    private RecyclerView rvItemsPresupuesto;
    private TextView tvSubtotal, tvTotal;
    private Button btnAgregarItem, btnGuardarPresupuesto, btnExportarPdf, btnNuevoCliente;
    private ImageView btnVolver;
    private ProgressBar pbSaving;

    private NuevoItemAdapter adapter;
    private final List<String> nombresClientes = new ArrayList<>();
    private final List<Client> clientesCache = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_nuevo_presupuesto, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated");
        try {
            viewModel = new ViewModelProvider(this).get(NuevoPresupuestoViewModel.class);

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

            pbSaving = new ProgressBar(requireContext());
            pbSaving.setVisibility(View.GONE);

            adapter = new NuevoItemAdapter(() -> {});
            rvItemsPresupuesto.setLayoutManager(new LinearLayoutManager(getContext()));
            rvItemsPresupuesto.setAdapter(adapter);

            observeViewModel();
            configurarBotones();
            viewModel.loadClients();
        } catch (Exception e) {
            Log.e(TAG, "onViewCreated error", e);
        }
    }

    private void observeViewModel() {
        viewModel.getClients().observe(getViewLifecycleOwner(), data -> {
            try {
                clientesCache.clear();
                nombresClientes.clear();
                if (data != null) {
                    clientesCache.addAll(data);
                    for (Client c : data) nombresClientes.add(c.getName());
                }
                if (nombresClientes.isEmpty()) {
                    nombresClientes.add("Sin clientes - Crear uno nuevo");
                }
                ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(),
                        android.R.layout.simple_spinner_item, new ArrayList<>(nombresClientes));
                spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerClienteNuevo.setAdapter(spinnerAdapter);
            } catch (Exception e) {
                Log.e(TAG, "clientes observe", e);
            }
        });

        viewModel.getItems().observe(getViewLifecycleOwner(), items -> {
            try {
                if (items == null) return;
                List<BudgetItem> current = new ArrayList<>(adapter.getItems());
                if (current.size() != items.size()) {
                    while (adapter.getItemCount() > 0) {
                        adapter.notifyItemRemoved(0);
                    }
                    for (BudgetItem it : items) adapter.addItemSilent(it);
                    adapter.notifyDataSetChanged();
                    refreshTotals();
                }
            } catch (Exception e) {
                Log.e(TAG, "items observe", e);
            }
        });

        viewModel.getTotalText().observe(getViewLifecycleOwner(), text -> {
            if (text != null) {
                tvSubtotal.setText(text);
                tvTotal.setText(text);
            }
        });

        viewModel.getSaved().observe(getViewLifecycleOwner(), saved -> {
            if (saved != null && saved) {
                try {
                    Toast.makeText(getContext(), "Presupuesto guardado + cobro generado", Toast.LENGTH_SHORT).show();
                    viewModel.consumeSaved();
                    if (getActivity() instanceof MainActivity) {
                        ((MainActivity) getActivity()).openPresupuestos();
                    } else {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                } catch (Exception e) {
                    Log.e(TAG, "saved observe", e);
                }
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                try {
                    Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "error observe", e);
                }
            }
        });
    }

    public void recargarClientes() {
        if (viewModel != null) viewModel.loadClients();
    }

    private void configurarBotones() {
        btnVolver.setOnClickListener(v -> {
            try {
                requireActivity().getSupportFragmentManager().popBackStack();
            } catch (Exception e) {
                Log.e(TAG, "volver", e);
            }
        });

        btnNuevoCliente.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).mostrarFormularioClientePublic();
            }
        });

        btnExportarPdf.setOnClickListener(v -> {
            try {
                Toast.makeText(getContext(), "Exportando a PDF...", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, "pdf", e);
            }
        });

        btnAgregarItem.setOnClickListener(v -> mostrarDialogoAgregarItem());
        btnGuardarPresupuesto.setOnClickListener(v -> guardarPresupuesto());
    }

    private void mostrarDialogoAgregarItem() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            View dialogView = getLayoutInflater().inflate(R.layout.dialog_agregar_item, null);
            builder.setView(dialogView);

            EditText etDescItem = dialogView.findViewById(R.id.etDescItem);
            EditText etCantidadItem = dialogView.findViewById(R.id.etCantidadItem);
            Spinner spinnerUnidadItem = dialogView.findViewById(R.id.spinnerUnidadItem);
            EditText etPrecioItem = dialogView.findViewById(R.id.etPrecioItem);
            TextView tvDialogSubtotalLive = dialogView.findViewById(R.id.tvDialogSubtotalLive);

            String[] unidades = {"unid", "hs", "m", "cm", "m2", "ml", "kg", "g", "l", "caja", "global", "servicio"};
            ArrayAdapter<String> unAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, unidades);
            unAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinnerUnidadItem.setAdapter(unAdapter);

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
                    } catch (Exception e) {
                        Log.e(TAG, "subtotal vivo", e);
                    }
                }
            };
            etCantidadItem.addTextChangedListener(tw);
            etPrecioItem.addTextChangedListener(tw);

            builder.setPositiveButton("Agregar", null);
            builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();
            dialog.show();

            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                try {
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

                    BudgetItem item = new BudgetItem(desc, cantidad, unidad, precio);
                    adapter.addItem(item);
                    viewModel.addItem(desc, cantidad, unidad, precio);
                    refreshTotals();
                    dialog.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, "agregar item", e);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "dialogo item", e);
        }
    }

    private void guardarPresupuesto() {
        try {
            if (adapter.getItems().isEmpty()) {
                Toast.makeText(getContext(), "Debe agregar al menos un ítem", Toast.LENGTH_SHORT).show();
                return;
            }
            String cliente = spinnerClienteNuevo.getSelectedItem() != null
                    ? spinnerClienteNuevo.getSelectedItem().toString() : "";
            String descripcionGral = etDescripcionServicio.getText().toString().trim();

            List<BudgetItem> copia = new ArrayList<>(adapter.getItems());
            double total = 0;
            for (BudgetItem it : copia) total += it.getSubtotal();

            String code = "#00" + (int) (Math.random() * 900 + 100);
            com.example.presupuestariov2.data.modelos.Budget budget =
                    new com.example.presupuestariov2.data.modelos.Budget(code, cliente, "Hoy",
                            total, com.example.presupuestariov2.data.modelos.Budget.Status.BORRADOR,
                            descripcionGral, copia);

            btnGuardarPresupuesto.setEnabled(false);
            com.example.presupuestariov2.utils.AppExecutors.getInstance().diskIO().execute(() -> {
                long result = -1;
                try {
                    result = com.example.presupuestariov2.data.repository.DataManager.getInstance().addBudget(budget);
                } catch (Exception e) {
                    Log.e(TAG, "guardar fondo", e);
                }
                final long finalResult = result;
                com.example.presupuestariov2.utils.AppExecutors.getInstance().mainThread().execute(() -> {
                    try {
                        btnGuardarPresupuesto.setEnabled(true);
                        if (finalResult == -1) {
                            Toast.makeText(getContext(), "No se pudo guardar (verifique cliente/código)", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(getContext(), "Presupuesto guardado + cobro generado", Toast.LENGTH_SHORT).show();
                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).openPresupuestos();
                        } else {
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "guardar ui", e);
                    }
                });
            });
        } catch (Exception e) {
            Log.e(TAG, "guardar", e);
            if (btnGuardarPresupuesto != null) btnGuardarPresupuesto.setEnabled(true);
        }
    }

    private void refreshTotals() {
        try {
            double total = adapter.calcularTotal();
            String text = String.format(Locale.getDefault(), "$ %.2f", total);
            tvSubtotal.setText(text);
            tvTotal.setText(text);
        } catch (Exception e) {
            Log.e(TAG, "totales", e);
        }
    }
}
