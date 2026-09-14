package com.example.presupuestariov2.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.presupuestariov2.R;
import com.example.presupuestariov2.model.ActivityItem;

import java.util.List;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ActivityViewHolder> {

    private final List<ActivityItem> items;

    public ActivityAdapter(List<ActivityItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ActivityViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_activity, parent, false);
        return new ActivityViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityViewHolder holder, int position) {
        ActivityItem item = items.get(position);
        holder.tvDescription.setText(item.getDescription());
        holder.tvTimeAgo.setText(item.getTimeAgo());

        int iconRes;
        switch (item.getType()) {
            case APROBADO:
            case PAGO:
                iconRes = R.drawable.ic_check_circle;
                break;
            case VISTO:
                iconRes = R.drawable.ic_eye;
                break;
            case ALERTA:
                iconRes = R.drawable.ic_warning;
                break;
            default:
                iconRes = R.drawable.ic_check_circle;
        }
        holder.ivIcon.setImageResource(iconRes);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ActivityViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvDescription;
        TextView tvTimeAgo;

        ActivityViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.ivIcon);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvTimeAgo = itemView.findViewById(R.id.tvTimeAgo);
        }
    }
}
