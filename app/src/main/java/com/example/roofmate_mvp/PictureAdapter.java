package com.example.roofmate_mvp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class PictureAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> pictureUrls;

    public PictureAdapter(Context context, List<String> pictureUrls) {
        super(context, 0, pictureUrls);
        this.context = context;
        this.pictureUrls = pictureUrls;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_picture, parent, false);
        }

        String url = pictureUrls.get(position);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        Glide.with(context).load(url).into(imageView);

        return convertView;
    }
}
