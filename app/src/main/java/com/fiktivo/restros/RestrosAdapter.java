package com.fiktivo.restros;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RestrosAdapter extends ArrayAdapter<Restro> {

    public RestrosAdapter(Context context, List<Restro> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Restro restro = getItem(position);
        convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_place, parent, false);

        String distance = restro.distance + " Meters";

        TextView placeNameTextView = (TextView) convertView.findViewById(R.id.place_name_textview);
        TextView placeDistanceTextView = (TextView) convertView.findViewById(R.id.place_distance_textview);
        TextView placeOffersTextView = (TextView) convertView.findViewById(R.id.place_offers_textview);

        ImageView placeLogoImageView = (ImageView) convertView.findViewById(R.id.place_logo_imageview);
        LoadImageTask loadImageTask = new LoadImageTask(placeLogoImageView, restro);
        loadImageTask.execute();

        placeNameTextView.setText(restro.name);
        placeDistanceTextView.setText(distance);
        placeOffersTextView.setText(restro.offers + " Offers");

        return convertView;
    }
}
