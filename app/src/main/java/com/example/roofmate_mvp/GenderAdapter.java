package com.example.roofmate_mvp;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GenderAdapter extends RecyclerView.Adapter<GenderAdapter.GenderViewHolder> {
    private List<String> genderList;
    private OnGenderClickListener onGenderClickListener;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public interface OnGenderClickListener {
        void onGenderClick(String gender);
    }

    public GenderAdapter(List<String> genderList, OnGenderClickListener onGenderClickListener) {
        this.genderList = genderList;
        this.onGenderClickListener = onGenderClickListener;
    }

    @NonNull
    @Override
    public GenderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
        return new GenderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GenderViewHolder holder, int position) {
        String gender = genderList.get(position);
        holder.genderTextView.setText(gender);
        holder.genderTextView.setBackgroundColor(selectedPosition == position ? 0xFFE0E0E0 : 0xFFFFFFFF);
        holder.genderTextView.setOnClickListener(v -> {
            if (selectedPosition != RecyclerView.NO_POSITION) {
                notifyItemChanged(selectedPosition);
            }
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(selectedPosition);
            onGenderClickListener.onGenderClick(gender);
        });
    }

    @Override
    public int getItemCount() {
        return genderList.size();
    }

    static class GenderViewHolder extends RecyclerView.ViewHolder {
        TextView genderTextView;

        public GenderViewHolder(@NonNull View itemView) {
            super(itemView);
            genderTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
