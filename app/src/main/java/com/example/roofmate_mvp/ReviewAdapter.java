package com.example.roofmate_mvp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.StarViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(int stars);
    }

    private final OnItemClickListener listener;
    private int selectedStars = 0;

    public ReviewAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public StarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.star_item, parent, false);
        return new StarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StarViewHolder holder, int position) {
        holder.bind(position + 1);
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    class StarViewHolder extends RecyclerView.ViewHolder {

        private final ImageView starImageView;

        public StarViewHolder(@NonNull View itemView) {
            super(itemView);
            starImageView = itemView.findViewById(R.id.starImageView);
        }

        public void bind(final int stars) {
            starImageView.setImageResource(stars <= selectedStars ? R.drawable.loop : R.drawable.ssst);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectedStars = stars;
                    listener.onItemClick(stars);
                    notifyDataSetChanged();
                }
            });
        }
    }
}
