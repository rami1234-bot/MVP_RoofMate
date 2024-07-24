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
    private OnGenderClickListener genderClickListener;

    public interface OnGenderClickListener {
        void onGenderClick(String gender);
    }

    public GenderAdapter(List<String> genderList, OnGenderClickListener genderClickListener) {
        this.genderList = genderList;
        this.genderClickListener = genderClickListener;
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
        holder.itemView.setOnClickListener(v -> genderClickListener.onGenderClick(gender));
    }

    @Override
    public int getItemCount() {
        return genderList.size();
    }

    static class GenderViewHolder extends RecyclerView.ViewHolder {
        TextView genderTextView;

        GenderViewHolder(@NonNull View itemView) {
            super(itemView);
            genderTextView = itemView.findViewById(android.R.id.text1);
        }
    }
}
