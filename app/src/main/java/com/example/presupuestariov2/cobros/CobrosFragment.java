package com.example.presupuestariov2.cobros;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.adapter.ReceivableAdapter;
import com.example.presupuestariov2.model.DataManager;
import com.example.presupuestariov2.model.Receivable;

import java.util.ArrayList;
import java.util.List;

public class CobrosFragment extends Fragment {

    private static final String TAG = "CicloVida_Cobros";
    private RecyclerView rvCobros;
    private ReceivableAdapter adapter;
    private final List<Receivable> listaCobros = new ArrayList<>();

    public CobrosFragment() {
        // Constructor vacío requerido
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: Fragmento adjuntado al Activity");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Fragmento creado, cargando datos desde DataManager");

        // Sincronizamos con el DataManager global para reflejar cobros nuevos si se agregaron
        listaCobros.clear();
        listaCobros.addAll(DataManager.getInstance().getReceivables());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: Inflando la vista XML");
        return inflater.inflate(R.layout.fragment_cobros, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: Vinculando RecyclerView y adaptador");

        // 1. ¡Crucial! Inicializar la vista del RecyclerView desde el layout inflado
        rvCobros = view.findViewById(R.id.rvCobros);

        if (rvCobros != null) {
            rvCobros.setLayoutManager(new LinearLayoutManager(getContext()));

            // 2. Inicializar el adaptador usando la lista sincronizada
            adapter = new ReceivableAdapter(listaCobros, receivable -> {
                String[] opciones = {"Pendiente", "Vencido", "Pagado"};
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setTitle("Actualizar Cobro")
                        .setItems(opciones, (dialog, which) -> {
                            receivable.setStatus(Receivable.Status.values()[which]);
                            adapter.notifyDataSetChanged();
                        })
                        .show();
            });
            rvCobros.setAdapter(adapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: Actualizando datos de cobros");

        // Refrescamos datos al volver a primer plano
        listaCobros.clear();
        listaCobros.addAll(DataManager.getInstance().getReceivables());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: Limpiando referencias de vistas");
        // Evitamos fugas de memoria anulando el RecyclerView
        rvCobros = null;
        adapter = null;
    }
}