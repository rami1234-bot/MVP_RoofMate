package com.example.roofmate_mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;
import androidx.core.content.ContextCompat;

import java.util.List;

public class HomeAdapter extends ArrayAdapter<Home> {

    private List<Home> homeList;

    public HomeAdapter(Context context, List<Home> homeList) {
        super(context, 0, homeList);
        this.homeList = homeList;
    }

    private static class ViewHolder {
        TextView nameTextView;
        TextView rentTextView;
        TextView availabilityTextView;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_home, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = convertView.findViewById(R.id.name);
            viewHolder.rentTextView = convertView.findViewById(R.id.rent);
            viewHolder.availabilityTextView = convertView.findViewById(R.id.availability);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Home home = getItem(position);
        if (home != null) {
            viewHolder.nameTextView.setText(home.getName());
            viewHolder.rentTextView.setText(String.format("$%d", home.getRent()));

            if (home.isAvailable()) {
                viewHolder.availabilityTextView.setText("Available");
                viewHolder.availabilityTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_green_dark));
            } else {
                viewHolder.availabilityTextView.setText("Unavailable");
                viewHolder.availabilityTextView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.holo_red_dark));
            }
        }

        return convertView;
    }

    public void updateList(List<Home> newHomeList) {
        homeList.clear();
        homeList.addAll(newHomeList);
        notifyDataSetChanged();
    }
}
