package com.example.presupuestariov2;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.presupuestariov2.clients.ClientesFragment;
import com.example.presupuestariov2.home.HomeFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Activity única de la app. Acá vive el bottom nav + FAB (compartidos por
 * todas las pantallas) y la lógica para cambiar de fragment sin recrear la
 * barra de navegación cada vez.
 *
 * Cómo agregar una pantalla nueva (ej. Presupuestos):
 *   1) Creá su Fragment (mirá ClientesFragment.java como ejemplo).
 *   2) Agregá un caso más en navigateTo(), similar al de TAB_CLIENTES.
 *   3) Conectá el click del ítem correspondiente en setupBottomNav().
 */
public class MainActivity extends AppCompatActivity {

    // Identificadores de pestaña, para saber cuál resaltar en el bottom nav
    private static final int TAB_INICIO = 0;
    private static final int TAB_PRESUPUESTOS = 1;
    private static final int TAB_CLIENTES = 2;
    private static final int TAB_COBROS = 3;
    private static final int TAB_MAS = 4;

    private int currentTab = TAB_INICIO;

    // Vistas del bottom nav (íconos y etiquetas), para poder pintarlas
    private ImageView ivNavInicio, ivNavPresupuestos, ivNavClientes, ivNavCobros, ivNavMas;
    private TextView tvNavInicio, tvNavPresupuestos, tvNavClientes, tvNavCobros, tvNavMas;

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
        fab.setOnClickListener(v ->
                Toast.makeText(this, "TODO: abrir menú de creación rápida", Toast.LENGTH_SHORT).show());
    }

    /** Punto de entrada público para que cualquier fragment (ej. HomeFragment) navegue a Clientes. */
    public void openClientes() {
        navigateTo(TAB_CLIENTES);
    }

    private void navigateTo(int tab) {
        if (tab == currentTab && getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) != null) {
            // Ya estamos en esa pestaña, no hacemos nada
            highlightTab(tab);
            return;
        }

        Fragment fragment;
        switch (tab) {
            case TAB_INICIO:
                fragment = new HomeFragment();
                break;
            case TAB_CLIENTES:
                fragment = new ClientesFragment();
                break;
            case TAB_PRESUPUESTOS:
            case TAB_COBROS:
            case TAB_MAS:
            default:
                // TODO: reemplazar por su Fragment real cuando lo armes
                Toast.makeText(this, "Esta sección todavía no está lista", Toast.LENGTH_SHORT).show();
                return;
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();

        currentTab = tab;
        highlightTab(tab);
    }

    /** Pinta de azul el ítem activo del bottom nav y de gris el resto. */
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
