package com.example.presupuestariov2.ui.controladores;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.ui.controladores.base.BaseFragment;
import com.example.presupuestariov2.ui.viewmodels.MasViewModel;
import com.example.presupuestariov2.utils.PreferencesManager;

public class MasFragment extends BaseFragment {

    private static final String TAG = "UI_Mas";

    private MasViewModel viewModel;
    private ProgressBar pbLoading;
    private View contentAccount;
    private TextView tvUserInitials, tvUserName, tvUserEmail, tvDueDays;

    public MasFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_mas, container, false);
    }

    @Override
    protected void setupViews(View root) {
        Log.d(TAG, "setupViews");
        try {
            pbLoading = root.findViewById(R.id.pbLoading);
            contentAccount = root.findViewById(R.id.contentAccount);
            tvUserInitials = root.findViewById(R.id.tvUserInitials);
            tvUserName = root.findViewById(R.id.tvUserName);
            tvUserEmail = root.findViewById(R.id.tvUserEmail);
            tvDueDays = root.findViewById(R.id.tvDueDays);

            root.findViewById(R.id.cardDueDays).setOnClickListener(v -> showDueDaysDialog());
            root.findViewById(R.id.cardLogout).setOnClickListener(v -> confirmLogout());
        } catch (Exception e) {
            Log.e(TAG, "setupViews error", e);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel = new ViewModelProvider(this).get(MasViewModel.class);

        viewModel.getUser().observe(getViewLifecycleOwner(), user -> {
            if (user == null) return;
            tvUserInitials.setText(user.getInitials());
            tvUserName.setText(user.getName());
            tvUserEmail.setText(user.getEmail());
        });

        viewModel.getDueDays().observe(getViewLifecycleOwner(), days -> {
            if (days != null) tvDueDays.setText(days + " días");
        });

        viewModel.getStatus().observe(getViewLifecycleOwner(), state ->
                renderStatus(state, pbLoading, contentAccount, null, null));

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) showError(msg);
        });

        viewModel.getLoggedOut().observe(getViewLifecycleOwner(), out -> {
            if (out != null && out) openLogin();
        });

        viewModel.load();
    }

    private void showDueDaysDialog() {
        if (getContext() == null) return;
        int[] options = PreferencesManager.DUE_DAYS_OPTIONS;
        String[] labels = new String[options.length];
        Integer value = viewModel.getDueDays().getValue();
        int current = value != null ? value : PreferencesManager.DEFAULT_DUE_DAYS;
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

    private void confirmLogout() {
        if (getContext() == null) return;
        new AlertDialog.Builder(requireContext())
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro que querés salir de tu cuenta?")
                .setPositiveButton("Sí, salir", (dialog, which) -> viewModel.logout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void openLogin() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        pbLoading = null;
        contentAccount = null;
        tvUserInitials = null;
        tvUserName = null;
        tvUserEmail = null;
        tvDueDays = null;
    }
}
