package com.example.presupuestariov2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.model.Budget;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    public interface OnBudgetClickListener {
        void onBudgetClick(Budget budget);
    }

    private List<Budget> items;
    private final OnBudgetClickListener listener;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("es", "AR"));

    public BudgetAdapter(List<Budget> items, OnBudgetClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    public void updateData(List<Budget> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_budget, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        Budget budget = items.get(position);

        holder.tvBudgetTitle.setText(String.format("Presupuesto %s", budget.getId()));
        holder.tvClientName.setText(budget.getClientName());
        holder.tvDate.setText(budget.getDate());
        holder.tvAmount.setText(String.format("$%s", currencyFormat.format(budget.getTotalAmount())));

        int colorRes;
        String statusText;
        switch (budget.getStatus()) {
            case APROBADO:
                colorRes = R.color.status_paid; // Reutilizamos el verde
                statusText = "Aprobado";
                break;
            case RECHAZADO:
                colorRes = R.color.status_overdue; // Reutilizamos el rojo
                statusText = "Rechazado";
                break;
            case ENVIADO:
                colorRes = R.color.primary_blue; // Azul para enviado
                statusText = "Enviado";
                break;
            case BORRADOR:
            default:
                colorRes = R.color.text_secondary; // Gris para borrador
                statusText = "Borrador";
        }

        holder.tvStatus.setText(statusText);
        holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), colorRes));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onBudgetClick(budget);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvBudgetTitle;
        TextView tvClientName;
        TextView tvDate;
        TextView tvAmount;
        TextView tvStatus;

        BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBudgetTitle = itemView.findViewById(R.id.tvBudgetTitle);
            tvClientName = itemView.findViewById(R.id.tvClientName);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}