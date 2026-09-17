package com.example.presupuestariov2.clients;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.adapter.ClientAdapter;
import com.example.presupuestariov2.model.Client;
import com.example.presupuestariov2.model.DataManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ClientesFragment extends Fragment {

    private final List<Client> allClients = new ArrayList<>();
    private ClientAdapter adapter;
    private RecyclerView rvClients;

    public ClientesFragment() {}

    @Override
    public void onResume() {
        super.onResume();
        refreshList();
    }

    public void refreshList() {
        allClients.clear();
        allClients.addAll(DataManager.getInstance().getClients());
        if (adapter != null) {
            adapter.updateData(allClients);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_clientes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        allClients.clear();
        allClients.addAll(DataManager.getInstance().getClients());

        rvClients = view.findViewById(R.id.rvClients);
        rvClients.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new ClientAdapter(new ArrayList<>(allClients), client ->
                Toast.makeText(getContext(), "Abrir ficha de " + client.getName(), Toast.LENGTH_SHORT).show());
        rvClients.setAdapter(adapter);

        setupSearch(view);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        rvClients = null;
        adapter = null;
    }

    private void setupSearch(View root) {
        EditText etSearch = root.findViewById(R.id.etSearchClient);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterClients(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void filterClients(String query) {
        String normalized = query.trim().toLowerCase(Locale.getDefault());
        List<Client> filtered = new ArrayList<>();
        for (Client client : allClients) {
            boolean matchesName = client.getName().toLowerCase(Locale.getDefault()).contains(normalized);
            boolean matchesEmail = client.getEmail().toLowerCase(Locale.getDefault()).contains(normalized);
            if (matchesName || matchesEmail) {
                filtered.add(client);
            }
        }
        adapter.updateData(filtered);
    }
}