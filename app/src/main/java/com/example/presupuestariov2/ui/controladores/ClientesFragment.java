package com.example.presupuestariov2.ui.controladores;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.ui.adaptadores.ClientAdapter;
import com.example.presupuestariov2.ui.controladores.base.BaseFragment;
import com.example.presupuestariov2.ui.viewmodels.ClientesViewModel;
import com.example.presupuestariov2.ui.viewmodels.state.UiStatus;

import java.util.ArrayList;

public class ClientesFragment extends BaseFragment {

    private static final String TAG = "UI_Clientes";

    private ClientesViewModel viewModel;
    private ClientAdapter adapter;
    private RecyclerView rvClients;
    private ProgressBar pbLoading;
    private TextView tvError;
    private TextView tvEmpty;

    public ClientesFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

    @Override
    protected void setupViews(View root) {
        Log.d(TAG, "setupViews");
        try {
            rvClients = root.findViewById(R.id.rvClients);
            pbLoading = root.findViewById(R.id.pbLoading);
            tvError = root.findViewById(R.id.tvError);
            tvEmpty = root.findViewById(R.id.tvEmpty);
            rvClients.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new ClientAdapter(new ArrayList<>(), client -> {
                try {
                    Toast.makeText(getContext(), "Abrir ficha de " + client.getName(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Log.e(TAG, "click cliente", e);
                }
            });
            rvClients.setAdapter(adapter);

            EditText etSearch = root.findViewById(R.id.etSearchClient);
            etSearch.addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (viewModel != null) viewModel.setQuery(s.toString());
                }
                @Override public void afterTextChanged(Editable s) {}
            });
        } catch (Exception e) {
            Log.e(TAG, "setupViews error", e);
        }
    }

    @Override
    protected void observeViewModel() {
        viewModel = new ViewModelProvider(this).get(ClientesViewModel.class);

        viewModel.getClients().observe(getViewLifecycleOwner(), data -> {
            try {
                if (adapter != null && data != null) adapter.updateData(data);
            } catch (Exception e) {
                Log.e(TAG, "update lista", e);
            }
        });

        viewModel.getStatus().observe(getViewLifecycleOwner(), state -> {
            renderStatus(state, pbLoading, rvClients, tvEmpty, "Sin clientes.");
            if (tvError != null && state != UiStatus.ERROR) tvError.setVisibility(View.GONE);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), msg -> {
            if (msg != null && !msg.isEmpty()) {
                showError(msg);
                if (tvError != null) {
                    tvError.setText(msg);
                    tvError.setVisibility(View.VISIBLE);
                }
            }
        });

        viewModel.load();
    }

    public void refreshList() {
        if (viewModel != null) viewModel.load();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (viewModel != null) viewModel.load();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvClients = null;
        adapter = null;
        pbLoading = null;
        tvError = null;
        tvEmpty = null;
    }
}
