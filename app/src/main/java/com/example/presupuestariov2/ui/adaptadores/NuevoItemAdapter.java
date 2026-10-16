package com.example.presupuestariov2.ui.adaptadores;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.data.modelos.BudgetItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NuevoItemAdapter extends RecyclerView.Adapter<NuevoItemAdapter.ItemViewHolder> {

    private List<BudgetItem> items = new ArrayList<>();
    private OnItemChangeListener listener;

    public interface OnItemChangeListener {
        void onItemsChanged();
    }

    public NuevoItemAdapter(OnItemChangeListener listener) {
        this.listener = listener;
    }

    public void addItem(BudgetItem item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
        if (listener != null) listener.onItemsChanged();
    }

    public void addItemSilent(BudgetItem item) {
        items.add(item);
    }

    public List<BudgetItem> getItems() {
        return items;
    }

    public double calcularTotal() {
        double total = 0;
        for (BudgetItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_presupuesto_detalle, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        BudgetItem item = items.get(position);
        holder.tvDescItem.setText(item.getDescripcion());

        String detalle = String.format(Locale.getDefault(), "%.2f %s x $%.2f",
                item.getCantidad(), item.getUnidadMedida(), item.getPrecioUnitario());
        holder.tvDetalleItem.setText(detalle);

        holder.tvSubtotalItem.setText(String.format(Locale.getDefault(), "$ %.2f", item.getSubtotal()));

        holder.btnEliminarItem.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            items.remove(pos);
            notifyItemRemoved(pos);
            listener.onItemsChanged();
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvDescItem, tvDetalleItem, tvSubtotalItem;
        ImageView btnEliminarItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDescItem = itemView.findViewById(R.id.tvDescItem);
            tvDetalleItem = itemView.findViewById(R.id.tvDetalleItem);
            tvSubtotalItem = itemView.findViewById(R.id.tvSubtotalItem);
            btnEliminarItem = itemView.findViewById(R.id.btnEliminarItem);
        }
    }
}