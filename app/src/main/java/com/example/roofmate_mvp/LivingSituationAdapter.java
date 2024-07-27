package com.example.roofmate_mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class LivingSituationAdapter extends RecyclerView.Adapter<LivingSituationAdapter.ViewHolder> {

    private List<String> livingSituations;
    private int selectedPosition = -1;

    public LivingSituationAdapter(List<String> livingSituations) {
        this.livingSituations = livingSituations;
    }

    public String getSelectedLivingSituation() {
        if (selectedPosition >= 0) {
            return livingSituations.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_living_situation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String livingSituation = livingSituations.get(position);
        holder.livingSituationTextView.setText(livingSituation);
        holder.itemView.setBackgroundColor(selectedPosition == position ? 0xFFCCCCCC : 0x00000000);
    }

    @Override
    public int getItemCount() {
        return livingSituations.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView livingSituationTextView;

        ViewHolder(View itemView) {
            super(itemView);
            livingSituationTextView = itemView.findViewById(R.id.livingSituationTextView);
            itemView.setOnClickListener(v -> {
                notifyItemChanged(selectedPosition);
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }
    }
}
