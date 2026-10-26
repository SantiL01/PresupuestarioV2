package com.example.presupuestariov2.ui.controladores;

import android.os.Bundle;
import android.util.Log;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.presupuestariov2.R;

import com.example.presupuestariov2.data.modelos.Client;
import com.example.presupuestariov2.data.repository.DataManager;
import com.example.presupuestariov2.ui.viewmodels.SharedViewModel;
import com.example.presupuestariov2.utils.AppExecutors;
import com.example.presupuestariov2.utils.PreferencesManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "UI_Main";

    private static final int TAB_INICIO = 0;
    private static final int TAB_PRESUPUESTOS = 1;
    private static final int TAB_CLIENTES = 2;
    private static final int TAB_COBROS = 3;
    private static final int TAB_MAS = 4;

    private int currentTab = TAB_INICIO;

    private ImageView ivNavInicio, ivNavPresupuestos, ivNavClientes, ivNavCobros, ivNavMas;
    private TextView tvNavInicio, tvNavPresupuestos, tvNavClientes, tvNavCobros, tvNavMas;

    private AlertDialog activeDialog;
    private SharedViewModel sharedViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Antes de super.onCreate: los fragments restaurados ya encuentran la base lista
        try {
            PreferencesManager.getInstance().init(getApplicationContext());
            DataManager.getInstance().init(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "init DataManager", e);
        }
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_main);

        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.getNotice().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        });
        sharedViewModel.getErrorMessage().observe(this, msg -> {
            if (msg != null && !msg.isEmpty()) Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
        });
        sharedViewModel.getRefreshClients().observe(this, refresh -> {
            if (refresh != null && refresh) {
                sharedViewModel.consumeRefreshClients();
                refreshCurrent();
            }
        });
        sharedViewModel.getRefreshReceivables().observe(this, refresh -> {
            if (refresh != null && refresh) {
                sharedViewModel.consumeRefreshReceivables();
                openCobros();
            }
        });

        bindBottomNavViews();
        setupBottomNav();

        if (savedInstanceState == null) {
            navigateTo(TAB_INICIO);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (activeDialog != null && activeDialog.isShowing()) {
                activeDialog.dismiss();
            }
        } catch (Exception e) {
            Log.e(TAG, "onDestroy dialog", e);
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
        try {
            String[] opciones = {"Nuevo Presupuesto", "Nuevo Cliente", "Registrar Cobro"};
            activeDialog = new AlertDialog.Builder(this)
                    .setTitle("¿Qué querés crear?")
                    .setItems(opciones, (dialog, which) -> {
                        try {
                            switch (which) {
                                case 0: mostrarFormularioPresupuesto(); break;
                                case 1: mostrarFormularioCliente(); break;
                                case 2: mostrarFormularioCobro(); break;
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "menu crear", e);
                        }
                    })
                    .create();
            activeDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "mostrarMenuCreacion", e);
        }
    }

    private void mostrarFormularioCliente() {
        try {
            View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_cliente, null);
            EditText etNombre = view.findViewById(R.id.etNombreCliente);
            EditText etEmail = view.findViewById(R.id.etEmailCliente);
            EditText etTelefono = view.findViewById(R.id.etTelefonoCliente);

            activeDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("Guardar", (dialog, which) -> {
                        try {
                            String nombre = etNombre.getText().toString().trim();
                            String email = etEmail.getText().toString().trim();
                            String telefono = etTelefono != null ? etTelefono.getText().toString().trim() : "";
                            sharedViewModel.createClient(nombre, email, telefono);
                        } catch (Exception e) {
                            Log.e(TAG, "guardar cliente", e);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();
            activeDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "mostrarFormularioCliente", e);
        }
    }

    public void mostrarFormularioClientePublic() { mostrarFormularioCliente(); }

    private void mostrarFormularioPresupuesto() {
        try {
            NuevoPresupuestoFragment fragment = new NuevoPresupuestoFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        } catch (Exception e) {
            Log.e(TAG, "mostrarFormularioPresupuesto", e);
        }
    }

    private void mostrarFormularioCobro() {
        try {
            View view = getLayoutInflater().inflate(R.layout.dialog_nuevo_cobro, null);
            Spinner spinner = view.findViewById(R.id.spinnerClienteCobro);
            Button btnAtajo = view.findViewById(R.id.btnAtajoClienteCobro);
            EditText etCodigo = view.findViewById(R.id.etCodigoCobro);
            EditText etMonto = view.findViewById(R.id.etMontoCobro);

            ArrayAdapter<Client> spinnerAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_spinner_dropdown_item, new ArrayList<>());
            spinner.setAdapter(spinnerAdapter);

            AppExecutors.getInstance().diskIO().execute(() -> {
                List<Client> data = new ArrayList<>();
                try {
                    data = DataManager.getInstance().getClients();
                } catch (Exception e) {
                    Log.e(TAG, "cargar clientes cobro", e);
                }
                List<Client> finalData = data;
                AppExecutors.getInstance().mainThread().execute(() -> {
                    try {
                        spinnerAdapter.clear();
                        spinnerAdapter.addAll(finalData);
                        spinnerAdapter.notifyDataSetChanged();
                    } catch (Exception e) {
                        Log.e(TAG, "spinner cobro", e);
                    }
                });
            });

            activeDialog = new AlertDialog.Builder(this)
                    .setView(view)
                    .setPositiveButton("Registrar", (d, which) -> {
                        try {
                            Client clienteSeleccionado = (Client) spinner.getSelectedItem();
                            String codigo = etCodigo.getText().toString().trim();
                            String montoStr = etMonto.getText().toString().trim();

                            if (clienteSeleccionado != null && !codigo.isEmpty() && !montoStr.isEmpty()) {
                                try {
                                    double monto = Double.parseDouble(montoStr);
                                    sharedViewModel.createReceivable(
                                            clienteSeleccionado.getName(), codigo, monto);
                                } catch (NumberFormatException e) {
                                    Toast.makeText(this, "Monto inválido", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(this, "Debe completar todos los datos", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            Log.e(TAG, "registrar cobro", e);
                        }
                    })
                    .setNegativeButton("Cancelar", null)
                    .create();

            btnAtajo.setOnClickListener(v -> {
                try {
                    activeDialog.dismiss();
                } catch (Exception e) {
                    Log.e(TAG, "atajo cliente", e);
                }
                mostrarFormularioCliente();
            });

            activeDialog.show();
        } catch (Exception e) {
            Log.e(TAG, "mostrarFormularioCobro", e);
        }
    }

    private void refreshCurrent() {
        try {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
            if (currentFragment instanceof ClientesFragment) {
                ((ClientesFragment) currentFragment).refreshList();
            } else if (currentFragment instanceof NuevoPresupuestoFragment) {
                ((NuevoPresupuestoFragment) currentFragment).recargarClientes();
            } else {
                openClientes();
            }
        } catch (Exception e) {
            Log.e(TAG, "refreshCurrent", e);
        }
    }

    public void openClientes() { navigateTo(TAB_CLIENTES); }
    public void openPresupuestos() { navigateTo(TAB_PRESUPUESTOS); }
    public void openCobros() { navigateTo(TAB_COBROS); }

    private void navigateTo(int tab) {
        try {
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
            Log.d(TAG, "navigateTo tab=" + tab);
        } catch (Exception e) {
            Log.e(TAG, "navigateTo", e);
        }
    }

    private void highlightTab(int tab) {
        try {
            int blue = ContextCompat.getColor(this, R.color.primary_blue);
            int gray = ContextCompat.getColor(this, R.color.text_secondary);

            setTabColor(ivNavInicio, tvNavInicio, tab == TAB_INICIO ? blue : gray);
            setTabColor(ivNavPresupuestos, tvNavPresupuestos, tab == TAB_PRESUPUESTOS ? blue : gray);
            setTabColor(ivNavClientes, tvNavClientes, tab == TAB_CLIENTES ? blue : gray);
            setTabColor(ivNavCobros, tvNavCobros, tab == TAB_COBROS ? blue : gray);
            setTabColor(ivNavMas, tvNavMas, tab == TAB_MAS ? blue : gray);
        } catch (Exception e) {
            Log.e(TAG, "highlightTab", e);
        }
    }

    private void setTabColor(ImageView icon, TextView label, int color) {
        try {
            icon.setColorFilter(color);
            label.setTextColor(color);
        } catch (Exception e) {
            Log.e(TAG, "setTabColor", e);
        }
    }
}
