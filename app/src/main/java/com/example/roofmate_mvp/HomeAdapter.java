package com.example.roofmate_mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.List;

public class HomeAdapter extends ArrayAdapter<Home> {

    private Context context;
    private List<Home> homeList;

    public HomeAdapter(Context context, List<Home> homeList) {
        super(context, 0, homeList);
        this.context = context;
        this.homeList = homeList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_home, parent, false);
        }

        Home home = homeList.get(position);

        TextView nameTextView = convertView.findViewById(R.id.name);
        TextView rentTextView = convertView.findViewById(R.id.rent);

        nameTextView.setText(home.getName());
        rentTextView.setText(String.format("$%d", home.getRent()));

        return convertView;
    }

    public void updateList(List<Home> newHomeList) {
        homeList.clear();
        homeList.addAll(newHomeList);
        notifyDataSetChanged();
    }
}
