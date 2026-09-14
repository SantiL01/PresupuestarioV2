package com.example.presupuestariov2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.model.Receivable;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ReceivableAdapter extends RecyclerView.Adapter<ReceivableAdapter.ReceivableViewHolder> {

    public interface OnReceivableClickListener {
        void onReceivableClick(Receivable receivable);
    }

    private final List<Receivable> items;
    private final OnReceivableClickListener listener;
    // $ y separador de miles al estilo argentino
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("es", "AR"));

    public ReceivableAdapter(List<Receivable> items, OnReceivableClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReceivableViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_receivable, parent, false);
        return new ReceivableViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReceivableViewHolder holder, int position) {
        Receivable item = items.get(position);
        holder.tvClientName.setText(item.getClientName());
        holder.tvCodeAndDate.setText(
                String.format("%s · %s", item.getCode(), item.getDueDateText()));
        holder.tvAmount.setText(String.format("$%s", currencyFormat.format(item.getAmount())));

        int colorRes;
        String statusText;
        switch (item.getStatus()) {
            case VENCIDO:
                colorRes = R.color.status_overdue;
                statusText = "Vencido";
                break;
            case PAGADO:
                colorRes = R.color.status_paid;
                statusText = "Pagado";
                break;
            case PENDIENTE:
            default:
                colorRes = R.color.status_pending;
                statusText = "Pendiente";
        }
        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onReceivableClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ReceivableViewHolder extends RecyclerView.ViewHolder {
        TextView tvClientName;
        TextView tvCodeAndDate;
        TextView tvAmount;
        TextView tvStatus;

        ReceivableViewHolder(@NonNull View itemView) {
            super(itemView);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvCodeAndDate = itemView.findViewById(R.id.tvCodeAndDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}
