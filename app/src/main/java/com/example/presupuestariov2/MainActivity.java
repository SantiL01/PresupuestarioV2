package com.example.presupuestariov2;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.presupuestariov2.budgets.PresupuestosFragment;
import com.example.presupuestariov2.clients.ClientesFragment;
import com.example.presupuestariov2.cobros.CobrosFragment;
import com.example.presupuestariov2.model.Budget;
import com.example.presupuestariov2.model.Client;
import com.example.presupuestariov2.model.DataManager;
import com.example.presupuestariov2.model.Receivable;
import com.example.presupuestariov2.more.MasFragment;
import com.example.presupuestariov2.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private static final int TAB_INICIO = 0;
    private static final int TAB_PRESUPUESTOS = 1;
    private static final int TAB_CLIENTES = 2;
    private static final int TAB_COBROS = 3;
    private static final int TAB_MAS = 4;

    private int currentTab = TAB_INICIO;

    private ImageView ivNavInicio, ivNavPresupuestos, ivNavClientes, ivNavCobros, ivNavMas;
    private TextView tvNavInicio, tvNavPresupuestos, tvNavClientes, tvNavCobros, tvNavMas;

    // Referencia opcional para controlar diálogos abiertos
    private AlertDialog activeDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindBottomNavViews();
        setupBottomNav();

        if (savedInstanceState == null) {
            navigateTo(TAB_INICIO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (activeDialog != null && activeDialog.isShowing()) {
            activeDialog.dismiss();
        }
    }

    private void bindBottomNavViews() {
        ivNavInicio = findViewById(R.id.ivNavInicio);
        ivNavPresupuestos = findViewById(R.id.ivNavPresupuestos);
        ivNavClientes = findViewById(R.id.ivNavClientes);
        ivNavCobros = findViewById(R.id.ivNavCobros);
        ivNavMas = findViewById(R.id.ivNavMas);

        tvNavInicio = findViewById(R.id.tvNavInicio);
        tvNavPresupuestos = findViewById(R.id.tvNavPresupuestos);
        tvNavClientes = findViewById(R.id.tvNavClientes);
        tvNavCobros = findViewById(R.id.tvNavCobros);
        tvNavMas = findViewById(R.id.tvNavMas);
    }

    private void setupBottomNav() {
        findViewById(R.id.navInicio).setOnClickListener(v -> navigateTo(TAB_INICIO));
        findViewById(R.id.navPresupuestos).setOnClickListener(v -> navigateTo(TAB_PRESUPUESTOS));
        findViewById(R.id.navClientes).setOnClickListener(v -> navigateTo(TAB_CLIENTES));
        findViewById(R.id.navCobros).setOnClickListener(v -> navigateTo(TAB_COBROS));
        findViewById(R.id.navMas).setOnClickListener(v -> navigateTo(TAB_MAS));

        FloatingActionButton fab = findViewById(R.id.fabAdd);
        fab.setOnClickListener(v -> mostrarMenuCreacion());
    }

    private void mostrarMenuCreacion() {
        String[] opciones = {"Nuevo Presupuesto", "Nuevo Cliente", "Registrar Cobro"};
        activeDialog = new AlertDialog.Builder(this)
                .setTitle("¿Qué querés crear?")
                .setItems(opciones, (dialog, which) -> {
                    switch (which) {
                        case 0: mostrarFormularioPresupuesto(); break;
                        case 1: mostrarFormularioCliente(); break;
                        case 2: mostrarFormularioCobro(); break;
                    }
                })
                .create();
        activeDialog.show();
    }

    private void mostrarFormularioCliente() {
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_cliente, null);
        EditText etNombre = view.findViewById(R.id.etNombreCliente);
        EditText etEmail = view.findViewById(R.id.etEmailCliente);

        activeDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String nombre = etNombre.getText().toString().trim();
                    String email = etEmail.getText().toString().trim();

                    if (!nombre.isEmpty()) {
                        Client nuevo = new Client(nombre, email, 0, 0, 0.0);
                        DataManager.getInstance().addClient(nuevo);
                        Toast.makeText(this, "Cliente agregado con éxito", Toast.LENGTH_SHORT).show();

                        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                        if (currentFragment instanceof ClientesFragment) {
                            ((ClientesFragment) currentFragment).refreshList();
                        } else {
                            openClientes();
                        }
                    } else {
                        Toast.makeText(this, "El nombre es obligatorio", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();
        activeDialog.show();
    }

    private void mostrarFormularioPresupuesto() {
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_presupuesto, null);
        Spinner spinner = view.findViewById(R.id.spinnerClientePresupuesto);
        Button btnAtajo = view.findViewById(R.id.btnAtajoClientePresup);
        EditText etMonto = view.findViewById(R.id.etMontoPresupuesto);

        ArrayAdapter<Client> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                DataManager.getInstance().getClients());
        spinner.setAdapter(spinnerAdapter);

        activeDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Crear", (d, which) -> {
                    Client clienteSeleccionado = (Client) spinner.getSelectedItem();
                    String montoStr = etMonto.getText().toString().trim();

                    if (clienteSeleccionado != null && !montoStr.isEmpty()) {
                        try {
                            double monto = Double.parseDouble(montoStr);
                            String id = "#00" + (int)(Math.random() * 900 + 100);
                            Budget nuevo = new Budget(id, clienteSeleccionado.getName(), "Hoy", monto, Budget.Status.BORRADOR);

                            DataManager.getInstance().addBudget(nuevo);
                            Toast.makeText(this, "Presupuesto creado", Toast.LENGTH_SHORT).show();

                            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                            if (currentFragment instanceof PresupuestosFragment) {
                                ((PresupuestosFragment) currentFragment).refreshList();
                            } else {
                                openPresupuestos();
                            }
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Complete todos los campos o cree un cliente", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();

        btnAtajo.setOnClickListener(v -> {
            activeDialog.dismiss();
            mostrarFormularioCliente();
        });

        activeDialog.show();
    }

    private void mostrarFormularioCobro() {
        View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_cobro, null);
        Spinner spinner = view.findViewById(R.id.spinnerClienteCobro);
        Button btnAtajo = view.findViewById(R.id.btnAtajoClienteCobro);
        EditText etCodigo = view.findViewById(R.id.etCodigoCobro);
        EditText etMonto = view.findViewById(R.id.etMontoCobro);

        ArrayAdapter<Client> spinnerAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item,
                DataManager.getInstance().getClients());
        spinner.setAdapter(spinnerAdapter);

        activeDialog = new AlertDialog.Builder(this)
                .setView(view)
                .setPositiveButton("Registrar", (d, which) -> {
                    Client clienteSeleccionado = (Client) spinner.getSelectedItem();
                    String codigo = etCodigo.getText().toString().trim();
                    String montoStr = etMonto.getText().toString().trim();

                    if (clienteSeleccionado != null && !codigo.isEmpty() && !montoStr.isEmpty()) {
                        try {
                            double monto = Double.parseDouble(montoStr);
                            Receivable nuevo = new Receivable(
                                    clienteSeleccionado.getName(), codigo, "Reciente", monto, Receivable.Status.PENDIENTE);

                            DataManager.getInstance().addReceivable(nuevo);
                            Toast.makeText(this, "Cobro registrado", Toast.LENGTH_SHORT).show();
                            openCobros();
                        } catch (NumberFormatException e) {
                            Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Debe completar todos los datos", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .create();

        btnAtajo.setOnClickListener(v -> {
            activeDialog.dismiss();
            mostrarFormularioCliente();
        });

        activeDialog.show();
    }

    public void openClientes() { navigateTo(TAB_CLIENTES); }
    public void openPresupuestos() { navigateTo(TAB_PRESUPUESTOS); }
    public void openCobros() { navigateTo(TAB_COBROS); }

    private void navigateTo(int tab) {
        if (tab == currentTab && getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) != null) {
            highlightTab(tab);
            return;
        }

        Fragment fragment;
        switch (tab) {
            case TAB_INICIO: fragment = new HomeFragment(); break;
            case TAB_PRESUPUESTOS: fragment = new PresupuestosFragment(); break;
            case TAB_CLIENTES: fragment = new ClientesFragment(); break;
            case TAB_COBROS: fragment = new CobrosFragment(); break;
            case TAB_MAS: fragment = new MasFragment(); break;
            default: fragment = new HomeFragment(); break;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        currentTab = tab;
        highlightTab(tab);
    }

    private void highlightTab(int tab) {
        int blue = ContextCompat.getColor(this, R.color.primary_blue);
        int gray = ContextCompat.getColor(this, R.color.text_secondary);

        setTabColor(ivNavInicio, tvNavInicio, tab == TAB_INICIO ? blue : gray);
        setTabColor(ivNavPresupuestos, tvNavPresupuestos, tab == TAB_PRESUPUESTOS ? blue : gray);
        setTabColor(ivNavClientes, tvNavClientes, tab == TAB_CLIENTES ? blue : gray);
        setTabColor(ivNavCobros, tvNavCobros, tab == TAB_COBROS ? blue : gray);
        setTabColor(ivNavMas, tvNavMas, tab == TAB_MAS ? blue : gray);
    }

    private void setTabColor(ImageView icon, TextView label, int color) {
        icon.setColorFilter(color);
        label.setTextColor(color);
    }
}