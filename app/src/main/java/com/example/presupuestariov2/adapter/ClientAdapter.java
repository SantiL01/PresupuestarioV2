package com.example.presupuestariov2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.model.Client;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    public interface OnClientClickListener {
        void onClientClick(Client client);
    }

    // Paleta de fondos/textos de avatar que rota según la posición en la lista
    private static final int[] AVATAR_BG = {
            R.color.client_avatar_bg_1, R.color.client_avatar_bg_2,
            R.color.client_avatar_bg_3, R.color.client_avatar_bg_4,
            R.color.client_avatar_bg_5
    };
    private static final int[] AVATAR_TEXT = {
            R.color.client_avatar_text_1, R.color.client_avatar_text_2,
            R.color.client_avatar_text_3, R.color.client_avatar_text_4,
            R.color.client_avatar_text_5
    };

    private List<Client> items;
    private final OnClientClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("es", "AR"));

    public ClientAdapter(List<Client> items, OnClientClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    /** Reemplaza la lista mostrada (por ejemplo, al filtrar con el buscador) y refresca la vista. */
    public void updateData(List<Client> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Client client = items.get(position);

        holder.tvInitials.setText(client.getInitials());
        holder.tvName.setText(client.getName());
        holder.tvEmail.setText(client.getEmail());
        holder.tvBudgetsCount.setText(String.valueOf(client.getBudgetsCount()));
        holder.tvInvoicesCount.setText(String.valueOf(client.getInvoicesCount()));
        holder.tvTotalBilled.setText(
                String.format("$%s", currencyFormat.format(client.getTotalBilled())));

        int paletteIndex = position % AVATAR_BG.length;
        holder.tvInitials.getBackground().setTint(
                ContextCompat.getColor(holder.itemView.getContext(), AVATAR_BG[paletteIndex]));
        holder.tvInitials.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), AVATAR_TEXT[paletteIndex]));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClientClick(client);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        TextView tvInitials;
        TextView tvName;
        TextView tvEmail;
        TextView tvBudgetsCount;
        TextView tvInvoicesCount;
        TextView tvTotalBilled;

        ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvBudgetsCount = itemView.findViewById(R.id.tvBudgetsCount);
            tvInvoicesCount = itemView.findViewById(R.id.tvInvoicesCount);
            tvTotalBilled = itemView.findViewById(R.id.tvTotalBilled);
        }
    }
}
