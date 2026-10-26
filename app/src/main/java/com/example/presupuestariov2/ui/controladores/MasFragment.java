package com.example.presupuestariov2.ui.controladores;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.ui.viewmodels.MasViewModel;
import com.example.presupuestariov2.utils.PreferencesManager;

public class MasFragment extends Fragment {

    private MasViewModel viewModel;

    public MasFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MasViewModel.class);

        view.findViewById(R.id.btnConfiguracion).setOnClickListener(v -> showDueDaysDialog());

        view.findViewById(R.id.btnCerrarSesion).setOnClickListener(v -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Cerrar Sesión")
                    .setMessage("¿Estás seguro que querés salir de tu cuenta?")
                    .setPositiveButton("Sí, salir", (dialog, which) ->
                            Toast.makeText(requireContext(), "Sesión cerrada correctamente", Toast.LENGTH_LONG).show()
                    )
                    .setNegativeButton("Cancelar", null)
                    .show();
        });
    }

    private void showDueDaysDialog() {
        if (getContext() == null) return;
        int[] options = PreferencesManager.DUE_DAYS_OPTIONS;
        String[] labels = new String[options.length];
        int current = viewModel.getDueDays().getValue() != null
                ? viewModel.getDueDays().getValue() : PreferencesManager.DEFAULT_DUE_DAYS;
        int checked = -1;
        for (int i = 0; i < options.length; i++) {
            labels[i] = options[i] + " días";
            if (options[i] == current) checked = i;
        }
        new AlertDialog.Builder(requireContext())
                .setTitle("Vencimiento de cobros")
                .setSingleChoiceItems(labels, checked, (dialog, which) -> {
                    viewModel.setDueDays(options[which]);
                    Toast.makeText(requireContext(),
                            "Los nuevos cobros vencen a " + labels[which], Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
