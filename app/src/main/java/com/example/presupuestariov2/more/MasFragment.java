package com.example.presupuestariov2.more;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.presupuestariov2.R;

public class MasFragment extends Fragment {

    public MasFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnConfiguracion).setOnClickListener(v ->
                showInfoDialog("Configuración", "Esta sección estará disponible en la próxima versión.")
        );

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

    private void showInfoDialog(String title, String message) {
        if (getContext() == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Entendido", null)
                .show();
    }
}